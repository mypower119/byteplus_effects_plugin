package com.example.byteplus_effects_plugin.lens.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.byteplus_effects_plugin.common.adapter.ItemViewRVAdapter;
import com.example.byteplus_effects_plugin.common.view.ButtonViewHolder;
import com.example.byteplus_effects_plugin.lens.manager.ImageQualityDataManager;

import java.util.List;
import java.util.Set;

/**
 * Created on 2021/5/19 11:52
 */
public class ImageQualityRVAdapter extends ItemViewRVAdapter<ImageQualityDataManager.ImageQualityItem, ButtonViewHolder> {
    private Set<ImageQualityDataManager.ImageQualityItem> mSelectSet;

    public ImageQualityRVAdapter(List<ImageQualityDataManager.ImageQualityItem> itemList,
                                 OnItemClickListener<ImageQualityDataManager.ImageQualityItem> listener,
                                 Set<ImageQualityDataManager.ImageQualityItem> set) {
        super(itemList, listener);
        mSelectSet = set;
    }

    @Override
    public ButtonViewHolder onCreateViewHolderInternal(ViewGroup parent, int viewType) {
        return new ButtonViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(com.example.byteplus_effects_plugin.R.layout.view_holder_button, parent, false));
    }

    @Override
    public void onBindViewHolderInternal(ButtonViewHolder holder, int position, ImageQualityDataManager.ImageQualityItem item) {
        holder.setIcon(item.getIcon());
        holder.setTitle(holder.itemView.getContext().getString(item.getTitle()));
        holder.change(mSelectSet.contains(item));
    }

    @Override
    public void changeItemSelectRecord(ImageQualityDataManager.ImageQualityItem item, int position) {
        if (mSelectSet.contains(item)) {
            mSelectSet.remove(item);
        } else {
            mSelectSet.add(item);
        }
    }

//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        super.onBindViewHolder(holder, position);
//
//        ImageQualityDataManager.ImageQualityItem item = mItemList.get(position);
//        if (item == null) {
//            LogUtils.e("item must not be null");
//            return;
//        };
//        holder.bv.change(mSelectSet.contains(item));
//        holder.bv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mSelectSet.contains(item)) {
//                    mSelectSet.remove(item);
//                } else {
//                    mSelectSet.add(item);
//                }
//                mListener.onItemClick(item, holder.getAdapterPosition());
//            }
//        });
//    }
}
