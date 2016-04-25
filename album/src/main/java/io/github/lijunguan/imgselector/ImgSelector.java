package io.github.lijunguan.imgselector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import static io.github.lijunguan.imgselector.utils.CommonUtils.checkNotNull;

/**
 * Created by lijunguan on 2016/4/21.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 */
public class ImgSelector {


    public static final String SELECTED_RESULT = "selected_result";

    public static final int REQUEST_SELECT_IMAGE = 1024;

    public static final int REQUEST_OPEN_CAMERA = 2048;

    public static final String ARG_ALBUM_CONFIG = "albumConfig";
    /**
     * 单选模式
     */
    public static final int SINGLE_MODEL = 0;
    /**
     * 多选模式
     */
    public static final int MULTI_MODEL = 1;


    private  AlbumConfig mConfig;


    private static ImgSelector ourInstance = new ImgSelector();

    public static ImgSelector getInstance() {
        return ourInstance;
    }

    private ImgSelector() {
        mConfig = new AlbumConfig();
    }

    public ImgSelector setMaxCount(@NonNull int maxCount) {
        checkNotNull(maxCount);
        mConfig.setMaxCount(maxCount);
        return this;
    }

    public ImgSelector setSelectModel(@NonNull int model) {
        checkNotNull(model);
        mConfig.setSelectModel(model);
        return this;
    }

    public ImgSelector setShowCamera(@NonNull boolean shown) {
        checkNotNull(shown);
        mConfig.setShownCamera(shown);
        return this;
    }

    public void startSelect(@NonNull Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(ImgSelector.ARG_ALBUM_CONFIG, mConfig);
        context.startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }


    public void startSelect(@NonNull Context context) {
        if (context instanceof Activity) {
            startSelect((Activity) context);
        } else {
            throw new IllegalArgumentException("Require a Activity.class,but find a Context.class");
        }
    }

}
