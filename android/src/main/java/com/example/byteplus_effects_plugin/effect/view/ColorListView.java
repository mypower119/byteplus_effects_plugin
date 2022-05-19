package com.example.byteplus_effects_plugin.effect.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.effect.adapter.ColorListAdapter;
import com.example.byteplus_effects_plugin.effect.model.ColorItem;

import java.util.List;

public class ColorListView extends FrameLayout {
    private List<ColorItem> mColors;

    private RecyclerView mRecyclerView;

    private ColorListAdapter mAdapter;


    private ColorSelectCallback mCallback;
    public interface ColorSelectCallback{
        public void onColorSelected(final int index);

    }


    public ColorListView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ColorListView(@NonNull  Context context, @Nullable  AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ColorListView(@NonNull  Context context, @Nullable  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ColorListView(@NonNull  Context context, @Nullable  AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_color_list, this, true);
        mRecyclerView = view.findViewById(R.id.recycler_colors);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new ColorListAdapter(context);
        mRecyclerView.setAdapter(mAdapter);
    }


    public void updateColors(List<ColorItem> colors, ColorSelectCallback listener){
        if(mColors == colors)return;
        mColors = colors;
        mCallback = listener;
        mAdapter.setData(colors, new ColorListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object item, int position) {
                mCallback.onColorSelected(position);
            }
        });

    }

    public void setSelect(int select) {
        mAdapter.setSelect(select);
    }

}
