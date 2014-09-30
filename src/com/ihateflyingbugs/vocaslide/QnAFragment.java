package com.ihateflyingbugs.vocaslide;


import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.AsyncTask.Async_send_inidv_ques;
import com.ihateflyingbugs.vocaslide.AsyncTask.VocaCallback;
import com.ihateflyingbugs.vocaslide.data.Config;
import com.ihateflyingbugs.vocaslide.data.Date;
import com.ihateflyingbugs.vocaslide.login.MainActivitys;
import com.ihateflyingbugs.vocaslide.tutorial.MainActivity;

import android.R.anim;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class QnAFragment extends Fragment implements OnClickListener{
	private EditText et_qna_email, et_qna_phone, et_qna_text;
	private Spinner sp_qna_title;
	private Button bt_qna_submit;
	
	private Activity thisActivity;
	
	private Handler handler;
	
	SharedPreferences mPreference;
	Context mContext;
	private ProgressBar pb_qna_splash;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		thisActivity = getActivity();
		mContext = getActivity().getApplicationContext();
		View view = inflater.inflate(R.layout.fragment_qna, container, false);
		
		handler= new Handler();
		et_qna_email = (EditText)view.findViewById(R.id.et_qna_email);
		et_qna_phone = (EditText)view.findViewById(R.id.et_qna_phone);
		et_qna_text = (EditText)view.findViewById(R.id.et_qna_text);
		
		sp_qna_title = (Spinner)view.findViewById(R.id.spinner1);
		
		bt_qna_submit= (Button)view.findViewById(R.id.bt_qna_submit);
		pb_qna_splash =  (ProgressBar) view.findViewById(R.id.pb_qna_splash);
		bt_qna_submit.setOnClickListener(this);
		
		if(getMySharedPreferences(MainActivitys.GpreEmail).length()>0){
			et_qna_email.setText(getMySharedPreferences(MainActivitys.GpreID));
		}
		
		
		return view;
	}
	
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SideActivity.setActionBar(false, "�����ϱ�");
	}


	private String getMySharedPreferences(String _key) {
		if(mPreference == null){
			mPreference =  mContext.getSharedPreferences(MainActivitys.preName, mContext.MODE_WORLD_READABLE|mContext.MODE_WORLD_WRITEABLE);
		}
		return mPreference.getString(_key, "");
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_qna_submit:
			pb_qna_splash.setVisibility(View.VISIBLE);
			String email = et_qna_email.getText().toString();
			String phone = et_qna_phone.getText().toString();
			String title = sp_qna_title.getSelectedItem().toString();
			String text = et_qna_text.getText().toString();
			
			try {
				if(text.length()<5){
					Toast.makeText(thisActivity, "������ 5�� �̻��̿��� �մϴ�.", Toast.LENGTH_SHORT).show();
					return;
				}
				FlurryAgent.logEvent("SideActivity_WriteQnAFragment:ClickSubmit");
				
				new Async_send_inidv_ques(email, phone, text, new VocaCallback() {

					@Override
					public void Resonponse(JSONObject response) {
						// TODO Auto-generated method stub
						int success = 0;
						String message = null;
						try {
							success = response.getInt(Config.TAG_SUCCESS);
							message = response.getString("message");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Log.e("webtest", "success : " + success);
						switch(success){
						case Async_send_inidv_ques.INDIVQNA_SUCCESS:
							Log.v("success", "SEND_SUCCESS");
							Log.e("webtest", message);
							handler.post(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub

									pb_qna_splash.setVisibility(View.GONE);
									new AlertDialog.Builder(thisActivity)
									.setTitle("���� ������ �����Ǿ����ϴ�.")
									.setMessage("�ִ��� �������� ���� \n�亯�帮���� �ϰڽ��ϴ�.\n�����մϴ�.")
									.setPositiveButton("Ȯ��", null).show();
									Toast.makeText(thisActivity, "���� �Ͻ� ������ ������ ���۵Ǿ����ϴ�.", Toast.LENGTH_SHORT).show();
								}
							});
							break;
						default:
							Toast.makeText(thisActivity, "������ ���� �Ͽ����ϴ�.", Toast.LENGTH_SHORT).show();
							break;

						}
					}

					@Override
					public void Exception() {
						Log.v("success", "SEND_ Exception(");
						// TODO Auto-generated method stub
						//Toast.makeText(thisActivity, "������ ���� �Ͽ����ϴ�.", Toast.LENGTH_SHORT).show();

					}
				}).execute();
				
			} catch (NullPointerException e) {
				// TODO: handle exception
				return;
			}

			
			break;
		default:
			break;
		}
	}
	
	String starttime;
	String startdate;
	Date date = new Date();
	Map<String, String> articleParams;
	
	public void onStart()
	{

		super.onStart();
		articleParams = new HashMap<String, String>();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("SideActivity_WriteQnAFragment", articleParams);
		// your code
	//	MainActivity.writeLog("[���ܾ� �ϱ�Ϸ� ����,FinishStudyFragment,1]\r\n");

	}

	public void onStop()
	{
		super.onStop();
		//FlurryAgent.endTimedEvent("WriteQnAFragment:Start");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		Log.e("splash", startdate+"        "+date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		// your code
	//	MainActivity.writeLog("[���ܾ� �ϱ�Ϸ� ��,FinishStudyFragment,1,{Start:"+articleParams.get("Start")+",End:"+articleParams.get("End")+"}]\r\n");
	}
}
