// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
package com.example.byteplus_effects_plugin.effect.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.example.byteplus_effects_plugin.common.adapter.ItemViewRVAdapter;
import com.example.byteplus_effects_plugin.common.fragment.ItemViewPageFragment;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.effect.adapter.SelectRVAdapter;
import com.example.byteplus_effects_plugin.effect.manager.FilterDataManager;
import com.example.byteplus_effects_plugin.effect.model.FilterItem;

/** {zh} 
 * 滤镜
 */

/** {en}
 * Filter
 */

public class FilterFragment extends ItemViewPageFragment<SelectRVAdapter>
        implements ItemViewRVAdapter.OnItemClickListener<FilterItem>{
//    private RecyclerView rv;
    private String mSavedFilterPath;
    private IFilterCallback mCallback = null;
    private FilterDataManager mFilterDataManager = null;

    public FilterFragment setFilterCallback(IFilterCallback mCallback) {
        this.mCallback = mCallback;
        return this;
    }

    public interface IFilterCallback {
        void onFilterSelected(FilterItem filterItem, int position);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mFilterDataManager = new FilterDataManager(getContext());
        setAdapter(new SelectRVAdapter(mFilterDataManager.getItems(), this));
        setItemSelectedPadding(getResources().getDimensionPixelSize(R.dimen.select_padding));

        super.onViewCreated(view, savedInstanceState);
//        rv = view.findViewById(R.id.rv_filter);


//        SelectItemRVAdapter adapter = new SelectItemRVAdapter(getActivity(), mFilterDataManager.getItems(), this);


//        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        rv.setAdapter(adapter);
//        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//                int position = parent.getChildAdapterPosition(view);
//                int totalCount = parent.getAdapter().getItemCount();
//                int space = getResources().getDimensionPixelSize(com.example.byteplus_effects_plugin.R.dimen.item_distance)
//                        - 2*getResources().getDimensionPixelSize(com.example.byteplus_effects_plugin.R.dimen.select_padding);
//                if (position == 0) {//第一个
//                    outRect.left = 0;
//                    outRect.right = space / 2;
//                } else if (position == totalCount - 1){
//                    outRect.left = space / 2;
//                    outRect.right = 0;
//                } else {//中间其它的
//                    outRect.left = space / 2;
//                    outRect.right = space / 2;
//                }
//            }
//        });
    }


//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        int width = DensityUtils.getScreenWidth(getActivity());
//        int itemWidth = getResources().getDimensionPixelSize(com.example.byteplus_effects_plugin.R.dimen.logo_size);
//        int itemDistance = getResources().getDimensionPixelSize(com.example.byteplus_effects_plugin.R.dimen.item_distance);
//        int rvMarginHorizontal = getResources().getDimensionPixelSize(com.example.byteplus_effects_plugin.R.dimen.rv_margin_horizontal);
//        int itemCount = rv.getAdapter().getItemCount();
//        boolean rvContained = width - 2*rvMarginHorizontal >= itemCount * itemWidth + (itemCount - 1) * itemDistance;
//
//        if(rvContained) {
//            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) rv.getLayoutParams();
//            lp.rightMargin = rvMarginHorizontal;
//            lp.gravity = Gravity.CENTER_HORIZONTAL;
//            rv.setLayoutParams(lp);
//        }
//    }


//    public void refreshUI() {
//        if (rv == null) return;
//        SelectRVAdapter adapter = (SelectRVAdapter) rv.getAdapter();
//        if (adapter == null) return;
//        adapter.notifyDataSetChanged();
//    }

    public void setSavedFilterPath(String path) {
        // 注释掉这里是为了与iOS对齐，filter在退出再进入后保持原有的选择状态
//        if (null != getAdapter()) {
//            getAdapter().setSelect(0);
//        }
        mSavedFilterPath = path;
        refreshUI();
    }



    @Override
    public void onItemClick(FilterItem item, int position) {
        if (mCallback == null) {
            return;
        }
        mCallback.onFilterSelected(item, position);
        mSavedFilterPath = item.getResource();
        refreshUI();
    }
}
