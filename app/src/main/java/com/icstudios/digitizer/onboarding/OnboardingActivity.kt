package com.icstudios.digitizer.onboarding;

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.icstudios.digitizer.R
import com.icstudios.digitizer.signIn
import com.icstudios.digitizer.signIn.context
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
        @JvmStatic lateinit var next : Button
        @JvmStatic lateinit var previous : Button
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = findViewById(R.id.pager)

        // The pager adapter, which provides the pages to the view pager widget.
        pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        next.isEnabled = true
                        next.setText(context.getString(R.string.next_button))
                        previous.isEnabled = false
                    }
                    1 -> {
                        next.isEnabled = EmailChooseFragment.accountChoose
                        next.setText(context.getString(R.string.next_button))
                        previous.isEnabled = true
                    }
                    2 -> {
                        next.setText(getString(R.string.finish_button))
                        next.isEnabled = hasPermissions()
                        previous.isEnabled = true
                    }
                    else -> { // Note the block
                        print("x is neither 1 nor 2")
                    }
                }

            }
        })

        val dotsIndicator = findViewById<WormDotsIndicator>(R.id.dots_indicator)
        dotsIndicator.setViewPager2(viewPager)

        next = findViewById<Button>(R.id.next)
        next.setOnClickListener(View.OnClickListener {
            nextPage()
        })

        previous = findViewById<Button>(R.id.previous)
        previous.setOnClickListener(View.OnClickListener {
            previousPage()
        })
    }

    fun hasPermissions() = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context!!, it) == PackageManager.PERMISSION_GRANTED
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

    fun nextPage()
    {
        if(viewPager.getCurrentItem() + 1 == 2 && getProgress() == 2)
        {
            Toast.makeText(applicationContext, getString(R.string.choose_mail_toast),
                    Toast.LENGTH_LONG).show()
        }
        else if (viewPager.getCurrentItem() + 1 < getProgress())
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1)
        else startMainActivity()
    }

    private fun startMainActivity() {
        val sharedPref: SharedPreferences = applicationContext.getSharedPreferences("strings", Context.MODE_PRIVATE)

        val editor = sharedPref.edit()
        editor.putBoolean("firstTime", false)
        editor.apply()

        // move to next activity
        startActivity(
                Intent(applicationContext, signIn::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    fun previousPage()
    {

        if (viewPager.getCurrentItem() != 0)
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1)
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
        fun getProgress(): Int {
            return mProgress
        }
    }

    override fun onProgress(progress: Int) {
        pagerAdapter.setProgress(progress)
    }
    override fun getProgress(): Int {
        return pagerAdapter.getProgress()
    }
}

    interface ViewPagerNavigation {
    fun getProgress(): Int
    fun onProgress(progress: Int)
}