package com.rc.nowtv.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rc.nowtv.fragments.HomeFragment;
import com.rc.nowtv.fragments.LoginFragment;

/**
 * Created by berg on 29/05/17.
 */

public class SectionsPageAdapter extends FragmentPagerAdapter {
    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                LoginFragment loginFragment = new LoginFragment();
                return loginFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
