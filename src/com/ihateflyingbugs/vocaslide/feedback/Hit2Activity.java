package com.ihateflyingbugs.vocaslide.feedback;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.indicator.TestFragment;
import com.viewpagerindicator.TabPageIndicator;

public class Hit2Activity extends FragmentActivity {
	private static final String[] CONTENT = new String[] { "Recent", "Artists", "Albums", "Songs", "Playlists", "Genres" };

	RelativeLayout graph_layout;

	ToggleButton hit_push;

	SharedPreferences pushPreference;
	SharedPreferences.Editor pushEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hit);

		hit_push = (ToggleButton) findViewById(R.id.hit_pushBtn);

		pushPreference = getPreferences(MODE_WORLD_READABLE
				| MODE_WORLD_WRITEABLE);
		pushEditor = pushPreference.edit();

		graph_layout = (RelativeLayout) findViewById(R.id.hit_percentage);
		DefaultRenderer rengerer = new DefaultRenderer();

		rengerer.setChartTitle("Graph");
		rengerer.setChartTitleTextSize(30);

		double value[] = { 30, 20, 15, 20, 25 };

		CategorySeries series = new CategorySeries("그래프 내용");
		series.add("1", value[0]);
		series.add("2", value[1]);
		series.add("3", value[2]);
		series.add("4", value[3]);
		series.add("6", value[4]);

		rengerer.setLabelsTextSize(15);
		rengerer.setLegendTextSize(25);

		int colors[] = { Color.BLUE, Color.MAGENTA, Color.YELLOW, Color.RED,
				Color.GREEN };
		for (int i = 0; i < colors.length; i++) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(colors[i]);
			rengerer.addSeriesRenderer(r);
		}

		rengerer.setZoomButtonsVisible(true);
		rengerer.setZoomEnabled(true);

		GraphicalView view = ChartFactory.getPieChartView(this, series,
				rengerer);
		graph_layout.addView(view);

		FragmentPagerAdapter adapter = new GoogleMusicAdapter(getSupportFragmentManager());

		ViewPager pager = (ViewPager)findViewById(R.id.pager);
		pager.setAdapter(adapter);

		TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(pager);


		hit_push.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (hit_push.isChecked()) {
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

	}
	class GoogleMusicAdapter extends FragmentPagerAdapter {
		public GoogleMusicAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return TestFragment.newInstance(CONTENT[position % CONTENT.length]);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length].toUpperCase();
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}
	}
}
