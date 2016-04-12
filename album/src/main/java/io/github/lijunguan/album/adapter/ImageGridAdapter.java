package io.github.lijunguan.album.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import io.github.lijunguan.album.R;
import io.github.lijunguan.album.entity.ImageInfo;



/**
 * Created by lijunguan on 2016/4/11
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 */
public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.ImageViewHolder> {

    private List<ImageInfo> mData;

    private Context mContext;

    private int mSpanCount;

    public ImageGridAdapter(Context context, @NonNull List<ImageInfo> data) {
        this.mData = data;
        mContext = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            mSpanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        }
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int height = parent.getWidth() / mSpanCount;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_grid, parent, false);
        view.getLayoutParams().height = height;
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

        final ImageInfo item = getItem(position);
        if (item.isSelected()) {
            holder.mCheckBox.setChecked(true);
            holder.mMaskView.setVisibility(View.VISIBLE);
        } else {
            holder.mCheckBox.setChecked(false);
            holder.mMaskView.setVisibility(View.GONE);
        }
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setSelected(isChecked);
            }
        });
        Glide.with(mContext)
                .load(item.getPath())
                .placeholder(R.drawable.placeholder)
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public ImageInfo getItem(int position) {
        return mData.get(position);
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        View mMaskView;
        CheckBox mCheckBox;

        public ImageViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_image);
            mMaskView = itemView.findViewById(R.id.mask);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.cb_checkbox);

        }
    }

}
