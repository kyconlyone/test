package com.ihateflyingbugs.vocaslide.struct;

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
//HttpPost
//--------------------------
public class HttpPost {
	
	public  String   rString;                         // Receive String;
    public  StringBuilder  rBuffer;                // Receive Buffer
    private ArrayList<HttpQue>  sBuffer;      // SendBuffer

    //-------------------------- 
    //  ������
    //-------------------------- 
    public HttpPost(ArrayList<HttpQue> _sBuffer) {
          sBuffer = _sBuffer;

          rBuffer = new StringBuilder(20000);
          rString = "";
    }
    
    //--------------------------
    //   URL �����ϰ� �����ϱ�
    //--------------------------
    public void HttpPostData() {
    	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
    	try {
    		
    		URL url = new URL(sBuffer.get(0).value);       // URL ����
    		HttpURLConnection http = (HttpURLConnection) url.openConnection();   // ����
            
    		//--------------------------
    		// ���� ��� ���� - �⺻���� �����̴�
    		//--------------------------
    		http.setConnectTimeout(5000);  // 5��
    		http.setReadTimeout(10000) ;  // 10��
    		http.setDefaultUseCaches(false);
    		http.setDoInput(true);                         // �������� �б� ��� ����
    		http.setDoOutput(true);                        // ������ ���� ��� ����
    		http.setRequestMethod("POST");    // ���� ����� POST
    		// �������� ������ <Form>���� ���� �Ѿ�� �Ͱ� ���� ������� ó���϶�� �� �˷��ش�
    		http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
    		
    		//--------------------------
    		// ������ �� ����
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
            // �������� ���۹ޱ�
            //--------------------------
            
            //InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "EUC-KR");
            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
            
            BufferedReader reader = new BufferedReader(tmp);
            String str;
            while ((str = reader.readLine()) != null) {  // �������� ���δ����� ������ ���̹Ƿ� ���δ����� �д´�
            	rBuffer.append(str + "\n");
            }
            rString = rBuffer.toString().trim();        // ���۰���� ���ڿ���
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
