package com.ihateflyingbugs.vocaslide.feedback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.vocaslide.StudyFragment.StudyAdapter;
import com.ihateflyingbugs.vocaslide.AsyncTask.Async_get_FAQ;
import com.ihateflyingbugs.vocaslide.AsyncTask.VocaCallback;
import com.ihateflyingbugs.vocaslide.data.Config;
import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.data.DBPool;
import com.ihateflyingbugs.vocaslide.data.Date;
import com.ihateflyingbugs.vocaslide.data.FAQ;
import com.ihateflyingbugs.vocaslide.data.MyQnA;
import com.ihateflyingbugs.vocaslide.tutorial.Feed;
import com.ihateflyingbugs.vocaslide.tutorial.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FAQActivity extends Activity{

	private Activity thisActivity;

	private Handler handler;

	Context mContext;
	DBPool db;
	List<FAQ> list_faq;
	FAQAdapter adapter;


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_faq_list);
		ListView lv = (ListView)findViewById(R.id.faq_listView);
		
		Button bt_call_qna = (Button)findViewById(R.id.bt_call_qna);
		Button bt_send_email  = (Button)findViewById(R.id.bt_send_email);
		
		bt_call_qna.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		bt_send_email.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:01071017935" ));
                startActivity(intent);
                
				
			}
		});

		mContext = getApplicationContext();

		handler = new Handler();

		list_faq = new ArrayList<FAQ>();

		adapter = new FAQAdapter(mContext, R.layout.itemlist_feed_faq, list_faq);
		lv.setAdapter(adapter);
		lv.setClickable(false);
		new Async_get_FAQ(new VocaCallback() {

			@Override
			public void Resonponse(JSONObject response) {
				// TODO Auto-generated method stub
				Log.e("Download FAQ", "Download FAQ");

				list_faq.clear();

				try{

					JSONArray num 	= response.getJSONArray("Num");	
					JSONArray q_date = response.getJSONArray("Q_Date");
					JSONArray question = response.getJSONArray("Question");
					JSONArray a_date = response.getJSONArray("A_Date");
					JSONArray answer = response.getJSONArray("Answer");

					for(int i=0; i<num.length(); i++){
						FAQ faq = new FAQ(num.optInt(i), q_date.optString(i), question.optString(i), a_date.optString(i), answer.optString(i));
						list_faq.add(faq);
					}

					handler.post(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub

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

	}







	public class FAQAdapter extends ArrayAdapter<FAQ>{
		List<FAQ> list_FAQ;
		private LayoutInflater mInflater;

		public FAQAdapter(Context context, int resourceId, List<FAQ> list_faq){
			super(context, resourceId, list_faq);
			mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.list_FAQ = list_faq;
			mContext= context;
		}



		/* (non-Javadoc)
		 * @see android.widget.ArrayAdapter#addAll(java.util.Collection)
		 */
		@Override
		public void addAll(Collection<? extends FAQ> collection) {
			// TODO Auto-generated method stub
			list_FAQ.addAll(collection);
			this.notifyDataSetChanged();
		}



		/* (non-Javadoc)
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			int type = getItemViewType(position);
			final FAQ faq = list_FAQ.get(position);
			final ViewHoder holder = new ViewHoder();
			System.out.println("getView " + position + " " + convertView + " type = " + type);


			convertView = mInflater.inflate(R.layout.itemlist_feed_faq, null);
			//holder.text_title= (TextView)convertView.findViewById(R.id.tv_feed_study_title);

			holder.ll_faq_question = (LinearLayout)convertView.findViewById(R.id.ll_faq_question);
			holder.ll_faq_answer = (LinearLayout)convertView.findViewById(R.id.ll_faq_answer);
			holder.tv_question = (TextView)convertView.findViewById(R.id.tv_feed_faq_title);
			holder.tv_answer = (TextView)convertView.findViewById(R.id.tv_feed_faq_contents);
			holder.iv_answer = (ImageView)convertView.findViewById(R.id.iv_faq_question);

			holder.iv_question = (ImageView)convertView.findViewById(R.id.tv_study_feed_month);
			holder.tv_question.setText(faq.getQuestion());
			holder.tv_answer.setText(faq.getAnswer());

			holder.tv_answer.setEllipsize(TruncateAt.END);
			holder.tv_answer.setMaxLines(2);

			holder.ll_faq_question.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});

			holder.ll_faq_answer.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!holder.isOpen){
						FlurryAgent.logEvent("SideActivity_FAQFragment:OpenFAQ", true);
						holder.isOpen = true;
						holder.tv_answer.setEllipsize(TruncateAt.MARQUEE);
						holder.tv_answer.setMaxLines(500);
					}else{
						FlurryAgent.endTimedEvent("SideActivity_FAQFragment:OpenFAQ");
						FlurryAgent.logEvent("SideActivity_FAQFragment:CloseFAQ", true);
						holder.tv_answer.setEllipsize(TruncateAt.END);
						holder.tv_answer.setMaxLines(2);
						holder.isOpen = false;
					}
				}
			});


			//			holder.tv_question.setText(""+faq.getQdate().substring(0, 4)+"."+faq.getQdate().substring(4, 6));
			//			holder.tv_answer.setText(""+faq.getAdate().substring(6, faq.getAdate().length()));

			convertView.setTag(holder);

			return convertView;
		}

	}

	class ViewHoder{
		LinearLayout ll_faq_question;
		LinearLayout ll_faq_answer;
		TextView tv_question;
		TextView tv_answer;
		ImageView iv_answer;
		ImageView iv_question;
		boolean isOpen = false;
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
		FlurryAgent.logEvent("SideActivity_FAQFragment", articleParams);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 시작,FinishStudyFragment,1]\r\n");

	}

	public void onStop()
	{
		super.onStop();
		//FlurryAgent.endTimedEvent("SideActivity_FAQFragment");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		Log.e("splash", startdate+"        "+date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 끝,FinishStudyFragment,1,{Start:"+articleParams.get("Start")+",End:"+articleParams.get("End")+"}]\r\n");
	}
}
