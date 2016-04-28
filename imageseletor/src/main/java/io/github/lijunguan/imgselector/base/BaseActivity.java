package io.github.lijunguan.imgselector.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import io.github.lijunguan.imgselector.ImageSelector;
import io.github.lijunguan.imgselector.R;
import io.github.lijunguan.imgselector.utils.KLog;
import io.github.lijunguan.imgselector.utils.StatusBarUtil;

/**
 * Created by lijunguan on 2016/4/8
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 */

public class BaseActivity extends AppCompatActivity {

    protected Toolbar mToolbar;

    public static final String TAG = "life_cycle";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        KLog.d(TAG, "=======Activity=========onCreate===========");
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        trySetToolBar();
        setStatusBar();
    }

    private void trySetToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            ActionBar ab = getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

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
    protected void onStart() {
        super.onStart();
        KLog.d(TAG, "======Activity==========onStart===========");

    }

    @Override
    protected void onResume() {
        super.onResume();
        KLog.d(TAG, "======Activity==========onResume===========");
    }


    @Override
    protected void onPause() {
        super.onPause();
        KLog.d(TAG, "======Activity==========onPause===========");

    }

    @Override
    protected void onStop() {
        super.onStop();
        KLog.d(TAG, "======Activity==========onStop===========");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KLog.d(TAG, "=====Activity===========onDestroy===========");
    }


}
