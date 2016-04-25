package io.github.lijunguan.imgselector.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by lijunguan on 2016/4/8
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 */
public class AlbumFolder implements Parcelable {
    /**
     * 相册目录的路径
     */
    private String mPath;
    /**
     * 相册目录名
     */
    private String mFloderName;
    /**
     * 目录下的所有图片集合
     */
    private List<ImageInfo> mImgInfos;
    /**
     * 目录封面
     */
    private ImageInfo mCover;

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public String getFloderName() {
        return mFloderName;
    }

    public void setFloderName(String mFloderName) {
        this.mFloderName = mFloderName;
    }

    public List<ImageInfo> getImgInfos() {
        return mImgInfos;
    }

    public void setImgInfos(List<ImageInfo> mImgInfos) {
        this.mImgInfos = mImgInfos;
    }

    public ImageInfo getCover() {
        return mCover;
    }

    public void setCover(ImageInfo mCover) {
        this.mCover = mCover;
    }

    public AlbumFolder() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mPath);
        dest.writeString(this.mFloderName);
        dest.writeTypedList(mImgInfos);
        dest.writeParcelable(this.mCover, flags);
    }

    protected AlbumFolder(Parcel in) {
        this.mPath = in.readString();
        this.mFloderName = in.readString();
        this.mImgInfos = in.createTypedArrayList(ImageInfo.CREATOR);
        this.mCover = in.readParcelable(ImageInfo.class.getClassLoader());
    }

    public static final Creator<AlbumFolder> CREATOR = new Creator<AlbumFolder>() {
        @Override
        public AlbumFolder createFromParcel(Parcel source) {
            return new AlbumFolder(source);
        }

        @Override
        public AlbumFolder[] newArray(int size) {
            return new AlbumFolder[size];
        }
    };
}
