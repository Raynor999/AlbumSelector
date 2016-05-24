package io.github.lijunguan.imgselector.album;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import io.github.lijunguan.imgselector.R;
import io.github.lijunguan.imgselector.album.previewimage.ImageDetailFragment;
import io.github.lijunguan.imgselector.base.BaseActivity;
import io.github.lijunguan.imgselector.model.AlbumRepository;
import io.github.lijunguan.imgselector.utils.ActivityUtils;
import io.github.lijunguan.imgselector.utils.KLog;


public class AlbumActivity extends BaseActivity {

    public static final String TAG = AlbumActivity.class.getSimpleName();

    private AlbumPresenter mAlbumPresenter;

    private Button mSubmitBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        initViews();

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

            albumFragment = AlbumFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), albumFragment, AlbumFragment.TAG, false);

        }
        AlbumRepository albumRepository = AlbumRepository.getInstance(this);
        //创建AlbumPresenter
        mAlbumPresenter = new AlbumPresenter(
                albumRepository,
                getSupportLoaderManager(),
                albumFragment);
    }


    private void initViews() {
        mSubmitBtn = (Button) mToolbar.findViewById(R.id.btn_submit);
        mSubmitBtn.setEnabled(false);
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlbumPresenter.returnResult();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KLog.d(TAG, "=======onDestroy========");
        mAlbumPresenter.clearCache();
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        KLog.d("=========onConfigurationChanged===========");
    }
}
