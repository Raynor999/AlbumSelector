package io.github.lijunguan.album.base;

import io.github.lijunguan.album.model.entity.ImageInfo;

/**
 * Created by lijunguan on 2016/4/22.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 */
public interface BaseContract {

    interface BaseView<T> {
        /**
         * 给View（Fragment，Activity 视图UI）设置Presenter
         *
         * @param presenter
         */
        void setPresenter(T presenter);

        void restoreChecbox(int position);

        void showSelectedCount(int count);

        /**
         * 用来显示提示信息，用Snackbar实现
         *
         * @param message 提示的消息内容
         */
        void showToast(CharSequence message);
    }

    interface BasePresenter {
        /**
         * 一般在onResume（）方法中调用，执行一些数据初始化工作
         */
        void start();

        void selectImage(ImageInfo imageInfo, int maxCount, int position);

        void unSelectImage(ImageInfo imageInfo);
    }


}
