package com.ihateflyingbugs.vocaslide.login;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gcm.GCMRegistrar;
import com.ihateflyingbugs.vocaslide.CommonUtilities;
import com.ihateflyingbugs.vocaslide.Get_my_uuid;
import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.SideActivity;
import com.ihateflyingbugs.vocaslide.AsyncTask.Async_login;
import com.ihateflyingbugs.vocaslide.data.Config;
import com.ihateflyingbugs.vocaslide.struct.KakaoLink;

public class MainActivitys  {

	public static SharedPreferences mPreference;
	public static final String preName = "remember_voca";

	public static String FpreToken = "V_UserToken";

	public static final String GpreID	= "v_id";
	public static final String GpreEmail = "v_email";
	public static final String GpreCul = "v_cul";
	public static final String GprePass = "v_pass";
	public static final String GpreLevel = "v_level";
	public static final String GpreLevelCounting = "v_level_count";
	public static final String GpreTutorial = "tutorial";
	public static final String GpreReviewTutorial = "reviewtutorial";
	public static final String GpreTime = "time";
	public static final String GpreFinishTime = "finishtime";
	public static final String GpreGCM = "gcm";
	public static final String GpreTopic = "topic";
	public static final String GpreFirtst = "firtst";
	
	public static final String GpreName = "name";
	public static final String GpreGender = "gender";
	public static final String GpreBirth = "birth";
	public static final String GpreSchool = "school";

	public static int GpreAccessDuration=0;
	public static final String GpreEndTime = "endTime";
	public static final String GpreTotalReviewCnt = "totalReviewCnt";	//전체 복습할 갯수
	public static final String GpreTodayReviewCnt = "todayReviewCnt";	//오늘 복습한 갯수
	public static final String GpreTodayLearnCnt = "todayLearnCnt";		//새로 외운 단어 갯수
	public static boolean GpreFlag=true;
	public static final String M_DATE = "mdate";
	

	public static String pre_email;
	public static String pre_pass;
	public static String pre_level;

	public final static int Sign_requestCode = 1;
	final static int Fsign_requestCode = 2;


	public static UUID savedUuid;
	private static int check_login =0;
	EditText UserEmail;
	EditText UserPassword;

	boolean namecard = false;
	Intent intent;

	static ProgressDialog mProgress;
	static boolean loadingFinished = true;
	AlertDialog.Builder alert;
	Button SignIn;
	private static Handler mhandler;

	private static Activity thisActivity;
	private static Context context;

	public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	static boolean flag_activity = false;
	
	static SharedPreferences settings;



}