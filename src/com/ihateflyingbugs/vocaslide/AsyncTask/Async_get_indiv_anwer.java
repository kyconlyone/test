/*
 *  associated with 'DownloadIndivAnswer.java'
 */

package com.ihateflyingbugs.vocaslide.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.ihateflyingbugs.vocaslide.QnAListFragment;
import com.ihateflyingbugs.vocaslide.data.Config;
import com.ihateflyingbugs.vocaslide.data.DBPool;
import com.ihateflyingbugs.vocaslide.login.JSONParser;

public class Async_get_indiv_anwer extends AsyncTask<String, String, String> {
	
	JSONParser jParser;
	
	protected String ID;		//id
	protected String pNumber;	//phone number
	protected String title;		//title
	protected String q_date;	//question registered date
	protected String question;	//question
	
	VocaCallback mCallback;
	
	public Async_get_indiv_anwer(VocaCallback callback){
		Log.e("Async_get_indiv_answer constuctor", "Async_get_indiv_answer constuctor");
		mCallback = callback;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
	}

	@Override
	protected String doInBackground(String... args) {
		
		Log.e("Async_get_indiv_answer doInBackground", "Async_get_indiv_answer doInBackground");
		 jParser = new JSONParser();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("ID",	Config.USER_ID));
		
		Log.e("success", "ID     "+Config.USER_ID);
		JSONObject json = null;
		
		try {
			json= jParser.makeHttpRequest("http://lnslab.com/vocaslide/Download_IndivAnswer.php", "GET", params);
			mCallback.Resonponse(json);
		} catch (NullPointerException e) {
			// TODO: handle exception
			mCallback.Exception();
		}

		Log.e("success", "answer doInBackground	     "+json.toString());
	
		
	
		
		//QnAListFragment.donwloadIndivQues(json);
		
		return null;
	}

	protected String getID(){
		//get ID
		String ID="kang";
		return ID;
	}

	
}
