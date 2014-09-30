package com.caldroidsample.day;

import hirondelle.date4j.DateTime;

import java.util.Calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.VOCAconfig;
import com.ihateflyingbugs.vocaslide.data.DBPool;

public class DayFragment extends Fragment {
	private TextView dayTextview;
	private TextView newStudyWord;
	private TextView reviewWord;
	private TextView goalTime;
	private TextView studyTime;
	private ImageView studyImage;

	Calendar mThisDayCalendar;
	DateTime currentDay;
	DBPool db;
	
	public DateTime getDateTime(){
		return currentDay;
	}
	

	public DayFragment(DateTime dateTime) {
		currentDay = dateTime;
		db = DBPool.getInstance(VOCAconfig.getGlobalApplicationContext());
		
		
		Log.d("DayText", "create : " + getDate());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.day_view, container, false);
		dayTextview = (TextView) view.findViewById(R.id.tvCurrentDay);
		newStudyWord = (TextView) view.findViewById(R.id.new_study_word);
		reviewWord = (TextView) view.findViewById(R.id.review_word);
		goalTime = (TextView) view.findViewById(R.id.real_use_time_countTv);
		studyTime = (TextView) view.findViewById(R.id.all_use_time_countTv);
		studyImage = (ImageView) view.findViewById(R.id.study_gaugeImg);
		Log.d("DayText", getDate());
		
		String year, month, day;
		
		year = currentDay.getYear().toString();
		if(currentDay.getMonth() < 10){
			month = "0" + currentDay.getMonth().toString();
		}else{
			month = currentDay.getMonth().toString();
		}
		if(currentDay.getDay() < 10){
			day = "0" + currentDay.getDay().toString();
		}else{
			day = currentDay.getDay().toString();
		}

		int[] timeData;
		int[] wordData;
		timeData = db.getCalendarTimeData(year+month+day);
		wordData = db.getCalendarCountData(year+month+day);
		
		//Log.d("dbdata", timeData.toString() + " " + wordData.toString());

		
		dayTextview.setText(getDate());
		
		if (wordData != null && timeData != null) {
			// Log.d("TimeData", " " + timeData[0] + " " + timeData[1]);
			Log.d("dbdata", year+month+day);
			Log.d("dbdata", timeData.toString());
			Log.d("dbdata", wordData.toString());
			
			newStudyWord.setText("" + wordData[0]);
			reviewWord.setText(wordData[1] + "/" + wordData[2]);
			studyTime.setText(timeData[0] + "");
			goalTime.setText("/" + timeData[1] + "분");

			if (timeData[0] != 0) {
				float result = (float) timeData[0] / timeData[1];
				if (result > 0 && result < 1) {
					studyImage.setImageResource(R.drawable.halfstudy);
				} else if (result >= 1 && result < 2) {
					studyImage.setImageResource(R.drawable.fullstudys);
				} else if (result >= 2) {
					studyImage.setImageResource(R.drawable.doublestudy);
				}
			} else {
				studyImage.setImageResource(R.drawable.nostudy);
			}
		}else {
			newStudyWord.setText("" + "0");
			reviewWord.setText("0" + "/" + "0");
			studyTime.setText("0" + "분");
			goalTime.setText("/" + "0" + "분");
			studyImage.setImageResource(0);
		}
		
		return view;
	}
	
	public void setDays(DateTime dateTime){
		currentDay = dateTime;
		dayTextview.setText(getDate());
		
		String year, month, day;
		
		year = currentDay.getYear().toString();
		if(currentDay.getMonth() < 10){
			month = "0" + currentDay.getMonth().toString();
		}else{
			month = currentDay.getMonth().toString();
		}
		if(currentDay.getDay() < 10){
			day = "0" + currentDay.getDay().toString();
		}else{
			day = currentDay.getDay().toString();
		}

		int[] timeData;
		int[] wordData;
		timeData = db.getCalendarTimeData(year+month+day);
		wordData = db.getCalendarCountData(year+month+day);
		
		//Log.d("dbdata", timeData.toString() + " " + wordData.toString());

		
		dayTextview.setText(getDate());
		
		if (wordData != null && timeData != null) {
			// Log.d("TimeData", " " + timeData[0] + " " + timeData[1]);
			Log.d("dbdata", year+month+day);
			Log.d("dbdata", timeData.toString());
			Log.d("dbdata", wordData.toString());
			
			newStudyWord.setText("" + wordData[0]);
			reviewWord.setText(wordData[1] + "/" + wordData[2]);
			studyTime.setText(timeData[0] + "");
			goalTime.setText("/" + timeData[1] + "분");

			if (timeData[0] != 0) {
				float result = (float) timeData[0] / timeData[1];
				if (result > 0 && result < 1) {
					studyImage.setImageResource(R.drawable.halfstudy);
				} else if (result >= 1 && result < 2) {
					studyImage.setImageResource(R.drawable.fullstudys);
				} else if (result >= 2) {
					studyImage.setImageResource(R.drawable.doublestudy);
				}
			} else {
				studyImage.setImageResource(R.drawable.nostudy);
			}
		}else {
			newStudyWord.setText("" + "0");
			reviewWord.setText("0" + "/" + "0");
			studyTime.setText("0" + "분");
			goalTime.setText("/" + "0" + "분");
			studyImage.setImageResource(0);
		}
	}
	
//	public void plusDay(){
//		currentDay = currentDay.plusDays(1);
//		dayTextview.setText(getDate());
//	}
//	
//	public void minusDay(){
//		currentDay = currentDay.minusDays(1);
//		dayTextview.setText(getDate());
//	}

	public String getDate() {
		int year = currentDay.getYear();
		int month = currentDay.getMonth();
		int day = currentDay.getDay();
		return year + "." + month + "." + day;
	}

}