/*
 *  associated with 'UploadIndivQues.java'
 */

package com.ihateflyingbugs.vocaslide.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ihateflyingbugs.vocaslide.QnAFragment;
import com.ihateflyingbugs.vocaslide.data.Config;
import com.ihateflyingbugs.vocaslide.data.DBPool;
import com.ihateflyingbugs.vocaslide.login.JSONParser;
public class Async_send_inidv_ques extends AsyncTask<String, String, String> {
	private static final String TAG_SUCCESS = "success";
	public static final int INDIVQNA_EXCEPTION= 0;
	public static final int INDIVQNA_SUCCESS= 1;
	JSONParser jParser = new JSONParser();
	
	protected String ID;		//id
	protected String pNumber;	//phone number
	protected String title;		//title
	protected String q_date;	//question registered date
	protected String question;	//question
	protected VocaCallback mCallback;
	
	public Async_send_inidv_ques(){
		Log.e("A_Test_Send constuctor", "A_Test_Send constuctor");
	}
	
	public Async_send_inidv_ques(String email, String phone, String text, VocaCallback callback) {
		// TODO Auto-generated constructor stub
		this.ID = email;
		this.pNumber = phone;
		this.question = text;
		mCallback = callback;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
	}

	@Override
	protected String doInBackground(String... args) {
		
		Log.e("Async_test doInBackground", "Async_test doInBackground");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("ID",		Config.USER_ID));
		params.add(new BasicNameValuePair("pNumber", pNumber));
		params.add(new BasicNameValuePair("question",question));
		
		JSONObject json = null;
		
		try {
			json= jParser.makeHttpRequest("http://lnslab.com/vocaslide/Upload_IndivQues.php", "GET", params);
			mCallback.Resonponse(json);
		} catch (NullPointerException e) {
			// TODO: handle exception
			mCallback.Exception();
		}
		
		
		//UploadIndivQues.uploadIndivQues(json);
		
		return null;
	}
}
