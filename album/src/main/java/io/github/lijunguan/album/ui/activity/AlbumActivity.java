package io.github.lijunguan.album.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.github.lijunguan.album.R;
import io.github.lijunguan.album.adapter.ImageGridAdapter;
import io.github.lijunguan.album.base.BaseActivity;
import io.github.lijunguan.album.entity.AlbumFloder;
import io.github.lijunguan.album.entity.ImageInfo;
import io.github.lijunguan.album.presenter.LoadAlbumPresenerImpl;
import io.github.lijunguan.album.presenter.LoadAlbumPresenter;
import io.github.lijunguan.album.ui.widget.GridDividerDecorator;
import io.github.lijunguan.album.utils.KLog;
import io.github.lijunguan.album.view.AlbumView;


public class AlbumActivity extends BaseActivity implements AlbumView {
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

    private LoadAlbumPresenter mLoadALbumPresenter;
    private RecyclerView mGridRv;
    private ImageGridAdapter mAdapter;
    private List<ImageInfo> mImageInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        if (getIntent() != null) {
            mSelectModel = getIntent().getIntExtra(ARG_SELECT_MODEL, MULTI_MODEL);
            mMaxCount = getIntent().getIntExtra(ARG_SELECT_MAX_COUNT, DEFAULT_MAX_COUNT);
        }
        mLoadALbumPresenter = new LoadAlbumPresenerImpl(this);
        initViews();
    }

    private void initViews() {
        mGridRv = (RecyclerView) findViewById(R.id.recycler_view);
        initRecyclerView();
        LoadData();
    }

    private void LoadData() {
        mAdapter = new ImageGridAdapter(this, mImageInfos);
        mGridRv.setAdapter(mAdapter);
        mLoadALbumPresenter.loadAllImageData(this, getSupportLoaderManager());
    }

    private void initRecyclerView() {
        mGridRv.setHasFixedSize(true);
        GridLayoutManager glManager = new GridLayoutManager(this, spanCount);
        mGridRv.setLayoutManager(glManager);
        mGridRv.addItemDecoration(new GridDividerDecorator(this));
    }

    @Override
    public void bindAlbumData(List<AlbumFloder> data) {

        if (data.isEmpty()) {
            findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
            return;
        }
        switchAlbumFolder(data.get(0));
    }
    @Override
    public void switchAlbumFolder(AlbumFloder floder) {
        if (!mImageInfos.isEmpty()) {
            mImageInfos.clear();
        }
        mImageInfos.addAll(floder.getImgInfos());
        mAdapter.notifyDataSetChanged();
        mGridRv.postDelayed(new Runnable() {
            @Override
            public void run() {
                int count = mGridRv.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = mGridRv.getChildAt(i);
                    KLog.e("paddingBottom:"+child.getPaddingBottom() + "paddingRight:" + child.getPaddingRight());

                }
            }
        },2000);


    }


}
