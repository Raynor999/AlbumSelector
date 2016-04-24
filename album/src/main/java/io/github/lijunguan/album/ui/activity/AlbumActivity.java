package io.github.lijunguan.album.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

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
    private Toolbar mToolbar;

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
                (AlbumFragment) getSupportFragmentManager().findFragmentByTag(AlbumFragment.TAG);

        if (albumFragment == null) {
            //创建AlbumFragment
            if (config == null)
                config = new AlbumConfig();
            albumFragment = AlbumFragment.newInstance(config);
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), albumFragment, AlbumFragment.TAG, false);
        }
        //创建AlbumPresenter
        mAlbumPresenter = new AlbumPresenter(
                getApplicationContext(),
                getSupportLoaderManager(),
                albumFragment);
    }

    private void initToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        mSubmitBtn = (Button) mToolbar.findViewById(R.id.btn_submit);
        mSubmitBtn.setEnabled(false);
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlbumPresenter.commitSlection();
            }
        });
    }


    public void setSubmitBtnText(CharSequence text, boolean enabled) {
        mSubmitBtn.setText(text);
        mSubmitBtn.setEnabled(enabled);
    }

    public void setToolbarTitle(CharSequence title) {
        mToolbar.setTitle(title);
    }

    @Override
    public void onBackPressed() {
        KLog.d(TAG, "=======onBackPressed========");
        AlbumFragment albumFragment =
                (AlbumFragment) getSupportFragmentManager().findFragmentByTag(AlbumFragment.TAG);

        if (albumFragment == null) {
            super.onBackPressed();
            return;
        }

        if (albumFragment.isHidden()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .show(albumFragment)
                    .commit();
        } else if (albumFragment.mFab.getVisibility() != View.VISIBLE) {
            albumFragment.hideFolderList();
            return;
        }
        super.onBackPressed();

    }
}
