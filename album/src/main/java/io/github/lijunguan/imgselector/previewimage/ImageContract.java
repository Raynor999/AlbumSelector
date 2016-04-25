package io.github.lijunguan.imgselector.previewimage;

import io.github.lijunguan.imgselector.base.BasePresenter;
import io.github.lijunguan.imgselector.base.BaseView;
import io.github.lijunguan.imgselector.model.entity.ImageInfo;

/**
 * Created by lijunguan on 2016/4/24.
 */
public interface ImageContract {

    interface View extends BaseView<Presenter> {

        void updateIndicator();

        void showOutOfRange(int position);

        void showSelectedCount(int count);
    }

    interface Presenter extends BasePresenter {

        void selectImage(ImageInfo imageInfo, int maxCount, int position);

        void unSelectImage(ImageInfo imageInfo);
    }

}
