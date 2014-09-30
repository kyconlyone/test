package com.ihateflyingbugs.vocaslide.service;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ihateflyingbugs.vocaslide.R;

public class ChatHeadService extends Service {

	private ImageView chatHead;
	private ImageView TrashHead;
	WindowManager.LayoutParams params;

	// Controls for animations
	private Timer 					mTrayAnimationTimer;
	private TrayAnimationTimerTask 	mTrayTimerTask;
	private Handler 				mAnimationHandler = new Handler();

	private WindowManager 				mWindowManager;			// Reference to the window
	private WindowManager.LayoutParams 	mRootLayoutParams;		// Parameters of the root layout
	private RelativeLayout 				mRootLayout;			// Root layout

	private static final int TRAY_HIDDEN_FRACTION 			= 7; 	// Controls fraction of the tray hidden when open
	private static final int TRAY_MOVEMENT_REGION_FRACTION 	= 7;	// Controls fraction of y-axis on screen within which the tray stays.
	private static final int ANIMATION_FRAME_RATE 			= 20;	// Animation frame rate per second.
	private static final int TRAY_DIM_X_DP 					= 48;	// Width of the tray in dps
	private static final int TRAY_DIM_Y_DP 					= 48; 	// Height of the tray in dps
	private static final int MARGIN_BOTTOM_DP 				= 25; 	// BOTTOM MARGIN of the tray in dps
	private static final int TRASH_AREA_DP 					= 98; 	// TRASH of AREA in dps
	private static final int IN_AREA_DP 					= 60; 	// TRASH of AREA in dps
	

	private boolean mIsTrayOpen = true;

	//Variables that control drag
	private int mStartDragX;
	//private int mStartDragY; // Unused as yet
	private int mPrevDragX;
	private int mPrevDragY;

	int Trash_startX ;
	int Trash_endX ;
	int Trash_height ;
	int width;
	int height;
	
	@Override public IBinder onBind(Intent intent) {
		// Not used
		return null;
	}

	@Override public void onCreate() {
		super.onCreate();

		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		chatHead = new ImageView(this);
		chatHead.setImageResource(R.drawable.icon48);
		
		Display display = ((WindowManager) this.getSystemService(getApplicationContext().WINDOW_SERVICE)).getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
		
		calcArea();
		
		TrashHead = new ImageView(this);
		TrashHead.setImageResource(R.drawable.icon96);
		WindowManager.LayoutParams Trashparams = new WindowManager.LayoutParams(
				Utils.dpToPixels(TRAY_DIM_X_DP, getResources()),
				Utils.dpToPixels(TRAY_DIM_Y_DP, getResources()),
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		
		Trashparams.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
		Trashparams.y = Utils.dpToPixels(MARGIN_BOTTOM_DP, getResources());
		Trashparams.alpha = 128;
		
		TrashHead.setVisibility(View.GONE);
		

		mRootLayoutParams = new WindowManager.LayoutParams(
				Utils.dpToPixels(TRAY_DIM_X_DP, getResources()),
				Utils.dpToPixels(TRAY_DIM_Y_DP, getResources()),
				WindowManager.LayoutParams.TYPE_PHONE, 
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE 
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, 
				PixelFormat.TRANSLUCENT);
		
		mRootLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
		
		params = new WindowManager.LayoutParams(
				Utils.dpToPixels(TRAY_DIM_X_DP, getResources()),
				Utils.dpToPixels(TRAY_DIM_Y_DP, getResources()),
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 100;

		mWindowManager.addView(chatHead, params);
		mWindowManager.addView(TrashHead, Trashparams);
		
		
		
		calcArea();
		chatHead.setOnTouchListener(new View.OnTouchListener() {
			private int initialX;
			private int initialY;
			private float initialTouchX;
			private float initialTouchY;

			@Override public boolean onTouch(View v, MotionEvent event) {
				final int action = event.getActionMasked();

				switch (action) {
				case MotionEvent.ACTION_DOWN:
//					initialX = params.x;
//					initialY = params.y;
//					initialTouchX = event.getRawX();
//					initialTouchY = event.getRawY();
//					return true;
				case MotionEvent.ACTION_UP:

					//return true;
				case MotionEvent.ACTION_MOVE:
//					params.x = initialX + (int) (event.getRawX() - initialTouchX);
//					params.y = initialY + (int) (event.getRawY() - initialTouchY);
//					windowManager.updateViewLayout(chatHead, params);
//					return true;
				case MotionEvent.ACTION_CANCEL:
					TrashHead.setVisibility(View.VISIBLE);
					dragTray(action, (int)event.getRawX(), (int)event.getRawY());
					return true;
				}
				return false;
			}
		});

	}

	public void calcArea(){
		Trash_startX = width/2 - Utils.dpToPixels(TRAY_DIM_X_DP, getResources());
		Trash_endX = width/2 + Utils.dpToPixels(TRAY_DIM_X_DP, getResources());
		Trash_height = height -  Utils.dpToPixels(TRASH_AREA_DP, getResources());
		
		Log.e("chathead", "Trash_startX            :  "+Trash_startX);
		Log.e("chathead", "Trash_endX              :  "+Trash_endX);
		Log.e("chathead", "Trash_height            :  "+Trash_height);
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		
		Display display = ((WindowManager) this.getSystemService(getApplicationContext().WINDOW_SERVICE)).getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
		calcArea();
		
		Log.e("chathead", "change");
		
	}	
	private void dragTray(int action, int x, int y){
		switch (action){
		case MotionEvent.ACTION_DOWN:

			
			// Cancel any currently running animations/automatic tray movements.
			if (mTrayTimerTask!=null){
				mTrayTimerTask.cancel();
				mTrayAnimationTimer.cancel();
			}

			// Store the start points
			mStartDragX = x;
			//mStartDragY = y;
			mPrevDragX = x;
			mPrevDragY = y;
			
			break;

		case MotionEvent.ACTION_MOVE:

			// Calculate position of the whole tray according to the drag, and update layout.
			float deltaX = x-mPrevDragX;
			float deltaY = y-mPrevDragY;
			mRootLayoutParams.x += deltaX;
			mRootLayoutParams.y += deltaY;
			mPrevDragX = x;
			mPrevDragY = y;
			mWindowManager.updateViewLayout(chatHead, mRootLayoutParams);
			if((x >Trash_startX&&x<Trash_endX)&&(y>Trash_height)){
				WindowManager.LayoutParams Inparams = new WindowManager.LayoutParams(
						Utils.dpToPixels(IN_AREA_DP, getResources()),
						Utils.dpToPixels(IN_AREA_DP, getResources()),
						WindowManager.LayoutParams.TYPE_PHONE,
						WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
						PixelFormat.TRANSLUCENT);

				Inparams.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
				Inparams.alpha = 128;
				Inparams.y = Utils.dpToPixels(MARGIN_BOTTOM_DP, getResources());

				Inparams.alpha = 128;

				mWindowManager.updateViewLayout(TrashHead, Inparams);
			}else{
				WindowManager.LayoutParams Outparams = new WindowManager.LayoutParams(
						Utils.dpToPixels(TRAY_DIM_X_DP, getResources()),
						Utils.dpToPixels(TRAY_DIM_Y_DP, getResources()),
						WindowManager.LayoutParams.TYPE_PHONE,
						WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
						PixelFormat.TRANSLUCENT);
				
				Outparams.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
				Outparams.y = Utils.dpToPixels(MARGIN_BOTTOM_DP, getResources());
				Outparams.alpha = 128;
				mWindowManager.updateViewLayout(TrashHead, Outparams);
			}
			break;

		case MotionEvent.ACTION_UP:
			
		case MotionEvent.ACTION_CANCEL:			
			if((x >Trash_startX&&x<Trash_endX)&&(y>Trash_height)){
				mWindowManager.removeView(chatHead);
				mWindowManager.removeView(TrashHead);
				Log.e("chathead", "trash success");
			}else{
				// When the tray is released, bring it back to "open" or "closed" state.
				if ((mIsTrayOpen && (x-mStartDragX)<=0) ||
						(!mIsTrayOpen && (x-mStartDragX)>=0))
					mIsTrayOpen = !mIsTrayOpen;

				mTrayTimerTask = new TrayAnimationTimerTask(x);
				mTrayAnimationTimer = new Timer();
				mTrayAnimationTimer.schedule(mTrayTimerTask, 0, ANIMATION_FRAME_RATE);
				TrashHead.setVisibility(View.GONE);
				
				WindowManager.LayoutParams Outparams = new WindowManager.LayoutParams(
						Utils.dpToPixels(TRAY_DIM_X_DP, getResources()),
						Utils.dpToPixels(TRAY_DIM_Y_DP, getResources()),
						WindowManager.LayoutParams.TYPE_PHONE,
						WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
						PixelFormat.TRANSLUCENT);
				
				Outparams.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
				Outparams.y = Utils.dpToPixels(MARGIN_BOTTOM_DP, getResources());
				Outparams.alpha = 128;
				mWindowManager.updateViewLayout(TrashHead, Outparams);
			}
			
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (chatHead != null) mWindowManager.removeView(chatHead);
	}

	//Timer for animation/automatic movement of the tray.
	private class TrayAnimationTimerTask extends TimerTask{

		// Ultimate destination coordinates toward which the tray will move
		int mDestX;
		int mDestY;

		public TrayAnimationTimerTask(int x){

			// Setup destination coordinates based on the tray state. 
			super();
			if(width/2>x){
				mDestX = -chatHead.getWidth()/TRAY_HIDDEN_FRACTION;
			}else{
				mDestX = width - chatHead.getWidth();
			}
			// Keep upper edge of the widget within the upper limit of screen
			int screenHeight = getResources().getDisplayMetrics().heightPixels;
			mDestY = Math.max(
					screenHeight/TRAY_MOVEMENT_REGION_FRACTION, 
					mRootLayoutParams.y);

			// Keep lower edge of the widget within the lower limit of screen
			mDestY = Math.min(
					((TRAY_MOVEMENT_REGION_FRACTION-1)*screenHeight)/TRAY_MOVEMENT_REGION_FRACTION - chatHead.getWidth(), 
					mDestY);
		}

		// This function is called after every frame.
		@Override
		public void run() {

			// handler is used to run the function on main UI thread in order to
			// access the layouts and UI elements.
			mAnimationHandler.post(new Runnable() {
				@Override
				public void run() {
					if(width/2>1){
						mRootLayoutParams.x = (2*(mRootLayoutParams.x-mDestX))/3 + mDestX;
						mRootLayoutParams.y = (2*(mRootLayoutParams.y-mDestY))/3 + mDestY;
						mWindowManager.updateViewLayout(chatHead, mRootLayoutParams);

						// Cancel animation when the destination is reached
						if (Math.abs(mRootLayoutParams.x-mDestX)<2 && Math.abs(mRootLayoutParams.y-mDestY)<2){
							TrayAnimationTimerTask.this.cancel();
							mTrayAnimationTimer.cancel();
						}
					}
					// Update coordinates of the tray
					
				}
			});
		}
	}


}