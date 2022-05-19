package com.example.byteplus_effects_plugin.algorithm.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.common.adapter.ItemViewRVAdapter;
import com.example.byteplus_effects_plugin.common.view.ButtonViewHolder;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;

import java.util.HashSet;
import java.util.List;

public class AlgorithmRVAdapter extends ItemViewRVAdapter<AlgorithmItem, ButtonViewHolder> {

    private ICheckAvailable mCheckAvailableCallback;
    private HashSet<AlgorithmTaskKey> mSelectSet;

    private boolean isMarqueue = true;

    public AlgorithmRVAdapter(List<AlgorithmItem> itemList, OnItemClickListener<AlgorithmItem> listener) {
        super(itemList, listener);
    }

    public AlgorithmRVAdapter(List<AlgorithmItem> itemList, OnItemClickListener<AlgorithmItem> listener, HashSet<AlgorithmTaskKey> selectSet) {
        super(itemList, listener);
        mSelectSet = selectSet;
    }

    @Override
    public ButtonViewHolder onCreateViewHolderInternal(ViewGroup parent, int viewType) {
        return new ButtonViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(com.example.byteplus_effects_plugin.R.layout.view_holder_button, parent, false));
    }

    @Override
    public void onBindViewHolderInternal(ButtonViewHolder holder, int position, AlgorithmItem item) {
        holder.setIcon(item.getIcon());
        holder.setTitle(holder.itemView.getContext().getString(item.getTitle()));
        holder.setMarqueue(isMarqueue);
        holder.change(mSelectSet.contains(item.getKey()));
    }

    @Override
    public void changeItemSelectRecord(AlgorithmItem item, int position) {
        if (mCheckAvailableCallback != null &&
                !mCheckAvailableCallback.checkAvailable(item)) {
            return;
        }
        if (mSelectSet.contains(item.getKey())) {
            mSelectSet.remove(item.getKey());
        } else {
            mSelectSet.add(item.getKey());
        }
    }

    public interface ICheckAvailable {
        boolean checkAvailable(AlgorithmItem item);
    }

    public void setCheckAvailableCallback(ICheckAvailable check) {
        this.mCheckAvailableCallback = check;
    }

    public void setMarqueue(boolean flag){
        isMarqueue = flag;
        notifyDataSetChanged();
    }
}
