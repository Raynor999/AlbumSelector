package io.github.lijunguan.album.presenter_view;

import java.util.List;

import io.github.lijunguan.album.base.BasePresenter;
import io.github.lijunguan.album.base.BaseView;
import io.github.lijunguan.album.model.entity.ImageInfo;

/**
 * Created by lijunguan on 2016/4/19.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 *
 *
 *指定 View 和 Presenter之间的关系，统一声明便于查看和管理接口方法
 */
public interface AlbumContract {

    interface View extends BaseView<Presenter> {

        void showEmptyView();

        void showToast(CharSequence message);

        void showImages(List<ImageInfo> imageInfos);

    }

    interface Presenter extends BasePresenter {

        void loadAlbumFolder();

        void swtichFloder();

        void selectImage();

        void unSelectImage();

        void previewImage();

        void commitSlection();

    }

}
