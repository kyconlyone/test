package com.ihateflyingbugs.vocaslide;

import static com.ihateflyingbugs.vocaslide.CommonUtilities.DISPLAY_MESSAGE_ACTION;

import com.google.android.gcm.GCMRegistrar;
import com.ihateflyingbugs.vocaslide.login.AlertDialogManager;
import com.ihateflyingbugs.vocaslide.login.ConnectionDetector;
import com.ihateflyingbugs.vocaslide.login.WakeLocker;
import com.kakao.GlobalApplication;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class VOCAconfig extends GlobalApplication {


	AsyncTask<Void, Void, Void> mRegisterTask;

	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();

	// Connection detector
	ConnectionDetector cd;
	String TAG = "haha";


	/* (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
//			alert.showAlertDialog(VOCAconfig.this,
//					"Internet Connection Error",
//					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}
		
		 // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
 
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);       
         
        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                DISPLAY_MESSAGE_ACTION));
         
        // Get GCM registration id
        final String regId = GCMRegistrar.getRegistrationId(this);

        Log.e(TAG, "A");
        // Check if regid already presents
        if (regId.equals("")) {
            // Registration is not present, register now with GCM    
            GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
            Log.e(TAG, "B");
        } else {
            // Device is already registered on GCM
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.              
                Log.e(TAG, "C");
               
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of com.ihateflyingbugs.vocaslide.AsyncTask instead of a raw thread.
                Log.e(TAG, "D");
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {
 
                    @Override
                    protected Void doInBackground(Void... params) {
                        // Register on our server
                        // On server creates a new user
                        Log.e(TAG, "E");
                        ServerUtilities.register(context, regId);
                        return null;
                    }
 
                    @Override
                    protected void onPostExecute(Void result) {
                        Log.e(TAG, "F");
                        mRegisterTask = null;
                    }
 
                };
                mRegisterTask.execute(null, null, null);
            }
        }
	}
	
	 private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
	            // Waking up mobile if it is sleeping
	            WakeLocker.acquire(getApplicationContext());

	            Log.e(TAG, "G");
	             
	            /**
	             * Take appropriate action on this message
	             * depending upon your app requirement
	             * For now i am just displaying it on the screen
	             * */
	             
	            // Showing received message         
	            //Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
	             
	            // Releasing wake lock
	            WakeLocker.release();
	        }
	    };
	    

	/* (non-Javadoc)
	 * @see android.app.Application#onTerminate()
	 */
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}
	
	
	
	


}
