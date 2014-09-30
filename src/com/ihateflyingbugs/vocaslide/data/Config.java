package com.ihateflyingbugs.vocaslide.data;

import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.login.TimeClass;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class Config {

	public static final String PREFS_NAME = "remember_voca";
	public static final String DB_NAME = "remember_voca.sqlite";
	public static final String Log_NAME = "remember_voca_log.txt";
	public static final String SLog_NAME = "DbService_log.txt";
//	public static final String DB_NAME = "Remember_Temp.sqlite";
	public static final String DB_USER_NAME = "local.sqlite";
	public static final String PROFILE_NAME = "profile.png";
	public static final String TAG_SUCCESS = "success";
	public static final String SYSTEM_NAME = "system_name";
	

	public static String USER_ID ;
	public static String USER_PASS;
	public static String USER_PHONENUM=null;

//	public static final String ExternalDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Aremember_voca/";
//	
//	public static final String DB_FILE_DIR = ExternalDirectory + "db/";

	public static final String ExternalDirectory = "/data/data/com.ihateflyingbugs.vocaslide/";

	public static final String DB_FILE_DIR = ExternalDirectory + "";

	public static final String DIFFICULTY = "DIFFICULTY"; 
	public static final String REVIEW_COUNT = "REVIEW_COUNT"; 
	public static final String NEW_COUNT = "NEW_COUNT"; 
	public static final String M_DATE = "M_DATE";
	
	public static final String STUDY_GOAL_TIME = "STUDY_GOAL_TIME";

	public static final int ONCE_WORD_COUNT = 20;
	public static int Difficulty;
	public static int WORD_TOPIC;
	public static int MAX_DIFFICULTY = 6;
	public static int MIN_DIFFICULTY = 1;

	public static final int Time_ONE_HOUR = 1;
	public static int CHANGE_LEVEL_COUNT;
	public static String VERSION;

	public static int FOR_DUPLICATION_CHECK = 1;
	public static int FOR_REGISTER_CHECK = 2;
	
	//word set log
	public static int wordSetCount=1;
	public static int unknw_to_knw=0;
	public static int new_to_unknw_to_knw=0;
	public static int new_to_knw=0;
	public static int knw_to_unknw_to_knw=0;
	public static int knw_to_knw=0;
	public static int knw_to_unknw=0;
	public static int new_to_unknw=0;
	public static int unknw_to_unknw=0;
	public static int new_to_new=0;
	public static int know_count=0;
	public static int unknow_count=0;

	
//	public static int mutex=0;
	
	public static String NAME;

	public static boolean checkNetwork(Context context, boolean isToastOn)
	{
		ConnectivityManager manager = (ConnectivityManager)context.getSystemService (Context.CONNECTIVITY_SERVICE);
//		Log.d("kja", "manager = " + manager + "   " + ConnectivityManager.TYPE_MOBILE + "    " + manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI));

		boolean isMobile;

		try
		{
			isMobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
		}catch(Exception e){isMobile = true;}


		boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

		if( isMobile == false && isWifi == false){
//			new AlertDialog.Builder(mContext)
//			.setTitle("°æ°í")
//			.setMessage(mContext.getResources().getString(R.string.network_error))
//			.setNeutralButton("È®ÀÎ", new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dlg, int sumthin) {
//					
//				}
//			}).show();
			if(isToastOn)
				Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();

			return false;
		}
		else
			return true;
	}
	
	
	static public String setFlurryKey(Context context){
		String USE_KEY ="";
		
		String USER_KEY ="YN8VNG7S368KB3W2G699";	//betaTest
		String TEST_KEY ="RR7TSFKW8632R7BCZKKC";	//TestVoca
		
		if(USER_PHONENUM == null){
			TelephonyManager manager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
			String phoneNum = manager.getLine1Number();
			if(phoneNum ==null){
				return "010";
			}
			
			try {
				if(phoneNum.subSequence(0, 3).equals("+82")){
					phoneNum = phoneNum.replace("+82", "0");
				}
			} catch (StringIndexOutOfBoundsException e) {
				// TODO: handle exception
				phoneNum = "01000000000";
			}
			USER_PHONENUM = phoneNum;
		}
		
		/*
		 * ¹èÀçÈ£ ÆÀÀå´Ô 01032263632
		 * ÃÖÀ±Á¤ »ç¿ø´Ô 01029460430
		 * ±è¿ëÈÆ »ç¿ø´Ô 01044961311
		 * ¹ÚÂù¿ë ´ëÇ¥´Ô 01071017935
		 * °­ÀÏ±¸ °³¹ßÀÚ 01036654711
		 * ¾È±¤¹Î »ç¿ø´Ô 01085353819
		 * Â¯Â¯°É           01072292908
		 * ¾È´ëÇö           01075771015
		 * ¼­°­¼®           01029460430
		 * 
		 */
		if(USER_PHONENUM.equals("01071950310")||USER_PHONENUM.equals("01032263632")||USER_PHONENUM.equals("01029460430")||USER_PHONENUM.equals("01044961311")||
				USER_PHONENUM.equals("01088255761")||USER_PHONENUM.equals("01036654711")||USER_PHONENUM.equals("01085353819")||USER_PHONENUM.equals("01072292908")
				||USER_PHONENUM.equals("01044961311")||USER_PHONENUM.equals("01071017935")||USER_PHONENUM.equals("01075771015")){
			USE_KEY = TEST_KEY;
		}else{
			USE_KEY = USER_KEY;
		}
		
		return USE_KEY;
		
	}
	
	static public int getAge(String birth){
		int sum=0;

		if(Integer.valueOf((String) birth.subSequence(0, 2))>50){
			sum = 1900;
			int years = Integer.valueOf((String) birth.subSequence(0, 2))-1;
			sum += years;
		}else{
			sum = 2000;
			int years = Integer.valueOf((String) birth.subSequence(0, 2))-1;
			sum += years;
		}

		int level = 0;
		int age = Integer.valueOf(TimeClass.getYear()) - sum;
		
		return age;
	}
	
	public static boolean isNetworkAvailable(Context context) {
		
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    Log.e("leveltest", ""+(activeNetworkInfo != null && activeNetworkInfo.isConnected()));
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	
	
	
}