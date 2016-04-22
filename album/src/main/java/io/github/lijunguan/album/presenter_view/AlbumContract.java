package io.github.lijunguan.album.presenter_view;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

import io.github.lijunguan.album.base.BaseContract;
import io.github.lijunguan.album.model.entity.AlbumFolder;
import io.github.lijunguan.album.model.entity.ImageInfo;

/**
 * Created by lijunguan on 2016/4/19.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 * <p>
 * <p>
 * 指定 View 和 Presenter之间的关系，统一声明便于查看和管理接口方法
 */
public interface AlbumContract {

    interface View extends BaseContract.BaseView<Presenter> {

        void showEmptyView();

        void showImages(List<ImageInfo> imageInfos);

        void showSystemCamera();

        void showFolderList();

        void hideFolderList();

        void initFolderList(List<AlbumFolder> folders);

        void showImageDetailUi(ImageInfo imageInfo);

        /**
         * 图片选择完成，
         * @param imagePaths 选择的图片路径集合
         * @param refreshMedia 是否刷新系统媒体库 true 将通过相机拍摄的照片加入Media.Store
         */
        void selectComplete(List<String> imagePaths, boolean refreshMedia);
    }

    interface Presenter extends BaseContract.BasePresenter {
        /**
         * 切换相册目录，刷新Grid显示
         * @param folder 选择的相册目录实体
         */
        void swtichFloder(@NonNull AlbumFolder folder);

        void previewImage(ImageInfo imageInfo);

        void commitSlection(List<String> selectResult);

        void openCamera();

        List<String> getSelectResult();

        /**
         * 系统相机Activity 返回结果    * {@link Activity#onActivityResult(int, int, Intent)}.
         * @param requestCode 请求码
         * @param resultCode  请求结果码
         * @param mTmpFile  保存相机拍摄图片的零时文件
         */
        void result(int requestCode, int resultCode, File mTmpFile);
    }

}
