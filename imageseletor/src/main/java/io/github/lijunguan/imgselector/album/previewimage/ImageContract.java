package io.github.lijunguan.imgselector.album.previewimage;

import android.support.annotation.NonNull;

import io.github.lijunguan.imgselector.base.BasePresenter;
import io.github.lijunguan.imgselector.base.BaseView;
import io.github.lijunguan.imgselector.model.entity.ImageInfo;

/**
 * Created by lijunguan on 2016/4/24.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 */
public interface ImageContract {

    interface View extends BaseView<Presenter> {

        void updateIndicator();

        void showOutOfRange(int position);

        void showSelectedCount(int count);
    }

    interface Presenter extends BasePresenter {

        void selectImage(@NonNull ImageInfo imageInfo, int maxCount, int position);

        void unSelectImage(@NonNull ImageInfo imageInfo, int position);
    }

}
