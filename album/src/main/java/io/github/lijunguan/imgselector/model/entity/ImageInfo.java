package io.github.lijunguan.imgselector.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lijunguan on 2016/4/8
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 */
public class ImageInfo implements Parcelable {
    /**
     * 图片文件的名字
     */
    private String mDisplayName;
    /**
     * 文件被添加到 media provider的时间 ，单位是 从1970年开始的毫秒数
     */
    private long mAddedTime;
    /**
     * 文件存储路径
     */
    private String mPath;
    /**
     * 图片大小
     */
    private long mSize;
    /**
     * 选择状态
     */
    private boolean isSelected;

    public ImageInfo(String mPath, String mDisplayName, long mAddedTime, long mSize) {
        this.mDisplayName = mDisplayName;
        this.mAddedTime = mAddedTime;
        this.mPath = mPath;
        this.mSize = mSize;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long mSize) {
        this.mSize = mSize;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String mDisplayName) {
        this.mDisplayName = mDisplayName;
    }

    public long getAddedTime() {
        return mAddedTime;
    }

    public void setAddedTime(long mAddedTime) {
        this.mAddedTime = mAddedTime;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public ImageInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDisplayName);
        dest.writeLong(this.mAddedTime);
        dest.writeString(this.mPath);
        dest.writeLong(this.mSize);
        dest.writeByte(isSelected ? (byte) 1 : (byte) 0);
    }

    protected ImageInfo(Parcel in) {
        this.mDisplayName = in.readString();
        this.mAddedTime = in.readLong();
        this.mPath = in.readString();
        this.mSize = in.readLong();
        this.isSelected = in.readByte() != 0;
    }

    public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
        @Override
        public ImageInfo createFromParcel(Parcel source) {
            return new ImageInfo(source);
        }

        @Override
        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };
}
