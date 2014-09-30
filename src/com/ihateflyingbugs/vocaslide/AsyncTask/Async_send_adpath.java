/*
 *  associated with 'DownloadServerDB.java' and 'DBPool.java'
 */

package com.ihateflyingbugs.vocaslide.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ihateflyingbugs.vocaslide.Get_my_uuid;
import com.ihateflyingbugs.vocaslide.login.JSONParser;

public class Async_send_adpath extends AsyncTask<String, String, String> {
	
	private static final String TAG_SUCCESS = "success";
	static final int ADPATH_EXCEPTION= 0;
	public static final int ADPATH_SUCCESS= 1;
	
	JSONParser jParser = new JSONParser();
	
	protected String app_version;
	String mValue;
	String mEnvironment;
	Context mContext;
	
	public Async_send_adpath(String value, String environment,Context context){
		mValue = value;
		mEnvironment = environment;
		mContext = context;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
	}

	@Override
	protected String doInBackground(String... args) {
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("Uid2", mValue));
		params.add(new BasicNameValuePair("DeviceId",Get_my_uuid.get_Device_id(mContext)));
		params.add(new BasicNameValuePair("DownloadBrowser", mEnvironment));
		Log.e("bookmark",  ""+mValue+"        "+mEnvironment+"       "+Get_my_uuid.get_Device_id(mContext));
		try {
			JSONObject json_version = jParser.makeHttpRequest("http://hott.kr/appdown/CheckDownload.php", "GET", params);
		}catch (NullPointerException e) {
			// TODO: handle exception
		}
		return null;
	}


	protected void onPostExecute(String file_url) {

	}
	
	

}
