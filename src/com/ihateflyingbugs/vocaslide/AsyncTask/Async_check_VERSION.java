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

import com.ihateflyingbugs.vocaslide.data.DBPool;
import com.ihateflyingbugs.vocaslide.login.JSONParser;

public class Async_check_VERSION extends AsyncTask<String, String, String> {
	
	private static final String TAG_SUCCESS = "success";
	static final int VERSION_EXCEPTION= 0;
	public static final int VERSION_SUCCESS= 1;
	VocaCallback mCallback ;
	
	JSONParser jParser = new JSONParser();
	
	protected String app_version;
	
	
	public Async_check_VERSION(VocaCallback callback){
		Log.e("Async_get_FAQ constuctor", "Async_get_FAQ constuctor");
		this.mCallback = callback;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
	}

	@Override
	protected String doInBackground(String... args) {
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("version",null));
		JSONObject json_version = null;
		
				
		try {
			
			json_version = jParser.makeHttpRequest("http://lnslab.com/vocaslide/Check_Version.php", "GET", params);
			
			int success = json_version.getInt(TAG_SUCCESS);	
			String message = json_version.getString("message");
			String version = json_version.getString("version");
			String isUpdate = json_version.getString("isUpdate");
				
			
			if (success ==  VERSION_SUCCESS) {
				mCallback.Resonponse(json_version);
			}else{
				mCallback.Exception();
			}
			
//			if (success ==  VERSION_SUCCESS) {
//				SplashActivity.get_version(VERSION_SUCCESS, message, version,isUpdate);
//			}else{
//				SplashActivity.get_version(VERSION_EXCEPTION, message, null,null);
//			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}catch (NullPointerException e) {
			// TODO: handle exception
			mCallback.Exception();
		}
		return null;
	}


	protected void onPostExecute(String file_url) {

	}
	
	

}
