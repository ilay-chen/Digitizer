package com.icstudios.digitizer.onboarding;

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.icstudios.digitizer.R

class IntroOnboardingFragment : Fragment() {

    private lateinit var layout : LinearLayout
    private lateinit var mButtonNext: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        layout = LinearLayout(context)
        layout.setOrientation(LinearLayout.VERTICAL)
        layout.setPadding(40,40,40,40)

        var introduction = TextView(context)
        introduction.setText(R.string.open_page)

        mButtonNext = Button(context)
        mButtonNext.setText("next")
        mButtonNext.setOnClickListener(View.OnClickListener { view ->
            (activity as OnboardingActivity?)?.getMyViewPager()?.setCurrentItem(1, true)
        })

        layout.addView(introduction)
        layout.addView(mButtonNext)
        //return inflater.inflate(R.layout.fragment_intro_onboarding, container, false)
        return layout
    }
}
