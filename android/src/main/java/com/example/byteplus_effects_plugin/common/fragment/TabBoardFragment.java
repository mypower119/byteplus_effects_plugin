package com.example.byteplus_effects_plugin.common.fragment;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.common.adapter.FragmentVPAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public abstract class TabBoardFragment extends Fragment implements View.OnClickListener {

    private ImageView ivClose;
    private ImageView ivRecord;
    private ImageView imgDefault;


    private TabLayout tl;
    private ViewPager vp;
    private FragmentVPAdapter mAdapter;

    private int mTabSelectedPosition;

    private List<Fragment> mFragmentList;
    private List<String> mTitleList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_board, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tl = view.findViewById(R.id.tl_board_head);
        vp = view.findViewById(R.id.vp_board_content);

        ivClose = view.findViewById(R.id.iv_close_board);
        ivClose.setOnClickListener(this);
        ivRecord = view.findViewById(R.id.iv_record_board);
        ivRecord.setOnClickListener(this);
        imgDefault = view.findViewById(R.id.img_default);
        imgDefault.setOnClickListener(this);

        initVP();
        adjustTabLayoutStyle();
    }

    private void initVP() {
        setData();
        mAdapter = new FragmentVPAdapter(getChildFragmentManager(), mFragmentList, mTitleList);
        vp.setAdapter(mAdapter);
        if(mFragmentList != null){
            vp.setOffscreenPageLimit(mFragmentList.size());
        }
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mTabSelectedPosition = i;
                onViewPagerSelected(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        tl.setupWithViewPager(vp);
    }

    private void adjustTabLayoutStyle() {
        if (tl != null && mTitleList != null) {
            if (mTitleList.size() == 1) {
                tl.setSelectedTabIndicatorColor(0);
                tl.setTabMode(TabLayout.MODE_FIXED);
            } else {
                tl.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorWhite));
                tl.setTabMode(TabLayout.MODE_SCROLLABLE);
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.iv_close_board || v.getId() == R.id.iv_record_board || v.getId() == R.id.img_default) {
            onClickEvent(v);
        }

    }

//    public void setSelected(int pos) {
//        if (null == mAdapter) return;
//        StickerFragment fragment = (StickerFragment) mAdapter.getCurrentFragment();
//        fragment.setSelected(pos);
//    }

    public void removeButtonImgDefault (){
        getView().findViewById(R.id.img_default).setVisibility(View.GONE);
    }

    public void setFragmentList(List<Fragment> fragments){
        this.mFragmentList = fragments;
    }
    public void setTitleList(List<String> titles){
        this.mTitleList = titles;
    };

    public Fragment getCurrentFragment(){
        if (mAdapter == null) {
            return null;
        }
        return mAdapter.getCurrentFragment();
    }

    public int getSelectedTabId() {
        return tl.getSelectedTabPosition();
    }

    public Fragment getFragment(int position){
        if (mFragmentList == null) {
            return null;
        }
        return mFragmentList.get(position);
    }

    public List<Fragment> getFragmentList() {
        return mFragmentList;
    }


    public void showTabPage(int duration){
        tl.setVisibility(View.VISIBLE);
        vp.setVisibility(View.VISIBLE);
        if(duration > 0){
            tl.animate().alpha(1).setDuration(duration).start();
            vp.animate().alpha(1).setDuration(duration).start();
        }
    }

    public void hideTabPage(int duration){
        if(duration > 0){
            tl.animate().alpha(0).setDuration(duration).start();
            vp.animate().alpha(0).setDuration(duration).start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tl.setVisibility(View.GONE);
                    vp.setVisibility(View.GONE);
                }
            }, duration);
        } else {
            tl.setVisibility(View.GONE);
            vp.setVisibility(View.GONE);
        }
    }

//    public void setTabPageVisibility(int visibility, int duration){
//        int alpha = 0;
//        switch (visibility) {
//            case View.VISIBLE:
//                alpha = 0;
//            case View.GONE:
//                alpha = 1;
//        }
//        tl.animate().alpha(alpha).setDuration(duration).start();
//        tl.setVisibility(visibility);
//        vp.setVisibility(visibility);
//
//        mHeadFragment.tvTitle.animate().alpha(0).setDuration(ANIMATION_DURATION).start();
//    }

    public void refreshTabPageAdapterData(List<Fragment> fragments, List<String> titles) {
        setFragmentList(fragments);
        setTitleList(titles);
        if(mAdapter != null){
            mAdapter.setContent(mFragmentList,mTitleList);
            mAdapter.notifyDataSetChanged();
        }
        adjustTabLayoutStyle();
    }


    protected float setBoardFragmentHeight(float height, int duration){
        int boardLayoutID = getResources().getIdentifier("fl_effect_board", "id", getActivity().getPackageName());
        View boardView = getActivity().getWindow().findViewById(boardLayoutID);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)boardView.getLayoutParams();
        float diffHeight = lp.height - height;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(lp.height, (int) height);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lp.height = (int)animation.getAnimatedValue();
                boardView.setLayoutParams(lp);
            }
        });
        valueAnimator.setDuration(duration);
        valueAnimator.start();
        return diffHeight;
    }

    public int getTabSelectedPosition() {
        return mTabSelectedPosition;
    }

    // To implement a new Fragment, abstract methods below must be implemented.

    public abstract void onViewPagerSelected(int position);
    // implements click event for close_board/photo_shot/reset_to_default 3 buttons.
    public abstract void onClickEvent(View view);
    // using setFragmentList & setTitleList these 2 methods to set data
    public abstract void setData();

}
