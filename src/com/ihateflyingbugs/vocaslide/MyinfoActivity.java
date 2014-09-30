package com.ihateflyingbugs.vocaslide;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.vocaslide.login.MainActivitys;
import com.ihateflyingbugs.vocaslide.tutorial.MainActivity;
import com.kakao.APIErrorResult;
import com.kakao.LogoutResponseCallback;
import com.kakao.Session;
import com.kakao.SessionCallback;
import com.kakao.UserManagement;
import com.kakao.exception.KakaoException;

public class MyinfoActivity extends Activity implements OnClickListener {

	EditText et_my_name, et_my_birth, et_my_school, et_my_email;
	Button bt_change, bt_edit_logout, bt_secession;
	private Context mContext;
	SharedPreferences mPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myinfo);

		mContext = getApplicationContext();
		mPreference = mContext.getSharedPreferences(MainActivitys.preName,
				mContext.MODE_WORLD_READABLE | mContext.MODE_WORLD_WRITEABLE);

		et_my_name = (EditText) findViewById(R.id.et_my_name);
		et_my_birth = (EditText) findViewById(R.id.et_my_birth);
		et_my_school = (EditText) findViewById(R.id.et_my_school);
		et_my_email = (EditText) findViewById(R.id.et_my_email);

		bt_change = (Button) findViewById(R.id.bt_change);
		bt_edit_logout = (Button) findViewById(R.id.bt_edit_logout);
		bt_secession = (Button) findViewById(R.id.bt_secession);

		InputMethodManager imm = (InputMethodManager) getApplicationContext()
				.getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromInputMethod(et_my_name.getWindowToken(), 0);

		bt_change.setOnClickListener(this);
		bt_edit_logout.setOnClickListener(this);
		bt_secession.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_change:

			break;
		case R.id.bt_edit_logout:
			deleteMySharedPreferences(MainActivitys.GprePass);
			onClickLogout();
			FlurryAgent.logEvent(
					"SideActivity_EditUserInfoFragment:ClickLogout", true);

			break;
		case R.id.bt_secession:

			break;
		default:
			break;
		}
	}

	private void onClickLogout() {
		UserManagement.requestLogout(new LogoutResponseCallback() {
			@Override
			protected void onSuccess(final long userId) {

				redirectLoginActivity();
			}

			@Override
			protected void onFailure(APIErrorResult errorResult) {
				// TODO Auto-generated method stub
				redirectLoginActivity();
			}
		});
	}

	protected void redirectLoginActivity() {
		// TODO Auto-generated method stub
		Session.initializeSession(getApplicationContext(),
				new SessionCallback() {

					@Override
					public void onSessionOpened() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSessionClosed(KakaoException exception) {
						// TODO Auto-generated method stub

					}
				});
		finish();
		MainActivity.finish_logout();
	}

	private void deleteMySharedPreferences(String _key) {
		if (mPreference == null) {
			mPreference = mContext.getSharedPreferences(MainActivitys.preName,
					mContext.MODE_WORLD_READABLE
							| mContext.MODE_WORLD_WRITEABLE);
		}
		SharedPreferences.Editor editor = mPreference.edit();
		editor.remove(_key);
		editor.commit();
	}
}
