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
	
	public String var;          // ë³?ˆ˜ëª?
	public String value;      // ê°?
	
	// ?ì„±??
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
    //  ?ì„±??
    //-------------------------- 
    public HttpPost(ArrayList<HttpQue> _sBuffer) {
          sBuffer = _sBuffer;

          rBuffer = new StringBuilder(20000);
          rString = "";
    }
    
    //--------------------------
    //   URL ?¤ì •?˜ê³  ?‘ì†?˜ê¸°
    //--------------------------
    public void HttpPostData() {
    	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
    	try {
    		
    		URL url = new URL(sBuffer.get(0).value);       // URL ?¤ì •
    		HttpURLConnection http = (HttpURLConnection) url.openConnection();   // ?‘ì†
    		
            
    		//--------------------------
    		// ?„ì†¡ ëª¨ë“œ ?¤ì • - ê¸°ë³¸?ì¸ ?¤ì •?´ë‹¤
    		//--------------------------
    		http.setConnectTimeout(5000);  // 5ì´?
    		http.setReadTimeout(10000) ;  // 10ì´?
    		http.setDefaultUseCaches(false);
    		http.setDoInput(true);                         // ?œë²„?ì„œ ?½ê¸° ëª¨ë“œ ì§? •
    		http.setDoOutput(true);                        // ?œë²„ë¡??°ê¸° ëª¨ë“œ ì§? •
    		http.setRequestMethod("POST");    // ?„ì†¡ ë°©ì‹??POST
    		// ?œë²„?ê²Œ ?¹ì—??<Form>?¼ë¡œ ê°’ì´ ?˜ì–´??ê²ƒê³¼ ê°™ì? ë°©ì‹?¼ë¡œ ì²˜ë¦¬?˜ë¼??ê±??Œë ¤ì¤?‹¤
    		http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
    		
    		//--------------------------
    		// ?œë²„ë¡?ê°??„ì†¡
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
            // ?œë²„?ì„œ ?„ì†¡ë°›ê¸°
            //--------------------------
            
            //InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "EUC-KR");
            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
            
            BufferedReader reader = new BufferedReader(tmp);
            String str;
            while ((str = reader.readLine()) != null) {  // ?œë²„?ì„œ ?¼ì¸?¨ìœ„ë¡?ë³´ë‚´ì¤?ê²ƒì´ë¯?¡œ ?¼ì¸?¨ìœ„ë¡??½ëŠ”??
            	rBuffer.append(str + "\n");
            }
            rString = rBuffer.toString().trim();        // ?„ì†¡ê²°ê³¼ë¥?ë¬¸ì?´ë¡œ
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
