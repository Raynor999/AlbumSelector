package io.github.lijunguan.album.presenter_view;

import java.util.List;

import io.github.lijunguan.album.base.BaseContract;
import io.github.lijunguan.album.model.entity.ImageInfo;

/**
 * Created by lijunguan on 2016/4/24.
 */
public interface ImageDetailContract {

    interface View extends BaseContract.BaseView<Presenter> {

        void updateIndicator();

        void showImageDetail(int position, List<ImageInfo> imageInfos);

    }

    interface Presenter extends BaseContract.BasePresenter {

    }

}
