package io.github.lijunguan.album.view;

import java.util.List;

import io.github.lijunguan.album.model.entity.AlbumFloder;


/**
 * Created by lijunguan on 2016/4/8
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 */
public interface AlbumView {
    /**
     * 根据相册集合数据，绑定数据到View上，初始化页面
     * @param data
     */
    void bindAlbumData(List<AlbumFloder> data);

    /**
     * 切换相册目录时，重新刷新View
     * @param floder
     */
    void switchAlbumFolder(AlbumFloder floder);



    void showCarmeraAction();
}
