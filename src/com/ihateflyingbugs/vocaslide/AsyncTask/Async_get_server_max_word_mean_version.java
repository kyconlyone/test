/*
 * Get max modify version of word and mean on server db word and mean table.
 * Modify contains that insert, update and delete.
 * Return value is different in parameter, insert, update, and delete.
 * 
 * parameter : (int) Indicate what to execute, insert(0), update(1), or delete(2)
 * return type : (String) Max version of corresponding execution
 */


package com.ihateflyingbugs.vocaslide.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.ihateflyingbugs.vocaslide.DownloadServerWordnMean;
import com.ihateflyingbugs.vocaslide.login.JSONParser;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class Async_get_server_max_word_mean_version extends AsyncTask<String, String, String> {
	
	//public Context context;
	public Async_get_server_max_word_mean_version(){
		
	}
	
	JSONParser jParser = new JSONParser();
	
	protected String doInBackground(String... args) {
		
		Log.e("downloadServerDB","Async get server max word mean version");
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		JSONObject json= jParser.makeHttpRequest("http://lnslab.com/vocaslide/get_server_max_word_mean_version.php", "GET", params);
		
		int version[] = new int[6];
		String verName[] = {"wrd_ins_ver", "wrd_upd_ver", "wrd_del_ver",
							"mn_ins_ver", "mn_upd_ver", "mn_del_ver"};
		
		for(int i=0;i<6;i++){
			try{
				version[i] = json.getInt(verName[i]);
			}catch(JSONException e){
				version[i]=0;
			}
		}
		
		try{
			DownloadServerWordnMean.downloadServerWordnMean(version[0], version[1], version[2], 
															version[3], version[4], version[5] );
		}catch(Exception e){
			Log.e("downloadServerDB", "Exception is occured");
			e.printStackTrace();
		}
		
		return null;
	}
	
	
}