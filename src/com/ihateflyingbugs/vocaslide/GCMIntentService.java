package com.ihateflyingbugs.vocaslide;

import android.app.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gcm.*;
import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.data.Config;
import com.ihateflyingbugs.vocaslide.login.MainActivitys;
import com.ihateflyingbugs.vocaslide.tutorial.SplashActivity;

import static com.ihateflyingbugs.vocaslide.CommonUtilities.SENDER_ID;
import static com.ihateflyingbugs.vocaslide.CommonUtilities.displayMessage;

public class GCMIntentService extends GCMBaseIntentService {

	final static int LOG_OUT = 0;

	private static final String TAG = "GCMIntentService";

	public GCMIntentService() {
		super(SENDER_ID);
	}

	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
		displayMessage(context, "Your device registred with GCM");

		// Log.d("NAME", VOCAconfig.name);
		ServerUtilities.register(context, registrationId);

	}

	/**
	 * Method called on device un registred
	 * */
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		ServerUtilities.unregister(context, registrationId);
	}

	/**
	 * Method called on Receiving a new message
	 * */
	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message");
		String message = intent.getExtras().getString("price");
		// int sort = intent.getExtras().getInt("sort");
		int sort = Integer.parseInt((intent.getExtras().getString("sort")));
		displayMessage(context, message);
		// notifies user
		generateNotification(context, sort, message);
	}

	/**
	 * Method called on receiving a deleted message
	 * */
	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		int sort = 1;
		// notifies user
		generateNotification(context, sort, message);
	}

	/**
	 * Method called on Error
	 * */
	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		return super.onRecoverableError(context, errorId);
	}

	static SharedPreferences settings;

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification(Context context, int sort,
			String message) {
		int icon = R.drawable.icon48;
		long when = System.currentTimeMillis();
		String msg;

		Log.d("GCM", message + " " + sort);
		Intent notificationIntent = null;
		// sort = 0;
		if (sort == LOG_OUT) {
			msg = "다른 기기에서 로그인 되었습니다.";
		} else if (sort == 1) {
			msg = "밀당영단어의 업데이트가 있습니다.";
			Uri uri = Uri
					.parse("market://details?id=com.ihateflyingbugs.vocaslide");
			notificationIntent = new Intent(Intent.ACTION_VIEW, uri);
		} else if (sort == 2) {
			msg = sort + "번 Push Message";
		} else if (sort == 3) {
			msg = sort + "번 Push Message";
		} else if (sort == 4) {
			msg = sort + "번 Push Message";
		} else if (sort == 5) {
			msg = sort + "번 Push Message";
		} else if (sort == 6) {
			msg = sort + "번 Push Message";
		} else if (sort == 7) {
			msg = sort + "번 Push Message";
		} else if (sort == 8) {
			msg = sort + "번 Push Message";
		} else if (sort == 9) {
			msg = sort + "번 Push Message";
		} else {
			msg = message;
			notificationIntent = new Intent(context, SplashActivity.class);
		}

		// https://play.google.com/store/apps/details?id=com.ihateflyingbugs.vocaslide
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification(icon, msg, when);

		String title = context.getString(R.string.app_name);

		// notificationIntent = new Intent(context, SplashActivity.class);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		notification.contentView = new RemoteViews(context.getPackageName(),
				R.layout.notification);
		notification.contentView.setTextViewText(R.id.tv_noti_title, "밀당 영단어");
		notification.contentView.setTextViewText(R.id.tv_noti_message, msg);

		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);

		notification.setLatestEventInfo(context, title, msg, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;

		// Vibrate if vibrate is enabled
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);

		if (sort == LOG_OUT) {
			deleteMySharedPreferences(MainActivitys.GpreEmail, context);
			deleteMySharedPreferences(MainActivitys.GprePass, context);
			settings = context.getSharedPreferences(Config.PREFS_NAME,
					Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.remove(MainActivitys.GpreEmail);
			editor.remove(MainActivitys.GprePass);
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}

	private static void deleteMySharedPreferences(String _key, Context context) {
		if (MainActivitys.mPreference == null) {
			MainActivitys.mPreference = context.getSharedPreferences(
					MainActivitys.preName, MODE_WORLD_READABLE
							| MODE_WORLD_WRITEABLE);
		}
		SharedPreferences.Editor editor = MainActivitys.mPreference.edit();
		editor.remove(_key);
		editor.commit();
	}

}