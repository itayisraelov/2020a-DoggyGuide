package com.technion.doggyguide.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.technion.doggyguide.R;
import com.technion.doggyguide.homeScreen.ChatFragment;
import com.technion.doggyguide.homeScreen.EventsFragment;
import com.technion.doggyguide.homeScreen.HomeFragment;
import com.technion.doggyguide.homeScreen.NotificationsFragment;

public class HomeSectionsPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.home_tab_text_1,
            R.string.home_tab_text_2, R.string.home_tab_text_3, R.string.home_tab_text_4};


    private final Context mContext;

    public HomeSectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = HomeFragment.newInstance("param1", "param2");
                break;
            case 1:
                fragment = EventsFragment.newInstance("param1", "param2");
                break;
            case 2:
                fragment = ChatFragment.newInstance("param1", "param2");
                break;
            case 3:
                fragment = NotificationsFragment.newInstance("param1", "param2");
                break;
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }


    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}