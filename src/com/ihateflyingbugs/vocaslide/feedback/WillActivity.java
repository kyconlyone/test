package com.ihateflyingbugs.vocaslide.feedback;

import hirondelle.date4j.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.caldroidsample.CaldroidSampleCustomFragment;
import com.caldroidsample.day.CaldroidDayFragment;
import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.data.DBPool;
import com.ihateflyingbugs.vocaslide.popup.SetGoalTimePopup;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.roomorama.caldroid.CaldroidListener;
import com.roomorama.caldroid.CalendarHelper;
import com.ihateflyingbugs.vocaslide.R;

public class WillActivity extends FragmentActivity {

	private boolean undo = false;
	private CaldroidSampleCustomFragment caldroidFragment;
	private CaldroidDayFragment caldroidDayFragment;
	
	private TextView weekTv;
	private TextView monthTv;
	
	private TextView tvMyAverage;
	private TextView tvUserGroupAverage;
	private TextView tvGradeAverage;
	
	private TextView will_pushTv;
	private TextView block_kakaoTv;
	
	private DBPool db;

	TextView more_info_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_will);
		
		db = DBPool.getInstance(getApplicationContext());
		
		weekTv = (TextView) findViewById(R.id.week_average);
		monthTv = (TextView) findViewById(R.id.month_average);
		tvMyAverage = (TextView) findViewById(R.id.tv_my_average);
		tvUserGroupAverage = (TextView) findViewById(R.id.tv_user_group_average);
		tvGradeAverage = (TextView) findViewById(R.id.tv_grade_average);
		
		will_pushTv = (TextView) findViewById(R.id.will_pushTv);
		block_kakaoTv = (TextView) findViewById(R.id.block_kakaoTv);
		more_info_tv = (TextView) findViewById(R.id.more_info);
		more_info_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),
						WillCoachActivity.class);
				startActivity(i);
			}
		});

		final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");

		ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();

		caldroidFragment = new CaldroidSampleCustomFragment();
		caldroidDayFragment = new CaldroidDayFragment();

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction t = fragmentManager.beginTransaction();
		t.replace(R.id.calendar1, caldroidFragment);
		t.replace(R.id.day1, caldroidDayFragment);
		t.commit();
		fragmentManager.executePendingTransactions();

		caldroidDayFragment.setCaldoridFragment(caldroidFragment);

		Button bt_will_settime = (Button) findViewById(R.id.bt_will_settime);
		bt_will_settime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(WillActivity.this,
						SetGoalTimePopup.class));
			}
		});

		final CaldroidListener caldroidListener = new CaldroidListener() {
			public CaldroidGridAdapter adapter;

			protected void getAdapter() {
				adapter = caldroidFragment.getDatePagerAdapters().get(0);
			}

			@Override
			public void onSelectDate(Date date, View view, int type) {

				Log.d("month", "123123 date" + date);

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);

				DateTime currentDay = adapter.getCurrentDay();

				int currentTimeMillis = (currentDay.getYear() * 12)
						+ currentDay.getMonth();
				int nextTimeMillis = calendar.get(Calendar.YEAR) * 12
						+ calendar.get(Calendar.MONTH) + 1;
				Log.d("timezone", "current " + currentTimeMillis);
				Log.d("timezone", "next " + nextTimeMillis);

				if (currentTimeMillis == nextTimeMillis) {
					adapter.setCurrentDay(date);
				} else if (currentTimeMillis > nextTimeMillis) {
					adapter.setCurrentDay(date);
					if (type == 0 || type == 2) {
						caldroidFragment.prevMonth();
					}
				} else {
					adapter.setCurrentDay(date);
					if (type == 0 || type == 2) {
						caldroidFragment.nextMonth();
					}
				}
				Log.d("a", adapter.getCurrentDay().toString());

				DateTime currnetTime =  CalendarHelper.convertDateToDateTime(date);

				String dateStr = db.dateTimeToString(currnetTime);
				
				tvMyAverage.setText(Integer.toString(db.getWeekAverageTime(dateStr)));
				
				caldroidDayFragment.setDays(adapter.getCurrentDay());
				caldroidFragment.refreshView();
			}

			@Override
			public void onChangeMonth(int month) {
				Log.d("month", "123123 month" + month);

			}

			@Override
			public void onLongClickDate(Date date, View view) {
				Log.d("month", "123123 longclick" + date);
			}

			@Override
			public void onCaldroidViewCreated() {
				Log.d("month", "123123 created");
				getAdapter();
			}

		};

		caldroidFragment.setCaldroidListener(caldroidListener);
		
		Date current = new Date();
		DateTime currnetTime = CalendarHelper.convertDateToDateTime(current);
				
		String date = db.dateTimeToString(currnetTime);
		
		Log.d("DateTimeToString", date);
		
		tvMyAverage.setText(Integer.toString(db.getWeekAverageTime(date)));
		
		weekTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DateTime currnetTime =  caldroidDayFragment.getCurrentDay();

				String date = db.dateTimeToString(currnetTime);
				
				Log.d("DateTimeToString", "Week " + date);
				tvMyAverage.setText(Integer.toString(db.getWeekAverageTime(date)));
			}
		});
		
		monthTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DateTime currnetTime =  caldroidDayFragment.getCurrentDay();

				String date = db.dateTimeToString(currnetTime);
				
				Log.d("DateTimeToString", "Month " + date);
				tvMyAverage.setText(Integer.toString(db.getMonthAverageTime(date)));
			}
		});

		will_pushTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(
						WillActivity.this,
						com.ihateflyingbugs.vocaslide.popup.OneButtonPopUp.class);
				i.putExtra("title", 0);
				startActivity(i);
			}
		});
		
		block_kakaoTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(
						WillActivity.this,
						com.ihateflyingbugs.vocaslide.popup.OneButtonPopUp.class);
				i.putExtra("title", 1);
				startActivity(i);
			}
		});

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	/**
	 * Save current states of the Caldroid here
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);

		// if (caldroidFragment != null) {
		// caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
		// }
	}
}
