package com.ihateflyingbugs.vocaslide;

import com.ihateflyingbugs.vocaslide.data.Config;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangeNameActivity extends Activity {
	
	private Button changeNameBtn;
	private	EditText nameEdit;
	
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_system_name_change);

		settings = getSharedPreferences(Config.PREFS_NAME,
				Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
		editor = settings.edit();
		
		changeNameBtn = (Button) findViewById(R.id.btn_change);
		nameEdit = (EditText) findViewById(R.id.edit_text_name);
		
		nameEdit.setText(settings.getString(Config.SYSTEM_NAME, ""));
		
		changeNameBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String temp = nameEdit.getText().toString().trim();
				
				if(!temp.equals("")){
					editor.putString(Config.SYSTEM_NAME, temp);
					editor.commit();
					finish();
				}
				else{
					Toast.makeText(getApplicationContext(), "시스템 이름을 바르게 입력하여 주십시오.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		
	}

}
