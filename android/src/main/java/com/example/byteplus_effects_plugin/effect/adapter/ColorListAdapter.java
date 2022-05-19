package com.example.byteplus_effects_plugin.effect.adapter;

import android.content.Context;
import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.byteplus_effects_plugin.common.utils.CommonUtils;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.effect.model.ColorItem;
import com.example.byteplus_effects_plugin.effect.view.ColorCircleView;

import java.util.List;

public  class ColorListAdapter extends  RecyclerView.Adapter<ColorListAdapter.ViewHolder>{
    protected   List<ColorItem> mItemList;

    protected   OnItemClickListener mListener;


    protected int mSelect = 0;

    public void setSelect(int select) {
        if (mSelect != select) {
            int oldSelect = mSelect;
            mSelect = select;
            notifyItemChanged(oldSelect);
            notifyItemChanged(select);
        }
    }

    public int getSelect() {
        return mSelect;
    }
    public ColorListAdapter(Context mContext) {
    }

    public ColorListAdapter(Context context, List list, OnItemClickListener listener) {
    }

    public void setData( List<ColorItem> list, OnItemClickListener listener){
        mItemList = list;
        mListener = listener;
        notifyDataSetChanged();
    }







    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_color_select, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ColorItem item = mItemList.get(position);

        onBindViewHolderInternal(holder, mSelect == position, item);

        holder.itemView.setOnClickListener(v -> {
            if (CommonUtils.isFastClick()) {
                LogUtils.e("too fast click");
                return;
            }
            setSelect(position);
            mListener.onItemClick(item, position);
        });
    }

    @Override
    public int getItemCount() {
        return mItemList==null?0:mItemList.size();    }

    protected void onBindViewHolderInternal(ViewHolder holder, boolean select, ColorItem color) {

        holder.colorCircleView.setSelected(select);
        holder.colorCircleView.setmInnerColor(color);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll;
        ColorCircleView colorCircleView;

        ViewHolder(View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.ll_item);
            colorCircleView = itemView.findViewById(R.id.iv_item);
        }
    }

    public interface OnItemClickListener<T> {
        void onItemClick(T item, int position);
    }

}
