package com.ihateflyingbugs.vocaslide.AsyncTask;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.ihateflyingbugs.vocaslide.login.JSONParser;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class Async_get_user_info extends AsyncTask<String, String, String> {
	private static final String TAG_SUCCESS = "success";
	static final int GET_INFO_EXCEPTION= 0;
	static final int GET_INFO_SUCCESS= 1;
	static final int GET_INFO_FAIL= 2;
	
	JSONParser jParser1 = new JSONParser();
	
	protected String User_email;
	String act_name;
	Context mcontext;
	ProgressDialog mProgress;
	
	public Async_get_user_info(String email, Context context){
		User_email = null;
		User_email = email;
		mcontext = context;
	}
	

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//mProgress = ProgressDialog.show(mcontext, "Loading", "Please wait for a moment...");
		
	}


	protected String doInBackground(String... args) {
		
		ArrayList<String> gunss=null;
		gunss = new ArrayList<String>();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("User", User_email));
		JSONObject json1 = jParser1.makeHttpRequest("http://www.lnslab.com/vocaslide/get_user_info.php", "GET", params);
		
		String name = null;
		String gender = null;
		String school = null;
		String birth = null;
		
		try {
			int success = json1.getInt(TAG_SUCCESS);
			Log.e("webtest", "success : " + success);
			String message = json1.getString("message");
			Log.e("webtest", "message : " + message);
			if (success ==  GET_INFO_SUCCESS) {
				
				try {
					name = new String(json1.getString("name").getBytes());
					
					
					gender = json1.getString("gender");
					school = json1.getString("school");
					birth = json1.getString("birth");
				} catch (JSONException e) {
					// TODO: handle exception
					
				}
				//MyInfoFragment.get_user_info(GET_INFO_SUCCESS, message,name, gender, school,birth);
			} else if(success == GET_INFO_FAIL) {
				//MyInfoFragment.get_user_info(GET_INFO_FAIL, message,name, gender, school,birth);
			}else{
				//MyInfoFragment.get_user_info(GET_INFO_EXCEPTION, message,name, gender, school,birth);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}


	protected void onPostExecute(String file_url) {
//		if(null !=mProgress) {
//			if(mProgress.isShowing()) {
//				mProgress.dismiss();
//			}
//		}
	}
	
	
	
}