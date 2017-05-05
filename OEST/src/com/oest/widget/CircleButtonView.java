package com.oest.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class CircleButtonView extends View {

    private Paint mPaint;  
      
    public CircleButtonView(Context context) {
        super(context);  
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  
    }
  
    @Override  
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);  
        mPaint.setColor(Color.YELLOW);  
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2 , getHeight() / 2 -1 , mPaint);
    }
  
}
