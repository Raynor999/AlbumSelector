package io.github.lijunguan.album.view;

import io.github.lijunguan.album.entity.ImageInfo;

/**
 * Created by lijunguan on 2016/4/18
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 */
public interface SelectedImgView {
    /**
     *当图片选择状态变化时，更新UI
     * @param imageInfo 图片bean
     */
    void updateSelectedCount(ImageInfo imageInfo);

}
