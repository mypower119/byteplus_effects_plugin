package com.example.byteplus_effects_plugin.effect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.byteplus_effects_plugin.common.adapter.ItemViewRVAdapter;
import com.example.byteplus_effects_plugin.common.view.ButtonViewHolder;
import com.example.byteplus_effects_plugin.effect.model.EffectButtonItem;

import java.util.List;

public class EffectRVAdapter extends ItemViewRVAdapter<EffectButtonItem, ButtonViewHolder>{

    private boolean mShowIndex = false;

    public EffectRVAdapter(List<EffectButtonItem> itemList, OnItemClickListener<EffectButtonItem> listener) {
        super(itemList, listener);
    }

    // 此回调中使用定制ViewHolder的构造函数返回一个实例，inflate时可以使用不同的layout
    @Override
    public ButtonViewHolder onCreateViewHolderInternal(ViewGroup parent, int viewType) {
        return new ButtonViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(com.example.byteplus_effects_plugin.R.layout.view_holder_button, parent, false));
    }

    @Override
    public void onBindViewHolderInternal(ButtonViewHolder holder, int position, EffectButtonItem item) {
        Context context = holder.itemView.getContext();

        holder.setIcon(item.getIcon());
        if (mShowIndex && position > 0){
            holder.setTitle(getIndex(position));
        }else {
            holder.setTitle(context.getString(item.getTitle()));

        }
        holder.setMarqueue(false);

        holder.change(item.shouldHighLight());

        holder.pointChange(item.shouldPointOn());
    }

    // 需要adapter来记录item状态的情形需要实现此方法来更新adapter内的记录
    @Override
    public void changeItemSelectRecord(EffectButtonItem item, int position) {

    }

    private String getIndex(int pos){
        if (pos < 10){
            return "0"+pos;
        }else {
            return Integer.toString(pos);
        }
    }

    public void setShowIndex(boolean showIndex) {
        this.mShowIndex = showIndex;
    }

}
