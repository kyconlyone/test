package com.ihateflyingbugs.vocaslide.tab;

import java.util.List;

import com.ihateflyingbugs.vocaslide.data.ExamContents;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HitTabAdapter extends FragmentPagerAdapter {
	
	
	List<ExamContents> list;
	
	public HitTabAdapter(FragmentManager fm, List<ExamContents> list) {
		super(fm);
		this.list = list; 
	}

	@Override
	public Fragment getItem(int position) {
		return HitTabFragment.newInstance(list.get(position % list.size()));
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return list.get(position % list.size()).getTitle();
	}

	@Override
	public int getCount() {
		return list.size();
	}

}
