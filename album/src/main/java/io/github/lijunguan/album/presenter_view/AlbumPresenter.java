package io.github.lijunguan.album.presenter_view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;

import java.io.File;
import java.util.List;

import io.github.lijunguan.album.ImgSelector;
import io.github.lijunguan.album.R;
import io.github.lijunguan.album.model.AlbumDataSource;
import io.github.lijunguan.album.model.AlbumRepository;
import io.github.lijunguan.album.model.entity.AlbumFolder;
import io.github.lijunguan.album.model.entity.ImageInfo;

import static io.github.lijunguan.album.utils.CommonUtils.checkNotNull;

/**
 * Created by lijunguan on 2016/4/21.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 *
 *
 */
public class AlbumPresenter implements AlbumContract.Presenter {

    private AlbumContract.View mAlbumView;

    private AlbumRepository mAlbumRepository;

    private LoaderManager mLoadManager;

    private Context mContext;


    public AlbumPresenter(@NonNull Context context, @NonNull LoaderManager loaderManager, @NonNull AlbumContract.View albumView) {
        mLoadManager = checkNotNull(loaderManager, "loader manager cannot be null");
        mAlbumView = checkNotNull(albumView, "albumView cannot be null");
        mContext = checkNotNull(context);
        mAlbumRepository = AlbumRepository.getInstance();
        //为mAlbumView 设置Presenter
        mAlbumView.setPresenter(this);

    }

    @Override
    public void start() {

        mAlbumRepository.initImgRepository(mContext, mLoadManager, new AlbumDataSource.LoadImagesCallback() {
            @Override
            public void onImagesLoaded(List<ImageInfo> images) {
                mAlbumView.showImages(images);
                mAlbumView.initFolderList(mAlbumRepository.getFolders());
            }

            @Override
            public void onDataNoAvaliable() {
                mAlbumView.showEmptyView();
            }
        });
    }

    @Override
    public void result(int requestCode, int resultCode, File mTmpFile) {
        if (requestCode == ImgSelector.REQUEST_OPEN_CAMERA && resultCode == Activity.RESULT_OK) {
            mAlbumRepository.addSelect(mTmpFile.getAbsolutePath());
            mAlbumView.selectComplete(mAlbumRepository.getSelectedResult(), true);
        } else {
            //出错时，删除零时文件,
            while (mTmpFile != null && mTmpFile.exists()) {
                boolean success = mTmpFile.delete();
                if (success) {
                    mTmpFile = null;
                }
            }
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
            mAlbumView.showOutOfRange(
                    position,
                    mContext.getResources().getString(R.string.out_of_limit, maxCount));
            return;
        }
        imageInfo.setSelected(true);
        mAlbumRepository.addSelect(imageInfo.getPath());
        mAlbumView.showSelectedCount(mAlbumRepository.getSelectedCount());
    }

    @Override
    public void unSelectImage(@NonNull ImageInfo imageInfo) {
        checkNotNull(imageInfo, "ImageInfo cannot be null");
        imageInfo.setSelected(false);
        mAlbumRepository.removeSelect(imageInfo.getPath());
        mAlbumView.showSelectedCount(mAlbumRepository.getSelectedCount());
    }

    @Override
    public void previewImage(@NonNull ImageInfo imageInfo) {
        checkNotNull(imageInfo, "ImageInfo cannot be null");
        mAlbumView.showImageDetailUi(imageInfo);
    }


    @Override
    public void commitSlection() {
        List<String> selectedResult = mAlbumRepository.getSelectedResult();
        mAlbumView.selectComplete(selectedResult, false);
    }

    @Override
    public void openCamera() {
        mAlbumView.showSystemCamera();
    }

}
