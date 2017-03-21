package com.example.wen.filemanager;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by wen on 2017/2/25.
 */

public class MyPagerAdapter extends PagerAdapter {
    private List<View> viewLists;
    private List<String> titles;

    public MyPagerAdapter(List<View> lists,List<String> titles){
        this.viewLists=lists;
        this.titles=titles;
    }

    @Override
    public int getCount() {
        return viewLists.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewLists.get(position));
        return viewLists.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewLists.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
