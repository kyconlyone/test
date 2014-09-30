package com.ihateflyingbugs.vocaslide.login;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gcm.GCMRegistrar;
import com.ihateflyingbugs.vocaslide.CommonUtilities;
import com.ihateflyingbugs.vocaslide.Get_my_uuid;
import com.ihateflyingbugs.vocaslide.MyTalkHttpResponseHandler;
import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.AsyncTask.Async_check_id;
import com.ihateflyingbugs.vocaslide.AsyncTask.Async_MakeAccount;
import com.ihateflyingbugs.vocaslide.AsyncTask.VocaCallback;
import com.ihateflyingbugs.vocaslide.data.Config;
import com.ihateflyingbugs.vocaslide.data.Date;
import com.ihateflyingbugs.vocaslide.tutorial.ChoiceTypeActivity;
import com.kakao.APIErrorResult;
import com.kakao.KakaoTalkProfile;
import com.kakao.KakaoTalkService;
import com.kakao.MeResponseCallback;
import com.kakao.UserManagement;
import com.kakao.UserProfile;



public class WriteUserInfoActivity extends Activity {
	final static int fb_requestCode = 0;

	protected static int check_id =0;
	protected static int check_make_account =0;


	private static SharedPreferences mPreference;



	static String name;
	static String birth;

	CheckBox cb_utilization;
	CheckBox cb_collection;

	RadioGroup rg;
	RadioButton rb_male;
	RadioButton rb_female;

	static int sex;

	static ProgressDialog mProgress;
	static boolean loadingFinished = true;
	AlertDialog.Builder alert;


	EditText et_name ;
	EditText et_birth;

	Button confirm;


	String collection;
	String utilization;

	private static Activity thisActivity = null;


	private static Context context;


	static String user_pass;
	static String email;
	String phonenum;
	String device;
	
	static Handler handler;

	public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	EditText et_UserEmail ;

	TextView tv_colloection;
	TextView tv_utilization;
	ImageView iv_userinfo_picture;

	static Map<String, String> choiceTypeParams = new HashMap<String, String>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		
		setContentView(R.layout.activity_signin_edit);
		context = getApplicationContext();
		thisActivity =this;
		handler = new Handler();

//		try{
//			User_id = getIntent().getExtras().getString("id");
//			user_pass = getIntent().getExtras().getString("password");
//		}catch(NullPointerException e ){
//			getMySharedPreferences(MainActivitys.GpreEmail);
//			getMySharedPreferences(MainActivitys.GprePass);
//		}

		et_UserEmail = (EditText) findViewById(R.id.tv_input_email);
		cb_utilization = (CheckBox)findViewById(R.id.cb_utilization);
		cb_collection = (CheckBox)findViewById(R.id.cb_collection);
		tv_colloection = (TextView)findViewById(R.id.tv_collection);
		tv_utilization = (TextView)findViewById(R.id.tv_utilization);
		iv_userinfo_picture = (ImageView)findViewById(R.id.iv_userinfo_picture);

		et_name = (EditText) findViewById(R.id.tv_add_name);
		et_birth = (EditText) findViewById(R.id.tv_add_birth);


		rg = (RadioGroup)findViewById(R.id.rg_sex);
		rb_male = (RadioButton)findViewById(R.id.rb_male);
		rb_female = (RadioButton)findViewById(R.id.rb_female);

		confirm = (Button) findViewById(R.id.buttonConfirm);

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());

		readProfile();

	}

	Bitmap bitmap;
	static String User_id=null;

	private String thumbnailURL;
	public void readProfile() {
		

		
		UserManagement.requestMe(new MeResponseCallback() {
			
			@Override
			protected void onSuccess(UserProfile userProfile) {
				// TODO Auto-generated method stub
		            if (userProfile != null)
		                userProfile.saveUserToCache();
				Log.e("kakao_info", "userid  =  "+userProfile.getId() );
				User_id = String.valueOf(userProfile.getId());
				Config.USER_ID  =User_id;
				setMySharedPreferences(MainActivitys.GpreID, User_id);
				
			}
			
			@Override
			protected void onSessionClosedFailure(APIErrorResult errorResult) {
				// TODO Auto-generated method stub
			}
			
			@Override
			protected void onNotSignedUp() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected void onFailure(APIErrorResult errorResult) {
				// TODO Auto-generated method stub
				
			}
		});
		
		 KakaoTalkService.requestProfile(new MyTalkHttpResponseHandler<KakaoTalkProfile>() {
		        @Override
		        protected void onHttpSuccess(final KakaoTalkProfile talkProfile) {
		              final String nickName = talkProfile.getNickName();
		              final String profileImageURL = talkProfile.getProfileImageURL();
		              final String thumbnailURL = talkProfile.getThumbnailURL();
		              final String countryISO = talkProfile.getCountryISO();
		              // display
		              
		              setMySharedPreferences(MainActivitys.GpreName, nickName);
		              setMySharedPreferences("image", thumbnailURL);
		              readPicture();
		        }
		    });
		   
		

		
		// load image
	}
	public void readPicture(){
		thumbnailURL = getMySharedPreferences("image");
		Thread threadi= new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				FileOutputStream out = null;
				try {
					URL url = new URL(thumbnailURL);
					URLConnection conn = url.openConnection();
					conn.connect();// url연결
					BufferedInputStream bis = new BufferedInputStream(
							conn.getInputStream()); // 접속한 url로부터 데이터값을 얻어온다
					Bitmap bm = BitmapFactory.decodeStream(bis);// 얻어온 데이터 Bitmap 에저장

					String path = Config.DB_FILE_DIR;
					File file = new File(path);
					if(!file.exists() && !file.mkdirs())
						file.mkdir();
					file= new File(path, Config.PROFILE_NAME);
					out = new FileOutputStream(file);
					bm.compress(Bitmap.CompressFormat.PNG, 90, out);
					bis.close();// 사용을 다한 BufferedInputStream 종료
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					try{
						out.close();
						getPicture();
					} catch(Throwable ignore) {}
				}
			}
		});
		threadi.start();
		
	}
	public void getPicture(){
		
		Thread thread= new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				String path = Config.DB_FILE_DIR;

				File directory = new File (path);

				File file = new File(directory, Config.PROFILE_NAME); //or any other format supported

				FileInputStream streamIn;

				try {
					streamIn = new FileInputStream(file);
					bitmap = BitmapFactory.decodeStream(streamIn); //This gets the image
					streamIn.close();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				if(bitmap!=null){
					bitmap = roundBitmap(bitmap);
				}


				//					url = new URL(thumbnailURL);
				//					URLConnection conn = url.openConnection();
				//					conn.connect();// url연결
				//					bis = new BufferedInputStream(
				//							conn.getInputStream()); // 접속한 url로부터 데이터값을 얻어온다
				//					bm = BitmapFactory.decodeStream(bis);// 얻어온 데이터 Bitmap 에저장
				//					bm = roundBitmap(bm);
				//					bis.close();// 사용을 다한 BufferedInputStream 종료


				handler.post(new Runnable() {
					@Override
					public void run() {
						try {
							if(!bitmap.equals(null)){
								iv_userinfo_picture.setImageBitmap(bitmap);
							}
						} catch (NullPointerException e) {
							// TODO: handle exception
							iv_userinfo_picture.setImageResource(R.drawable.my_btn_photo_default);
						}
					}
				});
			}
		});
		thread.start();
	}
	
	public Bitmap roundBitmap(Bitmap source) {
		int w = source.getWidth();
		int h = source.getHeight();
		Bitmap rounder = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(rounder);
		Path mPath = new Path();
		BitmapDrawable drawable = new BitmapDrawable(source);
		drawable.setBounds(0, 0, w, h);
		canvas.translate(0, 0);
		mPath.reset();
		canvas.clipPath(mPath);
		mPath.addCircle(w/2, h/2, w/2, Path.Direction.CCW);
		canvas.clipPath(mPath, Region.Op.REPLACE);
		drawable.draw(canvas);

		return rounder;
	}
	

	private boolean isFinish = false;
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isFinish = false;
		confirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 占쌘듸옙 占쏙옙占?占쌨소듸옙 占쏙옙占쏙옙

				
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(et_UserEmail.getWindowToken(), 0);
				email = null;
				email = et_UserEmail.getText().toString();

				

				email = et_UserEmail.getText().toString();
				phonenum = GetMyPhoneNumber();
				device = GCMRegistrar.getRegistrationId(getApplicationContext());
				name = et_name.getText().toString();
				birth = et_birth.getText().toString();

				Log.e("leveltest", "device  :  " +device);
				if (device.equals("")) {
					// Registration is not present, register now with GCM    
					GCMRegistrar.register(getApplicationContext(), CommonUtilities.SENDER_ID);
					device = GCMRegistrar.getRegistrationId(getApplicationContext());
					Log.e("leveltest", "device null");
				}
				Log.e("leveltest", "after   device  :  " +device);


				Pattern pattern = Pattern.compile(EMAIL_PATTERN);
				
				if (!pattern.matcher(email).matches()) {
					Toast.makeText(
							WriteUserInfoActivity.this,
							"이메일 형식이 올바르지 않습니다.",
							Toast.LENGTH_LONG).show();
					return;
				}else if(email.length()>40||email.length()<8){
					Toast.makeText(
							WriteUserInfoActivity.this,
							getString(R.string.error_length_email),Toast.LENGTH_LONG).show();
					return;
				}

				if(!(cb_collection.isChecked()&&cb_utilization.isChecked())){
					Toast.makeText(WriteUserInfoActivity.this,
							"약관에 동의하지 않으셨습니다", Toast.LENGTH_SHORT).show();
					return;
				}



				

				if(rg.getCheckedRadioButtonId()==R.id.rb_male){
					sex = 1;
				}else{
					sex = 2;
				}


				if(name.length()<2||name.length()>15){
					Toast.makeText(
							WriteUserInfoActivity.this,
							"이름은 2~15자 까지만 가능합니다",Toast.LENGTH_LONG).show();
					return;

				}
				

				if (birth.length() != 6) {
					Toast.makeText(WriteUserInfoActivity.this,
							"생년월일은 년월일(ex. 880502)",
							Toast.LENGTH_LONG).show();
					return;
				}


				mProgress = ProgressDialog.show(WriteUserInfoActivity.this, "Loading", "Please wait for a moment...");
				if (name != null && birth !=null&&email != null) {
					List<String> data = new ArrayList<String>();
					
					
					Log.e("leveltest", device);
					data.add(User_id);
					data.add(Get_my_uuid.get_Device_id(context));
					data.add(phonenum);
					data.add(device);
					data.add(name);
					data.add(birth);
					data.add(email);
					data.add(String.valueOf(sex));
					
					MakeAccount(data);
					
				}


			}
		});

		
		tv_colloection.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WriteUserInfoActivity.this,
						DocPopup.class);
				intent.putExtra("title",1);
				startActivity(intent);
			}
		});
		tv_utilization.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WriteUserInfoActivity.this,
						DocPopup.class);
				intent.putExtra("title",2);
				startActivity(intent);
			}
		});

	}
	
	protected void MakeAccount(List<String> data) {
		// TODO Auto-generated method stub
		new Async_MakeAccount(data, WriteUserInfoActivity.this, new VocaCallback() {
			
			@Override
			public void Resonponse(JSONObject response) {
				// TODO Auto-generated method stub
				
				int state = 0;
				try {
					state = response.getInt(Config.TAG_SUCCESS);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				check_make_account = state;
				onPageFinished();
				switch (check_make_account) {
				case Async_MakeAccount.MAKE_SUCCESS:
					Log.e("webtest", "MAKE_SUCCESS");
					
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub

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
							boolean flag_year = false;


							Log.e("age1", "나이는 :  "+age);

//							if( age == 14){
//								level = 1;
//								flag_year = true;
//							}else if(age == 15){
//								level = 4;
//								flag_year = true;
//							}else if(age == 16){
//								level = 7;
//								flag_year = true;
//							}else if(age < 14){
//								level = 1;
//								flag_year = false;
//							}else if(age > 16){
//								level = 8;
//								flag_year = false;
//							}else{
//								level = 0;
//							}
//
//							if(flag_year){
//								if(Integer.valueOf(TimeClass.getMonth())>6){
//									level++;
//								}
//							}

							
							Log.e("age1", "레벨은 " + String.valueOf(level));
							setMySharedPreferences(MainActivitys.GpreID, User_id);
							setMySharedPreferences(MainActivitys.GpreEmail, email);
							setMySharedPreferences(MainActivitys.GpreGender, String.valueOf(sex));
							setMySharedPreferences(MainActivitys.GpreBirth, birth);
							setMySharedPreferences(MainActivitys.GpreName, name);
							
							
								byte gender =com.flurry.android.Constants.UNKNOWN;
								String getGender = String.valueOf(sex);
								if(getGender.equals("2")){
									gender = com.flurry.android.Constants.FEMALE;
								}else if(getGender.equals("1")){
									gender = com.flurry.android.Constants.MALE;
								}
								FlurryAgent.setGender(gender);
								String Birth = birth;
								FlurryAgent.setAge(Config.getAge(Birth));
								
							
							

//							//?덈꺼 ?ㅼ젙 遺?텇?낅땲??;
//							setMySharedPreferences(MainActivitys.GpreLevel, String.valueOf(level));

							
							
							Toast.makeText(thisActivity, "로그인 성공", Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(context, ChoiceTypeActivity.class);
							intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("activity", 1);
							context.startActivity(intent);
							thisActivity.finish();
						}
					});
					break;
				case Async_MakeAccount.MAKE_FAIL_MATCH:
					Log.e("webtest", "MAKE_FAIL_NOT_EXIST");
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							FlurryAgent.logEvent("JoinActivity:JoinFail_Unmatched");
							Toast.makeText(thisActivity, "일치하지 않습니다 .. ? 뭐가 ", Toast.LENGTH_SHORT).show();
						}
					});
					break;
				}
				
			}
			
			@Override
			public void Exception() {
				// TODO Auto-generated method stub
				Log.e("webtest", "MAKE_EXCEPTION");

				FlurryAgent.logEvent("JoinActivity:JoinFail_NoServerResponse");
				Toast.makeText(thisActivity, "알수없는 에러", Toast.LENGTH_SHORT).show();

			
			}
		}).execute();
	}
	

	private static void setMySharedPreferences(String _key, String _value) {
		if(mPreference == null){
			mPreference = context.getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
		}  
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putString(_key, _value);
		editor.commit();
	}
	private String getMySharedPreferences(String _key) {
		if(mPreference == null){
			mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
		}
		return mPreference.getString(_key, "");
	}

	public static void onPageFinished() {
		if(loadingFinished){
			if(null !=mProgress) {
				if(mProgress.isShowing()) {
					mProgress.dismiss();
				}
			}

		}
	}

	private long mLastBackPressedTime = 0;

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Calendar now = Calendar.getInstance();
		long nowTime = now.getTimeInMillis();

		Toast.makeText(getApplicationContext(), "뒤로버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
		if(nowTime - mLastBackPressedTime <= 2000){
			isFinish = true;
//			moveTaskToBack(true);
//			handler.postDelayed(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					if(isFinish){
						finish();
//					}
//				}
//			},15000);
		}
		mLastBackPressedTime = nowTime;	
	}
	

	private String GetMyPhoneNumber() {
		TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String phoneNum = manager.getLine1Number();
		if(phoneNum ==null){
			return "";
		}
		try {
			if(phoneNum.subSequence(0, 3).equals("+82")){
				phoneNum = phoneNum.replace("+82", "0");
			}
		} catch (StringIndexOutOfBoundsException e) {
			// TODO: handle exception
			phoneNum = "01000000000";
		}
		return phoneNum;
	}
	
	String starttime;
	String startdate;
	Date date = new Date();
	Map<String, String> articleParams ;
	public void onStart()
	{

		super.onStart();
		articleParams = new HashMap<String, String>();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("JoinActivity", articleParams);

		// your code
	}

	public void onStop()
	{
		super.onStop();
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
		    // Do whatever
			articleParams.put("WIFI", "On");
		}else{
			articleParams.put("WIFI", "Off");
		}
		
		FlurryAgent.endTimedEvent("JoinActivity:Start");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
		// your code
	}

}


