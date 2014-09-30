/*
 *  associated with 'Async_send_indiv_ques.java'
 */

package com.ihateflyingbugs.vocaslide;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class UploadIndivQues  {


	public static void uploadIndivQues(JSONObject json){
		Log.e("Upload indivQues", "Upload indivQues");
		try{

			JSONArray num 		= json.getJSONArray("Num");	
			JSONArray user_id	= json.getJSONArray("User_ID");
			JSONArray title		= json.getJSONArray("Title");
			JSONArray q_date 	= json.getJSONArray("Q_Date");
			JSONArray question 	= json.getJSONArray("Question");	
			JSONArray a_flag	= json.getJSONArray("A_Flag");
			JSONArray a_date 	= json.getJSONArray("A_Date");
			JSONArray answer 	= json.getJSONArray("Answer");

			for(int i=0; i<num.length(); i++){
				Log.e("print test: Num",		"Num : " + num.optInt(i));
				Log.e("print test: User_ID",	"User_ID: " + user_id.optString(i));
				Log.e("print test: Title", 		"Title : " + title.optString(i));
				Log.e("print test: Q_Date",		"Q_Date : " + q_date.optString(i));
				Log.e("print test: Question",	"Question : " + question.optString(i));	//int�몄� �뺤씤�대씪!!
				Log.e("print test: A_Flag",		"A_Flag : " + a_flag.optString(i));
				Log.e("print test: A_Date", 	"A_Date : " + a_date.optString(i));
				Log.e("print test: Answer",		"Answer : " + answer.optString(i));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}

















