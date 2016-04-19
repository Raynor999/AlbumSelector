package io.github.lijunguan.album.adapter;

import android.app.Activity;
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
import io.github.lijunguan.album.model.entity.ImageInfo;
import io.github.lijunguan.album.ui.activity.AlbumActivity;
import io.github.lijunguan.album.utils.KLog;
import io.github.lijunguan.album.view.SelectedImgView;


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

    private Context mContext;
    /**
     * 允许选择的最大个数
     */
    private int mMaxCount;

    private int mSelectModel;
    /**
     * 已选择的个数
     */
    private int mSelectedCount = 0;

    public ImageGridAdapter(Activity activity, int maxCount, int selectModel) {
        mContext = activity;
        mMaxCount = maxCount;
        mSelectModel = selectModel;

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
            //inflate 拍照 item
            rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_camera, parent, false);
            rootView.getLayoutParams().height = height;
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof AlbumActivity) {
                        ((AlbumActivity) mContext).showCarmeraAction();
                    }
                }
            });
            return new RecyclerView.ViewHolder(rootView) {
            };
        }
    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder holder, int position) {

        if (position > 0) {
            final ImageViewHolder imgHolder = (ImageViewHolder) holder;
            final ImageInfo item = getItem(position);

            if (mSelectModel == AlbumActivity.MULTI_MODEL) {
                //这里使用CheckBox的OnClickListener监听,而不是OnCheckedChangeListener ,当调用CheckBox的CheckBox.setChecked（）
                //方法时又会触发OnCheckedChangeListener监听，加上VieHolder缓存服用， 问题简直不能更多！！！  用OnClickListener巧妙解决
                imgHolder.mCheckBox.setOnClickListener(new View.OnClickListener() {
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
                            ImageInfo imageInfo = getItem(imgHolder.getAdapterPosition());
                            imageInfo.setSelected(isChecked);
                            imgHolder.mMaskView.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                            if (mContext instanceof SelectedImgView) {
                                ((SelectedImgView) mContext).updateSelectedCount(imageInfo);
                            }

                        } else {
                            mSelectedCount--;
                            imgHolder.mCheckBox.setChecked(false);
                            // Toast.makeText(mContext, mContext.getString(R.string.out_of_limit, mMaxCount), Toast.LENGTH_SHORT).show();
                            Snackbar.make(imgHolder.itemView, mContext.getString(R.string.out_of_limit, mMaxCount), Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        }

                        KLog.i("ImageViewHolder", "displayName:" + item.getDisplayName() + " postition:" + imgHolder.getAdapterPosition());
                        KLog.i("ImageViewHolder", "mSelectedCount:" + mSelectedCount);
                    }
                });

                imgHolder.mCheckBox.setChecked(item.isSelected());
                imgHolder.mMaskView.setVisibility(item.isSelected() ? View.VISIBLE : View.GONE);
                imgHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO 预览图片
                    }
                });
            } else if (mSelectModel == AlbumActivity.SINGLE_MODEL) {
                imgHolder.mCheckBox.setVisibility(View.GONE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO 裁剪图片，设置头像

                    }
                });
            }
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
