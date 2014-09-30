package com.ihateflyingbugs.vocaslide.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ihateflyingbugs.vocaslide.DownloadServerWordnMean;
import com.ihateflyingbugs.vocaslide.data.DBPool;
import com.ihateflyingbugs.vocaslide.login.JSONParser;
import com.ihateflyingbugs.vocaslide.login.MainActivitys;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class Async_get_server_word_mean extends AsyncTask<String, String, String> {
	
	private static final int WORD = 0;
	private static final int MEAN = 1;

	private static final int INSERT = 0;
	private static final int UPDATE = 1;
	private static final int DELETE = 2;

	//local version
	private static int maxLocWrdInsVer;
	private static int maxLocWrdUpdVer;
	private static int maxLocWrdDelVer;
	private static int maxLocMnInsVer;
	private static int maxLocMnUpdVer;
	private static int maxLocMnDelVer;
	
	//server veresion
	private static int maxSrvWrdInsVer;
	private static int maxSrvWrdUpdVer;
	private static int maxSrvWrdDelVer;
	private static int maxSrvMnInsVer;  
	private static int maxSrvMnUpdVer;
	private static int maxSrvMnDelVer;
	
	public Async_get_server_word_mean(int _maxLocWrdInsVer, int _maxLocWrdUpdVer, int _maxLocWrdDelVer,
									  int _maxLocMnInsVer,  int _maxLocMnUpdVer,  int _maxLocMnDelVer,
									  int _maxSrvWrdInsVer, int _maxSrvWrdUpdVer, int _maxSrvWrdDelVer,
									  int _maxSrvMnInsVer,  int _maxSrvMnUpdVer,  int _maxSrvMnDelVer){
		
		
		//local version
		maxLocWrdInsVer = _maxLocWrdInsVer;
		maxLocWrdUpdVer = _maxLocWrdUpdVer;
		maxLocWrdDelVer = _maxLocWrdDelVer;
		maxLocMnInsVer  = _maxLocMnInsVer;
		maxLocMnUpdVer  = _maxLocMnUpdVer;
		maxLocMnDelVer  = _maxLocMnDelVer;
		
		//server version
		maxSrvWrdInsVer = _maxSrvWrdInsVer;
		maxSrvWrdUpdVer = _maxSrvWrdUpdVer;
		maxSrvWrdDelVer = _maxSrvWrdDelVer;
		maxSrvMnInsVer  = _maxSrvMnInsVer;
		maxSrvMnUpdVer  = _maxSrvMnUpdVer;
		maxSrvMnDelVer  = _maxSrvMnDelVer;
		
	}
	
	JSONParser jParser = new JSONParser();
	
	protected String doInBackground(String... args) {
		
		Log.e("downloadServerDB","Async get server word mean ");
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("maxLocWrdInsVer", ""+maxLocWrdInsVer));
		params.add(new BasicNameValuePair("maxLocWrdUpdVer", ""+maxLocWrdUpdVer));
		params.add(new BasicNameValuePair("maxLocWrdDelVer", ""+maxLocWrdDelVer));
		params.add(new BasicNameValuePair("maxLocMnInsVer",  ""+maxLocMnInsVer));
		params.add(new BasicNameValuePair("maxLocMnUpdVer",  ""+maxLocMnUpdVer));
		params.add(new BasicNameValuePair("maxLocMnDelVer",  ""+maxLocMnDelVer));
		
		params.add(new BasicNameValuePair("maxSrvWrdInsVer", ""+maxSrvWrdInsVer));
		params.add(new BasicNameValuePair("maxSrvWrdUpdVer", ""+maxSrvWrdUpdVer));
		params.add(new BasicNameValuePair("maxSrvWrdDelVer", ""+maxSrvWrdDelVer));
		params.add(new BasicNameValuePair("maxSrvMnInsVer",  ""+maxSrvMnInsVer));
		params.add(new BasicNameValuePair("maxSrvMnUpdVer",  ""+maxSrvMnUpdVer));
		params.add(new BasicNameValuePair("maxSrvMnDelVer",  ""+maxSrvMnDelVer));
		
		JSONObject json = jParser.makeHttpRequest("http://lnslab.com/vocaslide/get_server_word_mean.php", "GET", params);
		
		try{
			
			Log.e("downloadServerDB","Async_get_server_word_mean:get JSONArray");
			//get JSONArray
			//word insert
			if(maxLocWrdInsVer < maxSrvWrdInsVer){
				JSONArray wrdIns_word_code  = json.getJSONArray("wrd_ins_word_code");
				JSONArray wrdIns_word  	    = json.getJSONArray("wrd_ins_word");
				JSONArray wrdIns_typ_class  = json.getJSONArray("wrd_ins_typ_class");
				JSONArray wrdIns_difficulty = json.getJSONArray("wrd_ins_difficulty");
				JSONArray wrdIns_priority   = json.getJSONArray("wrd_ins_priority");
				JSONArray wrdIns_real_time  = json.getJSONArray("wrd_ins_real_time");
				JSONArray wrdIns_w_modify   = json.getJSONArray("wrd_ins_w_modify");
				JSONArray wrdIns_w_version  = json.getJSONArray("wrd_ins_w_version");
				
				DownloadServerWordnMean.modifyUserWord(INSERT, wrdIns_word_code, wrdIns_word, wrdIns_typ_class, wrdIns_difficulty, 
						   									   wrdIns_priority, wrdIns_real_time, wrdIns_w_modify, wrdIns_w_version);
			}
			
			//word update
			if(maxLocWrdUpdVer < maxSrvWrdUpdVer){
				JSONArray wrdUpd_word_code  = json.getJSONArray("wrd_upd_word_code");
				JSONArray wrdUpd_word  	    = json.getJSONArray("wrd_upd_word");
				JSONArray wrdUpd_typ_class  = json.getJSONArray("wrd_upd_typ_class");
				JSONArray wrdUpd_difficulty = json.getJSONArray("wrd_upd_difficulty");
				JSONArray wrdUpd_priority   = json.getJSONArray("wrd_upd_priority");
				JSONArray wrdUpd_real_time  = json.getJSONArray("wrd_upd_real_time");
				JSONArray wrdUpd_w_modify   = json.getJSONArray("wrd_upd_w_modify");
				JSONArray wrdUpd_w_version  = json.getJSONArray("wrd_upd_w_version");
				
				DownloadServerWordnMean.modifyUserWord(UPDATE, wrdUpd_word_code, wrdUpd_word, wrdUpd_typ_class, wrdUpd_difficulty, 
															   wrdUpd_priority, wrdUpd_real_time, wrdUpd_w_modify, wrdUpd_w_version);
			}
			
			//word delete
			if(maxLocWrdDelVer < maxSrvWrdDelVer){
				JSONArray wrdDel_word_code  = json.getJSONArray("wrd_del_word_code");
				JSONArray wrdDel_word  	    = json.getJSONArray("wrd_del_word");
				JSONArray wrdDel_typ_class  = json.getJSONArray("wrd_del_typ_class");
				JSONArray wrdDel_difficulty = json.getJSONArray("wrd_del_difficulty");
				JSONArray wrdDel_priority   = json.getJSONArray("wrd_del_priority");
				JSONArray wrdDel_real_time  = json.getJSONArray("wrd_del_real_time");
				JSONArray wrdDel_w_modify   = json.getJSONArray("wrd_del_w_modify");
				JSONArray wrdDel_w_version  = json.getJSONArray("wrd_del_w_version");
				
				DownloadServerWordnMean.modifyUserWord(DELETE, wrdDel_word_code, wrdDel_word, wrdDel_typ_class, wrdDel_difficulty, 
						   									   wrdDel_priority, wrdDel_real_time, wrdDel_w_modify, wrdDel_w_version);
			}
			
			//mean insert
			if(maxLocMnInsVer < maxSrvMnInsVer){
				JSONArray mnIns_mean_code = json.getJSONArray("mn_ins_mean_code");
				JSONArray mnIns_word_code = json.getJSONArray("mn_ins_word_code");
				JSONArray mnIns_class     = json.getJSONArray("mn_ins_class");
				JSONArray mnIns_mean      = json.getJSONArray("mn_ins_mean");
				JSONArray mnIns_priority  = json.getJSONArray("mn_ins_priority");
				JSONArray mnIns_m_modify  = json.getJSONArray("mn_ins_m_modify");
				JSONArray mnIns_m_version = json.getJSONArray("mn_ins_m_version");
				
				DownloadServerWordnMean.modifyUserMean(INSERT, mnIns_mean_code, mnIns_word_code, mnIns_class, mnIns_mean,
						   									   mnIns_priority, mnIns_m_modify, mnIns_m_version);
			}
			
			//mean update
			if(maxLocMnUpdVer < maxSrvMnUpdVer){
				JSONArray mnUpd_mean_code = json.getJSONArray("mn_upd_mean_code");
				JSONArray mnUpd_word_code = json.getJSONArray("mn_upd_word_code");
				JSONArray mnUpd_class     = json.getJSONArray("mn_upd_class");
				JSONArray mnUpd_mean      = json.getJSONArray("mn_upd_mean");
				JSONArray mnUpd_priority  = json.getJSONArray("mn_upd_priority");
				JSONArray mnUpd_m_modify  = json.getJSONArray("mn_upd_m_modify");
				JSONArray mnUpd_m_version = json.getJSONArray("mn_upd_m_version");
			
				DownloadServerWordnMean.modifyUserMean(UPDATE, mnUpd_mean_code, mnUpd_word_code, mnUpd_class, mnUpd_mean,
															   mnUpd_priority, mnUpd_m_modify, mnUpd_m_version);
			}
			
			
			//mean delete
			if(maxLocMnDelVer < maxSrvMnDelVer){
				JSONArray mnDel_mean_code = json.getJSONArray("mn_del_mean_code");
				JSONArray mnDel_word_code = json.getJSONArray("mn_del_word_code");
				JSONArray mnDel_class     = json.getJSONArray("mn_del_class");
				JSONArray mnDel_mean      = json.getJSONArray("mn_del_mean");
				JSONArray mnDel_priority  = json.getJSONArray("mn_del_priority");
				JSONArray mnDel_m_modify  = json.getJSONArray("mn_del_m_modify");
				JSONArray mnDel_m_version = json.getJSONArray("mn_del_m_version");
			
				DownloadServerWordnMean.modifyUserMean(DELETE, mnDel_mean_code, mnDel_word_code, mnDel_class, mnDel_mean,
						   									   mnDel_priority, mnDel_m_modify, mnDel_m_version);
			}
			
			
		}catch(JSONException e){
			Log.e("downloadServerDB","Async_get_server_word_mean:JSON Exception");
			e.printStackTrace();
		}catch(Exception e){
			Log.e("downloadServerDB","Async_get_server_word_mean:Exception");
			e.printStackTrace();
		}
		
		return null;
	}

	

}