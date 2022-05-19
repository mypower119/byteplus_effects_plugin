package com.example.byteplus_effects_plugin.effect.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.byteplus_effects_plugin.common.adapter.ItemViewRVAdapter;
import com.example.byteplus_effects_plugin.common.utils.DensityUtils;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.effect.model.SelectUploadItem;

import java.util.List;

public class SelectUploadRVAdapter extends ItemViewRVAdapter<SelectUploadItem,SelectUploadRVAdapter.ViewHolder> {


    public SelectUploadRVAdapter(List<SelectUploadItem> itemList, ItemViewRVAdapter.OnItemClickListener<SelectUploadItem> listener) {
        super(itemList, listener);
    }

    @Override
    public ViewHolder onCreateViewHolderInternal(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_upload_select, parent, false)
        );
    }

    @Override
    public void onBindViewHolderInternal(ViewHolder holder, int position, SelectUploadItem item) {
        if (item.getPath() != null) {
            holder.setIcon(item.getPath());
        } else {
            holder.setIcon(item.getIcon());
        }
    }

    @Override
    public void changeItemSelectRecord(SelectUploadItem item, int position) {

    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iv;
        ViewHolder(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv_select_upload_item);
        }

        public void setIcon (int iconID) {
            Glide.with(iv).load(iconID).apply(RequestOptions.bitmapTransform(new RoundedCorners((int) DensityUtils.dp2px(itemView.getContext(),3)))).into(iv);
        }

        public void setIcon (String iconUri) {
            Glide.with(iv).load(iconUri).apply(RequestOptions.bitmapTransform(new RoundedCorners((int) DensityUtils.dp2px(itemView.getContext(),3)))).into(iv);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(SelectUploadItem item, int position);
    }
}
