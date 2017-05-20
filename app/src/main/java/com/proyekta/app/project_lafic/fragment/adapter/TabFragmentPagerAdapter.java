package com.proyekta.app.project_lafic.fragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.proyekta.app.project_lafic.fragment.TabFragmentFoundItem;
import com.proyekta.app.project_lafic.fragment.TabFragmentLostItem;

/**
 * Created by WINDOWS 10 on 13/05/2017.
 */

public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

    String[] title = new String[]{
            "Lost Items", "Found Items"
    };

    public TabFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new TabFragmentLostItem();
                break;
            case 1:
                fragment = new TabFragmentFoundItem();
                break;
            default:
                fragment = null;
                break;
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    @Override
    public int getCount() {
        return title.length;
    }
}
