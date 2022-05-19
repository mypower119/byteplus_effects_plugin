package com.example.byteplus_effects_plugin.algorithm.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.byteplus_effects_plugin.algorithm.adapter.FaceClusterAdapter;
import com.example.byteplus_effects_plugin.algorithm.task.facecluster.FaceClusterMgr;
import com.example.byteplus_effects_plugin.common.utils.CommonUtils;
import com.example.byteplus_effects_plugin.common.utils.ToastUtils;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.R;
import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.entity.Media;

import java.util.ArrayList;
import java.util.List;

;

//import androidx.fragment.app.Fragment;;

public class FaceClusterFragment extends Fragment implements FaceClusterMgr.ClusterCallback, FaceClusterAdapter.OnItemClickListener {
    private static final int REQUEST_CODE_CHOOSE = 10;
    private View mIvClear;
    private View mIvAdd;
    private RelativeLayout mRlCluster;
    private RecyclerView mRvFaceList;
    private Button mBtnStart;
    private View mBtnRet;
    private ProgressBar mProgressBar;

    private FaceClusterMgr mFaceClusterMgr;

    private List<List<String>> mClusterResultList;
    private List<String> mChoosePicture;
    private FaceClusterAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.face_cluster_layout, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRlCluster = view.findViewById(R.id.rl_cluster);
        mBtnStart = view.findViewById(R.id.btn_cluster_start);
        mRvFaceList = view.findViewById(R.id.rv_cluster_list);
        mIvClear = view.findViewById(R.id.ll_cluster_clear);
        mIvAdd = view.findViewById(R.id.ll_cluster_add);
        mBtnRet = view.findViewById(R.id.btn_cluster_ret);
        mProgressBar = view.findViewById(R.id.progress);

        mIvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isFastClick()) {
                    LogUtils.e("too fast click");
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getActivity().getPackageManager())!= null){
                    startChoosePic();
                } else {
                    ToastUtils.show(getString(R.string.ablum_not_supported));
                }

            }
        });

        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isFastClick()){
                    LogUtils.e("too fast click");
                    return;
                }
                startCluster();
            }
        });

        mIvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isFastClick()){
                    LogUtils.e("too fast click");
                    return;
                }

                cleanData();
            }
        });

        mBtnRet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isFastClick()){
                    LogUtils.e("too fast click");
                    return;
                }

                mAdapter.resetCluster();
                v.setVisibility(View.GONE);
            }
        });

        LinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(this.getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvFaceList.setLayoutManager(layoutManager);
        mAdapter = new FaceClusterAdapter(getActivity());
        mRvFaceList.setAdapter(mAdapter);
        mFaceClusterMgr = new FaceClusterMgr(getActivity(), this );
    }

    private void startCluster() {
        LogUtils.d("cluster fragment start cluster");
        mBtnStart.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setIndeterminate(true);
        mFaceClusterMgr.cluster(mChoosePicture);
    }

    private void cleanData(){
        mFaceClusterMgr.clean();
        mIvAdd.setVisibility(View.VISIBLE);
        mRlCluster.setVisibility(View.GONE);
        mChoosePicture = null;
        mClusterResultList = null;
        mAdapter.clear();
    }

    private void setData(){
        mRlCluster.setVisibility(View.VISIBLE);
        mAdapter.setChooseList(mChoosePicture);
        mIvAdd.setVisibility(View.GONE);
        mRvFaceList.setVisibility(View.VISIBLE);
        mBtnStart.setEnabled(true);
        mBtnStart.setVisibility(View.VISIBLE);

        mProgressBar.setVisibility(View.GONE);
    }

    private void startChoosePic(){

        Intent intent =new Intent(getActivity(), PickerActivity.class);
        intent.putExtra(PickerConfig.SELECT_MODE,PickerConfig.PICKER_IMAGE);// {zh} 设置选择类型，默认是图片和视频可一起选择(非必填参数) {en} Set the selection type, the default is that pictures and videos can be selected together (non-required parameters)
        long maxSize=188743680L;// {zh} long long long long类型 {en} Long long long long type
        intent.putExtra(PickerConfig.MAX_SELECT_SIZE,maxSize); // {zh} 最大选择大小，默认180M（非必填参数） {en} Maximum selection size, default 180M (not required)
        intent.putExtra(PickerConfig.MAX_SELECT_COUNT,Integer.MAX_VALUE);  // {zh} 最大选择数量，默认40（非必填参数） {en} Maximum number of selections, default 40 (not required)

        startActivityForResult(intent,REQUEST_CODE_CHOOSE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == PickerConfig.RESULT_CODE) {

            ArrayList<Media> select = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);// {zh} 选择完后返回的list {en} Returning list after selection
            if (null == select || select.size() == 0)return;
            mChoosePicture = new ArrayList<String>(select.size());
            for (int i = 0;i < select.size();i++){
                mChoosePicture.add(select.get(i).path);
            }

            setData();
        }
    }

    @Override
    public void onClusterCallback(List<List<String>> result, int clusterNums) {
        mClusterResultList = result;
        mAdapter.setClusterResultList(mClusterResultList, clusterNums);
        mAdapter.setOpenCluserListener(this);
        mBtnStart.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClusterProcess(int process) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFaceClusterMgr.release();
    }

    @Override
    public void onOpenCluster() {
        mBtnRet.setVisibility(View.VISIBLE);
    }

    /**
     * fix: java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionViewHolder
     */
    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }
}
