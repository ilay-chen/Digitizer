package com.icstudios.digitizer

//import com.icstudios.digitizer.signIn.context
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
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.icstudios.digitizer.appData.*
import java.util.*


class UserManager : LifecycleObserver {

    companion object {
        lateinit var activity : Activity
        lateinit var listener : AuthStateListener
        lateinit var mListener: MyListener
        var proDialog: ProgressDialog? = null
        var mService: com.google.api.services.calendar.Calendar? = null
        val PREF_ACCOUNT_NAME = "accountName"

        fun setUserManagerForActivity(activity: Activity)
        {
            this.activity = activity
        }

        fun logout(context: Context) {
            AuthUI.getInstance()
                    .signOut(context)
                    .addOnCompleteListener(object : OnCompleteListener<Void?> {
                        override fun onComplete(task: Task<Void?>) {
                            if(proDialog!=null && proDialog!!.isShowing)
                                proDialog!!.dismiss()
                            val a = Intent(activity, signIn::class.java)
                            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            activity.startActivity(a)
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
            proDialog = ProgressDialog.show(activity, activity.getString(R.string.validation_title), activity.getString(R.string.validation_body))

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
            proDialog = ProgressDialog.show(activity, activity.getString(R.string.login_title), activity.getString(R.string.login_body))

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
                            Toast.makeText(activity, "user Expire", Toast.LENGTH_LONG).show()
                            val ft: FragmentManager = (activity as FragmentActivity).supportFragmentManager

                            val newFragment: DialogFragment = ValidationFragment.newInstance(activity as FragmentActivity?, false)
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
                            readData(activity)
                            var time = dataSnapshot.child("progress").child("lastUpdate").value as Long

                            if(!allTasks.isUpToDate(time))
                            {
                                allTasks = marketingTasks()
                                for (task in dataSnapshot.child("progress").child("allTopics").children) {
                                    allTasks.addTopic(task.getValue(topicTasks::class.java))
                                }
                                saveData(activity)
                            }
                            readData(activity)
                            updateStrings(activity)
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
            val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(activity)
            return connectionStatusCode == ConnectionResult.SUCCESS
        }

        private fun acquireGooglePlayServices() {
            val apiAvailability = GoogleApiAvailability.getInstance()
            val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(activity)
            if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
                showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
            }
        }

        fun showGooglePlayServicesAvailabilityErrorDialog(
                connectionStatusCode: Int) {
            val apiAvailability = GoogleApiAvailability.getInstance()
            val dialog = apiAvailability.getErrorDialog(
                    activity as Activity?,
                    connectionStatusCode,
                    mainNav.REQUEST_GOOGLE_PLAY_SERVICES)
            dialog.show()
        }

        private fun isDeviceOnline(): Boolean {
            val connMgr = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

        private fun chooseAccount() {
            val sharedPref: SharedPreferences = activity.getApplicationContext().getSharedPreferences("strings", Context.MODE_PRIVATE)
            val accountName = sharedPref.getString(PREF_ACCOUNT_NAME, null)
            //String accountName = getPreferences(Context.MODE_PRIVATE)
            // .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.selectedAccountName = accountName
                getResultsFromApi()
            } else {
                // Start a dialog from which the user can choose an account
                (activity).startActivityForResult(
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
                MakeRequestTask(mCredential, activity).execute()
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
                val a = Intent(activity, mainNav::class.java)
                a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                activity.startActivity(a)
//                FirebaseAuth.getInstance().removeAuthStateListener(listener)
                (activity).finishAffinity()
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
                        (activity).startActivityForResult(
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
                        (activity),
                        connectionStatusCode,
                        mainNav.REQUEST_GOOGLE_PLAY_SERVICES)
                dialog.show()
            }

            init {
                val transport = AndroidHttp.newCompatibleTransport()
                val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()
                mService = com.google.api.services.calendar.Calendar.Builder(
                        transport, jsonFactory, credential)
                        .setApplicationName(activity.getString(R.string.app_name))
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
                    FirebaseAuth.getInstance().removeAuthStateListener(listener)

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
                appData.readData(activity)
                var mDatabase = FirebaseDatabase.getInstance().getReference(userTaskPath)
                val scheduledDate = Calendar.getInstance()
                appData.allTasks.setLastUpdate(scheduledDate.time.time)
                mDatabase.setValue(appData.allTasks)
            }
        }

        fun initUser()
        {
            initTasks(activity)
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                readData(activity)
                var mDatabase = FirebaseDatabase.getInstance().getReference(userTaskPath)
                val scheduledDate = Calendar.getInstance()
                allTasks.setLastUpdate(scheduledDate.time.time)
                mDatabase.setValue(allTasks)
                mUserData = userData(user.displayName)

//                mDatabase = FirebaseDatabase.getInstance().getReference(userDataPath)
//                var user = userData(user.displayName)

                val ft: FragmentManager = (activity as FragmentActivity).supportFragmentManager

                val newFragment: DialogFragment = ValidationFragment.newInstance(activity as FragmentActivity?, true)
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

//        fun initListener()
//        {
//            listener = AuthStateListener { firebaseAuth ->
//                val user = firebaseAuth.currentUser
//                if (user == null) { // Sign in logic here.
////                val ft =
////                        (activity as FragmentActivity).supportFragmentManager
////                val newFragment: DialogFragment = SignInFragment.newInstance()
////                newFragment.isCancelable = false
////                newFragment.show(ft, "signIn")
//                val a = Intent(activity, signIn::class.java)
//                a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                activity.startActivity(a)
//                }
//                else {
//                    userConnected(user)
//                }
//            }
//
//            FirebaseAuth.getInstance().addAuthStateListener(listener)
//        }
//
//        fun detach() {
//            FirebaseAuth.getInstance().removeAuthStateListener(listener)
//        }

        fun userConnected(user : FirebaseUser)
        {
            if(user!=null) {
                userRootPath = "users/" + user.uid
                userDataPath = "$userRootPath/data/"
                userTaskPath = "$userRootPath/progress/"
                userValidation()
            }
        }

//        fun createSignInIntent() {
//            // [START auth_fui_create_intent]
//            // Choose authentication providers
//            val providers = Arrays.asList( /*new AuthUI.IdpConfig.EmailBuilder().build(),*/
//                    GoogleBuilder().build())
//
//            // Create and launch sign-in intent
//            activity.startActivityForResult(
//                    AuthUI.getInstance()
//                            .createSignInIntentBuilder()
//                            .setAvailableProviders(providers)
//                            .build(),
//                    signIn.RC_SIGN_IN)
//            // [END auth_fui_create_intent]
//        }
    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
//    fun userConnection() {
//        listener = AuthStateListener { firebaseAuth ->
//            val user = firebaseAuth.currentUser
//            if (user == null) { // Sign in logic here.
////                val a = Intent(activity, signIn::class.java)
////                a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
////                activity.startActivity(a)
//                createSignInIntent()
//            }
//            else {
//
//            }
//        }
//
//        FirebaseAuth.getInstance().addAuthStateListener(listener)
//    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    fun detach() {
//        FirebaseAuth.getInstance().removeAuthStateListener(listener)
//    }
//    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
//    fun detachh() {
//        FirebaseAuth.getInstance().removeAuthStateListener(listener)
//    }
//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    fun detachhh() {
//        FirebaseAuth.getInstance().removeAuthStateListener(listener)
//    }

    interface MyListener {
        // you can define any parameter as per your requirement
        fun callback(success: Boolean?, time : Long)
    }
}