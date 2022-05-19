package com.example.byteplus_effects_plugin.effect.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.byteplus_effects_plugin.common.adapter.ItemViewRVAdapter;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.effect.resource.StickerItem;
import com.example.byteplus_effects_plugin.effect.view.DownloadView;
import com.example.byteplus_effects_plugin.effect.view.StickerViewHolder;
import com.example.byteplus_effects_plugin.resource.RemoteResource;

import java.util.List;

public class StickerRVAdapter extends ItemViewRVAdapter<StickerItem, StickerViewHolder> {

    protected int mSelect = 0;

    public StickerRVAdapter(List<StickerItem> itemList, OnItemClickListener<StickerItem> listener) {
        super(itemList, listener);
    }

    @Override
    public StickerViewHolder onCreateViewHolderInternal(ViewGroup parent, int viewType) {
        return new StickerViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_holder_online_item, parent, false));
    }

    @Override
    public void onBindViewHolderInternal(StickerViewHolder holder, int position, StickerItem item) {
        if (mSelect == position) {
            holder.change(true);
        } else {
            holder.change(false);
        }

        if (item.getResource() != null && item.getResource() instanceof RemoteResource) {
            holder.setIcon(item.getIconUrl());

            RemoteResource resource = (RemoteResource) item.getResource();
            switch (resource.getState()) {
                case REMOTE:
                    holder.setState(DownloadView.DownloadState.REMOTE);
                    break;
                case CACHED:
                    holder.setState(DownloadView.DownloadState.CACHED);
                    break;
                case DOWNLOADING:
                    holder.setState(DownloadView.DownloadState.DOWNLOADING);
                    holder.setProgress(resource.getDownloadProgress());
                    break;
                case UNKNOWN:
                    throw new IllegalStateException();
            }
        } else {
            holder.setIcon(item.getIconId());
            holder.setState(DownloadView.DownloadState.CACHED);
        }
        holder.setTitle(item.getTitle(holder.itemView.getContext()));
    }

    @Override
    public void changeItemSelectRecord(StickerItem item, int position) {
        setSelect(position);
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
