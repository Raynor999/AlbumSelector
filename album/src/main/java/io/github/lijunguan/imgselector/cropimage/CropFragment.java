package io.github.lijunguan.imgselector.cropimage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.io.File;
import java.io.IOException;

import io.github.lijunguan.imgselector.BuildConfig;
import io.github.lijunguan.imgselector.R;
import io.github.lijunguan.imgselector.cropimage.crop.CropView;
import io.github.lijunguan.imgselector.utils.FileUtils;
import io.github.lijunguan.imgselector.utils.KLog;

import static io.github.lijunguan.imgselector.utils.CommonUtils.checkNotNull;

/**
 * Created by lijunguan on 2016/4/26.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 */
public class CropFragment extends Fragment {

    private Activity mContext;

    public static final String TAG = CropFragment.class.getSimpleName();

    public static final String ARG_IMAGE_PATH = "imageInfo";

    private String mImagePath;

    private CropView mCropView;

    private CropImageListener mListener;

    public static CropFragment newInstance(String imagePath) {
        checkNotNull(imagePath);
        CropFragment fragment = new CropFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_PATH, imagePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (Activity) context;
        if (context instanceof CropImageListener) {
            mListener = (CropImageListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CropImageListener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImagePath = getArguments().getString(ARG_IMAGE_PATH);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = getLayoutInflater(savedInstanceState).inflate(R.layout.crop_view, container, false);
        mCropView = (CropView) rootView;
        performLoad(); //加载图片
        return rootView;
    }

    void performLoad() {
        //得到图片尺寸，合适的缩放图片大小
        if (mCropView.getWidth() == 0 && mCropView.getHeight() == 0) {
            if (!mCropView.getViewTreeObserver().isAlive()) {
                return;
            }
            mCropView.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @SuppressWarnings("deprecation")
                        @Override
                        public void onGlobalLayout() {
                            if (mCropView.getViewTreeObserver().isAlive()) {
                                mCropView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            }
                            loadImage();
                        }
                    }
            );
            return;
        }
        loadImage();
    }

    private void loadImage() {
        Glide.with(mContext)
                .load(mImagePath)
                .asBitmap()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .transform(new FillViewportTransformation(
                        Glide.get(mContext).getBitmapPool(),
                        mCropView.getViewportWidth(),
                        mCropView.getViewportHeight()))
                .into(mCropView);
    }

    public void cropImage() {

        final File avatorFile = new File(FileUtils.getCacheDirectory(mContext), System.currentTimeMillis() + "avator.jpg");

        try {
            new CropView.CropRequest(mCropView)
                    .quality(80)
                    .format(Bitmap.CompressFormat.JPEG)
                    .into(avatorFile);
            if (mListener != null) {
                //通知Activity裁剪完成
                mListener.onCropCompleted(avatorFile.getPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (BuildConfig.LOG_DEBUG) {
                KLog.e("Error save cropImage file");
            }
        }
    }

    interface CropImageListener {
        void onCropCompleted(String path);
    }

    static class FillViewportTransformation extends BitmapTransformation {

        private final int viewportWidth;
        private final int viewportHeight;

        public FillViewportTransformation(BitmapPool bitmapPool, int viewportWidth, int viewportHeight) {
            super(bitmapPool);
            this.viewportWidth = viewportWidth;
            this.viewportHeight = viewportHeight;
        }

        @Override
        protected Bitmap transform(BitmapPool bitmapPool, Bitmap source, int outWidth, int outHeight) {
            int sourceWidth = source.getWidth();
            int sourceHeight = source.getHeight();

            Rect target = computeTargetSize(sourceWidth, sourceHeight, viewportWidth, viewportHeight);

            int targetWidth = target.width();
            int targetHeight = target.height();

            return Bitmap.createScaledBitmap(
                    source,
                    targetWidth,
                    targetHeight,
                    true);
        }

        @Override
        public String getId() {
            return getClass().getName();
        }

        Rect computeTargetSize(int sourceWidth, int sourceHeight, int viewportWidth, int viewportHeight) {

            if (sourceWidth == viewportWidth && sourceHeight == viewportHeight) {
                return new Rect(0, 0, viewportWidth, viewportHeight); // Fail fast for when source matches exactly on viewport
            }

            float scale;
            if (sourceWidth * viewportHeight > viewportWidth * sourceHeight) {
                scale = (float) viewportHeight / (float) sourceHeight;
            } else {
                scale = (float) viewportWidth / (float) sourceWidth;
            }
            final int recommendedWidth = (int) ((sourceWidth * scale) + 0.5f);
            final int recommendedHeight = (int) ((sourceHeight * scale) + 0.5f);
            return new Rect(0, 0, recommendedWidth, recommendedHeight);
        }

    }
}
