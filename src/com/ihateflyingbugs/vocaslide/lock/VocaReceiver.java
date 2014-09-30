package com.ihateflyingbugs.vocaslide.lock;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.ihateflyingbugs.vocaslide.service.DBService;
import com.ihateflyingbugs.vocaslide.LockActivity;

public class VocaReceiver extends BroadcastReceiver  {
	public static boolean wasScreenOn = true;
	public static AlarmManager alarm;
	@Override
	public void onReceive(Context context, Intent intent) {

		//Toast.makeText(mContext, "" + "enterrrrrr", Toast.LENGTH_SHORT).show();
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			Toast.makeText(context, "" + "screeen off", Toast.LENGTH_SHORT).show();
			Log.v("kjw", "ACTION_SCREEN_OFF");
			wasScreenOn = false;
			Intent intent11 = new Intent(context, LockActivity.class);
			intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			context.startActivity(intent11);

			
			// do whatever you need to do here
			//wasScreenOn = false;
		} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

			Log.v("kjw", "ACTION_SCREEN_ON");
			wasScreenOn = true;
			Intent intent11 = new Intent(context, LockActivity.class);
			intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			//mContext.startActivity(intent11);
			Toast.makeText(context, "" + "start activity", Toast.LENGTH_SHORT).show();
			// and do whatever you need to do here
			// wasScreenOn = true;
		}
		else if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
		{
			/*  	KeyguardManager.KeyguardLock k1;
        	KeyguardManager km =(KeyguardManager)mContext.getSystemService(mContext.KEYGUARD_SERVICE);
            k1 = km.newKeyguardLock("IN");
            k1.disableKeyguard();
			 */
//			Calendar cal = Calendar.getInstance();
//			cal.add(Calendar.SECOND, 10);
//			
//			Intent intent12 = new Intent(mContext, DBService.class);
//			PendingIntent pintent = PendingIntent.getService(mContext, 0, intent12, 0);
//			AlarmManager alarm = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
//			
//			alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
//					AlarmManager.INTERVAL_HOUR, pintent);
//			mContext.startService(new Intent(mContext, DBService.class));
			
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, 10);
//			
			Log.e("alma", "ACTION_BOOT_COMPLETED");
			Intent intent12 = new Intent(context, DBService.class);
			PendingIntent pintent = PendingIntent.getService(context, 0, intent12, 0);
			AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			
			alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
					AlarmManager.INTERVAL_HOUR, pintent);
			
			Log.e("alma", "ACTION_BOOT_COMPLETED");
			
			if(false){
				Intent intent11 = new Intent(context, LockActivity.class);

				intent11.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent11);
			}
			
			

			//  Intent intent = new Intent(mContext, LockPage.class);
			//  mContext.startActivity(intent);
			//  Intent serviceLauncher = new Intent(mContext, UpdateService.class);
			//  mContext.startService(serviceLauncher);
			//  Log.v("TEST", "Service loaded at start");
		}

	}


}
