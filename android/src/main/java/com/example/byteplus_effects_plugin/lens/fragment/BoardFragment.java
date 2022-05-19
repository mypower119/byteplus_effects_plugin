package com.example.byteplus_effects_plugin.lens.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;

import com.example.byteplus_effects_plugin.common.adapter.ItemViewRVAdapter;
import com.example.byteplus_effects_plugin.common.fragment.ItemViewPageFragment;
import com.example.byteplus_effects_plugin.common.fragment.TabBoardFragment;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.lens.adapter.ImageQualityRVAdapter;
import com.example.byteplus_effects_plugin.lens.manager.ImageQualityDataManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created on 2021/5/19 11:37
 */
public class BoardFragment extends TabBoardFragment implements ItemViewRVAdapter.OnItemClickListener<ImageQualityDataManager.ImageQualityItem> {
//        extends BoardButtonFragment<ImageQualityDataManager.ImageQualityItem, ButtonViewHolder>  {
    private Set<ImageQualityDataManager.ImageQualityItem> mSelectSet = new HashSet<>();
    private ImageQualityDataManager.ImageQualityItem mItem;
    private IImageQualityCallback mCallback;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        removeButtonImgDefault();
    }

    @Override
    public void onViewPagerSelected(int position) {

    }

    @Override
    public void onClickEvent(View view) {
        if (mCallback == null) {
            LogUtils.e("mCallback == null!!");
            return;
        }
        mCallback.onClickEvent(view);
    }

    @Override
    public void setData() {
        if (mItem != null) {
            setTitleList(new ArrayList<String>(){
                {
                    add(getContext().getString(mItem.getTitle()));
                }
            });
            setFragmentList(new ArrayList<Fragment>(){
                {
                    ImageQualityRVAdapter adapter = createAdapter();
                    ItemViewPageFragment<ImageQualityRVAdapter> fragment = new ItemViewPageFragment();
                    fragment.setAdapter(adapter);
                    add(fragment);
                }
            });
        }
    }

//    @Override
//    protected int layoutId() {
//        return R.layout.fragment_board_button;
//    }

    protected ImageQualityRVAdapter createAdapter() {
        return new ImageQualityRVAdapter(Collections.singletonList(mItem), this, mSelectSet);
    }

    @Override
    public void onItemClick(ImageQualityDataManager.ImageQualityItem item, int position) {
        boolean selected = mSelectSet.contains(item);
        mCallback.onItem(item, selected);

        ItemViewPageFragment currentFragment = (ItemViewPageFragment)getCurrentFragment();
        currentFragment.refreshUI();
    }

    /** {zh} 
     * 设置选中按钮
     * @param set
     * @return
     */
    /** {en} 
     * Set the selected button
     * @param set
     * @return
     */

    public BoardFragment setSelectSet(Set<ImageQualityDataManager.ImageQualityItem> set) {
        mSelectSet = set;
        return this;
    }

    public BoardFragment setItem(ImageQualityDataManager.ImageQualityItem item) {
        mItem = item;
        return this;
    }

    public BoardFragment setCallback(IImageQualityCallback callback){
        mCallback = callback;
        return this;
    }

    public interface IImageQualityCallback{
        void onItem(ImageQualityDataManager.ImageQualityItem item, boolean flag);
        void onClickEvent(View view);
    }

}
