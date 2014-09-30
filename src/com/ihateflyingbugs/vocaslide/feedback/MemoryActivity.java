package com.ihateflyingbugs.vocaslide.feedback;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.data.DBPool;

public class MemoryActivity extends Activity {

	private SharedPreferences settings;
	private static DBPool db;
	TextView memorize_word, review_word, anxiety_word, forgetting_word,
			relax_word;
	TextView forgetting_curve_percentage_count;
	ProgressBar forgetting_curve_percentage_bar;

	ImageView info_forgetting_curve;

	Switch memorize_push;

	SharedPreferences pushPreference;
	SharedPreferences.Editor pushEditor;

	TextView memory_pushTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memory);

		pushPreference = getPreferences(MODE_WORLD_READABLE
				| MODE_WORLD_WRITEABLE);
		pushEditor = pushPreference.edit();

		memory_pushTv = (TextView) findViewById(R.id.memory_pushTv);

		memorize_word = (TextView) findViewById(R.id.memorize_word);
		review_word = (TextView) findViewById(R.id.review_word);
		anxiety_word = (TextView) findViewById(R.id.anxiety_word);
		forgetting_word = (TextView) findViewById(R.id.forgetting_word);
		relax_word = (TextView) findViewById(R.id.relax_word);
		Button questionBtn = (Button) findViewById(R.id.questionBtn);

		questionBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MemoryActivity.this,
						ExplainActivity.class));
			}
		});

		forgetting_curve_percentage_count = (TextView) findViewById(R.id.forgetting_curve_percentage_countTv);
		forgetting_curve_percentage_bar = (ProgressBar) findViewById(R.id.forgetting_curve_percentagePb);

		info_forgetting_curve = (ImageView) findViewById(R.id.info_forgetting_curve);

		memorize_push = (Switch) findViewById(R.id.memorize_pushBtn);

		db = DBPool.getInstance(this);

		memorize_word.setText(Integer.toString(db.getKnownCount()));

		Integer f_count[] = db.getForgetCount();
		review_word.setText(Integer.toString(f_count[1]) + f_count[0]);
		anxiety_word.setText(Integer.toString(f_count[1]));
		forgetting_word.setText(Integer.toString(f_count[0]));

		int p_remember = db.getPRememberCount();
		relax_word.setText(Integer.toString(p_remember));

		int not_zero_state = db.getNoZeroState();
		int forgetting_curve_percentage = (not_zero_state / 5);
		forgetting_curve_percentage_count.setText(Integer
				.toString(forgetting_curve_percentage));
		forgetting_curve_percentage_bar
				.setProgress(forgetting_curve_percentage);

		info_forgetting_curve.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(
						MemoryActivity.this,
						com.ihateflyingbugs.vocaslide.feedback.ExplainActivity.class);
				startActivity(intent);
			}
		});

		memorize_push.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (memorize_push.isChecked()) {
					pushEditor.putBoolean("pushFlag", true);
					pushEditor.commit();
					boolean b = pushPreference.getBoolean("pushFlag", false);
					if (b == true) {
						Log.d("toggle", "true");
					}
				} else {
					pushEditor.putBoolean("pushFlag", false);
					pushEditor.commit();
					boolean b = pushPreference.getBoolean("pushFlag", true);
					if (b == false) {
						Log.d("toggle", "false");
					}
				}
			}
		});

		memory_pushTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(
						MemoryActivity.this,
						com.ihateflyingbugs.vocaslide.popup.OneButtonPopUp.class);
				i.putExtra("title", 2);
				startActivity(i);
			}
		});

	}

}
