package io.github.lijunguan.albumselector;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import io.github.lijunguan.imgselector.utils.KLog;

/**
 * Created by lijunguan on 2016/4/28.
 * emial: lijunguan199210@gmail.com
 * blog: https://lijunguan.github.io
 */
public class App extends Application {

    private RefWatcher mRrefWatcher;

    public RefWatcher getRrefWatcher() {
        return mRrefWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRrefWatcher = LeakCanary.install(this);
        KLog.d("Aplication========onCreate=========");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mRrefWatcher = LeakCanary.install(this);
        KLog.d("Aplication========onTerminate=========");

    }
}
