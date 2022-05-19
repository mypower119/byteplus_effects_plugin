package com.example.byteplus_effects_plugin.effect.fragment;

import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_CLOSE;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.example.byteplus_effects_plugin.common.adapter.ItemViewRVAdapter;
import com.example.byteplus_effects_plugin.common.fragment.ItemViewPageFragment;
import com.example.byteplus_effects_plugin.common.model.EffectType;
import com.example.byteplus_effects_plugin.effect.adapter.EffectRVAdapter;
import com.example.byteplus_effects_plugin.effect.manager.EffectDataManager;
import com.example.byteplus_effects_plugin.effect.model.EffectButtonItem;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;


public class BeautyFaceFragment extends ItemViewPageFragment<EffectRVAdapter> implements ItemViewRVAdapter.OnItemClickListener<EffectButtonItem>  {
//        Fragment
//        implements ItemViewRVAdapter.OnItemClickListener<EffectButtonItem> {
//    private RecyclerView rv;
    private EffectButtonItem mItemGroup;
    private Set<EffectButtonItem> mSelectNodes;
    private IBeautyCallBack mCallback;
//    private EffectRVAdapter mAdapter;

    public BeautyFaceFragment setBeautyCallBack(IBeautyCallBack mCallback) {
        this.mCallback = mCallback;
        return this;
    }

    public interface IBeautyCallBack {
        void onEffectItemClick(EffectButtonItem item);

        void onEffectItemClose(EffectButtonItem item);

        EffectType getEffectType();
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {

        if (getAdapter() == null) {
            setAdapter(new EffectRVAdapter(items(), this));
        } else {
            getAdapter().setItemList(items());
        }

        if (mItemGroup != null && mItemGroup.getParent() != null) {
            getAdapter().setShowIndex(false);
        }
        refreshUI();

        super.onViewCreated(view, savedInstanceState);
    }


    public BeautyFaceFragment setData(EffectButtonItem item) {
        mItemGroup = item;
        // refresh ui if view loaded
        if (getRecyclerView() != null && getAdapter() != null){
            getRecyclerView().scrollToPosition(0);
            if (mItemGroup != null && mItemGroup.getParent() != null) {
                ((EffectRVAdapter)getAdapter()).setShowIndex(false);
            }
            ((EffectRVAdapter)getAdapter()).setItemList(items());
        }
        return this;
    }


    /** {zh} 
     * 绑定外层对象
     * @param selectNode
     * @return
     */
    /** {en} 
     * Binding outer object
     * @param selectNode
     * @return
     */
    public BeautyFaceFragment setSelectNodes(Set<EffectButtonItem> selectNode) {
        mSelectNodes = selectNode;
        return this;
    }


    @Override
    public void onItemClick(EffectButtonItem item, int position) {
        if (item.getId() == TYPE_CLOSE) {
            removeOrAddItem(mSelectNodes, item.getParent(), false);
        } else {
            if (!mSelectNodes.contains(item) && !item.hasChildren()) {
                float[] itemIntensity = null;
                if (!mItemGroup.isEnableMultiSelect()) {
                    if (mItemGroup.getSelectChild() != null ) {
                        EffectButtonItem itemToRemove = mItemGroup.getSelectChild();

                        if (mItemGroup.isReuseChildrenIntensity() && itemToRemove.getId() != TYPE_CLOSE) {
                            itemIntensity = itemToRemove.getIntensityArray().clone();
                        }

                        removeOrAddItem(mSelectNodes, itemToRemove, false);
                    }
                }

                if (item.getNode() != null) {
                    if (itemIntensity == null) {
                        itemIntensity = EffectDataManager.getDefaultIntensity(item.getId(), mCallback.getEffectType(), item.isEnableNegative());
                    }

                    if (itemIntensity != null && item.getIntensityArray() != null) {
                        for (int i = 0; i < itemIntensity.length && i < item.getIntensityArray().length; i++) {
                            item.getIntensityArray()[i] = itemIntensity[i];
                        }
                    }

                    removeOrAddItem(mSelectNodes, item, true);
                }
            }

        }
        mItemGroup.setSelectChild(item);
        mCallback.onEffectItemClick(item);
        refreshUI();
    }

//    public void refreshUI() {
//        if (rv == null) return;
//        RecyclerView.Adapter adapter = rv.getAdapter();
//        if (adapter != null) {
//            adapter.notifyDataSetChanged();
//        }
//    }


    private List<EffectButtonItem> items() {
        if (mItemGroup.hasChildren()) {
            return Arrays.asList(mItemGroup.getChildren());
        }
        return Collections.singletonList(mItemGroup);
    }

    /** {zh} 
     * 删除或者添加选中的小项，注意删除时，需要同时将小项的强度置为0
     * @param set
     * @param item
     * @param add
     */
    /** {en} 
     * Delete or add the selected item. Note that when deleting, you need to set the intensity of the item to 0 at the same time
     * @param set
     * @param item
     * @param add
     */
    private void removeOrAddItem(Set<EffectButtonItem> set, EffectButtonItem item, boolean add) {
        if (add) {
            if (item.getAvailableItem() != null) {
                set.add(item);
                item.setSelected(true);

            }
        } else {
            item.setSelectChild(null);
            set.remove(item);
            item.setSelected(false);
            mCallback.onEffectItemClose(item);
            if (item.hasChildren()) {
                for (EffectButtonItem child : item.getChildren()) {
                    removeOrAddItem(set, child, false);
                }
            }
        }
    }


}
