package com.example.byteplus_effects_plugin.core.license;
import com.bytedance.labcv.licenselibrary.HttpRequestProvider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class EffectHttpRequestProvider implements HttpRequestProvider {
    @Override
    public ResponseInfo getRequest(RequestInfo requestInfo) {
        return new ResponseInfo();
    }

    @Override
    public ResponseInfo postRequest(RequestInfo requestInfo) {
        ResponseInfo resultInfo = new ResponseInfo();
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(requestInfo.url).openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            for (Map.Entry<String, String> entry : requestInfo.requestHead.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

            OutputStream out = conn.getOutputStream();
            out.write(requestInfo.bodydata.getBytes());
            out.flush();
            resultInfo.status_code = conn.getResponseCode();
            if (resultInfo.status_code == HttpURLConnection.HTTP_OK) {
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    response.append(line);
                in.close();
                resultInfo.bodydata = response.toString();
                resultInfo.bodySize = resultInfo.bodydata.length();
                resultInfo.userdata = requestInfo.userdata;
                resultInfo.isSuc = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultInfo;
    }
}
