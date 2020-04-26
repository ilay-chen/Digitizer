package com.icstudios.digitizer

import android.app.Activity
import android.content.Context
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser


class UserManager(activity: AppCompatActivity) : LifecycleObserver {
    var activity : AppCompatActivity = activity
    lateinit var listener : AuthStateListener

    companion object {
        fun logout(context: Context) {
            AuthUI.getInstance()
                .signOut(context)
                .addOnCompleteListener(object : OnCompleteListener<Void?> {
                    override fun onComplete(task: Task<Void?>) {
                    }
                })
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun initUser() {
        listener = AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) { // Sign in logic here.
//                val ft =
//                    (activity as FragmentActivity).supportFragmentManager
//                val newFragment: DialogFragment = SignInFragment.newInstance()
//                newFragment.isCancelable = false
//                newFragment.show(ft, "signIn")
            }
        }

        FirebaseAuth.getInstance().addAuthStateListener(listener)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun detach() {
        FirebaseAuth.getInstance().removeAuthStateListener(listener)
    }
}