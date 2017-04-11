package com.vest.album.base;

import android.support.v4.app.Fragment;

/**
 * Created by Administrator on 2017/4/10.
 */
public abstract class BaseMediaFragment extends Fragment {

    public abstract void addNewEntry(String url, String name);
}
