package io.github.lijunguan.imgselector.previewimage;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import io.github.lijunguan.imgselector.R;
import io.github.lijunguan.imgselector.base.BaseActivity;
import io.github.lijunguan.imgselector.data.AlbumRepository;
import io.github.lijunguan.imgselector.utils.ActivityUtils;
import io.github.lijunguan.imgselector.utils.StatusBarUtil;

/**
 * Created by lijunguan on 16/6/15.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 */
public class PreviewActivity extends BaseActivity {
    /**
     *  由于Actionbar设置的是透明背景，查看背景为纯白图片时，可能干扰Actionbar显示
     *  所以设置渐变遮罩，提高用户体验，学习google photo
     */
    private View mActionbarOverlay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
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

        AlbumRepository albumRepository = AlbumRepository.getInstance(this);

        //创建Presenter
        new ImageDetailPresenter(
                albumRepository,
                fragment);

        mActionbarOverlay = findViewById(R.id.fl_actionbar_overlay);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    @Override
    protected void onSubmitBtnClick() {
        //目的是通知SelectActivity，不需要任何数据
        setResult(Activity.RESULT_OK, new Intent());
        finish();
    }

    @Override
    protected void setStatusBar() {
//        StatusBarUtil.setTranslucent(this);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mToolbar.getLayoutParams();
        layoutParams.setMargins(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);
        mToolbar.requestLayout();
    }

    @Override
    public void fullScreenToggle() {
        super.fullScreenToggle();
        //全屏预览时，隐藏Actionbar的遮罩
        mActionbarOverlay.setVisibility(mActionbarOverlay.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }
}
