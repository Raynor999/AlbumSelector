package io.github.lijunguan.imgselector.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;

import java.util.List;

import io.github.lijunguan.imgselector.data.entity.AlbumFolder;
import io.github.lijunguan.imgselector.data.entity.ImageInfo;


/**
 * Created by lijunguan on 2016/4/8
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 * <p>
 * Model 层接口
 */
public interface AlbumDataSource {


    void loadImages(@NonNull LoaderManager loaderManager, @NonNull InitAlbumCallback mCallback);

    void selectedImage(@NonNull ImageInfo igeInfo);

    void unSelectedImage(@NonNull ImageInfo imageInfo);


    @Nullable
    List<String> getSelectedResult();

    int getSelectedCount();

    void clearAlbumRepository();

    void updateFolder(AlbumFolder folder);

    interface InitAlbumCallback {

        void onInitFinish(List<AlbumFolder> folders);

        void onDataNoAvaliable();
    }

}

