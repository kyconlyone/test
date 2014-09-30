package com.ihateflyingbugs.vocaslide.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.google.android.gcm.GCMRegistrar;
import com.ihateflyingbugs.vocaslide.Get_my_uuid;
import com.ihateflyingbugs.vocaslide.data.Date;
import com.ihateflyingbugs.vocaslide.login.JSONParser;

public class Async_check_id extends AsyncTask<String, String, String> {
	private static final String TAG_SUCCESS = "success";
	public static final int ID_EXCEPTION= 0;
	public static final int ID_SUCCESS= 1;
	public static final int ID_DUPLICATION= 2;
	
	JSONParser jParser1 = new JSONParser();
	
	protected String User_ID;
	protected String User_Version;
	VocaCallback mCallback;
	
	
	Context mcontext;
	ProgressDialog mProgress;
	int caller;
	
	public Async_check_id(String email, String version ,Context context, VocaCallback callback){
		User_ID = null;
		User_ID = email;
		User_Version = null;
		User_Version = version;
		mcontext = context;
		this.caller = caller;
		mCallback = callback;
		
		
	}
	
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//mProgress = ProgressDialog.show(mcontext, "Loading", "Please wait for a moment...");
		
	}
	public void make(){
		LocationManager locationManager;
		String provider;
		Criteria criteria;
		locationManager = (LocationManager) mcontext.getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the location provider
		criteria = new Criteria();
	}

	//DBÎ∞õÏïÑ?§Í∏∞
	protected String doInBackground(String... args) {
		
		Date date = new Date();
		String time =date.get_currentTime();
		Map<String, String> articleParams = new HashMap<String, String>();
		articleParams.put("id",""+User_ID);
		articleParams.put("time",""+time);
		FlurryAgent.logEvent("UserLogin",articleParams);

		Log.e("activitygg", "UserLogin");
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("User", User_ID));
		params.add(new BasicNameValuePair("Version", User_Version));
		params.add(new BasicNameValuePair("Time", time));
		params.add(new BasicNameValuePair("Device", ""+Get_my_uuid.get_Device_id(mcontext)));
		params.add(new BasicNameValuePair("Gcm", ""+GCMRegistrar.getRegistrationId(mcontext)));
		
		JSONObject json1 = null;
		
		
		try {
			
			json1 = jParser1.makeHttpRequest("http://www.lnslab.com/vocaslide/id_check.php", "GET", params);
			
			int success = json1.getInt(TAG_SUCCESS);	
			String message = json1.getString("message");	

			if (success !=  ID_EXCEPTION) {
				mCallback.Resonponse(json1);
			}else{
				mCallback.Exception();
			}
		
		} catch (JSONException e) {
			mCallback.Exception();
			e.printStackTrace();
		}catch (NullPointerException e) {
			// TODO: handle exception
			mCallback.Exception();
		}
		return null;
	}

	//?¨Í∏∞??UI ?ëÏóÖ?¥ÎÇò ?ÑÏóê Î°úÎî©Ï§??•Í??•Í? Í∑∏Í±∞ ?ÜÏï†Î≤ÑÎ¶º ???ã„Öã??
	protected void onPostExecute(String file_url) {
//		if(null !=mProgress) {
//			if(mProgress.isShowing()) {
//				mProgress.dismiss();
//			}
//		}
	}
	
}