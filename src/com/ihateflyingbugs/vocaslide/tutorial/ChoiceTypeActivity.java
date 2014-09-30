package com.ihateflyingbugs.vocaslide.tutorial;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.R.anim;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.vocaslide.MyTalkHttpResponseHandler;
import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.data.Config;
import com.ihateflyingbugs.vocaslide.data.Date;
import com.ihateflyingbugs.vocaslide.lock.LockService;
import com.ihateflyingbugs.vocaslide.lock.VocaReceiver;
import com.ihateflyingbugs.vocaslide.login.MainActivitys;
import com.ihateflyingbugs.vocaslide.login.TimeClass;
import com.ihateflyingbugs.vocaslide.struct.KakaoLink;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.kakao.APIErrorResult;
import com.kakao.KakaoTalkProfile;
import com.kakao.KakaoTalkService;
import com.kakao.MeResponseCallback;
import com.kakao.UserManagement;
import com.kakao.UserProfile;

public class ChoiceTypeActivity extends FragmentActivity implements OnClickListener{


	public final static int FRAGMENT_CHOICE = 0;
	public final static int FRAGMENT_START = 1;

	LinearLayout ll_tutorial_start;
	LinearLayout ll_choice_topic;
	TextView tv_choice_title;
	private SharedPreferences mPreference;
	Handler handler;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		
		handler = new Handler();
		
		setContentView(R.layout.activity_tutorial_choicetopic);

		ll_tutorial_start = (LinearLayout)findViewById(R.id.ll_tutorial_start);
		ll_choice_topic= (LinearLayout)findViewById(R.id.ll_choice_topic);

		Button bt_choice_sat = (Button)findViewById(R.id.bt_choice_sat);
		Button bt_choice_toeic = (Button)findViewById(R.id.bt_choice_toeic);
		Button bt_choice_toefl = (Button)findViewById(R.id.bt_choice_toefl);
		Button bt_tutorial_start = (Button)findViewById(R.id.bt_tutorial_start);
		Button bt_tutorial_rechoice = (Button)findViewById(R.id.bt_tutorial_rechoice);

		bt_choice_sat.setOnClickListener(this);
		bt_choice_toeic.setOnClickListener(this);
		bt_choice_toefl.setOnClickListener(this);
		bt_tutorial_start.setOnClickListener(this);
		bt_tutorial_rechoice.setOnClickListener(this);
		bt_choice_sat.setOnTouchListener(new CustomTouchListener());
		bt_choice_toeic.setOnTouchListener(new CustomTouchListener());
		bt_choice_toefl.setOnTouchListener(new CustomTouchListener());
		bt_tutorial_start.setOnTouchListener(new CustomTouchListener());
		bt_tutorial_rechoice.setOnTouchListener(new CustomTouchListener());


		mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
		tv_choice_title  = (TextView)findViewById(R.id.tv_choice_title);
		tv_choice_title.setText("자, 그럼 지금부터 "+mPreference.getString(MainActivitys.GpreName, "이름없음")+"님만을 위한 밀당영단어 머신러닝 사용법을 배워보겠습니다.");



	}

	private boolean isFinish;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isFinish = false;
	}


	private long mLastClickTime = 0;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
			return;
		}
		mLastClickTime = SystemClock.elapsedRealtime();
		SharedPreferences.Editor editor = mPreference.edit();
		switch (v.getId()) {
		case R.id.bt_choice_sat:
			FlurryAgent.logEvent("SelctTopicActivity:SelectSAT");

			ll_choice_topic.setVisibility(View.GONE);
			ll_tutorial_start.setVisibility(View.VISIBLE);
			ll_choice_topic.startAnimation(new AnimationUtils().loadAnimation(this, R.anim.slide_out_left));
			ll_tutorial_start.startAnimation(new AnimationUtils().loadAnimation(this, R.anim.slide_in_left));

			editor.putString(MainActivitys.GpreTopic	, "1").commit();
			editor.putString(MainActivitys.GpreLevel	, "1").commit();

			editor.commit();
			break;
		case R.id.bt_choice_toeic:
			FlurryAgent.logEvent("SelctTopicActivity:SelectTOEIC");

			ll_choice_topic.setVisibility(View.GONE);
			ll_tutorial_start.setVisibility(View.VISIBLE);
			ll_choice_topic.startAnimation(new AnimationUtils().loadAnimation(this, R.anim.slide_out_left));
			ll_tutorial_start.startAnimation(new AnimationUtils().loadAnimation(this, R.anim.slide_in_left));

			editor.putString(MainActivitys.GpreTopic	, "2").commit();
			editor.putString(MainActivitys.GpreLevel	, "3").commit();

			editor.commit();
			break;
		case R.id.bt_choice_toefl:
			FlurryAgent.logEvent("SelctTopicActivity:SelectTOEFL");

			ll_choice_topic.setVisibility(View.GONE);
			ll_tutorial_start.setVisibility(View.VISIBLE);
			ll_choice_topic.startAnimation(new AnimationUtils().loadAnimation(this, R.anim.slide_out_left));
			ll_tutorial_start.startAnimation(new AnimationUtils().loadAnimation(this, R.anim.slide_in_left));

			editor.putString(MainActivitys.GpreTopic	, "3").commit();
			editor.putString(MainActivitys.GpreLevel	, "5").commit();

			editor.commit();
			break;
		case R.id.bt_tutorial_start:
			FlurryAgent.logEvent("SelctTopicActivity:ClickStart");

			Intent intent = new Intent(ChoiceTypeActivity.this, Tutorial_Activity.class);

			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
			break;
		case R.id.bt_tutorial_rechoice:
			FlurryAgent.logEvent("SelctTopicActivity:ClickReselect");
			
			ll_tutorial_start.setVisibility(View.GONE);
			ll_choice_topic.setVisibility(View.VISIBLE);
			ll_tutorial_start.startAnimation(new AnimationUtils().loadAnimation(this, R.anim.slide_out_right));
			ll_choice_topic.startAnimation(new AnimationUtils().loadAnimation(this, R.anim.slide_in_right));
			break;
		}

	}


	public class CustomTouchListener implements View.OnTouchListener {     


		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch(event.getAction()){            
			case MotionEvent.ACTION_DOWN:

				((TextView)v).setTextColor(0xFFFFFFFF); //white     

				break; 
			case MotionEvent.ACTION_CANCEL:             
			case MotionEvent.ACTION_UP:
				if(v.getId()== R.id.bt_tutorial_start){
					((TextView)v).setTextColor(0xFFFFFFFF); //white
				}else{
					((TextView)v).setTextColor(Color.rgb(0, 181, 105)); //black   
				}
				break; 

			} 
			return false;   
		} 
	}

	public void readProfile() {
		 final Map<String, String> properties = new HashMap<String, String>();

		KakaoTalkService.requestProfile(new MyTalkHttpResponseHandler<KakaoTalkProfile>() {

			/* (non-Javadoc)
			 * @see com.ihateflyingbugs.vocaslide.MyTalkHttpResponseHandler#onFailure(com.kakao.APIErrorResult)
			 */
			@Override
			protected void onFailure(APIErrorResult errorResult) {
				// TODO Auto-generated method stub
				super.onFailure(errorResult);
				tv_choice_title.setText("자, 그럼 지금부터 당신만을 위한 밀당영단어 머신러닝 사용법을 배워보겠습니다.");
			}

			@Override
			protected void onHttpSuccess(final KakaoTalkProfile talkProfile) {


				final String nickName = talkProfile.getNickName();
				final String profileImageURL = talkProfile.getThumbnailURL();

				properties.put("nickName", nickName);
				properties.put("profileImageURL", profileImageURL);

				mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
				SharedPreferences.Editor editor = mPreference.edit();
				editor.putString(MainActivitys.GpreName, nickName).commit();
				editor.putString("image"	, profileImageURL).commit();

				tv_choice_title.setText("자, 그럼 지금부터 "+mPreference.getString(MainActivitys.GpreName, "이름없음")+"님만을 위한 밀당영단어 머신러닝 사용법을 배워보겠습니다.");

				Thread thread= new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						FileOutputStream out = null;
						try {
							URL url = new URL(profileImageURL);
							URLConnection conn = url.openConnection();
							conn.connect();// url연결
							BufferedInputStream bis = new BufferedInputStream(
									conn.getInputStream()); // 접속한 url로부터 데이터값을 얻어온다
							Bitmap bm = BitmapFactory.decodeStream(bis);// 얻어온 데이터 Bitmap 에저장

							String path = Config.DB_FILE_DIR;
							File file = new File(path);
							if(!file.exists() && !file.mkdirs())
								file.mkdir();
							file= new File(path, Config.PROFILE_NAME);
							out = new FileOutputStream(file);
							bm.compress(Bitmap.CompressFormat.PNG, 90, out);
							bis.close();// 사용을 다한 BufferedInputStream 종료
						} catch (MalformedURLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}finally {
							try{
								out.close();
							} catch(Throwable ignore) {}
						}
					}
				});
				thread.start();

				//thumbnailURL = talkProfile.getThumbnailURL();
				// display
			}
		});
	}

	String starttime;
	String startdate;
	Date date = new Date();
	Map<String, String> articleParams;
	@Override
	protected void onStart()
	{
		super.onStart();
		articleParams = new HashMap<String, String>();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		FlurryAgent.setUserId(mPreference.getString(MainActivitys.GpreID, "000000"));
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("SelctTopicActivity", articleParams);
	//	MainActivity.writeLog("[타입 선택 시작,ChoiceTypeActivity,1]\r\n");
	}

	@Override
	protected void onStop()
	{
		super.onStop();	
		FlurryAgent.endTimedEvent("SelctTopicActivity:Start");
		
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
		    // Do whatever
			articleParams.put("WIFI", "On");
		}else{
			articleParams.put("WIFI", "Off");
		}
		
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		
		FlurryAgent.onEndSession(this);
		
	//	MainActivity.writeLog("[타입 선택 끝,ChoiceTypeActivity,1,{Start:"+choiceTypeParams.get("Start")+",End:"+choiceTypeParams.get("End")+"}]\r\n");
	}
	
	private long mLastBackPressedTime = 0;
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Calendar now = Calendar.getInstance();
		long nowTime = now.getTimeInMillis();

		Toast.makeText(getApplicationContext(), "뒤로버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
		if(nowTime - mLastBackPressedTime <= 2000){
			isFinish = true;
//			moveTaskToBack(true);
//			handler.postDelayed(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					if(isFinish){
						finish();
//					}
//				}
//			},15000);
		}
		mLastBackPressedTime = nowTime;	
	}
}
