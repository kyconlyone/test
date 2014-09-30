package org.andlib.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class HitPercentView extends View {

	private int STROKE_WIDTH = 35;

	public HitPercentView(Context context) {
		super(context);
		init();
	}

	public HitPercentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public HitPercentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		paint1 = new Paint();
		paint1.setColor(Color.rgb(255, 0, 0));
		paint1.setAntiAlias(true);
		paint1.setStyle(Paint.Style.STROKE);
		paint1.setStrokeWidth(STROKE_WIDTH);
		paint1.setStrokeCap(Paint.Cap.BUTT);

		paint2 = new Paint();
		paint2.setColor(Color.rgb(255, 94, 0));
		paint2.setAntiAlias(true);
		paint2.setStyle(Paint.Style.STROKE);
		paint2.setStrokeWidth(STROKE_WIDTH);
		paint2.setStrokeCap(Paint.Cap.BUTT);

		paint3 = new Paint();
		paint3.setColor(Color.rgb(255, 184, 0));
		paint3.setAntiAlias(true);
		paint3.setStyle(Paint.Style.STROKE);
		paint3.setStrokeWidth(STROKE_WIDTH);
		paint3.setStrokeCap(Paint.Cap.BUTT);

		paint4 = new Paint();
		paint4.setColor(Color.rgb(171, 242, 0));
		paint4.setAntiAlias(true);
		paint4.setStyle(Paint.Style.STROKE);
		paint4.setStrokeWidth(STROKE_WIDTH);
		paint4.setStrokeCap(Paint.Cap.BUTT);

		paint5 = new Paint();
		paint5.setColor(Color.rgb(0, 216, 255));
		paint5.setAntiAlias(true);
		paint5.setStyle(Paint.Style.STROKE);
		paint5.setStrokeWidth(STROKE_WIDTH);
		paint5.setStrokeCap(Paint.Cap.BUTT);

		rect = new RectF();
		setPercentage(0, 0, 0, 0, 0, 0);
	}

	Paint paint1, paint2, paint3, paint4, paint5;
	RectF rect;
	float percentage1 = 0, percentage2 = 0, percentage3 = 0, percentage4 = 0,
			percentage5 = 0;
	int current_lv = 0;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// draw background circle anyway
		int left = 0;
		int width = getWidth();
		int top = 0;

		rect.set(left + STROKE_WIDTH, top + STROKE_WIDTH, left + width
				- STROKE_WIDTH, top + width - STROKE_WIDTH);

		// rect.set(left, top, left+width, top + width);
		if (current_lv == 1) {

			canvas.drawArc(rect, -90, 
					(360 * percentage1), false, paint1);
			
			canvas.drawArc(rect, 
					-90 + (360 * percentage1),
					(360 * percentage2), false, paint2);
			
			canvas.drawArc(rect, 
					-90 + (360 * (percentage1 + percentage2)),
					(360 * percentage3), false, paint3);
			
			canvas.drawArc(rect, -90
					+ (360 * (percentage1 + percentage2 + percentage3)),
					(360 * percentage4), false, paint4);
			
			canvas.drawArc(
					rect,
					-90 + (360 * (percentage1 + percentage2 + percentage3 + percentage4)),
					(360 * percentage5), false, paint5);

		}

		// canvas.drawArc(rect, (360*percentage), (360*(100 - percentage)),
		// true, bgpaint);
	}

	public void setPercentage(int percentage1, int percentage2,
			int percentage3, int percentage4, int percentage5,
			int current_lv) {
		this.current_lv = current_lv;
		this.percentage1 = (float)percentage1/100;
		this.percentage2 = (float)percentage2/100;
		this.percentage3 = (float)percentage3/100;
		this.percentage4 = (float)percentage4/100;
		this.percentage5 = (float)percentage5/100;
		Log.e("percentage", ""+this.percentage1);
		Log.e("percentage", ""+this.percentage2);
		Log.e("percentage", ""+this.percentage3);
		Log.e("percentage", ""+this.percentage4);
		Log.e("percentage", ""+this.percentage5);
		invalidate();
	}
}