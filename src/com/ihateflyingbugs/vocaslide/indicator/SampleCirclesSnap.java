package com.ihateflyingbugs.vocaslide.indicator;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Browser;
import android.provider.Browser.BookmarkColumns;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.AsyncTask.Async_check_id;
import com.ihateflyingbugs.vocaslide.AsyncTask.VocaCallback;
import com.ihateflyingbugs.vocaslide.data.Config;
import com.ihateflyingbugs.vocaslide.data.Date;
import com.ihateflyingbugs.vocaslide.login.AlertDialogManager;
import com.ihateflyingbugs.vocaslide.login.ConnectionDetector;
import com.ihateflyingbugs.vocaslide.login.MainActivitys;
import com.ihateflyingbugs.vocaslide.login.WriteUserInfoActivity;
import com.ihateflyingbugs.vocaslide.tutorial.MainActivity;
import com.ihateflyingbugs.vocaslide.tutorial.SplashActivity;
import com.kakao.APIErrorResult;
import com.kakao.MeResponseCallback;
import com.kakao.Session;
import com.kakao.SessionCallback;
import com.kakao.UserManagement;
import com.kakao.UserProfile;
import com.kakao.exception.KakaoException;
import com.kakao.widget.LoginButton;
import com.viewpagerindicator.CirclePageIndicator;

public class SampleCirclesSnap extends BaseSampleActivity {

	private static SharedPreferences mPreference;
	
	static long curPageTime=-1;
	static long prePageTime=-1;
	int preArg0=-1;
	static Map<String, String> pageParams ;
	 
	static String token;

	ProgressDialog mProgress;
	boolean loadingFinished = true;
	AlertDialog.Builder alert;
	static Context mContext;
	static Activity activity;
	
	 private final SessionCallback mySessionCallback = new MySessionStatusCallback();

	 boolean isStart = false;
	 
	 LoginButton bt_login;
	 
	 ProgressBar pb_ready_login;
	 
	 Date date = new Date();
	 ActivityManager am;

		private boolean isFinish;
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isFinish = false;
		isActivity = false;
		
		if(!Config.isNetworkAvailable(getApplicationContext())){
			FlurryAgent.logEvent("SplashActivity:NotAccessInternet");
			isFinish = true;
			AlertDialog alertDialog = new AlertDialog.Builder(SampleCirclesSnap.this).create();
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
		}
		
		mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				
				curPageTime=System.currentTimeMillis();
				try {
					switch(preArg0){
					case -1:
						pageParams.put("FirstPage","0");
						pageParams.put("SecondPage","0");
						pageParams.put("ThirdPage","0");
						pageParams.put("FourthPage","0");
						pageParams.put("FirstPage", ""+ ((Long.valueOf(pageParams.get("FirstPage")) + curPageTime - prePageTime))/1000);
						break;
					case 0:
						pageParams.put("FirstPage", ""+ ((Long.valueOf(pageParams.get("FirstPage")) + curPageTime - prePageTime))/1000);
						break;
					case 1:
						pageParams.put("SecondPage", ""+ ((Long.valueOf(pageParams.get("SecondPage")) + curPageTime - prePageTime))/1000);
						break;
					case 2:
						pageParams.put("ThirdPage", ""+ ((Long.valueOf(pageParams.get("ThirdPage")) + curPageTime - prePageTime))/1000);
						break;
					case 3:
						pageParams.put("FourthPage", ""+ ((Long.valueOf(pageParams.get("FourthPage")) + curPageTime - prePageTime))/1000);
						break;
					default:
						break;
					}
				} catch (NumberFormatException e) {
					// TODO: handle exception
				}
				
				//pageParams.put("FirstPage", String.valueOf(System.currentTimeMillis()))
				if(arg0<3){
					bt_next.setText("다음");
					bt_login.setVisibility(View.INVISIBLE);
				}else{
					if(!anim){
					bt_login.startAnimation(new AnimationUtils().loadAnimation(getApplicationContext(), android.R.anim.fade_in));
					anim = true;
					}
					bt_login.setVisibility(View.VISIBLE);
					bt_next.setText("단어장 선택하기");
				}
				preArg0=arg0;
				prePageTime=curPageTime;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		
		
	        // 세션을 초기화 한다
	        if(Session.initializeSession(this, mySessionCallback)){
	            // 1. 세션을 갱신 중이면, 프로그레스바를 보이거나 버튼을 숨기는 등의 액션을 취한다
	        	bt_login.setVisibility(View.INVISIBLE);
	        } else if (Session.getCurrentSession().isOpened()){
	            // 2. 세션이 오픈된된 상태이면, 다음 activity로 이동한다.
	        	pb_ready_login.setVisibility(View.VISIBLE);
	        }
	}

	boolean anim = false;
	Button bt_next;
	Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_circles);
		
		
		prePageTime=System.currentTimeMillis();
		
		//MainActivity.writeLog("머신러닝 소개 시작,SampleCirclesSnap,1]\r\n");
		
		Log.e("activitygg", "Sample onCreate");
		
		mAdapter = new TestFragmentAdapter(getSupportFragmentManager());
		activity = this;
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		mContext = getApplicationContext();
		mPager.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		handler = new Handler();
		mPreference =  getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);

		
		
		
		mPreference.edit().putString(MainActivitys.GpreFirtst, "1").commit();
		
		final CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
		mIndicator = indicator;
		indicator.setViewPager(mPager);
		indicator.setSnap(true);
		
		pb_ready_login = (ProgressBar)findViewById(R.id.pb_ready_login);
		
		bt_next = (Button)findViewById(R.id.bt_tutorial_login);
		bt_login = (LoginButton)findViewById(R.id.bt_tutorial_next);
		bt_login.setVisibility(View.INVISIBLE);
		bt_login.setLoginSessionCallback(mySessionCallback);
		
//		bt_login.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Log.e("onClickLogin!","onClickLogin!");
//				// TODO Auto-generated method stub
//				if(mPreference.getString(MainActivitys.GpreTutorial,"0").equals("1")){
//		            final Intent intent = new Intent(SampleCirclesSnap.this, MainActivity.class);
//		            startActivity(intent);
//		            finish();
//		    	}else{
//		            final Intent intent = new Intent(SampleCirclesSnap.this, ChoiceTypeActivity.class);
//		            startActivity(intent);
//		            finish();
//		    	}
//			}
//		});
		
		//ImageButton ib_choice = (ImageButton)findViewById(R.id.ib_choice);
		bt_next.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mPager.getCurrentItem()< 2){
				mPager.setCurrentItem(mPager.getCurrentItem()+1,true);
				bt_login.setVisibility(View.VISIBLE);

				}else{
					
					bt_login.setVisibility(View.INVISIBLE);

				}
			}
		});
		
		
	}
	


	@Override
	protected void onActivityResult(int requestCode, int resultcode, Intent intent) {
		// TODO Auto-generated method stub
		if(resultcode!=RESULT_CANCELED){
			setResult(RESULT_OK);
			finish();
		}else if(resultcode==RESULT_CANCELED){
//			switch(requestCode){
//			case SideActivity.Sign_requestCode:
//				finish();
//			}
		}

	}

	/**
	 * ?ㅽ뵆?섏떆 ?쒖떆?섎뒗 寃껉낵 珥덇린?붾? ?숈떆??吏꾪뻾?쒗궎湲??꾪븯???곕젅??泥섎━
	 *
	 */
	private void initialize()
	{
		InitializationRunnable init = new InitializationRunnable();
		new Thread(init).start();
	}


	/**
	 * 珥덇린???묒뾽 泥섎━
	 *
	 */

	class InitializationRunnable implements Runnable
	{
		public void run()
		{
			// ?ш린?쒕???珥덇린???묒뾽 泥섎━
			// do_something


			//踰꾩쟾 ?뺣낫瑜??뺤씤?섎뒗 怨?




		}

	}
	
	
	
	private class MySessionStatusCallback implements SessionCallback {
        @Override
        public void onSessionOpened() {
            // 프로그레스바를 보이고 있었다면 중지하고 세션 오픈후 보일 페이지로 이동
        	pb_ready_login.setVisibility(View.VISIBLE);
            SampleCirclesSnap.this.onSessionOpened();
        }

        @Override
        public void onSessionClosed(final KakaoException exception) {
            // 프로그레스바를 보이고 있었다면 중지하고 세션 오픈을 못했으니 다시 로그인 버튼 노출.
            bt_login.setVisibility(View.VISIBLE);
        }
    }
	
	Map<String, String> articleParams = new HashMap<String, String>();

	
    protected void onSessionOpened(){
    	if(mPreference == null){
			mPreference =  getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
		}
    	
    	articleParams.put("time", String.valueOf(System.currentTimeMillis()));
    	
    	//FlurryAgent.logEvent("onSessionOpened,SampleCirclesSnap,1",articleParams);
    //	MainActivity.writeLog("[onSessionOpened,SampleCirclesSnap,1,{time:"+articleParams.get("time")+"}]\r\n");
    	
    	UserManagement.requestMe(new MeResponseCallback() {
			
    		
			@Override
			protected void onSuccess(UserProfile userProfile) {
				// TODO Auto-generated method stub
		            if (userProfile != null)
		                userProfile.saveUserToCache();
		            PackageManager packageManager = mContext.getPackageManager();
					PackageInfo infor = null;
					try {
						infor = packageManager.getPackageInfo(mContext.getPackageName(),      PackageManager.GET_META_DATA);
					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String cur_version = infor.versionName;
					int code = infor.versionCode;
					Config.VERSION= cur_version;
				Config.USER_ID  =String.valueOf(userProfile.getId());
				mPreference.edit().putString(MainActivitys.GpreID, Config.USER_ID ).commit();
				FlurryAgent.setUserId(Config.USER_ID);
				setMySharedPreferences(MainActivitys.GpreID, Config.USER_ID);
				
				if(mPreference.getString(MainActivitys.GpreName, "0").equals("0")){
					mPreference.edit().putString(MainActivitys.GpreName, userProfile.getNickname()).commit();
					Config.NAME =  userProfile.getNickname();
				}
				
				if(!isStart){
					Date dated = new Date();
					check_id(Config.USER_ID, Config.VERSION, getApplicationContext());
				}else{
					isStart =true;
				}
				
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
    
    
    private void setMySharedPreferences(String _key, String _value) {
		if(mPreference == null){
			mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
		}  
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putString(_key, _value);
		editor.commit();
	}
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(mPager.getCurrentItem()> 0){
			mPager.setCurrentItem(mPager.getCurrentItem()-1,true);
		}else{
//			moveTaskToBack(true);
			isFinish = true;
//			handler.postDelayed(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					if(isFinish){
			ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);    

			Intent startMain = new Intent(Intent.ACTION_MAIN); 
			startMain.addCategory(Intent.CATEGORY_HOME); 
			startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			this.startActivity(startMain);
			finish();
//						Log.e("activitygg", "first end");
//					}
//				}
//			},15000);
//			finish();
//			Log.e("activitygg", "second end");
		}
	}
	
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();
		Log.e("activitygg", "=====================================");
		Log.e("alarm", "app is destroy");
	}
	
	public void onPageFinished() {
		
		if(loadingFinished){
			if(null !=mProgress) {
				if(mProgress.isShowing()) {
					mProgress.dismiss();
				}
			}

		}
	}
	
	String startdate;
	String starttime;
	
	static boolean isStopCheck =true;
	
	
	public void onStart()
	{

		super.onStart();

		Log.e("activitygg", "Sample onStart");
		pageParams = new HashMap<String, String>();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		if(isStopCheck){
			FlurryAgent.logEvent("IntroMachineLearning", pageParams);
		}
		
		// your code
	}
	
	
	public void onStop()
	{
		
		super.onStop();
		
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
		    // Do whatever
			pageParams.put("WIFI", "On");
		}else{
			pageParams.put("WIFI", "Off");
		}
		
		Log.e("activitygg", "Sample onStop");
		FlurryAgent.endTimedEvent("IntroMachineLearning:Start");
		pageParams.put("Start", startdate);
		curPageTime=System.currentTimeMillis();
		
		if(isStopCheck){
			try {
				pageParams.put("FourthPage", ""+ ((Long.valueOf(pageParams.get("FourthPage")) + curPageTime - prePageTime))/1000);
				pageParams.put("End", date.get_currentTime());
				pageParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
				//pageParams.clear();
			} catch (NumberFormatException e) {
				// TODO: handle exception
				FlurryAgent.logEvent("IntroMachineLearning:NumberFormatException");
			//	MainActivity.writeLog("[머신러닝 소개 끝,SampleCirclesSnap,,{Start:"+pageParams.get("Start")+",End:"+pageParams.get("End")+"}]\r\n");
			}
		}
		
		ActivityManager am = (ActivityManager) this .getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName componentInfo = taskInfo.get(0).topActivity;

		if(componentInfo.getClassName().equals("com.kakao.sdk.CapriLoggedInActivity")){
			isStopCheck = false;
		}else{
			isStopCheck = true;
		}
		FlurryAgent.onEndSession(this);
		pageParams.put("FirstPage","0");
		pageParams.put("SecondPage","0");
		pageParams.put("ThirdPage","0");
		pageParams.put("FourthPage","0");
		
		
		// your code
	}
	
	
	static boolean isActivity = false;
	
	
		
	public void check_id(String email, String version ,Context context){
		new Async_check_id(Config.USER_ID, Config.VERSION, getApplicationContext(),new VocaCallback() {
			
			@Override
			public void Resonponse(JSONObject response) {
				// TODO Auto-generated method stub

				int success = 0;
				try {
					success = response.getInt(Config.TAG_SUCCESS);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!isActivity) {
					if(success == Async_check_id.ID_DUPLICATION){
						final Intent intent = new Intent(mContext, MainActivity.class);
						activity.startActivity(intent);
						activity.finish();
						isActivity = true;
					}else if(success == Async_check_id.ID_SUCCESS){
						final Intent intent = new Intent(mContext, WriteUserInfoActivity.class);
						activity.startActivity(intent);
						activity.finish();
						isActivity = true;
					}else{
						final Intent intent = new Intent(mContext, WriteUserInfoActivity.class);
						activity.startActivity(intent);
						activity.finish();
						isActivity = true;
					}
				}
			}
			
			@Override
			public void Exception() {
				// TODO Auto-generated method stub
				
			}
		}).execute();
	}

}