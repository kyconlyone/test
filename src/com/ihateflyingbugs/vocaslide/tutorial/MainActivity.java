package com.ihateflyingbugs.vocaslide.tutorial;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import org.andlib.ui.PercentView;
import org.andlib.ui.StudyPercentView;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gcm.GCMRegistrar;
import com.ihateflyingbugs.vocaslide.Known_Activity;
import com.ihateflyingbugs.vocaslide.MyInfoFragment;
import com.ihateflyingbugs.vocaslide.MyScoreFragment;
import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.SideActivity;
import com.ihateflyingbugs.vocaslide.WordListFragment;
import com.ihateflyingbugs.vocaslide.blur.Blur;
import com.ihateflyingbugs.vocaslide.blur.ImageUtils;
import com.ihateflyingbugs.vocaslide.data.Config;
import com.ihateflyingbugs.vocaslide.data.DBPool;
import com.ihateflyingbugs.vocaslide.data.Date;
import com.ihateflyingbugs.vocaslide.feedback.FAQActivity;
import com.ihateflyingbugs.vocaslide.feedback.SettingActivity;
import com.ihateflyingbugs.vocaslide.login.MainActivitys;
import com.ihateflyingbugs.vocaslide.service.DBService;
import com.ihateflyingbugs.vocaslide.struct.KakaoLink;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends FragmentActivity implements OnClickListener,
OnKeyListener {

	TextView memorize_word;
	TextView review_word;
	TextView forgetting_curve_percentage_count;
	ProgressBar forgetting_curve_percentage_bar;

	int timer_sec = 20;
	int percentage = 0;
	PercentView percentView1;
	CountDownTimer cTimer;
	SharedPreferences timerPreference;
	SharedPreferences.Editor timerEditor;

	private static int mCurrentFragmentIndex;

	private int mMainWordIndex;

	public final static String BOARD_URL = "http://lnslab.com/notice.php";

	public final static int FRAGMENT_WORD_KNOWN_LIST = 0;
	public final static int FRAGMENT_WORD_SCORE_LIST = 1;
	public static final int FRAGMENT_EXAM_KNOWN_LIST = 2;
	public static final int FRAGMENT_EXAM_SCORE_LIST = 3;

	public final static int FRAGMENT_SETTING = 4;
	public final static int FRAGMENT_MYSCORE = 5;
	public final static int FRAGMENT_MYINFO = 6;
	public final static int FRAGMENT_INVITE = 7;
	public final static int FRAGMENT_BOARD = 8;
	public final static int FRAGMENT_QNA = 9;
	public final static int FRAGMENT_CULC = 10;

	private static final int RESULT_MODE = 100;

	private static final int TOP_HEIGHT = 700;

	private SlidingMenu menu;

	private ActionBar bar;
	private static LinearLayout ll_actionbar;
	private static LinearLayout ll_action_review;
	static private TextView tv_action_review;
	private View actionbar_main;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {

		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private static CheckBox chkSetting;
	private CheckBox chkMode;
	static private TextView tvTitle, tvLevel;

	private boolean isExam = false;
	private String query;

	private boolean isBackPressed = false;

	private SharedPreferences settings;

	static SharedPreferences mPreference;
	SharedPreferences.Editor editor;

	private static Activity thisActivity = null;
	private Handler handler;
	static Context context;

	private static DBPool db;
	List<Feed> list;
	static ListView lv_feedback_main;
	MyCustomAdapter adapter;

	String thumbnailURL;

	static TextView tv_show_reviewexplain;

	public final int ScrollLearningStatus = 0;

	String feedback_starttime;
	String feedback_startdate;

	Map<String, String> levelParams = new HashMap<String, String>();
	Map<String, String> statusParams = new HashMap<String, String>();
	Map<String, String> lrnStausOrTmLineParams = new HashMap<String, String>();

	Date date = new Date();
	
	/*
	 * 시계부분
	 */
	RelativeLayout timerLayout;
	TextView tv_using_time_min;
	TextView tv_using_time_sec;
	float use_sec, use_min;
	String use_sec_st, use_min_st;
	int usingTime;
	

	@Override
	public void onPause() {
		super.onPause();
		SharedPreferences.Editor editor = settings.edit();

	}

	Timer Finish_timer = new Timer();
	boolean isFinish = true;

	@Override
	public void onResume() {

		switch (Config.WORD_TOPIC) {
		case 1:
			Config.MAX_DIFFICULTY = 2;
			Config.MIN_DIFFICULTY = 1;
			choiced_topic = "수능";
			break;
		case 2:
			Config.MAX_DIFFICULTY = 4;
			Config.MIN_DIFFICULTY = 1;
			choiced_topic = "토익";
			break;
		case 3:
			Config.MAX_DIFFICULTY = 6;
			Config.MIN_DIFFICULTY = 1;
			choiced_topic = "토플";
			break;
		default:
			break;
		}

		menu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {

			@Override
			public void onOpened() {
				// TODO Auto-generated method stub

				if (mPreference.getString("firststart", "0").equals("0")) {
					view_topnavi.clearAnimation();
					view_topnavi.setVisibility(View.GONE);
					tv_tuto_graph.setVisibility(View.GONE);
				}
				feedback_starttime = String.valueOf(System.currentTimeMillis());
				feedback_startdate = date.get_currentTime();
				FlurryAgent.logEvent("BaseActivity:OpenLearningStatus");
				// set_graph_color(check_graph, check_text);
			}
		});

		menu.setOnCloseListener(new SlidingMenu.OnCloseListener() {

			@Override
			public void onClose() {

				cTimer = timer(percentView1);
				cTimer.start();
				Log.d("Timer", "CloseListener");
				// TODO Auto-generated method stub
				try {
					if (mPreference.getString("firststart", "0").equals("0")) {

						setMySharedPreferences("firststart", "1");
						new AlertDialog.Builder(MainActivity.this)
						.setMessage(
								settings.getString(
										MainActivitys.GpreName, "이름없음")
										+ "님만을 위한 단어 암기 프로그램이 시작되었습니다.\n"
										+ "프로그램과 함께 "
										+ choiced_topic
										+ "에 필요한 모든 단어를 마스터해보세요!")
										.setPositiveButton("확인", null).show();
					}
					Map<String, String> feedbackParam = new HashMap<String, String>();
					feedbackParam.put("Start", feedback_startdate);
					feedbackParam.put("End", date.get_currentTime());
					feedbackParam.put(
							"Duration",
							""
									+ ((Long.valueOf(System.currentTimeMillis()) - Long
											.valueOf(feedback_starttime)))
											/ 1000);

					FlurryAgent.logEvent("BaseActivity:CloseLearningStatus",
							feedbackParam);
				} catch (NumberFormatException e) {
					// TODO: handle exception
					Map<String, String> feedbackExeptParam = new HashMap<String, String>();
					feedbackExeptParam.put("Exception", "NumberFomatException");
					FlurryAgent.logEvent("BaseActivity:CloseLearningStatus",
							feedbackExeptParam);
				}

			}
		});
		isFinish = false;

		super.onResume();
		if (tvLevel != null)
			tvLevel.setText("Level : " + Config.Difficulty);

	}

	ImageView view_topnavi;
	Animation anim;;

	TextView tv_tuto_graph;

	String choiced_topic = "수능";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// /////////////////////////////////////////

		// writeLog("[MainActivity Create,MainActivity,1]\r\n");


		//		//get date
		//		date = new Date();
		//		int hour = Integer.parseInt(date.get_hour());
		//		int minute = Integer.parseInt(date.get_minute());
		//		if(minute >= 30){
		//			hour++;
		//			if(hour==25){
		//				hour=0;
		//			}
		//		}
		//		minute = 0;
		//		
		//		Log.e("gethour", ""+ hour +"   "+minute);
		//		
		//		if(minute >= 30){
		//			hour++;
		//			if(hour==25){
		//				hour=0;
		//			}
		//		}
		//		hour--;
		//		minute = 0;
		//		
		//		
		//		Log.e("gethour", ""+ hour +"   "+minute);



		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		thisActivity = this;

		bar = thisActivity.getActionBar();
		actionbar_main = thisActivity.getLayoutInflater().inflate(
				R.layout.main_action_bar, null);

		
		/*
		 * 시계부분
		 * 
		 */
		
		tv_using_time_min = (TextView)findViewById(R.id.tv_goal_time_min);
		tv_using_time_sec = (TextView)findViewById(R.id.tv_goal_time_sec);
		
		
		bar.setCustomView(actionbar_main, new ActionBar.LayoutParams(
				ActionBar.LayoutParams.MATCH_PARENT,
				ActionBar.LayoutParams.MATCH_PARENT));
		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		ll_actionbar = (LinearLayout) actionbar_main
				.findViewById(R.id.ll_actionbar);
		ll_action_review = (LinearLayout) actionbar_main
				.findViewById(R.id.ll_action_review);

		view_topnavi = (ImageView) actionbar_main
				.findViewById(R.id.view_topnavi);
		view_topnavi.setOnClickListener(this);

		ll_action_review.setOnClickListener(this);
		chkSetting = (CheckBox) actionbar_main.findViewById(R.id.chkSetting);
		tv_action_review = (TextView) actionbar_main
				.findViewById(R.id.tv_action_review);
		makeActionBar(isExam);
		context = getApplicationContext();
		handler = new Handler();
		List<String> list = new ArrayList<String>();
		// list.add("aa@aa.com");
		// list.add("aaaaaa");
		// list.add(GCMRegistrar.getRegistrationId(getApplicationContext()));
		// list.add(Get_my_uuid.get_Device_id(mContext));
		//
		// new Async_login(list, MainActivity.this).execute();
		Log.e("hahaha",
				"" + GCMRegistrar.getRegistrationId(getApplicationContext()));

		setContentView(R.layout.text_activity_main);
		tv_show_reviewexplain = (TextView) findViewById(R.id.tv_show_reviewexplain);

		db = DBPool.getInstance(this);

		settings = getSharedPreferences(Config.PREFS_NAME,
				Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
		mPreference = getSharedPreferences(Config.PREFS_NAME,
				MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);

		tv_tuto_graph = (TextView) findViewById(R.id.tv_tuto_graph);
		tv_tuto_graph.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});

		try {
			Config.Difficulty = Integer.parseInt(settings.getString(
					MainActivitys.GpreLevel, "1"));
			settings.edit()
			.putString(MainActivitys.GpreLevel, "" + Config.Difficulty)
			.commit();
		} catch (NumberFormatException e) {
			Config.Difficulty = 1;
			setMySharedPreferences(MainActivitys.GpreLevel, "1");
		}

		try {
			Config.CHANGE_LEVEL_COUNT = Integer.parseInt(settings.getString(
					MainActivitys.GpreLevelCounting, "20"));
			settings.edit()
			.putString(MainActivitys.GpreLevelCounting,
					"" + Config.CHANGE_LEVEL_COUNT).commit();
		} catch (NumberFormatException e) {
			Config.CHANGE_LEVEL_COUNT = 20;
			// setMySharedPreferences(MainActivitys.GpreLevelCounting, 20);
		}
		try {
			Config.WORD_TOPIC = Integer.parseInt(settings.getString(
					MainActivitys.GpreTopic, "1"));
			settings.edit()
			.putString(MainActivitys.GpreTopic, "" + Config.WORD_TOPIC)
			.commit();
		} catch (NumberFormatException e) {
			// TODO: handle exception
			Config.WORD_TOPIC = 1;
			setMySharedPreferences(MainActivitys.GpreTopic, "1");
		}

		Log.e("leveltest", "현재 토픽     " + Config.WORD_TOPIC);

		Log.e("level", "" + Config.CHANGE_LEVEL_COUNT);

		anim = new AnimationUtils().loadAnimation(getApplicationContext(),
				R.anim.zoom_out_tuto_center);
		anim.setRepeatCount(500);
		anim.setRepeatMode(Animation.REVERSE);

		if (!getMySharedPreferences("firststart").equals("1")) {
			view_topnavi.setAnimation(anim);
			view_topnavi.setVisibility(View.VISIBLE);
			tv_tuto_graph.setVisibility(View.VISIBLE);
			// tv_tuto_graph.bringToFront();

		}

		makeSlidingMenu();

		mCurrentFragmentIndex = FRAGMENT_WORD_SCORE_LIST;

		mMainWordIndex = mCurrentFragmentIndex;
		fragmentReplace(mCurrentFragmentIndex);

		// SharedPreferences.Editor editor = settings.edit();

		// startService(new Intent(this, LockService.class));

		Log.d("kjw", "db service start");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, 10);

		Log.e("truecount", "" + Config.USER_ID + "      "
				+ getMySharedPreferences(MainActivitys.GpreEmail));

		chkSetting.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				menu.toggle();

				// set_graph_color(check_graph, check_text);
			}
		});

		editor = mPreference.edit();

		/*
		 * 
		 * 1시간마다 db갱신하는 부분이지만 나중에 따로 체크를 해봐야한다 현재는 다음 단어장 불러오기를 했을때 db갱신을 한다.
		 */
		Config.NAME = getMySharedPreferences(MainActivitys.GpreName);
		Intent intent = new Intent(this, DBService.class);
		PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				AlarmManager.INTERVAL_HOUR, pintent);
		Log.e("alma", "mainactivity alarm manager");

		// /////////////////////////////////////////////////////////////////////////////////////

		percentView1 = (PercentView) findViewById(R.id.percentView1);

		cTimer = timer(percentView1);
		cTimer.start();
		Log.d("Timer", "onCreate()");
	}

	public static void anim_ListView() {
		lv_feedback_main.setVisibility(View.VISIBLE);
		lv_feedback_main.startAnimation(new AnimationUtils().loadAnimation(
				context, R.anim.slide_in_right));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// outState.putString("WORKAROUND_FOR_BUG_19917_KEY",
		// "WORKAROUND_FOR_BUG_19917_VALUE");
		// super.onSaveInstanceState(outState);
		// invokeFragmentManagerNoteStateNotSaved();
	}

	public void fragmentReplace(int reqNewFragmentIndex) {

		Fragment fragment = getFragment(reqNewFragmentIndex);

		Log.e("fragments", " " + reqNewFragmentIndex);
		// replace fragment
		final FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.linearFragment, fragment).addToBackStack(null)
		.commit();
	}

	private Fragment getFragment(int idx) {
		Fragment newFragment = null;

		switch (idx) {
		case FRAGMENT_WORD_SCORE_LIST:

			newFragment = new WordListFragment(
					WordListFragment.MODE_NORMAL_SCORE_LIST);
			break;
		case FRAGMENT_WORD_KNOWN_LIST:

			newFragment = new WordListFragment(
					WordListFragment.MODE_NORMAL_KNOWN_LIST);
			break;

		case FRAGMENT_EXAM_KNOWN_LIST:
			newFragment = new WordListFragment(
					WordListFragment.MODE_EXAM_KNOWN_LIST, query);
			break;
		case FRAGMENT_EXAM_SCORE_LIST:
			newFragment = new WordListFragment(
					WordListFragment.MODE_EXAM_SCORE_LIST, query);
			break;

		case FRAGMENT_MYSCORE:
			newFragment = new MyScoreFragment();
			break;

		case FRAGMENT_MYINFO:
			newFragment = new MyInfoFragment();
			break;

		default:
			break;
		}

		return newFragment;
	}

	private void makeActionBar(boolean isExam) {

		// if(isExam)
		// {
		// actionbar_main =
		// thisActivity.getLayoutInflater().inflate(R.layout.main_action_bar_exam,
		// null);
		// }
		// else
		// {

		// }

		Log.w("kjw", bar + "   " + actionbar_main);
		chkMode = (CheckBox) actionbar_main.findViewById(R.id.chkMode);
		tvTitle = (TextView) actionbar_main.findViewById(R.id.tvTitle);

		// btnKnown.setOnClickListener(this);
		// btnScore.setOnClickListener(this);
		// chkMode.setOnClickListener(this);
	}

	public static void setActionBar(boolean isExam) {
		if (isExam) {
			int mforget = db.getMforget();
			if (mforget > 0) {
				ll_actionbar
				.setBackgroundResource(R.drawable.main_actionbar_re);
				chkSetting
				.setBackgroundResource(R.drawable.main_actionbar_reicon);
				ll_action_review.setVisibility(View.VISIBLE);
				tv_action_review.setText("" + mforget);
				tv_show_reviewexplain
				.setText("현재 수집된"
						+ getMySharedPreferences(MainActivitys.GpreName)
						+ "님의 망강 주기 정보를 바탕으로 선정한 곧 까먹을지도 모르는 단어들의 갯수입니다.\n단어 어장 관리를 통해 모두 지켜드리겠습니다.");
			} else {
				ll_actionbar.setBackgroundResource(R.drawable.main_actionbar);
				chkSetting
				.setBackgroundResource(R.drawable.main_actionbar_icon);
				ll_action_review.setVisibility(View.GONE);
			}
		} else {
			ll_actionbar.setBackgroundResource(R.drawable.main_actionbar);
			chkSetting.setBackgroundResource(R.drawable.main_actionbar_icon);
			ll_action_review.setVisibility(View.GONE);
		}

	}

	View header;

	TextView tv_feedback_username;
	TextView tv_feedback_current_newStudy;
	TextView tv_feedback_current_total;
	TextView tv_feedback_current_rewind;

	TextView tv_feedback_lv;
	LinearLayout ll_testfeed;
	LinearLayout ll_feed_lv;
	LinearLayout ll_level_graph;
	LinearLayout ll_slide_feedback;
	LinearLayout ll_slide_other;

	private View headerView;

	private ImageView mBlurredImage;
	private ImageView mNormalImage;
	private ImageView iv_picture;
	private ImageView iv_edit_info;

	private StudyPercentView pv_totalgraph;

	private float alpha;
	private PercentView percentView[] = new PercentView[6];
	private ImageView iv_level[] = new ImageView[6];
	private TextView tv_level[] = new TextView[6];
	private TextView tv_explain_feed[] = new TextView[10];
	private View view_explain_left;
	private View view_explain_right;

	static boolean check_graph = true;
	static boolean check_text = true;

	LinearLayout ll_graph_second;
	private TextView tv_sec_explain_feed[] = new TextView[3];

	TextView tv_graph_main_cate;
	TextView tv_graph_main_count;

	TextView tv_lvcount_current;
	TextView tv_lvcount_total;

	View view_current_level_do;
	View view_current_level_rest;
	TextView tv_total_study_feed_title;

	TextView tv_info_total_study;
	TextView tv_feed_today;

	private Integer level_counting[][] = new Integer[6][2];

	BufferedInputStream bis;
	URL url;

	public void makeSlidingMenu() {

		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		menu.setMenu(R.layout.activity_main_feedback);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
		.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		ImageView profile = (ImageView) menu.findViewById(R.id.iv_picture);

		TextView setting = (TextView) menu.findViewById(R.id.tv_setting);
		
		TextView tv_faq = (TextView) menu.findViewById(R.id.tv_faq);
		tv_faq.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this,
						FAQActivity.class);
				Log.d("settingtv", "onclick");
				startActivity(i);
			}
		});

		setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this,
						SettingActivity.class);
				Log.d("settingtv", "onclick");
				startActivity(i);
			}
		});


		profile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this,
						com.ihateflyingbugs.vocaslide.MyinfoActivity.class);
				startActivity(i);
			}
		});

		Button will = (Button) menu.findViewById(R.id.willBtn);
		Button memory = (Button) menu.findViewById(R.id.memoryBtn);
		Button hit = (Button) menu.findViewById(R.id.hitBtn);

		memorize_word = (TextView) menu.findViewById(R.id.memorize_word);
		review_word = (TextView) menu.findViewById(R.id.review_word);

		forgetting_curve_percentage_count = (TextView) menu
				.findViewById(R.id.forgetting_curve_percentageTv);
		forgetting_curve_percentage_bar = (ProgressBar) menu
				.findViewById(R.id.forgetting_curve_percentagePb);

		will.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(
						MainActivity.this,
						com.ihateflyingbugs.vocaslide.feedback.WillActivity.class);
				startActivity(i);
			}
		});

		memory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(
						getApplicationContext(),
						com.ihateflyingbugs.vocaslide.feedback.MemoryActivity.class);
				startActivity(i);
			}
		});

		hit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(
						getApplicationContext(),
						com.ihateflyingbugs.vocaslide.feedback.HitActivity.class);
				startActivity(i);
			}
		});

		menu.setOnOpenListener(new SlidingMenu.OnOpenListener() {

			@Override
			public void onOpen() {
				// TODO Auto-generated method stub
				setProfile();
			}

		});

		/*
		 * 
		 * // RadioGroup radioGroup =
		 * (RadioGroup)menu.findViewById(R.id.radioGroup); lv_feedback_main =
		 * (ListView)menu.findViewById(R.id.lv_feedback_main1);
		 * menu.setMarginLay(lv_feedback_main); header =
		 * getLayoutInflater().inflate(R.layout.tst_header_feedback, null,
		 * false); View footer =
		 * getLayoutInflater().inflate(R.layout.footer_feedback_more, null,
		 * false); View tem =
		 * getLayoutInflater().inflate(R.layout.word_list_row, null, false);
		 * 
		 * ImageView iv_feedback_invite =
		 * (ImageView)header.findViewById(R.id.iv_feedback_invite); ImageView
		 * iv_feedback_choice =
		 * (ImageView)header.findViewById(R.id.iv_feedback_choice); ImageView
		 * iv_feedback_faq =
		 * (ImageView)header.findViewById(R.id.iv_feedback_faq); ImageButton
		 * bt_graph_show_explain =
		 * (ImageButton)header.findViewById(R.id.bt_graph_show_explain);
		 * bt_graph_show_explain.setOnClickListener(this);
		 * 
		 * iv_edit_info= (ImageView)header.findViewById(R.id.iv_edit_info);
		 * iv_picture = (ImageView)header.findViewById(R.id.iv_picture); Button
		 * bt = (Button)footer.findViewById(R.id.bt_feed_moreinfo);
		 * tv_feedback_current_total =
		 * (TextView)header.findViewById(R.id.tv_feedback_today_total);
		 * tv_feedback_current_rewind =
		 * (TextView)header.findViewById(R.id.tv_feedback_today_rewind);
		 * tv_feedback_current_newStudy =
		 * (TextView)header.findViewById(R.id.tv_feedback_current_newstudy);
		 * tv_feed_today = (TextView)header.findViewById(R.id.tv_feed_today);
		 * 
		 * tv_feedback_username =
		 * (TextView)header.findViewById(R.id.tv_feedback_username);
		 * 
		 * tv_feedback_lv= (TextView)header.findViewById(R.id.tv_feedback_lv);
		 * 
		 * ll_testfeed = (LinearLayout)header.findViewById(R.id.ll_testfeed);
		 * ll_feed_lv = (LinearLayout)header.findViewById(R.id.ll_feed_lv);
		 * ll_level_graph =
		 * (LinearLayout)header.findViewById(R.id.ll_level_graph);
		 * ll_slide_feedback =
		 * (LinearLayout)header.findViewById(R.id.ll_slide_feedback);
		 * 
		 * pv_totalgraph =
		 * (StudyPercentView)header.findViewById(R.id.pv_totalgraph);
		 * 
		 * ll_graph_second=
		 * (LinearLayout)header.findViewById(R.id.ll_graph_second);
		 * 
		 * tv_graph_main_cate =
		 * (TextView)header.findViewById(R.id.tv_graph_main_cate);
		 * tv_graph_main_count =
		 * (TextView)header.findViewById(R.id.tv_graph_main_count);
		 * 
		 * tv_lvcount_current =
		 * (TextView)header.findViewById(R.id.tv_lvcount_current);
		 * tv_lvcount_total =
		 * (TextView)header.findViewById(R.id.tv_lvcount_total);
		 * tv_total_study_feed_title =
		 * (TextView)header.findViewById(R.id.tv_total_study_feed_title);
		 * 
		 * 
		 * view_current_level_do =
		 * (View)header.findViewById(R.id.view_current_level_do);
		 * view_current_level_rest =
		 * (View)header.findViewById(R.id.view_current_level_rest);
		 * 
		 * tv_info_total_study=
		 * (TextView)header.findViewById(R.id.tv_info_total_study);
		 * 
		 * Button bt_show_remeber_word =
		 * (Button)header.findViewById(R.id.bt_show_remeber_word);
		 * 
		 * bt_show_remeber_word.setOnClickListener(this);
		 * 
		 * 
		 * 
		 * LinearLayout ll_feedback_new_area =
		 * (LinearLayout)header.findViewById(R.id.ll_feedback_new_area);
		 * LinearLayout ll_feedback_reivew_area =
		 * (LinearLayout)header.findViewById(R.id.ll_feedback_reivew_area);
		 * ll_feedback_new_area.setOnClickListener(this);
		 * ll_feedback_reivew_area.setOnClickListener(this);
		 * 
		 * list = new ArrayList<Feed>();
		 * 
		 * lv_feedback_main.addHeaderView(header);
		 * //lv_feedback_main.addFooterView(footer);
		 * 
		 * adapter = new MyCustomAdapter(getApplicationContext(), thisActivity,
		 * lv_feedback_main);
		 * 
		 * lv_feedback_main.setAdapter(adapter);
		 * 
		 * iv_feedback_choice.setOnClickListener(this);
		 * iv_feedback_invite.setOnClickListener(this);
		 * iv_feedback_faq.setOnClickListener(this);
		 * bt.setOnClickListener(this); iv_edit_info.setOnClickListener(this);
		 * 
		 * makeView();
		 * 
		 * tv_feedback_lv.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * 
		 * // TODO Auto-generated method stub
		 * if(ll_level_graph.getVisibility()==View.VISIBLE){
		 * 
		 * FlurryAgent.endTimedEvent("BaseActivity:OpenLevelGraph");
		 * FlurryAgent.logEvent("BaseActivity:CloseLevelGraph");
		 * ll_level_graph.startAnimation(new
		 * AnimationUtils().loadAnimation(getApplicationContext(),
		 * R.anim.slide_out_to_top)); ll_level_graph.setVisibility(View.GONE);
		 * }else{ FlurryAgent.logEvent("BaseActivity:OpenLevelGraph",true);
		 * 
		 * level_counting = db.getLevelCounting(); setLevelCounting();
		 * ll_level_graph.startAnimation(new
		 * AnimationUtils().loadAnimation(getApplicationContext(),
		 * R.anim.slide_in_to_top)); ll_level_graph.setVisibility(View.VISIBLE);
		 * } } });
		 * 
		 * menu.setOnClosedListener(new SlidingMenu.OnClosedListener() {
		 * 
		 * @Override public void onClosed() { // TODO Auto-generated method stub
		 * ll_level_graph.setVisibility(View.GONE);
		 * //lv_feedback_main.setSelectionFromTop(1,
		 * header.getHeight()-ll_feed_lv.getHeight()-50);
		 * lv_feedback_main.setSelectionFromTop(0,0); } });
		 * 
		 * menu.setOnOpenListener(new SlidingMenu.OnOpenListener() {
		 * 
		 * @Override public void onOpen() { // TODO Auto-generated method stub
		 * setProfile(); }
		 * 
		 * });
		 * 
		 * final int screenWidth = ImageUtils.getScreenWidth(this);
		 * 
		 * 
		 * mBlurredImage = (ImageView) findViewById(R.id.blurred_image);
		 * mNormalImage = (ImageView) findViewById(R.id.normal_image); Date
		 * mdate = new Date(); final int month = (mdate.getMonth())%4; try {
		 * if(month ==
		 * Integer.parseInt(getMySharedPreferences(MainActivitys.M_DATE))){
		 * final File del_Image = new File(getFilesDir() + "blurred_image.png");
		 * del_Image.delete(); setMySharedPreferences(MainActivitys.M_DATE,
		 * String.valueOf(month)); } } catch (NumberFormatException e) { //
		 * TODO: handle exception setMySharedPreferences(MainActivitys.M_DATE,
		 * String.valueOf(month)); }
		 * 
		 * 
		 * 
		 * final File blurredImage = new File(getFilesDir() +
		 * "blurred_image.png"); Log.e("asdf", ""+getFilesDir().toString()); if
		 * (!blurredImage.exists()) {
		 * 
		 * // launch the progressbar in ActionBar
		 * setProgressBarIndeterminateVisibility(true);
		 * 
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run() { // No image found => let's generate it!
		 * BitmapFactory.Options options = new BitmapFactory.Options();
		 * options.inSampleSize = 4;
		 * 
		 * Bitmap image = null; switch (month) { case 0:
		 * mNormalImage.setImageResource(R.drawable.bg01); image =
		 * BitmapFactory.decodeResource(getResources(), R.drawable.bg01,
		 * options); break; case 1:
		 * mNormalImage.setImageResource(R.drawable.bg02); image =
		 * BitmapFactory.decodeResource(getResources(), R.drawable.bg02,
		 * options); break; case 2:
		 * mNormalImage.setImageResource(R.drawable.bg03); image =
		 * BitmapFactory.decodeResource(getResources(), R.drawable.bg03,
		 * options); break; case 3:
		 * mNormalImage.setImageResource(R.drawable.bg04); image =
		 * BitmapFactory.decodeResource(getResources(), R.drawable.bg04,
		 * options); break; default: break; }
		 * 
		 * 
		 * Bitmap newImg = Blur.fastblur(MainActivity.this, image, 20);
		 * ImageUtils.storeImage(newImg, blurredImage); runOnUiThread(new
		 * Runnable() {
		 * 
		 * @Override public void run() { updateView(screenWidth);
		 * 
		 * // And finally stop the progressbar
		 * setProgressBarIndeterminateVisibility(false); } });
		 * 
		 * } }).start();
		 * 
		 * } else {
		 * 
		 * // The image has been found. Let's update the view
		 * updateView(screenWidth);
		 * 
		 * }
		 * 
		 * //headerView = new View(this); //headerView.setLayoutParams(new
		 * LayoutParams(LayoutParams.MATCH_PARENT, TOP_HEIGHT));
		 * //lv_feedback_main.addHeaderView(headerView);
		 * 
		 * lv_feedback_main.setOnScrollListener(new
		 * AbsListView.OnScrollListener() {
		 * 
		 * @Override public void onScrollStateChanged(AbsListView view, int
		 * scrollState) { // TODO Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void onScroll(AbsListView view, int
		 * firstVisibleItem, int visibleItemCount, int totalItemCount) { // TODO
		 * Auto-generated method stub
		 * /*if(firstVisibleItem==ScrollLearningStatus){
		 * lrnStausOrTmLineParams.put("time",
		 * String.valueOf(System.currentTimeMillis()));
		 * FlurryAgent.logEvent("학습 현황 화면,MainActivity_tv_feedback_bleft,1",
		 * lrnStausOrTmLineParams, true); }else{ //TimeLine
		 * lrnStausOrTmLineParams.put("time",
		 * String.valueOf(System.currentTimeMillis()));
		 * FlurryAgent.logEvent("타임 라인 화면,MainActivity_tv_feedback_bleft,1",
		 * lrnStausOrTmLineParams, true);
		 * 
		 * }
		 */
		// Log.e("check_feed", ""+firstVisibleItem+
		// "                "+visibleItemCount);

		/*
		 * alpha = (float) -header.getTop() / (float) TOP_HEIGHT; // Apply a
		 * ceil if (alpha > 1) { alpha = 1; } // Apply on the ImageView if
		 * needed
		 * 
		 * //alpha값 바뀌는데 걸리는 시간 체크 Log.e("alpha_val", "1");
		 * mBlurredImage.setAlpha(alpha); Log.d("alpha_val", "1");
		 * 
		 * 
		 * // Parallax effect : we apply half the scroll amount to our // three
		 * views mBlurredImage.setTop(header.getTop() / 7);
		 * mNormalImage.setTop(header.getTop() / 7); } });
		 */
	}

	protected void setProfile() {
		// TODO Auto-generated method stub
		pause(cTimer);

		String title = "";
		;

		readProfile();

		memorize_word.setText(Integer.toString(db.getKnownCount()));

		Integer fcount[] = db.getForgetCount();
		review_word.setText(Integer.toString(fcount[0] + fcount[1]));

		int not_zero_state = db.getNoZeroState();
		int forgetting_curve_percentage = (not_zero_state / 5);

		forgetting_curve_percentage_bar
		.setProgress(forgetting_curve_percentage);
		forgetting_curve_percentage_count.setText(Integer
				.toString(forgetting_curve_percentage));

		String name = getMySharedPreferences(MainActivitys.GpreName);
		if (name.equals("")) {
			name = "알수없음";
		}
		String year = date.get_today().substring(0, 4);
		String month = date.get_today().substring(4, 6);
		String day = date.get_today().substring(6, 8);

		String topic = getMySharedPreferences(MainActivitys.GpreTopic);
		if (topic.length() == 0) {
			topic = "1";
		}

	}

	Bitmap bitmap;

	public void readProfile() {

		thumbnailURL = getMySharedPreferences("image");
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				String path = Config.DB_FILE_DIR;

				File directory = new File(path);

				File file = new File(directory, Config.PROFILE_NAME); // or any
				// other
				// format
				// supported

				FileInputStream streamIn;

				try {
					streamIn = new FileInputStream(file);
					bitmap = BitmapFactory.decodeStream(streamIn); // This gets
					// the image
					streamIn.close();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (bitmap != null) {
					bitmap = roundBitmap(bitmap);
				}

				// url = new URL(thumbnailURL);
				// URLConnection conn = url.openConnection();
				// conn.connect();// url연결
				// bis = new BufferedInputStream(
				// conn.getInputStream()); // 접속한 url로부터 데이터값을 얻어온다
				// bm = BitmapFactory.decodeStream(bis);// 얻어온 데이터 Bitmap 에저장
				// bm = roundBitmap(bm);
				// bis.close();// 사용을 다한 BufferedInputStream 종료

				handler.post(new Runnable() {
					@Override
					public void run() {
						try {
							if (!bitmap.equals(null)) {
								iv_picture.setImageBitmap(bitmap);
							}
						} catch (NullPointerException e) {
							// TODO: handle exception
							// iv_picture
							// .setImageResource(R.drawable.my_btn_photo_default);
						}
					}
				});
			}
		});
		thread.start();
		// load image

	}

	public Bitmap roundBitmap(Bitmap source) {
		int w = source.getWidth();
		int h = source.getHeight();
		Bitmap rounder = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(rounder);
		Path mPath = new Path();
		BitmapDrawable drawable = new BitmapDrawable(source);
		drawable.setBounds(0, 0, w, h);
		canvas.translate(0, 0);
		mPath.reset();
		canvas.clipPath(mPath);
		mPath.addCircle(w / 2, h / 2, w / 2, Path.Direction.CCW);
		canvas.clipPath(mPath, Region.Op.REPLACE);
		drawable.draw(canvas);

		return rounder;
	}

	LinearLayout.LayoutParams view_lp;
	LinearLayout.LayoutParams view_rp;

	protected void setLevelCounting() {
		// TODO Auto-generated method stub
		boolean current_lv = false;
		for (int i = 0; i < 6; i++) {
			if (i + 1 == Config.Difficulty) {
				current_lv = true;
				tv_level[i].setTextColor(Color.rgb(0, 186, 105));
				tv_lvcount_current.setText("" + level_counting[i][0]);
				tv_lvcount_total.setText("/" + level_counting[i][1]);
				view_lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT, 6);
				view_rp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT, 2);
				view_lp.weight = level_counting[i][1];
				view_current_level_do.setLayoutParams(view_lp);
				view_rp.weight = level_counting[i][0] - view_lp.weight + 1;
				view_current_level_rest.setLayoutParams(view_rp);
			} else {
				current_lv = false;
				tv_level[i].setTextColor(Color.rgb(255, 255, 255));
			}

			if (level_counting[i][0] == 0) {
				iv_level[i].setImageResource(R.drawable.feed_level_lv1_n + i
						* 3);
				percentView[i].setPercentage(0, current_lv);
			} else {
				iv_level[i].setImageResource(R.drawable.feed_level_lv1 + i * 3);
				float per = (level_counting[i][0] * 100) / level_counting[i][1];
				percentView[i].setPercentage(per + 1, current_lv);
				Log.e("levelcount", "" + level_counting[i][0] + "     "
						+ level_counting[i][1]);
				if (level_counting[i][0].equals(level_counting[i][1])) {
					iv_level[i].setImageResource(R.drawable.feed_level_lv1_100
							+ i * 3);
					Log.e("levelcount", "하하하하하");
				}
			}
			tv_level[i].setText("" + level_counting[i][0] + "/"
					+ level_counting[i][1]);

		}
	}

	private void makeView() {
		// TODO Auto-generated method stub

		iv_level[0] = (ImageView) header.findViewById(R.id.iv_level_1);
		iv_level[1] = (ImageView) header.findViewById(R.id.iv_level_2);
		iv_level[2] = (ImageView) header.findViewById(R.id.iv_level_3);
		iv_level[3] = (ImageView) header.findViewById(R.id.iv_level_4);
		iv_level[4] = (ImageView) header.findViewById(R.id.iv_level_5);
		iv_level[5] = (ImageView) header.findViewById(R.id.iv_level_6);

		tv_level[0] = (TextView) header.findViewById(R.id.tv_level_1);
		tv_level[1] = (TextView) header.findViewById(R.id.tv_level_2);
		tv_level[2] = (TextView) header.findViewById(R.id.tv_level_3);
		tv_level[3] = (TextView) header.findViewById(R.id.tv_level_4);
		tv_level[4] = (TextView) header.findViewById(R.id.tv_level_5);
		tv_level[5] = (TextView) header.findViewById(R.id.tv_level_6);

		percentView[0] = (PercentView) header.findViewById(R.id.percentView1);
		percentView[1] = (PercentView) header.findViewById(R.id.percentView2);
		percentView[2] = (PercentView) header.findViewById(R.id.percentView3);
		percentView[3] = (PercentView) header.findViewById(R.id.percentView4);
		percentView[4] = (PercentView) header.findViewById(R.id.percentView5);
		percentView[5] = (PercentView) header.findViewById(R.id.percentView6);
		for (int i = 0; i < percentView.length; i++) {
			percentView[i].setOnClickListener(this);
		}

		set_graph_view();

		for (int i = 0; i < tv_explain_feed.length; i++) {
			tv_explain_feed[i].setOnClickListener(this);
		}

	}

	View view_title_top;
	View view_title_bottom;
	View view_title_top_div;
	View view_title_bottom_div;
	TextView tv_feedback_bleft_contents_unknown;
	View view_show_remeber_word;
	Button bt_show_remeber_word;
	LinearLayout ll_feedback_tleft_total;
	LinearLayout ll_feedback_tright_total;

	public void set_graph_view() {

		tv_explain_feed[0] = (TextView) header
				.findViewById(R.id.tv_feedback_tleft_total_count);
		tv_explain_feed[1] = (TextView) header
				.findViewById(R.id.tv_feedback_tleft_total_text);

		tv_explain_feed[2] = (TextView) header
				.findViewById(R.id.tv_feedback_tright_total_count);
		tv_explain_feed[3] = (TextView) header
				.findViewById(R.id.tv_feedback_tright_total_text);

		tv_explain_feed[4] = (TextView) header
				.findViewById(R.id.tv_feedback_bleft_count);
		tv_explain_feed[5] = (TextView) header
				.findViewById(R.id.tv_feedback_bleft_title);
		tv_explain_feed[6] = (TextView) header
				.findViewById(R.id.tv_feedback_bleft_contents);

		tv_explain_feed[7] = (TextView) header
				.findViewById(R.id.tv_feedback_bright_count);
		tv_explain_feed[8] = (TextView) header
				.findViewById(R.id.tv_feedback_bright_title);
		tv_explain_feed[9] = (TextView) header
				.findViewById(R.id.tv_feedback_bright_contents);

		tv_feedback_bleft_contents_unknown = (TextView) header
				.findViewById(R.id.tv_feedback_bleft_contents_unknown);

		view_explain_left = (View) header.findViewById(R.id.view_percent_left);
		view_explain_right = (View) header
				.findViewById(R.id.view_percent_right);

		view_title_top = (View) header.findViewById(R.id.view_title_top);
		view_title_bottom = (View) header.findViewById(R.id.view_title_bottom);

		view_title_top_div = (View) header
				.findViewById(R.id.view_feedback_bleft);
		view_title_bottom_div = (View) header
				.findViewById(R.id.view_feedback_bright);

		view_show_remeber_word = (View) header
				.findViewById(R.id.view_show_remeber_word);
		bt_show_remeber_word = (Button) header
				.findViewById(R.id.bt_show_remeber_word);

		ll_feedback_tleft_total = (LinearLayout) header
				.findViewById(R.id.ll_feedback_tleft_total);
		ll_feedback_tright_total = (LinearLayout) header
				.findViewById(R.id.ll_feedback_tright_total);

		tv_sec_explain_feed[0] = (TextView) header
				.findViewById(R.id.tv_feedback_bleft_contents1);
		tv_sec_explain_feed[1] = (TextView) header
				.findViewById(R.id.tv_feedback_bleft_contents2);
		tv_sec_explain_feed[2] = (TextView) header
				.findViewById(R.id.tv_feedback_bleft_contents3);
		tv_sec_explain_feed[0].setOnClickListener(this);
		tv_sec_explain_feed[1].setOnClickListener(this);
		tv_sec_explain_feed[2].setOnClickListener(this);

		for (int i = 0; i < tv_explain_feed.length; i++) {
			tv_explain_feed[i].setOnClickListener(this);
		}
		// set_graph_color(check_graph, check_text);
	}

	public void set_graph_color(boolean checked_g, boolean checked_t) {

		setProfile();

		int left_top = Color.rgb(0x31, 0x80, 0x60);
		int left_bottom = Color.rgb(0x00, 0x99, 0x49);
		int right_top = Color.rgb(0xe6, 0x44, 0x2e);
		int right_bottom = Color.rgb(0x8c, 0x60, 0x5c);
		int checked_color = 0xff;
		int unchecked_color = 0x22;
		int unchecked_big_color = 0x44;
		int checked_small_color = 0x77;

		// tv_feedback_total_study.setText(""+db.getTotalStudy());
		// tv_feedback_perfect_remember.setText(""+db.getPRememberCount());
		// tv_feedback_potential_forget.setText(""+db.getMforget());
		// tv_feedback_ready_study.setText(""+555);
		// tv_feedback_remember_word.setText(""+db.getKnownCount());
		// tv_feedback_current_unknown.setText(""+db.getUnknownCount());

		int KnownCount = db.getKnownCount();
		int UnKnownCount = db.getUnKnownCount();
		pv_totalgraph.setPercentage((int) ((KnownCount) * 100 / (KnownCount
				+ UnKnownCount + 1)), check_graph);

		tv_explain_feed[0].setText("" + KnownCount);
		tv_explain_feed[2].setText("" + UnKnownCount);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);

		if (checked_g) {
			Integer[] f_count = db.getForgetCount();
			int Mforget = db.getMforget();
			int PRemember = db.getPRememberCount();
			ll_graph_second.setVisibility(View.VISIBLE);
			tv_explain_feed[4].setText("" + Mforget);
			tv_explain_feed[5].setText("불안한 단어");
			tv_explain_feed[6].setText("지금 복습하면 잊지 않을 단어 ");
			tv_explain_feed[7].setText("" + PRemember);
			tv_explain_feed[8].setText("안심 단어");
			tv_explain_feed[9].setText("당장은 잊어버릴 가능성이 없는 단어입니다.");
			tv_feedback_bleft_contents_unknown.setVisibility(View.GONE);

			tv_sec_explain_feed[0].setText("" + f_count[1]);
			tv_sec_explain_feed[1].setText("복습 타이밍을 놓쳐 잊은 단어");
			tv_sec_explain_feed[2].setText("" + f_count[0]);

			lp.weight = PRemember;
			view_explain_left.setLayoutParams(lp);
			rp.weight = Mforget;
			view_explain_right.setLayoutParams(rp);

			view_show_remeber_word.setVisibility(View.VISIBLE);
			bt_show_remeber_word.setVisibility(View.VISIBLE);
			ll_feedback_tleft_total
			.setBackgroundResource(R.drawable.feed_graph_activate);
			ll_feedback_tright_total
			.setBackgroundResource(R.drawable.feed_graph_deactive);
		} else {
			ll_graph_second.setVisibility(View.GONE);
			tv_feedback_bleft_contents_unknown.setVisibility(View.VISIBLE);
			int NotRemem = db.getNotRememCount();
			int NotShown = db.getNotShownCount();

			tv_explain_feed[4].setText("" + db.getNotRememCount());
			tv_explain_feed[5].setText("암기 중인 단어");
			tv_feedback_bleft_contents_unknown
			.setText("뜻을 확인했지만 아직 외우지 못한 단어입니다.");
			tv_explain_feed[7].setText("" + db.getNotShownCount());
			tv_explain_feed[8].setText("학습 대기 단어");
			tv_explain_feed[9].setText("아직 한번도 보지 않은 단어입니다.");

			lp.weight = NotShown;
			view_explain_left.setLayoutParams(lp);
			rp.weight = NotRemem;
			view_explain_right.setLayoutParams(rp);
			view_show_remeber_word.setVisibility(View.INVISIBLE);
			bt_show_remeber_word.setVisibility(View.INVISIBLE);
			ll_feedback_tleft_total
			.setBackgroundResource(R.drawable.feed_graph_deactive_r);
			ll_feedback_tright_total
			.setBackgroundResource(R.drawable.feed_graph_activate_r);
		}

		if (checked_g) {
			tv_explain_feed[0].setTextColor(Color.rgb(0x23, 0x77, 0x46));
			tv_explain_feed[1].setTextColor(Color.rgb(0x23, 0x77, 0x46));
			tv_explain_feed[2].setTextColor(Color.argb(0x44, 0xff, 0xff, 0xff));
			tv_explain_feed[3].setTextColor(Color.argb(0x44, 0xff, 0xff, 0xff));

			tv_explain_feed[2] = (TextView) header
					.findViewById(R.id.tv_feedback_tright_total_count);
			tv_explain_feed[3] = (TextView) header
					.findViewById(R.id.tv_feedback_tright_total_text);

			view_explain_left.setBackgroundColor(left_top);
			view_explain_right.setBackgroundColor(left_bottom);

			tv_explain_feed[4].setTextColor(left_top);
			tv_explain_feed[5].setTextColor(left_top);
			tv_explain_feed[7].setTextColor(left_bottom);
			tv_explain_feed[8].setTextColor(left_bottom);

			view_title_top.setBackgroundColor(left_top);
			view_title_bottom.setBackgroundColor(left_bottom);

			view_title_top_div.setBackgroundColor(left_top);
			view_title_bottom_div.setBackgroundColor(left_bottom);
		} else {
			tv_explain_feed[0].setTextColor(Color.argb(0x44, 0xff, 0xff, 0xff));
			tv_explain_feed[1].setTextColor(Color.argb(0x44, 0xff, 0xff, 0xff));

			tv_explain_feed[2].setTextColor(Color.rgb(0x89, 0x0e, 0x00));
			tv_explain_feed[3].setTextColor(Color.rgb(0x89, 0x0e, 0x00));

			tv_explain_feed[4].setTextColor(right_top);
			tv_explain_feed[5].setTextColor(right_top);
			tv_explain_feed[7].setTextColor(right_bottom);
			tv_explain_feed[8].setTextColor(right_bottom);
			view_explain_left.setBackgroundColor(right_top);
			view_explain_right.setBackgroundColor(right_bottom);
			view_title_top.setBackgroundColor(right_top);
			view_title_bottom.setBackgroundColor(right_bottom);

			view_title_top_div.setBackgroundColor(right_top);
			view_title_bottom_div.setBackgroundColor(right_bottom);
		}

		adapter.notifyDataSetChanged();
	}

	private void updateView(final int screenWidth) {
		try {
			Bitmap bmpBlurred = BitmapFactory.decodeFile(getFilesDir()
					+ "blurred_image.png");
			bmpBlurred = Bitmap
					.createScaledBitmap(
							bmpBlurred,
							screenWidth,
							(int) (bmpBlurred.getHeight()
									* ((float) screenWidth) / (float) bmpBlurred
									.getWidth()), false);

			mBlurredImage.setImageBitmap(bmpBlurred);
			final int month = (date.getMonth()) % 4;
			switch (month) {
			case 0:
				mNormalImage.setImageResource(R.drawable.bg01);
				break;
			case 1:
				mNormalImage.setImageResource(R.drawable.bg02);
				break;
			case 2:
				mNormalImage.setImageResource(R.drawable.bg03);
				break;
			case 3:
				mNormalImage.setImageResource(R.drawable.bg04);
				break;
			default:
				break;
			}
		} catch (NullPointerException e) {
			// TODO: handle exception

			setProgressBarIndeterminateVisibility(true);

			final File blurredImage = new File(getFilesDir()
					+ "blurred_image.png");

			new Thread(new Runnable() {

				@Override
				public void run() {
					// No image found => let's generate it!
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 4;

					Bitmap image = null;
					final int month = (date.getMonth()) % 4;
					switch (month) {
					case 0:
						mNormalImage.setImageResource(R.drawable.bg01);
						image = BitmapFactory.decodeResource(getResources(),
								R.drawable.bg01, options);
						break;
					case 1:
						mNormalImage.setImageResource(R.drawable.bg02);
						image = BitmapFactory.decodeResource(getResources(),
								R.drawable.bg02, options);
						break;
					case 2:
						mNormalImage.setImageResource(R.drawable.bg03);
						image = BitmapFactory.decodeResource(getResources(),
								R.drawable.bg03, options);
						break;
					case 3:
						mNormalImage.setImageResource(R.drawable.bg04);
						image = BitmapFactory.decodeResource(getResources(),
								R.drawable.bg04, options);
						break;
					default:
						break;
					}

					final Bitmap newImg = Blur.fastblur(MainActivity.this,
							image, 20);
					ImageUtils.storeImage(newImg, blurredImage);

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							updateView(screenWidth);

							// And finally stop the progressbar
							setProgressBarIndeterminateVisibility(false);

						}
					});

				}
			}).start();
		}

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(MainActivity.this, ExpainFeedPopup.class);

		switch (v.getId()) {

		case R.id.lv_feedback_main1:

			if (isExam)
				mCurrentFragmentIndex = FRAGMENT_EXAM_SCORE_LIST;
			else
				mCurrentFragmentIndex = FRAGMENT_WORD_SCORE_LIST;

			mMainWordIndex = mCurrentFragmentIndex;
			fragmentReplace(mCurrentFragmentIndex);
			break;

		case R.id.tv_feedback_tleft_total_count:
		case R.id.tv_feedback_tleft_total_text:
			check_graph = true;
			check_text = true;
			// set_graph_color(check_graph, check_text);
			break;
		case R.id.tv_feedback_tright_total_count:
		case R.id.tv_feedback_tright_total_text:
			check_graph = false;
			check_text = true;
			// set_graph_color(check_graph, check_text);
			break;

		case R.id.tv_feedback_bleft_count:
		case R.id.tv_feedback_bleft_title:
		case R.id.tv_feedback_bleft_contents:
		case R.id.view_percent_left:
		case R.id.view_feedback_bleft:
		case R.id.tv_feedback_bleft_contents1:
		case R.id.tv_feedback_bleft_contents2:
		case R.id.tv_feedback_bleft_contents3:
			check_text = true;
			// set_graph_color(check_graph, check_text);

			statusParams
			.put("time", String.valueOf(System.currentTimeMillis()));
			FlurryAgent.logEvent("BaseActivity:ClickMemorizedWordInfo");

			break;

		case R.id.tv_feedback_bright_count:
		case R.id.tv_feedback_bright_title:
		case R.id.tv_feedback_bright_contents:
		case R.id.view_percent_right:
		case R.id.view_feedback_bright:
			check_text = false;
			// set_graph_color(check_graph, check_text);

			statusParams
			.put("time", String.valueOf(System.currentTimeMillis()));
			FlurryAgent.logEvent("BaseActivity:ClickRemainWordInfo");

			break;

		case R.id.iv_feedback_choice:
			Intent intent_help = new Intent(MainActivity.this,
					com.ihateflyingbugs.vocaslide.SideActivity.class);
			intent_help.putExtra("Fragment", SideActivity.FRAGMENT_CHOICE);
			intent_help.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent_help, 123);
			overridePendingTransition(R.anim.slide_in_left, 0);
			lv_feedback_main.startAnimation(new AnimationUtils().loadAnimation(
					getApplicationContext(), R.anim.slide_out_left));
			lv_feedback_main.setVisibility(View.GONE);
			break;
		case R.id.iv_feedback_invite:
			try {
				sendUrlLink(v);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			overridePendingTransition(R.anim.slide_in_left, 0);
			break;
		case R.id.iv_feedback_faq:
			Intent intent_setting = new Intent(MainActivity.this,
					com.ihateflyingbugs.vocaslide.SideActivity.class);
			intent_setting.putExtra("Fragment", SideActivity.FRAGMENT_FAQ);
			intent_setting.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent_setting, 123);
			overridePendingTransition(R.anim.slide_in_left, 0);
			lv_feedback_main.startAnimation(new AnimationUtils().loadAnimation(
					getApplicationContext(), R.anim.slide_out_left));
			lv_feedback_main.setVisibility(View.GONE);
			break;
			
			

		case R.id.iv_feed_study:
			Intent intent_study = new Intent(MainActivity.this,
					com.ihateflyingbugs.vocaslide.SideActivity.class);
			intent_study.putExtra("Fragment", SideActivity.FRAGMENT_Study);
			intent_study.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent_study, 123);
			overridePendingTransition(R.anim.slide_in_left, 0);
			lv_feedback_main.startAnimation(new AnimationUtils().loadAnimation(
					getApplicationContext(), R.anim.slide_out_left));
			lv_feedback_main.setVisibility(View.GONE);
			break;
		case R.id.iv_feed_notice:
			Intent intent_notice = new Intent(MainActivity.this,
					com.ihateflyingbugs.vocaslide.SideActivity.class);
			intent_notice.putExtra("Fragment", SideActivity.FRAGMENT_BOARD);
			intent_notice.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent_notice, 123);
			overridePendingTransition(R.anim.slide_in_left, 0);
			lv_feedback_main.startAnimation(new AnimationUtils().loadAnimation(
					getApplicationContext(), R.anim.slide_out_left));
			lv_feedback_main.setVisibility(View.GONE);
			break;
		case R.id.iv_feed_level:

			break;

		case R.id.iv_edit_info:
			Intent intent_myInfo = new Intent(MainActivity.this,
					com.ihateflyingbugs.vocaslide.SideActivity.class);
			intent_myInfo.putExtra("Fragment", SideActivity.FRAGMENT_MYINFO);
			intent_myInfo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent_myInfo, 123);

			overridePendingTransition(R.anim.slide_in_left, 0);
			lv_feedback_main.startAnimation(new AnimationUtils().loadAnimation(
					getApplicationContext(), R.anim.slide_out_left));
			lv_feedback_main.setVisibility(View.GONE);
			break;
		case R.id.bt_show_remeber_word:
			Intent intent_known = new Intent(MainActivity.this,
					Known_Activity.class);
			intent_known.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent_known, 123);
			overridePendingTransition(android.R.anim.fade_in,
					android.R.anim.fade_out);
			break;
		case R.id.percentView1:
			levelParams.put("time", String.valueOf(System.currentTimeMillis()));
			FlurryAgent.logEvent("BaseActivity:ClickLevel1");
			setCurrentLevel(1);

			break;
		case R.id.percentView2:
			levelParams.put("time", String.valueOf(System.currentTimeMillis()));
			FlurryAgent.logEvent("BaseActivity:ClickLevel2");

			if (Config.Difficulty > 2) {
				if (level_counting[1][0] > 0) {
					setCurrentLevel(2);
				} else if (level_counting[1][0] == level_counting[1][1]) {
					Toast.makeText(MainActivity.this, "해당레벨의 단어를 모두 오우셨습니다.",
							Toast.LENGTH_SHORT).show();
				} else {
					setCurrentLevel(2);
				}
			} else if (Config.Difficulty == 2) {
				Toast.makeText(MainActivity.this, "현재 레벨입니다.",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(MainActivity.this, "해당 레벨로 변경하실 수 없습니다.",
						Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.percentView3:
			levelParams.put("time", String.valueOf(System.currentTimeMillis()));
			FlurryAgent.logEvent("BaseActivity:ClickLevel3");

			if (Config.Difficulty > 3) {
				if (level_counting[2][0] > 0) {
					setCurrentLevel(3);
				} else if (level_counting[2][0] == level_counting[2][1]) {
					Toast.makeText(MainActivity.this, "해당레벨의 단어를 모두 오우셨습니다.",
							Toast.LENGTH_SHORT).show();
				} else {
					setCurrentLevel(3);
				}
			} else if (Config.Difficulty == 3) {
				Toast.makeText(MainActivity.this, "현재 레벨입니다.",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(MainActivity.this, "해당 레벨로 변경하실 수 없습니다.",
						Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.percentView4:
			levelParams.put("time", String.valueOf(System.currentTimeMillis()));
			FlurryAgent.logEvent("BaseActivity:ClickLevel4");

			if (Config.Difficulty > 4) {
				if (level_counting[3][0] > 0) {
					setCurrentLevel(4);
				} else if (level_counting[3][0] == level_counting[3][1]) {
					Toast.makeText(MainActivity.this, "해당레벨의 단어를 모두 오우셨습니다.",
							Toast.LENGTH_SHORT).show();
				} else {
					setCurrentLevel(4);
				}
			} else if (Config.Difficulty == 4) {
				Toast.makeText(MainActivity.this, "현재 레벨입니다.",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(MainActivity.this, "해당 레벨로 변경하실 수 없습니다.",
						Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.percentView5:
			levelParams.put("time", String.valueOf(System.currentTimeMillis()));
			FlurryAgent.logEvent("BaseActivity:ClickLevel5");

			if (Config.Difficulty > 5) {
				if (level_counting[4][0] > 0) {
					setCurrentLevel(5);
				} else if (level_counting[4][0] == level_counting[4][1]) {
					Toast.makeText(MainActivity.this, "해당레벨의 단어를 모두 오우셨습니다.",
							Toast.LENGTH_SHORT).show();
				} else {
					setCurrentLevel(5);
				}
			} else if (Config.Difficulty == 5) {
				Toast.makeText(MainActivity.this, "현재 레벨입니다.",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(MainActivity.this, "해당 레벨로 변경하실 수 없습니다.",
						Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.percentView6:
			levelParams.put("time", String.valueOf(System.currentTimeMillis()));
			FlurryAgent.logEvent("BaseActivity:ClickLevel6");

			if (Config.Difficulty > 5) {
				if (level_counting[5][0] > 0) {
					setCurrentLevel(6);
				} else if (level_counting[5][0] == level_counting[5][1]) {
					Toast.makeText(MainActivity.this, "해당레벨의 단어를 모두 오우셨습니다.",
							Toast.LENGTH_SHORT).show();
				}
			} else if (Config.Difficulty == 6) {
				Toast.makeText(MainActivity.this, "현재 레벨입니다.",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(MainActivity.this, "해당 레벨로 변경하실 수 없습니다.",
						Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.ll_action_review:
			if (tv_show_reviewexplain.getVisibility() == View.VISIBLE) {
				tv_show_reviewexplain.setVisibility(View.GONE);
				tv_show_reviewexplain.startAnimation(new AnimationUtils()
				.loadAnimation(getApplicationContext(),
						android.R.anim.fade_out));
			} else {
				tv_show_reviewexplain.setVisibility(View.VISIBLE);
				tv_show_reviewexplain.startAnimation(new AnimationUtils()
				.loadAnimation(getApplicationContext(),
						android.R.anim.fade_in));
			}
			break;

		case R.id.ll_feedback_new_area:

			new AlertDialog.Builder(MainActivity.this)
			.setMessage("오늘 새롭게 만나 외운 단어들의 숫자입니다.")
			.setPositiveButton("확인", null).show();
			break;
		case R.id.ll_feedback_reivew_area:
			new AlertDialog.Builder(MainActivity.this)
			.setMessage(
					"이미 외운 단어들 중\n잊혀져 가는 불안한 단어들의 숫자와\n그 중 단어 어장관리를 통해 복습에 성공한 단어들의 숫자입니다.")
					.setPositiveButton("확인", null).show();
			break;
		case R.id.view_topnavi:
			menu.toggle();

			break;
		case R.id.bt_graph_show_explain:
			Intent intent_explain = new Intent(MainActivity.this,
					com.ihateflyingbugs.vocaslide.SideActivity.class);
			intent_explain.putExtra("Fragment", SideActivity.FRAGMENT_EXPLAIN);
			intent_explain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent_explain, 123);
			overridePendingTransition(R.anim.slide_in_left, 0);
			lv_feedback_main.startAnimation(new AnimationUtils().loadAnimation(
					getApplicationContext(), R.anim.slide_out_left));
			lv_feedback_main.setVisibility(View.GONE);
			break;
		}

	}

	public void setCurrentLevel(final int choiced_level) {
		menu.toggle();

		if (choiced_level <= Config.MAX_DIFFICULTY) {
			new AlertDialog.Builder(MainActivity.this)
			.setMessage("해당 레벨로 이동하시겠습니까?")
			.setPositiveButton("확인",
					new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					// TODO Auto-generated method stub
					Config.Difficulty = choiced_level;
					setMySharedPreferences(
							MainActivitys.GpreLevel, ""
									+ choiced_level);
					db.deleteAllCurrentWord();
					SlidefragmentReplace(FRAGMENT_WORD_SCORE_LIST);

				}
			})
			.setNegativeButton("취소",
					new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					// TODO Auto-generated method stub
					menu.toggle();

				}
			}).show();
		}
	}

	private void setActionBar(boolean isMenuShow, String title) {
		if (isMenuShow) {
			chkMode.setVisibility(View.VISIBLE);
			tvTitle.setVisibility(View.INVISIBLE);
		} else {
			chkMode.setVisibility(View.INVISIBLE);

			tvTitle.setText(title);
			tvTitle.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Log.d("kjw", "onActivityResult");
		lv_feedback_main.setVisibility(View.VISIBLE);
		// set_graph_color(check_graph, check_text);

	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {

		return false;
	}

	public void setFeedPage() {

	}

	@Override
	public void onBackPressed() {
		if (menu.isMenuShowing()) {
			menu.toggle();

			return;
		}

		if (!isBackPressed) {
			isBackPressed = true;

			Toast.makeText(MainActivity.this, "뒤로가기 버튼을 한번 더 눌러 종료 하여 주십시오.",
					Toast.LENGTH_SHORT).show();

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					isBackPressed = false;
				}
			}, 2000);
		} else {

			isFinish = true;
			// moveTaskToBack(true);
			// handler.postDelayed(new Runnable() {
			//
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// if(isFinish){
			finish();
			// }
			// }
			// }, 15000);
		}
	}

	public void sendUrlLink(View v) throws NameNotFoundException {
		// Recommended: Use application mContext for parameter.
		KakaoLink kakaoLink = KakaoLink.getLink(getApplicationContext());

		// int checkedRadioButtonId = rg.getCheckedRadioButtonId();

		// check, intent is available.
		if (!kakaoLink.isAvailableIntent()) {
			alert("카카오톡을 설치하고 다시 시도해 주세요");
			return;
		}

		FlurryAgent.logEvent("BaseActivity:InviteFriends");

		String mssg = "영어 단어 봐도 봐도 까먹는다면 밀당 영단어! 아래의 링크를 클릭해주세요";
		kakaoLink
		.openKakaoLink(
				this,
				"http://bit.ly/MDAppDwns",
				mssg,
				getPackageName(),
				getPackageManager().getPackageInfo(getPackageName(), 0).versionName,
				"밀당 영단어", "UTF-8");
	}

	private void alert(String message) {
		new AlertDialog.Builder(this).setIcon(R.drawable.icon48)
		.setTitle(R.string.app_name).setMessage(message)
		.setPositiveButton(android.R.string.ok, null).create().show();
	}

	public static String getMySharedPreferences(String _key) {
		if (mPreference == null) {
			mPreference = context.getSharedPreferences(Config.PREFS_NAME,
					MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		}
		return mPreference.getString(_key, "");
	}

	public static void setMySharedPreferences(String _key, String _value) {
		if (mPreference == null) {
			mPreference = context.getSharedPreferences(Config.PREFS_NAME,
					MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		}
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putString(_key, _value);
		editor.commit();
	}

	private static int index = 0;
	private final static int interval = 50;
	private static boolean isRunning = true;

	public static void set_tvLevel() {
		tvLevel.setText("Level : " + Config.Difficulty);
	}

	public void SlidefragmentReplace(int reqNewFragmentIndex) {

		Fragment fragment = getslideFragment(reqNewFragmentIndex);
		Log.e("fragments", " " + reqNewFragmentIndex);
		// replace fragment
		final FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.linearFragment, fragment).commit();
	}

	private Fragment getslideFragment(int idx) {
		Fragment newFragment = null;

		switch (idx) {
		case FRAGMENT_WORD_SCORE_LIST:
			newFragment = new WordListFragment(
					WordListFragment.MODE_NORMAL_SCORE_LIST);
			break;
		case FRAGMENT_WORD_KNOWN_LIST:
			newFragment = new WordListFragment(
					WordListFragment.MODE_NORMAL_KNOWN_LIST);
			break;

		case FRAGMENT_EXAM_KNOWN_LIST:
			newFragment = new WordListFragment(
					WordListFragment.MODE_EXAM_KNOWN_LIST, query);
			break;

		case FRAGMENT_EXAM_SCORE_LIST:
			newFragment = new WordListFragment(
					WordListFragment.MODE_EXAM_SCORE_LIST, query);
			break;

		default:
			break;
		}

		return newFragment;
	}

	public static void finish_logout() {
		thisActivity.finish();
	}

	String starttime;
	String startdate;



	Map[] maps = new Map[41];

	Map<String, String> articleParams;

	protected void onStart() {
		super.onStart();

		articleParams = new HashMap<String, String>();
		MainActivitys.GpreFlag = true;
		MainActivitys.GpreAccessDuration = 0;
		// writeLog("[MainActivity 시작,MainActivity,1]\r\n");
		FlurryAgent.onStartSession(this,
				Config.setFlurryKey(getApplicationContext()));
		FlurryAgent.setUserId(mPreference.getString(MainActivitys.GpreID,
				"000000"));
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		// 카카오톡에 저 부분만 com.kakao.talk.activity.main.MainTabFragmentActivity만 로그를
		// 보내고
		// 나머지는 안보낸다

		Log.e("activitygg", "Base onstart");
		FlurryAgent.logEvent("BaseActivity", articleParams);
	}

	protected void onStop() {
		super.onStop();

		pause(cTimer);
		Log.d("Timer", "onStop()");

		// FlurryAgent.endTimedEvent("BaseActivity:Start");
		setMySharedPreferences(MainActivitys.GpreEndTime,
				"" + System.currentTimeMillis());
		setMySharedPreferences(MainActivitys.GpreTotalReviewCnt,
				"" + db.getMforget());
		setMySharedPreferences(MainActivitys.GpreTodayReviewCnt,
				"" + settings.getInt(Config.REVIEW_COUNT, 0));
		setMySharedPreferences(MainActivitys.GpreTodayLearnCnt,
				"" + settings.getInt(Config.NEW_COUNT, 0));

		MainActivitys.GpreFlag = true;

		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
			// Do whatever
			articleParams.put("WIFI", "On");
		} else {
			articleParams.put("WIFI", "Off");
		}

		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put(
				"Duration",
				""
						+ ((Long.valueOf(System.currentTimeMillis()) - Long
								.valueOf(starttime))) / 1000);
		articleParams.put("know_action_cnt", "" + Config.know_count);
		articleParams.put("unknow_action_cnt", "" + Config.unknow_count);

		articleParams.put("total_study_cnt", "" + db.getKnownCount());
		articleParams.put("today_review_cnt",
				"" + settings.getInt(Config.REVIEW_COUNT, 0));
		articleParams.put("today_new_cnt",
				"" + settings.getInt(Config.NEW_COUNT, 0));

		ActivityManager am = (ActivityManager) this
				.getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName componentInfo = taskInfo.get(0).topActivity;

		if (!componentInfo.getPackageName().equals(
				"com.ihateflyingbugs.vocaslide")
				&& !isFinish) {
			Map<String, String> Escape = new HashMap<String, String>();
			Escape.put("package", "" + componentInfo.getPackageName());
			FlurryAgent.logEvent("BaseActivity:EscapeApplication", Escape);
			Log.e("bookmark", "" + Escape.get("package") + "           "
					+ componentInfo.getClassName());
		} else if (!componentInfo.getPackageName().equals(
				"com.ihateflyingbugs.vocaslide")
				&& isFinish) {
			FlurryAgent.logEvent("BaseActivity:FinishByBackpress");
		}

		Log.e("activitygg", "Base onStop");

		FlurryAgent.onEndSession(this);

		setMySharedPreferences(MainActivitys.GpreFinishTime,
				String.valueOf(System.currentTimeMillis()));
		setMySharedPreferences("firststart", "1");

		Config.know_count = 0;
		Config.unknow_count = 0;

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d("Timer", "onRestart()");
		cTimer = timer(percentView1);
		cTimer.start();
	}

	
	

	public CountDownTimer timer(final PercentView percentView) {

		Log.d("Timer", "timer()");

		mPreference = getSharedPreferences(MainActivitys.preName,
				MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		timer_sec = mPreference.getInt(Config.STUDY_GOAL_TIME, 0);

		timerPreference = getSharedPreferences(MainActivitys.preName,
				MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		use_min = timerPreference.getFloat("use_min", 0);
		use_sec = timerPreference.getFloat("use_sec", 0);

		// time초 동안 time/100초 마다 onTick 실행
		CountDownTimer cTimer = new CountDownTimer(1440000, 1000) {

			// (time * 1000) / 100마다 실행
			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				Log.d("Timer", "onTick");

				// 60초 경과 시마다 1분 증가
				if (use_sec == 60) {
					use_min += 1;
					use_sec = 0;
				}

				// 10이하일 때 두자리 유지
				if (use_sec < 10) {
					use_sec_st = "0" + Integer.toString((int) use_sec++);
				} else {
					use_sec_st = Integer.toString((int) use_sec++);
				}
				if (use_min < 10) {
					use_min_st = "0" + Integer.toString((int) use_min);
				} else {
					use_min_st = Integer.toString((int) use_min);
				}

				tv_using_time_min.setText(use_min_st + "분");
				tv_using_time_sec.setText(use_sec_st);

				float use_time = (use_min * 60000) + use_sec * 1000;

				percentView.setPercentage(
						(use_time / ((timer_sec) * 60000)) * 100, true);

				Log.d("percent", Float
						.toString((use_time / ((timer_sec) * 60000)) * 100));
			}

			// 종료시 실행
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				// percentView.setPercentage(100, true);
				// percentage = 0;
				Log.d("Timer", "onFinidh");
			}
		};

		return cTimer;
	}

	public void pause(CountDownTimer cTimer) {

		Log.d("Timer", "pause()");

		timerEditor = timerPreference.edit();
		timerEditor.putFloat("use_min", use_min);
		timerEditor.commit();
		timerEditor.putFloat("use_sec", use_sec);
		timerEditor.commit();

		cTimer.cancel();
	}


}
