package io.github.lijunguan.album.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import io.github.lijunguan.album.utils.KLog;

/**
 * Created by lijunguan on 2016/4/8
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 */

public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "life_cycle";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KLog.d(TAG,"=======Activity=========onCreate===========");
    }

    @Override
    protected void onStart() {
        super.onStart();
        KLog.d(TAG,"======Activity==========onStart===========");

    }

    @Override
    protected void onResume() {
        super.onResume();
        KLog.d(TAG,"======Activity==========onResume===========");
    }



    @Override
    protected void onPause() {
        super.onPause();
        KLog.d(TAG,"======Activity==========onPause===========");

    }

    @Override
    protected void onStop() {
        super.onStop();
        KLog.d(TAG,"======Activity==========onStop===========");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KLog.d(TAG,"=====Activity===========onDestroy===========");
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


}
