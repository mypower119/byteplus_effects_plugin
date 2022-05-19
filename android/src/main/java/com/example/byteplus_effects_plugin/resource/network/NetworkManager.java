package com.example.byteplus_effects_plugin.resource.network;

import com.example.byteplus_effects_plugin.resource.ErrorCode;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created on 2021/11/15 19:39
 */
public class NetworkManager {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public enum Method {
        GET,
        POST
    }

    public enum ContentType {
        JSON,
        URL_ENCODED
    }

    private final OkHttpClient mClient;
    private final Gson mGson;

    public NetworkManager() {
        mClient = new OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        mGson = new Gson();
    }

    public String postJson(String url, Map<String, String> params) throws IOException {
        Request request = createRequest(url, Method.POST, ContentType.JSON, params);
        return Objects.requireNonNull(mClient.newCall(request).execute().body()).string();
    }

    public Call createDownloadTask(String filePath, String url, Method method, ContentType contentType, Map<String, String> params, DownloadCallback callback) {
        Request request = createRequest(url, method, contentType, params);

        Call call = mClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onDownloadError(call, ErrorCode.DEFAULT, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                InputStream is = response.body().byteStream();

                File file = new File(filePath);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);

                    long total = response.body().contentLength();
                    long count = 0;
                    int consumed;
                    byte[] bytes = new byte[1024 * 8];
                    while ((consumed = is.read(bytes)) > 0) {
                        if (call.isCanceled()) {
                            callback.onDownloadError(call, ErrorCode.CANCELED, "download cancelled");
                            return;
                        }
                        fos.write(bytes, 0, consumed);
                        count += consumed;
                        callback.onDownloadProgressChanged(call, 1.f * count / total);
                    }
                } catch (IOException e) {
                    if (call.isCanceled()) {
                        callback.onDownloadError(call, ErrorCode.CANCELED, "download cancelled");
                    } else {
                        callback.onDownloadError(call, ErrorCode.DEFAULT, "resource download fail");
                    }
                    return;
                } finally {
                    if (is != null) {
                        is.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                    if (call.isCanceled()) {
                        file.delete();
                    }
                }

                callback.onDownloadSuccess(call, file);
            }
        });

        return call;
    }

    private Request createRequest(String url, Method method, ContentType contentType, Map<String, String> params) {
        Request.Builder builder = new Request.Builder();
        if (paramsInUrl(method)) {
            builder.url(urlWithParams(url, params))
                    .method(stringOfMethod(method), null)
                    .addHeader("Accept-Encoding", "identity");
        } else {
            builder.url(url)
                    .method(stringOfMethod(method), requestBodyWithParams(contentType, params));
        }
        return builder.build();
    }

    private String stringOfMethod(Method method) {
        switch (method) {
            case GET: return "GET";
            case POST: return "POST";
        }
        return null;
    }

    private String urlWithParams(String url, Map<String, String> params) {
        if (params == null) {
            return url;
        }
        StringBuilder builder = new StringBuilder(url);
        builder.append('?');
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.append(entry.getKey());
            builder.append('=');
            builder.append(entry.getValue());
            builder.append('&');
        }
        return builder.substring(0, builder.length() - 1);
    }

    private RequestBody requestBodyWithParams(ContentType type, Map<String, String> params) {
        switch (type) {
            case JSON: {
                return RequestBody.create(JSON, mGson.toJson(params));
            }
            case URL_ENCODED:{
                FormBody.Builder builder = new FormBody.Builder();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    builder.add(entry.getKey(), entry.getValue());
                }
                return builder.build();
            }
        }
        return null;
    }

    private boolean paramsInUrl(Method method) {
        return method == Method.GET;
    }

    public interface DownloadCallback {
        void onDownloadError(Call call, int errorCode, String msg);
        void onDownloadProgressChanged(Call call, float progress);
        void onDownloadSuccess(Call call, File file);
    }
}
