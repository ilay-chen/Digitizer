package com.icstudios.digitizer.onboarding;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.icstudios.digitizer.R

class IntroOnboardingFragment : Fragment() {

    private lateinit var layout : LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        layout = LinearLayout(context)
        layout.setOrientation(LinearLayout.VERTICAL)
        layout.setPadding(40,40,40,40)

        val noteParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        noteParams.setMargins(40, 60, 40, 300)

        val innerNoteParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        innerNoteParams.setMargins(60, 270, 60, 80)

        var noteLayout = LinearLayout(context)
        noteLayout.setOrientation(LinearLayout.VERTICAL)
        noteLayout.setBackgroundResource(R.drawable.pinnote7)

        var introduction = TextView(context)
        introduction.setText(R.string.open_page)

        noteLayout.addView(introduction, innerNoteParams)
        layout.addView(noteLayout, noteParams)
        return layout
    }
}
