package com.ihateflyingbugs.vocaslide.popup;

import com.ihateflyingbugs.vocaslide.Preferaance;
import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.data.Config;
import com.ihateflyingbugs.vocaslide.login.MainActivitys;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

public class SetGoalTimePopup extends Activity {


	RadioGroup radioGroup;
	private static SharedPreferences mPreference;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_goaltime);



		mPreference =  getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);

		radioGroup = (RadioGroup)findViewById(R.id.rg_settime);

		switch (mPreference.getInt(Config.STUDY_GOAL_TIME, 15)) {
		case 10:
			radioGroup.check(R.id.radio_10minute);
			break;
		case 15:
			radioGroup.check(R.id.radio_15minute);
			break;
		case 30:
			radioGroup.check(R.id.radio_30minute);
			break;
		default:
			radioGroup.check(R.id.radio_15minute);
			break;
		}


		Button bt_close_popup = (Button)findViewById(R.id.bt_close_popup);

		bt_close_popup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				switch (radioGroup.getCheckedRadioButtonId()) {
				case R.id.radio_10minute:
					mPreference.edit().putInt(Config.STUDY_GOAL_TIME, 10).commit();
					break;
				case R.id.radio_15minute:
					mPreference.edit().putInt(Config.STUDY_GOAL_TIME, 15).commit();
					break;
				case R.id.radio_30minute:
					mPreference.edit().putInt(Config.STUDY_GOAL_TIME, 30).commit();
					break;
				default:
					mPreference.edit().putInt(Config.STUDY_GOAL_TIME, 15).commit();
					break;
				}

				finish();

			}
		});


	}


}
