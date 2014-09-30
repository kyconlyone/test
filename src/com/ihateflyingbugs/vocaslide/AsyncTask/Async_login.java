package com.ihateflyingbugs.vocaslide.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.ihateflyingbugs.vocaslide.login.JSONParser;
import com.ihateflyingbugs.vocaslide.login.MainActivitys;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class Async_login extends AsyncTask<String, String, String> {
	private static final String TAG_SUCCESS = "success";
	
	public static final int Login_EXCEPTION= 0;
	public static final int Login_SUCCESS= 1;
	public static final int Login_ID_FAIL_MATCH= 2;
	public static final int Login_PW_FAIL_MATCH= 3;
	public static final int Login_EXPIRE_CODE= 4;
	public static final int Login_SAME_DEVICE= 5;
	public static final int Login_OTHER_DEVICE_ALREADY_LOGIN= 6;
	public static final int Login_OTHER_DEVICE_LOGIN= 7;
	public static final int Login_OTHER_DEVICE_ALREADY_LOGIN_ENOUGH= 98;
	public static final int Login_OTHER_DEVICE_ENOUGH= 99;

	
	JSONParser jParser1 = new JSONParser();
	
	protected String User_email;
	protected String User_password;
	protected String User_gcm;
	protected String User_device_num;
	
	Context mcontext;
	ProgressDialog mProgress;
	
	public Async_login(List<String> User_Data, Context context){
		if(User_Data.isEmpty()||User_Data.size()!=5){
			// ?¨Í∏∞???àÏô∏ Ï≤òÎ¶¨
		}
		
		User_email = null;
		User_password= null;
		User_gcm= null;
		User_device_num= null;
		
		User_email = User_Data.get(0);
		User_password=User_Data.get(1);
		User_gcm=User_Data.get(2);
		User_device_num= User_Data.get(3);
		mcontext = context;
		
	}
	
	//?¨Í∏∞??Î≠?Î°úÎî©Ï§ëÏù¥?ºÎäî ?ôÍ??ôÍ? ?£Ïñ¥Ï£ºÎ©¥ ?úÎ? ?ã„Öã
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//mProgress = ProgressDialog.show(mcontext, "Loading", "Please wait for a moment...");

	}

	//DBÎ∞õÏïÑ?§Í∏∞
	protected String doInBackground(String... args) {
		
		ArrayList<String> gunss=null;
		gunss = new ArrayList<String>();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_id", "aa@aa.com"));
		params.add(new BasicNameValuePair("user_pass", "aaaaaa"));
		params.add(new BasicNameValuePair("user_gcm", User_gcm)); //user_deviceø°º≠ user_gcm¿∏∑Œ
		params.add(new BasicNameValuePair("user_devicenum", User_device_num)); // √ﬂ∞°µ» device ≥—πˆ
		
		Log.e("gcm_num","asdfasdfasdf"+User_gcm);
		Log.e("hahaha", User_email);
		//JSONObject json1 = jParser1.makeHttpRequest("http://manulkk.cafe24.com/login.php", "GET", params);
		JSONObject json1 = jParser1.makeHttpRequest("http://lnslab.com/vocaslide/login1.php", "GET", params);

		try {
			
			int success = json1.getInt(TAG_SUCCESS);	//?¥Î©î??Í≤πÏπòÎ©?1??Î≥¥ÎÇ¥Í≥? ?àÍ≤πÏπòÎ©¥ 0??Î≥¥ÎÇºÍ≤?
			String message = json1.getString("message");
			String name;
			Log.e("hahaha", message);
			
			try {
				name = json1.getString("name");
			} catch (JSONException e) {
				// TODO: handle exception
				name = "testname";
			}
			
			Log.e("tag_su", String.valueOf(success));
				
//			if (success ==  Login_SUCCESS) {
//				MainActivitys.login_account(Login_SUCCESS, message, name);
//			} else if(success == Login_ID_FAIL_MATCH) {
//				MainActivitys.login_account(Login_ID_FAIL_MATCH, message, name);
//			}else if(success == Login_PW_FAIL_MATCH) {
//				MainActivitys.login_account(Login_PW_FAIL_MATCH, message, name);
//			}else if(success == Login_EXPIRE_CODE) {
//				MainActivitys.login_account(Login_EXPIRE_CODE, message, name);
//			}else if(success == Login_SAME_DEVICE) {
//				MainActivitys.login_account(Login_SAME_DEVICE, message, name);
//			}else if(success == Login_OTHER_DEVICE_ALREADY_LOGIN) {
//				MainActivitys.login_account(Login_OTHER_DEVICE_ALREADY_LOGIN, message, name);
//			}else if(success == Login_OTHER_DEVICE_LOGIN) {
//				MainActivitys.login_account(Login_OTHER_DEVICE_LOGIN, message, name);
//			}else if(success == Login_OTHER_DEVICE_ALREADY_LOGIN_ENOUGH) {
//				MainActivitys.login_account(Login_OTHER_DEVICE_ALREADY_LOGIN_ENOUGH, message, name);
//			}else if(success == Login_OTHER_DEVICE_ENOUGH) {
//				MainActivitys.login_account(Login_OTHER_DEVICE_ENOUGH, message, name);
//			}else{
//				MainActivitys.login_account(Login_EXCEPTION, message, name);
//			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	//?¨Í∏∞??UI ?ëÏóÖ?¥ÎÇò ?ÑÏóê Î°úÎî©Ï§??•Í??•Í? Í∑∏Í±∞ ?ÜÏï†Î≤ÑÎ¶º ???ã„Öã??
	protected void onPostExecute(String file_url) {
		if(null !=mProgress) {
			if(mProgress.isShowing()) {
				mProgress.dismiss();
			}
		}
	}
	
}