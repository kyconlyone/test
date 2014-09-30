package com.ihateflyingbugs.vocaslide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.vocaslide.StudyFragment.StudyAdapter;
import com.ihateflyingbugs.vocaslide.StudyFragment.ViewHoder;
import com.ihateflyingbugs.vocaslide.AsyncTask.Async_get_notice;
import com.ihateflyingbugs.vocaslide.AsyncTask.VocaCallback;
import com.ihateflyingbugs.vocaslide.data.Config;
import com.ihateflyingbugs.vocaslide.data.Date;
import com.ihateflyingbugs.vocaslide.tutorial.Feed;
import com.ihateflyingbugs.vocaslide.tutorial.MainActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class NoticeFragment extends Fragment{

	Context mContext;
	ArrayList<Feed> list;
	NoticeAdapter adapter;
	Handler handler;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_notice_list, container, false);
		mContext = getActivity().getApplicationContext();
		 list = new ArrayList<Feed>();
		ListView lv = (ListView)view.findViewById(R.id.notice_listView);

		handler = new Handler();

		adapter = new NoticeAdapter(mContext, R.layout.itemlist_feed_notice, list);
		lv.setAdapter(adapter);
		adapter.addAll(list);
		lv.setClickable(false);
		
		new Async_get_notice(new VocaCallback() {
			
			@Override
			public void Resonponse(JSONObject response) {
				// TODO Auto-generated method stub
				Log.e("Download Notice", "Download Notice");
				
				try{
					
					JSONArray num 	= response.getJSONArray("Num");	
					JSONArray title = response.getJSONArray("Title");
					JSONArray n_date 	= response.getJSONArray("N_Date");
					JSONArray contents = response.getJSONArray("Notice");
					
					final ArrayList<Feed> list_notice = new ArrayList<Feed>();
					
					for(int i=0; i<num.length(); i++){
						
						Feed notice = new Feed(3, n_date.optString(i), title.optString(i), contents.optString(i));
						
						//Log.e("print test: Num", 	"Num : " + num.optInt(i));
						//Log.e("print test: Title", 	"Title : " + title.optString(i));
						//Log.e("print test: date", 	"Date : " + date.optString(i));
						//Log.e("print test: contents", "Contents : " + contents.optString(i));
						
						list_notice.add(notice);
					}
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							adapter.addAll(list_notice);
							adapter.notifyDataSetChanged();
						
						}
					});
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			@Override
			public void Exception() {
				// TODO Auto-generated method stub
				
			}
		}).execute();
		
		return view;
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SideActivity.setActionBar(false, "공지사항");
	}

	public class NoticeAdapter extends ArrayAdapter<Feed>{
		List<Feed> list_study;
		private LayoutInflater mInflater;

		public NoticeAdapter(Context context, int resourceId, ArrayList<Feed> items){
			super(context, resourceId, items);
			mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.list_study = items;
			mContext= context;
		}

		/* (non-Javadoc)
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			int type = getItemViewType(position);
			Feed feed = list_study.get(position);
			final ViewHoder holder= new ViewHoder();
			Log.e("getView ","" + position + " " + convertView + " type = " + type);
			

			convertView = mInflater.inflate(R.layout.itemlist_feed_notice, null);
			//holder.text_title= (TextView)convertView.findViewById(R.id.tv_feed_study_title);
			holder.ll_feed_notice= (LinearLayout)convertView.findViewById(R.id.ll_feed_notice);
			holder.tv_title = (TextView)convertView.findViewById(R.id.tv_feed_notice_title);
			holder.tv_contents = (TextView)convertView.findViewById(R.id.tv_feed_notice_contents);
			holder.tv_day = (TextView)convertView.findViewById(R.id.tv_feed_notice_day);

			holder.tv_title.setText(""+feed.getTitle());
			holder.tv_contents.setText(""+feed.getContents());
			holder.tv_day.setText(""+feed.getDate().substring(6, 10));
			
			holder.ll_feed_notice.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(holder.tv_contents.getVisibility()==View.VISIBLE){
						FlurryAgent.endTimedEvent("SideActivity_NoticeFragment:OpenNotice");
						FlurryAgent.logEvent("SideActivity_NoticeFragment:CloseNotice");
						holder.tv_contents.setVisibility(View.GONE);
					}else{
						FlurryAgent.logEvent("SideActivity_NoticeFragment:OpenNotice", true);
						holder.tv_contents.setVisibility(View.VISIBLE);
					}
				}
			});
			convertView.setTag(holder);

			return convertView;
		}

	}

	class ViewHoder{
		LinearLayout ll_feed_notice;
		TextView tv_title;
		TextView tv_contents;
		TextView tv_day;
	}
	


	
	
	String starttime;
	String startdate;
	Date date = new Date();

	Map<String, String> articleParams ;
	public void onStart()
	{

		super.onStart();
		articleParams = new HashMap<String, String>();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("SideActivity_NoticeFragment", articleParams);
		// your code
	//	MainActivity.writeLog("[모든단어 암기완료 시작,FinishStudyFragment,1]\r\n");

	}

	public void onStop()
	{
		super.onStop();
		//FlurryAgent.endTimedEvent("NoticeFragment:Start");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		Log.e("splash", startdate+"        "+date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		// your code
	//	MainActivity.writeLog("[모든단어 암기완료 끝,FinishStudyFragment,1,{Start:"+articleParams.get("Start")+",End:"+articleParams.get("End")+"}]\r\n");
	}
	
	
	
}
