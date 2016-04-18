package io.github.lijunguan.album.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import io.github.lijunguan.album.R;
import io.github.lijunguan.album.entity.AlbumFloder;
import io.github.lijunguan.album.entity.ImageInfo;
import io.github.lijunguan.album.utils.KLog;


/**
 * Created by lijunguan on 2016/4/8
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 */
public class LoadAlbumModelImpl implements LoadAlbumModel {


    public static final String TAG = LoadAlbumModelImpl.class.getSimpleName();
    /**
     * Loader的唯一ID号
     */
    private final static int IMAGE_LOADER_ID = 1000;


    private static final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media._ID};

    private OnLoadAllImageFinish mOnScanImageFinish;


    //非空注解，参数都不能为空
    @Override
    public void loadAllImage(@NonNull final Context context,
                             @NonNull LoaderManager loaderManager, @NonNull final OnLoadAllImageFinish listener) {
        mOnScanImageFinish = listener;

        LoaderManager.LoaderCallbacks imgLoadCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION, null,
                        null, IMAGE_PROJECTION[2] + " DESC");
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                if (data == null) return;
                if (data.getCount() <= 0) {
                    if (mOnScanImageFinish != null)
                        mOnScanImageFinish.onFinsh(Collections.EMPTY_LIST);  //无图片返回EmptyList
                    return;
                }
                //创建包涵所有图片的相册目录
                AlbumFloder allAlbumFolder = new AlbumFloder();
                ArrayList<ImageInfo> imgList = new ArrayList<>();
                allAlbumFolder.setImgInfos(imgList);
                allAlbumFolder.setFloderName(context.getResources().getString(R.string.all_picture));
                mAlbumFloderList.add(allAlbumFolder);

                while (data.moveToNext()) {
                    ImageInfo imageInfo = createImageInfo(data);
                    allAlbumFolder.getImgInfos().add(imageInfo); //每一张图片都加入到allAlbumFolder 目录中
                    File folderFile = new File(imageInfo.getPath()).getParentFile(); //得到当前图片的目录
                    String path = folderFile.getAbsolutePath();
                    AlbumFloder albumFloder = getFloderByPath(path);
                    if (albumFloder == null) {
                        //相册集合中不存在，则创建该相册目录，并添加到集合中
                        albumFloder = new AlbumFloder();
                        albumFloder.setCover(imageInfo);
                        albumFloder.setFloderName(folderFile.getName());
                        albumFloder.setPath(path);
                        ArrayList<ImageInfo> imageInfos = new ArrayList<>();
                        imageInfos.add(imageInfo);
                        albumFloder.setImgInfos(imageInfos);
                        mAlbumFloderList.add(albumFloder);
                    } else {
                        albumFloder.getImgInfos().add(imageInfo);
                    }
                }
                allAlbumFolder.setCover(allAlbumFolder.getImgInfos().get(0));
                KLog.d(TAG, "onLoadFinished :" + mAlbumFloderList.size());

                if (mOnScanImageFinish != null) {
                    mOnScanImageFinish.onFinsh(mAlbumFloderList);
                }
            }


            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };

        loaderManager.initLoader(IMAGE_LOADER_ID, null, imgLoadCallback);
    }

    /**
     * 根据传入的路径得到AlbumFloder对象
     *
     * @param path 文件路径
     * @return 如果mAlbumFloders集合中存在 改path路径的albumFolder则返回AlbumFloder ，否则返回null
     */
    private AlbumFloder getFloderByPath(String path) {
        if (mAlbumFloderList == null)
            return null;

        for (AlbumFloder floder : mAlbumFloderList) {
            if (TextUtils.equals(floder.getPath(), path)) {
                return floder;
            }
        }
        return null;
    }


    private ImageInfo createImageInfo(Cursor data) {
        String imgPath = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
        String displayName = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
        long addedTime = data.getLong(data.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
        long imageSize = data.getLong(data.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
        return new ImageInfo(imgPath, displayName, addedTime, imageSize);
    }

    @Override
    public AlbumFloder getAlbumFloder(@NonNull ImageInfo imageInfo) {
        if (imageInfo == null) return null;
        for (AlbumFloder floder : mAlbumFloderList) {
            if (floder.getImgInfos().contains(imageInfo)) {
                return floder;
            }
        }
        return null;
    }
}
