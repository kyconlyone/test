package com.ihateflyingbugs.vocaslide.tutorial;

import java.util.HashMap;
import java.util.Map;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.data.Config;
import com.ihateflyingbugs.vocaslide.data.DBPool;
import com.ihateflyingbugs.vocaslide.data.Date;
import com.ihateflyingbugs.vocaslide.login.MainActivitys;

import android.R.anim;
import android.app.Activity;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ReviewTutoActivity extends Activity {


	SharedPreferences mPreference;
	RelativeLayout rl_tuto_review_2;
	LinearLayout ll_tuto_review_1;


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_tuto_review);

		
		rl_tuto_review_2= (RelativeLayout)findViewById(R.id.rl_tuto_review_2);
		ll_tuto_review_1= (LinearLayout)findViewById(R.id.ll_tuto_review_1);
		TextView tv_tuto_review_1 = (TextView)findViewById(R.id.tv_tuto_review_1);
		final TextView tv_tuto_review_2 = (TextView)findViewById(R.id.tv_tuto_review_2);
		
		setMySharedPreferences(MainActivitys.GpreReviewTutorial, "1");
		

		rl_tuto_review_2.setVisibility(View.GONE);
		ll_tuto_review_1.setVisibility(View.VISIBLE);
		final Animation anim1 = new AnimationUtils().loadAnimation(getApplicationContext(), android.R.anim.fade_out);
		final Animation anim2 = new AnimationUtils().loadAnimation(getApplicationContext(), android.R.anim.fade_in);
		final Animation anim3 = new AnimationUtils().loadAnimation(getApplicationContext(), android.R.anim.fade_in);

		DBPool db= DBPool.getInstance(getApplicationContext());


		tv_tuto_review_1.setText("�ܾ� ���� ���� ��带 ���� "+getMySharedPreferences(MainActivitys.GpreName)+"����\n����� ���� �ִ� "+db.getMforget()+"���� �ܾ \n�� Ȯ���� ��ų �� �ֽ��ϴ�.");
		tv_tuto_review_2.setText("�ܾ� ���� ���� ��带 ��ĥ����\n "+getMySharedPreferences(MainActivitys.GpreName)
				+"���� ��� ���� �ֱ� ��� ��������ϴ�.\n��� �ϼ��ɼ��� ���� ��Ȯ�� ������ ���� �˴ϴ�.\n������ �н��� ���� ��� �ϼ��� ������^^");
		

		final Button bt_tuto_review_next = (Button)findViewById(R.id.bt_tuto_review_next);
		final Button bt_tuto_review = (Button)findViewById(R.id.bt_tuto_review);

		tv_tuto_review_1.startAnimation( anim1);

		anim1.setAnimationListener(new AnimationListener() {

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
				tv_tuto_review_2.startAnimation(anim2);
			}
		});
		anim2.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				bt_tuto_review.startAnimation(anim3);

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
			}
		});
		bt_tuto_review.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				setMySharedPreferences(MainActivitys.GpreReviewTutorial, "1");
			}
		});
		bt_tuto_review_next.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rl_tuto_review_2.setVisibility(View.VISIBLE);
				ll_tuto_review_1.setVisibility(View.GONE);
				ll_tuto_review_1.startAnimation(new AnimationUtils().loadAnimation(getApplicationContext(), R.anim.slide_out_left));
				rl_tuto_review_2.startAnimation(new AnimationUtils().loadAnimation(getApplicationContext(), R.anim.slide_in_left));
			}
		});
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
		//FlurryAgent.logEvent("ReviewTutorialActivity:Start", true);
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("ReviewTutorialActivity", articleParams);
		
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();	
		
		//FlurryAgent.endTimedEvent("ReviewTutorialActivity:Start");
		
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		
		FlurryAgent.onEndSession(this);

	}

}
