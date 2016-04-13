package io.github.lijunguan.album.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import io.github.lijunguan.album.R;
import io.github.lijunguan.album.entity.ImageInfo;
import io.github.lijunguan.album.utils.KLog;
import io.github.lijunguan.album.view.AlbumView;


/**
 * Created by lijunguan on 2016/4/11
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 */
public class ImageGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int NORMAL_ITEM = 0;
    public static final int CAMERA_ITEM = 1;


    private List<ImageInfo> mData = Collections.emptyList();

    private int mSpanCount;

    private AlbumView mAlbumView;

    private Context mContext;
    /**
     * 允许选择的最大个数
     */
    private int mMaxCount;


    /**
     * 已选择的个数
     */
    private int mSelectedCount = 0;

    public ImageGridAdapter(AlbumView albumView, int maxCount) {
        mAlbumView = albumView;
        mMaxCount = maxCount;
        if (mAlbumView instanceof Context) {
            mContext = (Context) mAlbumView;
        }
    }

    public void setData(@NonNull List<ImageInfo> data) {
        mData = data;
        notifyDataSetChanged();
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int height = parent.getWidth() / mSpanCount;
        View rootView;
        if (viewType == NORMAL_ITEM) {
            rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_grid, parent, false);
            rootView.getLayoutParams().height = height;
            return new ImageViewHolder(rootView);
        } else {
            rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_camera, parent, false);
            rootView.getLayoutParams().height = height;
            return new RecyclerView.ViewHolder(rootView) {
            };
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (position > 0 && holder instanceof ImageViewHolder) {
            final ImageViewHolder imgHolder = (ImageViewHolder) holder;
            final ImageInfo item = getItem(position);
            //这里使用CheckBox的OnClickListener监听,而不是OnCheckedChangeListener ,当调用CheckBox的CheckBox.setChecked（）
            //方法时又会触发OnCheckedChangeListener监听，加上VieHolder缓存服用， 问题简直不能更多！！！  用OnClickListener巧妙解决
            ((ImageViewHolder) imgHolder).mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v;
                    boolean isChecked = checkBox.isChecked();
                    if (isChecked) {
                        mSelectedCount++;
                    } else {
                        mSelectedCount--;
                    }

                    if (mSelectedCount <= mMaxCount) {
                        ImageInfo imageInfo = getItem(position);
                        imageInfo.setSelected(isChecked);
                        imgHolder.mMaskView.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                        mAlbumView.updateSelectedCount(imageInfo);
                    } else {
                        mSelectedCount--;
                        imgHolder.mCheckBox.setChecked(false);
                        // Toast.makeText(mContext, mContext.getString(R.string.out_of_limit, mMaxCount), Toast.LENGTH_SHORT).show();
                        Snackbar.make(holder.itemView, mContext.getString(R.string.out_of_limit, mMaxCount), Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }

                    KLog.i("ImageViewHolder", "displayName:" + item.getDisplayName() + " postition:" + position);
                    KLog.i("ImageViewHolder", "mSelectedCount:" + mSelectedCount);
                }
            });

            imgHolder.mCheckBox.setChecked(item.isSelected());
            imgHolder.mMaskView.setVisibility(item.isSelected() ? View.VISIBLE : View.GONE);

            Glide.with(mContext)
                    .load(item.getPath())
                    .placeholder(R.drawable.placeholder)
                    .into(imgHolder.mImageView);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    public ImageInfo getItem(int position) {
        return mData.get(position - 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return CAMERA_ITEM;
        } else {
            return NORMAL_ITEM;
        }
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
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
