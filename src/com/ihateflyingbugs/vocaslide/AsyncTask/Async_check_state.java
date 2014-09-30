package com.ihateflyingbugs.vocaslide.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.ihateflyingbugs.vocaslide.login.JSONParser;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class Async_check_state extends AsyncTask<String, String, String> {
	private static final String TAG_SUCCESS = "success";
	static final int STATE_EXCEPTION= 0;
	static final int STATE_ALL_FINISH= 1;
	static final int STATE_PAY_FINISH= 2;
	static final int STATE_NOTHING= 3;
	
	JSONParser jParser1 = new JSONParser();
	
	protected String User_phonenum;
	Context mcontext;
	
	public Async_check_state(String phonenum, Context context){
		User_phonenum = null;
		User_phonenum = phonenum;
		mcontext = context;
	}
	
	//?¨Í∏∞??Î≠?Î°úÎî©Ï§ëÏù¥?ºÎäî ?ôÍ??ôÍ? ?£Ïñ¥Ï£ºÎ©¥ ?úÎ? ?ã„Öã
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//mProgress = ProgressDialog.show(mcontext, "Loading", "Please wait for a moment...");
		
	}

	//DBÎ∞õÏïÑ?§Í∏∞
	protected String doInBackground(String... args) throws NullPointerException {
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("User", User_phonenum));
		
		

		try {
			JSONObject json1 = jParser1.makeHttpRequest("http://www.lnslab.com/vocaslide/check_state.php", "GET", params);
			int success = json1.getInt(TAG_SUCCESS);	//?¥Î©î??Í≤πÏπòÎ©?1??Î≥¥ÎÇ¥Í≥? ?àÍ≤πÏπòÎ©¥ 0??Î≥¥ÎÇºÍ≤?
			String message = json1.getString("message");	
			
			Log.e("listtest", String.valueOf(User_phonenum)+"      "+message+"      "+String.valueOf(success));
			
//			if (success ==  STATE_ALL_FINISH) {
//				IntroActivity.get_state(STATE_ALL_FINISH, message);
//				//Toast.makeText(mcontext, "?¨Ïö© Í∞?ä•???ÑÏù¥???ÖÎãà??, Toast.LENGTH_SHORT).show();
//			} else if(success == STATE_PAY_FINISH) {
//				IntroActivity.get_state(STATE_PAY_FINISH,message);
//				//Toast.makeText(mcontext, "?¥Î? ?àÎäî ?ÑÏù¥?îÏûÖ?àÎã§", Toast.LENGTH_SHORT).show();
//			}else if(success == STATE_NOTHING) {
//				IntroActivity.get_state(STATE_NOTHING,message);
//				//Toast.makeText(mcontext, "?¥Î? ?àÎäî ?ÑÏù¥?îÏûÖ?àÎã§", Toast.LENGTH_SHORT).show();
//			}else{
//				IntroActivity.get_state(STATE_EXCEPTION,message);
//				//Toast.makeText(mcontext, "Í≤?Ç¨???§Ìå®?òÏ??µÎãà??, Toast.LENGTH_SHORT).show();
//			}
		} catch (JSONException e) {
			Log.e("listtest", "JSONException      ");
			e.printStackTrace();
		}catch (RuntimeException e) {
			// TODO: handle exception
			Log.e("listtest", "RuntimeException");
		//	IntroActivity.get_state(STATE_EXCEPTION,"server error");
		}
		
		return null;
	}

	//?¨Í∏∞??UI ?ëÏóÖ?¥ÎÇò ?ÑÏóê Î°úÎî©Ï§??•Í??•Í? Í∑∏Í±∞ ?ÜÏï†Î≤ÑÎ¶º ???ã„Öã??
	protected void onPostExecute(String file_url) {
//		if(null !=mProgress) {
//			if(mProgress.isShowing()) {
//				mProgress.dismiss();
//			}
//		}
	}
	
}