package com.ihateflyingbugs.vocaslide;


import java.util.HashMap;
import java.util.Map;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.data.Config;
import com.ihateflyingbugs.vocaslide.data.DBPool;
import com.ihateflyingbugs.vocaslide.data.Date;
import com.ihateflyingbugs.vocaslide.login.MainActivitys;
import com.ihateflyingbugs.vocaslide.tutorial.MainActivity;

import android.R.anim;
import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FinishStudyFragment extends Fragment implements OnClickListener{
	private EditText et_qna_email, et_qna_phone, et_qna_text;
	private Spinner sp_qna_title;
	private Button bt_qna_submit;
	DBPool db;

	private static Activity thisActivity;

	private static Handler handler;

	static SharedPreferences mPreference;

	TextView tv_finish_study_cong;
	TextView tv_finish_study_time;
	Context mContext;

	Integer level_count[][];
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		thisActivity = getActivity();
		mContext = getActivity().getApplicationContext();
		View view = inflater.inflate(R.layout.fragment_finish_study, container, false);

		db = DBPool.getInstance(getActivity());

		tv_finish_study_cong= (TextView)view.findViewById(R.id.tv_finish_study_cong);
		tv_finish_study_time= (TextView)view.findViewById(R.id.tv_finish_study_time);


		Button bt_finish_study_next = (Button)view.findViewById(R.id.bt_finish_study_next);
		Button bt_finish_study_destory = (Button)view.findViewById(R.id.bt_finish_study_destory);

		bt_finish_study_destory.setOnClickListener(this);
		bt_finish_study_next.setOnClickListener(this);

		level_count = db.getLevelCounting();


		if(Config.Difficulty==6){
			bt_finish_study_next.setVisibility(View.INVISIBLE);
		}
		
		String topic = null;
		
		if(getMySharedPreferences(MainActivitys.GpreTopic).equals("1")){

			topic = "수능";
		}else if(getMySharedPreferences(MainActivitys.GpreTopic).equals("2")){

			topic = "토익";

		}else if(getMySharedPreferences(MainActivitys.GpreTopic).equals("3")){
			topic = "토플";
		}else{
			topic = "해당";
		}
		tv_finish_study_cong.setText("축하합니다\n "+topic+" 단어를\n 마스터 하셨습니다.");

		handler= new Handler();

		return view;
	}

	private String getMySharedPreferences(String _key) {
		if(mPreference == null){
			mPreference =  mContext.getSharedPreferences(MainActivitys.preName, mContext.MODE_WORLD_READABLE|mContext.MODE_WORLD_WRITEABLE);
		}
		return mPreference.getString(_key, "");
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_finish_study_next:
			FlurryAgent.logEvent("SideActivity_CompleteCurrentTopic:ClickNextTopic");
			if(Config.Difficulty<5){
				Config.Difficulty= Config.Difficulty+1;
				Config.WORD_TOPIC = Config.WORD_TOPIC+1;
				mPreference.edit().putString(MainActivitys.GpreTopic, ""+Config.WORD_TOPIC).commit();
				mPreference.edit().putString(MainActivitys.GpreLevel, ""+Config.Difficulty).commit();
				Config.MAX_DIFFICULTY = Config.MAX_DIFFICULTY+2;
				//Config.MIN_DIFFICULTY = Config.MIN_DIFFICULTY+2;

				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				fragmentTransaction.replace(R.id.linearFragment, new WordListFragment(WordListFragment.MODE_NORMAL_SCORE_LIST)).addToBackStack(null).commit();
				
			}else{
				Config.Difficulty= Config.Difficulty+1;
				mPreference.edit().putString(MainActivitys.GpreLevel, ""+Config.Difficulty).commit();
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				fragmentTransaction.replace(R.id.linearFragment, new WordListFragment(WordListFragment.MODE_NORMAL_SCORE_LIST)).addToBackStack(null).commit();
			}

			break;

		case R.id.bt_finish_study_destory:
			FlurryAgent.logEvent("SideActivity_CompleteCurrentTopic:ClickAppFinish");
			getActivity().finish();
			break;
		}
	}
	String starttime;
	String startdate;
	Date date = new Date();

	Map<String, String> articleParams;
	public void onStart()
	{

		super.onStart();
		articleParams = new HashMap<String, String>();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("SideActivity_SideActivity_CompleteCurrentTopic", articleParams);
		// your code
	//	MainActivity.writeLog("[모든단어 암기완료 시작,FinishStudyFragment,1]\r\n");

	}

	public void onStop()
	{
		super.onStop();
//		FlurryAgent.endTimedEvent("CompleteCurrentTopic:Start");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		Log.e("splash", startdate+"        "+date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		// your code
	//	MainActivity.writeLog("[모든단어 암기완료 끝,FinishStudyFragment,1,{Start:"+articleParams.get("Start")+",End:"+articleParams.get("End")+"}]\r\n");
	}
	
	
	
	
	 

}
