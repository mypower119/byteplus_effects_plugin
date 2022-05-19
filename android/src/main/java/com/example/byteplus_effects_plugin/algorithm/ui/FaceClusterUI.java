package com.example.byteplus_effects_plugin.algorithm.ui;

import androidx.fragment.app.Fragment;

import com.example.byteplus_effects_plugin.algorithm.fragment.FaceClusterFragment;
import com.example.byteplus_effects_plugin.core.algorithm.FaceClusterAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.R;

;


/**
 * Created on 2020/8/19 15:01
 */
public class FaceClusterUI extends BaseAlgorithmUI<Object> {

    @Override
    void initView() {
        super.initView();

        addLayout(R.layout.layout_face_cluster_board, R.id.fl_algorithm_info);
        provider().setBoardTarget(R.id.fl_face_cluster_board);
    }

    @Override
    public void onReceiveResult(Object algorithmResult) {

    }

    @Override
    public IFragmentGenerator getFragmentGenerator() {
        return new IFragmentGenerator() {
            @Override
            public Fragment create() {
                return new FaceClusterFragment();
            }

            @Override
            public int title() {
                return R.string.tab_face_cluster;
            }

            @Override
            public AlgorithmTaskKey key() {
                return FaceClusterAlgorithmTask.FACE_CLUSTER;
            }
        };
    }
}
