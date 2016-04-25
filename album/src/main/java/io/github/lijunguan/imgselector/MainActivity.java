package io.github.lijunguan.imgselector;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import io.github.lijunguan.imgselector.album.AlbumFragment;
import io.github.lijunguan.imgselector.album.AlbumPresenter;
import io.github.lijunguan.imgselector.base.BaseActivity;
import io.github.lijunguan.imgselector.previewimage.ImageDetailFragment;
import io.github.lijunguan.imgselector.utils.ActivityUtils;
import io.github.lijunguan.imgselector.utils.KLog;


public class MainActivity extends BaseActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private AlbumPresenter mAlbumPresenter;

    private Button mSubmitBtn;

    private AlbumConfig config;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        initViews();
        config = null;
        if (getIntent() != null) { //得到用户传入的配置
            config = getIntent()
                    .getParcelableExtra(ImgSelector.ARG_ALBUM_CONFIG);
        }

        AlbumFragment albumFragment;
        ImageDetailFragment imageDetailFragment;

        if (savedInstanceState != null) {   //内存重启时调用   解决 内存重启时可能发生的Fragment重叠异常
            imageDetailFragment =
                    (ImageDetailFragment) getSupportFragmentManager().findFragmentByTag(ImageDetailFragment.TAG);
            albumFragment =
                    (AlbumFragment) getSupportFragmentManager().findFragmentByTag(AlbumFragment.TAG);

            if (imageDetailFragment != null && albumFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .hide(albumFragment)
                        .show(imageDetailFragment)
                        .commit();
            }
        } else {
            //创建AlbumFragment
            if (config == null)
                config = new AlbumConfig();
            albumFragment = AlbumFragment.newInstance(config);
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), albumFragment, AlbumFragment.TAG, false);

            //创建AlbumPresenter
            mAlbumPresenter = new AlbumPresenter(
                    getApplicationContext(),
                    getSupportLoaderManager(),
                    albumFragment);
        }

    }

    private void initViews() {
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
