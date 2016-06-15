package io.github.lijunguan.imgselector.previewimage;

import android.support.annotation.NonNull;

import io.github.lijunguan.imgselector.data.AlbumRepository;
import io.github.lijunguan.imgselector.data.entity.ImageInfo;

import static io.github.lijunguan.imgselector.utils.CheckUtils.checkNotNull;

/**
 * Created by lijunguan on 2016/4/24.
 */
public class ImageDetailPresenter implements ImageContract.Presenter {
    private AlbumRepository mAlbumRepository;

    private ImageContract.View mImageDetailView;


    public ImageDetailPresenter(
            @NonNull AlbumRepository albumRepository,
            @NonNull ImageContract.View imageDetailView
           ) {

        mImageDetailView = checkNotNull(imageDetailView, "ImageContract.View  cannt be null");
        mAlbumRepository = checkNotNull(albumRepository, "AlbumRepository cannt be null");
        mImageDetailView.setPresenter(this);
    }

    public void start() {
//        AlbumFolder folder = mAlbumRepository.getFolderByImage(mImageInfo);
//        int index = folder.getImgInfos().indexOf(mImageInfo);
//        mImageDetailView.showImageDetail(index, folder.getImgInfos());
    }


    @Override
    public void selectImage(@NonNull ImageInfo imageInfo, int maxCount, int position) {
        checkNotNull(imageInfo, "ImageInfo cannot be null");
        if (mAlbumRepository.getSelectedResult().size() >= maxCount) {
            mImageDetailView.showOutOfRange(0);
            return;
        }
        imageInfo.setSelected(true);
        mAlbumRepository.addSelect(imageInfo.getPath());
        mImageDetailView.showSelectedCount(mAlbumRepository.getSelectedCount());
    }

    @Override
    public void unSelectImage(@NonNull ImageInfo imageInfo,int position) {
        checkNotNull(imageInfo, "ImageInfo cannot be null");
        imageInfo.setSelected(false);
        mAlbumRepository.removeSelect(imageInfo.getPath());
        mImageDetailView.showSelectedCount(mAlbumRepository.getSelectedCount());
    }
}
