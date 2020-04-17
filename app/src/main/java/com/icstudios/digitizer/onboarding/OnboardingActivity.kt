package com.icstudios.digitizer.onboarding;

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.icstudios.digitizer.R
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

/**
 * The number of pages (wizard steps) to show in this demo.
 */
private const val NUM_PAGES = 3

class OnboardingActivity : FragmentActivity(),ViewPagerNavigation {

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    val mIntroOnboardingFragment = IntroOnboardingFragment()
    val mSmileIntervalOnBoardingFragment = EmailChooseFragment()
    val mPermissionsOnboardingFragment = PermissionsOnboardingFragment()

    companion object {
        @JvmStatic lateinit var viewPager: ViewPager2
        @JvmStatic lateinit var pagerAdapter: ScreenSlidePagerAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = findViewById(R.id.pager)

        // The pager adapter, which provides the pages to the view pager widget.
        pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        val dotsIndicator = findViewById<WormDotsIndicator>(R.id.dots_indicator)
        dotsIndicator.setViewPager2(viewPager)
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    fun getMyViewPager(): ViewPager2 {
        if (null == viewPager) {
            viewPager = findViewById(R.id.pager)
        }
        return viewPager;
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        var mProgress = 2

        override fun getItemCount(): Int = mProgress

        override fun createFragment(position: Int): Fragment {
            if (position == 0) {
                return mIntroOnboardingFragment
            }
            else if (position == 1){
                return mSmileIntervalOnBoardingFragment
            }
            else if (position == 2){
                return mPermissionsOnboardingFragment
            }
            else
            {
                return mIntroOnboardingFragment
            }
        }

        fun setProgress(progress: Int)
        {
            if (progress > mProgress)
            {
                mProgress = progress
                notifyDataSetChanged()
            }
        }
    }

    override fun onProgress(progress: Int) {
        pagerAdapter.setProgress(progress)
    }
}

    interface ViewPagerNavigation {
    fun onProgress(progress: Int)
}