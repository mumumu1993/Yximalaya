package com.yuqingsen.yximalaya.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
;import com.yuqingsen.yximalaya.utils.FragmentCreator;

public class MainContentAdapter extends FragmentPagerAdapter {

    public MainContentAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return FragmentCreator.getFragment(i);
    }

    @Override
    public int getCount() {
        return FragmentCreator.PAGE_COUNT;
    }
}
