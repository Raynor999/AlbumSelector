package io.github.lijunguan.imgselector.selectimage;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import java.lang.reflect.Field;

import io.github.lijunguan.imgselector.R;
import io.github.lijunguan.imgselector.base.BaseActivity;
import io.github.lijunguan.imgselector.data.AlbumRepository;
import io.github.lijunguan.imgselector.previewimage.ImageDetailFragment;
import io.github.lijunguan.imgselector.utils.ActivityUtils;
import io.github.lijunguan.imgselector.utils.KLog;


public class SelectActivity extends BaseActivity {

    public static final String TAG = SelectActivity.class.getSimpleName();

    private SelectPresenter mAlbumPresenter;

    private Button mSubmitBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        initViews();

        SelectFragment albumFragment;
        ImageDetailFragment imageDetailFragment;

        if (savedInstanceState != null) {   //内存重启时调用   解决 内存重启时可能发生的Fragment重叠异常
            imageDetailFragment =
                    (ImageDetailFragment) getSupportFragmentManager().findFragmentByTag(ImageDetailFragment.TAG);
            albumFragment =
                    (SelectFragment) getSupportFragmentManager().findFragmentByTag(SelectFragment.TAG);

            if (imageDetailFragment != null && albumFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .hide(albumFragment)
                        .show(imageDetailFragment)
                        .commit();
            }
        } else {
            //创建AlbumFragment

            albumFragment = SelectFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), albumFragment, SelectFragment.TAG, false);

        }
        AlbumRepository albumRepository = AlbumRepository.getInstance(this);
        //创建AlbumPresenter
        mAlbumPresenter = new SelectPresenter(
                albumRepository,
                getSupportLoaderManager(),
                albumFragment);
    }


    private void initViews() {
        mSubmitBtn = (Button) mToolbar.findViewById(R.id.btn_submit);
        mSubmitBtn.setEnabled(false);
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlbumPresenter.returnResult();
            }
        });
    }


    public void setSubmitBtnText(CharSequence text, boolean enabled) {
        mSubmitBtn.setText(text);
        mSubmitBtn.setEnabled(enabled);
    }

    public void setToolbarTitle(CharSequence title) {
        mToolbar.setTitle(title);
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
        mAlbumPresenter.clearCache();
        fixInputMethodManagerLeak(this);
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        KLog.d("=========onConfigurationChanged===========");
    }



    public static void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        String [] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f = null;
        Object obj_get = null;
        for (int i = 0;i < arr.length;i ++) {
            String param = arr[i];
            try{
                f = imm.getClass().getDeclaredField(param);
                if (f.isAccessible() == false) {
                    f.setAccessible(true);
                } // author: sodino mail:sodino@qq.com
                obj_get = f.get(imm);
                if (obj_get != null && obj_get instanceof View) {
                    View v_get = (View) obj_get;
                    if (v_get.getContext() == destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的
                        f.set(imm, null); // 置空，破坏掉path to gc节点
                    } else {
                        // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                        break;
                    }
                }
            }catch(Throwable t){
                t.printStackTrace();
            }
        }
    }
}
