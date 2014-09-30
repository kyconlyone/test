/*
 *  associated with 'DownloadServerDB.java' and 'DBPool.java'
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

import com.ihateflyingbugs.vocaslide.FAQFragment;
import com.ihateflyingbugs.vocaslide.data.DBPool;
import com.ihateflyingbugs.vocaslide.login.JSONParser;

public class Async_get_FAQ extends AsyncTask<String, String, String> {
	
	JSONParser jParser = new JSONParser();
	
	protected String app_version;
	VocaCallback mCallback;
	
	public Async_get_FAQ(VocaCallback callback){
		Log.e("Async_get_FAQ constuctor", "Async_get_FAQ constuctor");
		mCallback = callback;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
	}

	@Override
	protected String doInBackground(String... args) {
		
		Log.e("Async_get_FAQ doInBackground", "Async_get_FAQ doInBackground");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("version",null));
		
		JSONObject json_faq = jParser.makeHttpRequest("http://lnslab.com/vocaslide/FAQTable.php", "GET", params);
		
		Log.e("DownloadFAQ.downloadFAQ()", "DonwloadDB.downloadFAQ()");

		Log.e("webtest", "Async_get_FAQ : " + json_faq.toString());
		mCallback.Resonponse(json_faq);
		
		
		return null;
	}


	protected void onPostExecute(String file_url) {

	}

}
