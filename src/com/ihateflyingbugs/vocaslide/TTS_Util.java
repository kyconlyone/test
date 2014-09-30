package com.ihateflyingbugs.vocaslide;

import java.util.Locale;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.vocaslide.data.Config;


import android.app.Activity;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * �좎?��λ벝���좎룞�쇿?��?�챿���좎�紐??���좎룞���좎떩�듭?��좎뙇�먯??TextToSpeech �좎룞�?�뜝�숈?? * @author mailss
 */
public class TTS_Util {
	private TextToSpeech mTTS;							
	private static boolean isInit, isSupport;
	
	public TTS_Util(Context context){
		try {
			mTTS = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

				@Override
				public void onInit(int status) {
					// TODO Auto-generated method stub
					isInit = status == TextToSpeech.SUCCESS;
					
					mTTS.setEngineByPackageName("com.google.android.tts");
					mTTS.setLanguage(Locale.US);

					
					mTTS.setPitch(10/10.0f);
					mTTS.setSpeechRate(10/10.0f);		
				}
			});
		} 
		catch (Exception e) {
			// TODO: handle exception
			isInit = false;
		}
	}

	
	public boolean tts_check(){
	
		if(!isInit){
			isSupport = false;
			return false;
		}
		
		try {
			int available = mTTS.isLanguageAvailable(Locale.US);	
			if(available < 0) {					
				isSupport = false;
			}
			else isSupport = true;

			Log.e("tts_test", String.valueOf(isSupport));
		} catch (Exception e) {
			// TODO: handle exception
			isSupport = false;
		}
		
		return isSupport;
	}

	public void tts_reading(String text){
		
		if(!isInit){
			//Toast.makeText(this, R.string.msg_fail_init, Toast.LENGTH_SHORT).show();
		}else if(!tts_check()){	
			//Toast.makeText(getApplicationContext(), R.string.msg_not_support_lang, Toast.LENGTH_SHORT).show();
		}else {
			if(TextUtils.isEmpty(text)){		
				//Toast.makeText(this, R.string.msg_success_init, Toast.LENGTH_SHORT).show();
			}else {

				mTTS.setLanguage(Locale.US);
				mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
			
				Log.e("tts_test", text);
			}
		}

	}
	protected void onDestroy() {


		//Close the Text to Speech Library
		if(mTTS != null) {

			mTTS.stop();
			mTTS.shutdown();
			Log.d("tts_test", "TTS Destroyed");
		}
	}

	
}