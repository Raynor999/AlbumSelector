package io.github.lijunguan.album.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.konifar.fab_transformation.FabTransformation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.lijunguan.album.R;
import io.github.lijunguan.album.adapter.FolderListAdapter;
import io.github.lijunguan.album.adapter.ImageGridAdapter;
import io.github.lijunguan.album.base.BaseActivity;
import io.github.lijunguan.album.model.entity.AlbumFloder;
import io.github.lijunguan.album.model.entity.ImageInfo;
import io.github.lijunguan.album.presenter.LoadAlbumPresenerImpl;
import io.github.lijunguan.album.presenter.LoadAlbumPresenter;
import io.github.lijunguan.album.ui.widget.GridDividerDecorator;
import io.github.lijunguan.album.utils.FileUtils;
import io.github.lijunguan.album.utils.KLog;
import io.github.lijunguan.album.view.AlbumView;
import io.github.lijunguan.album.view.SelectedImgView;


public class AlbumActivity extends BaseActivity implements AlbumView, SelectedImgView, View.OnClickListener {
    public static final String TAG = AlbumActivity.class.getSimpleName();
    /**
     * 图片选择模式，默认多选
     */
    public static final String ARG_SELECT_MODEL = "select_model";
    /**
     * 多选模式下，最大选择图片数，默认为9张
     */
    public static final String ARG_SELECT_MAX_COUNT = "select_max_count";
    /**
     * 选择的结果，返回格式为 ArrayList&ltString&gt ; 图片路径集合
     */
    public static final String SELECTED_RESULT = "selected_result";

    /**
     * 单选模式
     */
    public static final int SINGLE_MODEL = 0;
    /**
     * 多选模式
     */
    public static final int MULTI_MODEL = 1;

    public static final int DEFAULT_MAX_COUNT = 9;
    /**
     * grid 的列数
     */
    private int spanCount = 3;


    private int mSelectModel;

    private int mMaxCount;
    /**
     * 所有选择的图片 path 集合
     */
    private ArrayList<String> mSelectedResult;

    // 请求加载系统照相机
    private static final int REQUEST_CAMERA = 100;

    private LoadAlbumPresenter mLoadALbumPresenter;
    /**
     * 用来展示图片的RecyclerView(Grid)
     */
    private RecyclerView mImgGridRv;
    /**
     * 相册列表RecyclerView
     */
    private RecyclerView mAlbumFloderListRV;

    private ImageGridAdapter mImageAdapter;

    private List<AlbumFloder> mAlbumFloders;

    private FloatingActionButton mFab;

    private View mOverlay;

    private Button mSubmitBtn;

    private Toolbar mToolbar;
    private File mTmpFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        if (getIntent() != null) {
            mSelectModel = getIntent().getIntExtra(ARG_SELECT_MODEL, MULTI_MODEL);
            mMaxCount = getIntent().getIntExtra(ARG_SELECT_MAX_COUNT, DEFAULT_MAX_COUNT);
            mSelectedResult = new ArrayList<>(mMaxCount);
        }
        mLoadALbumPresenter = new LoadAlbumPresenerImpl(this);
        initViews();
        Bundle bundle = new Bundle();

    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mImgGridRv = (RecyclerView) findViewById(R.id.rv_image_grid);
        mAlbumFloderListRV = (RecyclerView) findViewById(R.id.rv_album_list);
        mSubmitBtn = (Button) mToolbar.findViewById(R.id.btn_submit);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mOverlay = findViewById(R.id.overlay);
        mFab.setOnClickListener(this);
        mOverlay.setOnClickListener(this);
        mSubmitBtn.setOnClickListener(this);
        mSubmitBtn.setEnabled(false);
        initRecyclerView();
        LoadData();
    }

    private void LoadData() {
        mImageAdapter = new ImageGridAdapter(this, mMaxCount, mSelectModel);
        mImgGridRv.setAdapter(mImageAdapter);
        mLoadALbumPresenter.loadAllImageData(this, getSupportLoaderManager());
    }

    private void initRecyclerView() {
        mImgGridRv.setHasFixedSize(true);
        GridLayoutManager glManager = new GridLayoutManager(this, spanCount);
        mImgGridRv.setLayoutManager(glManager);
        mImgGridRv.addItemDecoration(new GridDividerDecorator(this));

        mAlbumFloderListRV.setHasFixedSize(true);
        mAlbumFloderListRV.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_submit) {
            //返回选择的图片路径集合
            Intent data = new Intent();
            data.putStringArrayListExtra(SELECTED_RESULT, mSelectedResult);
            setResult(RESULT_OK, data);
            finish();

        } else if (id == R.id.fab) {
            if (mFab.getVisibility() == View.VISIBLE) {
                FabTransformation.with(mFab).setOverlay(mOverlay).transformTo(mAlbumFloderListRV);
            }
        } else if (id == R.id.overlay) {
            FabTransformation.with(mFab).setOverlay(mOverlay).transformFrom(mAlbumFloderListRV);
        }
    }

    @Override
    public void bindAlbumData(List<AlbumFloder> data) {
        if (data.isEmpty()) {
            findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
            return;
        }
        mAlbumFloders = data;
        switchAlbumFolder(mAlbumFloders.get(0));
        createAlbumList(mAlbumFloders);
    }

    private void createAlbumList(List<AlbumFloder> mAlbumFloders) {
        mAlbumFloderListRV.setAdapter(new FolderListAdapter(this, mAlbumFloders));
    }

    @Override
    public void switchAlbumFolder(AlbumFloder floder) {
        mImageAdapter.setData(floder.getImgInfos());
        FabTransformation.with(mFab).setOverlay(mOverlay).transformFrom(mAlbumFloderListRV);
    }

    @Override
    public void updateSelectedCount(ImageInfo imageInfo) {
        if (imageInfo.isSelected()) {
            mSelectedResult.add(imageInfo.getPath());
        } else {
            mSelectedResult.remove(imageInfo.getPath());
        }
        if (mSelectedResult.size() > 0) {
            mSubmitBtn.setText("完成（" + mSelectedResult.size() + "/9)");
            mSubmitBtn.setEnabled(true);
        }
        KLog.i(mSelectedResult);
    }

    @Override
    public void showCarmeraAction() {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(this.getPackageManager()) != null) {
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            mTmpFile = null;
            try {
                mTmpFile = FileUtils.createTmpFile(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mTmpFile != null && mTmpFile.exists()) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
            } else {
                Snackbar.make(mFab, "图片错误", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        } else {

            Snackbar.make(mFab, R.string.msg_no_camera, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                if (mTmpFile != null) {
                    // notify system ,保存拍照的照片到MediaStore,
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mTmpFile)));
                    Intent intent = new Intent();
                    mSelectedResult.add(mTmpFile.getAbsolutePath());
                    intent.putStringArrayListExtra(SELECTED_RESULT, mSelectedResult);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            } else {
                //删除零时文件
                while (mTmpFile != null && mTmpFile.exists()) {
                    boolean success = mTmpFile.delete();
                    if (success) {
                        mTmpFile = null;
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mFab.getVisibility() != View.VISIBLE) {
            FabTransformation.with(mFab).setOverlay(mOverlay).transformFrom(mAlbumFloderListRV);
            return;
        }
        super.onBackPressed();
    }


    public static class Builder {
        protected int maxCount;
        protected SelectModel mSelectedModel;


        protected enum SelectModel {
            /**
             * 多选模式
             */
            MULTI,
            /**
             *单选模式
             */
            SINGLE
        }
    }
}
