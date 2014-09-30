package com.ihateflyingbugs.vocaslide.data;

import org.json.JSONArray;

public class FAQ {
	int num;
	String p_number;
	String title;
	String q_date ;
	String question;	
	String a_flag;
	String answer ;
	String a_date;
	

	public FAQ(int num, String q_Date, String question,String a_date,String answer){
		this.num = num;
		this.q_date = q_Date;
		this.question = question;
		this.a_date = a_date;
		this.answer = answer;
	}

	public void setA_flag(String a_flag){
		this.a_flag = a_flag;
	}
	public void setAnswer(String answer, String Adate){
		this.answer = answer;
		this.a_date = Adate;
	}

	public String getTitle(){
		return title;

	}
	public String getQdate(){
		return q_date;

	}
	public String getQuestion(){
		return question;

	}
	public String getAflag(){
		return a_flag;

	}
	public String getAnswer(){
		return answer;

	}
	public String getAdate(){
		return a_date;

	}

}
