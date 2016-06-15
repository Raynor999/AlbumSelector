package io.github.lijunguan.imgselector.previewimage.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

import io.github.lijunguan.imgselector.R;
import io.github.lijunguan.imgselector.data.entity.ImageInfo;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static io.github.lijunguan.imgselector.utils.CheckUtils.checkNotNull;

/**
 * Created by lijunguan on 2016/6/15.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 */
public class ImageDetailAdapter extends RecyclingPagerAdapter {

    private static final int HOLDER_TAG = 33554432;

    private List<ImageInfo> mData = new ArrayList<>();

    private final RequestManager mRequestManager;

    private final LayoutInflater mInflater;

    private PhotoViewAttacher.OnViewTapListener mListener;

    public ImageDetailAdapter(Context context, @NonNull List<ImageInfo> data, @NonNull PhotoViewAttacher.OnViewTapListener listener) {
        mData = checkNotNull(data);
        mListener = checkNotNull(listener);
        mRequestManager = Glide.with(context);
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        PreviewViewHolder holder;
        if (convertView != null) {
            holder = (PreviewViewHolder) convertView.getTag(HOLDER_TAG);
        } else {
            convertView = mInflater.inflate(R.layout.item_image_detail, container, false);
            holder = new PreviewViewHolder(convertView,mListener);
            //Glide setTag冲突，
            convertView.setTag(HOLDER_TAG, holder);
        }
        mRequestManager
                .load(mData.get(position).getPath())
                .asBitmap()
                .fitCenter()
                .into(holder.mImageView);
        return convertView;
    }


    @Override
    public int getCount() {
        return mData.size();
    }


    public ImageInfo getItem(int position) {
        return mData.get(position);
    }

    static class PreviewViewHolder {

        PhotoView mImageView;

        public PreviewViewHolder(View view, PhotoViewAttacher.OnViewTapListener listener) {
            mImageView = (PhotoView) view;
            mImageView.setOnViewTapListener(listener);
        }
    }
}