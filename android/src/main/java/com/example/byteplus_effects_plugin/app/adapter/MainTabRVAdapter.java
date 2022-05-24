package com.example.byteplus_effects_plugin.app.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.byteplus_effects_plugin.ByteplusEffectsPlugin;
import com.example.byteplus_effects_plugin.common.utils.CommonUtils;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.app.DemoApplication;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.app.model.FeatureTab;
import com.example.byteplus_effects_plugin.app.model.FeatureTabItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 5/10/21 12:14 PM
 */
public class MainTabRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_TITLE = 1;
    public static final int TYPE_ITEM = 2;
    public static final int TYPE_FOOTER = 3;

    private OnItemClickListener mListener;
    private final List<FeatureTabItem> mData;

    public MainTabRVAdapter(List<FeatureTab> groups) {
        mData = new ArrayList<>();
        for (FeatureTab group : groups) {
            mData.addAll(group.toList());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_TITLE:
                return new TitleViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_main_tab_title, parent, false)
                );
            case TYPE_ITEM:
                return new ItemViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_main_tab_item, parent, false)
                );

            case TYPE_FOOTER:
                return new FooterViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_main_version, parent, false)
                );
        }
        throw new IllegalStateException("viewType must be TYPE_TITLE or TYPE_ITEM");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (position < mData.size()){
            FeatureTabItem item = mData.get(position);
            if (viewType == TYPE_TITLE) {
                TitleViewHolder vh = (TitleViewHolder) holder;
                vh.tv.setText(item.getTitleId());
            } else if (viewType == TYPE_ITEM) {
                ItemViewHolder vh = (ItemViewHolder) holder;
                vh.iv.setImageResource(item.getIconId());
                vh.tv.setText(item.getTitleId());

                vh.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CommonUtils.isFastClick()){
                            LogUtils.e("too fast click");
                            return;
                        }
                        if (mListener == null) {
                            return;
                        }
                        mListener.onItemClick(item);
                    }
                });
            }
        }

        if (viewType == TYPE_FOOTER) {
            FooterViewHolder vh = (FooterViewHolder) holder;
            vh.tv.setText(getVersionName());
        }
    }

    private String getVersionName() {
//        Context context = ByteplusEffectsPlugin.context();
//        try {
//            return "V" + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return "";
//        }
        return ByteplusEffectsPlugin.getVersionName();
    }

    @Override
    public int getItemCount() {
        return mData.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mData.size()){
            FeatureTabItem item = mData.get(position);
            if (item instanceof FeatureTab) {
                return TYPE_TITLE;
            } else {
                return TYPE_ITEM;
            }
        }else {
            return TYPE_FOOTER;
        }

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    static class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_main_tab_title);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView iv;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_main_tab_item);
            iv = itemView.findViewById(R.id.iv_main_tab_item);
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_version);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(FeatureTabItem item);
    }
}
