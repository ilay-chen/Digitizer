package com.icstudios.digitizer;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List mFragmentList = new ArrayList<>();
    private final List mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager)
    {
      super(manager);
}

    @Override
    public Fragment getItem(int position) {
        return (Fragment) mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (CharSequence) mFragmentTitleList.get(position);
    }
}