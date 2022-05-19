package com.example.byteplus_effects_plugin.effect.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.app.AlertDialog;
import android.widget.EditText;

import com.example.byteplus_effects_plugin.common.utils.ToastUtils;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.resource.BaseResource;
import com.example.byteplus_effects_plugin.resource.RemoteResource;
import com.example.byteplus_effects_plugin.resource.ResourceManager;

/**
 * Created on 2021/12/20 15:32
 */
public class StickerTestActivity extends BaseEffectActivity implements BaseResource.ResourceListener {
    private AlertDialog adInput;
    private ProgressDialog pdDownload;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        adInput.show();
    }

    private void initView() {
        pdDownload = new ProgressDialog(this, R.style.ProgressTheme);
        pdDownload.setTitle(R.string.resource_download_progress);
        pdDownload.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pdDownload.setCancelable(false);

        EditText et = new EditText(this);
        adInput = new AlertDialog.Builder(this)
                .setTitle("输入贴纸名")
                .setView(et)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestSticker(et.getText().toString());
                    }
                })
                .create();
    }

    private void requestSticker(String name) {
        if (!name.endsWith(".zip")) {
            name = name + ".zip";
        }

        RemoteResource resource = new RemoteResource();
        resource.setName(name);
        resource.setUrl("http://sticker-distribution.bytedance.net/material/download?filename=" + name);
        resource.setUseCache(false);
        resource.setNeedCache(false);
        resource.setNeedCheckMd5(false);

        ResourceManager.getInstance(this)
                .asyncGetResource(resource, this);
    }

    @Override
    public void onResourceSuccess(BaseResource resource, BaseResource.ResourceResult result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pdDownload.dismiss();
            }
        });
        mEffectManager.setStickerAbs(result.path);
    }

    @Override
    public void onResourceFail(BaseResource resource, int errorCode, String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.show(msg);
            }
        });
    }

    @Override
    public void onResourceStart(BaseResource resource) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pdDownload.setProgress(0);
                pdDownload.show();
            }
        });
    }

    @Override
    public void onResourceProgressChanged(BaseResource resource, float progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pdDownload.setProgress((int) (progress * 100));
            }
        });
    }

    @Override
    public boolean closeBoardFragment() {
        return false;
    }

    @Override
    public boolean showBoardFragment() {
        return false;
    }
}
