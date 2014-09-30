package com.ihateflyingbugs.vocaslide.AsyncTask;

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
import android.widget.Toast;

public class Async_make_account extends AsyncTask<String, String, String> {
	private static final String TAG_SUCCESS = "success";
	public static final int MAKE_EXCEPTION= 0;
	public static final int MAKE_SUCCESS= 1;
	public static final int MAKE_FAIL_NOT_EXIST= 2;
	public static final int MAKE_FAIL_ALREADY_USED= 3;
	public static final int MAKE_FAIL_EXPIRE_CODE= 4;
	public static final int MAKE_FAIL_MATCH= 5;
	public static final int MAKE_FAIL_ALREADY_ID= 6;

	JSONParser jParser1 = new JSONParser();

	protected String User_email;
	protected String User_password;
	protected String User_paynum;
	protected String User_phonenum;
	protected String User_gcm;
	protected String User_devicenum;
	

	Context mcontext;
	ProgressDialog mProgress;

	public Async_make_account(List<String> User_Data, Context context){
		if(User_Data.isEmpty()||User_Data.size()!=5){
			// ?¨Í∏∞???àÏô∏ Ï≤òÎ¶¨
		}
		User_email = null;
		User_password=null;
		User_paynum= null;
		User_phonenum=null;
		User_gcm=null;
		User_devicenum = null;

		User_email = User_Data.get(0);
		User_password=User_Data.get(1);
		User_paynum= User_Data.get(2);
		User_phonenum=User_Data.get(3);
		User_gcm=User_Data.get(4);
		User_devicenum = User_Data.get(5);
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
		params.add(new BasicNameValuePair("user_id", User_email));
		params.add(new BasicNameValuePair("user_pass", User_password));
		params.add(new BasicNameValuePair("user_phonenum", User_phonenum));
		params.add(new BasicNameValuePair("user_gcm", User_gcm));
		params.add(new BasicNameValuePair("user_paynum", User_paynum));
		params.add(new BasicNameValuePair("user_devicenum", User_devicenum));


		JSONObject json1 = jParser1.makeHttpRequest("http://www.lnslab.com/vocaslide/id_make.php", "GET", params);

		try {
			int success = json1.getInt(TAG_SUCCESS);	//?¥Î©î??Í≤πÏπòÎ©?1??Î≥¥ÎÇ¥Í≥? ?àÍ≤πÏπòÎ©¥ 0??Î≥¥ÎÇºÍ≤?
			String message = json1.getString("message");	

//			if (success ==  MAKE_SUCCESS) {
//				JoinActivity.cofirm_make_account(MAKE_SUCCESS, message);
//			} else if(success == MAKE_FAIL_NOT_EXIST) {
//				JoinActivity.cofirm_make_account(MAKE_FAIL_NOT_EXIST, message);
//			}else if(success == MAKE_FAIL_MATCH) {
//				JoinActivity.cofirm_make_account(MAKE_FAIL_MATCH, message);
//			}else if(success == MAKE_FAIL_ALREADY_USED) {
//				JoinActivity.cofirm_make_account(MAKE_FAIL_ALREADY_USED, message);
//			}else if(success == MAKE_FAIL_EXPIRE_CODE) {
//				JoinActivity.cofirm_make_account(MAKE_FAIL_EXPIRE_CODE, message);
//			}else if(success == MAKE_FAIL_ALREADY_ID) {
//				JoinActivity.cofirm_make_account(MAKE_FAIL_ALREADY_ID, message);
//			}else{
//				JoinActivity.cofirm_make_account(MAKE_EXCEPTION, message);
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