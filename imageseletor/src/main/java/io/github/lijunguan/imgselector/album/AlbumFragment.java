package io.github.lijunguan.imgselector.album;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.konifar.fab_transformation.FabTransformation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.lijunguan.imgselector.AlbumConfig;
import io.github.lijunguan.imgselector.ImageSelector;
import io.github.lijunguan.imgselector.R;
import io.github.lijunguan.imgselector.album.adapter.FolderListAdapter;
import io.github.lijunguan.imgselector.album.adapter.ImageGridAdapter;
import io.github.lijunguan.imgselector.album.previewimage.ImageDetailFragment;
import io.github.lijunguan.imgselector.album.previewimage.ImageDetailPresenter;
import io.github.lijunguan.imgselector.album.widget.GridDividerDecorator;
import io.github.lijunguan.imgselector.base.BaseFragment;
import io.github.lijunguan.imgselector.cropimage.CropActivity;
import io.github.lijunguan.imgselector.cropimage.CropFragment;
import io.github.lijunguan.imgselector.model.AlbumRepository;
import io.github.lijunguan.imgselector.model.entity.AlbumFolder;
import io.github.lijunguan.imgselector.model.entity.ImageInfo;
import io.github.lijunguan.imgselector.utils.ActivityUtils;
import io.github.lijunguan.imgselector.utils.FileUtils;

import static io.github.lijunguan.imgselector.utils.CommonUtils.checkNotNull;

/**
 * Created by lijunguan on 2016/4/21.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 */
public class AlbumFragment extends BaseFragment
        implements AlbumContract.View, View.OnClickListener {

    public static final String TAG = AlbumFragment.class.getSimpleName();

    public static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;

    private AlbumContract.Presenter mPresenter;

    private AlbumConfig mAlbumConfig;
    /**
     * 展示图片浓缩图的Grid;
     */
    private RecyclerView mRvImageGrid;

    private ImageGridAdapter mImagesAdapter;
    /**
     * 相册目录列表
     */
    private RecyclerView mRvFolderList;

    private FolderListAdapter mFolderAdapter;
    /**
     * 相册目录列表弹出时的遮罩View
     */
    private View mOverlay;

    public FloatingActionButton mFab;

    private View mEmptyView;
    /**
     * 保存相机拍摄的照片
     */
    private File mTmpFile;
    /**
     * 当前选择的相册目录下的 图片集合
     */
    private ArrayList<ImageInfo> mImages;
    private RequestManager mRequestManager;


    public static AlbumFragment newInstance() {
        return new AlbumFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mAlbumConfig = savedInstanceState.getParcelable(ImageSelector.ARG_ALBUM_CONFIG);
            ImageSelector.getInstance().setConfig(mAlbumConfig);
        } else {
            mAlbumConfig = ImageSelector.getInstance().getConfig();
        }

        //改用接口监听 而不是让Adapter持有Presenter对象，
        // 1.更符合MVP架构 2.解决当程序处于后台，系统因资源不足杀死App后，复原时会先执行Fragment的onCreate()方法
        //再执行 Activity的onCreate()方法，导致mPresenter throw NullPointerException异常
        mRequestManager = Glide.with(this);
        mImagesAdapter = new ImageGridAdapter(mRequestManager, mAlbumConfig, mItemListener);
        mFolderAdapter = new FolderListAdapter(mRequestManager, mFolderItemClickListener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //保存配置参数,防止Application被kill后，恢复Fragment时，配置参数发送异常
        outState.putParcelable(ImageSelector.ARG_ALBUM_CONFIG, mAlbumConfig);
    }

    /**
     * 图片Item点击事件监听
     */
    ImageItemListener mItemListener = new ImageItemListener() {

        @Override
        public void onSelectedImageClick(@NonNull ImageInfo imageInfo, int maxCount, int position) {
            mPresenter.selectImage(imageInfo, maxCount, position);
        }

        @Override
        public void onUnSelectedImageClick(ImageInfo imageInfo, int position) {
            mPresenter.unSelectImage(imageInfo, position);
        }

        @Override
        public void onCameraItemClick() {
            mPresenter.openCamera();
        }

        @Override
        public void onImageClick(int position, ImageInfo imageInfo, int selectModel) {
            //根据选择模式，打开不同的UI
            if (selectModel == ImageSelector.MULTI_MODE) {
                mPresenter.previewImage(position);
            } else if (selectModel == ImageSelector.AVATOR_MODE) {
                mPresenter.cropImage(imageInfo);
            }
        }

    };

    FolderItemListener mFolderItemClickListener = new FolderItemListener() {
        @Override
        public void onFloderItemClick(AlbumFolder folder) {
            mPresenter.swtichFloder(folder);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_album, container, false);
        initViews(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN //READ_EXTERNAL_STORAGE Permission 再 API Level 16 时被添加
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            mContext.requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.permission_read_storage_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            mPresenter.start(); //初始化数据
        }
    }

    /**
     * 当动态权限申请结果的回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_READ_ACCESS_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPresenter.start(); //初始化数据
                } else {
//                    showToast(getString(R.string.permission_denied_error_msg));
                    showEmptyView(getString(R.string.permission_denied_error_msg));
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initViews(View rootView) {
        mRvImageGrid = (RecyclerView) rootView.findViewById(R.id.rv_image_grid);
        mRvFolderList = (RecyclerView) rootView.findViewById(R.id.rv_album_list);
        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mOverlay = rootView.findViewById(R.id.overlay);
        mEmptyView = rootView.findViewById(R.id.rl_no_image);

        initRecyclerView();

        mFab.setOnClickListener(this);
        mOverlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fab) {
            showFolderList();
        } else if (id == R.id.overlay) {
            hideFolderList();
        }
    }

    private void initRecyclerView() {
        mRvFolderList.setHasFixedSize(true);
        mRvFolderList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvFolderList.setAdapter(mFolderAdapter);

        mRvImageGrid.setHasFixedSize(true);
        //给RecclerView设置GridlayoutManager，并根据配置信息，指定列数
        mRvImageGrid.setLayoutManager(new GridLayoutManager(mContext, mAlbumConfig.getGridColumns()));
        mRvImageGrid.addItemDecoration(new GridDividerDecorator(mContext)); //添加divider
        mRvImageGrid.setAdapter(mImagesAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.result(requestCode, resultCode, data, mTmpFile);
    }

    @Override
    public void setPresenter(AlbumContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void showImages(@NonNull List<ImageInfo> imageInfos) {
        mImages = (ArrayList<ImageInfo>) checkNotNull(imageInfos);
        mImagesAdapter.replaceData(imageInfos);
        mEmptyView.setVisibility(View.GONE);
    }

    @Override
    public void showFolderList() {
        FabTransformation.with(mFab)
                .setOverlay(mOverlay)
                .transformTo(mRvFolderList);

    }

    @Override
    public void hideFolderList() {
        FabTransformation.with(mFab)
                .setOverlay(mOverlay)
                .transformFrom(mRvFolderList);
    }

    @Override
    public void showSystemCamera() {
        //        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(mContext.getPackageManager()) != null) {
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            mTmpFile = null;
            try {
                mTmpFile = FileUtils.createTmpFile(mContext);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mTmpFile != null && mTmpFile.exists()) {
                Uri uri = getFileUri(getActivity(), mTmpFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(cameraIntent, ImageSelector.REQUEST_OPEN_CAMERA);
            } else {
                showToast(getString(R.string.img_error));
            }
        } else {
            showToast(getString(R.string.msg_no_camera));
        }
    }

    private Uri getFileUri(@NonNull Context context, @NonNull File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context,
                    context.getApplicationContext().getPackageName() + ".file.provider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    @Override
    public void initFolderList(@NonNull List<AlbumFolder> folders) {
        mFolderAdapter.setData(folders);
    }

    @Override
    public void showOutOfRange(int position) {
        String warningMSg = getString(R.string.out_of_limit, mAlbumConfig.getMaxCount());
        showToast(warningMSg);
        //复原Checkbox的选择状态
        mImagesAdapter.notifyItemChanged(position);
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
    public void showImageDetailUi(int currentPosition) {
        ImageDetailFragment fragment = (ImageDetailFragment) mContext
                .getSupportFragmentManager()
                .findFragmentByTag(ImageDetailFragment.TAG);

        if (fragment == null) {
            fragment = ImageDetailFragment.newInstance(mImages, currentPosition);
            AlbumRepository albumRepository = AlbumRepository.getInstance(mContext);

            new ImageDetailPresenter(
                    albumRepository,
                    fragment,
                    AlbumFragment.this);

            ActivityUtils.addFragmentToActivity(mContext.getSupportFragmentManager(),
                    fragment,
                    ImageDetailFragment.TAG,
                    true); //将ImageDetailFragment 加入返回栈。
        }
        mContext.getSupportFragmentManager()
                .beginTransaction()
                .hide(this)
                .show(fragment)
                .commit();
    }

    @Override
    public void showImageCropUi(@NonNull String imagePath) {
        Intent intent = new Intent(mContext, CropActivity.class);
        intent.putExtra(CropFragment.ARG_IMAGE_PATH, imagePath);
        startActivityForResult(intent, ImageSelector.REQUEST_CROP_IMAGE);
    }

    @Override
    public void selectComplete(List<String> imagePaths, boolean refreshMedia) {
        checkNotNull(imagePaths);
        Intent data = new Intent();
        data.putStringArrayListExtra(ImageSelector.SELECTED_RESULT, (ArrayList<String>) imagePaths);
        // notify system ,保存拍照的照片到MediaStore,
        if (refreshMedia) {
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mTmpFile)));
        }
        mContext.setResult(Activity.RESULT_OK, data);
        mContext.finish();
    }

    @Override
    public void syncCheckboxStatus(int position) {
        // 同步Checkbox 状态
        if (mAlbumConfig.isShownCamera()) {
            position++;
        }
        mImagesAdapter.notifyItemChanged(position);
    }


    @Override
    public void showEmptyView(@Nullable CharSequence message) {
        if (!TextUtils.isEmpty(message)) {
            TextView textView = (TextView) mEmptyView.findViewById(R.id.tv_empty);
            textView.setText(message);
        }
        mEmptyView.setVisibility(View.VISIBLE);
    }


    @Override
    public void showToast(CharSequence message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }


    public interface ImageItemListener {

        void onSelectedImageClick(ImageInfo imageInfo, int maxCount, int position);

        void onUnSelectedImageClick(ImageInfo imageInfo, int position);

        void onCameraItemClick();

        void onImageClick(int realPosition, ImageInfo imageInfo, int slecteModel);
    }

    public interface FolderItemListener {
        void onFloderItemClick(AlbumFolder folder);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            mContext.setToolbarTitle(getString(R.string.album_activity_title));
        }
    }
}
