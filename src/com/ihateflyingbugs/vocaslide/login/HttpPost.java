package com.ihateflyingbugs.vocaslide.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.os.*;
import android.util.Log;




//-------------------------- 
//HttpQue
//--------------------------
class HttpQue {
	
	public String var;          // �?���?
	public String value;      // �?
	
	// ?�성??
	public HttpQue(String _var, String _value) {
		var = _var;
		value = _value;
	}
}// HttpQue


//-------------------------- 
//HttpPost
//--------------------------
class HttpPost {
	
	public  String   rString;                         // Receive String;
    public  StringBuilder  rBuffer;                // Receive Buffer
    private ArrayList<HttpQue>  sBuffer;      // SendBuffer

    //-------------------------- 
    //  ?�성??
    //-------------------------- 
    public HttpPost(ArrayList<HttpQue> _sBuffer) {
          sBuffer = _sBuffer;

          rBuffer = new StringBuilder(20000);
          rString = "";
    }
    
    //--------------------------
    //   URL ?�정?�고 ?�속?�기
    //--------------------------
    public void HttpPostData() {
    	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
    	try {
    		
    		URL url = new URL(sBuffer.get(0).value);       // URL ?�정
    		HttpURLConnection http = (HttpURLConnection) url.openConnection();   // ?�속
    		
            
    		//--------------------------
    		// ?�송 모드 ?�정 - 기본?�인 ?�정?�다
    		//--------------------------
    		http.setConnectTimeout(5000);  // 5�?
    		http.setReadTimeout(10000) ;  // 10�?
    		http.setDefaultUseCaches(false);
    		http.setDoInput(true);                         // ?�버?�서 ?�기 모드 �?��
    		http.setDoOutput(true);                        // ?�버�??�기 모드 �?��
    		http.setRequestMethod("POST");    // ?�송 방식??POST
    		// ?�버?�게 ?�에??<Form>?�로 값이 ?�어??것과 같�? 방식?�로 처리?�라??�??�려�?��
    		http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
    		
    		//--------------------------
    		// ?�버�?�??�송
    		//--------------------------
    		StringBuffer buffer = new StringBuffer();
    		for (int i = 1; i < sBuffer.size(); i++) {
    			buffer.append(sBuffer.get(i).var).append("=");
    			buffer.append(sBuffer.get(i).value).append("&");
    		}
    		
            //OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "EUC-KR");
    		OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
    		
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();
            // Log.v("Http post", buffer.toString());
            
            //--------------------------
            // ?�버?�서 ?�송받기
            //--------------------------
            
            //InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "EUC-KR");
            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
            
            BufferedReader reader = new BufferedReader(tmp);
            String str;
            while ((str = reader.readLine()) != null) {  // ?�버?�서 ?�인?�위�?보내�?것이�?�� ?�인?�위�??�는??
            	rBuffer.append(str + "\n");
            }
            rString = rBuffer.toString().trim();        // ?�송결과�?문자?�로
            // Log.v("Receive String", rString);
            
            } catch (MalformedURLException e) {
            	Log.v("MalformedURLException error", "--------------");
            	rString = "N/A";
            } catch (IOException e) {
            	Log.v("IOException", rString + "--------------");
            	rString = "N/A";
            } // try
	} // HttpPostData
} // class
