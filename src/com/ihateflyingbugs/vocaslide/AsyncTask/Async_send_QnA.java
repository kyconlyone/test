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
import android.util.Log;
import android.widget.Toast;

class Async_send_QnA extends AsyncTask<String, String, String> {
	private static final String TAG_SUCCESS = "success";
	static final int SEND_EXCEPTION= 0;
	static final int SEND_SUCCESS= 1;
	
	JSONParser jParser1 = new JSONParser();
	
	protected String Qna_email;
	protected String Qna_phone;
	protected String Qna_title;
	protected String Qna_text;
	Context mcontext;
	ProgressDialog mProgress;
	VocaCallback mCallback;
	
	public Async_send_QnA(String email,String phone,String title,String text, Context context, VocaCallback callback){
		Qna_email = null;
		Qna_phone = null;
		Qna_title = null;
		Qna_text = null;
		
		Qna_email = email;
		Qna_phone = phone;
		Qna_title = title;
		Qna_text = text;
		mcontext = context;
		mCallback = callback;
	}
	
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//mProgress = ProgressDialog.show(mcontext, "Loading", "Please wait for a moment...");
		
	}

	
	protected String doInBackground(String... args) {
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("qna_email", Qna_email));
		params.add(new BasicNameValuePair("qna_phone", Qna_phone));
		params.add(new BasicNameValuePair("qna_title", Qna_title));
		params.add(new BasicNameValuePair("qna_text", Qna_text));
		
		Log.e("webtest", Qna_email);
		JSONObject json1 = jParser1.makeHttpRequest("http://www.lnslab.com/vocaslide/helpcenter.php", "GET", params);

		try {
			int success = json1.getInt(TAG_SUCCESS);	
			String message = json1.getString("message");	
				
			if (success ==  SEND_SUCCESS) {
				mCallback.Resonponse(json1);
			}else{
				mCallback.Exception();
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