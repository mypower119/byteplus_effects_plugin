package com.example.byteplus_effects_plugin.app.activity;

import static com.example.byteplus_effects_plugin.common.config.ImageSourceConfig.ImageSourceType.TYPE_CAMERA;
import static com.example.byteplus_effects_plugin.app.activity.PermissionsActivity.PERMISSION_AUDIO;
import static com.example.byteplus_effects_plugin.app.activity.PermissionsActivity.PERMISSION_CAMERA;
import static com.example.byteplus_effects_plugin.app.activity.PermissionsActivity.PERMISSION_STORAGE;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.byteplus_effects_plugin.ByteplusEffectsPlugin;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.byteplus_effects_plugin.algorithm.config.AlgorithmConfig;
import com.example.byteplus_effects_plugin.common.config.ImageSourceConfig;
import com.example.byteplus_effects_plugin.common.config.UIConfig;
import com.example.byteplus_effects_plugin.common.customfeatureswitch.CustomFeatureSwitch;
import com.example.byteplus_effects_plugin.common.utils.CommonUtils;
import com.example.byteplus_effects_plugin.common.utils.DensityUtils;
import com.example.byteplus_effects_plugin.common.utils.ToastUtils;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.app.adapter.MainTabRVAdapter;
import com.example.byteplus_effects_plugin.app.adapter.decoration.GridDividerItemDecoration;
import com.example.byteplus_effects_plugin.app.boradcast.LocalBroadcastReceiver;
import com.example.byteplus_effects_plugin.app.model.FeatureConfig;
import com.example.byteplus_effects_plugin.app.model.FeatureTab;
import com.example.byteplus_effects_plugin.app.model.FeatureTabItem;
import com.example.byteplus_effects_plugin.app.model.MainDataManager;
import com.example.byteplus_effects_plugin.app.model.UserData;
import com.example.byteplus_effects_plugin.app.task.RequestLicenseTask;
import com.example.byteplus_effects_plugin.app.task.UnzipTask;
import com.example.byteplus_effects_plugin.effect.activity.QRScanActivity;
import com.example.byteplus_effects_plugin.effect.config.EffectConfig;
import com.example.byteplus_effects_plugin.effect.config.StickerConfig;
import com.example.byteplus_effects_plugin.lens.config.ImageQualityConfig;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 5/10/21 12:01 PM
 */
public class MainActivity extends FragmentActivity
        implements MainTabRVAdapter.OnItemClickListener, UnzipTask.IUnzipViewCallback, View.OnClickListener {
    private CoordinatorLayout cl;
    private TabLayout tl;
    private RecyclerView rv;
    private FrameLayout flMask;
    private ImageView ivQRScan;

    private GridLayoutManager mLayoutManager;
    private boolean mSkipRVScrollListen;

    private MainDataManager mDataManager;
    private List<FeatureTab> mTabs = new ArrayList<>();
    private Point mDefaultPreviewSize = new Point(1280,720);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mDataManager = new MainDataManager();

        cl = findViewById(R.id.cl_main);
        tl = findViewById(R.id.tl_main);
        rv = findViewById(R.id.rv_main);
        flMask = findViewById(R.id.fl_mask);

        initView();

        checkResourceReady();


        LocalBroadcastManager.getInstance(this).registerReceiver(new LocalBroadcastReceiver(), new IntentFilter(LocalBroadcastReceiver.ACTION));

//        startActivity(new Intent(this, SportsHomeActivity.class));
    }

    private void initView() {
        Glide.with(this).load(R.drawable.banner)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners((int) DensityUtils.dp2px(this,4))))
                .into((ImageView) findViewById(R.id.img_banner));

        mTabs = mDataManager.getFeatureTabs(this);
        initRV(mTabs);
        initTab(mTabs);
        initMask();

        ivQRScan = findViewById(R.id.iv_qr_scan);
        ivQRScan.setOnClickListener(this);
    }

    private void initTab(List<FeatureTab> tabs) {
        for (FeatureTab tab : tabs) {
            tl.addTab(tl.newTab().setText(tab.getTitleId()));
        }

        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = getChildPosition(tab.getPosition());
                RecyclerView.SmoothScroller scroller = new LinearSmoothScroller(getContext()) {
                    @Override
                    protected int getVerticalSnapPreference() {
                        return LinearSmoothScroller.SNAP_TO_START;
                    }
                };
                scroller.setTargetPosition(position);
                mLayoutManager.startSmoothScroll(scroller);
                mSkipRVScrollListen = true;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initRV(List<FeatureTab> tabs) {
        MainTabRVAdapter adapter = new MainTabRVAdapter(tabs);
        adapter.setOnItemClickListener(this);
        mLayoutManager = new GridLayoutManager(getContext(), 2);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = adapter.getItemViewType(position);
                if (viewType == MainTabRVAdapter.TYPE_TITLE || viewType == MainTabRVAdapter.TYPE_FOOTER) {
                    return 2;
                }
                return 1;
            }
        });
        rv.setLayoutManager(mLayoutManager);
        rv.setAdapter(adapter);
        rv.addItemDecoration(new GridDividerItemDecoration(this));
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mSkipRVScrollListen) {
                    //   {zh} 如果是 tabLayout 导致的滑动，就跳过这一步，       {en} If the slide is caused by tabLayout, skip this step,  
                    //   {zh} 否则会引起循环调用       {en} Otherwise it will cause a loop call  
                    return;
                }
                tl.setScrollPosition(getParentPosition(mLayoutManager.findFirstVisibleItemPosition()), 0, true);
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    //   {zh} 滑动状态变为 SCROLL_STATE_DRAGGING 时意味着手动滑动       {en} When the sliding state changes to SCROLL_STATE_DRAGGING, it means manual sliding  
                    //   {zh} 此时关闭跳过       {en} Close Skip at this time  
                    mSkipRVScrollListen = false;
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initMask() {
        flMask.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (CommonUtils.isFastClick()){
            LogUtils.e("too fast click");
            return;
        }
        if (v.getId() == R.id.iv_qr_scan) {
            ImageSourceConfig imageSourceConfig = new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_BACK));
            imageSourceConfig.setType(TYPE_CAMERA);
            imageSourceConfig.setRecordable(false);
            imageSourceConfig.setRequestWidth(mDefaultPreviewSize.x);
            imageSourceConfig.setRequestHeight(mDefaultPreviewSize.y);

            FeatureConfig config = new FeatureConfig();
            config.setImageSourceConfig(imageSourceConfig);
            config.setActivityClassName(QRScanActivity.class.getName());

            startActivity(config);
        }
    }

    @Override
    public void onItemClick(FeatureTabItem item) {
        FeatureConfig config = item.getConfig();

        if(item.getTitleId() == R.string.feature_ar_watch)
        {
            CustomFeatureSwitch.getInstance().setFeature_watch(true);
        }
        else if(item.getTitleId() == R.string.feature_ar_bracelet)
        {
            CustomFeatureSwitch.getInstance().setFeature_bracelet(true);
        }

        startActivity(config);
    }

    private void startActivity(FeatureConfig config) {
        ImageSourceConfig imageSourceConfig;
        if (config.getImageSourceConfig() != null) {
            imageSourceConfig = config.getImageSourceConfig();
        } else {
            /** {zh} 
             * 默认设置视频源为：前置相机，支持视频录制，默认分辨为mDefaultPreviewSize
             */
            /** {en} 
             * Default setting video source is: front camera, support video recording, default resolution is mDefaultPreviewSize
             */

            imageSourceConfig = new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_FRONT));

        }

        imageSourceConfig.setRecordable(false);
        imageSourceConfig.setRequestWidth(mDefaultPreviewSize.x);
        imageSourceConfig.setRequestHeight(mDefaultPreviewSize.y);

        Class<?> clz;
        try {
            if (config.getActivityClassName() == null) {
                throw new ClassNotFoundException();
            }
            clz = Class.forName(config.getActivityClassName());
        } catch (ClassNotFoundException e) {
            ToastUtils.show("class " + config.getActivityClassName() + " not found," +
                    " ensure your config for this item");
            return;
        }

        Intent intent = new Intent(this, clz);
        // in sake of speed
        if (config.getAlgorithmConfig() != null){
            intent.putExtra(AlgorithmConfig.ALGORITHM_CONFIG_KEY, new Gson().toJson(config.getAlgorithmConfig()));

        }
        intent.putExtra(ImageSourceConfig.IMAGE_SOURCE_CONFIG_KEY, new Gson().toJson(imageSourceConfig));
        if (config.getImageQualityConfig() != null){
            intent.putExtra(ImageQualityConfig.IMAGE_QUALITY_KEY, new Gson().toJson(config.getImageQualityConfig()));

        }
        if (config.getStickerConfig() != null){
            intent.putExtra(StickerConfig.StickerConfigKey, new Gson().toJson(config.getStickerConfig()));

        }
        if (config.getEffectConfig() != null){
            intent.putExtra(EffectConfig.EffectConfigKey, new Gson().toJson(config.getEffectConfig()));
        }

        if (config.getUiConfig() != null){
            intent.putExtra(UIConfig.KEY, new Gson().toJson(config.getUiConfig()));
        }
        try {
            checkPermissionAndStart(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void checkPermissionAndStart(Intent intent) throws ClassNotFoundException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(PERMISSION_CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(PERMISSION_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(PERMISSION_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                // start Permissions activity
                ComponentName componentName = intent.getComponent();
                intent.setClass(this, PermissionsActivity.class);
                intent.putExtra(PermissionsActivity.PERMISSION_SUC_ACTIVITY, Class.forName(componentName.getClassName()));
                startActivity(intent);
                return;
            }
        }
        startActivity(intent);
    }

    public int getChildPosition(int parentPosition) {
        int childPosition = 0;
        for (int i = 0; i < parentPosition; i++) {
            childPosition += mTabs.get(i).getChildren().size() + 1;
        }
        return childPosition;
    }

    public int getParentPosition(int childPosition) {
        for (int i = 0; i < mTabs.size(); i++) {
            if (childPosition < mTabs.get(i).getChildren().size() + 1) {
                return i;
            }
            childPosition -= mTabs.get(i).getChildren().size() + 1;
        }
        return 0;
    }

    public void checkResourceReady() {
        int savedVersionCode = UserData.getInstance(this).getVersion();
        int currentVersionCode = getVersionCode();
        if (savedVersionCode < currentVersionCode) {
            UnzipTask task = new UnzipTask(this);
            task.execute(UnzipTask.DIR);
        }
    }

    public void checkLicenseReady() {
        RequestLicenseTask task = new RequestLicenseTask(new RequestLicenseTask.ILicenseViewCallback() {

            @Override
            public Context getContext() {
                return getApplicationContext();
            }

            @Override
            public void onStartTask() {
                TextView view = (TextView)findViewById(R.id.load_textview);
                view.setText(R.string.wait_license);
                flMask.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEndTask(boolean result) {
                flMask.setVisibility(View.GONE);

            }
        });
        task.execute();

    }

    private int getVersionCode() {
//        Context context = getApplicationContext();
//        try {
//            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return -1;
//        }
        return ByteplusEffectsPlugin.getVersionCode();
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void onStartTask() {
        flMask.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEndTask(boolean result) {
        if (result) {
            UserData.getInstance(this)
                    .setVersion(getVersionCode());
        }
        if (!result) {
            ToastUtils.show("fail to copy resource, check your resource and re-open");
        } else {
            flMask.setVisibility(View.GONE);
        }
        checkLicenseReady();
    }
}
