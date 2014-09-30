package com.ihateflyingbugs.vocaslide;

import org.andlib.ui.PercentView;

import com.ihateflyingbugs.vocaslide.data.DBPool;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyScoreFragment extends Fragment{

	private DBPool db;
	
	private TextView tvRightCount, tvWrongCount, tvNoneCount, tvPercentage;
	private PercentView percentScore;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_myscore, container, false);
		
		tvRightCount = (TextView)view.findViewById(R.id.tvRightCount);
		tvWrongCount = (TextView)view.findViewById(R.id.tvWrongCount);
		tvNoneCount = (TextView)view.findViewById(R.id.tvNoneCount);
		tvPercentage = (TextView)view.findViewById(R.id.tvPercentage);
		
		percentScore = (PercentView)view.findViewById(R.id.percentScore);
		
		db = DBPool.getInstance(getActivity());
		
		int right = db.getRightWordCount();
		int wrong = db.getWorngWordCount();
		int none = db.getNoneWordCount();
		
		float percentage = (right * 100) / (right + wrong + none);
		
		tvRightCount.setText(right + "");
		tvWrongCount.setText(wrong + "");
		tvNoneCount.setText(none + "");
		
		percentScore.setPercentage(percentage,false);
		tvPercentage.setText(percentage + "%");
		
		return view;
	}
}
