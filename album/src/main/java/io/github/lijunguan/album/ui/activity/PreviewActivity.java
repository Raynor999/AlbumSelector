package io.github.lijunguan.album.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.github.lijunguan.album.R;
import io.github.lijunguan.album.entity.ImageInfo;
import io.github.lijunguan.album.view.SelectedImgView;

public class PreviewActivity extends AppCompatActivity implements SelectedImgView{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
    }

    @Override
    public void updateSelectedCount(ImageInfo imageInfo) {

    }
}
