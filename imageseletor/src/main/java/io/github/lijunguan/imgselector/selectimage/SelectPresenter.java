package io.github.lijunguan.imgselector.selectimage;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;

import java.io.File;
import java.util.List;

import io.github.lijunguan.imgselector.ImageSelector;
import io.github.lijunguan.imgselector.cropimage.CropActivity;
import io.github.lijunguan.imgselector.data.AlbumDataSource;
import io.github.lijunguan.imgselector.data.AlbumRepository;
import io.github.lijunguan.imgselector.data.entity.AlbumFolder;
import io.github.lijunguan.imgselector.data.entity.ImageInfo;

import static io.github.lijunguan.imgselector.utils.CheckUtils.checkNotNull;

/**
 * Created by lijunguan on 2016/4/21.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 */
public class SelectPresenter implements SelectContract.Presenter {

    private SelectContract.View mAlbumView;

    private AlbumRepository mAlbumRepository;

    private LoaderManager mLoadManager;


    public SelectPresenter(
            @NonNull AlbumRepository albumRepository,
            @NonNull LoaderManager loaderManager,
            @NonNull SelectContract.View albumView) {
        mLoadManager = checkNotNull(loaderManager, "loader manager cannot be null");
        mAlbumView = checkNotNull(albumView, "albumView cannot be null");
        mAlbumRepository = checkNotNull(albumRepository, "albumRepository cannot be null");
        //为mAlbumView 设置Presenter
        mAlbumView.setPresenter(this);
    }

    @Override
    public void start() {
        loadData();
    }

    private void loadData() {
        mAlbumRepository.loadImages(mLoadManager, new AlbumDataSource.InitAlbumCallback() {
            @Override
            public void onInitFinish(List<AlbumFolder> folders) {
                mAlbumView.initFolderList(folders);
                swtichFolder(mAlbumRepository.getSelectedAlbum());
            }

            @Override
            public void onDataNoAvaliable() {
                mAlbumView.showEmptyView(null);
            }
        });
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data, File mTmpFile) {

        switch (requestCode) {
            case ImageSelector.REQUEST_OPEN_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    if (ImageSelector.getInstance().getConfig().getSelectModel() == ImageSelector.AVATOR_MODE) {
                        mAlbumView.showImageCropUi(mTmpFile.getPath());
                        return;
                    }
                    mAlbumRepository.selectedImage(mTmpFile.getPath());
                    mAlbumView.selectComplete(mAlbumRepository.getSelectedResult(), true);
                } else if (mTmpFile != null && mTmpFile.exists()) {
                    //出错时，删除零时文件,
                    mTmpFile.delete();
                }
                break;
            case ImageSelector.REQUEST_CROP_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    String path = data.getStringExtra(CropActivity.CROP_RESULT);
                    mAlbumRepository.selectedImage(path);
                    mAlbumView.selectComplete(mAlbumRepository.getSelectedResult(), false);
                }
                break;

            case ImageSelector.REQUEST_PRVIEW_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    returnResult();
                }
        }

    }

    @Override
    public void swtichFolder(@NonNull AlbumFolder folder) {
        checkNotNull(folder);
        //刷新当前选择的相册目录
        mAlbumRepository.updateFolder(folder);
        mAlbumView.showImages(folder.getImgInfos());
        mAlbumView.hideFolderList();
    }

    @Override
    public void selectImage(@NonNull ImageInfo imageInfo, int maxCount, int position) {
        checkNotNull(imageInfo, "ImageInfo cannot be null");
        if (mAlbumRepository.getSelectedResult().size() >= maxCount) {
            mAlbumView.showOutOfRange(position);
            return;
        }
        mAlbumRepository.selectedImage(imageInfo);
        mAlbumView.showSelectedCount(mAlbumRepository.getSelectedCount());
    }

    @Override
    public void unSelectImage(@NonNull ImageInfo imageInfo, int positon) {
        checkNotNull(imageInfo, "ImageInfo cannot be null");
        mAlbumRepository.unSelectedImage(imageInfo);
        mAlbumView.showSelectedCount(mAlbumRepository.getSelectedCount());
    }

    @Override
    public void previewImage(int position) {
        mAlbumView.showImageDetailUi(position);
    }

    @Override
    public void cropImage(ImageInfo imageInfo) {
        checkNotNull(imageInfo);
        mAlbumView.showImageCropUi(imageInfo.getPath());
    }

    @Override
    public void returnResult() {
        List<String> selectedResult = mAlbumRepository.getSelectedResult();
        mAlbumView.selectComplete(selectedResult, false);
    }

    @Override
    public void openCamera() {
        mAlbumView.showSystemCamera();
    }

    public void clearCache() {
        mAlbumRepository.clearAlbumRepository();
    }

}
