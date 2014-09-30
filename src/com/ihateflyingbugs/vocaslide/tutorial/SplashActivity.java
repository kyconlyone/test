package com.ihateflyingbugs.vocaslide.tutorial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Browser;
import android.provider.Browser.BookmarkColumns;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.flurry.android.impl.analytics.FlurryAnalyticsModule;
import com.google.analytics.tracking.android.EasyTracker;
import com.ihateflyingbugs.vocaslide.Get_my_uuid;
import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.AsyncTask.Async_check_VERSION;
import com.ihateflyingbugs.vocaslide.AsyncTask.Async_check_id;
import com.ihateflyingbugs.vocaslide.AsyncTask.Async_send_adpath;
import com.ihateflyingbugs.vocaslide.AsyncTask.VocaCallback;
import com.ihateflyingbugs.vocaslide.data.Config;
import com.ihateflyingbugs.vocaslide.data.Date;
import com.ihateflyingbugs.vocaslide.indicator.SampleCirclesSnap;
import com.ihateflyingbugs.vocaslide.login.AlertDialogManager;
import com.ihateflyingbugs.vocaslide.login.ConnectionDetector;
import com.ihateflyingbugs.vocaslide.login.JSONParser;
import com.ihateflyingbugs.vocaslide.login.MainActivitys;
import com.ihateflyingbugs.vocaslide.login.WriteUserInfoActivity;
import com.kakao.APIErrorResult;
import com.kakao.MeResponseCallback;
import com.kakao.Session;
import com.kakao.SessionCallback;
import com.kakao.UserManagement;
import com.kakao.UserProfile;
import com.kakao.exception.KakaoException;

public class SplashActivity extends Activity {


	private static SharedPreferences mPreference;
	private final SessionCallback mySessionCallback = new MySessionStatusCallback();
	static boolean isStart = false;
	static Context mContext;
	static Activity activity;
	Handler handler;
	ProgressBar pb_splash;

	static boolean isActivity = false;
	static int flurry_time = 30;
	static final int FLURRY_SUCCESS= 1;

	String check_firtst = "0";


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		Log.d("activitygg", "oncreate         "+ flurry_time);


		handler = new Handler();
		final String versionSDK = Build.VERSION.SDK;

		mPreference =  getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);



		mContext = getApplicationContext();
		activity = SplashActivity.this;

		pb_splash = (ProgressBar)findViewById(R.id.pb_splash);


		if(!Config.isNetworkAvailable(getApplicationContext())){
			FlurryAgent.logEvent("SplashActivity:NotAccessInternet");
			isFinish = true;
			AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this).create();
			// Setting Dialog Title
			alertDialog.setTitle("인터넷을 연결할 수 없습니다.");

			// Setting Dialog Message
			alertDialog.setMessage("연결 상태를 확인한 후 다시 시도해 주세요.");


			alertDialog.setIcon(R.drawable.icon36);

			// Setting OK Button
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});

			// Showing Alert Message
			alertDialog.show();
			return;
		}else{
			JSONParser jParser = new JSONParser();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			JSONObject json_version = jParser.makeHttpRequest("http://lnslab.com/vocaslide/GetFlurrySessionTime.php", "GET", params);

			try {
				int success = json_version.getInt(Config.TAG_SUCCESS);	
				String message = json_version.getString("message");
				int time = json_version.getInt("time");

				if (success ==  FLURRY_SUCCESS) {
					flurry_time = time;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}



		Log.e("os_version", versionSDK);


		if(Integer.parseInt(versionSDK)<14){
			new AlertDialog.Builder(getApplicationContext())
			.setMessage("현재 안드로이드 버전이 낮아 접속하실수 없습니다. 버전 업그래드후 다시 실행해주시기 바랍니다.")
			.setPositiveButton("확인", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

					FlurryAgent.logEvent("SplashActivity:HaveLowSDKVersion_"+versionSDK);
					finish();

				}
			}).show();
		}



	}



	boolean isFinish = false;
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		check_firtst = mPreference.getString(MainActivitys.GpreFirtst, "0");
		Log.e("activitygg", ""+check_firtst);
		isActivity = false;


		if(!Config.isNetworkAvailable(getApplicationContext())){
			// Check if Internet present
			return;
		}

		//				 boolean installed  =   appInstalledOrNot("com.google.android.tts");  
		//				 if(!installed&&!isFinish){
		//					 Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.tts"));
		//						startActivity(browserIntent);
		//						finish();
		//						return;
		//				 }

		if(Session.initializeSession(this, mySessionCallback)){
			// 1. 세션을 갱신 중이면, 프로그레스바를 보이거나 버튼을 숨기는 등의 액션을 취한다
			pb_splash.setVisibility(View.VISIBLE);
			Log.e("activitygg", "initial");

		} else if (Session.getCurrentSession().isOpened()){
			// 2. 세션이 오픈된된 상태이면, 다음 activity로 이동한다.
			pb_splash.setVisibility(View.VISIBLE);
			onSessionOpened();


		}else{
			if(check_firtst.equals("0")){
				if(geturl()){
					//php호출하기
					Intent intent = new Intent(SplashActivity.this, SampleCirclesSnap.class);
					startActivity(intent);
					finish();
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}else{
					if(isPackageInstalled("com.nhn.android.search", getApplicationContext())){
								 
						Log.e("activitygg", "naversearchapp://inappbrowser?url=http%3A%2F%2Fhott.kr/" +
								"appdown/CheckDownload.php?DeviceId="+ Get_my_uuid.get_Device_id(mContext)
								+"&DownloadBrowser=Naver&target=new&version=1");
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("naversearchapp://inappbrowser?url=http%3A%2F%2Fhott.kr%2F" +
								"appdown%2FCheckDownload.php%3FDeviceId%3D"+Get_my_uuid.get_Device_id(mContext)
								+"%26DownloadBrowser%3DNaver&target=new&version=1"));
						startActivity(browserIntent);
						finish();
					}else{
						Intent intent = new Intent(SplashActivity.this, SampleCirclesSnap.class);
						startActivity(intent);
						finish();
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					}
				}


			}else{
				Log.e("activitygg", "!check_firtst.equals");
				Intent intent = new Intent(SplashActivity.this, SampleCirclesSnap.class);
				startActivity(intent);
				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}


		}
	}



	private class MySessionStatusCallback implements SessionCallback {
		@Override
		public void onSessionOpened() {
			// 프로그레스바를 보이고 있었다면 중지하고 세션 오픈후 보일 페이지로 이동
			pb_splash.setVisibility(View.GONE);
			Log.e("activitygg", "onSessionOpened");
			SplashActivity.this.onSessionOpened();
		}

		@Override
		public void onSessionClosed(final KakaoException exception) {
			// 프로그레스바를 보이고 있었다면 중지하고 세션 오픈을 못했으니 다시 로그인 버튼 노출.	
			Log.e("activitygg", "onSessionClosed");
			pb_splash.setVisibility(View.GONE);
			if(check_firtst.equals("0")){
				if(geturl()){
					//php호출하기
					Intent intent = new Intent(SplashActivity.this, SampleCirclesSnap.class);
					startActivity(intent);
					finish();
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}else{
					if(isPackageInstalled("com.nhn.android.search", getApplicationContext())){
						
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("naversearchapp://inappbrowser?url=http%3A%2F%2Fhott.kr%2F" +
								"appdown%2FCheckDownload.php%3FDeviceId%3D"+Get_my_uuid.get_Device_id(mContext)
								+"%26DownloadBrowser%3DNaver&target=new&version=1"));startActivity(browserIntent);
						finish();
					}else{
						Intent intent = new Intent(SplashActivity.this, SampleCirclesSnap.class);
						startActivity(intent);
						finish();
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					}
				}

			}else{
				
				Intent intent = new Intent(SplashActivity.this, SampleCirclesSnap.class);
				startActivity(intent);
				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}


		}

	}


	private boolean isPackageInstalled(String packagename, Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}



	protected void onSessionOpened(){


		UserManagement.requestMe(new MeResponseCallback() {


			@Override
			protected void onSuccess(UserProfile userProfile) {
				// TODO Auto-generated method stub
				if (userProfile != null)
					userProfile.saveUserToCache();
				Config.USER_ID  =String.valueOf(userProfile.getId());
				mPreference.edit().putString(MainActivitys.GpreID, Config.USER_ID ).commit();
				if(mPreference.getString(MainActivitys.GpreName, "0").equals("0")){
					mPreference.edit().putString(MainActivitys.GpreName, userProfile.getNickname()).commit();
					Config.NAME =  userProfile.getNickname();
				}

				FlurryAgent.setUserId(Config.USER_ID);
				if(!mPreference.getString(MainActivitys.GpreGender, "-1").equals("-1")){
					byte gender =com.flurry.android.Constants.UNKNOWN;
					String getGender = mPreference.getString(MainActivitys.GpreGender, "-1");
					if(getGender.equals("2")){
						gender = com.flurry.android.Constants.FEMALE;
					}else if(getGender.equals("1")){
						gender = com.flurry.android.Constants.MALE;
					}
					FlurryAgent.setGender(gender);
					String Birth = mPreference.getString(MainActivitys.GpreBirth, "990405");
					FlurryAgent.setAge(Config.getAge(Birth));

				}

				setMySharedPreferences(MainActivitys.GpreID, Config.USER_ID);

				get_Version();

			}

			@Override
			protected void onSessionClosedFailure(APIErrorResult errorResult) {
				// TODO Auto-generated method stub
			}

			@Override
			protected void onNotSignedUp() {
				// TODO Auto-generated method stub

			}

			@Override
			protected void onFailure(APIErrorResult errorResult) {
				// TODO Auto-generated method stub

			}
		});

	}

	protected void get_Version() {
		// TODO Auto-generated method stub
		new Async_check_VERSION(new VocaCallback() {

			@Override
			public void Exception() {
				// TODO Auto-generated method stub

				FlurryAgent.logEvent("SplashActivity:NoGetVersionReponse");
			}

			@Override
			public void Resonponse(JSONObject response) {
				// TODO Auto-generated method stub

				String isUpdate = "false";
				String version  = "";

				try {
					isUpdate = response.getString("isUpdate");
					version = response.getString("version");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					PackageManager packageManager = getPackageManager();
					PackageInfo infor = null;
					try {
						infor = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					String cur_version = infor.versionName;
					int code = infor.versionCode;
					Config.VERSION= cur_version;
					Log.e("versioninfo", ""+cur_version+ "      "+version);

					Date dated = new Date();
					if(cur_version.equals(version)){
						if(!isStart){
							Log.e("activitygg", "check_id   :     Call check_id"); 
							check_id(Config.USER_ID, Config.VERSION, mContext);
							isStart =true;
						}else{
							isStart =false;
						}
					}else{
						if(isUpdate.equals("true")){
							activity.runOnUiThread(new Runnable() {
								public void run() {

									AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
									dialog.setTitle("VersionUpdate");
									dialog.setMessage("현재 App의 버전이 낮아 마켓에서의 업그레이드가 필요합니다.");
									dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											FlurryAgent.logEvent("SplashActivity:NeedVersionUpdate");
											Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.ihateflyingbugs.vocaslide"));
											activity.startActivity(browserIntent);
											activity.finish();
										}
									});
									dialog.create().show();  
								}
							});


						}else{

							Log.e("activitygg", "get_version");
							check_id(Config.USER_ID, Config.VERSION, mContext);
						}
					}

				}

			}
		}).execute();


	}

	private void setMySharedPreferences(String _key, String _value) {
		if(mPreference == null){
			mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
		}  
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putString(_key, _value);
		editor.commit();
	}


	public String getVersionName(Context context) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}





	private boolean appInstalledOrNot(String uri) {
		PackageManager pm = getPackageManager();
		boolean app_installed = false;
		try {
			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			app_installed = true;
		}
		catch (PackageManager.NameNotFoundException e) {
			app_installed = false;
		}
		return app_installed ;
	}

	public void check_id(String id, String version, Context context){
		Log.e("activitygg", "check_id   : start");
		new Async_check_id(id, version, context, new VocaCallback() {

			@Override
			public void Resonponse(JSONObject response) {
				// TODO Auto-generated method stub
				Log.e("activitygg", "check_id   : Resonponse");
				FlurryAgent.setUserId(Config.USER_ID);		

				int success = 0;
				try {
					success = response.getInt(Config.TAG_SUCCESS);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				if (!isActivity) {

					if(success == Async_check_id.ID_DUPLICATION){
						Log.e("activitygg", "check_id   : ID_DUPLICATION");
						final Intent intent = new Intent(mContext, MainActivity.class);
						activity.startActivity(intent);
						activity.finish();
						isActivity= true;
					}else {
						Log.e("activitygg", "check_id   : Not ID_DUPLICATION");
						final Intent intent = new Intent(mContext, WriteUserInfoActivity.class);
						activity.startActivity(intent);
						activity.finish();
						isActivity= true;
					}
				}else{

				}
			}

			@Override
			public void Exception() {
				// TODO Auto-generated method stub
				Log.e("activitygg", "check_id   : Exception");

			}
		}).execute();
	}
	

	public boolean geturl(){
		String[] proj = new String[] { Browser.BookmarkColumns. TITLE, Browser.BookmarkColumns.URL, BookmarkColumns.DATE };
		//android.net.Uri uriCustom = Browser.BOOKMARKS_URI;
		boolean isExist = false;

		android.net.Uri[] uriCustom = {Browser.BOOKMARKS_URI, android.net.Uri.parse("content://com.android.chrome.browser/bookmarks")};
		

		String sel = Browser.BookmarkColumns.BOOKMARK + " = 0"; // 0 = history, 1 = bookmark
		
		String browser = null;
		for(int i =0; i<uriCustom.length;i++){
			switch (i) {
			case 0:
				browser = "Default";
				break;
			case 1:
				browser = "Chrome";
				break;
			default:
				browser = "Default";
				break;
			}
			Cursor mCur = getContentResolver().query(uriCustom[i], proj, sel, null, Browser.BookmarkColumns.DATE + " DESC");
			try {
				mCur.getCount();
			} catch (NullPointerException e) {
				// TODO: handle exception
				Log.e("bookmark", "NullPointerException            " +i);
				continue;
			}

			mCur.moveToFirst();
			@SuppressWarnings("unused")
			String title = "";
			@SuppressWarnings("unused")
			String url = "";

			if (mCur.moveToFirst() && mCur.getCount() > 0) {
				boolean cont = true;
				while (mCur.isAfterLast() == false && cont) {
					title = mCur.getString(mCur.getColumnIndex(Browser.BookmarkColumns.TITLE));

					url = mCur.getString(mCur.getColumnIndex(Browser.BookmarkColumns.URL));

					Log.e("bookmark", ""+url);
					if(url.contains("hott.kr/appdown/indexs.php")){
						try {
							String sub = url.split("Uid=")[1];

							isExist = true;

							Log.e("bookmark", ""+isExist);
							new Async_send_adpath(sub, browser ,getApplicationContext()).execute();

						} catch (ArrayIndexOutOfBoundsException e) {
							// TODO: handle exception
						}
						
					}
					// Do something with title and url
					if(isExist){
						break;
					}
					mCur.moveToNext();
				}
			}
			mCur.close();
		}
		Log.e("bookmark", ""+isExist);
		return isExist;
	}


	String starttime;
	String startdate;
	Date date = new Date();
	Map<String, String> articleParams;

	@Override
	protected void onStart()
	{
		super.onStart();
		Log.d("activitygg", "onStart         "+ flurry_time);
		FlurryAgent.setContinueSessionMillis(flurry_time * 1000); // Set session timeout to 60 seconds
		FlurryAgent.setLogEnabled(true);
		FlurryAgent.setLogEvents(true);
		FlurryAgent.setLogLevel(Log.VERBOSE);

		articleParams = new HashMap<String, String>();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("SplashActivity", articleParams);
		Log.e("activitygg", "SplashActivity onstart");

	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (isFinish) {
			super.onBackPressed();
		}
	}






	@Override
	protected void onStop()
	{
		super.onStop();	
		//FlurryAgent.endTimedEvent("스플래쉬,SplashActivity,1");
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
			// Do whatever
			articleParams.put("WIFI", "On");
		}else{
			articleParams.put("WIFI", "Off");
		}

		FlurryAgent.endTimedEvent("SplashActivity:Start");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		EasyTracker.getInstance(this).activityStop(this);
		FlurryAgent.onEndSession(this);
		Log.e("activitygg", "SplashActivity onstop");
	}



}
