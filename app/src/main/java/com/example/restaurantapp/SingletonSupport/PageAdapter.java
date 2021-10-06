package com.example.restaurantapp.SingletonSupport;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.restaurantapp.R;

import java.util.ArrayList;
import java.util.List;

/*
    This is the page adapter
    this is for storing the different fragment activities
 */

public class PageAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitle = new ArrayList<>();

    @Nullable
    public void addFragment(Fragment fragment, String title)
    {
        fragmentList.add(fragment);
        fragmentTitle.add(title);
    }
    public CharSequence getPageTitle(int position) {
        return fragmentTitle.get(position);
    }

    public PageAdapter(FragmentManager fragmentManager)
    {

        super(fragmentManager);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
