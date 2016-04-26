package io.github.lijunguan.imgselector.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import java.lang.ref.WeakReference;

import io.github.lijunguan.imgselector.album.AlbumActivity;
import io.github.lijunguan.imgselector.utils.KLog;

/**
 * Created by lijunguan on 2016/4/8
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 *
 *
 */
public class BaseFragment extends Fragment {

    public static final String TAG = "life_cycle";

    protected AlbumActivity mContext;

    private WeakReference<AlbumActivity> mActivityRef;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //在Fragment中用 弱引用的方式持有一份Activity 引用，方便在Fragment中使用Context
        if (context instanceof AlbumActivity) {
            mActivityRef = new WeakReference<>((AlbumActivity) getActivity());
            mContext = mActivityRef.get();
        } else {
            throw new IllegalArgumentException("unexcepted context ");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KLog.d(TAG, "====Fragment=====onCreate==========");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        KLog.d(TAG, "===Fragment======onViewCreated==========");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        KLog.d(TAG, "====Fragment=====onActivityCreated==========");

    }

    @Override
    public void onResume() {
        super.onResume();
        KLog.d(TAG, "===Fragment======onResume==========");
    }

    @Override
    public void onPause() {
        super.onPause();
        KLog.d(TAG, "====Fragment=====onPause==========");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        KLog.d(TAG, "====Fragment=====onDestroyView==========");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KLog.d(TAG, "====Fragment=====onDestroy==========");
    }
}
