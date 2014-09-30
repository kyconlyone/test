package com.ihateflyingbugs.vocaslide.AsyncTask;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.ihateflyingbugs.vocaslide.MyInfoFragment;
import com.ihateflyingbugs.vocaslide.login.JSONParser;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class Async_edit_user_pass extends AsyncTask<String, String, String> {
	private static final String TAG_SUCCESS = "success";
	static final int Edit_PASS_EXCEPTION= 0; // 서버장애
	static final int Edit_PASS_SUCCESS= 1;	// 수정 성공
	static final int Edit_PASS_MISMATCH= 2; // 현재 비밀번호가 불일치

	
	JSONParser jParser1 = new JSONParser();
	
	protected String User_email;
	protected String User_cur_pass;
	protected String User_edit_pass;
	
	Context mcontext;
	ProgressDialog mProgress;
	
	public Async_edit_user_pass(String email, String cur_pass, String edit_pass,Context context){
		User_email = null;
		User_cur_pass= null;
		User_edit_pass= null;
		User_email = email;
		User_cur_pass = cur_pass;
		User_edit_pass = edit_pass;
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
		params.add(new BasicNameValuePair("User", User_email)); //아이디
		params.add(new BasicNameValuePair("Cur_pass", User_cur_pass)); // 현재비밀번호
		params.add(new BasicNameValuePair("Edit_pass", User_edit_pass)); //변경할 비밀번호
		JSONObject json1 = jParser1.makeHttpRequest("http://www.lnslab.com/vocaslide/edit_user_pass.php", "GET", params);
		
		try {
			int success = json1.getInt(TAG_SUCCESS);
			Log.e("webtest", "success : " + success);
			String message = json1.getString("message");
			Log.e("webtest", "message : " + message);
			//MyInfoFragment.edit_user_pass(success, message);
			
			
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