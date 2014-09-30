package com.ihateflyingbugs.vocaslide.data;

import hirondelle.date4j.DateTime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.json.JSONArray;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.vocaslide.login.MainActivitys;
import com.ihateflyingbugs.vocaslide.tutorial.Feed;
import com.ning.http.client.MaxRedirectException;

import android.R.array;
import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;

public class DBPool {

	private String DB_FILE_PATH;

	private static DBPool instance;
	private SQLiteDatabase db;
	private Context mconContext;
	private SharedPreferences settings;

	int version;

	private DBPool(Context context) {

		DB_FILE_PATH = Config.DB_FILE_DIR + Config.DB_NAME;
		mconContext = context;
		settings = mconContext.getSharedPreferences(Config.PREFS_NAME,
				Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);

		File file = new File(Config.DB_FILE_DIR);
		if (!file.exists() && !file.mkdirs())
			return;

		file = new File(DB_FILE_PATH);
		if (!file.exists()) {
			// file copy
			try {
				InputStream is = context.getAssets().open(Config.DB_NAME);
				OutputStream os = new FileOutputStream(DB_FILE_PATH);

				byte[] buffer = new byte[1024];
				int len;
				while ((len = is.read(buffer)) > 0)
					os.write(buffer, 0, len);

				os.flush();
				os.close();
				is.close();
			} catch (IOException e) {
				return;
			}
		}

		try {
			db = SQLiteDatabase.openDatabase(DB_FILE_PATH, null,
					SQLiteDatabase.OPEN_READWRITE
							| SQLiteDatabase.NO_LOCALIZED_COLLATORS);

			version = db.getVersion();
			Log.e("asdf", "" + version);

			// initialized = true;
		} catch (Exception e) {
			// if(Config.LOG)
			// Log.e(LogHelper.where(), e.toString());
			Log.e("asdf", "" + e.toString());
		}

	}

	public void resetDB(Context context) {

		db.close();

		File file = new File(DB_FILE_PATH);

		if (file.exists() && false == file.delete()) {
			return;
		}

		// file copy
		try {
			InputStream is = context.getAssets().open(Config.DB_NAME);
			OutputStream os = new FileOutputStream(DB_FILE_PATH);

			byte[] buffer = new byte[1024];
			int len;
			while ((len = is.read(buffer)) > 0)
				os.write(buffer, 0, len);

			os.flush();
			os.close();
			is.close();
		} catch (IOException e) {
			return;
		}

		try {
			db = SQLiteDatabase.openDatabase(DB_FILE_PATH, null,
					SQLiteDatabase.OPEN_READWRITE);
		} catch (Exception e) {
		}
	}

	public static DBPool getInstance(Context context) {

		if (instance == null) {
			instance = new DBPool(context);
		}
		return instance;
	}

	public void release() {

		db.close();

		if (instance != null) {
			instance = null;
		}
	}

	public ArrayList<Word> wordsWithScore() {

		ArrayList<Word> words = new ArrayList<Word>();

		Cursor cursor = db
				.rawQuery(
						"SELECT * FROM words WHERE Score IS NOT NULL AND Score > 0 ORDER BY Score DESC limit ?",
						new String[] { String.valueOf(Config.ONCE_WORD_COUNT), });

		Log.w("kjw",
				"wordsWithScore SELECT * FROM words WHERE Score IS NOT NULL AND Score > 0 ORDER BY Score DESC limit 20");
		cursor.moveToFirst();
		Word word = null;

		while (!cursor.isAfterLast()) {

			word = new Word(cursor.getInt(0), cursor.getString(1),
					cursor.getInt(2), cursor.getInt(3), cursor.getInt(4),
					cursor.getDouble(5), cursor.getInt(6), cursor.getInt(7),
					cursor.getInt(8));

			words.add(word);
			cursor.moveToNext();
		}

		cursor.close();

		try {

			db.beginTransaction();
			for (int i = 0; i < words.size(); i++) {
				words.get(i).setMeanList(getMean(words.get(i).get_id()));

			}
		} catch (SQLException e) {
			// TODO: handle exception
		} finally {
			db.endTransaction();
		}

		return words;
	}

	public ArrayList<Word> wordsWithUnknown() {

		ArrayList<Word> words = new ArrayList<Word>();

		Cursor cursor = db
				.rawQuery(
						"SELECT * FROM words WHERE State = -1 ORDER BY Frequency DESC limit ?",
						new String[] { String.valueOf(Config.ONCE_WORD_COUNT) });

		Log.w("kjw",
				"wordsWithUnknown SELECT * FROM words WHERE State = -1 ORDER BY Frequency DESC limit 30");
		cursor.moveToFirst();
		Word word = null;

		while (!cursor.isAfterLast()) {

			word = new Word(cursor.getInt(0), cursor.getString(1),
					cursor.getInt(2), cursor.getInt(3), cursor.getInt(4),
					cursor.getDouble(5), cursor.getInt(6), cursor.getInt(7),
					cursor.getInt(8));

			words.add(word);
			cursor.moveToNext();
		}

		cursor.close();

		try {

			db.beginTransaction();
			for (int i = 0; i < words.size(); i++) {
				words.get(i).setMeanList(getMean(words.get(i).get_id()));

			}
		} catch (SQLException e) {
			// TODO: handle exception
		} finally {
			db.endTransaction();
		}
		return words;
	}

	public ArrayList<Word> wordsWithLevel(int level, int limit) {

		ArrayList<Word> words = new ArrayList<Word>();

		Cursor cursor = db
				.rawQuery(
						"SELECT * FROM words WHERE Difficulty = ? AND State = 0 ORDER BY Score DESC limit ?",
						new String[] { String.valueOf(level),
								String.valueOf(limit), });

		Log.w("kjw", "wordsWithLevel SELECT * FROM words WHERE Difficulty = "
				+ level + " AND State = 0 ORDER BY Score DESC limit " + limit);
		cursor.moveToFirst();
		Word word = null;

		while (!cursor.isAfterLast()) {

			word = new Word(cursor.getInt(0), cursor.getString(1),
					cursor.getInt(2), cursor.getInt(3), cursor.getInt(4),
					cursor.getDouble(5), cursor.getInt(6), cursor.getInt(7),
					cursor.getInt(8));

			words.add(word);
			cursor.moveToNext();
		}

		cursor.close();

		try {

			db.beginTransaction();
			for (int i = 0; i < words.size(); i++) {
				words.get(i).setMeanList(getMean(words.get(i).get_id()));

			}
		} catch (SQLException e) {
			// TODO: handle exception
		} finally {
			db.endTransaction();
		}
		return words;
	}

	public ArrayList<Word> wordsWithKnown() {

		ArrayList<Word> words = new ArrayList<Word>();

		Cursor cursor = db.rawQuery(
				"SELECT * FROM words WHERE State > 0 ORDER BY Score", null);
		cursor.moveToFirst();
		Word word = null;

		while (!cursor.isAfterLast()) {

			word = new Word(cursor.getInt(0), cursor.getString(1),
					cursor.getInt(2), cursor.getInt(3), cursor.getInt(4),
					cursor.getDouble(5), cursor.getInt(6), cursor.getInt(7),
					cursor.getInt(8), false);
			words.add(word);

			cursor.moveToNext();
		}

		cursor.close();

		// try {
		//
		// db.beginTransaction();
		// for(int i =0; i <words.size();i++){
		// words.get(i).setMeanList(getMean(words.get(i).get_id()));
		//
		// }
		// }
		// catch (SQLException e) {
		// // TODO: handle exception
		// }finally{
		// db.endTransaction();
		// }
		return words;
	}

	public Word wordWithScore() {

		Cursor cursor = db
				.rawQuery(
						"SELECT * FROM words WHERE Score IS NOT NULL AND Score > 0 ORDER BY Score DESC limit 1",
						null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			Word word;

			word = new Word(cursor.getInt(0), cursor.getString(1),
					cursor.getInt(2), cursor.getInt(3), cursor.getInt(4),
					cursor.getDouble(5), cursor.getInt(6), cursor.getInt(7),
					cursor.getInt(8));

			cursor.close();
			word.setMeanList(getMean(word.get_id()));

			return word;
		} else {
			cursor.close();
			return null;
		}
	}

	// public ArrayList<Word> wordWithUnknown(){
	//
	// Cursor cursor =
	// db.rawQuery("SELECT * FROM words WHERE State = -1 ORDER BY Score DESC limit 1",
	// null);
	// cursor.moveToFirst();
	// Word word;
	//
	// while(!cursor.isAfterLast()){
	//
	// word = new Word(cursor.getInt(0),
	// cursor.getString(1),
	// cursor.getString(2),
	// cursor.getInt(3),
	// cursor.getInt(4),
	// cursor.getDouble(5),
	// cursor.getInt(6),
	// cursor.getInt(7),
	// cursor.getInt(8));
	//
	// words.add(word);
	// cursor.moveToNext();
	// }
	// cursor.close();
	// return words;
	// }

	public float getProbabilityWithStateAndHour(int hour, int state) {

		if (state == 0 || hour > 720 || hour < 1) {
			return -1;
		}

		// if(state == -1)
		// return 0;

		String column = "nine";

		switch (state) {
		case 1:
			column = "one";
			break;
		case 2:
			column = "two";
			break;
		case 3:
			column = "three";
			break;
		case 4:
			column = "four";
			break;
		case 5:
			column = "five";
			break;
		case 6:
			column = "six";
			break;
		case 7:
			column = "seven";
			break;
		case 8:
			column = "eight";
			break;
		case 9:
			column = "nine";
			break;
		default:
			column = "nine";
			break;
		}

		// updateProbabilityCount(hour, updateColumn);

		Cursor cursor = db.rawQuery("SELECT * FROM times WHERE Time = " + hour,
				null);
		cursor.moveToFirst();

		float probability = (float) cursor.getFloat(cursor
				.getColumnIndex(column));

		cursor.close();
		// return probability / 100;
		return probability;
	}

	synchronized public boolean updateProbabilityCount(int hour, int state) {

		String column = "ninth";

		switch (state) {
		case 1:
			column = "first";
			break;
		case 2:
			column = "second";
			break;
		case 3:
			column = "third";
			break;
		case 4:
			column = "fourth";
			break;
		case 5:
			column = "fifth";
			break;
		case 6:
			column = "sixth";
			break;
		case 7:
			column = "seventh";
			break;
		case 8:
			column = "eighth";
			break;
		case 9:
			column = "ninth";
			break;
		default:
			column = "ninth";
			break;
		}

		Cursor cursor = db.rawQuery("SELECT * FROM times WHERE Time = " + hour,
				null);
		cursor.moveToFirst();

		int value = cursor.getInt(cursor.getColumnIndex(column));

		ContentValues values = new ContentValues();
		values.put(column, value + 1);

		// values.put("Frequency", ++frequency);

		return db.update("times", values, "Time = ?",
				new String[] { String.valueOf(hour), }) >= 0;
	}

	synchronized public void insertLevel(Word word, boolean isKnown) {

		Log.d("kjw", "insert level difficulty = " + word.getDifficulty()
				+ "    s = " + isKnown);

		// ContentValues values = new ContentValues();
		// values.put("Difficulty", difficulty);
		// values.put("isKnown", isKnown);
		//
		// int result = (int) db.insert("level", null, values);

		try {
			if (word.getExState() == 0) {
				db.execSQL("insert into level(difficulty, isKnown) values ("
						+ word.getDifficulty() + ", '" + isKnown + "')");
			}
		} catch (NumberFormatException e) {
			// TODO: handle exception
		}

		// Log.d("kjw", "insert result = " + result);
		// return db.update("level", values, "Time = ?", new
		// String[]{String.valueOf(hour),}) >= 0;
	}

	public void insertTrueCount(int difficulty) {

		int count = Config.CHANGE_LEVEL_COUNT;
		for (int i = 0; i < count; i++) {
			db.execSQL("insert into level(difficulty, isKnown) values ("
					+ difficulty + ", '" + true + "')");
		}
		// Log.d("kjw", "insert result = " + result);
		// return db.update("level", values, "Time = ?", new
		// String[]{String.valueOf(hour),}) >= 0;
	}

	public void insertFalseCount(int difficulty) {

		int count = 1;

		db.execSQL("insert into level(difficulty, isKnown) values ("
				+ difficulty + ", '" + false + "')");

		// Log.d("kjw", "insert result = " + result);
		// return db.update("level", values, "Time = ?", new
		// String[]{String.valueOf(hour),}) >= 0;
	}

	public String[] get_index(int index) {
		String[] index_name = new String[2];
		switch (index) {
		case 1:
			index_name[0] = "one";
			index_name[1] = "first";
			break;
		case 2:
			index_name[0] = "two";
			index_name[1] = "second";
			break;
		case 3:
			index_name[0] = "three";
			index_name[1] = "third";
			break;
		case 4:
			index_name[0] = "four";
			index_name[1] = "fourth";
			break;
		case 5:
			index_name[0] = "five";
			index_name[1] = "fifth";
			break;
		case 6:
			index_name[0] = "six";
			index_name[1] = "sixth";
			break;
		case 7:
			index_name[0] = "seven";
			index_name[1] = "seventh";
			break;
		case 8:
			index_name[0] = "eight";
			index_name[1] = "eighth";
			break;
		case 9:
			index_name[0] = "nine";
			index_name[1] = "ninth";
			break;
		default:
			index_name[0] = "nine";
			index_name[1] = "ninth";
			break;
		}
		return index_name;
	}

	synchronized public void test_TimeTable(int index) {

		Log.e("kjws", "calcProbility    start");
		if (index < 1 || index > 720) {

			Log.e("kjws", "calcProbility    return");
			return;

		}

		String[] string = get_index(index);

		// Cursor cursor =
		// db.rawQuery("SELECT Time, one, first, two, second, three, third, four, fourth, five, fifth,"
		// +
		// " six, sixth, seven, seventh, eight, eighth, nine, ninth, nine, ninth"
		// +
		// " FROM times WHERE first IS NOT NULL or second IS NOT NULL or third IS NOT NULL or fourth IS NOT NULL or fifth IS NOT NULL or sixth IS NOT NULL or seventh IS NOT NULL or "
		// +
		// "eighth IS NOT NULL or ninth IS NOT NULL " , null);

		Cursor cursor = db
				.rawQuery(
						"SELECT Time, one, first, two, second, three, third, four, fourth, five, fifth,"
								+ " six, sixth, seven, seventh, eight, eighth, nine, ninth"
								+ " FROM times WHERE Time =  " + index, null);

		float probility0 = -1, probility1 = -1, probility2 = -1;
		int count0 = -1, count1 = -1, time0 = -1, time1 = -1, time2 = -1;

		boolean isCountUpdate = false;
		String query1 = "";
		String query2 = "";

		int Time = -1;
		float[] column0 = { -1, -1, -1, -1, -1, -1, -1, -1, -1 };
		Integer[] column1 = { -1, -1, -1, -1, -1, -1, -1, -1, -1 };

		cursor.moveToFirst();
		int j = 0;
		String value = "";
		String value2 = "";
		for (int i = 0; i < 19; i++) {
			if (i == 0) {
				Time = cursor.getInt(0);
				value += Time + "\n";
			} else if (i % 2 == 1) {
				column0[i / 2] = cursor.getFloat(i);
				value += cursor.getFloat(i) + ",  ";
			} else if (i % 2 == 0) {
				column1[i / 2 - 1] = cursor.getInt(i);
				value2 += cursor.getInt(i) + ",  ";
			}
		}
		Log.d("column", value + "\n" + value2);

		// String query = "";
		// if(isCountUpdate){
		// query = query1+ " End, "+ query2+" End";
		// }else{
		// query = query1+ " End";
		// }
		//
		// if(cursor.getCount()>0){
		// db.execSQL(query);
		// }

	}

	/**
	 * 2014/03/10 업데이트 대규모 수정 들어감 (Transaction사용, array와 query문 동시 사용)
	 * 
	 * 
	 * 
	 */
	public void calcProbility() {

		try {

			db.beginTransaction();
			// x가 9 까지인 이유는 망각곡선의 갯수가 9개가 있으므로 one~nine first~ninth
			for (int x = 1; x <= 9; x++) // for(int x = 1; x <= 9; x++)
			{

				Log.e("alarms", "calcProbility large for loop : " + x);
				int index = x;

				if (index < 0 || index > 9) {

					db.endTransaction();
					Log.e("alarms", "idx exception");
					return;

				}

				int tmp = 1;

				String[] string = get_index(index);

				// 타임 테이블에 한 한 컬럼 쌍을 가져온다 (ex, one&first 컬럼)
				Log.e("alarms", "cursor raw query");
				Cursor cursor = db.rawQuery("SELECT Time, " + string[0] + ", "
						+ string[1] + " FROM times WHERE " + string[1]
						+ " IS NOT NULL", null);
				// 해당 index에 대해, 단어가 노출된 지 얼마나 지났는지 모두 선택
				// 초기화
				float probility0 = -1, probility1 = -1, probility2 = -1;
				int count0 = -1, count1 = -1, time0 = -1, time1 = -1, time2 = -1;

				List<Time> list_time = new ArrayList<Time>();

				/*
				 * 
				 * 커서가 0이 아닐때 time이라는 객체에 결과값을 담고 array리스트에 삽입
				 * 
				 * 커서가 0일때
				 * 
				 * 아무것도 없으므로 함수 종료
				 */
				if (cursor.getCount() != 0) {
					cursor.moveToFirst();
					do {
						Time time = new Time(cursor.getInt(0),
								cursor.getFloat(1), cursor.getInt(2)); // Time,
																		// cardinal
																		// number(기수),
																		// ordinal
																		// number(서수)
						list_time.add(time);
					} while (cursor.moveToNext());
				} else {
					;
					// 19 modify : make annotation
					// cursor.close();
					// return;
				}

				/*
				 * list_time 의 사이즈가 1일떄
				 * 
				 * 한 first열에 정보가 1개만 들어가 있으므로 해당열 기준으로 위와 밑으로 곡선을 그린다 (y =
				 * 1/(x+1)의 그래프)
				 * 
				 * 
				 * list_time 의 사이즈가 1보다 클때
				 * 
				 * 2개 이상의 값이 있따는 뜻이므로 리스트 두 값사이의 확률은 평균값으로 그 이외에 위와 밑으로는 곡선을 그린다
				 * (y = 1/(x+1) 의 그래프)
				 * 
				 * 그뒤에 각 확률간의 차가 0.005보다 크면 다시 한번 while문을 돌아서 평균값을 줄여서 각 값간의 차가
				 * 0.005이하로 만든다 (플로팅 작업)
				 */

				cursor.moveToFirst();
				if (list_time.size() == 1) {
					Log.e("alarms", "if clause, list_time.size() : "
							+ list_time.size());
					if (probility0 == -1 && time0 == -1) {
						time0 = list_time.get(0).get_Time();
						probility0 = list_time.get(0).get_column0();

						double a = (1 - probility0) / (probility0 * time0);

						// 19 modify
						// string[0] < probility0이 필요한가??????
						db.execSQL(
								"update times set "
										+ string[0]
										+ " = 1 /((time * ?) + 1) WHERE time < ?", // string[0]
																					// :
																					// probabiltiy
								new String[] { String.valueOf(a),
										String.valueOf(time0) });

						db.execSQL(
								"update times set "
										+ string[0]
										+ " = 1 /((time * ?) + 1) WHERE time > ?",
								new String[] { String.valueOf(a),
										String.valueOf(time0) });

						/*
						 * db.execSQL("update times set " + string[0] +
						 * " = 1 /((time * ?) + 1) WHERE time < ? and " +
						 * string[0] +" < ?", //string[0] : probabiltiy new
						 * String[]{ String.valueOf(a), String.valueOf(time0),
						 * String.valueOf(probility0),});
						 * 
						 * db.execSQL("update times set " + string[0] +
						 * " = 1 /((time * ?) + 1) WHERE time > ? and " +
						 * string[0] +"> ?", new String[]{ String.valueOf(a),
						 * String.valueOf(time0), String.valueOf(probility0),});
						 */
					}

					tmp = 0;

					// 19 modify : make annotation
					// cursor.close();

					// return;

				} else if (list_time.size() > 1) {
					// Log.v("alarms", "calcProbility");
					Log.e("alarms", "else if clause, list_time.size() : "
							+ list_time.size());
					int loop_cnt = 0; // added!!
					while (tmp == 1) {
						int position = 0;
						tmp = 0;
						loop_cnt++; // loop cnt is added!!

						// int idx=0;
						// while(idx++<2){ //counting
						while (list_time.size() - 1 != position) { // counting
							if (probility0 == -1 && time0 == -1) {
								time0 = list_time.get(position).get_Time();
								count0 = list_time.get(position).get_column1();
								probility0 = list_time.get(position)
										.get_column0();
								// Log.e("pro0 is -1", "pro0 is -1");
							}

							if (probility1 == -1 && time1 == -1) {

								position++;

								time1 = list_time.get(position).get_Time();
								count1 = list_time.get(position).get_column1();
								probility1 = list_time.get(position)
										.get_column0();

								if (time1 == time0) // error handling
								{
									position++;
									continue;
								}

								float m = (float) (probility1 - probility0)
										/ (time1 - time0); // 기울기

								// 시간이 더 흐른뒤에의 알 확률이 높아졌을떄 플로팅 작업을 하는 부분
								if (m >= 0) {
									// (P1*Np1+P2*Np2)/(Np1+Np2)

									float t = (float) (probility0 * count0 + probility1
											* count1)
											/ (count0 + count1); // mean

									list_time.get(position).set_column0(t);
									list_time.get(position - 1).set_column0(t);

									int c = (count0 + count1) / 2; // 나중에 이 결과가
																	// 상관있지
																	// 않을까????
									list_time.get(position).set_column1(c);
									list_time.get(position - 1).set_column1(c);
									/*
									 * 나중에 loop_cnt 값으로 로그를 심어놓구 그런경우가 어느정도
									 * 발생하는지 평균을 내보자 !
									 */
									if (m > 0.005) {
										tmp = 1; // 루프 더 돌아라
									}
								}
								// 일반적인 망강곡선의 값을 가졌을땐 else문으로
								else {

									// /////////////
									list_time.get(position - 1).set_column0(
											m * time0 - m * time0 + probility0); // 아무
																					// 의미
																					// 없다
									list_time.get(position).set_column0(
											m * time1 - m * time0 + probility0); //

								}

							}

							time0 = list_time.get(position).get_Time(); // 19
																		// modify
							count0 = list_time.get(position).get_column1(); // 19
																			// modify
							probility0 = list_time.get(position).get_column0(); // 19
																				// modify

							// time0 = time1; //for next step
							// count0 = count1; //
							// probility0 = probility1; //

							time1 = -1; //
							count1 = -1; //
							probility1 = -1; //

							time2 = -1; //
							probility2 = -1; //

						}
						time0 = -1; // initialize
						count0 = -1; //
						probility0 = -1; //

					}
					// Log.v("alarms", "calcProbility");

					/*
					 * 평탄화 작업이 끝나면 리스트에 담긴 값을 대상으로 time을 업데이트 한다
					 */
					Log.e("alarms", "calcProbility changing is ended");
					for (int i = 0; i < list_time.size(); i++) {
						Log.e("test", "probability" + i + " : "
								+ list_time.get(i).get_column0());
					}
					if (tmp != 1) {
						// Log.d("alarms", "calcProbility");
						Log.e("alarms", "execute query");

						if (list_time.size() > 1) {

							time0 = -1;
							count0 = -1;
							probility0 = -1;

							time1 = -1;
							count1 = -1;
							probility1 = -1;

							time2 = -1;
							probility2 = -1;

							int position = 0;
							// int idx=0;
							// while(idx++<2){ //counting
							while (position != list_time.size() - 1) { // counting

								if (probility0 == -1 && time0 == -1) {
									time0 = list_time.get(position).get_Time();
									count0 = list_time.get(position)
											.get_column1();
									probility0 = list_time.get(position)
											.get_column0();

								}

								if (probility1 == -1 && time1 == -1) {

									position++;
									time1 = list_time.get(position).get_Time();
									count1 = list_time.get(position)
											.get_column1();
									probility1 = list_time.get(position)
											.get_column0();

									if (time1 == time0) // error handling
									{
										position++;
										continue;
									}

									float m = (float) (probility1 - probility0)
											/ (time1 - time0);

									// update times set first = one * time WHERE
									// time between 1 and 5

									// 19 modify : cardinal num: probability
									// which is common part
									db.execSQL(
											"update times set "
													+ string[0]
													+ " = ? * (time - ?) + ? WHERE time between ? and ?",
											new String[] { String.valueOf(m),
													String.valueOf(time0),
													String.valueOf(probility0),
													String.valueOf(time0),
													String.valueOf(time1) });

									if (m >= 0) // 비정상적 기울기
									{
										// (P1*Np1+P2*Np2)/(Np1+Np2)
										/*
										 * //19 modify : make annotation //float
										 * t = (float)(probility0 * count0 +
										 * probility1 * count1) / (count0 +
										 * count1); //mean
										 * 
										 * //int c = (count0 + count1)/2;
										 * //count
										 * 
										 * db.execSQL("update times set " +
										 * string[0] +
										 * " = ? WHERE time between ? and ?",
										 * //cardinal num: probability new
										 * String[]{ String.valueOf(t),
										 * String.valueOf(time0),
										 * String.valueOf(time1),});
										 * 
										 * db.execSQL("update times set " +
										 * string[1] +
										 * " = ? WHERE time = ? or time = ?",
										 * //ordinal num: count new String[]{
										 * String.valueOf(c),
										 * String.valueOf(time0),
										 * String.valueOf(time1),});
										 */

										// 19 modify : ordinal num: count
										// update count separately
										db.execSQL(
												"update times set " + string[1]
														+ " = ? WHERE time = ?",
												new String[] {
														String.valueOf(count0),
														String.valueOf(time0) });
										//
										db.execSQL(
												"update times set " + string[1]
														+ " = ? WHERE time = ?",
												new String[] {
														String.valueOf(count1),
														String.valueOf(time1) });

									} else // 정상적
									{
										;
									}

								}
								/*
								 * 19 modify if(list_time.get(0).get_Time()!=1){
								 * //list_time에서 Time의 첫번째 값이 1이 아닐 때 : 그래프는
								 * (0,1)을 지나야 한다 -> 연결해줌 time0 =
								 * list_time.get(0).get_Time(); probility0 =
								 * list_time.get(0).get_column0();
								 * 
								 * double a = ( 1 - probility0 ) / (probility0 *
								 * time0);
								 * 
								 * db.execSQL("update times set " + string[0] +
								 * " = 1 /((time * ?) + 1) WHERE time < ? and "
								 * + string[0] +" < ?", new String[]{
								 * String.valueOf(a), String.valueOf(time0),
								 * String.valueOf(probility0),}); }
								 * 
								 * if(list_time.get(position).get_Time()!=720&&
								 * list_time.size()-1==position){ //list_time에서
								 * Time의 마지막 값이 720이 아닐 때 : 연결해줌 time0 =
								 * list_time.get(position).get_Time();
								 * probility0 =
								 * list_time.get(position).get_column0();
								 * 
								 * double a = ( 1 - probility0 ) / (probility0 *
								 * time0); db.execSQL("update times set " +
								 * string[0] +
								 * " = 1 /((time * ?) + 1) WHERE time > ? and "
								 * + string[0] +"> ?", new String[]{
								 * String.valueOf(a), String.valueOf(time0),
								 * String.valueOf(probility0),});
								 * 
								 * }
								 */
								time0 = time1;
								count0 = count1;
								probility0 = probility1;

								time1 = -1;
								count1 = -1;
								probility1 = -1;
							}

							// 19 modify
							if (list_time.get(0).get_Time() != 1) { // list_time에서
																	// Time의 첫번째
																	// 값이 1이 아닐
																	// 때 : 그래프는
																	// (0,1)을
																	// 지나야 한다 ->
																	// 연결해줌
								time0 = list_time.get(0).get_Time();
								probility0 = list_time.get(0).get_column0();

								double a = (1 - probility0)
										/ (probility0 * time0);

								db.execSQL(
										"update times set "
												+ string[0]
												+ " = 1 /((time * ?) + 1) WHERE time < ?",
										new String[] { String.valueOf(a),
												String.valueOf(time0) });
							}
							if (list_time.get(list_time.size() - 1).get_Time() != 720) { // 19
																							// modify
																							// 조건
																							// //list_time에서
																							// Time의
																							// 마지막
																							// 값이
																							// 720이
																							// 아닐
																							// 때
																							// :
																							// 연결해줌
								time0 = list_time.get(position).get_Time();
								probility0 = list_time.get(position)
										.get_column0();

								double a = (1 - probility0)
										/ (probility0 * time0);
								db.execSQL(
										"update times set "
												+ string[0]
												+ " = 1 /((time * ?) + 1) WHERE time > ?",
										new String[] { String.valueOf(a),
												String.valueOf(time0) });

							}
						}

						Log.d("alarms", "calcProbility");

					}
				} else {
					Log.e("alarms", "else clause, list_time.size() : "
							+ list_time.size());
				}
				Log.e("alarms", "calcProbility large for loop : " + x
						+ " is ended");
				cursor.close(); // 19 modify
			}
			Log.e("alarms", "transaction successful");
			db.setTransactionSuccessful();

		} catch (SQLException e) {
			// TODO: handle exception
		} finally {
			db.endTransaction();
		}
		Log.e("alarms", "calcProbility is ended");
	}

	/*
	 * ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
	 * ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■ 다량의
	 * 데이터를 insert시킬때 시간을 최소화 시키는 방법은 트랜잭션이다. 25000 건 인서트에 0.914초
	 * 
	 * but mysql 에서는 그것보다 시간이 더 걸린다.
	 * ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
	 * ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
	 * ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
	 * 
	 * 
	 * try{ db.beginTransaction(); db.insert(TABLE_NAME, null, VALUE);
	 * db.setTransactionSuccessful(); } catch (SQLException e){ } finally {
	 * db.endTransaction(); }
	 */

	public void test_calcScore(int Time_to_count) {

		try {
			db.beginTransaction();

			List<Time> list_time = new ArrayList<Time>();

			Cursor cursor_time = db
					.rawQuery(
							"SELECT Time, one, first, two, second, three, third, four, fourth, five, fifth,"
									+ " six, sixth, seven, seventh, eight, eighth, nine, ninth"
									+ " FROM times", null);
			cursor_time.moveToFirst();

			Log.e("almas", "asdfasdfasdf");

			while (!cursor_time.isAfterLast()) {
				Time time = new Time(cursor_time.getInt(0),
						cursor_time.getFloat(1), cursor_time.getInt(2),
						cursor_time.getFloat(3), cursor_time.getInt(4),
						cursor_time.getFloat(5), cursor_time.getInt(6),
						cursor_time.getFloat(7), cursor_time.getInt(8),
						cursor_time.getFloat(9), cursor_time.getInt(10),
						cursor_time.getFloat(11), cursor_time.getInt(12),
						cursor_time.getFloat(13), cursor_time.getInt(14),
						cursor_time.getFloat(15), cursor_time.getInt(16),
						cursor_time.getFloat(17), cursor_time.getInt(18));
				list_time.add(time);
				cursor_time.moveToNext();

			}

			cursor_time.close();

			Cursor cursor = db.rawQuery("SELECT * FROM words WHERE State > 0",
					null);

			cursor.moveToFirst();
			Word word;

			Random random = new Random();
			random.setSeed(System.currentTimeMillis());

			while (!cursor.isAfterLast()) {

				word = new Word(cursor.getInt(0), cursor.getString(1),
						cursor.getInt(2), cursor.getInt(3), cursor.getInt(4),
						cursor.getDouble(5), cursor.getInt(6),
						cursor.getInt(7), cursor.getInt(8));

				float probability = list_time.get(word.getTime(Time_to_count))
						.get_property(word.getState());
				// float probability =
				// getProbabilityWithStateAndHour(word.getTime(Time_to_count),
				// word.getFrequency());
				double score;
				if (probability == -1) {
					cursor.moveToNext();
					continue;
				}

				// else if(probability == 0)
				// score = word.getScore() * 10;

				// 올림((0.95+0.05/exp(random*20)-n번 외운 단어의 k시간대의 확률)*1000
				else
					score = 0.95 - probability;
				// score = 0.95 + 0.05 / Math.exp(random.nextDouble() * 20) -
				// probability;

				updateScore(1, score, 1);
				// updateWordTime(word.get_id(), word.getTime());

				cursor.moveToNext();
			}

			cursor.close();
			db.setTransactionSuccessful();
		} catch (SQLException e) {
		} finally {
			db.endTransaction();
		}
	}

	public void calcScore(int Time_to_count) {

		try {
			db.beginTransaction();

			List<Time> list_time = new ArrayList<Time>();

			// 타임 테이븖을 모두 가져온다
			Cursor cursor_time = db
					.rawQuery(
							"SELECT Time, one, first, two, second, three, third, four, fourth, five, fifth,"
									+ " six, sixth, seven, seventh, eight, eighth, nine, ninth"
									+ " FROM times", null);
			cursor_time.moveToFirst();

			Log.e("almas", "asdfasdfasdf");

			// 타임테이블을 arraylist에 담는다
			while (!cursor_time.isAfterLast()) {
				Time time = new Time(cursor_time.getInt(0),
						cursor_time.getFloat(1), cursor_time.getInt(2),
						cursor_time.getFloat(3), cursor_time.getInt(4),
						cursor_time.getFloat(5), cursor_time.getInt(6),
						cursor_time.getFloat(7), cursor_time.getInt(8),
						cursor_time.getFloat(9), cursor_time.getInt(10),
						cursor_time.getFloat(11), cursor_time.getInt(12),
						cursor_time.getFloat(13), cursor_time.getInt(14),
						cursor_time.getFloat(15), cursor_time.getInt(16),
						cursor_time.getFloat(17), cursor_time.getInt(18));
				list_time.add(time);
				cursor_time.moveToNext();

			}

			cursor_time.close();

			// 현재 안다고 했던 단어들을 모두 가져온다
			Cursor cursor = db.rawQuery("SELECT * FROM words WHERE State > 0",
					null);

			cursor.moveToFirst();
			Word word;

			Random random = new Random();
			random.setSeed(System.currentTimeMillis());

			// 해쉬맵에 키값으로
			HashMap<String, String> hm = new HashMap<String, String>();
			while (!cursor.isAfterLast()) {

				int state = cursor.getInt(6);
				int time = cursor.getInt(7);

				/*
				 * 
				 * state +s time으로 key값을 만듬 ex) state = 7, time = 20이면 key =
				 * 7s20;
				 */

				String key = String.valueOf(state) + "s" + String.valueOf(time);

				float probability;

				// 타임 테이블은 720이 끝이므로 720 으로 조건문걸어준다
				if (time + Time_to_count <= 719) {
					probability = list_time.get(time + Time_to_count)
							.get_property(state);
				} else {
					probability = list_time.get(719).get_property(state);
				}

				double score;

				if (probability == -1) {
					cursor.moveToNext();
					continue;
				}

				// 올림((0.95+0.05/exp(random*20)-n번 외운 단어의 k시간대의 확률)*1000
				else
					score = 0.90 + 0.10 / Math.exp(random.nextDouble() * 30)
							- probability;
				// score = 0.95 - probability;
				hm.put(key, String.valueOf(score));

				cursor.moveToNext();
			}
			if (hm.size() == 0) {
				return;
			}
			updateScore(hm, Time_to_count);
			cursor.close();
			db.setTransactionSuccessful();
		} catch (SQLException e) {
		} finally {
			db.endTransaction();
		}
	}

	synchronized public void updateScore(HashMap<String, String> hm,
			int Time_to_count) {

		try {
			db.beginTransaction();
			Set<String> st = hm.keySet();
			Iterator<String> it = st.iterator();

			while (it.hasNext()) {

				String key = it.next();
				String[] words = key.split("s");
				String state = words[0];
				String time = words[1];
				String value = hm.get(key);

				ContentValues values = new ContentValues();
				values.put("Score", value);

				db.update("words", values, "time = ? and state = ?",
						new String[] { time, state });

			}

			db.execSQL("update words set Time = Time +" + Time_to_count
					+ " WHERE State>0");
			db.setTransactionSuccessful();
		} catch (SQLException e) {
		} finally {
			db.endTransaction();
		}

	}

	public void currentLeveling(int difficulty) {
		Cursor cursor = db
				.rawQuery(
						"SELECT * FROM level WHERE difficulty = ? ORDER BY rowid DESC LIMIT 10 ",
						new String[] { String.valueOf(difficulty), });
		cursor.moveToFirst();
	}

	int lv_difficulty;
	boolean isShownDial = false;

	public int calcLevel(int count) {

		lv_difficulty = 1;
		Log.e("level",
				"calcLevel()   difficulty: "
						+ String.valueOf(Config.Difficulty) + "   "
						+ Config.MIN_DIFFICULTY);
		lv_difficulty = Config.Difficulty;
		int origin_diff = Config.Difficulty;

		boolean isCheck = false;
		for (int i = Config.MIN_DIFFICULTY; i <= lv_difficulty; i++) {
			Cursor cursor = db
					.rawQuery(
							"SELECT Difficulty, "
									+ "count(case when isKnown='true' then 1 else null end) as truecount, "
									+ "count(case when isKnown='false' then 1 else null end) as falsecount "
									+ "FROM (select * from level where difficulty = ? ORDER BY rowid DESC limit ?  ) "
									+ "GROUP BY Difficulty ", new String[] {
									String.valueOf(i), String.valueOf(count), });

			cursor.moveToFirst();

			Log.d("level",
					"query = "
							+ String.format(
									"SELECT Difficulty, "
											+ "count(case when isKnown='true' then 1 else null end) as truecount, "
											+ "count(case when isKnown='false' then 1 else null end) as falsecount "
											+ "FROM (select * from level where difficulty = %d ORDER BY rowid DESC limit 10  ) "
											+ "GROUP BY Difficulty ", i));

			Log.e("level",
					"calcLevel()   .getCount: "
							+ String.valueOf(cursor.getCount() + "    " + count));

			if (cursor.getCount() > 0) {

				int trueCount = cursor.getInt(cursor
						.getColumnIndex("truecount"));
				int falseCount = cursor.getInt(cursor
						.getColumnIndex("falsecount"));

				if (i == lv_difficulty) {
					if (trueCount == count
							&& lv_difficulty <= Config.MAX_DIFFICULTY) {
						lv_difficulty++;
					}
				} else {
					Log.e("cursor.getCount() <= 0", "cursor.getCount() <= 0");
					float c = (float) (trueCount / (trueCount + falseCount));
					if (c < 0.9) {
						if (Config.CHANGE_LEVEL_COUNT < 40) {
							Config.CHANGE_LEVEL_COUNT += 10;
							settings.edit()
									.putString(MainActivitys.GpreLevelCounting,
											"" + Config.CHANGE_LEVEL_COUNT)
									.commit();
						}
						lv_difficulty = i;
						isCheck = true;
						break;
					}
				}

			}
			if (isCheck) {
				break;
			}
			cursor.close();
		}

		return lv_difficulty;
	}

	public boolean checkLevelChange(int origin_diff, Context activity) {
		if (origin_diff != Config.Difficulty) {
			// 레벨이 현재와 같을때 혹은 같지 않을때
			if (origin_diff < lv_difficulty) {
				putChangeLevel(origin_diff, lv_difficulty);
				FlurryAgent.logEvent("BaseActivity_DBPool:LevelUp"
						+ origin_diff + "to" + lv_difficulty);
			} else {
				putChangeLevel(origin_diff, lv_difficulty);
				new AlertDialog.Builder(activity)
						.setMessage(
								settings.getString(MainActivitys.GpreName,
										"이름없음") + "님의 레벨이 내려갔습니다")
						.setPositiveButton("확인", null).show();
				FlurryAgent.logEvent("BaseActivity_DBPool:LevelDown"
						+ origin_diff + "to" + lv_difficulty);
			}
		}

		return settings
				.edit()
				.putString(MainActivitys.GpreLevel,
						String.valueOf(lv_difficulty)).commit();
	}

	/*
	 * 우저 테이블을 생성후에 아래에 있는 words 다 테이블 이름으로 바꿔주면 될듯
	 */

	synchronized public boolean updateWordInfo(Word word, boolean isKnown) {

		// insertLevel(word.getDifficulty(), isKnown);

		boolean isTimeUpdate = true, isWordUpdate, isProbabilityUpdate = true;

		int state = word.getState();
		int preState = state;

		String column0 = null, column1 = null;

		switch (state) {
		case -1:
			column0 = null;
			column1 = null;
			break;
		case 0:
			column0 = null;
			column1 = null;
			break;

		case 1:
			column0 = "one";
			column1 = "first";
			break;
		case 2:
			column0 = "two";
			column1 = "second";
			break;
		case 3:
			column0 = "three";
			column1 = "third";
			break;
		case 4:
			column0 = "four";
			column1 = "fourth";
			break;
		case 5:
			column0 = "five";
			column1 = "fifth";
			break;
		case 6:
			column0 = "six";
			column1 = "sixth";
			break;
		case 7:
			column0 = "seven";
			column1 = "seventh";
			break;
		case 8:
			column0 = "eight";
			column1 = "eighth";
			break;
		case 9:
			column0 = "nine";
			column1 = "ninth";
			break;
		default:
			column0 = "nine";
			column1 = "ninth";
			break;
		}

		// Log.d("kjw", "SELECT * FROM times WHERE Time = " + word.getTime());
		Cursor cursor = db.rawQuery("SELECT * FROM times WHERE Time = ?",
				new String[] { String.valueOf(word
						.getTime(Config.Time_ONE_HOUR)), });
		cursor.moveToFirst();

		Log.d("kjw", column0 + "   " + column1);
		int time_value = 0;

		if (state > 0) {
			int saveValue = cursor.getInt(cursor.getColumnIndex(column1));
			float probability = cursor.getFloat(cursor.getColumnIndex(column0));

			int currentValue = saveValue + 1;
			int temp;
			time_value = currentValue;
			temp = isKnown ? 1 : 0;

			if (column1 != null) {
				ContentValues timeValues = new ContentValues();
				timeValues.put(column1, currentValue);
				isTimeUpdate = db.update("times", timeValues, "Time = ?",
						new String[] { String.valueOf(word
								.getTime(Config.Time_ONE_HOUR)), }) >= 0;
				cursor.close();
				// Log.d("kjw", "update time info " + word.getTime() + "  " +
				// column1 + " = " + currentValue);
			}

			if (column0 != null) {
				float calcProbability = (float) (probability * currentValue + temp)
						/ (currentValue + 1);
				Log.e("kjw", "@@@p calc   (" + probability + " * "
						+ currentValue + " + " + temp + ") / " + " ( "
						+ currentValue + " + 1 " + ") = " + calcProbability);
				ContentValues probabilityValues = new ContentValues();
				probabilityValues.put(column0, calcProbability);
				isProbabilityUpdate = db.update("times", probabilityValues,
						"Time = ?", new String[] { String.valueOf(word
								.getTime(Config.Time_ONE_HOUR)), }) >= 0;

				// db.execSQL("UPDATE times set " + column0 + " = " +
				// calcProbability + " where Time = " + word.getTime());
				// Log.w("kjw", "UPDATE times set " + column0 + " = " +
				// calcProbability + " where Time = " + word.getTime());
				Log.v("kjw",
						"probability time = "
								+ word.getTime(Config.Time_ONE_HOUR)
								+ " column =  " + column0 + " = "
								+ calcProbability);
			}
		}

		ContentValues values = new ContentValues();
		// values.put("Time", 1);
		values.put("Frequency", word.getFrequency() + 1);

		if (isKnown) {
			if (state == 0) {
				state = 9;

			} else if (state == -1) {
				state = 1;
			} else {
				state = state + 1;
			}
		} else {
			state = -1;
		}
		/*
		 * preState>0은 내가 이전에 안다라고 체크한 단어일텐데 그것 외에는 score나 time을 0으로 세팅을 해주지 않는다
		 * 297 4639
		 */

		if (preState > 0) {
			values.put("Score", 0);
			values.put("Time", 0);

			db.execSQL("update words set Score = null, time = null WHERE Word_Code = "
					+ word.get_id());
		}

		values.put("State", state);
		word.setState(state);
		// Log.d("kjw", "update word info = " + word.get_id() + "  " +
		// word.getWord() + "   " + state);
		isWordUpdate = db.update("words", values, "Word_Code = ?",
				new String[] { String.valueOf(word.get_id()), }) >= 0;

		return isWordUpdate && isTimeUpdate && isProbabilityUpdate;
	}

	synchronized public boolean updateWordFrequency(int wordCode,
			int frequency, boolean isKnown, int state) {

		ContentValues values = new ContentValues();
		values.put("Time", 1);
		values.put("Frequency", ++frequency);

		if (isKnown) {
			if (state != -1)
				values.put("State", ++state);
			else
				values.put("State", 1);
		} else
			values.put("State", -1);

		return db.update("words", values, "Word_Code = ?",
				new String[] { String.valueOf(wordCode), }) >= 0;
	}

	public ArrayList<Word> pwordsWithQuery(String query) {

		ArrayList<Word> words = new ArrayList<Word>();

		if (query == null) {
			return words;
		}
		Cursor cursor = db.rawQuery("SELECT * FROM words WHERE word_code in "
				+ "(SELECT word_code FROM pwords " + query
				+ ") AND (Score >= 0 OR Score IS NULL)", null);

		// Log.e("kjw", "SELECT * FROM words WHERE word_code in " +
		// "(SELECT word_code FROM pwords " + query + ")");
		cursor.moveToFirst();
		Word word = null;

		while (!cursor.isAfterLast()) {

			word = new Word(cursor.getInt(0), cursor.getString(1),
					cursor.getInt(2), cursor.getInt(3), cursor.getInt(4),
					cursor.getDouble(5), cursor.getInt(6), cursor.getInt(7),
					cursor.getInt(8));

			words.add(word);
			cursor.moveToNext();
		}

		cursor.close();
		return words;
	}

	public ArrayList<Word> pwordsWithKnownAndQuery(String query) {

		ArrayList<Word> words = new ArrayList<Word>();

		// Cursor cursor =
		// db.rawQuery("SELECT * FROM words WHERE State > 0 ORDER BY Score",
		// null);
		Cursor cursor = db.rawQuery(
				"SELECT * FROM words WHERE State > 0 And word_code in "
						+ "(SELECT word_code FROM pwords " + query + ")", null);

		cursor.moveToFirst();
		Word word = null;

		while (!cursor.isAfterLast()) {

			word = new Word(cursor.getInt(0), cursor.getString(1),
					cursor.getInt(2), cursor.getInt(3), cursor.getInt(4),
					cursor.getDouble(5), cursor.getInt(6), cursor.getInt(7),
					cursor.getInt(8));

			words.add(word);
			cursor.moveToNext();
		}

		cursor.close();
		return words;
	}

	public ArrayList<Publish> publishs() {

		ArrayList<Publish> arrays = new ArrayList<Publish>();

		Cursor cursor = db.rawQuery("SELECT * " + "FROM publish "
				+ "ORDER BY publish_id", null);

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {

			arrays.add(new Publish(cursor.getInt(1), cursor.getString(0)));
			cursor.moveToNext();
		}

		cursor.close();
		return arrays;
	}

	public ArrayList<Publish> publishs(int grade) {

		ArrayList<Publish> arrays = new ArrayList<Publish>();

		Cursor cursor = db
				.rawQuery(
						"SELECT * "
								+ "FROM publish "
								+ "WHERE publish_id in ( SELECT publisher_code FROM publish_info WHERE grade = ?)"
								+ "ORDER BY publish_id",
						new String[] { String.valueOf(grade), });

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {

			arrays.add(new Publish(cursor.getInt(1), cursor.getString(0)));
			cursor.moveToNext();
		}

		cursor.close();
		return arrays;
	}

	public int lesson(int grade, int publish_id) {

		Cursor cursor = db
				.rawQuery(
						"SELECT final_lesson FROM publish_info WHERE grade = ? AND publisher_code = ?",
						new String[] { String.valueOf(grade),
								String.valueOf(publish_id), });

		cursor.moveToFirst();

		int l = cursor.getInt(0);
		cursor.close();
		return l;
	}

	synchronized private boolean updateWordTime(int wordCode, int time) {

		// Calendar mCalendar = Calendar.getInstance();
		// mCalendar.setTimeInMillis(System.currentTimeMillis());
		//
		// int hour = mCalendar.getTime().getHours();

		ContentValues values = new ContentValues();
		values.put("Time", ++time);

		return db.update("words", values, "Word_Code = ?",
				new String[] { String.valueOf(wordCode), }) >= 0;
	}

	synchronized public boolean updateScore(int wordCode, double score, int hour) {

		ContentValues values = new ContentValues();
		values.put("Score", score);
		values.put("Time", hour);

		return db.update("words", values, "Word_Code = ?",
				new String[] { String.valueOf(wordCode), }) >= 0;
	}

	public int getRightWordCount() {
		Cursor cursor = db.rawQuery("SELECT * " + "FROM words "
				+ "WHERE state > 0", null);

		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	public int getNoZeroState() {
		Cursor cursor = db.rawQuery("SELECT * " + "FROM words "
				+ "WHERE state > 0", null);

		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	public Word getWord(int idx) {
		Cursor cursor = db.rawQuery("SELECT * "
				+ "FROM words WHERE Word_Code = ?",
				new String[] { String.valueOf(idx), });

		Word word = null;
		if (cursor.getCount() == 1) {
			cursor.moveToFirst();

			word = new Word(cursor.getInt(0), cursor.getString(1),
					cursor.getInt(2), cursor.getInt(3), cursor.getInt(4),
					cursor.getDouble(5), cursor.getInt(6), cursor.getInt(7),
					cursor.getInt(8));
		}

		return word;
	}

	public List<Mean> getMean(int idx) {
		Cursor cursor = db.rawQuery("SELECT * "
				+ "FROM means WHERE Word_Code = ?",
				new String[] { String.valueOf(idx), });

		List<Mean> mean = new ArrayList<Mean>();
		if (cursor.getCount() != 0) {
			cursor.moveToFirst();

			do {
				mean.add(new Mean(cursor.getInt(0), cursor.getInt(1), cursor
						.getInt(2), cursor.getString(3)));
			} while (cursor.moveToNext());
			// int _id, String meaning, int w_class, int w_priority

		}
		return mean;

	}

	public int getWorngWordCount() {
		Cursor cursor = db.rawQuery(
				"SELECT * " + "FROM words WHERE state = -1", null);

		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	public int getNoneWordCount() {
		Cursor cursor = db.rawQuery("SELECT * " + "FROM words WHERE state = 0",
				null);

		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	private void writeFile(String fileName, String s) {
		try {

			// File saveFile = new
			// File(Environment.getExternalStorageDirectory().getAbsolutePath()
			// + "/test_log.txt");
			File saveFile = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + fileName);
			if (!saveFile.exists()) {
				saveFile.createNewFile();
			}

			FileWriter fw = new FileWriter(saveFile, true);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(s);
			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e) {
			Log.e("kjw", "write error = " + e.getMessage());
			e.printStackTrace();
		} finally {

		}
	}

	/*
	 * ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
	 * ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
	 * 
	 * 유저 테이블 관리
	 * 
	 * ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
	 * ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
	 */

	public boolean insertCurrentWord(Word word, int id) {
		ContentValues values = new ContentValues();
		values.put("Word_Code", word.get_id());
		values.put("isRight", word.isRight() ? 1 : 0);
		values.put("isWrong", word.isWrong() ? 1 : 0);
		values.put("wrongCount", word.getWrongCount());
		values.put("exState", word.getState());

		return db.insert("user_words", null, values) >= 0;

	}

	synchronized public void deleteCurrentWord(int _id) {
		db.execSQL("DELETE FROM user_words WHERE Word_Code = " + _id);
	}

	synchronized public void deleteAllCurrentWord() {
		try {
			db.beginTransaction();
			db.execSQL("DELETE FROM user_words ");

			db.setTransactionSuccessful();
		} catch (SQLException e) {
			// TODO: handle exception
		} finally {
			db.endTransaction();
		}
	}

	synchronized public boolean updateRightWrong(boolean isRight, int _id) {
		ContentValues values = new ContentValues();

		if (isRight) {
			values.put("isRight", 1);
			values.put("isWrong", 0);
		} else {
			values.put("isRight", 0);
			values.put("isWrong", 1);
		}
		boolean isCheck = false;
		try {
			db.beginTransaction();
			isCheck = db.update("user_words", values, "Word_Code = ?",
					new String[] { String.valueOf(_id), }) >= 0;
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			// TODO: handle exception
		} finally {
			db.endTransaction();
		}
		return isCheck;
	}

	public int getRightCount() {
		Cursor cursor = db.rawQuery(
				"SELECT * FROM user_words WHERE isRight = 1", null);

		int count = cursor.getCount();
		cursor.close();

		return count;
	}

	/*
	 * 학습 피드백 관련 함수들
	 * ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
	 * ★★★★★★★★★★★★★★★★★★★★★★
	 */

	public boolean putNotice(String contents) {
		Date date = new Date();
		String day = date.get_today();

		Cursor cursor = db.rawQuery("SELECT * FROM feeds WHERE Date = ?",
				new String[] { day });

		if (cursor.getCount() == 0) {

			ContentValues values = new ContentValues();
			values.put("Type", 1);
			values.put("Date", day);
			values.put("Title", "공지사항");
			values.put("Contents", contents);

			cursor.close();

			return db.insert("feeds", null, values) >= 0;

		} else {
			cursor.close();
			return false;
		}
	}

	public boolean putChangeLevel(int ex_lv, int after_lv) {
		Date date = new Date();
		String day = date.get_today();

		ContentValues values = new ContentValues();
		values.put("Type", 2);
		values.put("Date", day);
		values.put("Title", "Lv. " + ex_lv + " -> Lv. " + after_lv);
		settings.edit()
				.putString(MainActivitys.GpreLevel, String.valueOf(after_lv))
				.commit();
		Config.Difficulty = after_lv;
		if (ex_lv < after_lv) {
			values.put("Contents", "한단계 더 높은 수준의 단어가 제공됩니다.");
		} else {
			values.put("Contents", "조금 낮은 수준의 단어가 제공됩니다.");
		}

		return db.insert("feeds", null, values) >= 0;

	}

	public boolean putDay_Of_Study() {
		Date date = new Date();
		String day = date.get_yesterday();

		Cursor cursor = db.rawQuery(
				"SELECT * FROM feeds WHERE Date = ? AND Type = 3 ",
				new String[] { day });

		if (cursor.getCount() == 0) {
			int total_count = 0;
			int mForget = getMforget();
			if (mForget > 0) {
				total_count = mForget + settings.getInt(Config.REVIEW_COUNT, 0);
			} else {
				total_count = settings.getInt(Config.REVIEW_COUNT, 0);
			}

			ContentValues values = new ContentValues();
			values.put("Type", 3);
			values.put("Date", day);
			values.put("Title", "학습 피드백");
			values.put("Total_Count", total_count);
			values.put("Do_count", settings.getInt(Config.REVIEW_COUNT, 0));
			values.put("New_count", settings.getInt(Config.NEW_COUNT, 0));
			Log.e("feeds", "insert : " + cursor.getCount());

			cursor.close();
			settings.edit().putInt(Config.NEW_COUNT, 0).commit();
			settings.edit().putInt(Config.REVIEW_COUNT, 0).commit();
			return db.insert("feeds", null, values) >= 0;

		} else {
			Log.e("feeds", "insert fail");
			cursor.close();
			return false;
		}
	}

	public int getPRememberCount() {
		Cursor cursor = db
				.rawQuery(
						"SELECT * FROM words WHERE Score IS NOT NULL AND State > 0 AND Score <= 0",
						null);

		int count = cursor.getCount();
		cursor.close();

		return count;
	}

	public int getMforget() {
		Cursor cursor = db
				.rawQuery("SELECT * FROM words WHERE Score > 0", null);

		int count = cursor.getCount();
		cursor.close();

		return count;
	}

	public int getKnownCount() {
		Cursor cursor = db.rawQuery(
				"SELECT * FROM words WHERE Score IS NOT NULL AND State > 0 ",
				null);

		int count = cursor.getCount();
		cursor.close();

		return count;
	}

	public int getTotalWordCount(int i) {
		int count;
		Cursor cursor = db.rawQuery("SELECT SUM(1) FROM words", null);

		cursor.moveToFirst();

		count = cursor.getInt(0);
		cursor.close();

		return count;
	}

	public Integer[] getForgetCount() {
		Integer[] count = new Integer[2];
		Cursor cursor = db
				.rawQuery(
						"SELECT SUM(0.05 + Score), SUM(1) FROM words where State > 0 AND Score > 0",
						null);

		cursor.moveToFirst();

		count[0] = cursor.getInt(0);
		count[1] = cursor.getInt(1) - count[0];
		cursor.close();

		return count;
	}

	public int getUnKnownCount() {
		Cursor cursor = db.rawQuery("SELECT * FROM words WHERE State <= 0 ",
				null);

		int count = cursor.getCount();
		cursor.close();

		return count;
	}

	public int getNotRememCount() {
		Cursor cursor = db.rawQuery("SELECT * FROM words WHERE State < 0 ",
				null);

		int count = cursor.getCount();
		cursor.close();

		return count;
	}

	public int getNotShownCount() {
		Cursor cursor = db.rawQuery("SELECT * FROM words WHERE State = 0 ",
				null);

		int count = cursor.getCount();
		cursor.close();

		return count;
	}

	public int getTotalStudy() {
		Cursor cursor = db.rawQuery("SELECT * FROM words WHERE State IS NOT 0",
				null);

		int count = cursor.getCount();
		cursor.close();

		return count;
	}

	public int getWrongCount() {
		Cursor cursor = db.rawQuery(
				"SELECT * FROM user_words WHERE isWrong = 1", null);

		int count = cursor.getCount();
		cursor.close();

		return count;
	}

	public ArrayList<Feed> getFeed() {
		ArrayList<Feed> arrays = new ArrayList<Feed>();

		Cursor cursor = db.rawQuery(
				"SELECT * FROM feeds ORDER BY Feed_Code DESC", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			Feed feed = null;

			while (!cursor.isAfterLast()) {
				switch (cursor.getInt(1)) {
				case Feed.NOTICE:
					feed = new Feed(cursor.getInt(1), cursor.getString(2),
							cursor.getString(3), cursor.getString(4));
					break;
				case Feed.LEVEL_UP:
					feed = new Feed(cursor.getInt(1), cursor.getString(2),
							cursor.getString(3), cursor.getString(4));
					break;
				case Feed.STUDY_FEEDBACK:
					feed = new Feed(cursor.getInt(1), cursor.getString(2),
							cursor.getString(3), cursor.getInt(5),
							cursor.getInt(6), cursor.getInt(7));
					break;

				}

				arrays.add(feed);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return arrays;
	}

	/*
	 * user table 가져오는 부분
	 * ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
	 * ★★★★★★★★★★★★★★★★★★★★★
	 */

	public ArrayList<Word> getCurrentWords() {

		ArrayList<Word> arrays = new ArrayList<Word>();

		Cursor cursor = db.rawQuery(
				"SELECT * FROM words a INNER JOIN user_words b ON a.Word_Code=b.Word_Code"
						+ " WHERE isRight = 0", null);

		cursor.moveToFirst();
		Word word;

		while (!cursor.isAfterLast()) {

			// public Word(int _id, String word, String meaning, int difficulty,
			// int priority, double score, int state, int time, int frequency,
			// boolean isRight, boolean isWrong, int wrongCount)

			/*
			 * int _id, String word, String meaning, int difficulty, int
			 * priority, double score, int state, int time, int frequency,
			 * boolean isRight, boolean isWrong, int wrongCount)
			 */

			word = new Word(cursor.getInt(0), cursor.getString(1),
					cursor.getInt(2), cursor.getInt(3), cursor.getInt(4),
					cursor.getDouble(5), cursor.getInt(6), cursor.getInt(7),
					cursor.getInt(8), cursor.getInt(11) > 0,
					cursor.getInt(12) > 0, cursor.getInt(13), cursor.getInt(14));

			arrays.add(word);
			cursor.moveToNext();
		}
		cursor.close();
		try {

			db.beginTransaction();
			for (int i = 0; i < arrays.size(); i++) {
				arrays.get(i).setMeanList(getMean(arrays.get(i).get_id()));

			}
		} catch (SQLException e) {
			// TODO: handle exception
		} finally {
			db.endTransaction();
		}
		return arrays;
	}

	public int getExState(int word_code) {
		Cursor cursor = db.rawQuery(
				"SELECT * FROM user_words WHERE Word_Code = ?",
				new String[] { String.valueOf(word_code), });
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			return 0;
		}
		int ex_state = cursor.getInt(4);
		cursor.close();

		return ex_state;
	}

	public List<Feed> getLevelList() {
		List<Feed> list = new ArrayList<Feed>();
		Cursor cursor = db.rawQuery(
				"SELECT * FROM feeds WHERE Type = 2 ORDER BY Date DESC", null);
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			return null;
		}

		while (cursor.moveToNext()) {
			list.add(new Feed(3, cursor.getString(2), cursor.getString(3),
					cursor.getString(4)));
		}
		cursor.close();

		return list;
	}

	public List<Feed> getStudyList() {
		List<Feed> list = new ArrayList<Feed>();
		list.clear();
		Cursor cursor = db.rawQuery(
				"SELECT * FROM feeds WHERE Type = 3 ORDER BY Date DESC", null);
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			return null;
		}

		do {
			list.add(new Feed(3, cursor.getString(2), cursor.getString(3),
					cursor.getInt(5), cursor.getInt(6), cursor.getInt(7)));

		} while (cursor.moveToNext());
		cursor.close();

		return list;
	}

	// 튜토리얼 부분 단어셋을 가져오기위한 부분
	public ArrayList<Word> get_test_db(int type) {

		ArrayList<Word> arrays = new ArrayList<Word>();

		Cursor cursor = db.rawQuery(
				"SELECT * FROM words a INNER JOIN test t ON a.Word_Code=t.Word_Code"
						+ " WHERE Type = ?",
				new String[] { String.valueOf(type), });

		cursor.moveToFirst();
		Word word;

		/*
		 * int _id, String word, String meaning, int difficulty, int priority,
		 * double score, int state, int time, int frequency, boolean isRight,
		 * boolean isWrong, int wrongCount)
		 */

		while (!cursor.isAfterLast()) {

			word = new Word(cursor.getInt(0), cursor.getString(1),
					cursor.getInt(2), cursor.getInt(3), cursor.getInt(4),
					cursor.getDouble(5), cursor.getInt(6), cursor.getInt(7),
					cursor.getInt(8), false, false, 0);

			arrays.add(word);
			cursor.moveToNext();
		}
		cursor.close();
		try {

			db.beginTransaction();
			for (int i = 0; i < arrays.size(); i++) {
				arrays.get(i).setMeanList(getMean(arrays.get(i).get_id()));
			}
		} catch (SQLException e) {
			// TODO: handle exception
		} finally {
			db.endTransaction();
		}

		return arrays;

	}

	public Integer[][] getLevelCounting() {
		// TODO Auto-generated method stub

		Integer DB_level_counting[][] = new Integer[6][2];

		try {

			db.beginTransaction();

			for (int i = 1; i <= 6; i++) {
				Cursor cursor = db
						.rawQuery(
								"SELECT "
										+ "count(case when Difficulty= ? then 1 else null end) as totalcount, "
										+ "count(case when state > 0 then 1 else null end) as studycount "
										+ "FROM words "
										+ "GROUP BY Difficulty ",
								new String[] { String.valueOf(i), });

				cursor.moveToFirst();

				if (cursor.getCount() > 0) {
					cursor.moveToPosition(i - 1);

					int totalcount = cursor.getInt(cursor
							.getColumnIndex("totalcount"));
					int studycount = cursor.getInt(cursor
							.getColumnIndex("studycount"));
					if (studycount == 0) {
						DB_level_counting[i - 1][0] = 0;
						DB_level_counting[i - 1][1] = totalcount;
					} else {
						DB_level_counting[i - 1][0] = studycount;
						DB_level_counting[i - 1][1] = totalcount;
					}

				} else {
					DB_level_counting[i - 1][0] = 0;
					DB_level_counting[i - 1][1] = 0;
				}

				cursor.close();
			}
		} catch (SQLException e) {
			// TODO: handle exception
		} finally {
			db.endTransaction();
		}

		return DB_level_counting;
	}

	public void calcReview(int level) {
		// asdf
		// Cursor cursor =
		// db.rawQuery("SELECT * FROM level WHERE difficulty = ? ORDER BY rowid DESC LIMIT 10 ",
		// new String[]{String.valueOf(level),});
		// cursor.moveToFirst();
	}

	// ///////////////////////////////////////////////////////////////
	// //////////// fit forgetting curve : 9th curve /////////////////
	// ///////////////////////////////////////////////////////////////

	public void fitNinthCurve() {
		Cursor cursor = db
				.rawQuery("SELECT * FROM times WHERE nine < 90", null);
		if (cursor.getCount() > 0) {
			db.execSQL("UPDATE times SET nine=100, ninth=10000 WHERE nine < 90");
		}
	}

	// ///////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////
	// ///////// Download Server DB(word, mean table) ////////////////
	// ///////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////

	public void addWordnMeanVersionColumn() { // add Word and Mean Version
												// Column

		Cursor cursor = db.rawQuery("SELECT * FROM words WHERE rowid=0", null);
		if (cursor.getColumnCount() == 10) { // old version : w_modify and
												// w_version is not contained
			db.execSQL("ALTER TABLE words ADD COLUMN w_modify INTEGER");
			db.execSQL("ALTER TABLE words ADD COLUMN w_version INTEGER");
			Log.e("downloadServerDB", "add Word Version Column");
		} else {
			Log.e("downloadServerDB", "no need to add Word Version Column");
		}
		cursor.close();

		cursor = db.rawQuery("SELECT * FROM means WHERE rowid=0", null);
		if (cursor.getColumnCount() == 5) { // old version : w_modify and
											// w_version is not contained
			db.execSQL("ALTER TABLE means ADD COLUMN m_modify INTEGER");
			db.execSQL("ALTER TABLE means ADD COLUMN m_version INTEGER");
			Log.e("downloadServerDB", "add Mean Version Column");
		} else {
			Log.e("downloadServerDB", "no need to add Mean Version Column");
		}
		cursor.close();

	}

	public int getMaxWordVersion(String modify) { // get max word version as
													// modify, namely INSERT,
													// UPDATE
		Log.e("downloadServerDB", "get Max Word Version " + modify);

		int version = 0;

		Cursor cursor = db.rawQuery(
				"SELECT MAX(w_version) FROM words WHERE w_modify='" + modify
						+ "'", null);
		cursor.moveToFirst();
		version = cursor.getInt(0);
		cursor.close();

		return version;
	}

	public int getMaxMeanVersion(String modify) { // get max mean version as
													// modify, namely INSERT,
													// UPDATE
		Log.e("downloadServerDB", "get Max Mean Version " + modify);

		int version = 0;

		Cursor cursor = db.rawQuery(
				"SELECT MAX(m_version) FROM means WHERE m_modify='" + modify
						+ "'", null);
		cursor.moveToFirst();
		version = cursor.getInt(0);
		cursor.close();

		return version;
	}

	public void modifyUserWord(int modify, JSONArray word_code, JSONArray word,
			JSONArray typ_class, JSONArray difficulty, JSONArray priority,
			JSONArray real_time, JSONArray w_modify, JSONArray w_version) {
		// modify user word table : INSERT, UPDATE or DELETE

		Log.e("downloadServerDB", "DBPool:modifyUserWord");

		if (modify == 0) { // insert
			for (int i = 0; i < word_code.length(); i++) {
				db.execSQL("INSERT INTO words(word_code, word, p_wordclass, difficulty,"
						+ "				  Priority, Score, State, Time,"
						+ "				  Frequency, Realtime, w_modify, w_version)"
						+ "VALUES(" + "'"
						+ word_code.optInt(i)
						+ "', "
						+ "'"
						+ word.optString(i)
						+ "', "
						+ "'"
						+ typ_class.optInt(i)
						+ "', "
						+ "'"
						+ difficulty.optInt(i)
						+ "', "
						+ "'"
						+ priority.optInt(i)
						+ "', "
						+ "'0', '0', '0', '0', "
						+ "'"
						+ real_time.optString(i)
						+ "', "
						+ "'"
						+ w_modify.optString(i)
						+ "', "
						+ "'"
						+ w_version.optInt(i) + "')");
			}
			Log.e("downloadServerDB", "DBPool:modifyUserWord:INSERT");

		} else if (modify == 1) { // update
			for (int i = 0; i < word_code.length(); i++) {
				db.execSQL("UPDATE words " + "SET word = '" + word.optString(i)
						+ "', " + "	p_wordclass = '" + typ_class.optInt(i)
						+ "', " + "	difficulty = '" + difficulty.optInt(i)
						+ "', " + "	Priority = '" + priority.optInt(i) + "', "
						+ "	Realtime = '" + real_time.optString(i) + "', "
						+ "	w_modify = '" + w_modify.optString(i) + "', "
						+ "	w_version = '" + w_version.optInt(i) + "'"
						+ "WHERE word_code = '" + word_code.optInt(i) + "'");
			}
			Log.e("downloadServerDB", "DBPool:modifyUserWord:UPDATE");

		} else if (modify == 2) { // delete
			for (int i = 0; i < word_code.length(); i++) {
				db.execSQL("DELETE FROM words " + "WHERE word_code = '"
						+ word_code.optInt(i) + "'");
			}
			Log.e("downloadServerDB", "DBPool:modifyUserWord:DELETE");

		} else { // error
			;
		}
	}

	public void modifyUserMean(int modify, JSONArray mean_code,
			JSONArray _word_code, JSONArray _class, JSONArray mean,
			JSONArray priority, JSONArray m_modify, JSONArray m_version) {
		// modify user mean table : INSERT, UPDATE or DELETE

		Log.e("downloadServerDB", "DBPool_modifyUserMean");

		if (modify == 0) { // insert
			for (int i = 0; i < mean_code.length(); i++) {
				db.execSQL("INSERT INTO means(Mean_Code, Word_Code, Class, Mean,"
						+ "				  M_Priority, m_modify, m_version)"
						+ "VALUES("
						+ "'"
						+ mean_code.optInt(i)
						+ "', "
						+ "'"
						+ _word_code.optInt(i)
						+ "', "
						+ "'"
						+ _class.optInt(i)
						+ "', "
						+ "'"
						+ mean.optString(i)
						+ "', "
						+ "'"
						+ priority.optInt(i)
						+ "', "
						+ "'"
						+ m_modify.optString(i)
						+ "', "
						+ "'"
						+ m_version.optInt(i) + "')");
			}
			Log.e("downloadServerDB", "DBPool_modifyUserMean:INSERT");

		} else if (modify == 1) { // update
			for (int i = 0; i < mean_code.length(); i++) {
				db.execSQL("UPDATE means " + "SET Word_Code = '"
						+ _word_code.optInt(i) + "', " + "	Class = '"
						+ _class.optInt(i) + "', " + "	Mean = '"
						+ mean.optString(i) + "', " + "	M_Priority = '"
						+ priority.optInt(i) + "', " + "	m_modify = '"
						+ m_modify.optString(i) + "', " + "	m_version = '"
						+ m_version.optInt(i) + "'" + "WHERE Mean_Code = '"
						+ mean_code.optInt(i) + "'");
			}
			Log.e("downloadServerDB", "DBPool_modifyUserMean:UPDATE");

		} else if (modify == 2) { // delete
			for (int i = 0; i < mean_code.length(); i++) {
				db.execSQL("DELETE FROM means " + "WHERE Mean_Code = '"
						+ mean_code.optInt(i) + "'");
			}
			Log.e("downloadServerDB", "DBPool_modifyUserMean:DELETE");

		} else { // error
			;
		}
	}

	public boolean putCalendarData(String date) {
		String sql = "SELECT * FROM calendar_data WHERE date = " + date;
		Cursor c = null;

		c = db.rawQuery(sql, null);

		ContentValues values = new ContentValues();

		values.put("date", date);
		values.put("study_time", (Integer.parseInt(date) % 20)); // 오늘 공부 한 시간
		values.put("goal_time", 10); // 목표 공부 시간
		values.put("new_count", Integer.parseInt(settings.getString(
				MainActivitys.GpreTodayLearnCnt, "0"))); // 오늘 새로 공부 한 단어
		values.put("review_count", Integer.parseInt(settings.getString(
				MainActivitys.GpreTodayReviewCnt, "0"))); // 오늘 복습 한 단어
		values.put("will_review_count", settings.getInt(Config.REVIEW_COUNT, 0)); // 복습
																					// 해야
																					// 할
																					// 단어

		if (c.getCount() == 0) { // 아직 해당날짜에 대한 값이 데이터베이스에 없으면 insert
			return db.insert("calendar_data", null, values) == 1;
		} else if (c.getCount() == 1) { // 해당 날짜에 대한 값이 이미 데이터베이스에 있으면 update
			return db.update("calendar_data", values, "date = " + date, null) == 1;
		}
		c.close();
		return false;
	}

	public int[] getCalendarTimeData(String date) {
		String sql = "SELECT * FROM calendar_data WHERE date = " + date;
		Cursor c = null;
		int timeData[] = new int[2];

		try {
			c = db.rawQuery(sql, null);

			if (c.getCount() == 0) {
				Log.d("Database", "" + c.getCount());
				return null;
			}
			c.moveToFirst();

			Log.d("Database", "" + c.getCount());

			timeData[0] = c.getInt(1);
			timeData[1] = c.getInt(2);
		} finally {
			if (c != null) {
				c.close();
			}
		}

		return timeData;
	}

	public int[] getCalendarCountData(String date) {
		String sql = "SELECT * FROM calendar_data WHERE date = " + date;
		Cursor c = null;
		int wordCount[] = new int[3];

		try {
			c = db.rawQuery(sql, null);
			if (c.getCount() == 0) {
				Log.d("Database", "" + c.getCount());
				return null;
			}
			c.moveToFirst();

			Log.d("Database", "" + c.getCount());
			wordCount[0] = c.getInt(3);
			wordCount[1] = c.getInt(4);
			wordCount[2] = c.getInt(5);

		} finally {
			if (c != null) {
				c.close();
			}
		}
		return wordCount;
	}
	
	public int getMonthAverageTime(String date){
		int sum = 0;
		int year, month, day;
		year = Integer.parseInt(date.substring(0, 4));
		month = Integer.parseInt(date.substring(4, 6));
		day = Integer.parseInt(date.substring(6, 8));

		String tempDate = date.substring(0, 6) + "%"; // 해당 월로 시작하는 값들을 다 불러온다.

		String sql = "SELECT study_time FROM calendar_data WHERE date LIKE \'"  + tempDate + "\'";
		Cursor c = null;

		DateTime dateTime = DateTime.forDateOnly(year, month, day);

		int numDayInMonth = dateTime.getNumDaysInMonth();

		try {
			c = db.rawQuery(sql, null);
			if (c.getCount() == 0) {
				Log.d("Database", "" + c.getCount());
				return 0;
			}
			int columnIndex = c.getColumnIndex("study_time");
			c.moveToFirst();
			do{
				sum += c.getInt(columnIndex);
			} while( c.moveToNext());

			//Log.d("Database", "" + c.getCount() + sum);

		} finally {
			if (c != null) {
				c.close();
			}
		}
		return sum/numDayInMonth;
	}

	public int getWeekAverageTime(String date){
		int sum = 0;
		int year, month, day;
		year = Integer.parseInt(date.substring(0, 4));
		month = Integer.parseInt(date.substring(4, 6));
		day = Integer.parseInt(date.substring(6, 8));

		DateTime dateTime = DateTime.forDateOnly(year, month, day);

		int weekDay = dateTime.getWeekDay();

		int min, max;

		max = 7-weekDay;
		min = 6-max;

		DateTime startDay = dateTime.minusDays(min);
		DateTime endDay = dateTime.plusDays(max);

		String startStr = dateTimeToString(startDay);
		String endStr = dateTimeToString(endDay);

		//Log.d("DatabaseWeek", "날짜 : " + startStr + " " + endStr);

		String sql = "SELECT * FROM calendar_data WHERE date >= " + (startStr) + " AND " + "date <= " + (endStr);
		Cursor c = null;

		try {
			c = db.rawQuery(sql, null);
			if (c.getCount() == 0) {
				//Log.d("DatabaseWeek", "null 인 경우" + c.getCount());
				return 0;
			}
			int columnIndex = c.getColumnIndex("study_time");
			c.moveToFirst();
			do{
				sum += c.getInt(columnIndex);
				//Log.d("DatabaseWeek", "공부시간 " + c.getInt(columnIndex) + ", 날짜 : " + c.getString(c.getColumnIndex("date")));
			} while( c.moveToNext());

			//Log.d("DatabaseWeek", "" + c.getCount() + " " + sum);

		} finally {
			if (c != null) {
				c.close();
			}
		}
		//Log.d("DatabaseWeek", "최종 값 : " + c.getCount() + " " + sum);
		return sum/7;
	}

	public String dateTimeToString(DateTime dateTime){

		String year, month, day;

		year = dateTime.getYear().toString();
		if(dateTime.getMonth() < 10){
			month = "0" + dateTime.getMonth().toString();
		}else{
			month = dateTime.getMonth().toString();
		}
		if(dateTime.getDay() < 10){
			day = "0" + dateTime.getDay().toString();
		}else{
			day = dateTime.getDay().toString();
		}

		return year+month+day;
	}

	
	
}
