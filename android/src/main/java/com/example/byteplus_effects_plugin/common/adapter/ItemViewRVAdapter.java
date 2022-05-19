package com.example.byteplus_effects_plugin.common.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.byteplus_effects_plugin.common.model.ButtonItem;
import com.example.byteplus_effects_plugin.common.utils.CommonUtils;
import com.example.byteplus_effects_plugin.core.util.LogUtils;

import java.util.List;

public abstract class ItemViewRVAdapter<T extends ButtonItem, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected List<T> mItemList;
    protected OnItemClickListener<T> mListener;

    public ItemViewRVAdapter(List<T> itemList, OnItemClickListener<T> listener) {
        mItemList = itemList;
        mListener = listener;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateViewHolderInternal(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        final T item = mItemList.get(position);
        if (item == null) return;
        onBindViewHolderInternal(holder, position, item);
        holder.itemView.setOnClickListener(v -> {
            if (CommonUtils.isFastClick()) {
                LogUtils.e("too fast click");
                return;
            }
            changeItemSelectRecord(item, position);
            mListener.onItemClick(item, position);
        });
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    public void setItemList(List<T> itemList) {
        this.mItemList = itemList;
        notifyDataSetChanged();
    }

    public List<T> getItemList(){
        return  mItemList;
    }

    public abstract VH onCreateViewHolderInternal(ViewGroup parent, int viewType);

    public abstract void onBindViewHolderInternal(VH holder, int position, T item);

    public abstract void changeItemSelectRecord(T item, int position);

    public interface OnItemClickListener<T extends ButtonItem> {
        void onItemClick(T item, int position);
    }

}
