package io.github.lijunguan.album.base;

/**
 * Created by lijunguan on 2016/4/19.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 */
public interface BaseView<T> {
    /**
     * 给View（Fragment，Activity 视图UI）设置Presenter
     * @param presenter
     */
    void setPresenter(T presenter);
}
