package com.ihateflyingbugs.timer;

import org.andlib.ui.PercentView;

import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.util.Log;

public class CustomTimer {

	int timer_sec = 20;
	int percentage = 0;
	PercentView percentView1;
	CountDownTimer cTimer;
	SharedPreferences timerPreference;
	SharedPreferences.Editor timerEditor;

	public CountDownTimer timer(SharedPreferences timerPreference,
			final PercentView percentView) {

		Log.d("Timer", "timer()");
		percentage = timerPreference.getInt("percentage", 0);

		Log.d("Timer", "Time = " + Integer.toString(timer_sec));

		// time초 동안 time/100초 마다 onTick 실행
		CountDownTimer cTimer = new CountDownTimer(timer_sec * 1000,
				(timer_sec * 1000) / 100) {

			// (time * 1000) / 100마다 실행
			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub

				percentView.setPercentage(percentage++, true);
			}

			// 종료시 실행
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				percentView.setPercentage(100, true);
			}
		};

		return cTimer;
	}

	public void pause(CountDownTimer cTimer) {

		Log.d("Timer", "pause()");

		timerEditor = timerPreference.edit();
		timerEditor.putInt("percentage", percentage);
		timerEditor.commit();

		cTimer.cancel();
	}
}
