package io.github.lijunguan.imgselector.cropimage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

import io.github.lijunguan.imgselector.BuildConfig;
import io.github.lijunguan.imgselector.R;
import io.github.lijunguan.imgselector.cropimage.crop.CropView;
import io.github.lijunguan.imgselector.model.entity.ImageInfo;
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

    public static final String ARG_IMAGE_INFO = "imageInfo";

    private ImageInfo mImageInfo;

    private CropView mCropView;

    private CropImageListener mListener;

    public static CropFragment newInstance(ImageInfo imageInfo) {
        checkNotNull(imageInfo);
        CropFragment fragment = new CropFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_IMAGE_INFO, imageInfo);
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
            mImageInfo = getArguments().getParcelable(ARG_IMAGE_INFO);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = getLayoutInflater(savedInstanceState).inflate(R.layout.crop_view, container, false);
        mCropView = (CropView) rootView;
        Glide.with(mContext)
                .load(mImageInfo.getPath())
                .into(mCropView);
        return rootView;
    }

    public void cropImage() {
        final File avatarFile = new File(mContext.getCacheDir(), "avatar.jpg");
        try {
            new CropView.CropRequest(mCropView)
                    .quality(60)
                    .format(Bitmap.CompressFormat.JPEG)
                    .into(avatarFile);
            if (mListener != null) {
                mListener.onCropCompleted(avatarFile.getPath());
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


}
