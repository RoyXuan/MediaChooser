package com.vest.album.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.vest.album.base.BaseMediaFragment;
import com.vest.album.fragment.MediaAudioFragment;
import com.vest.album.fragment.MediaImageFragment;
import com.vest.album.fragment.MediaVideoFragment;

import java.util.ArrayList;
import java.util.List;

public class MediaFragmentViewPagerAdapter extends PagerAdapter implements
        ViewPager.OnPageChangeListener {
    private List<BaseMediaFragment> fragments; // 每个Fragment对应�?��Page
    private FragmentManager fragmentManager;
    //    private ViewPager viewPager; // viewPager对象
    private int currentPageIndex = 0; // 当前page索引（切换之前）
    private final List<String> mFragmentTitleList = new ArrayList<>();

    private OnExtraPageChangeListener onExtraPageChangeListener; // ViewPager切换页面时的额外功能添加接口

    public MediaFragmentViewPagerAdapter(FragmentManager fragmentManager, ViewPager viewPager, List<BaseMediaFragment> fragments) {
        this.fragments = fragments;
        this.fragmentManager = fragmentManager;
        viewPager.setAdapter(this);
        viewPager.addOnPageChangeListener(this);
        mFragmentTitleList.add("音频");
        mFragmentTitleList.add("视频");
        mFragmentTitleList.add("图片");
        for (int i = 0; i < fragments.size(); i++) {
            if (!fragments.get(i).isAdded()) { // 如果fragment还没有added
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.add(fragments.get(i), fragments.get(i).getClass().getSimpleName());
                ft.commit();
                fragmentManager.executePendingTransactions();
            }
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
            fragment = fragmentManager.findFragmentByTag(MediaAudioFragment.class.getSimpleName());
        else if (position == 1)
            fragment = fragmentManager.findFragmentByTag(MediaVideoFragment.class.getSimpleName());
        else
            fragment = fragmentManager.findFragmentByTag(MediaImageFragment.class.getSimpleName());
        return fragment;
    }

    public Fragment getItem(String SimpleName) {
        Fragment fragment = fragmentManager.findFragmentByTag(SimpleName);
        return fragment;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(fragments.get(position).getView()); // 移出viewpager两边之外的page布局
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = fragments.get(position);
        if (fragment.getView().getParent() == null) {
            container.addView(fragment.getView()); // 为viewpager增加布局
//            viewPager.setObjectForPosition(fragment.getView(), position);
        }
        return fragment.getView();
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public OnExtraPageChangeListener getOnExtraPageChangeListener() {
        return onExtraPageChangeListener;
    }

    public void setOnExtraPageChangeListener(
            OnExtraPageChangeListener onExtraPageChangeListener) {
        this.onExtraPageChangeListener = onExtraPageChangeListener;
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
        if (null != onExtraPageChangeListener) { // 如果设置了额外功能接�?
            onExtraPageChangeListener.onExtraPageScrolled(i, v, i2);
        }
    }

    @Override
    public void onPageSelected(int i) {
        fragments.get(currentPageIndex).onPause(); // 调用切换前Fargment的onPause()
        // fragments.get(currentPageIndex).onStop(); // 调用切换前Fargment的onStop()
        if (fragments.get(i).isAdded()) {
            // fragments.get(i).onStart(); // 调用切换后Fargment的onStart()
            fragments.get(i).onResume(); // 调用切换后Fargment的onResume()
        }
        currentPageIndex = i;

        if (null != onExtraPageChangeListener) { // 如果设置了额外功能接�?
            onExtraPageChangeListener.onExtraPageSelected(i);
        }

    }

    @Override
    public void onPageScrollStateChanged(int i) {
        if (null != onExtraPageChangeListener) { // 如果设置了额外功能接�?
            onExtraPageChangeListener.onExtraPageScrollStateChanged(i);
        }
    }

    /**
     * page切换额外功能接口
     */
    public static class OnExtraPageChangeListener {
        public void onExtraPageScrolled(int i, float v, int i2) {
        }

        public void onExtraPageSelected(int i) {
        }

        public void onExtraPageScrollStateChanged(int i) {
        }
    }

}
