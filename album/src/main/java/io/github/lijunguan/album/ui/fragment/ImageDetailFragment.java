package io.github.lijunguan.album.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.lijunguan.album.R;
import io.github.lijunguan.album.base.BaseFragment;
import io.github.lijunguan.album.model.entity.ImageInfo;

/**
 * Created by lijunguan on 2016/4/22.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 */
public class ImageDetailFragment extends BaseFragment {
    public static final String ARG_IMAGE_INFO = "imageInfo";

    private ViewPager mViewPager;

    private ImageInfo mImageInfo;

    public static ImageDetailFragment newInstance(ImageInfo imageInfo) {
        ImageDetailFragment fragment = new ImageDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_IMAGE_INFO, imageInfo);
        fragment.setArguments(args);
        return fragment;
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
        View rootView = inflater.inflate(R.layout.fragmetn_image_detail, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    static class ImageDetailAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return false;
        }
    }
}
