package io.github.lijunguan.imgselector.previewimage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import io.github.lijunguan.imgselector.R;
import io.github.lijunguan.imgselector.base.BaseActivity;
import io.github.lijunguan.imgselector.data.AlbumRepository;
import io.github.lijunguan.imgselector.utils.ActivityUtils;

/**
 * Created by lijunguan on 16/6/15.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 */
public class PreviewActivity extends BaseActivity {


    private AlbumRepository mAlbumRepository;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        mToolbar.setBackgroundResource(R.drawable.alpha_toolbar);
        Bundle args = null;
        if (getIntent() != null) {
            args = getIntent().getExtras();
        }
        if (args == null) return;

        ImageDetailFragment fragment = (ImageDetailFragment) getSupportFragmentManager()
                .findFragmentByTag(ImageDetailFragment.TAG);

        if (fragment == null) {
            fragment = ImageDetailFragment.newInstance(args);


            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    fragment,
                    ImageDetailFragment.TAG);
        }

        mAlbumRepository = AlbumRepository.getInstance(this);

        new ImageDetailPresenter(
                mAlbumRepository,
                fragment);

    }

    @Override
    protected void onSubmitBtnClick() {
        setResult(Activity.RESULT_OK, new Intent());
        finish();
    }


}
