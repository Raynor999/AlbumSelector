package io.github.lijunguan.imgselector.album.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;

import java.util.Collections;
import java.util.List;

import io.github.lijunguan.imgselector.AlbumConfig;
import io.github.lijunguan.imgselector.ImageSelector;
import io.github.lijunguan.imgselector.R;
import io.github.lijunguan.imgselector.album.AlbumFragment;
import io.github.lijunguan.imgselector.model.entity.ImageInfo;

import static io.github.lijunguan.imgselector.utils.CommonUtils.checkNotNull;


/**
 * Created by lijunguan on 2016/4/11
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 */
public class ImageGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int NORMAL_ITEM = 0;
    public static final int CAMERA_ITEM = 1;

    private List<ImageInfo> mData = Collections.emptyList();

    private final RequestManager mRequestManager;

    private AlbumConfig mAlbumConfig;

    private AlbumFragment.ImageItemListener mListener;

    public ImageGridAdapter(RequestManager requestManager, AlbumConfig albumConfig, AlbumFragment.ImageItemListener listener) {
        mRequestManager = checkNotNull(requestManager);
        mAlbumConfig = checkNotNull(albumConfig);
        mListener = listener;
    }


    public void replaceData(List<ImageInfo> data) {
        mData = checkNotNull(data);
        notifyDataSetChanged();
    }


    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int itemWidth = parent.getWidth() / mAlbumConfig.getGridColumns();

        View rootView;
        if (viewType == NORMAL_ITEM) {
            rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_grid, parent, false);
            rootView.getLayoutParams().height = itemWidth; //构建正方形Item布局
            return new ImageViewHolder(rootView);
        } else {
            //inflate 拍照 item
            rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_camera, parent, false);
            rootView.getLayoutParams().height = itemWidth;
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //调用系统相机，拍照
                    mListener.onCameraItemClick();
                }
            });
            return new RecyclerView.ViewHolder(rootView) {
            };
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (isNormalItem(position)) {
            final ImageViewHolder imgHolder = (ImageViewHolder) holder;
            final ImageInfo imageInfo = getItem(position);

            if (mAlbumConfig.getSelectModel() == ImageSelector.MULTI_MODE) {
                //这里使用CheckBox的OnClickListener监听,而不是OnCheckedChangeListener ,当调用CheckBox的CheckBox.setChecked（）
                //方法时又会触发OnCheckedChangeListener监听，加上VieHolder缓存服用， 问题简直不能更多！！！  用OnClickListener巧妙解决
                imgHolder.mCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!imageInfo.isSelected()) {
                            mListener.onSelectedImageClick(imageInfo, mAlbumConfig.getMaxCount(), imgHolder.getAdapterPosition());
                            imgHolder.mMaskView.setVisibility(View.VISIBLE);
                        } else {
                            mListener.onUnSelectedImageClick(imageInfo, imgHolder.getAdapterPosition());
                            imgHolder.mMaskView.setVisibility(View.GONE);
                        }
                    }
                });
                imgHolder.mCheckBox.setChecked(imageInfo.isSelected());
                imgHolder.mMaskView.setVisibility(imageInfo.isSelected() ? View.VISIBLE : View.GONE);

            } else if (mAlbumConfig.getSelectModel() == ImageSelector.AVATOR_MODE) {
                imgHolder.mCheckBox.setVisibility(View.GONE);
            }

            imgHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int realPositon = holder.getAdapterPosition();
                    //得到图片在集合中真正的 position
                    if (mAlbumConfig.isShownCamera()) {
                        realPositon--;
                    }
                    mListener.onImageClick(realPositon, imageInfo, mAlbumConfig.getSelectModel());
                }
            });

            mRequestManager
                    .load(imageInfo.getPath())
                    .asBitmap()
                    .placeholder(R.drawable.placeholder)
                    .into(imgHolder.mImageView);


        }

    }

    private boolean isNormalItem(int position) {
        return !mAlbumConfig.isShownCamera() || position > 0;
    }

    @Override
    public int getItemCount() {
        return mAlbumConfig.isShownCamera() ? mData.size() + 1 : mData.size();
    }

    public ImageInfo getItem(int position) {
        return mAlbumConfig.isShownCamera() ? mData.get(position - 1) : mData.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (mAlbumConfig.isShownCamera()) {
            if (position == 0)
                return CAMERA_ITEM;
            return NORMAL_ITEM;
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
