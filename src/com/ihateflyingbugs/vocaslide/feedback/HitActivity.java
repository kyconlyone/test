package com.ihateflyingbugs.vocaslide.feedback;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.internal.co;
import com.google.android.gms.internal.ih;
import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.data.DBPool;
import com.ihateflyingbugs.vocaslide.data.ExamContents;
import com.ihateflyingbugs.vocaslide.tab.HitTabAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class HitActivity extends FragmentActivity {

	RelativeLayout graph_layout;

	ToggleButton hit_push;
	TextView hit_pushTv;

	private DBPool db;
	private Integer level_counting[][] = new Integer[6][2];

	private ProgressBar gradeOne;
	private ProgressBar gradeTwo;
	private ProgressBar gradeThree;
	private ProgressBar gradeFour;
	private ProgressBar gradeFive;

	private TextView gradeOneTv;
	private TextView gradeTwoTv;
	private TextView gradeThreeTv;
	private TextView gradeFourTv;
	private TextView gradeFiveTv;



	SharedPreferences pushPreference;
	SharedPreferences.Editor pushEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hit);

		db = DBPool.getInstance(getApplicationContext());
		level_counting = db.getLevelCounting();

		gradeOne = (ProgressBar) findViewById(R.id.grade_1_expect_word_percentagePb);
		gradeTwo = (ProgressBar) findViewById(R.id.grade_2_expect_word_percentagePb);
		gradeThree = (ProgressBar) findViewById(R.id.grade_3_expect_word_percentagePb);
		gradeFour = (ProgressBar) findViewById(R.id.grade_4_expect_word_percentagePb);
		gradeFive = (ProgressBar) findViewById(R.id.grade_5_expect_word_percentagePb);

		gradeOneTv = (TextView) findViewById(R.id.grade_1_expect_word_percentageTv);
		gradeTwoTv = (TextView) findViewById(R.id.grade_2_expect_word_percentageTv);
		gradeThreeTv = (TextView) findViewById(R.id.grade_3_expect_word_percentageTv);
		gradeFourTv = (TextView) findViewById(R.id.grade_4_expect_word_percentageTv);
		gradeFiveTv = (TextView) findViewById(R.id.grade_5_expect_word_percentageTv);

		gradeOne.setMax(level_counting[0][1]);
		gradeTwo.setMax(level_counting[1][1]);
		gradeThree.setMax(level_counting[2][1]);
		gradeFour.setMax(level_counting[3][1]);
		gradeFive.setMax(level_counting[4][1] + level_counting[5][1]);

		gradeOne.setProgress(level_counting[0][0]);
		gradeTwo.setProgress(level_counting[1][0]);
		gradeThree.setProgress(level_counting[2][0]);
		gradeFour.setProgress(level_counting[3][0]);
		gradeFive.setProgress(level_counting[4][0] + level_counting[5][0]);

		gradeOneTv.setText(Integer.toString(level_counting[0][0])+"/"+Integer.toString(level_counting[0][1]));
		gradeTwoTv.setText(Integer.toString(level_counting[1][0])+"/"+Integer.toString(level_counting[1][1]));
		gradeThreeTv.setText(Integer.toString(level_counting[2][0])+"/"+Integer.toString(level_counting[2][1]));
		gradeFourTv.setText(Integer.toString(level_counting[3][0])+"/"+Integer.toString(level_counting[3][1]));
		gradeFiveTv.setText(Integer.toString(level_counting[4][0] + level_counting[5][0])+"/"+Integer.toString(level_counting[4][1] + level_counting[5][1]));
		
		hit_push = (ToggleButton) findViewById(R.id.hit_pushBtn);
		TextView hit_pushTv = (TextView) findViewById(R.id.hit_pushTv);

		pushPreference = getPreferences(MODE_WORLD_READABLE
				| MODE_WORLD_WRITEABLE);
		pushEditor = pushPreference.edit();

		List<ExamContents> list = new ArrayList<ExamContents>();
		final String[] CONTENT2 = new String[] { "2014 수능", "9월 평가원", "3월평가원",
				"2013 수능" };
		for (int i = 0; i < 4; i++) {
			ExamContents exam = new ExamContents(CONTENT2[i], 90 + i,
					50 - i * 7, 50 + i * 8, 50 - i * 9, 50 - i * 10,
					50 + i * 11);
			list.add(exam);
		}

		final FragmentPagerAdapter adapter = new HitTabAdapter(
				getSupportFragmentManager(), list);

		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);

		hit_push.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (hit_push.isChecked()) {
					pushEditor.putBoolean("pushFlag", true);
					pushEditor.commit();
					boolean b = pushPreference.getBoolean("pushFlag", false);
					if (b == true) {
						Log.d("toggle", "true");
					}
				} else {
					pushEditor.putBoolean("pushFlag", false);
					pushEditor.commit();
					boolean b = pushPreference.getBoolean("pushFlag", true);
					if (b == false) {
						Log.d("toggle", "false");
					}
				}
			}
		});
		
		hit_pushTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(
						HitActivity.this,
						com.ihateflyingbugs.vocaslide.popup.OneButtonPopUp.class);
				i.putExtra("title", 3);
				startActivity(i);
			}
		});

	}
}
