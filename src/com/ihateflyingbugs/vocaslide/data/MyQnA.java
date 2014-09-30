package com.ihateflyingbugs.vocaslide.data;

public class MyQnA {
	int num;
	String user_id;
	String p_number;
	String title;
	String q_date ;
	String question;	
	String a_flag;
	String answer ;
	String a_date;
	public MyQnA(){

	}

	public MyQnA(int num, String id, String title, String q_Date, String question, String flag){
		this.num = num;
		this.user_id = id;
		this.title = title;
		this.q_date = q_Date;
		this.question = question;
		this.a_flag = flag;
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

	}public String getdAnswer(){
		return a_date;

	}

}
