package org.andlib.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.ihateflyingbugs.vocaslide.R;

public class PercentView extends View {

	private int STROKE_WIDTH = 7;
	private int STROKE_WIDTH_ = 2;
	
	private int state = 0;
	
	public PercentView (Context context) {
		super(context);
		init();
	}
	
	public PercentView (Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public PercentView (Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		paint = new Paint();
		paint.setColor(Color.rgb(0xff, 0xff, 0xff));
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(STROKE_WIDTH);
		paint.setStrokeCap(Paint.Cap.BUTT);
		bgpaint = new Paint();
		bgpaint.setColor(Color.rgb(0xff, 0xff, 0xff));
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
		setPercentage(0, false);
	}
	
	Paint paint;
	Paint bgpaint;
	Paint c_paint;
	Paint c_bgpaint;
	RectF rect;
	float percentage = 0;
	boolean current_lv ;
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//draw background circle anyway
		int left = 0;
		int width = getWidth();
		int top = 0;
		
		rect.set(left + STROKE_WIDTH, top + STROKE_WIDTH, left+width - STROKE_WIDTH, top + width -STROKE_WIDTH); 
		
		if(percentage >= 0) {
			
//			rect.set(left, top, left+width, top + width); 
			if(current_lv){
				
				canvas.drawArc(rect, -90, (3600*percentage), false, c_paint);
				canvas.drawArc(rect, -90 + (3600*percentage), 360 - (360*percentage), false, c_bgpaint);
				
			}else{

				canvas.drawArc(rect, -90, (3600*percentage), false, paint);
				canvas.drawArc(rect, -90 + (3600*percentage), 360 - (360*percentage), false, bgpaint);
			}
			
//			canvas.drawArc(rect, (360*percentage), (360*(100 - percentage)), true, bgpaint);
		}
	}
	
	public void setPercentage(float percentage, boolean current_lv) {
		this.current_lv = current_lv;
		this.percentage = percentage / 1000;
		invalidate();
	}
}