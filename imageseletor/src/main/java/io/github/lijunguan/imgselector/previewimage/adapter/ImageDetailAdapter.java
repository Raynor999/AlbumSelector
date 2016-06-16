package io.github.lijunguan.imgselector.previewimage.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
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
public class ImageDetailAdapter extends PagerAdapter {

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
    public final Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = (PhotoView) mInflater.inflate(R.layout.item_image_detail, container, false);
        photoView.setOnViewTapListener(mListener);
        mRequestManager
                .load(mData.get(position).getPath())
                .asBitmap()
                .fitCenter()
                .into(photoView);
        container.addView(photoView);
        return photoView;
    }

    @Override
    public final void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    public ImageInfo getItem(int position) {
        return mData.get(position);
    }

    static class PreviewViewHolder {

        PhotoView mImageView;

        public PreviewViewHolder(View view) {
            mImageView = (PhotoView) view;

        }
    }
}