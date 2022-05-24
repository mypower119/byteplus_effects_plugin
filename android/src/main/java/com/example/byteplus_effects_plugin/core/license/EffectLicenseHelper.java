package com.example.byteplus_effects_plugin.core.license;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.byteplus_effects_plugin.core.Config;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.bytedance.labcv.licenselibrary.BytedLicenseWrapper;
import com.bytedance.labcv.licenselibrary.HttpRequestProvider;
import com.bytedance.labcv.licenselibrary.LicenseCallback;

import java.io.File;
import java.util.HashMap;

/**
 * Created on 2020/7/30 14:26
 */
public class EffectLicenseHelper implements EffectLicenseProvider {
    public static final String RESOURCE = "resource";
    private static EffectLicenseHelper instance = null;

    private Context mContext;
    private LICENSE_MODE_ENUM _licenseMode = LICENSE_MODE_ENUM.ONLINE_LICENSE;
    private int _errorCode = 0;
    private String _errorMsg = "";
    private BytedLicenseWrapper licenseWrapper = null;
    private HttpRequestProvider httpProvider = null;

    public static EffectLicenseHelper getInstance(Context mContext) {
        // 先判断实例是否存在，若不存在再对类对象进行加锁处理
        if (instance == null) {
            synchronized (EffectLicenseHelper.class) {
                if (instance == null) {
                    instance = new EffectLicenseHelper(mContext);
                }
            }
        }
        return instance;
    }

    private EffectLicenseHelper(Context mContext) {
        this.mContext = mContext;
        HashMap<String, String> parames = new HashMap<>();
        if (this._licenseMode == LICENSE_MODE_ENUM.ONLINE_LICENSE) {
            parames.put("mode", "ONLINE");
            parames.put("url", "https://cv-tob.bytedance.com/v1/api/sdk/tob_license/getlicense");
            parames.put("key", "jiaoyang_test");
            parames.put("secret", "04273924-9a77-11eb-94da-0c42a1b32a30");
            parames.put("licensePath", mContext.getFilesDir().getPath() + "/license.bag");
        }
        else if (this._licenseMode == LICENSE_MODE_ENUM.OFFLINE_LICENSE)
        {
            parames.put("mode", "OFFLINE");
            parames.put("licensePath", new File(new File(getResourcePath(), "LicenseBag.bundle"), Config.LICENSE_NAME).getAbsolutePath());
        }
        this.httpProvider = new EffectHttpRequestProvider();
        this.licenseWrapper = new BytedLicenseWrapper(parames, this.httpProvider);
    }

    @Override
    public String getLicensePath() {

        _errorCode = 0;
        _errorMsg = "";
        //同步请求
        int retCode = licenseWrapper.getLicenseWithParams(new HashMap<String, String>(), false, new LicenseCallback() {
            @Override
            public void execute(String retmsg, int retSize, int errorCode, String errorMsg) {
//                _errorCode = errorCode;
//                _errorMsg = errorMsg;
            }
        });
//        if (retCode != 0) {
//            _errorCode = retCode;
//            _errorMsg = "{zh} jni注册失败，检查是否注入网络请求 {en} Jni registration failed, check whether the network request is injected";
//        }

        if (!checkLicenseResult("getLicensePath"))
            return "";

        return licenseWrapper.getParam("licensePath");
    }

    String getHalLicensePath() {
        try {
            String fileDir = "/sdcard/licenseExternal/";
            File file = new File(fileDir);

            if (file.listFiles().length == 1 && file.listFiles()[0].isFile()) {
                String externalLicense = file.listFiles()[0].toString();
                if (externalLicense.endsWith(".licbag")) {
                    return externalLicense;
                }
            }
        } catch( Exception e) {
            LogUtils.e("use package default license file to authentication");
        }
        return "";
    }

    @Override
    public String updateLicensePath() {
        _errorCode = 0;
        //同步请求
        int retCode = licenseWrapper.updateLicenseWithParams(new HashMap<String, String>(), false, new LicenseCallback() {
            @Override
            public void execute(String retmsg, int retSize, int errorCode, String errorMsg) {
                _errorCode = errorCode;
                _errorMsg = errorMsg;
            }
        });

//        if (retCode != 0) {
//            _errorCode = retCode;
//            _errorMsg = "{zh} jni注册失败，检查是否注入网络请求 {en} Jni registration failed, check whether the network request is injected";
//        }

        if (!checkLicenseResult("updateLicensePath"))
            return "";

        return licenseWrapper.getParam("licensePath");
    }

    @Override
    public LICENSE_MODE_ENUM getLicenseMode() {
        return _licenseMode;
    }

    @Override
    public int getLastErrorCode() {
        return 0;
    }

    public boolean checkLicenseResult(String msg) {
//        if (_errorCode != 0) {
//            String log = msg + " error: " + _errorCode;
//            LogUtils.e(log);
//            String toast = _errorMsg;
//            if (toast == "") {
//                toast = log;
//            }
//            Intent intent = new Intent(Config.CHECK_RESULT_BROADCAST_ACTION);
//            intent.putExtra("msg", toast);
//            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
//            return false;
//        }
        return true;
    }

    private String getResourcePath() {
        return mContext.getExternalFilesDir("assets").getAbsolutePath() + File.separator + RESOURCE;
    }

}
