package com.example.byteplus_effects_plugin.effect.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.example.byteplus_effects_plugin.common.adapter.ItemViewRVAdapter;
import com.example.byteplus_effects_plugin.common.fragment.ItemViewPageFragment;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.effect.adapter.StickerRVAdapter;
import com.example.byteplus_effects_plugin.effect.resource.StickerItem;

import java.util.List;

;

public class StickerFragment extends ItemViewPageFragment<StickerRVAdapter>
        implements ItemViewRVAdapter.OnItemClickListener<StickerItem> {
//    private RecyclerView mRvSticker;
    protected int mType;
//    private StickerRVAdapter mAdapter;

    private StickerFragmentCallback mCallback;

    interface StickerFragmentCallback {
        void onItemClick(StickerItem item, int position);
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_sticker, container, false);
//    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setItemSelectedPadding(getResources().getDimensionPixelSize(R.dimen.select_padding));
        super.onViewCreated(view, savedInstanceState);
//        mRvSticker = view.findViewById(R.id.rv_sticker);
//        mRvSticker.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        mRvSticker.setAdapter(mAdapter);
//        mRvSticker.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//                int position = parent.getChildAdapterPosition(view);
//                int totalCount = parent.getAdapter().getItemCount();
//                int space = getResources().getDimensionPixelSize(com.example.byteplus_effects_plugin.R.dimen.item_distance)
//                        - 2*getResources().getDimensionPixelSize(com.example.byteplus_effects_plugin.R.dimen.select_padding);
//                if (position == 0) {//第一个
//                    outRect.left = 0;
//                    outRect.right = space / 2;
//                } else if (position == totalCount - 1){
//                    outRect.left = space / 2;
//                    outRect.right = 0;
//                } else {//中间其它的
//                    outRect.left = space / 2;
//                    outRect.right = space / 2;
//                }
//            }
//        });
    }


//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        int width = DensityUtils.getScreenWidth(getActivity());
//        int itemWidth = getResources().getDimensionPixelSize(com.example.byteplus_effects_plugin.R.dimen.logo_size);
//        int itemDistance = getResources().getDimensionPixelSize(com.example.byteplus_effects_plugin.R.dimen.item_distance);
//        int rvMarginHorizontal = getResources().getDimensionPixelSize(com.example.byteplus_effects_plugin.R.dimen.rv_margin_horizontal);
//        int itemCount = mRvSticker.getAdapter().getItemCount();
//        boolean rvContained = width - 2*rvMarginHorizontal >= itemCount * itemWidth + (itemCount - 1) * itemDistance;
//
//        if(rvContained) {
//            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mRvSticker.getLayoutParams();
//            lp.rightMargin = rvMarginHorizontal;
//            lp.gravity = Gravity.CENTER_HORIZONTAL;
//            mRvSticker.setLayoutParams(lp);
//        }
//    }

    public StickerFragment setCallback(StickerFragmentCallback callback) {
        mCallback = callback;
//        if (mAdapter == null) {
//            mAdapter = new StickerItemRVAdapter(this);
//            if (mRvSticker != null) {
//                mRvSticker.setAdapter(mAdapter);
//            }
//        }
        return this;
    }

    public StickerFragment setData(List<StickerItem> stickerItems) {
        setAdapter(new StickerRVAdapter(stickerItems, this));
//        if (mAdapter == null) {
//            mAdapter = ;
//            if (mRvSticker != null) {
//                mRvSticker.setAdapter(mAdapter);
//            }
//        }
//        mAdapter.setData(stickerItems);
        return this;
    }

    public StickerFragment setType(int type) {
        mType = type;
        return this;
    }

    public void refresh() {
        mAdapter.notifyDataSetChanged();
    }

    public void refreshItem(int index) {
        mAdapter.notifyItemChanged(index);
    }

    @Override
    public void onItemClick(StickerItem item, int position) {
        if (mCallback != null) {
            mCallback.onItemClick(item, position);
        }
    }

    public void setSelected(int pos) {
        if (mAdapter == null) return;
        mAdapter.setSelect(pos);
    }

}
