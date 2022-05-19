package com.example.byteplus_effects_plugin.effect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.byteplus_effects_plugin.common.adapter.ItemViewRVAdapter;
import com.example.byteplus_effects_plugin.common.model.ButtonItem;
import com.example.byteplus_effects_plugin.common.view.SelectViewHolder;

import java.util.List;

public class SelectRVAdapter<T extends ButtonItem> extends ItemViewRVAdapter<T, SelectViewHolder> {

    protected boolean mShowIndex = false;
    protected int mSelect = 0;

    public SelectRVAdapter(List<T> itemList, OnItemClickListener<T> listener) {
        super(itemList, listener);
    }

    public SelectRVAdapter(List<T> itemList, OnItemClickListener<T> listener, int selectItem) {
        super(itemList, listener);
        mSelect = selectItem;
    }


    @Override
    public SelectViewHolder onCreateViewHolderInternal(ViewGroup parent, int viewType) {
        return new SelectViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(com.example.byteplus_effects_plugin.R.layout.view_holder_select, parent, false));
    }

    @Override
    public void onBindViewHolderInternal(SelectViewHolder holder, int position, T item) {
        Context context = holder.itemView.getContext();

        if (mSelect == position) {
            holder.change(true);
        } else {
            holder.change(false);
        }
        holder.setIcon(item.getIcon());

        if (mShowIndex && position > 0){
            holder.setTitle(getIndex(position));
        }else {
            holder.setTitle(context.getString(item.getTitle()));
        }

    }

    @Override
    public void changeItemSelectRecord(T item, int position) {
        setSelect(position);
    }

    private String getIndex(int pos){
        if (pos < 10){
            return "0"+pos;
        }else {
            return Integer.toString(pos);
        }
    }

    public void setSelect(int select) {
        if (mSelect != select) {
            int oldSelect = mSelect;
            mSelect = select;
            notifyItemChanged(oldSelect);
            notifyItemChanged(select);
        }
    }
}
