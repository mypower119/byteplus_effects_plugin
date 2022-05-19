package com.example.byteplus_effects_plugin.app.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/** {zh} 
 * Created  on 2021/6/7 2:04 下午
 */

/** {en}
 * Created on 2021/6/7 2:04 pm
 */

public class StreamUtils {

    public static String readString(Context context, int id) {
        try {
            InputStream stream = context.getResources().openRawResource(id);
            return read(stream);
        }catch (Exception e){
            e.printStackTrace();

        }
        return "";

    }

    public static String read(InputStream stream) {
        return read(stream, "utf-8");
    }

    public static String read(InputStream is, String encode) {
        if (is != null) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, encode));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                return sb.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}