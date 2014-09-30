package com.ihateflyingbugs.vocaslide.service;

import java.util.HashMap;
import java.util.Map;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.vocaslide.ServiceLogDataFile;
import com.ihateflyingbugs.vocaslide.WordListFragment;
import com.ihateflyingbugs.vocaslide.data.Config;
import com.ihateflyingbugs.vocaslide.data.DBPool;
import com.ihateflyingbugs.vocaslide.data.Date;
import com.ihateflyingbugs.vocaslide.login.MainActivitys;

public class DBService extends Service {
	public SharedPreferences mPreference;
	private int count = 0;
	private int dayPerTime = 24;
	private int weekPerDay = 7;
	ServiceLogDataFile service_log_file;
	Date date= new Date();
	
	Map<String, String> EndInfoParams = new HashMap<String, String>();
	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		
		throw new UnsupportedOperationException("Not yet implemented");
		
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.d("alma", "onCreate start");
		service_log_file = new ServiceLogDataFile(getApplicationContext());
		service_log_file.input_LogData_in_file(WordListFragment.get_date()+"_Service Created : start\r\n");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "Service Destroy", 1).show();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub	
		
		Log.d("alma", "onStartCommand start");
		Long time;
		
		try {

			time = Long.valueOf(getMySharedPreferences(MainActivitys.GpreTime));
		} catch (NumberFormatException e) {
			// TODO: handle exception
			time = System.currentTimeMillis()-(AlarmManager.INTERVAL_HOUR);
		}
		
		//Float fl = (float) ((System.currentTimeMillis()-time)/300000.0);
		
		Float fl = (float) ((System.currentTimeMillis()-time)/3600000.0);
		service_log_file.input_LogData_in_file(WordListFragment.get_date()+"_Service Running : time is "+Math.round(fl)+"hour after \r\n");
		Log.e("almas", "calc score    " +String.valueOf(fl));
		
		if(Math.round(fl)>=1){
			
			DBPool db = DBPool.getInstance(getApplicationContext()); 
			
			//db.calcProbility();
			
			int Time_to_hour= Math.round(fl);
			//int Time_to_hour= Math.round(1);
			Log.e("almas", "Time to hour : " + String.valueOf(Math.round(fl)));
			
			if(Config.isNetworkAvailable(getApplicationContext())
					&&!mPreference.getString(MainActivitys.GpreID, "000000").equals("000000")){
				
				new Async_upload_sqlite_file(date.get_currentTime(), getApplicationContext()).execute();
				
			}
			db.calcScore(Time_to_hour);
			
			db.putDay_Of_Study();
			
			setMySharedPreferences(MainActivitys.GpreTime, String.valueOf(System.currentTimeMillis()));
			MainActivitys.GpreAccessDuration++;
		}
		if(MainActivitys.GpreAccessDuration>=dayPerTime*weekPerDay && MainActivitys.GpreFlag==true){	//user doesn't access application more than 7 days
			Log.e("not accessed long time","not accessed long time");
			EndInfoParams.put("EndTime", getMySharedPreferences(MainActivitys.GpreEndTime));
			EndInfoParams.put("TotalReviewCnt", getMySharedPreferences(MainActivitys.GpreTotalReviewCnt));
			EndInfoParams.put("TodayReviewCnt", getMySharedPreferences(MainActivitys.GpreTodayReviewCnt));
			EndInfoParams.put("TodayLearnCnt", getMySharedPreferences(MainActivitys.GpreTodayLearnCnt));
			FlurryAgent.logEvent(""+weekPerDay+"동안 접속하지 않음,DBService,1",EndInfoParams,true);
			MainActivitys.GpreFlag=false;
			//setMySharedPreferences(MainActivitys.GpreFlag, "false");
		}
		
		//		Calendar mCalendar = Calendar.getInstance();
		//		mCalendar.setTimeInMillis(System.currentTimeMillis());
		//		
		//		int hour = mCalendar.getTime().getHours();
		//		int minute = mCalendar.getTime().getMinutes();
		//		int second = mCalendar.getTime().getSeconds();

		//		writeFile(count + "   " + hour + ":" + minute + ":" + second + "\n");
		Log.e("almas", "calc end    "+String.valueOf(System.currentTimeMillis())+"    "+String.valueOf(time)+"    "+String.valueOf(AlarmManager.INTERVAL_HOUR));
		return super.onStartCommand(intent, flags, startId);
	}


	private String getMySharedPreferences(String _key) {
		if(mPreference == null){
			mPreference =  getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
		}
		return mPreference.getString(_key, "");
	}

	private void setMySharedPreferences(String _key, String _value) {
		if(mPreference == null){
			mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
		}  
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putString(_key, _value);
		editor.commit();
	}
}
