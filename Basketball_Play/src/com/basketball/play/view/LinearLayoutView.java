package com.basketball.play.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.LinearLayout;

@SuppressLint("DrawAllocation")
public class LinearLayoutView extends LinearLayout {
	
	public LinearLayoutView(Context context) {  
        super(context);  
    }
	public LinearLayoutView(Context context,AttributeSet attributeSet) {
		super(context,attributeSet);
		// TODO Auto-generated constructor stub
	}
	private int sroke_width = 1;  
	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();  
        //  将边框设为黑色  
        paint.setColor(android.graphics.Color.BLACK);  
        //  画TextView的4个边  
        canvas.drawLine(0, 0, this.getWidth() - sroke_width, 0, paint);  
        canvas.drawLine(0, 0, 0, this.getHeight() - sroke_width, paint);  
        canvas.drawLine(this.getWidth() - sroke_width, 0, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);  
        canvas.drawLine(0, this.getHeight() - sroke_width, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);  
        super.onDraw(canvas);  
	}

}
