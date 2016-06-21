package io.github.lijunguan.imgselector.data;

import android.app.Activity;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.lijunguan.imgselector.R;
import io.github.lijunguan.imgselector.data.entity.AlbumFolder;
import io.github.lijunguan.imgselector.data.entity.ImageInfo;
import io.github.lijunguan.imgselector.utils.KLog;

import static io.github.lijunguan.imgselector.utils.CheckUtils.checkNotNull;


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

    /**
     * 缓存所有相册目录
     */
    Map<String, AlbumFolder> mCachedFolders;


    AlbumFolder mSelectedAlbum;

    /**
     * 用户选择的图片路径集合
     */
    private List<String> mSelectedResult = new ArrayList<>();

    /**
     * 包涵所有图片的相册名  综合的相册名
     */
    private String mMainFolderName;


    private final Context mAppContext;


    private final static String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media._ID};


    public static AlbumRepository getInstance(Activity activity) {
        if (mInstance == null) {
            synchronized (AlbumRepository.class) {
                if (mInstance == null) {
                    mInstance = new AlbumRepository(activity);
                    return mInstance;
                }
            }
        }
        return mInstance;
    }


    public AlbumRepository(Context context) {
        mMainFolderName = context.getString(R.string.label_general_folder_name);
        mAppContext = context.getApplicationContext();

    }


    //非空注解，参数都不能为空
    @Override
    public void loadImages(@NonNull LoaderManager loaderManager,
                           @NonNull final InitAlbumCallback callback) {

        checkNotNull(loaderManager);
        checkNotNull(callback);
        if (mCachedFolders != null) {  //如果缓存可用，则立即响应
            callback.onInitFinish(getCachedAlbumFolders());
            KLog.d("load cacehd iamgeInfos");
            return;
        }

        loaderManager.initLoader(IMAGE_LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(mAppContext, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION, null,
                        null, IMAGE_PROJECTION[2] + " DESC");
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                List<AlbumFolder> albumFolders = new ArrayList<>();
                if (data == null) return;
                if (data.getCount() <= 0) {
                    callback.onDataNoAvaliable();
                    return;
                }

                while (data.moveToNext()) {
                    ImageInfo imageInfo = createImageInfo(data);
                    //得到当前图片的目录
                    File folderFile = new File(imageInfo.getPath()).getParentFile();
                    String path = folderFile.getAbsolutePath();
                    AlbumFolder albumFloder = getFloderByPath(path, albumFolders);

                    //相册不存在，则创建该相册目录，并添加到相册目录集合中
                    if (albumFloder == null) {
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
                //创建一个包涵所有相册的 主相册
                albumFolders.add(0, createMainAlbum(data, albumFolders));
                //缓存数据
                processAlbumData(albumFolders);
                KLog.d("==========onLoadFinished:"+albumFolders.size());
                callback.onInitFinish(getCachedAlbumFolders());
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        });
    }


    @Override
    public List<String> getSelectedResult() {
        return mSelectedResult;
    }


    public AlbumFolder getSelectedAlbum() {
        return mSelectedAlbum;
    }

    @Override
    public void selectedImage(@NonNull ImageInfo imageInfo) {
        checkNotNull(imageInfo);
        imageInfo.setSelected(true);
        selectedImage(imageInfo.getPath());
    }

    @Override
    public void unSelectedImage(@NonNull ImageInfo imageInfo) {
        imageInfo.setSelected(false);
        mSelectedResult.remove(checkNotNull(imageInfo).getPath());
    }

    @Override
    public int getSelectedCount() {
        return mSelectedResult.size();
    }


    public void clearAlbumRepository() {
        mSelectedResult.clear();
        mCachedFolders = null;
    }

    @Override
    public void updateFolder(AlbumFolder folder) {
        checkNotNull(folder);
        if (mCachedFolders.containsValue(folder)) {
            mSelectedAlbum = folder;
        }
    }

    public void selectedImage(String path) {
        mSelectedResult.add(checkNotNull(path));
    }


    @NonNull
    private AlbumFolder createMainAlbum(Cursor data, List<AlbumFolder> albumFolders) {
        AlbumFolder mainAlbumFolder = new AlbumFolder();
        mainAlbumFolder.setFloderName(mMainFolderName);
        ArrayList<ImageInfo> imgList = new ArrayList<>(data.getCount());
        for (AlbumFolder albumFolder : albumFolders) {
            imgList.addAll(albumFolder.getImgInfos());
        }
        mainAlbumFolder.setImgInfos(imgList);
        mainAlbumFolder.setCover(imgList.get(0));
        mainAlbumFolder.setPath(System.currentTimeMillis() + "main_album_folder");
        return mainAlbumFolder;
    }


    private void processAlbumData(List<AlbumFolder> folders) {
        if (folders == null || folders.isEmpty()) {
            mCachedFolders = null;
            return;
        }
        if (mCachedFolders == null) {
            mCachedFolders = new LinkedHashMap<>();
        }
        mCachedFolders.clear();
        for (AlbumFolder folder : folders) {
            mCachedFolders.put(folder.getPath(), folder);
        }
        //初始化默认选择的相册为MainAlbumFolder
        mSelectedAlbum = folders.get(0);
    }

    public List<AlbumFolder> getCachedAlbumFolders() {
        return mCachedFolders == null ?
                Collections.<AlbumFolder>emptyList() :
                new ArrayList<>(mCachedFolders.values());
    }


    /**
     * 根据传入的路径得到AlbumFloder对象
     *
     * @param path 文件路径
     * @return 如果mAlbumFloders集合中存在 改path路径的albumFolder则返回AlbumFloder ，否则返回null
     */
    private AlbumFolder getFloderByPath(String path, @NonNull List<AlbumFolder> albumFolders) {
        checkNotNull(albumFolders);
        for (AlbumFolder floder : albumFolders) {
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
}
