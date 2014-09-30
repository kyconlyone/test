package com.ihateflyingbugs.vocaslide;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.drm.DrmStore.Action;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class MainActionbar  {

	ActionBar bar;
	Activity mActivity;
	public MainActionbar(ActionBar bar, Activity activity){
		this.bar = bar; 
		this.mActivity = activity;
	}
	
	private void makeActionBar(boolean isExam)
	{
//		bar = mActivity.getActionBar();
//
//		if(isExam)
//		{
//			actionbar_main = mActivity.getLayoutInflater().inflate(R.layout.main_action_bar_exam, null);
//		}
//		else
//		{
//			actionbar_main = mActivity.getLayoutInflater().inflate(R.layout.main_action_bar, null);
//		}
//
//
//		bar.setCustomView(actionbar_main, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
//		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//
//		chkSetting = (CheckBox)actionbar_main.findViewById(R.id.chkSetting);
//		btnKnown = (RadioButton)actionbar_main.findViewById(R.id.btnKnown);
//		btnScore = (RadioButton)actionbar_main.findViewById(R.id.btnScore);
//		chkMode = (CheckBox)actionbar_main.findViewById(R.id.chkMode);
//		tvTitle = (TextView)actionbar_main.findViewById(R.id.tvTitle);
//
//		chkSetting.setOnCheckedChangeListener(this);
//
//		btnKnown.setOnClickListener(this);
//		btnScore.setOnClickListener(this);
//		chkMode.setOnClickListener(this);
//	}
//	
	}
	

}
