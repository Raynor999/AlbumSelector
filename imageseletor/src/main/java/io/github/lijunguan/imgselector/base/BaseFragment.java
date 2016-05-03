package io.github.lijunguan.imgselector.base;

import android.content.Context;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;

import io.github.lijunguan.imgselector.album.AlbumActivity;

/**
 * Created by lijunguan on 2016/4/8
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 */
public class BaseFragment extends Fragment {
    protected AlbumActivity mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //在Fragment中用 弱引用的方式持有一份Activity 引用，方便在Fragment中使用Context
        if (context instanceof AlbumActivity) {
            WeakReference<AlbumActivity> mActivityRef = new WeakReference<>((AlbumActivity) getActivity());
            mContext = mActivityRef.get();
        } else {
            throw new IllegalArgumentException("unexcepted context ");
        }
    }

}
