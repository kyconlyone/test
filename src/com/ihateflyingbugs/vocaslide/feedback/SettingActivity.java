package com.ihateflyingbugs.vocaslide.feedback;

import com.ihateflyingbugs.vocaslide.ChangeNameActivity;
import com.ihateflyingbugs.vocaslide.NoticeActivity;
import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.UseActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class SettingActivity extends FragmentActivity implements OnClickListener {
	private LinearLayout noticeLayout;
	private LinearLayout provisionLayout;
	private LinearLayout updateLayout;
	private LinearLayout changeNameLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		noticeLayout = (LinearLayout) findViewById(R.id.notice_layout);
		provisionLayout = (LinearLayout) findViewById(R.id.provision_layout);
		updateLayout = (LinearLayout) findViewById(R.id.update_layout);
		changeNameLayout = (LinearLayout) findViewById(R.id.change_name_layout);

		noticeLayout.setOnClickListener(this);
		provisionLayout.setOnClickListener(this);
		updateLayout.setOnClickListener(this);
		changeNameLayout.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = null;
		Fragment newFragment = null;
		switch (v.getId()) {
		case R.id.notice_layout:
			Log.d("NoticeClick", "공지사항");
			intent = new Intent(SettingActivity.this, NoticeActivity.class);
			break;
		case R.id.provision_layout:
			Log.d("NoticeClick", "약관");
			intent = new Intent(SettingActivity.this, UseActivity.class);
			break;
		case R.id.update_layout:
			Uri uri = Uri.parse("market://details?id=com.ihateflyingbugs.vocaslide");
			intent = new Intent(Intent.ACTION_VIEW, uri);
			break;
		case R.id.change_name_layout:
			intent = new Intent(SettingActivity.this, ChangeNameActivity.class);
			break;
		default:

			break;
		}
		if (intent != null) {
			startActivity(intent);
		}
	}
}
