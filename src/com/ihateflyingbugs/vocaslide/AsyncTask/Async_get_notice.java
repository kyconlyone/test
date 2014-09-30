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

import com.ihateflyingbugs.vocaslide.NoticeFragment;
import com.ihateflyingbugs.vocaslide.data.DBPool;
import com.ihateflyingbugs.vocaslide.login.JSONParser;

public class Async_get_notice extends AsyncTask<String, String, String> {
	
	JSONParser jParser = new JSONParser();
	
	protected String app_version;
	VocaCallback mCallback;
	
	public Async_get_notice(VocaCallback callback){
		Log.e("Async_get_notice constuctor", "Async_get_notice constuctor");
		mCallback = callback;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
	}

	@Override
	protected String doInBackground(String... args) {
		
		Log.e("Async_get_notice doInBackground", "Async_get_notice doInBackground");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("version",null));
		
		JSONObject json_notice = jParser.makeHttpRequest("http://lnslab.com/vocaslide/NoticeTable.php", "GET", params);
		//JSONObject json_update = jParser.makeHttpRequest("http://lnslab.com/kig_test/updateTable.php", "GET", params);
		
		Log.e("DownloadNotice.downloadServerDB()", "DonwloadDB.downloadServerDB()");
		mCallback.Resonponse(json_notice);
		//DownloadServerNotice.
		//DownloadServerNotice.downloadServerDB(json_insert, json_update);	
		
		return null;
	}


	protected void onPostExecute(String file_url) {

	}

}
