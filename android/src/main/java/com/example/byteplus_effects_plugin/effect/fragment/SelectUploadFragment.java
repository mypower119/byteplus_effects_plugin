package com.example.byteplus_effects_plugin.effect.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.example.byteplus_effects_plugin.common.adapter.ItemViewRVAdapter;
import com.example.byteplus_effects_plugin.common.fragment.ItemViewPageFragment;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.effect.adapter.SelectUploadRVAdapter;
import com.example.byteplus_effects_plugin.effect.model.SelectUploadItem;

import java.io.Serializable;
import java.util.List;

public class SelectUploadFragment extends ItemViewPageFragment<SelectUploadRVAdapter> implements ItemViewRVAdapter.OnItemClickListener<SelectUploadItem> {

    private static final String ARG_PARAM_BUTTON_ITEM_LIST = "button_item_list";

    private List<SelectUploadItem> mItemList;
    private ISelectUploadCallback mCallback = null;

    public SelectUploadFragment() {
    }

//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return LayoutInflater.from(getContext())
//                .inflate(R.layout.fragment_select_upload, container, false);
//    }

    public static SelectUploadFragment newInstance(List<SelectUploadItem> param1) {
        SelectUploadFragment fragment = new SelectUploadFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_BUTTON_ITEM_LIST, (Serializable) param1);
        fragment.setArguments(args);
        return fragment;
    }

    public SelectUploadFragment setUploadSelectedCallback(ISelectUploadCallback callback){
        mCallback = callback;
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mItemList = (List<SelectUploadItem>) getArguments().getSerializable(ARG_PARAM_BUTTON_ITEM_LIST);
        }
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_select_upload, container, false);
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setAdapter(new SelectUploadRVAdapter(mItemList,this));
        setItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                int totalCount = parent.getAdapter().getItemCount();
                int space = getResources().getDimensionPixelSize(R.dimen.upload_select_distance);
                int margin = getResources().getDimensionPixelSize(R.dimen.upload_select_margin);
                if (position == 0) {//第一个
                    outRect.left = margin;
                    outRect.right = space / 2;
                } else if (position == totalCount - 1){
                    outRect.left = space / 2;
                    outRect.right = margin;
                } else {//中间其它的
                    outRect.left = space / 2;
                    outRect.right = space / 2;
                }
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onItemClick(SelectUploadItem item, int position) {
        if (mCallback == null) {
            return;
        }
        mCallback.onUploadSelected(item,position);
    }

    public interface ISelectUploadCallback{
        void onUploadSelected(SelectUploadItem buttonItem, int position);
    }
}
