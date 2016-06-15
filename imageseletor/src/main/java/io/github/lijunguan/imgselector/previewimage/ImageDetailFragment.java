package io.github.lijunguan.imgselector.previewimage;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.List;

import io.github.lijunguan.imgselector.AlbumConfig;
import io.github.lijunguan.imgselector.ImageSelector;
import io.github.lijunguan.imgselector.R;
import io.github.lijunguan.imgselector.base.BaseFragment;
import io.github.lijunguan.imgselector.data.entity.ImageInfo;
import io.github.lijunguan.imgselector.previewimage.adapter.ImageDetailAdapter;
import uk.co.senab.photoview.PhotoViewAttacher;

import static io.github.lijunguan.imgselector.utils.CheckUtils.checkNotNull;

/**
 * Created by lijunguan on 2016/4/22.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 */
public class ImageDetailFragment extends BaseFragment
        implements ImageContract.View ,
        PhotoViewAttacher.OnViewTapListener{
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

    private View mContentView;

    public static ImageDetailFragment newInstance(@NonNull Bundle args) {
        checkNotNull(args);
        ImageDetailFragment fragment = new ImageDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageInfos = getArguments().getParcelableArrayList(ARG_IMAGE_LIST);
            mCurrentPosition = getArguments().getInt(ARG_CURRENT_POSITION);
        }
        if (savedInstanceState != null) {
            //当Application被kill,复原Fragment时，得到原本配置信息
            mAlbumConfig = savedInstanceState.getParcelable(ImageSelector.ARG_ALBUM_CONFIG);
            mCurrentPosition = savedInstanceState.getInt(ARG_CURRENT_POSITION);
            ImageSelector.getInstance().setConfig(mAlbumConfig);
        } else {
            mAlbumConfig = ImageSelector.getInstance().getConfig();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //保存配置参数,防止Application被kill后，恢复Fragment时，配置参数发送异常
        outState.putParcelable(ImageSelector.ARG_ALBUM_CONFIG, mAlbumConfig);

        outState.putInt(ARG_CURRENT_POSITION,mCurrentPosition);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragmetn_image_detail, container, false);
        initViews(mContentView);
        updateIndicator();
        return mContentView;
    }



    private void initViews(View rootView) {
        mViewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        mCheckBox = (CheckBox) rootView.findViewById(R.id.cb_checkbox);
        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mViewPager.getCurrentItem();
                ImageInfo currentItem = mPagerAdapter.getItem(position);
                if (!currentItem.isSelected()) {
                    mPresenter.selectImage(currentItem, mAlbumConfig.getMaxCount(), position);
                } else {
                    mPresenter.unSelectImage(currentItem, position);
                }
            }
        });
        mPagerAdapter = new ImageDetailAdapter(mContext, mImageInfos,this);
        mViewPager.setAdapter(mPagerAdapter);
        //复原，切换Viewpager到之前选择的图片位置
        mViewPager.setCurrentItem(mCurrentPosition);

        mViewPager.addOnPageChangeListener(onPageChangeListener);
        //初始化mCheckBox状态
        mCheckBox.setChecked(mImageInfos.get(mCurrentPosition).isSelected());

    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            ImageInfo item = mPagerAdapter.getItem(position);
            mCheckBox.setChecked(item.isSelected());
            updateIndicator();
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
        Snackbar.make(mContentView, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onViewTap(View view, float v, float v1) {
        mContext.fullScreenToggle();
    }


}
