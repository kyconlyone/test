package com.ihateflyingbugs.vocaslide.data;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.R.bool;
import android.util.Log;
import android.view.View;

public class Word {
	
	public final static int Class_N = 1;
	public final static int Class_V = 2;
	public final static int Class_A = 3;
	public final static int Class_Ad = 4;
	public final static int Class_Prep = 5;

	private int _id;
	
	private String word;
	
	public Timer timer;
	public TimerTask timer_task;
	public View view;
	public int index;
	
	
	private int p_class;
	private int difficulty;
	private int priority;
	
	private double score;
	private int state;
	private int time;
	private int frequency;
	
	
	private boolean isRight = false;
	private boolean isWrong = false;
	
	
	private int wrongCount = 0;
	
	
	boolean isShow_wc = false;
	
	private List<Mean> mean_list;
	private HashMap<Integer, Boolean> total_class;
	
	private int exState;
	
	public Word(int _id, String word,int p_class,int difficulty, int priority, double score, int state, int time, int frequency,boolean isThread){
		
		this._id = _id;
		
		this.word = word;
		
		this.p_class = p_class;
		this.difficulty = difficulty;
		this.priority = priority;
		
		this.score = score;
		this.state = state;
		this.time = time;
		this.frequency = frequency;
		
		mean_list = new ArrayList<Mean>();
		
		
	}
	
	public Word(int _id, String word,int p_class,int difficulty, int priority, double score, int state, int time, int frequency){
		
		this._id = _id;
		
		this.word = word;
		
		this.p_class = p_class;
		this.difficulty = difficulty;
		this.priority = priority;
		
		this.score = score;
		this.state = state;
		this.time = time;
		this.frequency = frequency;
		
		mean_list = new ArrayList<Mean>();
		
		timer = new Timer();
		
		
	}
	
	public Word(int _id, String word,int p_class,int difficulty, int priority, double score, int state, int time, int frequency,
			boolean isRight, boolean isWrong, int wrongCount){
		
		this._id = _id;
		
		this.word = word;
		
		this.p_class = p_class;
		this.difficulty = difficulty;
		this.priority = priority;
		
		this.score = score;
		this.state = state;
		this.time = time;
		this.frequency = frequency;
		
		this.isRight = isRight;
		this.isWrong = isWrong;
		this.wrongCount = wrongCount;
		
		mean_list = new ArrayList<Mean>();
		timer = new Timer();
	}
	
	public Word(int _id, String word,int p_class,int difficulty, int priority, double score, int state, int time, int frequency,
			boolean isRight, boolean isWrong, int wrongCount, int exState){
		
		this._id = _id;
		
		this.word = word;
		
		this.p_class = p_class;
		this.difficulty = difficulty;
		this.priority = priority;
		
		this.score = score;
		this.state = state;
		this.time = time;
		this.frequency = frequency;
		
		this.isRight = isRight;
		this.isWrong = isWrong;
		this.wrongCount = wrongCount;
		
		mean_list = new ArrayList<Mean>();
		timer = new Timer();
		this.exState = exState;
	}

	

	public int get_id(){
		return _id;
	}
	
	public int get(){
		return _id;
	}
	
	public String getWord(){
		return word;
	}
	
	public int getDifficulty(){
		return difficulty;
	}
	
	public int getP_class(){
		return p_class;
	}
	
	public double getScore(){
		return score;
	}
	
	public void setState(int state)
	{
		this.state = state;
	}
	
	public int getState(){
		return state;
	}
	
	public int getTime(int i){
		return time + i;
	}
	
	
	
	public int getNextTime(){
		if(time == 0)
			return 0;
		
		return ++time;
	}
	
	public int getFrequency(){
		
		//5_16 주석처리함 
//		if(frequency == 0)
//			frequency = 1;
		
		return frequency;
	}
	
	
	
	public void setRight(boolean isRight)
	{
		this.isRight = isRight;
	}
	
	public void setWrong(boolean isWrong)
	{
		this.isWrong = isWrong;
	}
	
	public void setShow(boolean isShow_wc)
	{
		this.isShow_wc = isShow_wc;
	}
	
	public void increaseWrongCount()
	{
		wrongCount = wrongCount+1;
	}
	
	public int getWrongCount()
	{
		return wrongCount;
	}
	public void setWrongCount(int count){
		wrongCount = count;
	}
	
	public boolean isWrong()
	{
		return isWrong;
	}
	
	public boolean isRight()
	{
		return isRight;
	}
	
	public boolean isShow()
	{
		return isShow_wc;
	}
	
	public List<Mean> getMeanList(){
		return mean_list;

	}
	
	public void setMeanList(List<Mean> MeanList)
	{
		 
		
		Collections.sort(MeanList, new Comparator<Mean>() {

	        public int compare(Mean o1, Mean o2) {
	            return String.valueOf(o2.getMpriority()).compareTo(String.valueOf(o1.getMpriority()));
	        }
	    });
		
		//Collections.reverse(MeanList);
		
		this.mean_list = MeanList;
		setmClassList(mean_list);
	}
	
	
	
	private void setmClassList(List<Mean> meanList){
		total_class = new HashMap<Integer, Boolean>();
		for(int i =0; i < meanList.size();i++){
			switch (meanList.get(i).getMClass()) {
			case Class_N:
				total_class.put(Class_N, true);
				break;
			case Class_V:
				total_class.put(Class_V, true);
				break;
			case Class_Ad:
				total_class.put(Class_Ad, true);
				break;
			case Class_Prep:
				total_class.put(Class_Prep, true);
				break;
			case Class_A:
				total_class.put(Class_A, true);
				break;
			default:
				//total_class.put("N", Class_N);
				break;
			}
			
		}
		
	}
	
	public Mean getMean(int position){
		return mean_list.get(position);
	}
	
	public HashMap<Integer, Boolean> getmClassList(){
		return total_class;
	}
	
	public void setTimerTask(View v, int index){
		view = v;
		this.index = index;
	}
	
	public View getView(){
		return view;
		
	}
	public int getIndex(){
		return index;
		
	}
	public int getExState(){
		return exState;
	}
	public void setExState(int exState){
		this.exState = exState;
	}
	public void deleteTimer(){
		this.timer=null;
		this.timer_task=null;
	}
}
