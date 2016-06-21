package io.github.lijunguan.imgselector.previewimage;

import android.support.annotation.NonNull;

import io.github.lijunguan.imgselector.data.AlbumRepository;
import io.github.lijunguan.imgselector.data.entity.AlbumFolder;
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
        loadImages();
    }

    private void loadImages() {
        AlbumFolder selectedAlbum = mAlbumRepository.getSelectedAlbum();
        mImageDetailView.initImageDetailUi(selectedAlbum.getImgInfos());
        mImageDetailView.showSelectedCount(mAlbumRepository.getSelectedCount());
        mImageDetailView.updateIndicator();
    }


    @Override
    public void selectImage(@NonNull ImageInfo imageInfo, int maxCount, int position) {
        checkNotNull(imageInfo, "ImageInfo cannot be null");
        if (mAlbumRepository.getSelectedResult().size() >= maxCount) {
            mImageDetailView.showOutOfRange(0);
            return;
        }
        mAlbumRepository.selectedImage(imageInfo);
        mImageDetailView.showSelectedCount(mAlbumRepository.getSelectedCount());
    }

    @Override
    public void unSelectImage(@NonNull ImageInfo imageInfo, int position) {
        checkNotNull(imageInfo, "ImageInfo cannot be null");

        mAlbumRepository.unSelectedImage(imageInfo);
        mImageDetailView.showSelectedCount(mAlbumRepository.getSelectedCount());
    }
}
