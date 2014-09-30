package com.caldroidsample.day;

import hirondelle.date4j.DateTime;

import java.util.Date;

import android.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.roomorama.caldroid.CalendarHelper;

public class DayFragmentAdapter extends FragmentPagerAdapter {

	private DayFragment[] contents;

	public DayFragmentAdapter(android.support.v4.app.FragmentManager fm) {
		super(fm);
		// Calendar cal = Calendar.getInstance();
		Date date = new Date();
		DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
		contents = new DayFragment[3];
		
		DateTime days[] = new DateTime[3];
		
		days[0] = dateTime.minusDays(1);
		days[1] = dateTime;
		days[2] = dateTime.plusDays(1);
		
		for (int i = 0; i < contents.length; i++) {
			contents[i] = new DayFragment(days[i]);
//			contents[i].onCreateView(getLayoutInflater(), container, savedInstanceState)
		}
		notifyDataSetChanged();
//		contents[0] = DayFragment.newInstance(today);
//		contents[1] = DayFragment.newInstance(nextDay);
//		contents[2] = DayFragment.newInstance(prevDay);
		
	}

//	public void refreshAdapter(int nextPosition) {
//		for (int i = 0; i < contents.length; i++) {
//			if (nextPosition == 0) {
//				contents[i].minusDay();
//				Log.d("Date", "Refresh "+contents[i].getDate());
//			} else if (nextPosition == 2) {
//				contents[i].plusDay();
//			}
//		}
//		notifyDataSetChanged();
//	}
	
	public void refreshAdapter(DateTime dateTime){
		DateTime days[] = new DateTime[3];
		days[0] = dateTime.minusDays(1);
		days[1] = dateTime;
		days[2] = dateTime.plusDays(1);
		
		for (int i = 0; i < contents.length; i++) {
			contents[i].setDays(days[i]);
		}
	}
	
	public void setDays(DateTime dateTime){
		DateTime tempDateTime[] = new DateTime[3];
		
		tempDateTime[1] = dateTime;
		tempDateTime[0] = dateTime.minusDays(1);
		tempDateTime[2] = dateTime.plusDays(1);
		
		for (int i = 0; i < contents.length; i++) {
			contents[i].setDays(tempDateTime[i]);
		}
		
		notifyDataSetChanged();
	}
	
	public DateTime getDateTime(){
		return contents[1].getDateTime();
	}

	@Override
	public DayFragment getItem(int position) {
		// return HitTabFragment.newInstance(CONTENT[position % CONTENT.length]);
		return contents[position];
	}

	@Override
	public int getCount() {
		return contents.length;
	}

//	@Override
//	public CharSequence getPageTitle(int position) {
//		return TestFragmentAdapter.CONTENT[position % CONTENT.length];
//	}

//	public void setCount(int count) {
//		if (count > 0 && count <= 10) {
//			mCount = count;
//			notifyDataSetChanged();
//		}
//	}
}