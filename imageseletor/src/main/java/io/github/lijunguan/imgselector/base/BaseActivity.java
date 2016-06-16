package io.github.lijunguan.imgselector.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import java.lang.reflect.Field;

import io.github.lijunguan.imgselector.ImageSelector;
import io.github.lijunguan.imgselector.R;
import io.github.lijunguan.imgselector.utils.KLog;
import io.github.lijunguan.imgselector.utils.StatusBarUtil;

/**
 * Created by lijunguan on 2016/4/8
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 */

public abstract class BaseActivity extends AppCompatActivity {


    protected Toolbar mToolbar;

    private AlertDialog mAlertDialog;

    private Button mSubmitBtn;

    protected View mContentView;


    private final Handler nHandler = new Handler();

    private final Runnable mFullScreenRunnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };


    private final Runnable mBackFullScreenRunnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }

        }
    };

    private boolean isFullScreen = false;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        initToolbar();
        setStatusBar();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mContentView = getWindow().getDecorView().getRootView();
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mSubmitBtn = (Button) mToolbar.findViewById(R.id.btn_submit);
        mSubmitBtn.setEnabled(false);
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitBtnClick();
            }
        });

    }

    protected abstract void onSubmitBtnClick();

    public void setSubmitBtnText(CharSequence text, boolean enabled) {
        mSubmitBtn.setText(text);
        mSubmitBtn.setEnabled(enabled);
    }

    /**
     * 设置状态栏颜色，支持 API14 以上
     */
    protected void setStatusBar() {
        int toolBarColor = ImageSelector.getInstance().getConfig().getToolbarColor();
        if (toolBarColor != -1) {
           StatusBarUtil.setColor(this, toolBarColor);
            if (mToolbar != null)
                mToolbar.setBackgroundColor(toolBarColor);
        } else {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.primary));
        }

    }

    public void setToolbarTitle(CharSequence title) {
        mToolbar.setTitle(title);
    }

    /**
     * Android6.0 动态申请权限，如果这个permission已经被用户拒绝过，
     * 则弹出一个对话框，显示rationale内容（解释为什么要申请该权限），  否则 立即申请权限
     *
     * @param permission  要申请的权限
     * @param rationale   申请权限原因描述
     * @param requestCode
     */
    public void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            showAlertDialog(getString(R.string.permission_title_rationale), rationale,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(BaseActivity.this, new String[]{permission}, requestCode);
                        }
                    }, getString(R.string.label_ok), null, getString(R.string.label_cancel));
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }


    protected void showAlertDialog(@Nullable String title, @Nullable String message,
                                   @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                   @NonNull String positiveText,
                                   @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                   @NonNull String negativeText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener);
        mAlertDialog = builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    public void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f = null;
        Object obj_get = null;
        for (int i = 0; i < arr.length; i++) {
            String param = arr[i];
            try {
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
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        KLog.d("=========onConfigurationChanged===========");
    }

    public void fullScreenToggle() {
        if (isFullScreen) {
            backFullScreen();
        } else {
            starFullScreen();
        }
    }

    public void starFullScreen() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        isFullScreen = true;
        // Schedule a runnable to remove the status and navigation bar after a delay
        nHandler.removeCallbacks(mBackFullScreenRunnable);
        nHandler.postDelayed(mFullScreenRunnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    public void backFullScreen() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);


        isFullScreen = false;
        // Schedule a runnable to display UI elements after a delay
        nHandler.removeCallbacks(mFullScreenRunnable);
        nHandler.postDelayed(mBackFullScreenRunnable, UI_ANIMATION_DELAY);
    }
}
