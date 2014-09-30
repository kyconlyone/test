package com.ihateflyingbugs.vocaslide.login;


import java.io.IOException;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.data.Config;

import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


public class DocPopup extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.popup_doc);
		
		if(getIntent().getBooleanExtra("activity", false) ){

		}

		String title = null;
		String contents =null;
		int sort = getIntent().getIntExtra("title",1);
		if(sort==1){
			title = "개인정보 이용약관";
			
			try {
				InputStream is = getAssets().open("private.txt");

				// We guarantee that the available method returns the total
				// size of the asset...  of course, this does mean that a single
				// asset can't be more than 2 gigs.
				int size = is.available();

				// Read the entire asset into a local byte buffer.
				byte[] buffer = new byte[size];
				is.read(buffer);
				is.close();

				// Convert the buffer into a string.
				contents = new String(buffer);
				
			} catch (IOException e) {
				// Should never happen!
				throw new RuntimeException(e);
			}
		}else if(sort == 2){
			title = "이용약관";
			try {
				InputStream is = getAssets().open("use.txt");

				// We guarantee that the available method returns the total
				// size of the asset...  of course, this does mean that a single
				// asset can't be more than 2 gigs.
				int size = is.available();

				// Read the entire asset into a local byte buffer.
				byte[] buffer = new byte[size];
				is.read(buffer);
				is.close();

				// Convert the buffer into a string.
				contents = new String(buffer);
				
			} catch (IOException e) {
				// Should never happen!
				throw new RuntimeException(e);
			}
		}else{

		}
		TextView tv_title = (TextView)findViewById(R.id.tv_doc_title);
		TextView tv_contents = (TextView)findViewById(R.id.tv_doc_contents);
		Button bt_close_popup = (Button)findViewById(R.id.bt_close_doc_popup);

		tv_title.setText(title);


		tv_contents.setText(contents);


		bt_close_popup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				finish();
			}
		});



	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	

}
