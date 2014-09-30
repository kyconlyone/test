package com.caldroidsample;

import hirondelle.date4j.DateTime;

import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.VOCAconfig;
import com.ihateflyingbugs.vocaslide.data.DBPool;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

public class CaldroidSampleCustomAdapter extends CaldroidGridAdapter {
	
	private DBPool db;

	public CaldroidSampleCustomAdapter(Context context, int month, int year,
			HashMap<String, Object> caldroidData,
			HashMap<String, Object> extraData) {
		super(context, month, year, caldroidData, extraData);
		db = DBPool.getInstance(VOCAconfig.getGlobalApplicationContext());
		Log.d("TimeData", "Create CustomAdapter");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View cellView = convertView;
		
		Log.d("TimeData", "!!!!!!!!!!!!!!");
		
		// For reuse
		if (convertView == null) {
			cellView = inflater.inflate(R.layout.custom_cell, null);
		}

//		int topPadding = cellView.getPaddingTop();
//		int leftPadding = cellView.getPaddingLeft();
//		int bottomPadding = cellView.getPaddingBottom();
//		int rightPadding = cellView.getPaddingRight();

		TextView textView = (TextView) cellView.findViewById(R.id.tvDay);
		ImageView imageDay = (ImageView) cellView.findViewById(R.id.imageDay);

		// Get dateTime of this cell
		DateTime dateTime = this.datetimeList.get(position);
		Resources resources = context.getResources();
		
		String dateStr = dateToString(dateTime);
		
		if(dateStr.equals(dateToString(getToday()))){ // 데이터베이스에 오늘 날짜의 정보 삽입
			db.putCalendarData(dateStr);
		}
		
		int[] timeData = db.getCalendarTimeData(dateStr);
		
		if (timeData != null) {
			Log.d("TimeData", " " + timeData[0] + " " + timeData[1]);

			if (timeData[0] != 0) {
				float result = (float) timeData[0] / timeData[1];
				if (result > 0 && result < 1) {
					imageDay.setImageResource(R.drawable.halfstudy);
				} else if (result >= 1 && result < 2) {
					imageDay.setImageResource(R.drawable.fullstudys);
				} else if (result >= 2) {
					imageDay.setImageResource(R.drawable.doublestudy);
				}
			} else {
				imageDay.setImageResource(R.drawable.nostudy);
			}
		}
		
		
		textView.setTextColor(Color.BLACK);

		// Set color of the dates in previous / next month
		if (dateTime.getMonth() != month) {
			textView.setTextColor(resources
					.getColor(R.color.caldroid_darker_gray));
			//cellView.setVisibility(View.INVISIBLE);
		}

		boolean shouldResetDiabledView = false;
		boolean shouldResetSelectedView = false;

		// Customize for disabled dates and date outside min/max dates
		if ((minDateTime != null && dateTime.lt(minDateTime))
				|| (maxDateTime != null && dateTime.gt(maxDateTime))
				|| (disableDates != null && disableDatesMap
						.containsKey(dateTime))) {
		
			textView.setTextColor(CaldroidFragment.disabledTextColor);
			if (CaldroidFragment.disabledBackgroundDrawable == -1) {
				//cellView.setBackgroundResource(R.drawable.disable_cell);
			} else {
				textView.setBackgroundResource(CaldroidFragment.disabledBackgroundDrawable);
			}

			if (dateTime.equals(getCurrentDay())) {
				textView.setBackgroundResource(R.drawable.red_border_gray_bg);
				//cellView.setVisibility(View.GONE);
//				currentTv = cellView;
			}
		} else {
			shouldResetDiabledView = true;
		}

		// Customize for selected dates
		if (selectedDates != null && selectedDatesMap.containsKey(dateTime)) {
			if (CaldroidFragment.selectedBackgroundDrawable != -1) {
				textView.setBackgroundResource(CaldroidFragment.selectedBackgroundDrawable);
			} else {
				textView.setBackgroundColor(resources
						.getColor(R.color.caldroid_sky_blue));
			}

			textView.setTextColor(CaldroidFragment.selectedTextColor);
		} else {
			shouldResetSelectedView = true;
		}

		if (shouldResetDiabledView && shouldResetSelectedView) {
			// Customize for CurrentDay
			if (dateTime.equals(getCurrentDay()) && !dateTime.equals(getToday())) {
				cellView.setBackgroundResource(R.drawable.gray_border);
				//textView.setTextColor(Color.WHITE);
				currentTv = textView;
			}else if(dateTime.equals(getCurrentDay()) && dateTime.equals(getToday())){
				cellView.setBackgroundResource(R.drawable.red_border_gray_bg);
			}else if(dateTime.equals(getToday())){
				cellView.setBackgroundResource(R.drawable.red_border);
				//textView.setTextColor(Color.WHITE);
			}else {
				cellView.setBackgroundResource(R.drawable.cell_bg);
				//cellView.setTextColor(Color.BLACK);
			}
		}

		textView.setText("" + dateTime.getDay());

		// Somehow after setBackgroundResource, the padding collapse.
		// This is to recover the padding
//		cellView.setPadding(leftPadding, topPadding, rightPadding,
//				bottomPadding);

		// Set custom color if required
//		setCustomResources(dateTime, cellView, tv1);

		return cellView;
	}
	
	private String dateToString(DateTime dateTime){
		
		String year, month, day;
	
		year = dateTime.getYear().toString();
		if(dateTime.getMonth() < 10){
			month = "0" + dateTime.getMonth().toString();
		}else{
			month = dateTime.getMonth().toString();
		}
		if(dateTime.getDay() < 10){
			day = "0" + dateTime.getDay().toString();
		}else{
			day = dateTime.getDay().toString();
		}
		
		return year+month+day;
	}
}
