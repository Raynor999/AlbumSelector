package io.github.lijunguan.imgselector.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;

import java.util.List;

import io.github.lijunguan.imgselector.model.entity.AlbumFolder;


/**
 * Created by lijunguan on 2016/4/8
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 * <p>
 * Model 层接口
 */
public interface AlbumDataSource {


    void initImgRepository(@NonNull LoaderManager loaderManager, @NonNull InitAlbumCallback mCallback);

    @Nullable
    List<String> getSelectedResult();

    void addSelect(@NonNull String path);

    void removeSelect(@NonNull String path);

    void clearCacheAndSelect();

    int getSelectedCount();


    interface InitAlbumCallback {

        void onInitFinish(List<AlbumFolder> folders);

        void onDataNoAvaliable();
    }

}

