package com.oest.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class CircleView extends View {

    private Paint mPaint;  
    
    private Rect mBounds;  
      
    public CircleView(Context context) {
        super(context);  
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  
        mBounds = new Rect();  
    }
  
    @Override  
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);  
        
        //canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);  
        mPaint.setColor(Color.WHITE);  
        mPaint.setTextSize(30);  
       // String text = "HHHHH";
        // mPaint.getTextBounds(text, 0, text.length(), mBounds);  
/*        float textWidth = mBounds.width();  
        float textHeight = mBounds.height();  
*//*        canvas.drawText(text, getWidth() / 2 - textWidth / 2, getHeight() / 2  
                + textHeight / 2, mPaint);
*/      
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2 , getHeight() / 2 -5 , mPaint);
    }
  
}
