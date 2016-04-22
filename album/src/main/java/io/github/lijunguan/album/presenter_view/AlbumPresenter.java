package io.github.lijunguan.album.presenter_view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;

import java.io.File;
import java.util.List;

import io.github.lijunguan.album.ImgSelector;
import io.github.lijunguan.album.R;
import io.github.lijunguan.album.model.AlbumModel;
import io.github.lijunguan.album.model.AlbumModelImpl;
import io.github.lijunguan.album.model.entity.AlbumFolder;
import io.github.lijunguan.album.model.entity.ImageInfo;

import static io.github.lijunguan.album.utils.CommonUtils.checkNotNull;

/**
 * Created by lijunguan on 2016/4/21.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 */
public class AlbumPresenter implements AlbumContract.Presenter {

    private AlbumContract.View mAlbumView;

    private AlbumModel mAlbumModel;

    private LoaderManager mLoadManager;

    private Context mContext;


    public AlbumPresenter(@NonNull Context context, @NonNull LoaderManager loaderManager, @NonNull AlbumContract.View albumView) {
        mLoadManager = checkNotNull(loaderManager, "loader manager cannot be null");
        mAlbumView = checkNotNull(albumView, "albumView cannot be null");
        mContext = checkNotNull(context);
        mAlbumModel = new AlbumModelImpl();

        //为mAlbumView 设置Presenter
        mAlbumView.setPresenter(this);

    }

    @Override
    public void start() {
        mAlbumModel.initImgRepository(mContext, mLoadManager, new AlbumModel.OnInitFinish() {
            @Override
            public void onFinsh(@NonNull List<ImageInfo> imageInfos) {
                checkNotNull(imageInfos);
                if (!imageInfos.isEmpty()) {
                    mAlbumView.showImages(imageInfos);
                } else {
                    mAlbumView.showEmptyView();
                }

                mAlbumView.initFolderList(mAlbumModel.getFolders());
            }
        });
    }

    @Override
    public void result(int requestCode, int resultCode, File mTmpFile) {
        if (requestCode == ImgSelector.REQUEST_OPEN_CAMERA && resultCode == Activity.RESULT_OK) {
            mAlbumModel.addSelect(mTmpFile.getAbsolutePath());
            mAlbumView.selectComplete(mAlbumModel.getSelectedResult(), true);
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
    public void selectImage(ImageInfo imageInfo, int maxCount, int position) {
        checkNotNull(imageInfo, "ImageInfo cannot be null");
        if (getSelectResult().size() >= maxCount) {
            mAlbumView.showToast(mContext.getResources().getString(R.string.out_of_limit, maxCount));
            mAlbumView.restoreChecbox(position);
            return;
        }
        imageInfo.setSelected(true);
        mAlbumModel.addSelect(imageInfo.getPath());
        mAlbumView.showSelectedCount(mAlbumModel.getSelectedCount());
    }

    @Override
    public void unSelectImage(ImageInfo imageInfo) {
        checkNotNull(imageInfo, "ImageInfo cannot be null");
        imageInfo.setSelected(false);
        mAlbumModel.removeSelect(imageInfo.getPath());
        mAlbumView.showSelectedCount(mAlbumModel.getSelectedCount());
    }

    @Override
    public void previewImage(ImageInfo imageInfo) {
        checkNotNull(imageInfo, "ImageInfo cannot be null");
        mAlbumView.showImageDetailUi(imageInfo);
    }


    @Override
    public void commitSlection(List<String> selectResult) {
        checkNotNull(selectResult);
        mAlbumView.selectComplete(selectResult, false);
    }

    @Override
    public void openCamera() {
        mAlbumView.showSystemCamera();
    }

    @Override
    public List<String> getSelectResult() {
        return mAlbumModel.getSelectedResult();
    }


}
