package com.icstudios.digitizer;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class testFragment extends Fragment {

    ViewMaker vm;
    ViewPager mViewPager;
    ViewPager2 mViewPager2;
    ArrayList<LinearLayout> allLayouts;
    String []titles;
    String currentTopic;

        public testFragment() {
        }

        public static testFragment newInstance(int sectionNumber) {
            testFragment fragment = new testFragment();
            Bundle args = new Bundle();
            args.putInt("", sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //View rootView = inflater.inflate(R.layout.test_fragment, container, false);
            currentTopic = getArguments().getString("data");
            vm = new ViewMaker(getContext(), currentTopic);

            if(mViewPager!=null) mViewPager.removeAllViews();

            mViewPager = ((Activity)getContext()).findViewById(R.id.viewPager2);

            TabLayout tabLayout = (TabLayout) ((Activity)getContext()).findViewById(R.id.tabDots);
            tabLayout.setupWithViewPager(mViewPager, true);

            ScrollView []svs = new ScrollView[vm.allLayouts.size()];
            allLayouts = vm.getViews();
            titles = new String[allLayouts.size()];

            for(int i = 0; i <allLayouts.size(); i++)
                titles[i] = "";


            mViewPager.setAdapter(new PagerAdapter() {

                @Override
                public Object instantiateItem(ViewGroup container, int position) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    //ViewGroup layout = (ViewGroup) inflater.inflate(layouts[position], container, false);

                    ViewGroup layout = (ViewGroup)  allLayouts.get(position);

                    de(layout);
                    container.addView(layout);
                    return layout;
                }

                @Override
                public void destroyItem(ViewGroup container, int position, Object object) {
                    container.removeView((View)object);
                }

                @Override
                public CharSequence getPageTitle(int position) {
                    return titles[position];
                }

                @Override
                public int getCount() {
                    return allLayouts.size();
                }

                @Override
                public boolean isViewFromObject(View view, Object object) {
                    return view == object;
                }
            });


            mViewPager.setOffscreenPageLimit(10);
            mViewPager.setCurrentItem(currentTab(currentTopic), false);
            //container.addView(vm.drawPage());
            //ImageView im = (ImageView) rootView.findViewById(R.id.mainnews);
            //im.getLayoutParams().height = 23;
            //im.setScaleType(ImageView.ScaleType.FIT_XY);
            //return vm.drawPage();
            return null;
        }

        public void de(View v)
        {
            if(v.getParent()!=null)
                ((ViewGroup)v.getParent()).removeView(v);
            //for (int i = 0; i < container.getChildCount();i++) {
            //    if(container.getChildAt(i).equals(v))
            //        container.removeView(v);
            //}
        }

        public int currentTab(String currentTopic)
        {
            topicTasks tt =  appData.allTasks.getTopicById(currentTopic);
            int i = 0;
            for(; i < tt.tasks.size(); i++)
            {
               if(!tt.tasks.get(i).getDone())
                   return i;
            }
            return i;
        }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /*
        if (mViewPager != null) {
            ViewGroup parentViewGroup = (ViewGroup) mViewPager.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViewsInLayout();;
            }
        }
        if (mViewPager != null) {
            mViewPager.removeAllViews();
            ViewGroup parentViewGroup = (ViewGroup) mViewPager.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViews();
            }
        }
        */
    }
}

