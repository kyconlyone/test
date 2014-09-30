package com.ihateflyingbugs.vocaslide.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gcm.GCMRegistrar;
import com.ihateflyingbugs.vocaslide.login.JSONParser;
import com.ihateflyingbugs.vocaslide.login.WriteUserInfoActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class Async_MakeAccount extends AsyncTask<String, String, String> {
	private static final String TAG_SUCCESS = "success";
	
	public static final int MAKE_EXCEPTION= 0;
	public static final int MAKE_SUCCESS= 1;
	public static final int MAKE_FAIL_MATCH= 2;
	
	JSONParser jParser1 = new JSONParser();

	protected String User_id;
	protected String User_email;
	protected String User_pass;
	protected String name;
	protected String school;
	protected String birth;
	protected String gender;
	

	protected String User_phonenum;
	protected String User_gcm;
	protected String User_paynum;
	protected String User_devicenum;
	
	Context mcontext;
	ProgressDialog mProgress;
	
	VocaCallback mCallback;
	
	public Async_MakeAccount(List<String> User_Data, Context context, VocaCallback callback){
		if(User_Data.isEmpty()||User_Data.size()!=5){
			// ?¨Í∏∞???àÏô∏ Ï≤òÎ¶¨
		}
		
		User_email =null;
		User_pass = null;
		name =null;
		school =null;
		birth =null;
		gender =null;
		
		
		User_id = User_Data.get(0);
		User_devicenum=User_Data.get(1);
		User_phonenum=User_Data.get(2);
		User_gcm=User_Data.get(3);
		name = User_Data.get(4);
		birth=User_Data.get(5);
		User_email = User_Data.get(6);
		gender=User_Data.get(7);
		User_paynum="xxxxxx";
		mCallback = callback;
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
		params.add(new BasicNameValuePair("user_id", User_id));
		params.add(new BasicNameValuePair("user_name", name));
		params.add(new BasicNameValuePair("user_birth", birth));
		params.add(new BasicNameValuePair("user_gender", gender));
		params.add(new BasicNameValuePair("user_phonenum", User_phonenum));
		params.add(new BasicNameValuePair("user_gcm", User_gcm));
		params.add(new BasicNameValuePair("user_paynum", User_paynum));
		params.add(new BasicNameValuePair("user_devicenum", User_devicenum));
		params.add(new BasicNameValuePair("user_email", User_email));
		
				
		JSONObject json1 = jParser1.makeHttpRequest("http://www.lnslab.com/vocaslide/id_register.php", "GET", params);

		try {
			int success = json1.getInt(TAG_SUCCESS);	//?¥Î©î??Í≤πÏπòÎ©?1??Î≥¥ÎÇ¥Í≥? ?àÍ≤πÏπòÎ©¥ 0??Î≥¥ÎÇºÍ≤?
			String message = json1.getString("message");	
			
			if(success != MAKE_EXCEPTION){
				mCallback.Resonponse(json1);
			}else{
				mCallback.Exception();
			}
			
//			if (success ==  MAKE_SUCCESS) {
//				WriteUserInfoActivity.cofirm_update_account(MAKE_SUCCESS, message);
//			}else if(success == MAKE_FAIL_MATCH) {
//				WriteUserInfoActivity.cofirm_update_account(MAKE_FAIL_MATCH, message);
//			}else{
//				WriteUserInfoActivity.cofirm_update_account(MAKE_EXCEPTION, message);
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