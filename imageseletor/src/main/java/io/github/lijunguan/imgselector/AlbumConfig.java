package io.github.lijunguan.imgselector;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;

/**
 * Created by lijunguan on 2016/4/21.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 *
 * 图片选择器配置
 */
public class AlbumConfig implements Parcelable {

    public static final int DEFAULT_MAX_COUNT = 9;
    /**
     * 选择模式， SINGLE_MODE = 0 ，MULTI_MODE = 1
     */
    private int mSelectModel;
    /**
     * 图片最大可选择数量
     */
    private int mMaxCount;
    /**
     * 是否显示相机
     */
    private boolean mShownCamera;

    /**
     * grid 的列数
     */
    private int mGridColumns;

    @ColorInt
    private int mToolbarColor = -1;


    public int getSelectModel() {
        return mSelectModel;
    }

    public void setSelectModel(int mSlecteModel) {
        this.mSelectModel = mSlecteModel;
    }

    public int getMaxCount() {
        return mMaxCount;
    }

    public void setMaxCount(int mMaxCount) {
        this.mMaxCount = mMaxCount;
    }

    public boolean isShownCamera() {
        return mShownCamera;
    }

    public void setShownCamera(boolean mShownCamera) {
        this.mShownCamera = mShownCamera;
    }

    public int getGridColumns() {
        return mGridColumns;
    }

    public void setGridColumns(int mGridColumns) {
        this.mGridColumns = mGridColumns;
    }

    public int getToolbarColor() {
        return mToolbarColor;
    }

    public void setToolbarColor(@ColorInt int mToolbarColor) {
        this.mToolbarColor = mToolbarColor;
    }

    public AlbumConfig() {
        mMaxCount = DEFAULT_MAX_COUNT;
        mShownCamera = true;
        mSelectModel = ImageSelector.MULTI_MODE;
        mGridColumns = 3;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mSelectModel);
        dest.writeInt(this.mMaxCount);
        dest.writeByte(mShownCamera ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mGridColumns);
        dest.writeInt(this.mToolbarColor);
    }

    protected AlbumConfig(Parcel in) {
        this.mSelectModel = in.readInt();
        this.mMaxCount = in.readInt();
        this.mShownCamera = in.readByte() != 0;
        this.mGridColumns = in.readInt();
        this.mToolbarColor = in.readInt();
    }

    public static final Creator<AlbumConfig> CREATOR = new Creator<AlbumConfig>() {
        @Override
        public AlbumConfig createFromParcel(Parcel source) {
            return new AlbumConfig(source);
        }

        @Override
        public AlbumConfig[] newArray(int size) {
            return new AlbumConfig[size];
        }
    };
}

