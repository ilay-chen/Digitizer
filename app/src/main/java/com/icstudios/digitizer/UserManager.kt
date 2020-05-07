package com.icstudios.digitizer

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.*
import com.icstudios.digitizer.appData.*
import com.icstudios.digitizer.signIn.context
import java.util.*


class UserManager(var context: Context) : LifecycleObserver {
    lateinit var listener : AuthStateListener

    companion object {
        lateinit var mListener: MyListener
        var proDialog: ProgressDialog? = null
        var mService: com.google.api.services.calendar.Calendar? = null
        val PREF_ACCOUNT_NAME = "accountName"

        fun logout(context: Context) {
            AuthUI.getInstance()
                    .signOut(context)
                    .addOnCompleteListener(object : OnCompleteListener<Void?> {
                        override fun onComplete(task: Task<Void?>) {
                            if(proDialog!=null && proDialog!!.isShowing)
                                proDialog!!.dismiss()
                            val a = Intent(context, signIn::class.java)
                            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            context.startActivity(a)
                        }
                    })
        }

        fun update(context: Context, logOut : Boolean) {
            if(logOut)
                proDialog = ProgressDialog.show(context, context.getString(R.string.logout_text), context.getString(R.string.updating_progress))

            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                readData(context)
                var mDatabase = FirebaseDatabase.getInstance().getReference(userTaskPath)
                val scheduledDate = Calendar.getInstance()
                allTasks.setLastUpdate(scheduledDate.time.time)
                mDatabase.setValue(allTasks) { databaseError, databaseReference ->
                    if (databaseError != null) {
                        println("Data could not be saved. " + databaseError.message)
                    } else {
                        println("Data saved successfully.")
                        if(logOut) logout(context)
                    }
                }
            }
        }

        fun setValidation(validationCode: String?, init : Boolean)
        {
            proDialog = ProgressDialog.show(context, context.getString(R.string.validation_title), context.getString(R.string.validation_body))

            var validationCode: String? = validationCode
            val ml : MyListener
            var findKey : Boolean = false
            val user = FirebaseAuth.getInstance().currentUser
            var month = -1
            val databaseReference = FirebaseDatabase.getInstance().reference.child("keys").child("new")
            var query: Query = databaseReference.orderByChild("key").equalTo(validationCode)

            if (init)
            {
                month = -2
                query = databaseReference.orderByChild("months").equalTo("1")
            }

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.value == null) {
                        if(proDialog!=null && proDialog!!.isShowing)
                            proDialog!!.dismiss()
                        mListener.callback(false, 0)
                    }
                    for (task in dataSnapshot.children) {
                            if(!findKey) {
                                if(proDialog!=null && proDialog!!.isShowing)
                                    proDialog!!.dismiss()

                                findKey = true
                                val key = task.getValue(key::class.java)!!
                                var month = (key.getMonths() as String).toIntOrNull()
                                if(month == null) month = 0

                                if(validationCode==null)
                                    validationCode = key.getKey()
                                task.getRef().removeValue()

                                var mDatabase = FirebaseDatabase.getInstance().getReference(userDataPath)
                                mUserData.setValidation(validationCode, month!!)
                                mDatabase.setValue(mUserData)

                                //Invoke the interface
//                                if (!init)
                                mListener.callback(true, mUserData.expireTime)
//                                else {
//                                    val ft = (context as FragmentActivity).supportFragmentManager
//
//                                    val newFragment: DialogFragment = validationComplete.newInstance(mUserData.expireTime)
//                                    newFragment.isCancelable = false
//                                    newFragment.show(ft, "completeValidation")
////                                    userValidation()
//                                }
                                break
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
//                    if(!init)
                    mListener.callback(false, 0)
                }
            })

//            setCustomEventListener(object : MyListener {
//                override fun callback(success: Boolean?) {
//                    fun onEvent() {
//
//                    }
//                }
//            })
        }

        fun updateUser(validationCode: String?, month: Int)
        {
            var mDatabase = FirebaseDatabase.getInstance().getReference(userDataPath)
            mUserData.setValidation(validationCode, month)
            mDatabase.setValue(mUserData)
        }

        fun setCustomEventListener(eventListener: MyListener) {
            mListener = eventListener
        }

        fun userValidation(): Boolean? {
            context = signIn.getContext()
            proDialog = ProgressDialog.show(context, context.getString(R.string.login_title), context.getString(R.string.login_body))

            val databaseReference = FirebaseDatabase.getInstance().reference.child(userRootPath)

            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    var checkNull = dataSnapshot.child("data").getValue()
                    if(checkNull==null)
                    {
                        initUser()
                    }
                    else {
                        mUserData = dataSnapshot.child("data").getValue(userData::class.java)!!
                        if(mUserData.isExpire)
                        {
                            proDialog!!.dismiss()
                            //TODO user out of date
                            Toast.makeText(context, "user Expire", Toast.LENGTH_LONG).show()
                            val ft: FragmentManager = (signIn.getContext() as FragmentActivity).supportFragmentManager

                            val newFragment: DialogFragment = ValidationFragment.newInstance(signIn.getContext() as FragmentActivity?, false)
                            newFragment.isCancelable = false
                            newFragment.show(ft, "signIn")
//                        AuthUI.getInstance()
//                                .signOut(context)
//                                .addOnCompleteListener(object : OnCompleteListener<Void?> {
//                                    override fun onComplete(task: Task<Void?>) {
//                                        proDialog!!.dismiss()
//
//                                    }
//                                })
                        }
                        else {
                            readData(context)
                            var time = dataSnapshot.child("progress").child("lastUpdate").value as Long

                            if(!allTasks.isUpToDate(time))
                            {
                                allTasks = marketingTasks()
                                for (task in dataSnapshot.child("progress").child("allTopics").children) {
                                    allTasks.addTopic(task.getValue(topicTasks::class.java))
                                }
                                saveData(context)
                            }
                            readData(context)
                            updateStrings(context)
                            getResultsFromApi()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
            return null
        }

        private fun isGooglePlayServicesAvailable(): Boolean {
            val apiAvailability = GoogleApiAvailability.getInstance()
            val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context)
            return connectionStatusCode == ConnectionResult.SUCCESS
        }

        private fun acquireGooglePlayServices() {
            val apiAvailability = GoogleApiAvailability.getInstance()
            val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context)
            if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
                showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
            }
        }

        fun showGooglePlayServicesAvailabilityErrorDialog(
                connectionStatusCode: Int) {
            val apiAvailability = GoogleApiAvailability.getInstance()
            val dialog = apiAvailability.getErrorDialog(
                    context as Activity?,
                    connectionStatusCode,
                    mainNav.REQUEST_GOOGLE_PLAY_SERVICES)
            dialog.show()
        }

        private fun isDeviceOnline(): Boolean {
            val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

        private fun chooseAccount() {
            val sharedPref: SharedPreferences = context.getApplicationContext().getSharedPreferences("strings", Context.MODE_PRIVATE)
            val accountName = sharedPref.getString(PREF_ACCOUNT_NAME, null)
            //String accountName = getPreferences(Context.MODE_PRIVATE)
            // .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.selectedAccountName = accountName
                getResultsFromApi()
            } else {
                // Start a dialog from which the user can choose an account
                (context as Activity).startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        mainNav.REQUEST_ACCOUNT_PICKER)
            }
        }

        private fun getResultsFromApi() {
            if (!isGooglePlayServicesAvailable()) {
                acquireGooglePlayServices()
            } else if (mCredential.selectedAccountName == null) {
                chooseAccount()
            } else if (!isDeviceOnline()) {
                //mOutputText.setText("No network connection available.");
            } else {
                MakeRequestTask(mCredential, context as Activity).execute()
            }
        }

        private class MakeRequestTask(credential: GoogleAccountCredential?, activity: Activity) : AsyncTask<Void?, Void?, List<String>?>() {
            private var mLastError: Exception? = null
            private val FLAG = false
            private val activity: Activity

            /**
             * Background task to call Google Calendar API.
             * @param params no parameters needed for this task.
             */
            override fun doInBackground(vararg params: Void?): List<String>? {
                try {
                } catch (e: Exception) {
                    e.printStackTrace()
                    mLastError = e
                    cancel(true)
                    return null
                }
                return null
            }

            override fun onPreExecute() {
                //mOutputText.setText("");
                //mProgress.show();
            }

            override fun onPostExecute(output: List<String>?) {
                proDialog!!.dismiss()
                val a = Intent(signIn.getContext(), mainNav::class.java)
                a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                signIn.getContext().startActivity(a)
                //autoSingIn();
                //appData.checkProgress(context, -1, activity);
            }

            override fun onCancelled() {

                //mProgress.hide();
                if (mLastError != null) {
                    if (mLastError is GooglePlayServicesAvailabilityIOException) {
                        showGooglePlayServicesAvailabilityErrorDialog(
                                (mLastError as GooglePlayServicesAvailabilityIOException)
                                        .connectionStatusCode)
                    } else if (mLastError is UserRecoverableAuthIOException) {
                        (signIn.getContext() as Activity).startActivityForResult(
                                (mLastError as UserRecoverableAuthIOException).intent,
                                mainNav.REQUEST_AUTHORIZATION)
                    } else {
                        //mOutputText.setText("The following error occurred:\n"
                        //      + mLastError.getMessage());
                    }
                } else {
                    //mOutputText.setText("Request cancelled.");
                }
            }

            fun showGooglePlayServicesAvailabilityErrorDialog(
                    connectionStatusCode: Int) {
                val apiAvailability = GoogleApiAvailability.getInstance()
                val dialog = apiAvailability.getErrorDialog(
                        (signIn.getContext() as Activity),
                        connectionStatusCode,
                        mainNav.REQUEST_GOOGLE_PLAY_SERVICES)
                dialog.show()
            }

            init {
                val transport = AndroidHttp.newCompatibleTransport()
                val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()
                mService = com.google.api.services.calendar.Calendar.Builder(
                        transport, jsonFactory, credential)
                        .setApplicationName(signIn.context.getString(R.string.app_name))
                        .build()
                this.activity = activity
            }
        }

        fun userGetTasks(context : Context): Boolean? {
            val databaseReference = FirebaseDatabase.getInstance().reference.child(userTaskPath)
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    appData.allTasks = marketingTasks()
                    //ArrayList<topicTasks> allt = dataSnapshot.child("allTopics").getValue(ArrayList<topicTasks>);
                    //int j = 0;
                    for (task in dataSnapshot.child("allTopics").children) {
                        appData.allTasks.addTopic(task.getValue(topicTasks::class.java))
                    }
                    //appData.allTasks = dataSnapshot.child("allTopics").getValue(marketingTasks.class);
                    //appData.allTasks = new marketingTasks(allt);
                    for (datas in dataSnapshot.children) {
                        //appData.allTasks = (marketingTasks) datas.child("allTopics").getValue();
                    }
                    saveData(context)
                    val a = Intent(context, mainNav::class.java)
                    a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(a)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
            return null
        }

        fun updateTasks()
        {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                appData.readData(context)
                var mDatabase = FirebaseDatabase.getInstance().getReference(userTaskPath)
                val scheduledDate = Calendar.getInstance()
                appData.allTasks.setLastUpdate(scheduledDate.time.time)
                mDatabase.setValue(appData.allTasks)
            }
        }

        fun initUser()
        {
            initTasks(context)
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                readData(context)
                var mDatabase = FirebaseDatabase.getInstance().getReference(userTaskPath)
                val scheduledDate = Calendar.getInstance()
                allTasks.setLastUpdate(scheduledDate.time.time)
                mDatabase.setValue(allTasks)
                mUserData = userData(user.displayName)

//                mDatabase = FirebaseDatabase.getInstance().getReference(userDataPath)
//                var user = userData(user.displayName)

                val ft: FragmentManager = (signIn.getContext() as FragmentActivity).supportFragmentManager

                val newFragment: DialogFragment = ValidationFragment.newInstance(signIn.getContext() as FragmentActivity?, true)
                newFragment.isCancelable = false
                newFragment.show(ft, "signIn")
//                mDatabase.setValue(user)
//
//                userValidation()

                /*
                mDatabase.setValue(appData.allTasks) { databaseError, databaseReference ->
                    if (databaseError != null) {
                        println("Data could not be saved. " + databaseError.message)
                    } else {
                        println("Data saved successfully.")
                    }
                }

                 */

            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun userConnection() {
        listener = AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) { // Sign in logic here.
//                val ft =
//                        (activity as FragmentActivity).supportFragmentManager
//                val newFragment: DialogFragment = SignInFragment.newInstance()
//                newFragment.isCancelable = false
//                newFragment.show(ft, "signIn")
//                val a = Intent(activity, signIn::class.java)
//                a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                activity.startActivity(a)
            }
            else {
                userRootPath = "users/" + user.uid
                userDataPath = "$userRootPath/data/"
                userTaskPath = "$userRootPath/progress/"
                userValidation()
            }
        }

        FirebaseAuth.getInstance().addAuthStateListener(listener)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun detach() {
        FirebaseAuth.getInstance().removeAuthStateListener(listener)
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun detachh() {
        FirebaseAuth.getInstance().removeAuthStateListener(listener)
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun detachhh() {
        FirebaseAuth.getInstance().removeAuthStateListener(listener)
    }

    interface MyListener {
        // you can define any parameter as per your requirement
        fun callback(success: Boolean?, time : Long)
    }
}