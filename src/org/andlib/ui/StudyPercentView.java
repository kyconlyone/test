package org.andlib.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ihateflyingbugs.vocaslide.R;

public class StudyPercentView extends View {

	private int STROKE_WIDTH = 60;
	private int STROKE_WIDTH_ = 50;
	
	public StudyPercentView (Context context) {
		super(context);
		init();
	}
	public StudyPercentView (Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public StudyPercentView (Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	private void init() {

		STROKE_WIDTH = getResources().getDimensionPixelSize(R.dimen.big_demen);
		STROKE_WIDTH_= getResources().getDimensionPixelSize(R.dimen.small_demen);
		
		paint = new Paint();
		paint.setColor(Color.rgb(0xff, 0xff, 0xff));
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(STROKE_WIDTH);
		paint.setStrokeCap(Paint.Cap.BUTT);
		
		bgpaint = new Paint();
		bgpaint.setColor(Color.argb(130,0x00, 0x00, 0x00));
		bgpaint.setAntiAlias(true);
		bgpaint.setStyle(Paint.Style.STROKE);
		bgpaint.setStrokeWidth(STROKE_WIDTH_);

		c_bgpaint = new Paint();
		c_bgpaint.setColor(Color.rgb(0, 181, 105));
		c_bgpaint.setAntiAlias(true);
		c_bgpaint.setStyle(Paint.Style.STROKE);
		c_bgpaint.setStrokeWidth(STROKE_WIDTH_);

		c_paint = new Paint();
		c_paint.setColor(Color.rgb(0, 181, 105));
		c_paint.setAntiAlias(true);
		c_paint.setStyle(Paint.Style.STROKE);
		c_paint.setStrokeWidth(STROKE_WIDTH);
		c_paint.setStrokeCap(Paint.Cap.BUTT);
		rect = new RectF();
		rect2 = new RectF();
		setPercentage(0, false);
	}
	Paint paint;
	Paint bgpaint;
	Paint c_paint;
	Paint c_bgpaint;
	RectF rect;
	RectF rect2;
	float percentage = 0;
	boolean current_contents ;
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//draw background circle anyway
		int left = 0;
		int width = getWidth();
		int top = 0;

		rect2.set(left + STROKE_WIDTH+10, top + STROKE_WIDTH+10, left+width - STROKE_WIDTH-10, top + width -STROKE_WIDTH-10); 
		Log.e("rect", ""+(left + STROKE_WIDTH)+"    "+ (top + STROKE_WIDTH)+"    "+(left+width - STROKE_WIDTH)+"    "+(top + width -STROKE_WIDTH));
		rect.set(left + STROKE_WIDTH, top + STROKE_WIDTH, left+width - STROKE_WIDTH, top + width -STROKE_WIDTH); 
		if(percentage >= 0) {
			
//			rect.set(left, top, left+width, top + width); 
			if(current_contents){
				
				canvas.drawArc(rect2, 94 + (360*percentage)-4, 360 - (360*percentage)+4, false, bgpaint);
				canvas.drawArc(rect, 90, (360*percentage), false, paint);
				
			}else{
				canvas.drawArc(rect2, 90, (360*percentage), false, bgpaint);
				canvas.drawArc(rect, 94 + (360*percentage)-4, 360 - (360*percentage)+4, false, paint);
			}
			
//			canvas.drawArc(rect, (360*percentage), (360*(100 - percentage)), true, bgpaint);
		}
	}
	public void setPercentage(float percentage, boolean current_contents) {
		this.current_contents = current_contents;
		this.percentage = percentage / 100;
		invalidate();
	}
}