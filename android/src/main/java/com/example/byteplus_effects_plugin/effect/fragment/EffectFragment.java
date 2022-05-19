package com.example.byteplus_effects_plugin.effect.fragment;

import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.DESC_HAIR_DYE_FULL;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.DESC_HAIR_DYE_HIGHLIGHT;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.DESC_HAIR_DYE_HIGHLIGHT_PART_A;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.DESC_HAIR_DYE_HIGHLIGHT_PART_B;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.DESC_HAIR_DYE_HIGHLIGHT_PART_C;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.DESC_HAIR_DYE_HIGHLIGHT_PART_D;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.MASK;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.OFFSET;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_CLOSE;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_FILTER;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_HAIR_DYE;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_HAIR_DYE_FULL;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_HAIR_DYE_HIGHLIGHT;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_LIPSTICK;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_LIPSTICK_GLOSSY;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_LIPSTICK_MATTE;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_MAKEUP_HAIR;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.byteplus_effects_plugin.common.fragment.TabBoardFragment;
import com.example.byteplus_effects_plugin.common.model.EffectType;
import com.example.byteplus_effects_plugin.common.utils.DensityUtils;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.effect.manager.EffectDataManager;
import com.example.byteplus_effects_plugin.effect.model.ColorItem;
import com.example.byteplus_effects_plugin.effect.model.EffectButtonItem;
import com.example.byteplus_effects_plugin.effect.model.FilterItem;
import com.example.byteplus_effects_plugin.effect.view.ColorListView;
import com.example.byteplus_effects_plugin.effect.view.ProgressBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EffectFragment extends TabBoardFragment implements View.OnClickListener, BeautyFaceFragment.IBeautyCallBack, FilterFragment.IFilterCallback, ProgressBar.OnProgressChangedListener, ColorListView.ColorSelectCallback, TryOnFragment.ITryOnCallBack {
    private static final String ARG_PARAM_COLOR_LIST_POSITION = "color_list_position";
    private static final String ARG_PARAM_USE_PROGRESS_BAR = "use_progress_bar";
    public static final int BOARD_FRAGMENT_HEAD_INSIDE = 0;
    public static final int BOARD_FRAGMENT_HEAD_ABOVE = 1;

    public static final String TAG_OPTION_FRAGMENT = "option_fragment";
    public static final String TAG_VARY_HEIGHT = "vary_height";
    public static final int ANIMATION_DURATION = 400;

    private ProgressBar pb;
    private TextView tvTitle;
    private ColorListView colorListView;
    private ImageView ivCloseMakeupOption;

    private int colorListPosition = BOARD_FRAGMENT_HEAD_INSIDE;
    private boolean useProgressBar = true;

    public Set<EffectButtonItem> getSelectNodes() {
        return mSelectNodes;
    }

    //   {zh} 所有被选中的小项       {en} All selected items
    private Set<EffectButtonItem> mSelectNodes;
    //   {zh} 当前选中的小项       {en} Current selected item  
    private EffectButtonItem mCurrentItem;
    //   {zh} 由于滤镜与其他特效不在一个体系，其他的特效（美颜、美型、美体、美妆等）都通过       {en} Since filters are not in the same system as other special effects, other special effects (beauty, beauty, body beauty, beauty makeup, etc.) are passed
    //   {zh} EffectButtonItem 管理了，但是滤镜不在这个体系，所以包括保存项、强度都需要单独管理       {en} EffectButtonItem is managed, but the filter is not in this system, so the saving items and strength need to be managed separately
    //   {zh} TODO：尝试将滤镜也纳入 EffectButtonItem 体系，或者移出当前 fragment       {en} TODO: Try to include the filter in the EffectButtonItem system, or remove the current fragment  
    private String mSavedFilterPath;
    private float mSavedFilterIntensity;
    private boolean mIsFilter;
    private IEffectCallback mEffectCallback;
    private List<TabItem> mTabList;

    private EffectDataManager mEffectDataManager;

    public static EffectFragment newInstance(int param1, boolean param2) {
        EffectFragment fragment = new EffectFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_COLOR_LIST_POSITION, param1);
        args.putBoolean(ARG_PARAM_USE_PROGRESS_BAR,param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            colorListPosition = getArguments().getInt(ARG_PARAM_COLOR_LIST_POSITION);
            useProgressBar = getArguments().getBoolean(ARG_PARAM_USE_PROGRESS_BAR);
        }
    }

    /** {zh}
     * 设置Fragment回调
     */
    /** {en} 
     * Set Fragment Callback
     */

    public EffectFragment setCallback(IEffectCallback callback) {
        mEffectCallback = callback;
        return this;

    }

    public EffectFragment setColorListPosition(int position){
        colorListPosition = position;
        return this;
    }

    public EffectFragment useProgressBar(Boolean bool){
        useProgressBar = bool;
        return this;
    }


    /** {zh} 
     * 绑定数据
     *
     * @param tabItemList
     * @return
     */
    /** {en} 
     * Binding data
     *
     * @param tabItemList
     * @return
     */

    public void setData(Context context,EffectDataManager dataManager,  List<TabItem> tabItemList, EffectType effectType) {
        mEffectDataManager = dataManager;
        if (mEffectDataManager == null)return;
        mSelectNodes = mEffectDataManager.getDefaultItems();
        mTabList = tabItemList;
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        ArrayList<String> titleList = new ArrayList<>();

        for (TabItem tabItem : mTabList) {
            if (tabItem.id == TYPE_FILTER) {
                fragmentList.add(
                        new FilterFragment().setFilterCallback(this)
                );
            } else if ( (tabItem.id == TYPE_LIPSTICK_GLOSSY) || (tabItem.id == TYPE_LIPSTICK_MATTE) || (tabItem.id == TYPE_HAIR_DYE_FULL) || (tabItem.id == TYPE_HAIR_DYE_HIGHLIGHT) ) {
                fragmentList.add(
                        new TryOnFragment()
                                .setData(mEffectDataManager.getSubItem(tabItem.id))
                                .setSelectNodes(mSelectNodes)
                                .usePoint(true)
                                .setTryOnCallBack(this)
                );
            } else {
                fragmentList.add(
                        new BeautyFaceFragment()
                                .setData(mEffectDataManager.getItem(tabItem.id))
                                .setSelectNodes(mSelectNodes)
                                .setBeautyCallBack(this)
                );
            }
            titleList.add(context.getString(tabItem.title));
        }

        setFragmentList(fragmentList);
        setTitleList(titleList);

    }

    @Override
    public EffectType getEffectType() {
        if (mEffectCallback == null) {
            return EffectType.LITE_ASIA;
        }
        return mEffectCallback.getEffectType();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_effect, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pb = view.findViewById(R.id.pb1);
        pb.setOnProgressChangedListener(this);
        if (!useProgressBar) {
            pb.setVisibility(View.GONE);
        }

        LayoutInflater.from(getContext()).inflate(R.layout.layout_effect_board_head, view.findViewById(R.id.fl_board_head),true);
        tvTitle = view.findViewById(R.id.tv_title_identify);
        ivCloseMakeupOption = view.findViewById(R.id.iv_close_makeup_option);
        ivCloseMakeupOption.setOnClickListener(this);
        switch (colorListPosition) {
            case BOARD_FRAGMENT_HEAD_ABOVE:
                colorListView = view.findViewById(R.id.color_list_above);
                break;
            default:
                colorListView = view.findViewById(R.id.color_list);
        }

//        mHeadFragment.setClickListener(this);
//        getChildFragmentManager()
//                .beginTransaction()
//                .add(R.id.fl_board_head, mHeadFragment)
//                .show(mHeadFragment)
//                .commit();
//        tvTitle = view.findViewById(R.id.tv_title_identify);
//        tl = view.findViewById(R.id.tl_identify);
//        vp = view.findViewById(R.id.vp_identify);
//        ivClose = view.findViewById(R.id.iv_close_board);
//        ivClose.setOnClickListener(this);
//        ivRecord = view.findViewById(R.id.iv_record_board);
//        ivRecord.setOnClickListener(this);
//        imgDefault = view.findViewById(R.id.img_default);
//        imgDefault.setOnClickListener(this);
//        initVP();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        int layoutID = this.getResources().getIdentifier("fl_effect_board", "id", getActivity().getPackageName());
        View view = getActivity().getWindow().findViewById(layoutID);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)view.getLayoutParams();
        if (useProgressBar) {
            int pbHeight = getResources().getDimensionPixelSize(R.dimen.height_progress_bar);
            lp.height = lp.height + pbHeight;
        }
        // {zh} 初始化界面显不显示colorListView {en} Initial UI display colorListView
//        switch (colorListPosition) {
//            case BOARD_FRAGMENT_HEAD_ABOVE:
//                int colorListID = this.getResources().getIdentifier("color_list_above", "id", getActivity().getPackageName());
//                View colorListView = getActivity().getWindow().findViewById(colorListID);
//                lp.height = lp.height + colorListView.getHeight() + getResources().getDimensionPixelSize(R.dimen.colorlistview_margin_bottom);
//                break;
//            default:
//        }
        view.setLayoutParams(lp);
    }

    //    /** {zh}
//     * 初始化ViewPager
//     */
//    /** {en}
//     * Initialize ViewPager
//     */
//
//    private void initVP() {
//        FragmentVPAdapter adapter = new FragmentVPAdapter(getChildFragmentManager(), mFragmentList, mTitleList);
//        vp.setAdapter(adapter);
//        vp.setOffscreenPageLimit(mFragmentList.size());
//        vp.addOnPageChangeListener(new OnPageChangeListenerAdapter() {
//            @Override
//            public void onPageSelected(int position) {
//            }
//        });
//        tl.setupWithViewPager(vp);
//    }



    @Override
    public void onEffectItemClick(EffectButtonItem item) {
        if (item == null) return;


        if (item.hasChildren()) {

            // 解决点击item才能发生作用对象的问题，检查所有child中有无「被选中的根节点」，若有则选做mCurrentItem；有颜色更新colorPanel
            for (EffectButtonItem child: item.getChildren()){
                if (mSelectNodes.contains(child) && !child.hasChildren()){
                    mCurrentItem = child;
                    LogUtils.d("update mCurrentItem = "+ mCurrentItem);

                    // 该子节点有颜色选项时，设置colorListView的相应颜色为父节点颜色的选中状态
                    ArrayList<ColorItem> colorItems = child.getColorItems();
                    if (colorItems != null && colorItems.size() > 0) {
                        colorListView.setSelect(item.getSelectColorIndex());
                    }
                }
            }

            showOrHideOptionFragment(true, item);
        }else{
            mCurrentItem = item;
            LogUtils.d("update mCurrentItem = "+ mCurrentItem);

            // TODO: clear other type lipstick if different type
            if ( (mCurrentItem.getId() & MASK) == TYPE_LIPSTICK ) {
                switch (mCurrentItem.getId()) {
                    case TYPE_LIPSTICK_GLOSSY:
                        mSelectNodes.removeAll( new HashSet<>(Arrays.asList(mEffectDataManager.getItem(TYPE_LIPSTICK_MATTE).getChildren())));
                        ((TryOnFragment) getFragmentList().get(1)).setSelected(0);
                        break;
                    case TYPE_LIPSTICK_MATTE:
                        mSelectNodes.removeAll( new HashSet<>(Arrays.asList(mEffectDataManager.getItem(TYPE_LIPSTICK_GLOSSY).getChildren())));
                        ((TryOnFragment) getFragmentList().get(0)).setSelected(0);
                        break;
                }
            }

            updateProgressWithItem(item);
            if (item.getId() == TYPE_CLOSE){
                showOrHideColorPanel(false,null);
            }

            updateComposerNodes(mSelectNodes);

            if ( ((item.getId() & MASK) ==  TYPE_HAIR_DYE) || (item.getDesc() == DESC_HAIR_DYE_FULL) || (item.getDesc() == DESC_HAIR_DYE_HIGHLIGHT)) {

                if (item.getNode() != null) {
                    //  {zh} 有颜色选择的情况，需要将颜色值设置到强度数组中  {en} If there is a color selection, you need to set the color value to the intensity array
                    ArrayList<ColorItem> colorItems = item.getColorItems();
                    if (colorItems != null && colorItems.size() > 0){
                        if (item.getId() == TYPE_HAIR_DYE_HIGHLIGHT) {
                            showOrHideColorPanel(true, colorItems);
                        }
                        // 设置颜色栏选中状态
                        int index = mCurrentItem.getSelectColorIndex();
                        if (mEffectCallback != null) {
                            if ( (item.getId() == TYPE_CLOSE) && (item.getDesc() == DESC_HAIR_DYE_HIGHLIGHT) ) {
                                int[] parts = new int[] {
                                        DESC_HAIR_DYE_HIGHLIGHT_PART_A,
                                        DESC_HAIR_DYE_HIGHLIGHT_PART_B,
                                        DESC_HAIR_DYE_HIGHLIGHT_PART_C,
                                        DESC_HAIR_DYE_HIGHLIGHT_PART_D,
                                };
                                for (int part : parts ) {
                                    mEffectCallback.onHairDyeSelected(part,item.getColorItems().get(index));
                                }
                            } else {
                                mEffectCallback.onHairDyeSelected(item.getDesc(),item.getColorItems().get(index));
                            }
                        }
                        colorListView.setSelect(index);
                    }
                }
                return;
            }

            if (item.getNode() != null) {
                //  {zh} 有颜色选择的情况，需要将颜色值设置到强度数组中  {en} If there is a color selection, you need to set the color value to the intensity array
                ArrayList<ColorItem> colorItems = item.getColorItems();
                if (colorItems != null && colorItems.size() > 0){
                    if (colorItems != null && colorItems.size() > 0) {
                        showOrHideColorPanel(true, colorItems);
                    }else {
                        showOrHideColorPanel(false,null);
                    }
                    // 设置颜色栏选中状态
                    int index = item.getParent().getSelectColorIndex();
                    colorListView.setSelect(index);
                    if (item.getIntensityArray().length == 4){
                        item.getIntensityArray()[1] = colorItems.get(index).getR();
                        item.getIntensityArray()[2] = colorItems.get(index).getG();
                        item.getIntensityArray()[3] = colorItems.get(index).getB();
                    }
                }
                updateComposerNodeIntensity(item);
            }

            if (item.getParent() != null){
                showTip(getString(item.getTitle()),item.getDesc() == 0?"":getString(item.getDesc()));
            }

        }


    }

    @Override
    public void onEffectItemClose(EffectButtonItem item) {
        if (item.getIntensityArray() != null) {
            for (int i = 0; i < item.getIntensityArray().length; i++) {
                item.getIntensityArray()[i] = item.isEnableNegative()?0.5f:0f;
            }

        }
        updateComposerNodeIntensity(item);

    }

    /** {zh}
     * @param filterItem 滤镜对象
     * @param position   滤镜位置
     * @brief 滤镜点击回调
     * @details 滤镜的点击与 EffectButtonItem 不同，需要另外处理
     */
    /** {en} 
     * @param filterItem Filter object
     * @param position   Filter location
     * @brief Filter click callback
     * @details Filter clicks are different from EffectButtonItem and need additional processing
     */

    @Override
    public void onFilterSelected(FilterItem filterItem, int position) {
        float defaultIntensity = filterItem == null ? 0f : filterItem.getIntensity();
        String filterPath = filterItem == null ? "" : filterItem.getResource();
        pb.setProgress(defaultIntensity);

        updateFilter(filterPath);
        updateFilterIntensity(defaultIntensity);

        mSavedFilterPath = filterPath;
        mSavedFilterIntensity = defaultIntensity;

        //    {zh} 显示tip        {en} Display tips  
        showTip(getContext().getString(filterItem.getTitle()) ,"");
    }


    /** {zh} 
     * @param progressBar 事件来源
     * @param progress    进度，0-1
     * @param isFromUser  事件是否来自于手动滑动
     * @brief 分发强度
     * @details 将滑杆的滑动事件分发出去，两个出口
     * 1、调用各 fragment 更新 UI
     * 2、调用 updateFilterIntensity/updateComposerNodeIntensity 更新特效强度
     */
    /** {en} 
     * @param progressBar  Event source
     * @param progress     progress, 0-1
     * @param isFromUser   Whether the event comes from manual sliding
     * @brief  Distribution strength
     * @details  Distribute the sliding event of the slider, two exits
     * 1, call each fragment to update the UI
     * 2, call  updateFilterIntensity/updateComposerNodeIntensity  update the special effect strength
     */

    @Override
    public void onProgressChanged(ProgressBar progressBar, float progress, boolean isFromUser) {
        if (!isFromUser) {
            return;
        }
        //    {zh} 滤镜不在EffectButtonItem体系        {en} Filter not in EffectButtonItem system  
        if (!mIsFilter){
            if (mCurrentItem == null || mCurrentItem.getId() < 0) return;
        }
        if (progressBar != null && progressBar.getProgress() != progress) {
            progressBar.setProgress(progress);
        }
        if (mIsFilter) {
            mSavedFilterIntensity = progress;
            updateFilterIntensity(progress);
            return;
        }

        if (mCurrentItem.getAvailableItem() == null ||
                (mCurrentItem.getAvailableItem().getNode().getKeyArray() == null || mCurrentItem.getAvailableItem().getNode().getKeyArray().length == 0) ||
                (mCurrentItem.getAvailableItem().getIntensityArray().length <= 0)) {
            return;
        }

        mCurrentItem.getAvailableItem().getIntensityArray()[0] = progress;
        LogUtils.d("progress = "+progress);
        updateComposerNodeIntensity(mCurrentItem);
        refreshVP();

    }

    @Override
    public void onColorSelected(int index) {
        if ( mTabList.get(getSelectedTabId()).id ==  TYPE_HAIR_DYE_HIGHLIGHT ) {
            mCurrentItem.setSelectColorIndex(index);
            LogUtils.d("onColorSelected: "+mCurrentItem);
            if (mCurrentItem.getIntensityArray().length == 4){
                ArrayList<ColorItem> colorItems = mCurrentItem.getColorItems();
                mCurrentItem.getIntensityArray()[1] = colorItems.get(index).getR();
                mCurrentItem.getIntensityArray()[2] = colorItems.get(index).getG();
                mCurrentItem.getIntensityArray()[3] = colorItems.get(index).getB();
            }

            if (mEffectCallback != null) {
                mEffectCallback.onHairDyeSelected(mCurrentItem.getDesc(),mCurrentItem.getColorItems().get(index));
            }

            return;
        }

        mCurrentItem.getParent().setSelectColorIndex(index);
        LogUtils.d("onColorSelected: "+mCurrentItem);
        if (mCurrentItem.getIntensityArray().length == 4){
            ArrayList<ColorItem> colorItems = mCurrentItem.getColorItems();
            mCurrentItem.getIntensityArray()[1] = colorItems.get(index).getR();
            mCurrentItem.getIntensityArray()[2] = colorItems.get(index).getG();
            mCurrentItem.getIntensityArray()[3] = colorItems.get(index).getB();
        }

        updateComposerNodeIntensity(mCurrentItem);





    }

    /** {zh}
     * 根据当前Nodes直接刷新特效
     * 在EffectDataManager
     */
    /** {en} 
     * Refresh effects directly based on current Nodes
     *  in EffectDataManager
     */

    public void refreshByCurrentSelect() {
        updateComposerNodes(mSelectNodes);
        for (EffectButtonItem it : mSelectNodes) {
            updateComposerNodeIntensity(it);
        }

    }



    /** {zh} 
     * @brief 设置默认特效
     * @details 将所有的值都设置为默认给定的值，并加入默认的美颜、美型特效，需要解决三个问题
     * 1、各功能强度变动后，需要更新 UI
     * 2、修改默认值不影响当前的选中状态（原来选中的按钮依旧选中，进度条依旧指示当前选中的按钮）
     * 3、不影响其他功能
     */
    /** {en} 
     * @brief Set default effects
     * @details  Set all values to the default given values, and add the default beauty and beauty effects. Three problems need to be solved
     * 1. After the strength of each function changes, you need to update the UI
     * 2. Modifying the default value does not affect the current selected state (the original selected button is still selected, and the progress bar still indicates the currently selected button)
     * 3. It does not affect other functions
     */

    public void resetToDefault() {
        if (mEffectCallback == null) return;
        boolean viewLoaded = getView() != null;
        mSelectNodes.clear();
        mSelectNodes.addAll(mEffectDataManager.getDefaultItems());
        LogUtils.e("222mSelectNode="+mSelectNodes+"  size = "+mSelectNodes.size());

        if (mCurrentItem != null && !mSelectNodes.contains(mCurrentItem)) {
            //    {zh} 进度条复位        {en} Progress bar reset  
            if (mCurrentItem.isEnableNegative()) {
                pb.setProgress(0.5f);
            }else {
                pb.setProgress(0.f);
            }
            mCurrentItem = null;

        }

        mSavedFilterPath = null;
        mSavedFilterIntensity = 0.f;

        for (Fragment fragment : getFragmentList()) {
            if (fragment instanceof TryOnFragment) {
                ((TryOnFragment) fragment).setSelected(0);
            }
        }

        // reset UI
        if (viewLoaded) {
            refreshVP();
            showOrHideOptionFragment(false, null);
            if (mCurrentItem != null) {
                updateProgressWithItem(mCurrentItem);
            }
            //    {zh} 默认美颜没有滤镜，强度置0        {en} The default beauty has no filter, the intensity is set to 0  
            if (mIsFilter) {
                pb.setProgress(0.f);
            }

        }
    }


    //    {zh} 更新 ViewPager UI        {en} Update ViewPager UI  
    private void refreshVP() {
        for (Fragment fragment : getFragmentList()) {
            if (fragment instanceof BeautyFaceFragment) {
                ((BeautyFaceFragment) fragment).refreshUI();
            } else if (fragment instanceof FilterFragment) {
                ((FilterFragment) fragment).setSavedFilterPath(mSavedFilterPath);
            } else if (fragment instanceof TryOnFragment) {
                ((TryOnFragment) fragment).refreshUI();
            }
        }
        BeautyFaceFragment makeupOptionFragment = getOptionFragment();
        if (null != makeupOptionFragment){
            makeupOptionFragment.refreshUI();
        }

    }

    private void updateComposerNodes(Set<EffectButtonItem> effectButtonItems) {
        if (mEffectCallback == null) {
            return;
        }
        mEffectCallback.updateComposeNodes(effectButtonItems);
    }

    private void updateComposerNodeIntensity(EffectButtonItem effectButtonItem) {
        if (mEffectCallback == null) {
            return;
        }
        mEffectCallback.updateComposerNodeIntensity(effectButtonItem);
    }

    private void updateFilter(String filter) {
        if (mEffectCallback == null) {
            return;
        }
        mEffectCallback.onFilterSelected(filter);
    }

    /** {zh} 
     * 根据回调显示tip，滤镜和二级美妆按钮需要在屏幕上部弹出气泡
     *
     * @param title
     * @param desc
     */
    /** {en} 
     * According to the callback, the tip, filter and secondary makeup buttons need to pop bubbles on the upper part of the screen
     *
     * @param title
     * @param desc
     */

    private void showTip(String title, String desc) {
        if (mEffectCallback == null) {
            return;
        }
        mEffectCallback.showTip(title, desc);
    }


    public void showOrHideColorPanel(boolean isShow,ArrayList<ColorItem> colorItems){
        switch (colorListPosition) {
            case BOARD_FRAGMENT_HEAD_ABOVE:
                showColorListHeadAbove(isShow, colorItems);
                break;
            default:
                showColorListHeadInside(isShow, colorItems);
        }
    }

    private void showColorListHeadAbove(boolean isShow, ArrayList<ColorItem> colorItems) {

        int height = getResources().getDimensionPixelSize(R.dimen.height_board_total);
        int pbHeight = 0;
        if (useProgressBar) {
            pbHeight = getResources().getDimensionPixelSize(R.dimen.height_progress_bar);
        }
        float colorListHeight = DensityUtils.dp2px(getContext(),24);
        float colorListHeightMarginBottom = getResources().getDimensionPixelSize(R.dimen.colorlistview_margin_bottom);

        if (isShow){
            if (colorItems != null && colorItems.size() > 0){
                colorListView.setVisibility(View.VISIBLE);
                super.setBoardFragmentHeight(height + pbHeight + colorListHeight + colorListHeightMarginBottom,0);

                colorListView.updateColors(colorItems, this);
            }
        }else {

            super.setBoardFragmentHeight(height + pbHeight,0);
            colorListView.setVisibility(View.GONE);
        }

    }

    private void showColorListHeadInside(boolean isShow, ArrayList<ColorItem> colorItems) {
        if (isShow){
            if (colorItems != null && colorItems.size() > 0){
                colorListView.setVisibility(View.VISIBLE);
                tvTitle.setVisibility(View.GONE);
                colorListView.updateColors(colorItems, this);
            }
        }else {
            colorListView.setVisibility(View.GONE);
            tvTitle.setVisibility(View.VISIBLE);
        }
    }

    private void updateFilterIntensity(float intensity) {
        if (mEffectCallback == null) {
            return;
        }
        mEffectCallback.onFilterValueChanged(intensity);
    }


    private BeautyFaceFragment getOptionFragment(){
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.board_enter, R.anim.board_exit);
        return (BeautyFaceFragment) manager.findFragmentByTag(TAG_OPTION_FRAGMENT);

    }

    /** {zh} 
     * @param isShow 是否显示
     * @brief 显示/隐藏三级菜单
     * @details 显示/隐藏三级菜单，在没有实例的情况下会先初始化一个实例
     */
    /** {en} 
     * @param isShow  Whether to show
     * @brief  Show/hide the three-level menu
     * @details  Show/hide the three-level menu, which initializes an instance first if there is no instance
     */

    private void showOrHideOptionFragment(boolean isShow, EffectButtonItem item) {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        transaction.setCustomAnimations(R.anim.board_enter, R.anim.board_exit);
        Fragment optionFragment = getOptionFragment();

        if (isShow) {
            // BoardFragment 变高应用示例
//            setBoardFragmentHeight(DensityUtils.dp2px(getContext(),260),ANIMATION_DURATION);

            hideTabPage(ANIMATION_DURATION/2);
            ivCloseMakeupOption.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.animate().alpha(1).setDuration(ANIMATION_DURATION/2).start();
            tvTitle.setText(item.getTitle());
            ivCloseMakeupOption.animate().alpha(1).setDuration(ANIMATION_DURATION/2).start();
            if (optionFragment == null) {
                optionFragment = generateMakeupOptionFragment();
                ((BeautyFaceFragment) optionFragment).setData(item).setSelectNodes(mSelectNodes).setBeautyCallBack(this);
                transaction.add(R.id.fl_board_content, optionFragment, TAG_OPTION_FRAGMENT).commit();
            } else {
                ((BeautyFaceFragment) optionFragment).setData(item).setSelectNodes(mSelectNodes);
                transaction.show(optionFragment).commit();
            }
            for (EffectButtonItem child: item.getChildren()){
                if (mSelectNodes.contains(child) && item.getColorItems() != null){
                        showOrHideColorPanel(true, item.getColorItems());
                }
            }
        } else {
            // BoardFragment 变高应用示例
//            setBoardFragmentHeight(getResources().getDimension(R.dimen.height_board_total),ANIMATION_DURATION);

            if (colorListView != null) {
                //  {zh} 清空保存到颜色值  {en} Clear Save to Color Value
                showOrHideColorPanel(false,null);
            }

            if (optionFragment == null) return;
            transaction.hide(optionFragment).commit();
            showTabPage(ANIMATION_DURATION/2);
            tvTitle.animate().alpha(0).setDuration(ANIMATION_DURATION/2).start();
            ivCloseMakeupOption.animate().alpha(0).setDuration(ANIMATION_DURATION/2).start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvTitle.setVisibility(View.GONE);
                    ivCloseMakeupOption.setVisibility(View.GONE);
                }
            }, ANIMATION_DURATION/2);
        }
    }

    private Fragment generateMakeupOptionFragment() {
        return new BeautyFaceFragment();
    }

    private int getTypeWithPosition(int position) {
        return (position + 1) << OFFSET;
    }


    /** {zh} 
     * @param item EffectButtonItem
     * @brief 根据 EffectButtonItem 更新滑杆
     */
    /** {en} 
     * @param item EffectButtonItem
     * @brief Update sliders by EffectButtonItem
     */

    private void updateProgressWithItem(EffectButtonItem item) {
        pb.setNegativeable(item.isEnableNegative());

        float[] validIntensity = item.getValidIntensity();
        if (validIntensity != null && validIntensity.length > 0) {
                pb.setProgress(validIntensity[0]);


        } else {
            pb.setProgress(0.f);
        }
        if (useProgressBar) {
            if (item.getId() == TYPE_MAKEUP_HAIR) {
                pb.setVisibility(View.INVISIBLE);
            } else {
                pb.setVisibility(View.VISIBLE);

            }
        }
    }

    // 设定BoardFragment高度
    // height unit: px, duration unit: ms
    protected float setBoardFragmentHeight(float height, int duration){
        float pbHeight = getResources().getDimension(R.dimen.height_progress_bar);
        float diffHeight = super.setBoardFragmentHeight(height + pbHeight,duration);
        mEffectCallback.setImgCompareHeightBy(diffHeight,duration);

        return diffHeight;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.iv_close_makeup_option) {
            showOrHideOptionFragment(false, null);
        }
    }

    @Override
    public void onViewPagerSelected(int position) {
        int stickerType = getTypeWithPosition(position);
        mIsFilter = stickerType == TYPE_FILTER;
        if (mIsFilter) {
            pb.setProgress(mSavedFilterIntensity);
            pb.setVisibility(View.VISIBLE);
        } else {
            mCurrentItem = mEffectDataManager.getItem(stickerType);
            LogUtils.d("update mCurrentItem = "+ mCurrentItem);

            updateProgressWithItem(mCurrentItem);
        }

        TabItem tabItem = mTabList.get(getSelectedTabId());

        if ( ((tabItem.id&MASK) == TYPE_HAIR_DYE) ) {
            if ((tabItem.id != TYPE_HAIR_DYE_HIGHLIGHT)) {
                showOrHideColorPanel(false,null);
            } else {
                TryOnFragment fragment = ((TryOnFragment)getFragment(getTabSelectedPosition()));
                EffectButtonItem item = fragment.getAdapter().getSelectItem();
                if ((item != null) && (item.getId() != TYPE_CLOSE) ) {
                    showOrHideColorPanel(true,fragment.getAdapter().getSelectItem().getColorItems());
                }
            }
        }

    }

    @Override
    public void onClickEvent(View view) {
         if (mEffectCallback == null) {
            LogUtils.e("mEffectCallback == null!!");

            return;
        }
        mEffectCallback.onClickEvent(view);
    }

    @Override
    public void setData() {

    }


    public static class TabItem {
        public int id;
        public int title;

        public TabItem(int id, int title) {
            this.id = id;
            this.title = title;
        }
    }

    public interface IEffectCallback {


        /** {zh}
         * @param nodes 字符串数组，存储所有设置的美颜内容，当 node 长度为 0 时意为关闭美妆
         * @param tags  与 nodes 一一对应
         * @brief 更新特效
         */
        /** {en}
         * @param nodes  character string array, stores all settings of beauty content, when the node length is 0, it means to turn off beauty makeup
         * @param tags   one-to-one correspondence with nodes
         * @brief  update special effects
         */

        void updateComposeNodes(Set<EffectButtonItem> effectButtonItems);

        /** {zh} 
         * @param effectButtonItem  特效名称
         * @brief 更新特效强度
         */
        /** {en} 
         * @param effectButtonItem   Effect name
         * @brief  Update effect strength
         */

        void updateComposerNodeIntensity(EffectButtonItem effectButtonItem);

        /** {zh} 
         * @param filter 滤镜名称
         * @brief 更新滤镜
         */
        /** {en} 
         * @param filter  Filter name
         * @brief  Update filter
         */

        void onFilterSelected(String filter);

        /** {zh} 
         * @param cur 强度值，0-1
         * @brief 更新滤镜强度
         */
        /** {en} 
         * @param cur  strength value, 0-1
         * @brief  update filter strength
         */

        void onFilterValueChanged(float cur);

        /** {zh} 
         * 回调Fragment内部点击事件
         */
        /** {en} 
         * Callback Fragment Internal Click Event
         */

        void onClickEvent(View view);

        /** {zh} 
         * 获取当前选中的EffectType
         *
         * @return
         */
        /** {en} 
         * Get the currently selected EffectType
         *
         * @return
         */

        EffectType getEffectType();

        /** {zh} 
         * 显示tip
         */
        /** {en} 
         * Display tips
         */

        void showTip(String title, String desc);

        /** {zh}
         * 调节对比按钮的高度
         */
        /** {en}
         * Display tips
         */
        void setImgCompareHeightBy(float y, int duration);

        /** {zh}
         * @param part 设置的挑染颜色
         * @param colorItem 设置的挑染部位
         * @brief 更新挑染设置
         */
        /** {en}
         * @param part selected hair dye part
         * @param colorItem  selected hair dye color
         * @brief  update hair dye color
         */
        void onHairDyeSelected(int part, ColorItem colorItem);

    }
}
