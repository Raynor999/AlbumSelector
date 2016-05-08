package io.github.lijunguan.imgselector.album;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;

import java.io.File;
import java.util.List;

import io.github.lijunguan.imgselector.ImageSelector;
import io.github.lijunguan.imgselector.cropimage.CropActivity;
import io.github.lijunguan.imgselector.model.AlbumDataSource;
import io.github.lijunguan.imgselector.model.AlbumRepository;
import io.github.lijunguan.imgselector.model.entity.AlbumFolder;
import io.github.lijunguan.imgselector.model.entity.ImageInfo;

import static io.github.lijunguan.imgselector.utils.CommonUtils.checkNotNull;

/**
 * Created by lijunguan on 2016/4/21.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 */
public class AlbumPresenter implements AlbumContract.Presenter {

    private AlbumContract.View mAlbumView;

    private AlbumRepository mAlbumRepository;

    private LoaderManager mLoadManager;


    public AlbumPresenter(
            @NonNull AlbumRepository albumRepository,
            @NonNull LoaderManager loaderManager,
            @NonNull AlbumContract.View albumView) {
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
        mAlbumRepository.initImgRepository(mLoadManager, new AlbumDataSource.InitAlbumCallback() {
            @Override
            public void onInitFinish(List<AlbumFolder> folders) {
                List<ImageInfo> allImages = folders.get(0).getImgInfos();
                mAlbumView.showImages(allImages);
                mAlbumView.initFolderList(folders);
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
                    mAlbumRepository.addSelect(mTmpFile.getPath());
                    mAlbumView.selectComplete(mAlbumRepository.getSelectedResult(), true);
                } else if (mTmpFile != null && mTmpFile.exists()) {
                    //出错时，删除零时文件,
                    mTmpFile.delete();
                }
                break;
            case ImageSelector.REQUEST_CROP_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    String path = data.getStringExtra(CropActivity.CROP_RESULT);
                    mAlbumRepository.addSelect(path);
                    mAlbumView.selectComplete(mAlbumRepository.getSelectedResult(), false);
                }
                break;
        }

    }

    @Override
    public void swtichFloder(@NonNull AlbumFolder floder) {
        checkNotNull(floder);
        mAlbumView.showImages(floder.getImgInfos());
        mAlbumView.hideFolderList();
    }


    @Override
    public void selectImage(@NonNull ImageInfo imageInfo, int maxCount, int position) {
        checkNotNull(imageInfo, "ImageInfo cannot be null");
        if (mAlbumRepository.getSelectedResult().size() >= maxCount) {
            mAlbumView.showOutOfRange(position);
            return;
        }
        imageInfo.setSelected(true);
        mAlbumRepository.addSelect(imageInfo.getPath());
        mAlbumView.showSelectedCount(mAlbumRepository.getSelectedCount());
    }

    @Override
    public void unSelectImage(@NonNull ImageInfo imageInfo,int positon) {
        checkNotNull(imageInfo, "ImageInfo cannot be null");
        imageInfo.setSelected(false);
        mAlbumRepository.removeSelect(imageInfo.getPath());
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
        mAlbumRepository.clearCacheAndSelect();
    }

}
