package io.github.lijunguan.album.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;

import java.util.List;

import io.github.lijunguan.album.entity.AlbumFloder;
import io.github.lijunguan.album.entity.ImageInfo;
import io.github.lijunguan.album.model.LoadAlbumModel;
import io.github.lijunguan.album.model.LoadAlbumModelImpl;
import io.github.lijunguan.album.view.AlbumView;


/**
 * Created by lijunguan on 2016/4/11
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 */
public class LoadAlbumPresenerImpl implements LoadAlbumPresenter{
    private LoadAlbumModel mLoadAlbumModel;
    private AlbumView mAlbumView;

    public LoadAlbumPresenerImpl(AlbumView albumView) {
        this.mAlbumView = albumView;
        mLoadAlbumModel = new LoadAlbumModelImpl();
    }

    @Override
    public void loadAllImageData(@NonNull Context context, @NonNull LoaderManager loaderManager) {
        mLoadAlbumModel.loadAllImage(context, loaderManager, new LoadAlbumModel.OnLoadAllImageFinish() {
            @Override
            public void onFinsh(@NonNull List<AlbumFloder> floders) {
                mAlbumView.bindAlbumData(floders); //绑定相册数据
            }
        });
    }

    public AlbumFloder getFloderByImageInfo(ImageInfo imageInfo) {
        return mLoadAlbumModel.getAlbumFloder(imageInfo);
    }
}
