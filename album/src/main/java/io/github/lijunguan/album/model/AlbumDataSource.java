package io.github.lijunguan.album.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;

import java.util.List;

import io.github.lijunguan.album.model.entity.AlbumFolder;
import io.github.lijunguan.album.model.entity.ImageInfo;


/**
 * Created by lijunguan on 2016/4/8
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 */
public interface AlbumDataSource {


    void initImgRepository(@NonNull Context context, @NonNull LoaderManager loaderManager, @NonNull LoadImagesCallback mCallback);

    @Nullable
    List<AlbumFolder> getFolders();

    AlbumFolder getFolderByImage(@NonNull ImageInfo imageInfo);

    @Nullable
    List<String> getSelectedResult();

    void addSelect(@NonNull String path);

    void removeSelect(@NonNull String path);

    int getSelectedCount();


    interface LoadImagesCallback {

        void onImagesLoaded(List<ImageInfo> images);

        void onDataNoAvaliable();
    }

}

