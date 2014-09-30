package com.ihateflyingbugs.vocaslide;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.R.anim;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.vocaslide.data.Config;
import com.ihateflyingbugs.vocaslide.data.Date;
import com.ihateflyingbugs.vocaslide.lock.LockService;
import com.ihateflyingbugs.vocaslide.lock.VocaReceiver;
import com.ihateflyingbugs.vocaslide.login.MainActivitys;
import com.ihateflyingbugs.vocaslide.login.TimeClass;
import com.ihateflyingbugs.vocaslide.service.DBService;
import com.ihateflyingbugs.vocaslide.struct.KakaoLink;
import com.ihateflyingbugs.vocaslide.tutorial.MainActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class SideActivity extends FragmentActivity implements OnClickListener, OnKeyListener, OnCheckedChangeListener{
	
	
	private static int mCurrentFragmentIndex;
	private int mMainWordIndex;
	
	ServiceLogDataFile service_log_file;
	

	public final static String BOARD_URL = "http://lnslab.com/notice.php";


	public final static int FRAGMENT_FAQ = 1;
	public final static int FRAGMENT_CHOICE = 2;
	public final static int FRAGMENT_SETTING = 4;
	public final static int FRAGMENT_MYSCORE = 5;
	public final static int FRAGMENT_MYINFO = 6;
	public final static int FRAGMENT_INVITE = 7;
	public final static int FRAGMENT_BOARD = 8;
	public final static int FRAGMENT_QNA =9;
	public final static int FRAGMENT_QNALIST =10;
	public final static int FRAGMENT_Study =11;
	public final static int FRAGMENT_EXPLAIN =12;

	private static final int RESULT_MODE = 100;

	private static SlidingMenu menu ;

	private ActionBar bar;
	private View actionbar_main;
	
	static private TextView tvTitle;
	static private Button bt_side_back;
	static private Button bt_side_ask;
	

	private String query;

	private boolean isBackPressed = false;

	private SharedPreferences settings;
	
	static SharedPreferences mPreference;

	private static Activity thisActivity = null;
	private static FragmentManager fragmentManager;
	private static Handler handler;
	static Context mContext;
	private static Animation anima;
	

	@Override 
	public void onPause()
	{
		super.onPause();

	}

	@Override
	public void onResume()
	{
		super.onResume();
		//if(tvLevel != null) tvLevel.setText("Level : " + Config.Difficulty);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

	
		
		mContext = getApplicationContext();
		fragmentManager = getSupportFragmentManager();
		anima = AnimationUtils.loadAnimation(SideActivity.this, R.anim.anim_button);
		thisActivity= this;
		handler = new Handler();
		
		mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
		
		
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_main);

		makeActionBar();
		
		mCurrentFragmentIndex = getIntent().getExtras().getInt("Fragment", 8);
		mMainWordIndex = mCurrentFragmentIndex; 
		fragmentReplace(mCurrentFragmentIndex);

		
//		Log.d("kjw", "db service start");
//		Calendar cal = Calendar.getInstance();
//		cal.add(Calendar.SECOND, 10);
//		
//		Intent intent = new Intent(this, DBService.class);
//		PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
//		
//		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//		
//		
//		// 1시간단위
//		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
//				AlarmManager.INTERVAL_HOUR, pintent);
//		
//		
//		Log.e("alma", "mainactivity alarm manager");
		

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	}

	

	public void fragmentReplace(int reqNewFragmentIndex) {

		Fragment fragment = getFragment(reqNewFragmentIndex);
		Log.e("fragments", " "+reqNewFragmentIndex);
		// replace fragment
		final FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.linearFragment, fragment).addToBackStack(null).commit();
	}

	private Fragment getFragment(int idx) {
		Fragment newFragment = null;

		switch (idx) {
		
		case FRAGMENT_MYSCORE:
			setActionBar(false, "설정");
			newFragment = new MyScoreFragment();
			break;

		case FRAGMENT_MYINFO:
			setActionBar(false, "내정보");
			newFragment = new MyInfoFragment();
			break;

		case FRAGMENT_BOARD:
			setActionBar(false, "게시판");
			newFragment = new NoticeFragment();
			break;
		case FRAGMENT_QNA:
			setActionBar(false, "문의사항");
			newFragment = new QnAFragment();
			break;
		case FRAGMENT_QNALIST:
			setActionBar(false, "문의사항");
			newFragment = new QnAListFragment();
			break;
		case FRAGMENT_Study:
			setActionBar(false, "지난 학습현황");
			newFragment = new StudyFragment();
			break;
		case FRAGMENT_FAQ:
			setActionBar(false, "FAQ");
			newFragment = new FAQFragment();
			break;
		case FRAGMENT_CHOICE:
			setActionBar(false, "단어장 선택");
			newFragment = new ChoiceCateFragment();
			break;
		case FRAGMENT_EXPLAIN:
			setActionBar(false, "밀당 영단어 학습 안내");
			newFragment = new ExplainFragment();
			break;
		default:
			break;
		}

		return newFragment;
	}

	
	private void makeActionBar()
	{
		bar = getActionBar();


		actionbar_main = getLayoutInflater().inflate(R.layout.side_action_bar, null);

		bar.setCustomView(actionbar_main, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33000000")));
		bar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#55000000")));

		tvTitle = (TextView)actionbar_main.findViewById(R.id.side_tvTitle);
		bt_side_back = (Button)actionbar_main.findViewById(R.id.bt_side_back);
		bt_side_ask = (Button)actionbar_main.findViewById(R.id.bt_side_ask);
		bt_side_back.setOnClickListener(this);
		bt_side_ask.setOnClickListener(this);
	}

	

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.bt_side_back:
			if (getSupportFragmentManager().getBackStackEntryCount() == 1)
		    {
				setResult(Activity.RESULT_OK);
				this.finish();

				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
				MainActivity.anim_ListView();
		    }
		    else
		    {
		        getSupportFragmentManager().popBackStack();
		        SideActivity.setActionBar(true, "문의사항");
		        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
		    }
			break;
		case R.id.bt_side_ask:
			setActionBar(false, "1:1문의");
			mCurrentFragmentIndex = FRAGMENT_QNALIST;
			fragmentReplace(mCurrentFragmentIndex);

			break;
		}
	}

	static void setActionBar(boolean isMenuShow, String title)
	{
		if(isMenuShow)
		{
			tvTitle.setVisibility(View.INVISIBLE);
			bt_side_ask.setVisibility(View.GONE);
		}
		else
		{
			tvTitle.setText(title);
			tvTitle.setVisibility(View.VISIBLE);
			if(title.equals("FAQ")){
				bt_side_ask.setVisibility(View.VISIBLE);
			}else{
				bt_side_ask.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//		Log.d("kjw", "onActivityResult");
		if (requestCode == RESULT_MODE && resultCode == Activity.RESULT_OK) 
		{
			//			Log.d("kjw", "RESULT OK");

			query = data.getStringExtra("query");

			//			Log.d("kjw", "q = " + query);


			mMainWordIndex = mCurrentFragmentIndex;
			
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {

		return false;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch(buttonView.getId())
		{
		case R.id.chkSetting:
			menu.toggle();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		
		if (getSupportFragmentManager().getBackStackEntryCount() == 1)
	    {
			setResult(Activity.RESULT_OK);
			finish();
			MainActivity.anim_ListView();
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	    }
	    else
	    {
	        getSupportFragmentManager().popBackStack();
	        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	    }
	}
	
	
	
	public void sendUrlLink(View v) throws NameNotFoundException {
		// Recommended: Use application mContext for parameter.
		KakaoLink kakaoLink = KakaoLink.getLink(getApplicationContext());


		//int checkedRadioButtonId = rg.getCheckedRadioButtonId();


		// check, intent is available.
		if (!kakaoLink.isAvailableIntent()) {
			alert("카카오톡을 설치하고 다시 시도해 주세요");			
			return;
		}


		String mssg = "친구야 같이 외워Voca 하자!!";
		kakaoLink.openKakaoLink(this, 
				"http://bit.ly/VOCASlide", 
				mssg , 
				getPackageName(), 
				getPackageManager().getPackageInfo(getPackageName(), 0).versionName, 
				"외워 VOCA", 
				"UTF-8");
	}

	private void alert(String message) {
		new AlertDialog.Builder(this)
		.setIcon(R.drawable.icon48)
		.setTitle(R.string.app_name)
		.setMessage(message)
		.setPositiveButton(android.R.string.ok, null)
		.create().show();
	}
	
	private static String getMySharedPreferences(String _key) {
		if(mPreference == null){
			mPreference =  mContext.getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
		}
		return mPreference.getString(_key, "");
	}

	public static void setMySharedPreferences(String _key, String _value) {
		if(mPreference == null){
			mPreference = mContext.getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
		}  
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putString(_key, _value);
		editor.commit();
	}
	
	public static void set_tvLevel(){
		//tvLevel.setText("Level : " + Config.Difficulty);
	}
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */

	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	String starttime;
	String startdate;
	Date date = new Date();

	Map<String, String> articleParams ;
	public void onStart()
	{

		super.onStart();
		articleParams = new HashMap<String, String>();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		FlurryAgent.setUserId(mPreference.getString(MainActivitys.GpreID, "000000"));
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		// your code
	//	MainActivity.writeLog("[모든단어 암기완료 시작,FinishStudyFragment,1]\r\n");

	}

	public void onStop()
	{
		super.onStop();
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		Log.e("splash", startdate+"        "+date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
		// your code
	//	MainActivity.writeLog("[모든단어 암기완료 끝,FinishStudyFragment,1,{Start:"+articleParams.get("Start")+",End:"+articleParams.get("End")+"}]\r\n");
	}
	

}
