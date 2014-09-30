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

import com.ihateflyingbugs.vocaslide.QnAListFragment;
import com.ihateflyingbugs.vocaslide.data.DBPool;
import com.ihateflyingbugs.vocaslide.login.JSONParser;

public class Async_get_indiv_ques extends AsyncTask<String, String, String> {
	
	JSONParser jParser = new JSONParser();
	
	protected String app_version;
	
	public Async_get_indiv_ques(){
		Log.e("Async_get_individual_question constuctor", "Async_get_individual_question constuctor");
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
	}

	@Override
	protected String doInBackground(String... args) {
		
		Log.e("Async_get_individual_question doInBackground", "Async_get_individual_question doInBackground");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("version",null));
		
		JSONObject json_indivQues = jParser.makeHttpRequest("http://lnslab.com/vocaslide/IndivQuesTable.php", "GET", params);
		
		Log.e("DownloadIndivQues.downloadIndivQues()", "DonwloadDB.downloadIndivQues()");
		//QnAListFragment.donwloadIndivQues(json_indivQues);
		
		return null;
	}


	protected void onPostExecute(String file_url) {

	}

}
