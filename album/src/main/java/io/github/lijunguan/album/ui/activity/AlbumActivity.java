package io.github.lijunguan.album.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.util.List;

import io.github.lijunguan.album.AlbumConfig;
import io.github.lijunguan.album.ImgSelector;
import io.github.lijunguan.album.R;
import io.github.lijunguan.album.base.BaseActivity;
import io.github.lijunguan.album.presenter_view.AlbumPresenter;
import io.github.lijunguan.album.ui.fragment.AlbumFragment;
import io.github.lijunguan.album.utils.ActivityUtils;
import io.github.lijunguan.album.utils.KLog;


public class AlbumActivity extends BaseActivity {
    public static final String TAG = AlbumActivity.class.getSimpleName();

    private AlbumPresenter mAlbumPresenter;

    private Button mSubmitBtn;

    private AlbumConfig config;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        initToolBar();
        config = null;
        if (getIntent() != null) { //得到用户传入的配置
            config = getIntent()
                    .getParcelableExtra(ImgSelector.ARG_ALBUM_CONFIG);
        }

        AlbumFragment albumFragment =
                (AlbumFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (albumFragment == null) {
            //创建AlbumFragment
            if (config == null)
                config = new AlbumConfig();
            albumFragment = AlbumFragment.newInstance(config);
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), albumFragment, R.id.fragment_container,false);
        }

        //创建AlbumPresenter
        mAlbumPresenter = new AlbumPresenter(
                getApplicationContext(),
                getSupportLoaderManager(),
                albumFragment);
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        mSubmitBtn = (Button) toolbar.findViewById(R.id.btn_submit);
        mSubmitBtn.setEnabled(false);
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> selectResult = mAlbumPresenter.getSelectResult();
                mAlbumPresenter.commitSlection(selectResult);
            }
        });
    }

    public void setSelectCount(int count) {
        if (count > 0) {
            mSubmitBtn.setText(getString(R.string.update_count, count, config.getMaxCount()));
            mSubmitBtn.setEnabled(true);
        } else {
            mSubmitBtn.setText(getString(R.string.complete));
            mSubmitBtn.setEnabled(false);
        }
    }


    @Override
    public void onBackPressed() {
        KLog.d(TAG,"=======onBackPressed========");
        AlbumFragment albumFragment =
                (AlbumFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (albumFragment != null) {
            if (albumFragment.mFab.getVisibility() != View.VISIBLE) {
                albumFragment.hideFolderList();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }
}
