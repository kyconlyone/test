package com.ihateflyingbugs.vocaslide.tab;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import com.ihateflyingbugs.vocaslide.R;
import com.ihateflyingbugs.vocaslide.R.id;
import com.ihateflyingbugs.vocaslide.data.ExamContents;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public final class HitTabFragment extends Fragment {
    private static final String KEY_CONTENT = "HitTabFragment:ExamContents";
    static String Title = "";
    private ExamContents exam;

    public static HitTabFragment newInstance(ExamContents examContents) {
        HitTabFragment fragment = new HitTabFragment();
        fragment.exam = new ExamContents(examContents.getTitle(), examContents.getHitPercent(), examContents.getGrade_1(), 
        		examContents.getGrade_23(), examContents.getGrade_45(), examContents.getGrade_67(), examContents.getGrade_89());
        return fragment;
    }

    private String mContent = "???";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	
    	View view = inflater.inflate(R.layout.fragment_hittab, container, false);
    	TextView tv = (TextView)view.findViewById(R.id.hit_percentage_countTv);
    	org.andlib.ui.HitPercentView pv_exampercent =(org.andlib.ui.HitPercentView)view.findViewById(R.id.pv_exampercent);

    	TextView grade_1_hit_percentage = (TextView)view.findViewById(id.grade_1_hit_percentage);
    	TextView grade_2_hit_percentage = (TextView)view.findViewById(id.grade_2_hit_percentage);
    	TextView grade_3_hit_percentage = (TextView)view.findViewById(id.grade_3_hit_percentage);
    	TextView grade_4_hit_percentage = (TextView)view.findViewById(id.grade_4_hit_percentage);
    	TextView grade_5_hit_percentage = (TextView)view.findViewById(id.grade_5_hit_percentage);
    	
    	pv_exampercent.setPercentage(20,20,20,20,20,1);
    	
    	tv.setText(""+exam.getHitPercent());
    	grade_1_hit_percentage.setText(""+exam.getGrade_1());
    	grade_2_hit_percentage.setText(""+exam.getGrade_23());
    	grade_3_hit_percentage.setText(""+exam.getGrade_45());
    	grade_4_hit_percentage.setText(""+exam.getGrade_67());
    	grade_5_hit_percentage.setText(""+exam.getGrade_89());
    	
    	
    	

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }

	
}
