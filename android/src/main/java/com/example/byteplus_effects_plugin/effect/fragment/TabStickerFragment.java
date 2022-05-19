package com.example.byteplus_effects_plugin.effect.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;

import com.example.byteplus_effects_plugin.common.fragment.TabBoardFragment;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.effect.resource.StickerGroup;
import com.example.byteplus_effects_plugin.effect.resource.StickerItem;
import com.example.byteplus_effects_plugin.effect.resource.StickerGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

;

/**
 * Created on 2020-03-12 11:59
 */
public class TabStickerFragment
        extends TabBoardFragment implements View.OnClickListener {
//    private TabLayout tl;
//    private ViewPager vp;
//    private FragmentVPAdapter mAdapter;
//    private List<Fragment> mFragments = new ArrayList<>();
//    private List<String> mTitles = new ArrayList<>();
    private OnTabStickerFramentCallback mCallback;
//    private ImageView ivClose;
//    private ImageView ivRecord;

    private int mSelectedTab = 0;

    public interface OnTabStickerFramentCallback {
        void onStickerSelected(StickerItem item, int tabIndex, int contentIndex);

        void onClickEvent(View view);
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
////        mHeadFragment = new EffectBoardHeadFragment();
//        return inflater.inflate(R.layout.fragment_tab_sticker, container, false);
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        vp = view.findViewById(R.id.vp_identify);
//        tl = view.findViewById(R.id.tl_identify);
//        ivClose = view.findViewById(R.id.iv_close_board);
//        ivClose.setOnClickListener(this);
//        ivRecord = view.findViewById(R.id.iv_record_board);
//        ivRecord.setOnClickListener(this);
//        view.findViewById(R.id.img_default).setVisibility(View.GONE);
//        removeButtonImgDefault();


//        mAdapter = new FragmentVPAdapter(getChildFragmentManager(), mFragments, mTitles);
//        vp.setAdapter(mAdapter);
//        vp.setOffscreenPageLimit(mFragments.size());
//        tl.setupWithViewPager(vp);

        super.onViewCreated(view,savedInstanceState);
    }

    @Override
    public void onViewPagerSelected(int position) {

    }

    @Override
    public void onClickEvent(View view) {
        if (mCallback == null) {
            LogUtils.e("mEffectCallback == null!!");

            return;
        }
        mCallback.onClickEvent(view);
    }

    @Override
    public void setData() {

    }

    public void setData(List<StickerGroup> groups) {
        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();

        for (int i = 0; i < groups.size(); i++) {
            StickerGroup group = groups.get(i);
            int finalI = i;
            fragments.add(new StickerFragment()
                        .setCallback(new StickerFragment.StickerFragmentCallback() {
                            @Override
                            public void onItemClick(StickerItem item, int position) {
                                mCallback.onStickerSelected(item, finalI, position);
                            }
                        }).setData(Arrays.asList(group.getItems())));
            titles.add(group.getTitle(getContext()));
        }

        refreshTabPageAdapterData(fragments,titles);

//        if (mAdapter != null) {
//            mAdapter.notifyDataSetChanged();
//        }
//        adjustTabLayoutStyle();
    }

//    private void adjustTabLayoutStyle() {
//        if (tl != null) {
//            if (mTitles.size() == 1) {
//                tl.setSelectedTabIndicatorColor(0);
//                tl.setTabMode(TabLayout.MODE_FIXED);
//            } else {
//                tl.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorWhite));
//                tl.setTabMode(TabLayout.MODE_SCROLLABLE);
//            }
//        }
//    }

    public void setCallback(OnTabStickerFramentCallback callback) {
        mCallback = callback;
    }

    public void resetDefault() {
        StickerFragment fragment = (StickerFragment) getCurrentFragment();
        if (fragment != null) {
            fragment.setSelected(0);
        }
    }

    public void setSelected(int pos) {
        StickerFragment fragment = (StickerFragment) getCurrentFragment();
        if(fragment != null){
            fragment.setSelected(pos);
        }
    }

    public void refresh() {
        StickerFragment fragment = (StickerFragment) getCurrentFragment();
        if(fragment != null){
            fragment.refresh();
        }
    }

    public void selectItem(int tabIndex, int contentIndex) {
        if (mSelectedTab != tabIndex) {
            ((StickerFragment) getFragment(mSelectedTab)).setSelected(0);
            mSelectedTab = tabIndex;
        }
        StickerFragment fragment = (StickerFragment) getFragment(tabIndex);
        if (fragment != null) {
            fragment.setSelected(contentIndex);
        }
    }

    public void refreshItem(int tabIndex, int contentIndex) {
        StickerFragment fragment = (StickerFragment) getFragment(tabIndex);
        if (fragment != null) {
            fragment.refreshItem(contentIndex);
        }
    }

}
