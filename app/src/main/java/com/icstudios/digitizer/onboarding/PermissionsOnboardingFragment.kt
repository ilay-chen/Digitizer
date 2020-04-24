package com.icstudios.digitizer.onboarding;

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.icstudios.digitizer.R

private const val PERMISSION_OVERLAY_REQUEST_CODE = 10
private const val PERMISSIONS_REQUEST_CODE = 11
val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.GET_ACCOUNTS)
private var mFirebaseAnalytics: FirebaseAnalytics? = null

class PermissionsOnboardingFragment : Fragment() {

    private lateinit var mButtonPermission: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var layout = LinearLayout(context)
        layout.setOrientation(LinearLayout.VERTICAL)
        layout.setPadding(20, 20, 20, 20)

        val noteParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        noteParams.setMargins(40, 60, 40, 300)

        val innerNoteParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        innerNoteParams.setMargins(60, 270, 60, 80)

        val innerNote = LinearLayout(context)
        innerNote.orientation = LinearLayout.VERTICAL

        val noteLayout = LinearLayout(context)
        noteLayout.orientation = LinearLayout.VERTICAL
        noteLayout.setBackgroundResource(R.drawable.pinnote7)

        val title = TextView(activity)
        title.setText(getString(R.string.premmissions_title))
        title.setPadding(0, 10, 0, 30)

        val explanation = TextView(context)
        explanation.setText(getString(R.string.premmissions_body))
        explanation.setPadding(0, 10, 0, 30)

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context!!)

        OnboardingActivity.next.isEnabled = hasPermissions()

        mButtonPermission = Button(context)
        mButtonPermission.text = getString(R.string.premissions_button)
        mButtonPermission.setBackgroundResource(R.drawable.button_skype_rectangle_background)
        mButtonPermission.setOnClickListener(View.OnClickListener { view ->
            getPermissions()
        })

        innerNote.addView(title)
        innerNote.addView(explanation)
        innerNote.addView(mButtonPermission)

        noteLayout.addView(innerNote, innerNoteParams)
        layout.addView(noteLayout, noteParams)

        return layout
        /*
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.fragment_permissions_onboarding, container, false)

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context!!)

        OnboardingActivity.next.isEnabled = hasPermissions()

        mButtonPermission = rootView.findViewById(R.id.button_permissions)
        mButtonPermission.setOnClickListener(View.OnClickListener { view ->
            getPermissions()
        })

        return rootView

         */
    }

    override fun onResume() {
        super.onResume()
        OnboardingActivity.next.isEnabled = hasPermissions()
    }

    /**
     * Permission functions
     */

    fun getPermissions() {
        requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
    }

    /** Convenience method used to check if all permissions required by this app are granted */
    fun hasPermissions() = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context!!, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.all { grantResult ->  (PackageManager.PERMISSION_GRANTED == grantResult) }){
                // Take the user to the success fragment when permission is granted
                Toast.makeText(activity, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(activity, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Overlay permission functions
     */

    fun hasPermissionOverlay() : Boolean{
        val hasOverlayPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        return hasOverlayPermissions
    }

    fun getPermissionOverlay()
    {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        //jump auto to specific app overlay setting.
        intent.setData(Uri.parse("package:" + activity?.getString(R.string.app_package_name)))
        activity?.startActivityForResult(intent, PERMISSION_OVERLAY_REQUEST_CODE)
    }
}
