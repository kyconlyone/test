/*
 *  associated with 'Async_get_indiv_answer.java'
 */

package com.ihateflyingbugs.vocaslide;

import org.json.JSONArray;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

import com.ihateflyingbugs.vocaslide.AsyncTask.Async_get_server_max_word_mean_version;
import com.ihateflyingbugs.vocaslide.AsyncTask.Async_get_server_word_mean;
import com.ihateflyingbugs.vocaslide.data.DBPool;
import com.ihateflyingbugs.vocaslide.login.MainActivitys;


public class DownloadServerWordnMean extends Fragment{

	private static Context context;
	public static DBPool db;
	 
	public DownloadServerWordnMean(Context _context){
		//get max word, mean version on server db
		db=DBPool.getInstance(getActivity());
		context = _context;//getActivity().getApplicationContext();
		new Async_get_server_max_word_mean_version().execute();
	}
	
	public static void downloadServerWordnMean(//Context context,
												int maxSrvWrdInsVer, int maxSrvWrdUpdVer, int maxSrvWrdDelVer, 
											   int maxSrvMnInsVer, int maxSrvMnUpdVer, int maxSrvMnDelVer){
		
		//add version column : w(m)_modify, w(m)_version
		db.addWordnMeanVersionColumn();
		db.fitNinthCurve();
		
		SharedPreferences maxDelVer = context.getSharedPreferences(MainActivitys.preName, Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
				
		//get max word, mean version on local db
		int maxLocWrdInsVer = db.getMaxWordVersion("INSERT");
		int maxLocWrdUpdVer = db.getMaxWordVersion("UPDATE");
		int maxLocWrdDelVer = maxDelVer.getInt("Wrd_DELETE", 0);
		
		int maxLocMnInsVer = db.getMaxMeanVersion("INSERT");
		int maxLocMnUpdVer = db.getMaxMeanVersion("UPDATE");
		int maxLocMnDelVer = maxDelVer.getInt("Mn_DELETE", 0);
			
		new Async_get_server_word_mean(maxLocWrdInsVer, maxLocWrdUpdVer, maxLocWrdDelVer,
									   maxLocMnInsVer, maxLocMnUpdVer, maxLocMnDelVer,
									   maxSrvWrdInsVer, maxSrvWrdUpdVer, maxSrvWrdDelVer,
									   maxSrvMnInsVer, maxSrvMnUpdVer, maxSrvMnDelVer).execute();
		
		if(maxLocWrdDelVer < maxSrvWrdDelVer){
			maxDelVer.edit().putInt("Wrd_DELETE", maxSrvWrdDelVer).commit();;
		}
		if(maxLocMnDelVer < maxSrvMnDelVer){
			maxDelVer.edit().putInt("Mn_DELETE", maxSrvMnDelVer).commit();;
		}
		
	}
	
	public static void modifyUserWord(int modify, JSONArray word_code, JSONArray word, JSONArray typ_class, JSONArray difficulty,
									  			  JSONArray priority, JSONArray real_time, JSONArray w_modify, JSONArray w_version){
		db.modifyUserWord(modify, word_code, word, typ_class, difficulty,
						  		  priority, real_time, w_modify, w_version);
	}
	
	public static void modifyUserMean(int modify, JSONArray mean_code, JSONArray _word_code, JSONArray _class, JSONArray mean, 
									  			  JSONArray priority, JSONArray m_modify, JSONArray m_version){
		db.modifyUserMean(modify, mean_code, _word_code, _class, mean,
						  		  priority, m_modify, m_version);
	}
	
}

















