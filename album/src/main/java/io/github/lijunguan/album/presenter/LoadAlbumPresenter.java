package io.github.lijunguan.album.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;

import io.github.lijunguan.album.model.entity.AlbumFloder;
import io.github.lijunguan.album.model.entity.ImageInfo;

/**
 * Created by lijunguan on 2016/4/8
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 */
public interface LoadAlbumPresenter {
    /**
     * 加载图片文件夹列表
     * @param context
     * @param loaderManager
     */
    void loadAllImageData(@NonNull Context context, @NonNull LoaderManager loaderManager);

    AlbumFloder getFloderByImageInfo(ImageInfo imageInfo);
}
