package com.example.byteplus_effects_plugin.effect.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.byteplus_effects_plugin.common.fragment.ItemViewPageFragment;
import com.example.byteplus_effects_plugin.common.fragment.TabBoardFragment;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.effect.adapter.SelectRVAdapter;
import com.example.byteplus_effects_plugin.effect.model.ComposerNode;
import com.example.byteplus_effects_plugin.effect.model.EffectButtonItem;
import com.example.byteplus_effects_plugin.effect.view.ProgressBar;
import com.example.byteplus_effects_plugin.effect.view.RadioTextView;

import java.util.ArrayList;
import java.util.Arrays;


/** {zh} 
 * 风格妆Fragment
 * Created  on 2021/5/25 2:22 下午
 */

/** {en}
 * Style Makeup Fragment
 * Created on 2021/5/25 2:22 pm
 */

public class StyleMakeUpFragment extends TabBoardFragment implements View.OnClickListener, SelectRVAdapter.OnItemClickListener<EffectButtonItem>, ProgressBar.OnProgressChangedListener {

    private RadioTextView mRtFilter;
    private RadioTextView mRtMakeUp;
    private ProgressBar mProgressbar;
    private EffectButtonItem mEffectGroup;
    private EffectButtonItem mCurrentItem;
    private IMakeUpCallback mCallback;
    private float[] mItemIntensity;

    private Context mContext;

    public StyleMakeUpFragment(Context mContext) {
        this.mContext = mContext;
    }

    public StyleMakeUpFragment setData(EffectButtonItem item, IMakeUpCallback callback) {
        mEffectGroup = item;
        mCallback = callback;

        return this;

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_style_makeup, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        mRtFilter = view.findViewById(R.id.rt_filter);
        mRtFilter.setOnClickListener(this);
        mRtMakeUp = view.findViewById(R.id.rt_makeup);
        mRtMakeUp.setOnClickListener(this);

        mProgressbar = view.findViewById(R.id.pb_makeup);
        mProgressbar.setOnProgressChangedListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        int layoutID = this.getResources().getIdentifier("fl_effect_board", "id", getActivity().getPackageName());
        View view = getActivity().getWindow().findViewById(layoutID);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)view.getLayoutParams();
        int pbHeight = getResources().getDimensionPixelSize(R.dimen.height_progress_bar);
        lp.height = lp.height + pbHeight;
        view.setLayoutParams(lp);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        if (view.getId() == R.id.rt_filter) {
            mRtFilter.setState(true);
            mRtMakeUp.setState(false);
            updateProgressBar();
        } else if (view.getId() == R.id.rt_makeup) {
            mRtFilter.setState(false);
            mRtMakeUp.setState(true);
            updateProgressBar();
        }
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
        setFragmentList(new ArrayList<Fragment>(){
            {
                ItemViewPageFragment<SelectRVAdapter> fragment = new ItemViewPageFragment();
                fragment.setAdapter(new SelectRVAdapter(Arrays.asList(mEffectGroup.getChildren()),StyleMakeUpFragment.this));
                fragment.setItemSelectedPadding(getResources().getDimensionPixelSize(R.dimen.select_padding));
                add(fragment);
            }
        });

        setTitleList(new ArrayList<String>(){
            {
                add(getContext().getString(R.string.tab_style_makeup));
            }
        });
    }

    private void updateProgressBar(){
        if (mItemIntensity == null || mItemIntensity.length == 0){
            mProgressbar.setProgress(0);
            return;
        }
        if (mRtMakeUp.isSelected()){
            mProgressbar.setProgress(mItemIntensity[1]);
        }else if (mRtFilter.isSelected()){
            mProgressbar.setProgress(mItemIntensity[0]);

        }
    }

    @Override
    public void onItemClick(EffectButtonItem item, int position) {
        mCurrentItem = item;

        mEffectGroup.setSelectChild(item);
        onBeautySelect(item);

        ItemViewPageFragment currentFragment = (ItemViewPageFragment)getCurrentFragment();
        currentFragment.refreshUI();

    }

    @Override
    public void onProgressChanged(ProgressBar progressBar, float progress, boolean isFromUser) {
        if (!isFromUser) {
            return;
        }
        LogUtils.e("onProgressChanged "+progress);
        if (mCurrentItem == null || mCurrentItem.getId() < 0) return;
        if (progressBar != null && progressBar.getProgress() != progress) {
            progressBar.setProgress(progress);
        }

        int index = mRtFilter.isSelected() ? 0 : 1;
        if (mCurrentItem.getAvailableItem() == null ||
                (mCurrentItem.getAvailableItem().getNode().getKeyArray() == null || mCurrentItem.getAvailableItem().getNode().getKeyArray().length == 0) ||
                (mCurrentItem.getAvailableItem().getIntensityArray().length <= index)) {
            return;
        }
        mCurrentItem.getAvailableItem().getIntensityArray()[index] = progress;
        ComposerNode node = mCurrentItem.getAvailableItem().getNode();
        mCallback.updateComposerNodeIntensity(node.getPath(), node.getKeyArray()[index], progress);
    }

    private void onBeautySelect(EffectButtonItem item) {
        if (item == null) {
           return;
        }
        mItemIntensity = item.getIntensityArray();
        updateProgressBar();
        mCallback.onMakeUpItemSelect(item);


    }

    /** {zh} 
     * 恢复默认状态
     */
    /** {en} 
     * Restore the default state
     */

    public void resetDefault(){
        ItemViewPageFragment<SelectRVAdapter> currentFragment = (ItemViewPageFragment)getCurrentFragment();
        SelectRVAdapter adapter = currentFragment.getAdapter();
        if (null == adapter) return;
        adapter.setSelect(0);
        if (null == mProgressbar)return;
        mProgressbar.setProgress(0);


    }

    public interface IMakeUpCallback {
        void onMakeUpItemSelect(EffectButtonItem item);

        /** {zh} 
         * @param node  特效名称
         * @param key   功能 key
         * @param value 强度值
         * @brief 更新特效强度
         */
        /** {en} 
         * @param Node   Effect name
         * @param key    Function key
         * @param value  Strength value
         * @brief  Update effect strength
         */

        void updateComposerNodeIntensity(String node, String key, float value);

        /** {zh}
         * 回调Fragment内部点击事件
         */
        /** {en}
         * Callback Fragment Internal Click Event
         */

        void onClickEvent(View view);
    }
}
