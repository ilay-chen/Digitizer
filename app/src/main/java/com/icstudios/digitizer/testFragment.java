package com.icstudios.digitizer;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Locale;

public class testFragment extends Fragment {

    ViewMaker vm;
    ViewPager mViewPager;
    ViewPager2 mViewPager2;
    ArrayList<LinearLayout> allLayouts;
    String []titles;
    String currentTopic;
    Button next, previous;

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                //tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
            tabLayout.setupWithViewPager(mViewPager, true);

            next = ((Activity)getContext()).findViewById(R.id.next);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mViewPager.getCurrentItem() + 1 < allLayouts.size())
                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                }
            });

            previous = ((Activity)getContext()).findViewById(R.id.previous);
            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mViewPager.getCurrentItem() - 1 >= 0)
                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                }
            });

            ScrollView []svs = new ScrollView[vm.allLayouts.size()];
            allLayouts = vm.getViews();
            titles = new String[allLayouts.size()];

            for(int i = 0; i <allLayouts.size(); i++)
                titles[i] = "";

            if (isRTL())
                mViewPager.setRotationY(180);

            mViewPager.setAdapter(new PagerAdapter() {

                @Override
                public Object instantiateItem(ViewGroup container, int position) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    //ViewGroup layout = (ViewGroup) inflater.inflate(layouts[position], container, false);

                    ViewGroup layout = (ViewGroup)  allLayouts.get(position);
                    if (isRTL())
                        layout.setRotationY(180);

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

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                public void onPageScrollStateChanged(int state) {

                }
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                public void onPageSelected(int position) {
                    setButton(position);
                }
            });

            mViewPager.setOffscreenPageLimit(10);
            mViewPager.setCurrentItem(currentTab(currentTopic), false);
            setButton(currentTab(currentTopic));

            //container.addView(vm.drawPage());
            //ImageView im = (ImageView) rootView.findViewById(R.id.mainnews);
            //im.getLayoutParams().height = 23;
            //im.setScaleType(ImageView.ScaleType.FIT_XY);
            //return vm.drawPage();
            return null;
        }

    public static boolean isRTL() {
        return isRTL(Locale.getDefault());
    }

    public void setButton(int position)
    {
        int last = 0, first = allLayouts.size() - 1;
        if (!isRTL()) {
            last = allLayouts.size() - 1;
            first = 0;
        }

        if (position == last) {
            next.setEnabled(false);
            previous.setEnabled(true);
        }
        else if (position == first) {
            next.setEnabled(true);
            previous.setEnabled(false);
        }
        else {
            next.setEnabled(true);
            previous.setEnabled(true);
        }
    }

    public static boolean isRTL(Locale locale) {
        final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
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

