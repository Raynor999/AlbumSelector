package io.github.lijunguan.imgselector.selectimage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import io.github.lijunguan.imgselector.R;
import io.github.lijunguan.imgselector.base.BaseActivity;
import io.github.lijunguan.imgselector.data.AlbumRepository;
import io.github.lijunguan.imgselector.utils.ActivityUtils;
import io.github.lijunguan.imgselector.utils.KLog;


public class SelectActivity extends BaseActivity {

    public static final String TAG = SelectActivity.class.getSimpleName();

    private SelectPresenter mAlbumPresenter;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        SelectFragment albumFragment;

        if (savedInstanceState != null) {   //内存重启时调用   解决 内存重启时可能发生的Fragment重叠异常
            albumFragment =
                    (SelectFragment) getSupportFragmentManager().findFragmentByTag(SelectFragment.TAG);

        } else {
            //创建AlbumFragment
            albumFragment = SelectFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), albumFragment, SelectFragment.TAG);

        }
        AlbumRepository albumRepository = AlbumRepository.getInstance(this);
        //创建AlbumPresenter
        mAlbumPresenter = new SelectPresenter(
                albumRepository,
                getSupportLoaderManager(),
                albumFragment);
    }


    @Override
    protected void onSubmitBtnClick() {
        mAlbumPresenter.returnResult();
    }


    @Override
    public void onBackPressed() {
        SelectFragment albumFragment =
                (SelectFragment) getSupportFragmentManager().findFragmentByTag(SelectFragment.TAG);

        if (albumFragment == null) {
            super.onBackPressed();
            return;
        }

        if (albumFragment.isHidden()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .show(albumFragment)
                    .commit();
        } else if (albumFragment.mFab.getVisibility() != View.VISIBLE) {
            albumFragment.hideFolderList();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KLog.d(TAG, "=======onDestroy========");
        fixInputMethodManagerLeak(this);
    }



}
