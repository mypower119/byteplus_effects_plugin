package com.example.byteplus_effects_plugin.effect.resource;

import android.content.Context;
import android.content.pm.PackageManager;

import com.example.byteplus_effects_plugin.ByteplusEffectsPlugin;
import com.example.byteplus_effects_plugin.common.utils.FileUtils;
import com.example.byteplus_effects_plugin.common.utils.LocaleUtils;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.resource.BaseResource;
import com.example.byteplus_effects_plugin.resource.RemoteResource;
import com.example.byteplus_effects_plugin.resource.ResourceManager;
import com.example.byteplus_effects_plugin.resource.network.NetworkManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2021/11/15 20:13
 */
public class StickerFetch implements BaseResource.ResourceListener {
    public static final String BASE_URL = "https://assets.voleai.com";

    private RemoteResource mResource;
    private FetchListener mListener;
    private com.example.byteplus_effects_plugin.effect.resource.StickerPage mLocalStickerPage;
    private Context mContext;

    public void fetchPageWithType(Context context, String type) throws PackageManager.NameNotFoundException {
        mContext = context;
        String systemVersion = "Android";
        String systemLanguage = LocaleUtils.getLanguage(context);
//        String appVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        String appVersion = ByteplusEffectsPlugin.getVersionName();

        RemoteResource resource = new RemoteResource();
        resource.setName(String.format("%s_%s_%s_%s", systemVersion, systemLanguage, appVersion, type));
        resource.setUrl(BASE_URL + "/material/load_config");
        resource.setNeedCheckMd5(false);
        resource.setNeedUnzip(false);
        resource.setNeedCache(true);
        resource.setUseCache(true);
        resource.setDownloadMethod(NetworkManager.Method.POST);
        resource.setResourceListener(this);
        Map<String, String> params = new HashMap<>();
        params.put("system_version", systemVersion);
        params.put("system_language", systemLanguage);
        params.put("app_version", appVersion);
        params.put("resource_type", type);
        resource.setDownloadParams(params);
        mResource = resource;
        ResourceManager.getInstance(context).asyncGetResource(resource, this);
    }

    @Override
    public void onResourceSuccess(BaseResource resource, BaseResource.ResourceResult result) {
        StickerInfo info = stickerInfoWithFile(result.path);
        boolean available = info != null && info.data != null;
        StickerPage page = available ? info.data : null;

        if (result instanceof RemoteResource.RemoteResourceResult) {
            if (((RemoteResource.RemoteResourceResult) result).isFromCache) {
                if (available) {
                    mListener.onStickerFetchSuccess(page, true);
                    mLocalStickerPage = page;
                }

                ((RemoteResource) resource).setUseCache(false);
                ResourceManager.getInstance(mContext).asyncGetResource(resource, this);
            } else {
                if (!available) {
                    mListener.onStickerFetchError(-1, info == null ? "read json config fail" : info.message);
                    return;
                }
                if (mLocalStickerPage == null || page.getVersion() > mLocalStickerPage.getVersion()) {
                    mListener.onStickerFetchSuccess(page, false);
                }
            }
        }
    }

    @Override
    public void onResourceFail(BaseResource resource, int errorCode, String msg) {
        mListener.onStickerFetchError(errorCode, msg);
    }

    @Override
    public void onResourceStart(BaseResource resource) {
    }

    @Override
    public void onResourceProgressChanged(BaseResource resource, float progress) {
    }

    public void setListener(FetchListener mListener) {
        this.mListener = mListener;
    }

    private StickerInfo stickerInfoWithFile(String filePath) {
        try {
            String fileContent = FileUtils.loadStringFromFile(filePath);

            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .registerTypeAdapter(BaseResource.class, new TypeAdapter<BaseResource>() {
                        @Override
                        public void write(JsonWriter out, BaseResource value) throws IOException {
                            assert false;
                        }

                        @Override
                        public BaseResource read(JsonReader in) throws IOException {
                            RemoteResource resource = new RemoteResource();
                            in.beginObject();
                            String fieldName = null;
                            while (in.hasNext()) {
                                JsonToken token = in.peek();
                                if (token.equals(JsonToken.NAME)) {
                                    fieldName = in.nextName();
                                }

                                if ("name".equals(fieldName)) {
                                    resource.setName(in.nextString());
                                } else if ("url".equals(fieldName)) {
                                    resource.setUrl(in.nextString());
                                } else if ("md5".equals(fieldName)) {
                                    resource.setMd5(in.nextString());
                                }
                            }
                            in.endObject();
                            return resource;
                        }
                    })
                    .setLenient()
                    .create();
            StickerInfo info = gson.fromJson(fileContent, StickerInfo.class);
            StickerPage page = info.data;
            if (page != null) {

                // add close item for all groups
                StickerItem closeItem = new StickerItem();
                closeItem.setIconId(R.drawable.clear);
                closeItem.setTitles(Collections.singletonMap("", mContext.getString(R.string.close)));
                for (StickerGroup group : page.getTabs()) {
                    List<StickerItem> l = new ArrayList(Arrays.asList(group.getItems()));
                    l.add(0, closeItem);
                    group.setItems(l.toArray(new StickerItem[0]));
                }
            }

            return info;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("stickerPageWithFile failed: " + filePath);
        }
        return null;
    }

    private static class StickerInfo {
        @Expose int code;
        @Expose String message;
        @Expose StickerPage data;
    }

    public interface FetchListener {
        void onStickerFetchSuccess(StickerPage page, boolean fromCache);

        void onStickerFetchError(int errorCode, String msg);
    }
}
