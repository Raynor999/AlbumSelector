package io.github.lijunguan.imgselector.cropimage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import io.github.lijunguan.imgselector.R;
import io.github.lijunguan.imgselector.base.BaseActivity;
import io.github.lijunguan.imgselector.utils.ActivityUtils;

/**
 * Created by lijunguan on 2016/4/26.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 */
public class CropActivity extends BaseActivity implements CropFragment.CropImageListener {

    private CropFragment mCropFragment;

    public static final String CROP_RESULT = "cropResult";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        String iamgePath = getIntent().getStringExtra(CropFragment.ARG_IMAGE_PATH);
        if (savedInstanceState == null) {
            mCropFragment = CropFragment.newInstance(iamgePath);
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    mCropFragment,
                    CropFragment.TAG
            );
        }
    }


    @Override
    public void onCropCompleted(String path) {
        Intent intent = new Intent();
        intent.putExtra(CROP_RESULT, path);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onSubmitBtnClick() {
        if (mCropFragment != null)
            mCropFragment.cropImage();
    }
}
