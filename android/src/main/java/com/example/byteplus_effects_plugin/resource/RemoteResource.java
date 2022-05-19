package com.example.byteplus_effects_plugin.resource;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.example.byteplus_effects_plugin.resource.database.DatabaseManager;
import com.example.byteplus_effects_plugin.resource.file.FileUtils;
import com.example.byteplus_effects_plugin.resource.network.NetworkManager;
import com.google.gson.annotations.Expose;

import java.io.File;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;


/**
 * Created on 2021/10/12 10:32
 */
public class RemoteResource extends BaseResource implements NetworkManager.DownloadCallback {

    public enum DownloadDirectory {
        CACHE,
        DOCUMENT,
    }

    @Expose
    private String url;
    @Expose
    private String md5;

    private RemoteResourceState state = RemoteResourceState.UNKNOWN;

    private float downloadProgress = 0.f;
    private boolean isCanceled;
    private int retryCount = 0;

    private NetworkManager.Method downloadMethod = NetworkManager.Method.GET;
    private NetworkManager.ContentType contentType = NetworkManager.ContentType.URL_ENCODED;
    private DownloadDirectory downloadDirectory = DownloadDirectory.DOCUMENT;
    private Map<String, String> downloadParams;
    private boolean needUnzip = true;
    private boolean needCache = true;
    private boolean useCache = true;
    private boolean needCheckMd5 = true;

    private DownloadContext context;
    private Call call;

    public RemoteResource() {
    }

    @Override
    ResourceResult syncGetResource() {
        return null;
    }

    @Override
    void asyncGetResource() {
        isCanceled = false;
        retryCount = 0;

        DatabaseManager.DownloadResourceItem item = useCache ? cachedItem() : null;

        if (item == null) {
            fetchRemoteResource();
        } else {
            boolean ret = fetchLocalResource(item);
            if (!ret) {
                DatabaseManager.getInstance().deleteResourceItem(item.name);
                fetchRemoteResource();
            }
        }
    }

    protected boolean fetchLocalResource(DatabaseManager.DownloadResourceItem resourceItem) {
        String filePath = getFileDir() + resourceItem.path;

        if (!new File(filePath).exists()) {
            return false;
        }

        if (needCheckMd5) {
            if (!resourceItem.md5.equals(md5)) {
                return false;
            }
        }

        if (needUnzip) {
            filePath = FileUtils.removeFileNameExtension(filePath);
        }

        state = RemoteResourceState.CACHED;
        resourceListener.onResourceSuccess(this, RemoteResourceResult.instance(filePath, true));
        return true;
    }

    protected void fetchRemoteResource() {
        if (!isNetworkConnected(context.context)) {
            state = RemoteResourceState.UNKNOWN;
            resourceListener.onResourceFail(this, ErrorCode.NETWORK_NOT_AVAILABLE, "network unavailable");
            return;
        }

        String filePath = getFilePath();
        if (!prepareFilePath(filePath)) {
            state = RemoteResourceState.UNKNOWN;
            resourceListener.onResourceFail(this, ErrorCode.FILE_NOT_AVAILABLE, "file not prepared");
            return;
        }

        call = context.networkManager.createDownloadTask(filePath, url, downloadMethod,
                contentType, downloadParams, this);
        onDownloadStart();
    }

    public void onDownloadStart() {
        state = RemoteResourceState.DOWNLOADING;
        downloadProgress = 0.0f;
        resourceListener.onResourceStart(this);
    }

    @Override
    public void onDownloadError(Call call, int errorCode, String msg) {
        state = RemoteResourceState.UNKNOWN;
        this.call = null;
        resourceListener.onResourceFail(this, errorCode, msg);
    }

    @Override
    public void onDownloadProgressChanged(Call call, float progress) {
        state = RemoteResourceState.DOWNLOADING;
        downloadProgress = progress;
        resourceListener.onResourceProgressChanged(this, progress);
    }

    @Override
    public void onDownloadSuccess(Call call, File file) {
        this.call = null;
        if (file == null) {
            state = RemoteResourceState.REMOTE;
            resourceListener.onResourceFail(this, ErrorCode.DEFAULT, "resource download fail");
            return;
        }

        boolean ret;
        if (needCheckMd5) {
            ret = validateMd5(file.getAbsolutePath(), md5);
            if (!ret) {
                state = RemoteResourceState.REMOTE;
                if (!checkIfRetry()) {
                    resourceListener.onResourceFail(this, ErrorCode.MD5_CHECK_ERROR, "file md5 check fail");
                }
                return;
            }
        }

        String path = file.getAbsolutePath();
        if (needUnzip) {
            // 去除文件扩展名
            String unzipFilePath = FileUtils.removeFileNameExtension(path);
            ret = unzipResource(path, unzipFilePath);
            if (!ret) {
                // error callback
                state = RemoteResourceState.REMOTE;
                resourceListener.onResourceFail(this, ErrorCode.UNZIP_ERROR, "file unzip fail");
                return;
            }
            path = unzipFilePath;
        }

        if (needCache) {
            DatabaseManager.getInstance().addResourceItem(name, md5, getFileName());
        }

        state = RemoteResourceState.CACHED;
        resourceListener.onResourceSuccess(this, RemoteResourceResult.instance(path, false));
    }

    public void cancel() {
        if (call == null || call.isCanceled()) {
            return;
        }
        call.cancel();
        isCanceled = true;
    }

    protected DatabaseManager.DownloadResourceItem cachedItem() {
        return DatabaseManager.getInstance().resourceItem(name);
    }

    private boolean validateMd5(String filePath, String md5) {
        return FileUtils.validateFileMD5(md5, filePath);
    }

    private String getFilePath() {
        return getFileDir() + getFileName();
    }

    private String getFileName() {
        return File.separator + "resource" + File.separator + name + (needUnzip ? ".zip" : "");
    }

    private String getFileDir() {
        switch (downloadDirectory) {
            case CACHE:
                return Objects.requireNonNull(context.context.getExternalCacheDir()).getAbsolutePath();
            case DOCUMENT:
                return Objects.requireNonNull(context.context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)).getAbsolutePath();
        }
        return null;
    }

    private boolean prepareFilePath(String filePath) {
        return FileUtils.prepareFilePath(filePath);
    }

    private boolean unzipResource(String filePath, String destination) {
        return FileUtils.unzipFile(filePath, new File(destination));
    }

    private boolean checkIfRetry() {
        if (retryCount++ < 3) {
            fetchRemoteResource();
            return true;
        }
        return false;
    }

    private boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public RemoteResourceState getState() {
        if (state == RemoteResourceState.UNKNOWN) {
            state = cachedItem() == null ? RemoteResourceState.REMOTE : RemoteResourceState.CACHED;
        }
        return state;
    }

    public float getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(float downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public NetworkManager.Method getDownloadMethod() {
        return downloadMethod;
    }

    public void setDownloadMethod(NetworkManager.Method downloadMethod) {
        this.downloadMethod = downloadMethod;
    }

    public DownloadDirectory getDownloadDirectory() {
        return downloadDirectory;
    }

    public void setDownloadDirectory(DownloadDirectory downloadDirectory) {
        this.downloadDirectory = downloadDirectory;
    }

    public Map<String, String> getDownloadParams() {
        return downloadParams;
    }

    public void setDownloadParams(Map<String, String> downloadParams) {
        this.downloadParams = downloadParams;
    }

    public boolean isNeedUnzip() {
        return needUnzip;
    }

    public void setNeedUnzip(boolean needUnzip) {
        this.needUnzip = needUnzip;
    }

    public boolean isNeedCache() {
        return needCache;
    }

    public void setNeedCache(boolean needCache) {
        this.needCache = needCache;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public boolean isNeedCheckMd5() {
        return needCheckMd5;
    }

    public void setNeedCheckMd5(boolean needCheckMd5) {
        this.needCheckMd5 = needCheckMd5;
    }

    public void setContext(DownloadContext context) {
        this.context = context;
    }

    public NetworkManager.ContentType getContentType() {
        return contentType;
    }

    public void setContentType(NetworkManager.ContentType contentType) {
        this.contentType = contentType;
    }

    public static class DownloadContext {
        public Context context;
        public NetworkManager networkManager;

        public DownloadContext(Context context, NetworkManager networkManager) {
            this.context = context;
            this.networkManager = networkManager;
        }
    }

    public static class RemoteResourceResult extends ResourceResult {
        public boolean isFromCache;

        public static RemoteResourceResult instance(String path, boolean isFromCache) {
            RemoteResourceResult result = new RemoteResourceResult();
            result.path = path;
            result.isFromCache = isFromCache;
            return result;
        }
    }

    public enum RemoteResourceState {
        UNKNOWN,
        REMOTE,
        DOWNLOADING,
        CACHED
    }
}
