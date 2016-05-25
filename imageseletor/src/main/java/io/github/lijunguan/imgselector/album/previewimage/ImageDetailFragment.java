package io.github.lijunguan.imgselector.album.previewimage;

import android.graphics.Bitmap;
import android.graphics.Paint;
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
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.util.ArrayList;
import java.util.List;

import io.github.lijunguan.imgselector.AlbumConfig;
import io.github.lijunguan.imgselector.ImageSelector;
import io.github.lijunguan.imgselector.R;
import io.github.lijunguan.imgselector.base.BaseFragment;
import io.github.lijunguan.imgselector.model.entity.ImageInfo;
import io.github.lijunguan.imgselector.utils.KLog;
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

    public static ImageDetailFragment newInstance(ArrayList<ImageInfo> imageInfos, int currentPosition) {
        ImageDetailFragment fragment = new ImageDetailFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_IMAGE_LIST, imageInfos);
        args.putInt(ARG_CURRENT_POSITION, currentPosition);
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
            ImageSelector.getInstance().setConfig(mAlbumConfig);
        } else {
            mAlbumConfig = ImageSelector.getInstance().getConfig();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //保存配置参数,防止Application被kill后，恢复Fragment时，配置参数发送异常
        outState.putParcelable(ImageSelector.ARG_ALBUM_CONFIG, mAlbumConfig);
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
                int position = mViewPager.getCurrentItem();
                ImageInfo currentItem = mPagerAdapter.getItem(position);
                if (!currentItem.isSelected()) {
                    mPresenter.selectImage(currentItem, mAlbumConfig.getMaxCount(), position);
                } else {
                    mPresenter.unSelectImage(currentItem, position);
                }
            }
        });
        mPagerAdapter = new ImageDetailAdapter(Glide.with(mContext), mImageInfos);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mCurrentPosition);
        mViewPager.addOnPageChangeListener(onPageChangeListener);
        mCheckBox.setChecked(mImageInfos.get(mCurrentPosition).isSelected());
        updateIndicator();
        return rootView;
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
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }


    static class ImageDetailAdapter extends PagerAdapter {

        private List<ImageInfo> mData = new ArrayList<>();

        private final RequestManager mRequestManager;


        public ImageDetailAdapter(RequestManager requestManager, List<ImageInfo> data) {
            mData = checkNotNull(data);
            mRequestManager = checkNotNull(requestManager);
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
            PhotoView photoView = (PhotoView) LayoutInflater.from(container.getContext())
                    .inflate(R.layout.item_image_detail, container, false);
            mRequestManager
                    .load(mData.get(position).getPath())
                    .asBitmap()
                    .transform(new MyFitCenter(Glide.get(container.getContext()).getBitmapPool()))
//                    .centerCrop()
//                    .thumbnail(0.2f)
                    .into(photoView);
            container.addView(photoView);
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


    static class MyFitCenter extends BitmapTransformation {


        public MyFitCenter(BitmapPool bitmapPool) {
            super(bitmapPool);
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {

            if (toTransform.getWidth() == outWidth && toTransform.getHeight() == outHeight) {

                KLog.v(TAG, "requested target size matches input, returning input");

                return toTransform;
            }
            final float widthPercentage = outWidth / (float) toTransform.getWidth();
            final float heightPercentage = outHeight / (float) toTransform.getHeight();
            final float minPercentage = Math.min(widthPercentage, heightPercentage);

            // take the floor of the target outWidth/outHeight, not round. If the matrix
            // passed into drawBitmap rounds differently, we want to slightly
            // overdraw, not underdraw, to avoid artifacts from bitmap reuse.
            final int targetWidth = (int) (minPercentage * toTransform.getWidth());
            final int targetHeight = (int) (minPercentage * toTransform.getHeight());

            if (toTransform.getWidth() == targetWidth && toTransform.getHeight() == targetHeight) {

                KLog.v(TAG, "adjusted target size matches input, returning input");

                return toTransform;
            }

//            Bitmap.Config config = getSafeConfig(toTransform);
//            Bitmap toReuse = pool.get(targetWidth, targetHeight, config);
//            if (toReuse == null) {
//                toReuse = Bitmap.createBitmap(targetWidth, targetHeight, config);
//            }
//            // We don't add or remove alpha, so keep the alpha setting of the Bitmap we were given.
//            TransformationUtils.setAlpha(toTransform, toReuse);
//
//
//            KLog.v(TAG, "request: " + outWidth + "x" + outHeight);
//            KLog.v(TAG, "toTransform:   " + toTransform.getWidth() + "x" + toTransform.getHeight());
//            KLog.v(TAG, "toReuse: " + toReuse.getWidth() + "x" + toReuse.getHeight());
//            KLog.v(TAG, "minPct:   " + minPercentage);
//
//
//            Canvas canvas = new Canvas(toReuse);
//            Matrix matrix = new Matrix();
//            matrix.setScale(minPercentage, minPercentage);
//            Paint paint = new Paint(PAINT_FLAGS);
//            canvas.drawBitmap(toTransform, matrix, paint);

            return  Bitmap.createScaledBitmap(
                    toTransform,
                    targetWidth,
                    targetHeight,
                    true);
        }

        public static final int PAINT_FLAGS = Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG;

        @Override
        public String getId() {
            return "MyFitCenter.com.bumptech.glide.load.resource.bitmap";
        }

        private Bitmap.Config getSafeConfig(Bitmap bitmap) {
            return bitmap.getConfig() != null ? bitmap.getConfig() : Bitmap.Config.ARGB_8888;
        }
    }
}
