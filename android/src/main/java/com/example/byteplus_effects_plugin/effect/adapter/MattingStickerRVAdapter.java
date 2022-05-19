package com.example.byteplus_effects_plugin.effect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.byteplus_effects_plugin.common.adapter.ItemViewRVAdapter;
import com.example.byteplus_effects_plugin.common.view.ButtonViewHolder;
import com.example.byteplus_effects_plugin.effect.model.EffectButtonItem;

import java.util.List;

public class MattingStickerRVAdapter extends ItemViewRVAdapter<EffectButtonItem, ButtonViewHolder> {

    public MattingStickerRVAdapter(List<EffectButtonItem> itemList, OnItemClickListener<EffectButtonItem> listener) {
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
        holder.setTitle(context.getString(item.getTitle()));

        holder.setMarqueue(false);

        boolean se = item.isSelected();
        holder.change(se);

        holder.pointChange(false);
    }

    @Override
    public void changeItemSelectRecord(EffectButtonItem item, int position) {

    }
}
