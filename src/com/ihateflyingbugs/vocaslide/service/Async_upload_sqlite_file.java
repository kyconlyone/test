package com.ihateflyingbugs.vocaslide.service;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.ihateflyingbugs.vocaslide.data.Config;
import com.ihateflyingbugs.vocaslide.login.JSONParser;
import com.ihateflyingbugs.vocaslide.login.MainActivitys;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;




public class Async_upload_sqlite_file extends AsyncTask<String, String, String>{

	//constant
	protected final int NEED_TO_UPLOAD=1;
	protected final int NEED_NOT_TO_UPLOAD=0;
	
	//upload info
	protected final String uploadFileName = "remember_voca.sqlite";													//file name
	protected final String upLoadServerUri = "https://hott.kr/lnslab_upload_sqlite_file/upload_sqlite_file.php";	//url
	
	//user info
	String ID = null;	//id
	String startDate = null;	//start date
	int serverResponseCode = 0;
	
	JSONParser jParser = new JSONParser();
	
	public Async_upload_sqlite_file(String start_date, Context context){
		//assign user info
		
		SharedPreferences mpreperence = context.getSharedPreferences(Config.PREFS_NAME, context.MODE_WORLD_READABLE|context.MODE_WORLD_WRITEABLE);
		ID = mpreperence.getString(MainActivitys.GpreID, "000000");
		this.startDate = start_date.replace(":", ";");
		Log.e("uploadFile","Async_upload_sqlite_file constructor");
		
	}
	
	@Override
	protected String doInBackground(String... args) {

		//add info to list params
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("ID",ID));	//user id
		
		//JSONObject json_version = jParser.makeHttpRequest("http://lnslab.com/vocaslide/Check_Version.php", "GET", params);
		JSONObject json_upload_flag = jParser.makeHttpRequest("http://lnslab.com/vocaslide/check_user_list_to_upload_sqlite_file.php", "GET", params);
		
		try {
			//요기 에러떴었음 유플러스하는중에 ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
			int upload_flag = json_upload_flag.getInt("upload_flag");	
			if (upload_flag ==  NEED_TO_UPLOAD) {
				Log.e("uploadFile","need to upload");
				uploadFile(Config.ExternalDirectory + "" + uploadFileName);
			}else{	//NEED_NOT_TO_UPLOAD
				Log.e("uploadFile","need not to upload");
				;
			}
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		// TODO Auto-generated method stub
		return null;
	}
	
	public int uploadFile(String sourceFileUri) {
        
  	  
  	  String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;  
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024; 
        File sourceFile = new File(sourceFileUri); 
        
        if (!sourceFile.isFile()) {
      	  	    
	           return 0;
         
        }
        else
        {
	           try { 
	        	   
	            	 // open a URL connection to the Servlet
	               FileInputStream fileInputStream = new FileInputStream(sourceFile);
	               URL url = new URL(upLoadServerUri);
	               
	               // Open a HTTP  connection to  the URL
	               conn = (HttpURLConnection) url.openConnection(); 
	               conn.setDoInput(true); // Allow Inputs
	               conn.setDoOutput(true); // Allow Outputs
	               conn.setUseCaches(false); // Don't use a Cached Copy
	               conn.setRequestMethod("POST");
	               conn.setRequestProperty("Connection", "Keep-Alive");
	               conn.setRequestProperty("ENCTYPE", "multipart/form-data");
	               conn.setRequestProperty("ExamContents-Type", "multipart/form-data;boundary=" + boundary);
	               conn.setRequestProperty("uploaded_file", fileName); 
	               
	               
	               dos = new DataOutputStream(conn.getOutputStream());
	     
	               dos.writeBytes(twoHyphens + boundary + lineEnd); 
	               dos.writeBytes("ExamContents-Disposition: form-data; name=\"uploaded_file\";filename=\""
	            		                     + fileName + "??" + ID + "??" + startDate + "\"" + lineEnd);
	               
	               dos.writeBytes(lineEnd);
	     
	               // create a buffer of  maximum size
	               bytesAvailable = fileInputStream.available(); 
	     
	               bufferSize = Math.min(bytesAvailable, maxBufferSize);
	               buffer = new byte[bufferSize];
	     
	               // read file and write it into form...
	               bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
	                 
	               while (bytesRead > 0) {
	            	   
	                 dos.write(buffer, 0, bufferSize);
	                 bytesAvailable = fileInputStream.available();
	                 bufferSize = Math.min(bytesAvailable, maxBufferSize);
	                 bytesRead = fileInputStream.read(buffer, 0, bufferSize);   
	                 
	                }
	     
	               // send multipart form data necesssary after file data...
	               dos.writeBytes(lineEnd);
	               dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	     
	               // Responses from the server (code and message)
	               serverResponseCode = conn.getResponseCode();
	               String serverResponseMessage = conn.getResponseMessage();
	                
	               Log.i("uploadFile", "HTTP Response is : " 
	            		   + serverResponseMessage + ": " + serverResponseCode);

	               Log.e("uploadFile", "upload end\n=======================");
	               //close the streams //
	               fileInputStream.close();
	               dos.flush();
	               dos.close();
	                
	          } catch (MalformedURLException ex) {
	              
	        	  ex.printStackTrace();
	              Log.e("uploadFile", "MalformedURLException occured");
	              
	          } catch (Exception e) {
	        	   
	              e.printStackTrace();
	              Log.e("uploadFile", "Exception occured");
	              
	          }
	         
	          return serverResponseCode; 
	          
        } // End else block 
	}
}
