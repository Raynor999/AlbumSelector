package io.github.lijunguan.album.base;

/**
 * Created by lijunguan on 2016/4/19.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 */
public interface BasePresenter {
    /**
     * 一般在onResume（）方法中调用，执行一些数据初始化工作
     */
    void start();
}
