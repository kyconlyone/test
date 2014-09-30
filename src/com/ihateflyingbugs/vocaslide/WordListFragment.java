package com.ihateflyingbugs.vocaslide;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.flurry.sdk.ex;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.internal.LoadingLayout.OnNextClickListener;
import com.ihateflyingbugs.vocaslide.data.Config;
import com.ihateflyingbugs.vocaslide.data.DBPool;
import com.ihateflyingbugs.vocaslide.data.Date;
import com.ihateflyingbugs.vocaslide.data.Mean;
import com.ihateflyingbugs.vocaslide.data.Word;
import com.ihateflyingbugs.vocaslide.login.MainActivitys;
import com.ihateflyingbugs.vocaslide.service.DBService;
import com.ihateflyingbugs.vocaslide.tutorial.MainActivity;
import com.ihateflyingbugs.vocaslide.tutorial.ReviewTutoActivity;


public class WordListFragment extends Fragment implements OnScrollListener {
	public static final int MODE_NORMAL_SCORE_LIST = 0;
	public static final int MODE_NORMAL_KNOWN_LIST = 1;
	public static final int MODE_EXAM_KNOWN_LIST = 2;
	public static final int MODE_EXAM_SCORE_LIST = 3;
	public static int mode;
	private boolean flag_set_swipe_mode;
	private String query;
	static final int ANIMATION_DURATION = 400;
	private DisplayMetrics metrics;
	LogDataFile log_file;
	private DBPool db;
	private ArrayList<Word> words;
	private ListAdapter adapter;
	private ListView listView;
	private int right, wrong, ONCE_WORD_COUNT = Config.ONCE_WORD_COUNT;
	private SensorManager mSensorManager;
	private ShakeEventListener mSensorListener;
	TTS_Util tts_util;
	Thread mcount_thread;
	Thread pcount_thread;
	Runnable mRunnable;
	Runnable pRunnable;

	private SharedPreferences settings;

	Context mContext;


	public static Map<String, String> wordParams = new HashMap<String, String>();
	//public static Map<String, String> wordstatus = new HashMap<String, String>();
	static int Log_count=0;
	public static String studyWord=""; 



	public WordListFragment(){}

	public WordListFragment(int mode)
	{
		this.mode = mode;
		if(mode==MODE_EXAM_SCORE_LIST||mode == MODE_NORMAL_SCORE_LIST){
			flag_set_swipe_mode = true;
		}else{
			flag_set_swipe_mode = false;
		}
	}

	public WordListFragment(int mode, String query)
	{
		this.mode = mode;
		this.query = query;
		if(mode==MODE_EXAM_SCORE_LIST||mode == MODE_NORMAL_SCORE_LIST){
			flag_set_swipe_mode = true;
		}else{
			flag_set_swipe_mode = false;
		}
	}

	private RelativeLayout relativeWord;
	private PullToRefreshListView mPullToRefreshListView;
	private ImageView ivTemp;
	private boolean isListAnimaion = true;
	static Vibrator vibe;
	// 시작은 0 이므로 즉시 실행되고 진동 200 milliseconds, 멈춤 500 milliseconds 된다
	long[] pattern = { 0, 100, 50, 100 };
	private Handler handler;
	List<Word> del_word = new ArrayList<Word>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {





		log_file = new LogDataFile(getActivity().getApplicationContext());

		settings = getActivity().getSharedPreferences(Config.PREFS_NAME,  Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
		mContext= getActivity().getApplicationContext();
		handler = new Handler();
		View view = inflater.inflate(R.layout.fragment_word_list, container, false);
		metrics = new DisplayMetrics();  
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);  
		db = DBPool.getInstance(view.getRootView().getContext());

		vibe = (Vibrator)getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		relativeWord = (RelativeLayout)view.findViewById(R.id.relativeWord);
		listView = (ListView)view.findViewById(R.id.listView);
		Log.e("getword", "uuid = " + Get_my_uuid.get_Device_id(getActivity().getApplicationContext()));
		mPullToRefreshListView = new PullToRefreshListView(getActivity());

		mcount_thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(int i =0; i<10; i++){
					del_count--;
					Log.e("thread_del_m", "minu del_total =    "+del_count+"            ++");
				}
			}
		});

		pcount_thread= new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(int i =0; i<10; i++){
					del_count++;
					Log.e("thread_del_p", "plus del_total =    "+del_count+"            --");
				}

			}
		});




		mPullToRefreshListView.setOnLoadingNextClickListenet(new OnNextClickListener(){

			@Override
			public void onClick() {
				//				Log.d("kjw", "WordListFragment Click!!");

				if (flag_shake) {

				}

				Intent intent = new Intent(getActivity(), DBService.class);
				PendingIntent pintent = PendingIntent.getService(getActivity(), 0, intent, 0);

				try {
					pintent.send();
				} catch (CanceledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*if(studyWord!=""){
					wordParams.put("word", studyWord);
					FlurryAgent.logEvent("다음단어장누르기 &단어들", wordParams);
					MainActivity.writeLog("[다음단어장누르기 &단어들]\r\n"+studyWord+"\r\n");
					studyWord="";

				}*/

				Log.e("getword", "click refresh");
				del_count =0;

				sendWordlistLogInfo();
				db.deleteAllCurrentWord();
				ONCE_WORD_COUNT = Config.ONCE_WORD_COUNT;
				flag_touch = false;
				flag_shake = false;
				checkLevel();
			}
		});


		mPullToRefreshListView.setOnScrollListener(WordListFragment.this);
		mPullToRefreshListView.setLayoutParams(listView.getLayoutParams());
		mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>(){

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
			}
			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				Log.e("ssrefresh", "onPullUpToRefresh");
				mPullToRefreshListView.setShowIndicator(false);
				if(flag_touch){
					flag_touch = false;
					flag_shake = false;
				}
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run() {
						FlurryAgent.logEvent("BaseActivity_WordListFragment:PulltoRefresh");
						mPullToRefreshListView.onRefreshComplete(right, wrong, ONCE_WORD_COUNT);
					}
				}, 500);
			}
		});
		//		mPullToRefreshListView.setMode(mPullToRefreshListView.getMode() == Mode.BOTH ? Mode.PULL_FROM_START: Mode.BOTH);


		if(mode == MODE_NORMAL_SCORE_LIST || mode == MODE_EXAM_SCORE_LIST)
			mPullToRefreshListView.setMode(Mode.PULL_FROM_END);
		else
			mPullToRefreshListView.setMode(Mode.DISABLED);




		Log.d("kjw", "create refresh !!!!!!!");
		refresh();
		ivTemp = new ImageView(getActivity());
		ivTemp.setBackgroundResource(R.drawable.empty);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		ivTemp.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		relativeWord.addView(mPullToRefreshListView);
		relativeWord.addView(ivTemp, params);
		ivTemp.setVisibility(View.GONE);
		mSensorManager = (SensorManager) getActivity().getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
		mSensorListener = new ShakeEventListener();   
		mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
			public void onShake() {
				if(!words.isEmpty()&&adapter!=null&&!flag_shake){
					//어느 순간에 취소하고 싶다면 아래 코드 cancel() 함수를 실행하면 된다.  
					vibe.cancel();
					Collections.shuffle(words);
					listView.setSelection(0);
					adapter.notifyDataSetChanged();
					isListAnimaion = true;
					//repeat은 -1 무반복
					vibe.vibrate(pattern, -1);
				}else{
					Toast.makeText(getActivity(), "준비 안됨", Toast.LENGTH_SHORT).show();
				}
			}
		});
		tts_util = new TTS_Util(getActivity().getApplicationContext());
		if(tts_util.tts_check()){

		}

		listView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				return (event.getAction() == MotionEvent.ACTION_MOVE);

			}
		});

		return view;
	}
	int start;
	int last;
	int level;
	boolean level_check;

	private void refresh()
	{

		//		userDb.deleteAllCurrentWord();
		if(mode==MODE_EXAM_SCORE_LIST||mode == MODE_NORMAL_SCORE_LIST){
			flag_set_swipe_mode = true;
		}else{
			flag_set_swipe_mode = false;
		}
		if(isindi){
			mPullToRefreshListView.setShowIndicator(false);
		}
		if(mode == MODE_NORMAL_SCORE_LIST)
		{

			words = db.getCurrentWords();
			if(words.size() > 0)
			{
				boolean flag = false;
				for(int i=0; i<words.size();i++){
					if(words.get(i).getExState()>0){
						flag = true;
						String value = settings.getString(MainActivitys.GpreReviewTutorial, "0");
						if(value.equals("0")){
							startActivity(new Intent(getActivity(), ReviewTutoActivity.class));
						}
						break;
					}
				}

				MainActivity.setActionBar(flag);

				printList();

				right = db.getRightCount();
				wrong = db.getWrongCount();
				ONCE_WORD_COUNT = Config.ONCE_WORD_COUNT - right -wrong;
				return;
			}
			else
			{
				Log.e("leveltest", "refresh 토픽     :" + Config.WORD_TOPIC+"    refresh 레벨    : "+Config.Difficulty +"     refresh MAX      :"+Config.MAX_DIFFICULTY);

				right = wrong = 0;
				mPullToRefreshListView.setBackgroundResource(0);
				Log.w("getword", "count      = " + String.valueOf(words.size()));

				words = db.wordsWithScore();



				Log.w("getword", "wordsWithScore      count      = " + String.valueOf(words.size()));
				words.addAll(db.wordsWithUnknown());
				Log.w("getword", "wordsWithScore + wordsWithUnknown count = " + String.valueOf(words.size()));
				level = Config.Difficulty;
				int wordsCount = words.size();
				int restCount = Config.ONCE_WORD_COUNT - wordsCount;
				LinkedHashSet<Word> hs = new LinkedHashSet<Word>();
				hs.addAll(words);

				words.clear();
				words.addAll(hs);
				Log.d("getword", "level = " + level + "  wordsCount = " + wordsCount + "  restCount = " + restCount);
				// 가져온 단어가 30개가 안됐었을때
				if(restCount > 0)
				{
					//				if(level == 1)
					//				{
					// '13.12.23
					// 
					//					ArrayList<Word> temp = db.wordsWithLevel(level, restCount);
					//					words.addAll(temp);
					//				}
					//				else
					//				{
					Log.w("getword", "wordsWithScore      rest count =   " + String.valueOf(restCount));

					int p20Count = restCount * 1 / 5;
					int p80Count = restCount - p20Count;
					Log.w("getword", "wordsWithScore      p20Count   =   " + String.valueOf(restCount));
					Log.w("getword", "wordsWithScore      p80Count   =   " + String.valueOf(restCount));
					if(level == 1)
					{
						p20Count = 0;
						p80Count = restCount;
						Log.w("kjw", "wordsWithScore      p80Count   =   " + String.valueOf(p80Count)+"   is level 1" );
					}

					ArrayList<Word> temp = db.wordsWithLevel(level - 1, p20Count);
					int tempCount = temp.size();
					words.addAll(temp);
					Log.w("getword", "wordsWithScore      tempCount   =   " + String.valueOf(tempCount));
					temp.clear();
					p80Count = p80Count + (p20Count - tempCount);
					Log.w("getword", "wordsWithScore      p80Count   =   " + String.valueOf(p80Count)+"   is re calcul first");

					//여기서 레벨 을 체크하면된다 기획이 조금 필요하긴하지용~ㅎㅎㅎ


					do
					{
						temp = db.wordsWithLevel(level, p80Count);
						tempCount = temp.size();
						words.addAll(temp);
						p80Count = p80Count - tempCount;
						Log.w("getword", "wordsWithScore      p80Count - tempCount    =   " + String.valueOf(p80Count));

						if(p80Count > 0) 
						{
							if(level<Config.MAX_DIFFICULTY){
								Log.e("leveltest", "현재 토픽     :" + Config.WORD_TOPIC+"    현재 레벨    : "+level +"     현재 MAX      :"+Config.MAX_DIFFICULTY);

								new AlertDialog.Builder(getActivity())
								.setMessage("해당 레벨의 단어를 모두 외우셨습니다. 다음 레벨로 넘어감니다")
								.setPositiveButton("확인", null).show();

								db.insertTrueCount(level);
								level++;
								Config.Difficulty = level;
								settings.edit().putString(MainActivitys.GpreLevel, ""+Config.Difficulty).commit();								
							}else{
								break;
							}
							Log.w("getword", "wordsWithScore      level    =   " + String.valueOf(level)+"   is level up!!!!");
						}
						else
							break;
					}while(p80Count > 0);

					//				}
				}

				Log.d("getword", "words = " + words.size());
				while(words.size() > Config.ONCE_WORD_COUNT)
				{
					Log.w("getword", "wordsWithScore      total 30?   =   " + String.valueOf(words.size()));
					Log.w("getword", words.get(words.size() - 1).getWord()+ "  " +
							String.valueOf(words.get(words.size() - 1).getScore())+ "  "+
							String.valueOf(words.get(words.size() - 1).getState())+ "  ");
					words.remove(words.size() - 1);
				}
				//			words = db.wordsWithScore();
				int i =1;

				for(Word word : words)
				{
					db.insertCurrentWord(word, i);
					words.get(i-1).setExState(words.get(i-1).getState());
					i++;
				}
				SideActivity.set_tvLevel();
			}
		}
		else if(mode == MODE_NORMAL_KNOWN_LIST)
		{
			mPullToRefreshListView.setBackgroundResource(R.drawable.list_known_bg);
			words = db.wordsWithKnown();
		}
		else if(mode == MODE_EXAM_SCORE_LIST)
		{
			mPullToRefreshListView.setBackgroundResource(0);
			words = db.pwordsWithQuery(query); 
		}
		else if(mode == MODE_EXAM_KNOWN_LIST)
		{
			mPullToRefreshListView.setBackgroundResource(R.drawable.list_known_bg);
			words = db.pwordsWithKnownAndQuery(query);
		}
		printList();
	}
	private void printList()
	{

		if(words.size()==0&&mode==MODE_NORMAL_SCORE_LIST){
			String show_text = null;
			String topic = settings.getString(MainActivitys.GpreTopic, "1");
			if(topic.equals("1")){
				show_text = "축하합니다! 밀당영단어의 모든 수능 단어를 마스텨 하셨습니다.";
			}else if(topic.equals("2")){
				show_text = "축하합니다! 밀당영단어의 모든 토익 단어를 마스텨 하셨습니다.";
			}else if(topic.equals("3")){
				show_text = "축하합니다! 밀당영단어의 모든 토플 단어를 마스텨 하셨습니다.";
			}

			//트루 카운트를 임의로 10개를 넣는다 !!
			new AlertDialog.Builder(getActivity())
			.setMessage(show_text)
			.setPositiveButton("확인", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
					fragmentTransaction.replace(R.id.linearFragment, new FinishStudyFragment()).addToBackStack(null).commit();

				}
			}).show();
		}

		ONCE_WORD_COUNT = Config.ONCE_WORD_COUNT - right - wrong ;
		isListAnimaion =true;
		adapter = new ListAdapter(getActivity().getApplicationContext(), R.layout.word_list_row, words);
		mPullToRefreshListView.setAdapter(adapter);

	}






	private void setViewHolder(View view) {

		final int NORMAL_KNOWN_COLOR = Color.rgb(0, 181, 105);
		final int EXAM_KNOWN_COLOR = Color.rgb(0xee, 0xc0, 0x00);
		ViewHolder vh = new ViewHolder();
		vh.linearForward = (LinearLayout)view.findViewById(R.id.linearForward);
		vh.linearKnown = (LinearLayout)view.findViewById(R.id.linearKnown);
		vh.linearUnknown = (LinearLayout)view.findViewById(R.id.linearUnknown);


		vh.ll_known_first_mean = (LinearLayout)view.findViewById(R.id.ll_known_first_mean);
		vh.ll_known_second_mean = (LinearLayout)view.findViewById(R.id.ll_known_second_mean);
		vh.ll_known_third_mean = (LinearLayout)view.findViewById(R.id.ll_known_third_mean);
		vh.ll_known_forth_mean = (LinearLayout)view.findViewById(R.id.ll_known_forth_mean);

		vh.ll_first_mean = (LinearLayout)view.findViewById(R.id.ll_first_mean);
		vh.ll_second_mean = (LinearLayout)view.findViewById(R.id.ll_second_mean);
		vh.ll_third_mean = (LinearLayout)view.findViewById(R.id.ll_third_mean);
		vh.ll_forth_mean = (LinearLayout)view.findViewById(R.id.ll_forth_mean);


		vh.ivKnown = (ImageView)view.findViewById(R.id.ivKnown);


		vh.linearForward.setVisibility(View.VISIBLE);
		vh.tvForward =(TextView)view.findViewById(R.id.tvForward);
		vh.tvKnownWord =(TextView)view.findViewById(R.id.tvKnownWord);
		vh.tvUnknownWord =(TextView)view.findViewById(R.id.tvUnknownWord);
		vh.tvUnknownCount =(ImageView)view.findViewById(R.id.tvUnknownCount);

		vh.tv_known_first_mean_title=(TextView)view.findViewById(R.id.tv_known_first_mean_title);
		vh.tv_known_second_mean_title=(TextView)view.findViewById(R.id.tv_known_second_mean_title);
		vh.tv_known_third_mean_title=(TextView)view.findViewById(R.id.tv_known_third_mean_title);
		vh.tv_known_forth_mean_title=(TextView)view.findViewById(R.id.tv_known_forth_mean_title);

		vh.tv_known_first_mean=(TextView)view.findViewById(R.id.tv_known_first_mean);
		vh.tv_known_second_mean=(TextView)view.findViewById(R.id.tv_known_second_mean);
		vh.tv_known_third_mean=(TextView)view.findViewById(R.id.tv_known_third_mean);
		vh.tv_known_forth_mean=(TextView)view.findViewById(R.id.tv_known_forth_mean);

		vh.tv_first_mean_title=(TextView)view.findViewById(R.id.tv_first_mean_title);
		vh.tv_second_mean_title=(TextView)view.findViewById(R.id.tv_second_mean_title);
		vh.tv_third_mean_title=(TextView)view.findViewById(R.id.tv_third_mean_title);
		vh.tv_forth_mean_title=(TextView)view.findViewById(R.id.tv_forth_mean_title);

		vh.tv_first_mean=(TextView)view.findViewById(R.id.tv_first_mean);
		vh.tv_second_mean=(TextView)view.findViewById(R.id.tv_second_mean);
		vh.tv_third_mean=(TextView)view.findViewById(R.id.tv_third_mean);
		vh.tv_forth_mean=(TextView)view.findViewById(R.id.tv_forth_mean);

		vh.iv_back_del=(ImageView)view.findViewById(R.id.iv_back_del);

		//		vh.tvForward.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "font/KoPubBatangMedium.ttf"));
		//		vh.tv_known_forth_mean.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "font/KoPubBatangMedium.ttf"));
		//		vh.tv_first_mean.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "font/KoPubBatangMedium.ttf"));
		//		vh.tv_known_first_mean.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "font/KoPubBatangMedium.ttf"));
		//		vh.tv_second_mean.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "font/KoPubBatangMedium.ttf"));
		//		vh.tv_known_second_mean.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "font/KoPubBatangMedium.ttf"));
		//		vh.tv_third_mean.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "font/KoPubBatangMedium.ttf"));
		//		vh.tv_known_third_mean.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "font/KoPubBatangMedium.ttf"));
		//		vh.tv_forth_mean.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "font/KoPubBatangMedium.ttf"));

		if(mode == MODE_NORMAL_SCORE_LIST || mode == MODE_NORMAL_KNOWN_LIST)
			vh.linearKnown.setBackgroundColor(NORMAL_KNOWN_COLOR);
		else
			vh.linearKnown.setBackgroundColor(EXAM_KNOWN_COLOR);
		vh.needInflate = false;
		view.setTag(vh);
	}
	Integer del_count=0;
	boolean flag_touch = false;
	boolean flag_shake = false;
	boolean flag_scroll = true;

	FirstHangle hangle = new FirstHangle();

	private class ListAdapter extends ArrayAdapter<Word>{
		LayoutInflater vi;
		private ArrayList<Word> items;
		private boolean isWrongContinueShow;
		List<Timer> timer_list;
		Context mContext;

		public ListAdapter(Context context, int resourceId, ArrayList<Word> items){
			super(context, resourceId, items);
			this.items = items;
			timer_list = new ArrayList<Timer>();


			this.vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if(mode == MODE_NORMAL_SCORE_LIST || mode == MODE_EXAM_SCORE_LIST)
				isWrongContinueShow = true;
			else
				isWrongContinueShow = false;
			mContext= context;
		}
		//		@Override
		//		public boolean isEnabled(int position)
		//		{
		//			return false;
		//		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			final ViewHolder vh;
			final View view;
			if (convertView==null) {
				view = vi.inflate(R.layout.word_list_row, parent, false);
				setViewHolder(view);
			}
			else if (((ViewHolder)convertView.getTag()).needInflate) {
				view = vi.inflate(R.layout.word_list_row, parent, false);
				setViewHolder(view);
			}
			else {
				view = convertView;
			}
			final Word word = items.get(position);
			vh = (ViewHolder)view.getTag();
			vh.tvForward.setText(word.getWord());
			vh.tvKnownWord.setText(word.getWord());
			//vh.tvKnownMeaning.setText(word.getMeaning());
			vh.tvUnknownWord.setText(word.getWord());


			if(mode == MODE_NORMAL_KNOWN_LIST || mode == MODE_EXAM_KNOWN_LIST)
			{
				vh.ivKnown.setVisibility(View.VISIBLE);
			}
			else
			{
				vh.ivKnown.setVisibility(View.GONE);
			}


			vh.linearForward.setVisibility(View.VISIBLE);
			if(word.getState()>0 && isWrongContinueShow)
			{

				vh.tvUnknownCount.setBackgroundResource(R.drawable.main_cell_know);
				vh.tvUnknownCount.setVisibility(View.VISIBLE);

			}else if(word.getState()<0 && isWrongContinueShow){
				vh.tvUnknownCount.setBackgroundResource(R.drawable.main_cell_forget);
				vh.tvUnknownCount.setVisibility(View.VISIBLE);
			}
			else
			{
				vh.linearForward.setBackgroundColor(Color.WHITE);
				vh.tvUnknownCount.setVisibility(View.INVISIBLE);
			}

			vh.linearForward.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.zoom_out);
					vh.tvForward.startAnimation(animation);
					AudioManager audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
					switch(audioManager.getRingerMode()){
					case AudioManager.RINGER_MODE_VIBRATE:
						// 진동
						Toast.makeText(getActivity().getApplicationContext(), "소리 모드로 전화후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
						break;
					case AudioManager.RINGER_MODE_NORMAL:
						// 소리
						if(tts_util.tts_check()){
							tts_util.tts_reading(vh.tvForward.getText().toString());
						}else{
							Toast.makeText(getActivity().getApplicationContext(), "잠시후에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
						}
						break;
					case AudioManager.RINGER_MODE_SILENT:
						// 무음
						Toast.makeText(getActivity().getApplicationContext(), "소리 모드로 전화후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
						break;
					}
					//vh.iv_wc.setVisibility(View.INVISIBLE);
				}
			});
			vh.linearUnknown.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					vh.linearForward.setVisibility(View.VISIBLE);
					Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_left);
					anim.setDuration(150);
					vh.linearForward.startAnimation(anim);

				}
			});

			vh.iv_back_del.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					vh.iv_back_del.invalidate();
					vh.iv_back_del.clearAnimation();
					word.timer.cancel();
					flag_scroll = true;
					mPullToRefreshListView.setScrollContainer(flag_scroll);
					synchronized (del_count) {
						if(flag_set_swipe_mode){
							if(del_count>0){
								mcount_thread.run();
							}else{
								flag_shake = false;
							}
							word.timer = new Timer();
						}
					}

					Log.e("click_k", "취소되었습니다.");

					vh.linearForward.setVisibility(View.VISIBLE);
					Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_right);
					anim.setDuration(150);
					vh.linearForward.startAnimation(anim);

					int ex_state = items.get(position).getState();
					items.get(position).increaseWrongCount();

					boolean isKnown= false;
					if(!word.isWrong())
					{
						wrong++;
						ONCE_WORD_COUNT--;
						word.setWrong(true);
						word.setRight(false);
						db.updateRightWrong(false, word.get_id());
					}
					db.updateWordInfo(items.get(position), isKnown);
					db.insertLevel(items.get(position), isKnown);


					//Config.Difficulty = db.calcLevel(Config.CHANGE_LEVEL_COUNT);

					SideActivity.set_tvLevel();
					Word word_for_write= db.getWord(items.get(position).get_id());
					word.setState(word_for_write.getState());
					String data = word_for_write.getWord()+"@"+String.valueOf(word_for_write.getScore())+"@"+String.valueOf(ex_state)+"@"
							+String.valueOf(word_for_write.getState())+"@"+get_date()+"\r\n";
					log_file.input_LogData_in_file(data);
					SideActivity.set_tvLevel();
					if(word.getWrongCount()!=0&& isWrongContinueShow){
						//vh.tvUnknownCount.setText(items.get(position).getWrongCount() + "");

						vh.tvUnknownCount.setVisibility(View.VISIBLE);
						vh.tvUnknownCount.setBackgroundResource(R.drawable.main_cell_forget);

					}
					mPullToRefreshListView.setWordCount(right, wrong, ONCE_WORD_COUNT);
					Toast.makeText(mContext, "취소되었습니다.", Toast.LENGTH_SHORT).show();
				}
			});




			vh.linearForward.setOnTouchListener(new SwipeDismissTouchListener(
					vh.linearForward, vh.linearUnknown, vh.linearKnown, vh.iv_wc , flag_set_swipe_mode, 
					null,
					new SwipeDismissTouchListener.DismissCallbacks() {
						@Override
						public boolean canDismiss(Object token) {
							if(flag_touch){
								return false;
							}

							vh.iv_back_del.setVisibility(View.VISIBLE);

							if(word.getMeanList().size()!=0){

								int count = word.getMeanList().size();

								Log.d("count", ""+count);
								vh.ll_first_mean.setVisibility(View.GONE);
								vh.ll_second_mean.setVisibility(View.GONE);
								vh.ll_third_mean.setVisibility(View.GONE);
								vh.ll_forth_mean.setVisibility(View.GONE);

								vh.ll_known_first_mean.setVisibility(View.GONE);
								vh.ll_known_second_mean.setVisibility(View.GONE);
								vh.ll_known_third_mean.setVisibility(View.GONE);
								vh.ll_known_forth_mean.setVisibility(View.GONE);

								vh.tv_first_mean.setText("");

								vh.tv_known_first_mean.setText("");
								vh.tv_second_mean.setText("");
								vh.tv_known_second_mean.setText("");
								vh.tv_third_mean.setText("");
								vh.tv_known_third_mean.setText("");
								vh.tv_forth_mean.setText("");
								vh.tv_known_forth_mean.setText("");

								vh.tv_first_mean_title.setText("");
								vh.tv_known_first_mean_title.setText("");
								vh.tv_second_mean_title.setText("");
								vh.tv_known_second_mean_title.setText("");
								vh.tv_third_mean_title.setText("");
								vh.tv_known_third_mean_title.setText("");
								vh.tv_forth_mean_title.setText("");
								vh.tv_known_forth_mean_title.setText("");

								Mean mean ;

								String M_N="";
								String M_V="";
								String M_A="";
								String M_AD="";
								String M_PREP="";
								Log.d("test_class", "1");
								for(int i=0; i <word.getMeanList().size(); i++){
									mean = word.getMean(i);
									int key = mean.getMClass();
									switch (key) {
									case Word.Class_N:
										M_N += mean.getMeaning()+", ";
										//										vh.ll_first_mean.setVisibility(View.VISIBLE);
										//										vh.tv_first_mean.setText(vh.tv_first_mean.getText()+word.getMean(i).getMeaning()+", ");
										//										vh.ll_known_first_mean.setVisibility(View.VISIBLE);
										//										vh.tv_known_first_mean.setText(vh.tv_known_first_mean.getText()+word.getMean(i).getMeaning()+", ");
										break;
									case Word.Class_V:
										M_V += mean.getMeaning()+", ";
										//										vh.ll_second_mean.setVisibility(View.VISIBLE);
										//										vh.tv_second_mean.setText(vh.tv_second_mean.getText()+word.getMean(i).getMeaning()+", ");
										//										vh.ll_known_second_mean.setVisibility(View.VISIBLE);
										//										vh.tv_known_second_mean.setText(vh.tv_known_second_mean.getText()+word.getMean(i).getMeaning()+", ");
										break;
									case Word.Class_A:
										M_A += mean.getMeaning()+", ";
										//										vh.ll_third_mean.setVisibility(View.VISIBLE);
										//										vh.tv_third_mean.setText(vh.tv_third_mean.getText()+word.getMean(i).getMeaning()+", ");
										//										vh.ll_known_third_mean.setVisibility(View.VISIBLE);
										//										vh.tv_known_third_mean.setText(vh.tv_known_third_mean.getText()+word.getMean(i).getMeaning()+", ");
										break;
									case Word.Class_Ad:
										M_AD += mean.getMeaning()+", ";
										//										vh.ll_third_mean.setVisibility(View.VISIBLE);
										//										vh.tv_third_mean.setText(vh.tv_third_mean.getText()+word.getMean(i).getMeaning()+", ");
										//										vh.ll_known_third_mean.setVisibility(View.VISIBLE);
										//										vh.tv_known_third_mean.setText(vh.tv_known_third_mean.getText()+word.getMean(i).getMeaning()+", ");
										break;
									case Word.Class_Prep:
										M_PREP += mean.getMeaning()+", ";
										//										vh.ll_forth_mean.setVisibility(View.VISIBLE);
										//										vh.tv_forth_mean.setText(vh.tv_forth_mean.getText()+word.getMean(i).getMeaning()+", ");
										//										vh.ll_known_forth_mean.setVisibility(View.VISIBLE);
										//										vh.tv_known_forth_mean.setText(vh.tv_known_forth_mean.getText()+word.getMean(i).getMeaning()+", ");
										break;
									default:
										break;
									}
								}
								Log.d("test_class", "2");
								Log.d("test_class", "2  "+word.getP_class());

								switch (word.getP_class()) {
								case Word.Class_N:
									vh.ll_first_mean.setVisibility(View.VISIBLE);
									vh.tv_first_mean_title.setText("n");
									vh.tv_first_mean.setText(M_N.substring(0, M_N.length()-2));
									vh.ll_known_first_mean.setVisibility(View.VISIBLE);
									vh.tv_known_first_mean_title.setText("n");
									vh.tv_known_first_mean.setText(M_N.substring(0, M_N.length()-2));
									Log.d("test_class", "22222222222222");
									break;
								case Word.Class_V:
									vh.ll_first_mean.setVisibility(View.VISIBLE);
									vh.tv_first_mean_title.setText("v");
									vh.tv_first_mean.setText(M_V.substring(0, M_V.length()-2));
									vh.ll_known_first_mean.setVisibility(View.VISIBLE);
									vh.tv_known_first_mean_title.setText("v");
									vh.tv_known_first_mean.setText(M_V.substring(0, M_V.length()-2));
									break;
								case Word.Class_A:
									vh.ll_first_mean.setVisibility(View.VISIBLE);
									vh.tv_first_mean_title.setText("a");
									vh.tv_first_mean.setText(M_A.substring(0, M_A.length()-2));
									vh.ll_known_first_mean.setVisibility(View.VISIBLE);
									vh.tv_known_first_mean_title.setText("a");
									vh.tv_known_first_mean.setText(M_A.substring(0, M_A.length()-2));
									break;
								case Word.Class_Ad:
									vh.ll_first_mean.setVisibility(View.VISIBLE);
									vh.tv_first_mean_title.setText("ad");
									vh.tv_first_mean.setText(M_AD.substring(0, M_AD.length()-2));
									vh.ll_known_first_mean.setVisibility(View.VISIBLE);
									vh.tv_known_first_mean_title.setText("ad");
									vh.tv_known_first_mean.setText(M_AD.substring(0, M_AD.length()-2));
									break;
								case Word.Class_Prep:
									vh.ll_first_mean.setVisibility(View.VISIBLE);
									vh.tv_first_mean_title.setText("prep");
									vh.tv_first_mean.setText(M_PREP.substring(0, M_PREP.length()-2));
									vh.ll_known_first_mean.setVisibility(View.VISIBLE);
									vh.tv_known_first_mean_title.setText("prep");
									vh.tv_known_first_mean.setText(M_PREP.substring(0, M_PREP.length()-2));
									break;
								}
								Log.d("test_class", "3");

								HashMap<Integer, Boolean> hm = word.getmClassList();
								hm.remove(word.getP_class());
								Set<Integer> st = hm.keySet();
								Iterator<Integer> it= st.iterator();
								int line = 0;

								while(it.hasNext()){
									int key = it.next();
									String multi_mean = "";
									String mclass = "";
									switch(key){
									case Word.Class_A:
										mclass = "a";
										multi_mean = M_A;
										break;
									case Word.Class_Ad:
										mclass = "ad";
										multi_mean = M_AD;
										break;
									case Word.Class_N:
										mclass = "n";
										multi_mean = M_N;
										break;
									case Word.Class_V:
										mclass = "v";
										multi_mean = M_V;
										break;
									case Word.Class_Prep:
										mclass = "prep";
										multi_mean = M_PREP;
										break;
									}

									if(line ==0){
										vh.ll_second_mean.setVisibility(View.VISIBLE);
										vh.tv_second_mean_title.setText(mclass);
										vh.tv_second_mean.setText(multi_mean.substring(0,multi_mean.length()-2));
										vh.ll_known_second_mean.setVisibility(View.VISIBLE);
										vh.tv_known_second_mean_title.setText(mclass);
										vh.tv_known_second_mean.setText(multi_mean.substring(0,multi_mean.length()-2));
										mclass = "";
										multi_mean = "";
										line++;
									}else if(line ==1){
										vh.ll_third_mean.setVisibility(View.VISIBLE);
										vh.tv_third_mean_title.setText(mclass);
										vh.tv_third_mean.setText(multi_mean.substring(0,multi_mean.length()-2));
										vh.ll_known_third_mean.setVisibility(View.VISIBLE);
										vh.tv_known_third_mean_title.setText(mclass);
										vh.tv_known_third_mean.setText(multi_mean.substring(0,multi_mean.length()-2));
										mclass = "";
										multi_mean = "";
									}
								}

								Log.d("test_class", "4");

								int text_sp = 0;

								if(word.getmClassList().size()>1){
									text_sp = 15;
								}else if(word.getmClassList().size()==1){
									if(vh.tv_first_mean.length()>15||vh.tv_second_mean.length()>15){
										//포문으로 글씨를 계속 줄여본다 
										text_sp = 15;
									}else{
										text_sp = 17;
									}
								}else{
									text_sp = 17;
								}

								vh.tv_first_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_second_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_third_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_forth_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);

								vh.tv_first_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_second_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_third_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_forth_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);

								vh.tv_known_first_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_known_second_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_known_third_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_known_forth_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);

								vh.tv_known_first_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_known_second_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_known_third_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_known_forth_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);

								if(vh.tv_first_mean_title.getText().toString().equals("prep")
										||vh.tv_first_mean_title.getText().toString().equals("conj")){
									vh.tv_first_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
									vh.tv_known_first_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
								}else if(vh.tv_second_mean_title.getText().toString().equals("prep")
										||vh.tv_second_mean_title.getText().toString().equals("conj")){
									vh.tv_second_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
									vh.tv_known_second_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
								}else if(vh.tv_third_mean_title.getText().toString().equals("prep")
										||vh.tv_third_mean_title.getText().toString().equals("conj")){
									vh.tv_third_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
									vh.tv_known_third_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
								}

								Log.d("test_class", "5");
							}else{
								vh.tv_known_first_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
								vh.tv_known_first_mean.setText("없음");
								vh.ll_known_second_mean.setVisibility(View.GONE);
								vh.ll_known_third_mean.setVisibility(View.GONE);
								vh.ll_known_forth_mean.setVisibility(View.GONE);	

								vh.tv_first_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
								vh.tv_first_mean.setText("없음");
								vh.ll_second_mean.setVisibility(View.GONE);
								vh.ll_third_mean.setVisibility(View.GONE);
								vh.ll_forth_mean.setVisibility(View.GONE);	
							}

							Log.d("test_class", "6");
							flag_touch = true;
							return true;
						}


						@Override
						public void onLeftDismiss(View v, Object token,
								boolean flag) {
							// TODO Auto-generated method stub
							flag_touch = false;
							final int ex_state = items.get(position).getState();
							boolean isKnown =false;
							vh.linearForward.setVisibility(View.GONE);

							if(!isWrongContinueShow)
							{
								synchronized (del_count) {

									pcount_thread.run();

								}
								word.timer.schedule(new TimerTask() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										handler.post(new Runnable() {

											@Override
											public void run() {
												// TODO Auto-generated method stub
												if(del_count>0){
													synchronized (del_count) {

														mcount_thread.run();
													}
												}

												deleteCell(view, word, ex_state);
												//timer_list.clear();
											}
										});
									}
								}, 1500);
								//									words.remove(position);
							}

							word.increaseWrongCount();
							Log.e("swipe", "before   Right : "+String.valueOf(right)+"    wrong : "+String.valueOf(wrong)+"None : "+String.valueOf(ONCE_WORD_COUNT));
							Log.e("swipe", "before   isWrong : "+String.valueOf(word.isWrong()) + "isRight : "+String.valueOf(word.isRight()));
							if(!word.isWrong())
							{
								wrong++;
								//ONCE_WORD_COUNT--;
								word.setWrong(true);
								word.setRight(false);
								db.updateRightWrong(false, word.get_id());
							}

							Log.e("swipe", "after    Right : "+String.valueOf(right)+"    wrong : "+String.valueOf(wrong)+"None : "+String.valueOf(ONCE_WORD_COUNT));

							db.updateWordInfo(word, isKnown);
							db.insertLevel(word, isKnown);



							//Config.Difficulty = db.calcLevel(Config.CHANGE_LEVEL_COUNT);
							Word word_for_write= db.getWord(items.get(position).get_id());


							word.setState(word_for_write.getState());
							String data = word_for_write.getWord()+"@"+String.valueOf(word_for_write.getScore())+"@"+String.valueOf(ex_state)+"@"
									+String.valueOf(word_for_write.getState())+"@"+get_date()+"\r\n";
							log_file.input_LogData_in_file(data);
							SideActivity.set_tvLevel();
							mPullToRefreshListView.setWordCount(right, wrong, ONCE_WORD_COUNT);

							if(word.getWrongCount()!=0&& isWrongContinueShow){
								//vh.tvUnknownCount.setText(items.get(position).getWrongCount() + "");

								vh.tvUnknownCount.setVisibility(View.VISIBLE);
								vh.tvUnknownCount.setBackgroundResource(R.drawable.main_cell_forget);

							}
						}


						@Override
						public void onRightDismiss(final View v, Object token,
								boolean flag) {
							// TODO Auto-generated method stub
							Config.know_count++;

							flag_touch = false;
							ivTemp.setVisibility(View.VISIBLE);
							vh.linearForward.setVisibility(View.GONE);
							flag_scroll = false;
							mPullToRefreshListView.setScrollContainer(flag_scroll);

							final View ex_View = view;


							//word.setTimerTask(view,position);
							synchronized (del_count) {
								if(flag_set_swipe_mode){
									pcount_thread.run();
									flag_shake = true;
								}
							}
							Animation anim = new AnimationUtils().loadAnimation(mContext,android.R.anim.fade_out);
							anim.setDuration(1500);
							anim.setAnimationListener(new AnimationListener() {

								@Override
								public void onAnimationStart(Animation animation) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onAnimationRepeat(Animation animation) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onAnimationEnd(Animation animation) {
									// TODO Auto-generated method stub
									vh.iv_back_del.setVisibility(View.GONE);
								}
							});
							vh.iv_back_del.startAnimation(anim);



							word.timer.schedule(new TimerTask() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									handler.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub

											//int ex_state = items.get(position).getState();

											boolean isKnown= true;
											int ex_state;
											try {
												ex_state = word.getState();

												//v.setOnTouchListener(null)

												//								words.remove(position);
												//								vh.linearForward.setVisibility(View.GONE);
												//								vh.linearUnknown.setVisibility(View.GONE);
												//								vh.linearKnown.setVisibility(View.GONE);
												//'13.12.24 - 아는단어로 셀삭제시 셀 깜빡거리는거 없앰
												//							mPullToRefreshListView.setClickable(false);

												//							vh.linearForward.setEnabled(false);
												vh.iv_back_del.setClickable(false);



												Log.e("swipe", "before   Right : "+String.valueOf(right)+"    wrong : "+String.valueOf(wrong)+"None : "+String.valueOf(ONCE_WORD_COUNT));
												Log.e("swipe", "before   isWrong : "+String.valueOf(word.isWrong()) + "isRight : "+String.valueOf(word.isRight()));

												if(!word.isRight())
												{

													right++;
													ONCE_WORD_COUNT--;
													word.setRight(true);
													word.setWrong(false);
													//								userDb.deleteCurrentWord(word.get_id());

													db.updateRightWrong(true, word.get_id());
												}
												Log.e("swipe", "after   Right : "+String.valueOf(right)+"    wrong : "+String.valueOf(wrong)+"None : "+String.valueOf(ONCE_WORD_COUNT));

												db.updateWordInfo(word, isKnown);

												db.insertLevel(word, isKnown);

												//db.calcLevel(Config.CHANGE_LEVEL_COUNT, getActivity());
												//int after_level = db.calcLevel(Config.CHANGE_LEVEL_COUNT);

												Word word_for_write= db.getWord(word.get_id());
												word.setState(word_for_write.getState());
												String data = word_for_write.getWord()+"@"+String.valueOf(word_for_write.getScore())+"@"+String.valueOf(word.getExState())+"@"
														+String.valueOf(word_for_write.getState())+"@"+System.currentTimeMillis()/1000+"\r\n";
												log_file.input_LogData_in_file(data);
												SideActivity.set_tvLevel();
												Log.e("asdf", ""+word.getWord());
												//wordParams.put(""+Log_count++, "right@"+word_for_write.getWord()+"@"+String.valueOf(word_for_write.getScore())+"@"+String.valueOf(word.getExState())+"@"
												//		+String.valueOf(word_for_write.getState())+"@"+System.currentTimeMillis()/1000);
												studyWord += ""+(Log_count++)+"/right/"+word_for_write.getWord()+"/"+String.valueOf(word_for_write.getScore())+"/"+String.valueOf(word.getExState())+"/"
														+String.valueOf(word_for_write.getState())+"/"+System.currentTimeMillis()/1000+"\r\n";
												//												wordParams.put("word", word_for_write.getWord());
												//												wordParams.put("score", String.valueOf(word_for_write.getScore()));
												//												wordParams.put("exState", String.valueOf(word.getExState()));
												//												wordParams.put("state", String.valueOf(word_for_write.getState()));
												//												wordParams.put("date",String.valueOf(System.currentTimeMillis()));
												//												Log.e("wordParams","word:"+wordParams.get("word")+"/score:"+wordParams.get("score")+"/exStae:"+wordParams.get("exState")+
												//														"/state:"+wordParams.get("state")+"/date"+wordParams.get("date"));
												//												FlurryAgent.logEvent("안다,WordListFragment_onRightDismiss,0",wordParams,true);
												/*
												 * 여기서 카운트 올리면 될것같음 ex코드가 -1일떄 0 일떄 0보다 클떄
												 */

												int count = 0;


												Log.e("view_check", v.toString()+"         "+view.toString());

												Log.e("view_check", "equal");

												Log.e("getcount", String.valueOf(start)+ "   "+String.valueOf(last) + "  "+String.valueOf(position));
												if(start<= position&&start+last>=position){
													deleteCell(view, word, ex_state);
													Log.e("view_check", "해당뷰가 보이고있음");
												}else{
													mcount_thread.run();
													adapter.remove(word);
													Log.e("view_check", "단어는 "+word.getWord());
													Log.e("view_check", "해당뷰가 보이지 않음");
												}

											} catch (IndexOutOfBoundsException e) {
												// TODO: handle exception
												Toast.makeText(mContext, "잠시후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
												notifyDataSetChanged();
											}

											//timer_list.clear();
										}
									});
								}
							}, 1500);


							//							if(timer_list.size()>0){
							//								for(int i=0; i<timer_list.size();i++){
							//									try {
							//										timer_list.get(i).cancel();
							//										timer_list.get(i).schedule(new TimerTask() {
							//											
							//											@Override
							//											public void run() {
							//												// TODO Auto-generated method stub
							//												deleteCell(word.getView(), word.getIndex());
							//											}
							//										}, 1500);
							//									} catch (IllegalStateException e) {
							//										// TODO: handle exception
							//									}
							//									
							//								}
							//							}
							//							
							//							timer_list.add(word.timer);

						}



						@Override
						public void onLeftMovement() {
							// TODO Auto-generated method stub

							Config.unknow_count++;

							flag_touch = false;
							if(mode == MODE_EXAM_KNOWN_LIST||mode == MODE_NORMAL_KNOWN_LIST){
								return ;
							}
							int ex_state = items.get(position).getState();
							items.get(position).increaseWrongCount();
							boolean isKnown= false;
							if(!word.isWrong())
							{
								wrong++;
								ONCE_WORD_COUNT--;
								word.setWrong(true);
								word.setRight(false);
								db.updateRightWrong(false, word.get_id());
							}
							db.updateWordInfo(items.get(position), isKnown);
							db.insertLevel(items.get(position), isKnown);


							//Config.Difficulty = db.calcLevel(Config.CHANGE_LEVEL_COUNT);

							SideActivity.set_tvLevel();
							Word word_for_write= db.getWord(items.get(position).get_id());
							word.setState(word_for_write.getState());
							String data = word_for_write.getWord()+"@"+String.valueOf(word_for_write.getScore())+"@"+String.valueOf(ex_state)+"@"
									+String.valueOf(word_for_write.getState())+"@"+get_date()+"\r\n";
							log_file.input_LogData_in_file(data);

							//wordParams.put(""+Log_count++, "left@"+word_for_write.getWord()+"@"+String.valueOf(word_for_write.getScore())+"@"+String.valueOf(word.getExState())+"@"
							//		+String.valueOf(word_for_write.getState())+"@"+System.currentTimeMillis()/1000);
							studyWord += ""+(Log_count++)+"/left/"+word_for_write.getWord()+"/"+String.valueOf(word_for_write.getScore())+"/"+String.valueOf(word.getExState())+"/"
									+String.valueOf(word_for_write.getState())+"/"+System.currentTimeMillis()/1000+"\r\n";

							//							wordParams.put("word", word_for_write.getWord());
							//							wordParams.put("score", String.valueOf(word_for_write.getScore()));
							//							wordParams.put("exState", String.valueOf(ex_state));
							//							wordParams.put("state", String.valueOf(word_for_write.getState()));
							//							wordParams.put("date",String.valueOf(System.currentTimeMillis()));
							//							Log.e("wordParams","word:"+wordParams.get("word")+"/score:"+wordParams.get("score")+"/exStae:"+wordParams.get("exState")+
							//									"/state:"+wordParams.get("state")+"/date"+wordParams.get("date"));
							//							FlurryAgent.logEvent("모른다,WordListFragment_onLeftMovement,0",wordParams,true);

							SideActivity.set_tvLevel();
							if(word.getWrongCount()!=0&& isWrongContinueShow){
								//vh.tvUnknownCount.setText(items.get(position).getWrongCount() + "");

								vh.tvUnknownCount.setVisibility(View.VISIBLE);
								vh.tvUnknownCount.setBackgroundResource(R.drawable.main_cell_forget);

							}
							mPullToRefreshListView.setWordCount(right, wrong, ONCE_WORD_COUNT);
						}


						@Override
						public void onRightMovement() {
							// TODO Auto-generated method stub

							flag_touch = false;
						}
					})
					);
			if(isListAnimaion)
			{
				if(position<8){
					Animation animation = null;  
					animation = new TranslateAnimation(metrics.widthPixels/2, 0, 0, 0);
					animation.setDuration((position * 20) + 800);  
					view.startAnimation(animation);
				}else{
					isListAnimaion = false;
					if(!isindi){
						mPullToRefreshListView.setShowIndicator(true);
						isindi = true;
					}
				}
			}

			//			int mode = 2;
			//			switch(mode){  
			//			case 1:  
			//				animation = new TranslateAnimation(metrics.widthPixels/2, 0, 0, 0);  
			//				break;  
			//
			//			case 2:  
			//				animation = new TranslateAnimation(0, 0, metrics.heightPixels, 0);  
			//				break;  
			//
			//			case 3:  
			//				animation = new ScaleAnimation((float)1.0, (float)1.0 ,(float)0, (float)1.0);  
			//				break;  
			//
			//			case 4:  
			//				//non animation  
			//				animation = new Animation() {};  
			//				break;  
			//			}  
			//
			//			animation.setDuration(1000);  
			//			view.startAnimation(animation);  
			//			animation = null;  
			return view;
		}
	}

	boolean isindi=false;
	public int get_position(String line1, String line2, String line3){

		int position = 0;
		int min=line1.length();
		String[] line = {line1,line2,line3};
		for(int i=0 ; i<line.length; i++){
			if(line[i].length() < min){
				min=line[i].length(); 
				position = i;
			}
		}

		return position;
	}
	public int get_position(String line1, String line2){
		int position = 0;
		int min=line1.length();
		String[] line = {line1,line2};
		for(int i=0 ; i<line.length; i++){
			if(line[i].length() < min){
				min=line[i].length(); 
				position = i;
			}
		}
		return position;
	}

	public String Word_split(String means){

		//결과값
		String result_mean = "";

		//단어 쪼갠것
		String[] mean = means.split(", ");

		//각 라인
		String[] line = {"","",""};

		// 단어의 총길이.

		int length=means.length();
		for(int i=0;i<mean.length-1;i++){
			for(int j=0; j< mean.length-1-i;j++){
				if(mean[j].length()>mean[j+1].length()){                   
					String tmp = mean[j];
					mean[j] = mean[j+1];
					mean[j+1] = tmp;                  
				}              
			}          
		}

		if(mean.length==1){
			result_mean = means;
		}else if(length<=10){
			result_mean = means;
		}else if(length>10&&length<=20){
			for(int i=mean.length-1; i>-1;i--){
				line[get_position(line[0], line[1])] += mean[i]+", ";
			}

			result_mean =  line[0].substring(0, line[0].length()-2)+"\n"+line[1].substring(0, line[1].length()-2);
		}else if(length>20){
			for(int i=mean.length-1; i>-1;i--){
				line[get_position(line[0], line[1], line[2])] += mean[i]+", ";
			}

			result_mean =  line[0].substring(0, line[0].length()-2)+"\n"+line[1].substring(0, line[1].length()-2)
					+"\n"+line[2].substring(0, line[2].length()-2);
		}

		return result_mean;
	}

	public static String get_date(){
		String result = "";
		Calendar cal = Calendar.getInstance();

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);

		return result = year + "-" + month + "-" + day + " " + hour + ":" + minute;
	}

	boolean isDelete = false;
	private synchronized void deleteCell(final View v, final Word word, final int exState) {
		AnimationListener al = new AnimationListener() {


			@Override
			public void onAnimationEnd(Animation arg0) {

				synchronized (del_count) {
					if(del_count>0){
						mcount_thread.run();
					}
				}
				del_word.add(word);

				switch (db.getExState(word.get_id())) {
				case -1:	//모르는 단어
					//몰랐다가 외운 단어		? -> 0  
					Config.unknw_to_knw++;
					break;
				case 0:		//처음 보는 단어
					switch (exState) {
					case -1:
						//처음 본 모르는 단어를 외웠을 때		X -> ? -> 0
						Config.new_to_unknw_to_knw++;
						break;
					default:	// case 0
						//원래 아는 던어		X -> 0
						settings.edit().putInt(Config.NEW_COUNT, settings.getInt(Config.NEW_COUNT, 0)+1).commit();
						Config.new_to_knw++;
						break;
					}
					break;
				default:	//아는 단어
					switch (exState) {
					case -1:
						//외웠다가 까먹은 단어	! -> ? -> 0
						Config.knw_to_unknw_to_knw++;
						break;
					default:
						//까먹지 않은 단어 		! -> 0 
						settings.edit().putInt(Config.REVIEW_COUNT, settings.getInt(Config.REVIEW_COUNT, 0)+1).commit();
						Config.knw_to_knw++;
						break;
					}
					MainActivity.setActionBar(true);
					break;
				}


				ViewHolder vh = (ViewHolder)v.getTag();

				vh.needInflate = true;
				isListAnimaion = false;

				Log.e("test_listview",""+del_count + "       "+del_word.size());
				Log.e("test_listview","------------------------------------------------------------------");
				synchronized (del_count) {
					Log.e("test_listview","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
					if(del_count<=0){
						Log.e("test_listview","bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
						del_word.add(word);
						for(int i=del_word.size()-1; i>=0;i--){
							adapter.remove(del_word.get(i));
							del_word.remove(i);

							Log.e("view_check", "단어는 "+word.getWord());
						}


						isDelete= true;	
						flag_shake = false;
						flag_touch = false;
						flag_scroll = true;
						adapter.notifyDataSetChanged();

						for (int j = 0; j < words.size(); j++) {
							Log.e("test_listview", words.get(j).getWord()+"  "+words.get(j).getState()+ "   ");
						}
						Log.e("test_listview","------------------------------------------------------------------");

					}
				}



				ivTemp.setVisibility(View.GONE);

				if(adapter.getCount()==0){
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							new AlertDialog.Builder(getActivity())
							.setMessage("현재 단어장 내의 단어를 모두 학습을 끝냈습니다.\n다음 단어장으로 넘어갑니다.")
							.setPositiveButton("확인", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
								}
							}).show();

							checkLevel();

							Intent intent = new Intent(getActivity(), DBService.class);
							PendingIntent pintent = PendingIntent.getService(getActivity(), 0, intent, 0);
							try {
								pintent.send();
							} catch (CanceledException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});




				}
				//				vh.linearForward.setEnabled(true);
			}

			@Override public void onAnimationRepeat(Animation animation) {}

			@Override public void onAnimationStart(Animation animation) {

			}

		};
		collapse(v, al);
	}

	public void checkLevel(){

		final int ex_Difficult = Config.Difficulty;
		final int after_Difficult = db.calcLevel(Config.CHANGE_LEVEL_COUNT);

		if(ex_Difficult==after_Difficult){

			sendWordlistLogInfo();
			db.deleteAllCurrentWord();
			adapter.clear();
			ONCE_WORD_COUNT = Config.ONCE_WORD_COUNT;
			refresh();

		}else{
			if(ex_Difficult<after_Difficult&&ex_Difficult!=6){
				String text = "";
				if(ex_Difficult==Config.MAX_DIFFICULTY){
					text = "밀당영단어가 측정한 "+settings.getString(MainActivitys.GpreName, "이름없음")+"님의 단어 수준이 매우 높게 측정되었습니다. 고득점 필수 단어를 학습하시겠습니까?";
				}else{
					text = "높은 레벨로 평가됩니다.\n레벨을 올리시겠습니까?";
				}

				new AlertDialog.Builder(getActivity())
				.setMessage(text)

				.setPositiveButton("확인", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						if(Config.CHANGE_LEVEL_COUNT>20){
							Config.CHANGE_LEVEL_COUNT -=10;
							settings.edit().putString(MainActivitys.GpreLevelCounting, ""+Config.CHANGE_LEVEL_COUNT).commit();
						}


						if(ex_Difficult==Config.MAX_DIFFICULTY){
							if(Config.MAX_DIFFICULTY>6){
								Config.CHANGE_LEVEL_COUNT =20;
								settings.edit().putString(MainActivitys.GpreLevelCounting, ""+Config.CHANGE_LEVEL_COUNT).commit();
								int topic = Integer.parseInt(settings.getString(MainActivitys.GpreTopic, "1"))+1;
								settings.edit().putString(MainActivitys.GpreTopic, ""+topic).commit();
								Config.MAX_DIFFICULTY = Config.MAX_DIFFICULTY+2;
							}


						}

						if(after_Difficult>2&&after_Difficult<4){
							settings.edit().putString(MainActivitys.GpreTopic, "2").commit();
							Config.MIN_DIFFICULTY = 1;
							Config.MAX_DIFFICULTY = 4;

						}else if(after_Difficult>4&&after_Difficult<6){
							settings.edit().putString(MainActivitys.GpreTopic, "3").commit();
							Config.MIN_DIFFICULTY = 1;
							Config.MAX_DIFFICULTY = 6;
						}


						settings.edit().putString(MainActivitys.GpreLevel, ""+after_Difficult).commit();
						Config.Difficulty = after_Difficult;

						sendWordlistLogInfo();
						db.deleteAllCurrentWord();
						adapter.clear();
						ONCE_WORD_COUNT = Config.ONCE_WORD_COUNT;
						refresh();



					}
				}).setNegativeButton("취소", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(Config.CHANGE_LEVEL_COUNT<40){
							Config.CHANGE_LEVEL_COUNT +=10;
							settings.edit().putString(MainActivitys.GpreLevelCounting, ""+Config.CHANGE_LEVEL_COUNT).commit();
						}
						FlurryAgent.logEvent("BaseActivity_DBPool:LevelRetainOn"+Config.Difficulty);

						db.insertFalseCount(Config.Difficulty);

						sendWordlistLogInfo();
						db.deleteAllCurrentWord();
						adapter.clear();
						ONCE_WORD_COUNT = Config.ONCE_WORD_COUNT;
						refresh();
					}
				}).show();
			}else{
				new AlertDialog.Builder(getActivity())
				.setMessage("레벨이 내려갔습니다.")
				.setPositiveButton("확인", null).show();
				settings.edit().putString(MainActivitys.GpreLevel, ""+after_Difficult).commit();
				Config.Difficulty = after_Difficult;
				
				sendWordlistLogInfo();
				db.deleteAllCurrentWord();
				adapter.clear();
				ONCE_WORD_COUNT = Config.ONCE_WORD_COUNT;
				refresh();
			}
		}
	}

	OnTouchListener ev_touch = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				return flag_scroll; // Indicates that this has been handled by you and will not be forwarded further.
			}
			return false;
		}
	};
	private void collapse(final View v, AnimationListener al) {
		final int initialHeight = v.getHeight();
		//		Log.v("kjw", "real initialHeight = " + initialHeight);
		Animation anim = new Animation() {

			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				//				Log.v("kjw", "real interpolatedTime = " + interpolatedTime);
				if (interpolatedTime == 1) {
					v.setVisibility(View.GONE);
				}
				else {
					//					v.getLayoutParams().height = initialHeight;// - (int)(initialHeight * interpolatedTime);
					v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override 
			public boolean willChangeBounds() {
				return true;
			}
		};
		if (al != null) {
			anim.setAnimationListener(al);
		}
		anim.setDuration(ANIMATION_DURATION);
		v.startAnimation(anim);

	}


	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

		tts_util.onDestroy();
		super.onDestroy();
	}



	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		start = firstVisibleItem;
		last = visibleItemCount; 
		return;


	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if(flag_touch){
			flag_touch = false;
		}
		return;
	}

	@Override
	public void onResume() {
		super.onResume();
		mSensorManager.registerListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_UI);

		if(isindi){
			mPullToRefreshListView.setShowIndicator(false);
		}
	}

	@Override
	public void onPause() {
		mSensorManager.unregisterListener(mSensorListener);
		super.onPause();
	}

	//Map<String, String> wordListParams = new HashMap<String, String>();

	String starttime;
	String startdate;

	Date date = new Date();

	public void onStart()
	{

		super.onStart();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 시작,FinishStudyFragment,1]\r\n");
	}


	public void onStop()
	{
		super.onStop();
		Map<String, String> articleParams = new HashMap<String, String>();
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		Log.e("splash", startdate+"        "+date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		//FlurryAgent.logEvent("WordListFragment", true);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 끝,FinishStudyFragment,1,{Start:"+articleParams.get("Start")+",End:"+articleParams.get("End")+"}]\r\n");
	}

	public void sendWordlistLogInfo(){
		Map<String, String> map = new HashMap<String, String>();



		int reviewcount = db.getMforget();
		if(reviewcount>0){	//review word
			MainActivity.setActionBar(true);
			String value = settings.getString(MainActivitys.GpreReviewTutorial, "0");
			if(value.equals("0")){
				startActivity(new Intent(getActivity(), ReviewTutoActivity.class));
			}
			map.put("nxt_set_rvw_wrd",""+reviewcount);	//log : review word
		}else{	//new word
			MainActivity.setActionBar(false);
			map.put("nxt_set_rvw_wrd","0");	//log : new word
		}

		map.put("level", ""+Config.Difficulty);		//log : current level
		map.put("!", "[{?->! : " 		+ Config.unknw_to_knw + "}" +				//몰랐다가 외운 단어		? -> 0
				", {X->?->! : " 	+ Config.new_to_unknw_to_knw + "}" +		//처음 본 모르는 단어를 외웠을 때		X -> ? -> 0
				", {X->! : " 		+ Config.new_to_knw + "}" +					//원래 아는 던어		X -> 0
				", {!->?->! : " 	+ Config.knw_to_unknw_to_knw + "}" + 		//외웠다가 까먹은 단어	! -> ? -> 0
				", {!->! : " 		+ Config.knw_to_knw + "}]");				//까먹지 않은 단어 		! -> 0

		Config.wordSetCount++;
		map.put("WordSetCount",""+(Config.wordSetCount-1));


		if(adapter.getCount()>0){
			for(int i =0; i<adapter.getCount();i++){
				switch(adapter.getItem(i).getState()){
				case -1:	//unknown word
					switch (adapter.getItem(i).getExState()) {
					case -1:	//?->?
						Config.unknw_to_unknw++;
						break;	
					case 0:		//X->?
						Config.new_to_unknw++;
						break;
					default:	//!->?
						Config.knw_to_unknw++;
						break;	
					}
					break;
				default://case 0:	//new word,	X
					Config.new_to_new++;
					break;

				}	
			}
		}


		map.put("?","[{?->? : "+Config.unknw_to_unknw + "}" +  
				", {X->? : "+Config.new_to_unknw + "}" +
				", {!->? : "+Config.knw_to_unknw + "}]");

		map.put("X", "[{X : "+Config.new_to_new + "}]");

		//Log.e("log test","?->!")	


		FlurryAgent.logEvent("BaseActivity_WordListFragment:NextWordSet", map); 

		Config.unknw_to_knw=0;
		Config.new_to_unknw_to_knw=0;
		Config.new_to_knw=0;
		Config.knw_to_unknw_to_knw=0;
		Config.knw_to_knw=0;

		Config.unknw_to_unknw=0;
		Config.new_to_unknw=0;
		Config.knw_to_unknw=0;

		Config.new_to_new=0;

	}



	//	private void refresh()
	//	{
	//		right = 0;
	//		wrong = 0;
	//		
	//		if(mode == MODE_NORMAL_SCORE_LIST)
	//		{
	//			mPullToRefreshListView.setBackgroundResource(0);
	//			words = db.wordsWithScore();
	//			words.addAll(db.wordsWithUnknown());
	//			
	//			int level = Config.Diffilculty;
	//			int wordsCount = words.size();
	//			int restCount = Config.ONCE_WORD_COUNT - wordsCount;
	//			
	////			Log.d("kjw", "level = " + level + "  wordsCount = " + wordsCount + "  restCount = " + restCount);
	//			if(restCount > 0)
	//			{
	//				int p80Count = restCount * 4 / 5;
	//				
	//				ArrayList<Word> temp = db.wordsWithLevel(level, p80Count);
	//				int tempCount = temp.size();
	//				
	//				Log.d("kjw", "80Count = " + p80Count + "  after db 80 = " + tempCount);
	//				
	//				if(tempCount >= p80Count)
	//				{
	//					words.addAll(temp);
	//					
	//					wordsCount = words.size();
	//					restCount = Config.ONCE_WORD_COUNT - wordsCount;
	//					
	//					Log.d("kjw", "final rest" + restCount + "  wordsCOunt = " + wordsCount);
	//					
	//					if(level - 1 == 0)
	//						words.addAll(db.wordsWithLevel(level, restCount));
	//					else
	//						words.addAll(db.wordsWithLevel(level - 1, restCount));
	//				}
	//				else
	//				{
	//					words.addAll(temp);
	//					
	//					wordsCount = words.size();
	//					restCount = Config.ONCE_WORD_COUNT - wordsCount;
	//					
	//					if(level + 1 == 7)
	//						words.addAll(db.wordsWithLevel(level, restCount));
	//					else
	//						words.addAll(db.wordsWithLevel(level + 1, restCount));
	//				}
	//			}
	//			
	//			Log.d("kjw", "words = " + words.size());
	//			
	//			while(words.size() > Config.ONCE_WORD_COUNT)
	//			{
	//				words.remove(words.size() - 1);
	//			}
	////			words = db.wordsWithScore();
	//			
	//			
	//		}
	//		else if(mode == MODE_NORMAL_KNOWN_LIST)
	//		{
	//			mPullToRefreshListView.setBackgroundResource(R.drawable.list_known_bg);
	//			words = db.wordsWithKnown();
	//		}
	//		else if(mode == MODE_EXAM_SCORE_LIST)
	//		{
	//			mPullToRefreshListView.setBackgroundResource(0);
	//			words = db.pwordsWithQuery(query); 
	//		}
	//		else if(mode == MODE_EXAM_KNOWN_LIST)
	//		{
	//			mPullToRefreshListView.setBackgroundResource(R.drawable.list_known_bg);
	//			words = db.pwordsWithKnownAndQuery(query);
	//		}
	//			
	//		
	//		ONCE_WORD_COUNT = words.size();
	//		
	//		adapter = new ListAdapter(getActivity(), R.layout.word_list_row, words);
	//		mPullToRefreshListView.setAdapter(adapter);
	//	}
	/*
	 * 
	 * 
	 */
}