package com.ihateflyingbugs.vocaslide;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.internal.LoadingLayout.OnNextClickListener;
import com.ihateflyingbugs.vocaslide.Get_my_uuid;
import com.ihateflyingbugs.vocaslide.LogDataFile;
import com.ihateflyingbugs.vocaslide.SideActivity;
import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.ShakeEventListener;
import com.ihateflyingbugs.vocaslide.TTS_Util;
import com.ihateflyingbugs.vocaslide.ViewHolder;
import com.ihateflyingbugs.vocaslide.WordListFragment;
import com.ihateflyingbugs.vocaslide.data.Config;
import com.ihateflyingbugs.vocaslide.data.DBPool;
import com.ihateflyingbugs.vocaslide.data.Date;
import com.ihateflyingbugs.vocaslide.data.Mean;
import com.ihateflyingbugs.vocaslide.data.Word;
import com.ihateflyingbugs.vocaslide.tutorial.MainActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;


public class Known_Activity extends Activity implements OnScrollListener,OnCheckedChangeListener {

	public static final int MODE_NORMAL_KNOWN_LIST = 1;

	public static int mode;
	private boolean flag_set_swipe_mode= true;
	private String query;
	static final int ANIMATION_DURATION = 400;
	private DisplayMetrics metrics;

	private DBPool db;
	private static ArrayList<Word> words;
	private ListAdapter adapter;
	private ListView listView;
	
	TTS_Util tts_util;

	Handler handler ;

	public Known_Activity(){}


	private boolean isListAnimaion = true;
	private boolean isStartAnimaion = true;
	static Vibrator vibe;
	// ������ 0 �̹Ƿ� ��� ����ǰ� ���� 200 milliseconds, ���� 500 milliseconds �ȴ�
	long[] pattern = { 0, 100, 50, 100 };
	

	private ActionBar bar;
	private View actionbar_main;
	Context mContext;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		handler = new Handler();
		setContentView(R.layout.test_fragment_word_list);
		mContext = getApplicationContext();
		metrics = new DisplayMetrics();
		db = DBPool.getInstance(this);
		vibe = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		getWindowManager().getDefaultDisplay().getMetrics(metrics);  

		words = new ArrayList<Word>();

		listView = (ListView)findViewById(R.id.test_listView1);

		makeActionBar(false);
		
		Log.d("kjw", "create refresh !!!!!!!");
		refresh();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);


		tts_util = new TTS_Util(getApplicationContext());
	}
	View view_topnavi;
	private void makeActionBar(boolean isExam)
	{
		bar = getActionBar();


		actionbar_main = getLayoutInflater().inflate(R.layout.known_action_bar, null);
		

		bar.setCustomView(actionbar_main, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		Button bt_known_back = (Button)actionbar_main.findViewById(R.id.bt_known_back);


		bt_known_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		});
	}



	int start;
	int last;
	private void refresh()
	{
		words.clear();

		listView.setBackgroundResource(R.drawable.list_known_bg);
		words = db.wordsWithKnown();

		isListAnimaion = true;
		flag_set_swipe_mode = false;

		adapter = new ListAdapter(getApplicationContext(), R.layout.word_list_row, words);
		listView.setAdapter(adapter);
		printList();
	}
	private void printList()
	{

		isListAnimaion =true;
		adapter = new ListAdapter(getApplicationContext(), R.layout.word_list_row, words);
		listView.setAdapter(adapter);

	}



	private void setViewHolder(View view) {

		final int NORMAL_KNOWN_COLOR = Color.rgb(0x75, 0xc9, 0x6d);
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


		vh.linearKnown.setBackgroundColor(NORMAL_KNOWN_COLOR);

		vh.needInflate = false;
		view.setTag(vh);
	}
	
	boolean flag_touch = false;
	
	private class ListAdapter extends ArrayAdapter<Word>{
		LayoutInflater vi;
		private ArrayList<Word> items;
		private boolean isWrongContinueShow;
		public ListAdapter(Context context, int resourceId, ArrayList<Word> items){
			super(context, resourceId, items);
			this.items = items;
			this.vi = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			isWrongContinueShow = false;
			mContext= context;
		}
		
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
			
			vh.linearUnknown.setVisibility(View.GONE);

			//			if(word.getMeaning().length()>20){
			//				vh.tvUnknownMeaning.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			//				vh.tvUnknownMeaning.setLineSpacing(4, 1);
			//			}else{
			//				vh.tvUnknownMeaning.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
			//				vh.tvUnknownMeaning.setLineSpacing(2, 1);
			//			}



			//vh.tvUnknownMeaning.setText(Word_split(word.getMeaning()));

		

			//vh.tvUnknownMeaning.setTextSize(TypedValue.COMPLEX_UNIT_SP, position);


			vh.ivKnown.setVisibility(View.VISIBLE);

			//final Map<String, String> Time_map = new HashMap<String, String>();
			

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
					Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
					vh.tvForward.startAnimation(animation); 
					AudioManager audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
					
					//FlurryAgent.logEvent("�ƴ´ܾ��� ������� Ŭ��,Known_Activity,1", true);
					//Time_map.clear();
					switch(audioManager.getRingerMode()){
					case AudioManager.RINGER_MODE_VIBRATE:
						// ����
						Toast.makeText(getApplicationContext(), "�Ҹ� ���� ��ȭ�� �ٽ� �õ����ּ���.", Toast.LENGTH_SHORT).show();
						break;
					case AudioManager.RINGER_MODE_NORMAL:
						// �Ҹ�
						if(tts_util.tts_check()){
							tts_util.tts_reading(vh.tvForward.getText().toString());
						}else{
							Toast.makeText(getApplicationContext(), "����� ������ �ֽ��ϴ�. ����Ŀ� �ٽ� �õ����ּ���.", Toast.LENGTH_SHORT).show();
						}
						break;
					case AudioManager.RINGER_MODE_SILENT:
						// ����
						Toast.makeText(getApplicationContext(), "�Ҹ� ���� ��ȭ�� �ٽ� �õ����ּ���.", Toast.LENGTH_SHORT).show();
						break;
					}
					
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

			vh.linearForward.setOnTouchListener(new SwipeDismissTouchListener(
					vh.linearForward, vh.linearUnknown, vh.linearKnown, vh.iv_wc , flag_set_swipe_mode, 
					null,
					new SwipeDismissTouchListener.DismissCallbacks() {
						@Override
						public boolean canDismiss(Object token) {
							
							//Time_map.put("Start", ""+System.currentTimeMillis());
							//FlurryAgent.logEvent("�ƴ´ܾ��� �ܾ� ���� ����,Known_Activity,1", true);
							vh.ll_second_mean.setVisibility(View.GONE);
							vh.ll_third_mean.setVisibility(View.GONE);
							vh.ll_forth_mean.setVisibility(View.GONE);
							vh.ll_first_mean.setVisibility(View.VISIBLE);

							vh.tv_first_mean.setText("�о �ܾ� ���ֱ�");

							vh.tv_first_mean_title.setText(">");

							int text_sp = 17;
							vh.tv_first_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
							vh.tv_first_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);

							Log.d("test_class", "5");
							
							return true;
						}


						@Override
						public void onLeftDismiss(View v, Object token,
								boolean flag) {
							// TODO Auto-generated method stub

							flag_touch = false;
							int ex_state = word.getState();
							boolean isKnown =false;
							vh.linearForward.setVisibility(View.GONE);
							
							
							Map<String, String> wordmap = new HashMap<String, String>();
							wordmap.put("word", word.getWord());
							wordmap.put("State", String.valueOf(ex_state));
							wordmap.put("Score", String.valueOf(word.getScore()));
							FlurryAgent.logEvent("KnownWordListActivity:RemoveKnownWord",wordmap);
							
							word.increaseWrongCount();

							if(!word.isWrong())
							{

								word.setWrong(true);
								word.setRight(false);
								db.updateRightWrong(false, word.get_id());
							}
							
							
							

							db.updateWordInfo(word, isKnown);

							/*
							 * ���� ���� �ؾ��� �ε����� ��ġ���� �ʴ´�
							 * ����ȭ�����ָ� �ذ��Ҽ��ִ� �κ��ΰ� ����.							
							 */
							
							
							Word word_for_write= db.getWord(word.get_id());

							
							word.setState(word_for_write.getState());
							if(!isWrongContinueShow)
							{
								//Time_map.put("End", ""+System.currentTimeMillis());
								//Time_map.put("Duration", ""+(Long.valueOf(Time_map.get("End"))-Long.valueOf(Time_map.get("Start"))));
								//FlurryAgent.logEvent("�ƴ´ܾ��� Leftdismiss,Known_Activity,1", Time_map);
								//Time_map.clear();
								deleteCell(view, position);
							}

							if(word.getWrongCount()!=0&& isWrongContinueShow){
								//vh.tvUnknownCount.setText(items.get(position).getWrongCount() + "");
								handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										vh.tvUnknownCount.setVisibility(View.VISIBLE);
										vh.tvUnknownCount.setBackgroundResource(R.drawable.main_cell_forget);
									}
								});
							}
						}


						@Override
						public void onRightDismiss(View v, Object token,
								boolean flag) {
							// TODO Auto-generated method stub

							flag_touch = false;
							//Time_map.put("End", ""+System.currentTimeMillis());
							//Time_map.put("Duration", ""+(Long.valueOf(Time_map.get("End"))-Long.valueOf(Time_map.get("Start"))));
							//FlurryAgent.logEvent("�ƴ´ܾ��� Rightdismiss,Known_Activity,1", Time_map);
							//Time_map.clear();
							return ;
						}


						@Override
						public void onLeftMovement() {
							// TODO Auto-generated method stub
							flag_touch = false;
							flag_touch = false;
							//Time_map.put("End", ""+System.currentTimeMillis());
							//Time_map.put("Duration", ""+(Long.valueOf(Time_map.get("End"))-Long.valueOf(Time_map.get("Start"))));
							//FlurryAgent.logEvent("�ƴ´ܾ��� LeftMovement,Known_Activity,1", Time_map);
							//Time_map.clear();
							return ;


						}

						@Override
						public void onRightMovement() { 
							// TODO Auto-generated method stub
							flag_touch = false;
							
						}
					})
					);
			Log.e("getcount", String.valueOf(start)+ "   "+String.valueOf(last) + "  "+String.valueOf(position));
			if(isListAnimaion)
			{
				if(position<8){
					Animation animation = null;  
					animation = new TranslateAnimation(metrics.widthPixels/2, 0, 0, 0);
					animation.setDuration((position * 20) + 800);  
					view.startAnimation(animation);
				}else{
					isListAnimaion = false;
				}
			}
			return view;
		}
	}

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

		//�����
		String result_mean = "";

		//�ܾ� �ɰ���
		String[] mean = means.split(", ");

		//�� ����
		String[] line = {"","",""};

		// �ܾ��� �ѱ���.

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
	private void deleteCell(final View v, final int index) {
		AnimationListener al = new AnimationListener() {


			@Override
			public void onAnimationEnd(Animation arg0) {
				words.remove(index);
				ViewHolder vh = (ViewHolder)v.getTag();
				vh.needInflate = true;
				isListAnimaion = false; 
				adapter.notifyDataSetChanged();
//				if(adapter.getCount()==0){
//					new AlertDialog.Builder(Known_Activity.this)
//					.setMessage("���� �ܾ��� ���� �ܾ ��� �н��� ���½��ϴ�.\n���� �ܾ������� �Ѿ�ϴ�.")
//					.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							// TODO Auto-generated method stub
//							adapter.clear();
//							Intent intent = new Intent(Known_Activity.this, com.ihateflyingbugs.vocaslide.tutorial.MainActivity.class);
//							startActivity(intent);
//							finish();
//						}
//					}).show();
//				}
				//				vh.linearForward.setEnabled(true);
			}

			@Override public void onAnimationRepeat(Animation animation) {}

			@Override public void onAnimationStart(Animation animation) {}

		};
		collapse(v, al);
	}

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
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

		start = firstVisibleItem;
		last = visibleItemCount;

	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onResume() {
		super.onResume();
	
	}

	@Override
	public void onPause() {
		
		super.onPause();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		
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
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("KnownWordListActivity", articleParams);
		// your code
	//	MainActivity.writeLog("[�ƴ´ܾ��� ����,Known_Activity,1]\r\n");

	}

	public void onStop()
	{
		super.onStop();
		FlurryAgent.endTimedEvent("KnownWordListActivity:Start");
		articleParams.put("End", String.valueOf(System.currentTimeMillis()));
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
		//articleParams.clear();
		// your code
	//	MainActivity.writeLog("[�ƴ´ܾ��� ��,Known_Activity,1,{Start:"+articleParams.get("Start")+",End:"+articleParams.get("End")+"}]\r\n");
	}
	

}