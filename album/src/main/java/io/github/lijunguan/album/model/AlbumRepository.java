package io.github.lijunguan.album.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.lijunguan.album.R;
import io.github.lijunguan.album.model.entity.AlbumFolder;
import io.github.lijunguan.album.model.entity.ImageInfo;
import io.github.lijunguan.album.utils.KLog;

import static io.github.lijunguan.album.utils.CommonUtils.checkNotNull;


/**
 * Created by lijunguan on 2016/4/8
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 */
public class AlbumRepository implements AlbumDataSource {

    private static AlbumRepository INSTANCE = new AlbumRepository();

    public static final String TAG = AlbumRepository.class.getSimpleName();
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

    private List<AlbumFolder> mAlbumFolders;
    /**
     * 用户选择的图片路径集合
     */
    private List<String> mSelectedResult = new ArrayList<>();

    public static AlbumRepository getInstance() {
        return INSTANCE;
    }

    private AlbumRepository() {

    }

    //非空注解，参数都不能为空
    @Override
    public void initImgRepository(@NonNull final Context context,
                                  @NonNull LoaderManager loaderManager, @NonNull final LoadImagesCallback callback) {
        checkNotNull(loaderManager);
        checkNotNull(callback);
        mSelectedResult.clear();
        LoaderManager.LoaderCallbacks imgLoadCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                KLog.i("===============onCreateLoader============");
                return new CursorLoader(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION, null,
                        null, IMAGE_PROJECTION[2] + " DESC");
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


                mAlbumFolders = new ArrayList<>();
                if (data == null) return;
                if (data.getCount() <= 0) {
                    callback.onDataNoAvaliable();
                    return;
                }
                //创建包涵所有图片的相册目录
                AlbumFolder allAlbumFolder = new AlbumFolder();
                ArrayList<ImageInfo> imgList = new ArrayList<>();
                allAlbumFolder.setImgInfos(imgList);
                allAlbumFolder.setFloderName(context.getResources().getString(R.string.all_picture));
                mAlbumFolders.add(allAlbumFolder);

                while (data.moveToNext()) {
                    ImageInfo imageInfo = createImageInfo(data);
                    allAlbumFolder.getImgInfos().add(imageInfo); //每一张图片都加入到allAlbumFolder 目录中
                    File folderFile = new File(imageInfo.getPath()).getParentFile(); //得到当前图片的目录
                    String path = folderFile.getAbsolutePath();
                    AlbumFolder albumFloder = getFloderByPath(path);
                    if (albumFloder == null) {
                        //相册集合中不存在，则创建该相册目录，并添加到集合中
                        albumFloder = new AlbumFolder();
                        albumFloder.setCover(imageInfo);
                        albumFloder.setFloderName(folderFile.getName());
                        albumFloder.setPath(path);
                        ArrayList<ImageInfo> imageInfos = new ArrayList<>();
                        imageInfos.add(imageInfo);
                        albumFloder.setImgInfos(imageInfos);
                        mAlbumFolders.add(albumFloder);
                    } else {
                        albumFloder.getImgInfos().add(imageInfo);
                    }
                }
                KLog.i(TAG, "========nLoadFinished :" + mAlbumFolders.size());
                allAlbumFolder.setCover(allAlbumFolder.getImgInfos().get(0));

                callback.onImagesLoaded(allAlbumFolder.getImgInfos());

            }


            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };

        loaderManager.initLoader(IMAGE_LOADER_ID, null, imgLoadCallback);
    }

    @Nullable
    @Override
    public List<AlbumFolder> getFolders() {
        return mAlbumFolders;
    }

    /**
     * 根据传入的路径得到AlbumFloder对象
     *
     * @param path 文件路径
     * @return 如果mAlbumFloders集合中存在 改path路径的albumFolder则返回AlbumFloder ，否则返回null
     */
    private AlbumFolder getFloderByPath(String path) {
        AlbumFolder folder = null;
        if (mAlbumFolders != null) {
            for (AlbumFolder floder : mAlbumFolders) {
                if (TextUtils.equals(floder.getPath(), path)) {
                    return floder;
                }
            }
        }
        return folder;
    }


    private ImageInfo createImageInfo(Cursor data) {
        String imgPath = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
        String displayName = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
        long addedTime = data.getLong(data.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
        long imageSize = data.getLong(data.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
        return new ImageInfo(imgPath, displayName, addedTime, imageSize);
    }

    @Override
    public AlbumFolder getFolderByImage(@NonNull ImageInfo imageInfo) {
        AlbumFolder folder = null;
        if (mAlbumFolders != null) {
            for (int i = 1; i < mAlbumFolders.size(); i++) {
                folder = mAlbumFolders.get(i);
                if (folder.getImgInfos().contains(imageInfo)) {
                    return folder;
                }
            }
        }
        return folder;
    }

    @Override
    public List<String> getSelectedResult() {
        return mSelectedResult;
    }

    @Override
    public void addSelect(@NonNull String path) {
        mSelectedResult.add(checkNotNull(path));
    }

    @Override
    public void removeSelect(@NonNull String path) {
        mSelectedResult.remove(checkNotNull(path));
    }

    @Override
    public int getSelectedCount() {
        return mSelectedResult.size();
    }


}
