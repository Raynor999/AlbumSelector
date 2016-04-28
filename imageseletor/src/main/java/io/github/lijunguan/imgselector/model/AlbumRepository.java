package io.github.lijunguan.imgselector.model;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.lijunguan.imgselector.R;
import io.github.lijunguan.imgselector.model.entity.AlbumFolder;
import io.github.lijunguan.imgselector.model.entity.ImageInfo;
import io.github.lijunguan.imgselector.utils.KLog;

import static io.github.lijunguan.imgselector.utils.CommonUtils.checkNotNull;


/**
 * Created by lijunguan on 2016/4/8
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 */
public class AlbumRepository implements AlbumDataSource {

    public static final String TAG = AlbumRepository.class.getSimpleName();

    private static volatile AlbumRepository mInstance;
    /**
     * Loader的唯一ID号
     */
    private final static int IMAGE_LOADER_ID = 1000;

    Map<String, AlbumFolder> mCachedFolders;
    /**
     * 用户选择的图片路径集合
     */
    private List<String> mSelectedResult = new ArrayList<>();

    /**
     * 包涵所有图片的相册名  综合的相册名
     */
    private String mGeneralFolderName;

    private CursorLoader mLoader;

    public static AlbumRepository getInstance(Context context) {
        if (mInstance == null) {
            synchronized (AlbumRepository.class) {
                if (mInstance == null) {
                    mInstance = new AlbumRepository(context.getApplicationContext());
                    return mInstance;
                }
            }
        }
        return mInstance;
    }

    public AlbumRepository(Context context) {

        String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media._ID};
        mGeneralFolderName = context.getString(R.string.label_general_folder_name);
        mLoader = new CursorLoader(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGE_PROJECTION, null,
                null, IMAGE_PROJECTION[2] + " DESC");
    }

    //非空注解，参数都不能为空
    @Override
    public void initImgRepository(@NonNull LoaderManager loaderManager,
                                  @NonNull final InitAlbumCallback callback) {
        checkNotNull(loaderManager);
        checkNotNull(callback);
        if (mCachedFolders != null) {  //如果缓存可用，则立即响应
            callback.onInitFinish(getCachedAlbumFolder());
            return;
        }

        loaderManager.initLoader(IMAGE_LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return mLoader;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                List<AlbumFolder> albumFolders = new ArrayList<>();
                if (data == null) return;
                if (data.getCount() <= 0) {
                    callback.onDataNoAvaliable();
                    return;
                }
                //创建包涵所有图片的相册目录
                AlbumFolder generalAlbumFolder = new AlbumFolder();
                ArrayList<ImageInfo> imgList = new ArrayList<>();
                generalAlbumFolder.setImgInfos(imgList);
                generalAlbumFolder.setFloderName(mGeneralFolderName);
                albumFolders.add(generalAlbumFolder);

                while (data.moveToNext()) {
                    ImageInfo imageInfo = createImageInfo(data);
                    generalAlbumFolder.getImgInfos().add(imageInfo); //每一张图片都加入到allAlbumFolder 目录中

                    File folderFile = new File(imageInfo.getPath()).getParentFile(); //得到当前图片的目录
                    String path = folderFile.getAbsolutePath();
                    AlbumFolder albumFloder = getFloderByPath(path, albumFolders);
                    if (albumFloder == null) {
                        //相册集合中不存在，则创建该相册目录，并添加到集合中
                        albumFloder = new AlbumFolder();
                        albumFloder.setCover(imageInfo);
                        albumFloder.setFloderName(folderFile.getName());
                        albumFloder.setPath(path);
                        ArrayList<ImageInfo> imageInfos = new ArrayList<>();
                        imageInfos.add(imageInfo);
                        albumFloder.setImgInfos(imageInfos);
                        albumFolders.add(albumFloder);
                    } else {
                        albumFloder.getImgInfos().add(imageInfo);
                    }
                }
                KLog.i(TAG, "========nLoadFinished :" + albumFolders.size());

                generalAlbumFolder.setCover(generalAlbumFolder.getImgInfos().get(0));
                callback.onInitFinish(albumFolders);
                processLoadedAlbumFolder(albumFolders);
            }


            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        });
    }

    private void processLoadedAlbumFolder(List<AlbumFolder> folders) {
        if (folders == null) {
            mCachedFolders = null;
            return;
        }
        if (mCachedFolders == null) {
            mCachedFolders = new LinkedHashMap<>();
        }
        mCachedFolders.clear();
        for (AlbumFolder task : folders) {
            mCachedFolders.put(task.getPath(), task);
        }
    }

    public List<AlbumFolder> getCachedAlbumFolder() {
        return mCachedFolders == null ? null : new ArrayList<>(mCachedFolders.values());
    }

    /**
     * 根据传入的路径得到AlbumFloder对象
     *
     * @param path 文件路径
     * @return 如果mAlbumFloders集合中存在 改path路径的albumFolder则返回AlbumFloder ，否则返回null
     */
    private AlbumFolder getFloderByPath(String path, List<AlbumFolder> albumFolders) {
        AlbumFolder folder = null;
        if (albumFolders != null) {
            for (AlbumFolder floder : albumFolders) {
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

    public void clearCacheAndSelect() {
        mSelectedResult.clear();
        mCachedFolders = null;
    }
}
