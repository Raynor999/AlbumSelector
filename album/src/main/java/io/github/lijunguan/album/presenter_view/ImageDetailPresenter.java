package io.github.lijunguan.album.presenter_view;

import android.content.Context;
import android.support.annotation.NonNull;

import io.github.lijunguan.album.R;
import io.github.lijunguan.album.model.AlbumRepository;
import io.github.lijunguan.album.model.entity.AlbumFolder;
import io.github.lijunguan.album.model.entity.ImageInfo;

import static io.github.lijunguan.album.utils.CommonUtils.checkNotNull;

/**
 * Created by lijunguan on 2016/4/24.
 */
public class ImageDetailPresenter implements ImageDetailContract.Presenter {
    private AlbumRepository mAlbumRepository;

    private ImageDetailContract.View mImageDetailView;

    private Context mContext;

    private ImageInfo mImageInfo;


    public ImageDetailPresenter(Context context, ImageDetailContract.View imageDetailView, ImageInfo imageInfo) {
        mContext = context;
        mImageDetailView = imageDetailView;
        mImageInfo = checkNotNull(imageInfo);
        mAlbumRepository = AlbumRepository.getInstance();
        mImageDetailView.setPresenter(this);
    }

    public void start() {
        AlbumFolder folder = mAlbumRepository.getFolderByImage(mImageInfo);
        int index = folder.getImgInfos().indexOf(mImageInfo);
        mImageDetailView.showImageDetail(index, folder.getImgInfos());
    }

    @Override
    public void selectImage(@NonNull ImageInfo imageInfo, int maxCount, int position) {
        checkNotNull(imageInfo, "ImageInfo cannot be null");
        if (mAlbumRepository.getSelectedResult().size() >= maxCount) {
            mImageDetailView.showOutOfRange(
                    position,
                    mContext.getResources().getString(R.string.out_of_limit, maxCount));
            return;
        }
        imageInfo.setSelected(true);
        mAlbumRepository.addSelect(imageInfo.getPath());
        mImageDetailView.showSelectedCount(mAlbumRepository.getSelectedCount());
    }

    @Override
    public void unSelectImage(@NonNull ImageInfo imageInfo) {
        checkNotNull(imageInfo, "ImageInfo cannot be null");
        imageInfo.setSelected(false);
        mAlbumRepository.removeSelect(imageInfo.getPath());
        mImageDetailView.showSelectedCount(mAlbumRepository.getSelectedCount());
    }
}
