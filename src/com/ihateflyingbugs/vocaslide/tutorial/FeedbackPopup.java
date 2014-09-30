package com.ihateflyingbugs.vocaslide.tutorial;


import java.io.IOException;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.data.Config;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class FeedbackPopup extends Activity  implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.popup_tutorial);

		String word =null;
		String title = null;
		String contents =null;
		
		int sort = getIntent().getIntExtra("title",1);
		
		TextView tv_tupop_word = (TextView)findViewById(R.id.tv_tupop_word);
		TextView tv_title = (TextView)findViewById(R.id.tv_tupop_title);
		TextView tv_contents = (TextView)findViewById(R.id.tv_tupop_contents);
		ImageView iv_popup_mark = (ImageView)findViewById(R.id.iv_popup_mark);
		Button bt_close_tutopopup = (Button)findViewById(R.id.bt_close_tutopopup);
		
		bt_close_tutopopup.setOnClickListener(this);
		
		if(sort==1){
			tv_tupop_word.setVisibility(View.VISIBLE);
			iv_popup_mark.setVisibility(View.GONE);
			
			word = getIntent().getStringExtra("word");
			title = "�̹� �ƽô� �ܾ�ñ��� !";
			// Convert the buffer into a string.
			contents = "�˰��ִ� �ܾ�� �з��ϰڽ��ϴ�. ";
		}else if(sort == 2){
			tv_tupop_word.setVisibility(View.VISIBLE);
			iv_popup_mark.setVisibility(View.GONE);
			
			word = getIntent().getStringExtra("word");
			
			title = "�ܿ�� ����� ���� �ܾ��!";

			contents = "�ƴϸ� �ܿ����ǵ� ���������ϼ̳���?";
		}else if(sort == 3){
			tv_tupop_word.setVisibility(View.VISIBLE);
			iv_popup_mark.setVisibility(View.GONE);
			
			title = "�ܿ�� ���� �����̱���";
			word = getIntent().getStringExtra("word");

			contents = "�ӽŷ����� ����� ����� �ܿ� �ܾ ���� �ʵ��� �����ص帱�Կ�!";
		}else if(sort == 4){
			tv_tupop_word.setVisibility(View.VISIBLE);
			iv_popup_mark.setVisibility(View.GONE);
			
			word = getIntent().getStringExtra("word");
			
			title = "���� �ܿ�� ��������?";

			contents = "�ܿ�� ����� �ܾ�� �ر⵵ �����ϴ�\n�Ϻ��ϰ� �ܿ�� ������ ���� ���̰� �ɰſ��� ";
		}else if(sort == 5){
			tv_tupop_word.setVisibility(View.VISIBLE);
			iv_popup_mark.setVisibility(View.GONE);
			
			word = getIntent().getStringExtra("word");
			
			title = "���� ��� �ܾ Ȯ������ �����̾��~";

			contents = "��������?";
		}else if(sort == 6){
			tv_tupop_word.setVisibility(View.GONE);
			iv_popup_mark.setVisibility(View.VISIBLE);
			iv_popup_mark.setImageResource(R.drawable.tuto_popup_ques);
			
			word = getIntent().getStringExtra("word");
			
			title = "���� �� �ܿ� �ܾ��Դϴ�";

			contents = "���� ��������� �ܿ��ٰ� ǥ������ ���� �ܾ��";
		}else if(sort == 7){
			tv_tupop_word.setVisibility(View.GONE);
			iv_popup_mark.setVisibility(View.VISIBLE);
			iv_popup_mark.setImageResource(R.drawable.tuto_popup_check);
			
			word = getIntent().getStringExtra("word");
			
			title = "������ �ʿ��� �ܾ��Դϴ�";

			contents = "�̹� �ܿ������ ���� �������� ������ ���� ���� �ִ� �ܾ��";
		}else if(sort == 8){
			tv_tupop_word.setVisibility(View.GONE);
			iv_popup_mark.setVisibility(View.VISIBLE);
			iv_popup_mark.setImageResource(R.drawable.tuto_popup_check);
			
			word = getIntent().getStringExtra("word");
			
			title = "�ƹ��͵� ������";

			contents = "���� �н��� �ܾ��Դϴ�";
		}
		
		
		tv_tupop_word.setText("'"+word+"'");
		tv_title.setText(title);


		tv_contents.setText(contents);	

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_close_tutopopup:
			setResult(Activity.RESULT_OK);
			finish();
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			break;
		}
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
