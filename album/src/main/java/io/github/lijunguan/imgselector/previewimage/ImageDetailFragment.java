package io.github.lijunguan.imgselector.previewimage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import io.github.lijunguan.imgselector.AlbumConfig;
import io.github.lijunguan.imgselector.ImgSelector;
import io.github.lijunguan.imgselector.R;
import io.github.lijunguan.imgselector.base.BaseFragment;
import io.github.lijunguan.imgselector.model.entity.ImageInfo;
import uk.co.senab.photoview.PhotoView;

import static io.github.lijunguan.imgselector.utils.CommonUtils.checkNotNull;

/**
 * Created by lijunguan on 2016/4/22.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 */
public class ImageDetailFragment extends BaseFragment
        implements ImageContract.View {
    public static final String TAG = ImageDetailFragment.class.getSimpleName();

    public static final String ARG_IMAGE_LIST = "imageInfos";

    public static final String ARG_CURRENT_POSITION = "currentPosition";

    private ViewPager mViewPager;

    private CheckBox mCheckBox;

    private List<ImageInfo> mImageInfos;

    private int mCurrentPosition;

    private ImageContract.Presenter mPresenter;

    private AlbumConfig mAlbumConfig;

    private ImageDetailAdapter mPagerAdapter;

    public static ImageDetailFragment newInstance(ArrayList<ImageInfo> imageInfos, int currentPosition, AlbumConfig albumConfig) {
        ImageDetailFragment fragment = new ImageDetailFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_IMAGE_LIST, imageInfos);
        args.putInt(ARG_CURRENT_POSITION, currentPosition);
        args.putParcelable(ImgSelector.ARG_ALBUM_CONFIG, albumConfig);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageInfos = getArguments().getParcelableArrayList(ARG_IMAGE_LIST);
            mCurrentPosition = getArguments().getInt(ARG_CURRENT_POSITION);
            mAlbumConfig = getArguments().getParcelable(ImgSelector.ARG_ALBUM_CONFIG);
        }
        mPresenter = new ImageDetailPresenter(this);
        mPagerAdapter = new ImageDetailAdapter(mImageInfos);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragmetn_image_detail, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        mCheckBox = (CheckBox) rootView.findViewById(R.id.cb_checkbox);
        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageInfo currentItem = mPagerAdapter.getItem(mViewPager.getCurrentItem());
                if (!currentItem.isSelected()) {
                    mPresenter.selectImage(currentItem, mAlbumConfig.getMaxCount(), 0);
                } else {
                    mPresenter.unSelectImage(currentItem);
                }
            }
        });
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mCurrentPosition);
        updateIndicator();
        mViewPager.addOnPageChangeListener(onPageChangeListener);
        return rootView;
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener()

    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            ImageInfo item = mPagerAdapter.getItem(position);
            mCheckBox.setChecked(item.isSelected());
            updateIndicator();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void updateIndicator() {
        String text = String.format("(%1$d/%2$d)", mViewPager.getCurrentItem() + 1, mPagerAdapter.getCount());
        mContext.setToolbarTitle(text);
    }


    @Override
    public void setPresenter(ImageContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void showOutOfRange(int position) {
        String warningMSg = getString(R.string.out_of_limit, mAlbumConfig.getMaxCount());
        showToast(warningMSg);
        mCheckBox.setChecked(false);
    }

    @Override
    public void showSelectedCount(int count) {
        String text;
        if (count > 0) {
            text = getString(R.string.update_count, count, mAlbumConfig.getMaxCount());
            mContext.setSubmitBtnText(text, true);
        } else {
            text = getString(R.string.btn_submit_text);
            mContext.setSubmitBtnText(text, false);
        }
    }

    @Override
    public void showToast(CharSequence message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }


    static class ImageDetailAdapter extends PagerAdapter {

        private List<ImageInfo> mData = new ArrayList<>();

        public ImageDetailAdapter(List<ImageInfo> data) {
            mData = checkNotNull(data);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            Glide.with(container.getContext())
                    .load(mData.get(position).getPath())
                    .into(photoView);
            container.addView(photoView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public ImageInfo getItem(int position) {
            return mData.get(position);
        }
    }
}
