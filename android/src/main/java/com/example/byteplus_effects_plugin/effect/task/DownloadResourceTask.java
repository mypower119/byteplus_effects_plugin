package com.example.byteplus_effects_plugin.effect.task;

import android.content.Context;
import android.os.AsyncTask;

import com.example.byteplus_effects_plugin.common.utils.FileUtils;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.effect.qrscan.DownloadParam;
import com.example.byteplus_effects_plugin.effect.qrscan.EncryptParam;
import com.example.byteplus_effects_plugin.effect.qrscan.EncryptResult;
import com.example.byteplus_effects_plugin.effect.qrscan.QRResourceInfo;
import com.example.byteplus_effects_plugin.effect.utils.NetworkUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;


/**
 * Created on 2021/1/4 14:06
 */
public class DownloadResourceTask
        extends AsyncTask<String, Float, DownloadResourceTask.DownloadResourceResult>
        implements NetworkUtils.DownloadProgressListener {
    public static final String SDK_VERSION = "18.8.8";
    public static final String AUTH_FILE = "https://tosv.byted.org/obj/ies-effect-mall-admin/7f90a488b4a9f8de1cb47b4d40ff3313";

//        public static final String BASE_URL = "http://imuse-boe.bytedance.net";
    public static final String BASE_URL = "https://cv-tob.bytedance.com";
    public static final String ENCRYPT_URL = BASE_URL + "/sticker_mall_tob/v1/encrypt_sdk";
    public static final String DOWNLOAD_URL = BASE_URL + "/sticker_mall_tob/v1/download_effect";

    private final WeakReference<DownloadResourceTaskCallback> mCallback;
    private NetworkUtils mNetwork;
    private final Gson mGson = new Gson();
    private boolean isIgnoreVersionRequire = false;

    public DownloadResourceTask(DownloadResourceTaskCallback callback) {
        mCallback = new WeakReference<>(callback);
    }

    public DownloadResourceTask(DownloadResourceTaskCallback callback, boolean isIgnoreVersionRequire) {
        mCallback = new WeakReference<>(callback);
        this.isIgnoreVersionRequire = isIgnoreVersionRequire;
    }

    @Override
    protected void onPreExecute() {
        mNetwork = new NetworkUtils();
        mNetwork.setDownloadProgressCallback(this);
    }

    @Override
    protected DownloadResourceResult doInBackground(String... strings) {
        DownloadResourceResult result = new DownloadResourceResult();
        if (mCallback.get() == null || !(mCallback.get() instanceof Context)) {
            result.code = -1;
            result.msg = "invalid context";
            return result;
        }

        if (mCallback.get() != null && mCallback.get() instanceof Context) {
            boolean networkAvailable = NetworkUtils.isNetworkConnected((Context) mCallback.get());
            if (!networkAvailable) {
                result.code = -1;
                result.msg = mCallback.get().getString(R.string.network_error);
                return result;
            }
        }

        if (strings.length == 0 || strings[0] == null) {
            result.code = -1;
            result.msg = "qr text not found";
            return result;
        }

        LogUtils.i("sticker scan result: " + strings[0]);
        QRResourceInfo resourceInfo = mGson.fromJson(strings[0], QRResourceInfo.class);
        if (resourceInfo == null || resourceInfo.secId == null) {
            result.code = -1;
            result.msg = "secId not found";
            return result;
        }

        ResourceType resourceType = resourceTypeOfInfo(resourceInfo);
        if (resourceType == ResourceType.UNKNOWN) {
            result.code = -1;
            result.msg = "resource type not available";
            return result;
        }
        result.resourceType = resourceType;

        if (isGreaterThanAppVersion(resourceInfo.sdkVersion) && !isIgnoreVersionRequire) {
            result.code = DownloadResourceTaskCallback.ERROR_CODE_VERSION_NOT_MATCH;
            result.msg = "version not match";
            return result;
        }

        LogUtils.i("sticker secId: " + resourceInfo.secId);
        EncryptParam param = new EncryptParam.Builder()
                .setSecId(resourceInfo.secId)
                .build();

        EncryptResult encryptResult = null;
        try {
            encryptResult = mGson.fromJson(mNetwork.postWithJson(ENCRYPT_URL, mGson.toJson(param)), EncryptResult.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (encryptResult == null || encryptResult.base_response == null) {
            result.code = -1;
            result.msg = "error when get encrypted url";
            return result;
        }

        if (encryptResult.base_response.code != 0) {
            result.code = encryptResult.base_response.code;
            result.msg = encryptResult.base_response.message;
            return result;
        }

        if (encryptResult.data == null || encryptResult.data.encryptUrl == null) {
            result.code = -1;
            result.msg = "invalid data or encryptUrl";
            return result;
        }

        LogUtils.i("encryptUrl: " + encryptResult.data.encryptUrl);
        String url = encryptResult.data.encryptUrl;
        String filePath = generateFilePath(url);
        DownloadParam downloadParam = new DownloadParam.Builder()
                .setEncryptUrl(url)
                .build();
        try {
            String errorMsg = mNetwork.downloadFileWithJson(DOWNLOAD_URL, mGson.toJson(downloadParam), filePath);
//            String errorMsg = mNetwork.downloadFile(url, filePath);
            if (errorMsg != null) {
                result.code = -1;
                result.msg = errorMsg;
                return result;
            }
        } catch (IOException e) {
            result.code = -1;
            result.msg = e.getMessage();
            return result;
        }

        String dstDir = generateStickerDir(filePath);
        LogUtils.i("save sticker dir: " + dstDir);
        boolean unzipResult = FileUtils.unzipFile(filePath, new File(dstDir));
        if (!unzipResult) {
            result.code = -1;
            result.msg = "unzip sticker error";
            return result;
        }

        result.msg = dstDir;
        return result;
    }

    @Override
    protected void onPostExecute(DownloadResourceResult s) {
        DownloadResourceTaskCallback callback = mCallback.get();
        if (callback == null) return;
        if (s == null) {
            callback.onFail(-1, "fail");
            return;
        }
        if (s.code != 0) {
            callback.onFail(s.code, s.msg);
            return;
        }
        callback.onSuccess(s.msg, s.resourceType);
    }

    @Override
    protected void onProgressUpdate(Float... values) {
        DownloadResourceTaskCallback callback = mCallback.get();
        if (callback == null) return;
        callback.onProgressUpdate(values[0]);
    }

    @Override
    public void onProgressUpdate(float progress) {
        publishProgress(progress);
    }

    private String generateFilePath(String url) {
        String[] splits = url.split("/");
        String fileName = splits[splits.length - 1];
        if (mCallback.get() != null) {
            return mCallback.get().getExternalCacheDir() + File.separator + fileName;
        }
        return FileUtils.generateCacheFile(fileName);
    }

    private String generateStickerDir(String filePath) {
        return filePath.substring(0, filePath.length() - 4);
    }

    private ResourceType resourceTypeOfInfo(QRResourceInfo info) {
        if (info.goodsType == null || info.goodsSubType == null) {
            return ResourceType.UNKNOWN;
        }

        if ("cv".equals(info.goodsType.key) && "filter".equals(info.goodsSubType.key)) {
            return ResourceType.FILTER;
        } else if ("cv".equals(info.goodsType.key) && "effect".equals(info.goodsSubType.key)) {
            return ResourceType.STICKER;
        }

        return ResourceType.UNKNOWN;
    }

    private boolean isGreaterThanAppVersion(String sdkVersion) {
        if (mCallback.get() == null) {
            return false;
        }
        String appVersion = mCallback.get().getAppVersionName();
        if (appVersion == null) {
            return false;
        }

        return compareString(appVersion, sdkVersion) < 0;
    }

    private int compareString(String a, String b) {
        String[] versionA = a.split("_");
        if (versionA == null) {
            return -1;
        }
        String[] arrA = versionA[0].split("\\.");
        String[] arrB = b.split("\\.");

        int maxLength = Math.min(arrA.length, arrB.length);
        for (int i = 0; i < maxLength; i++) {
            int iA = Integer.parseInt(arrA[i]);
            int iB = Integer.parseInt(arrB[i]);

            if (iA > iB) {
                return 1;
            }
            if (iA < iB) {
                return -1;
            }
        }

        if (arrA.length == arrB.length) return 0;
        return arrA.length > arrB.length ? 1 : -1;
    }

    public interface DownloadResourceTaskCallback {
        int ERROR_CODE_DEFAULT = -1;
        int ERROR_CODE_VERSION_NOT_MATCH = -2;

        void onSuccess(String path, ResourceType type);
        void onFail(int errorCode, String message);
        void onProgressUpdate(float progress);
        String getString(int id);
        File getExternalCacheDir();
        String getAppVersionName();
    }

    public enum ResourceType {
        UNKNOWN,
        STICKER,
        FILTER
    }

    public static class DownloadResourceResult {
        int code = 0;
        String msg;
        ResourceType resourceType;
    }
}
