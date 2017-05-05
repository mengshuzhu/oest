package com.oest.old;

import java.util.List;



import com.oest.draw.Cannon;

import toxi.geom.Vec3D;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

public class FireButtonLeft extends View implements View.OnClickListener {  
	  
    private Paint mPaint;  
      
    private Rect mBounds;  
    
    private OestActivity activity;
      
    public FireButtonLeft(Context context) {
        super(context);  
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  
        mBounds = new Rect();  
        setOnClickListener(this);  
        activity =(OestActivity) context;
    }
  
    @Override  
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);  
        mPaint.setColor(Color.BLUE);  
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);  
        mPaint.setColor(Color.YELLOW);  
        mPaint.setTextSize(30);  
        String text = "Fire";
        mPaint.getTextBounds(text, 0, text.length(), mBounds);  
        float textWidth = mBounds.width();  
        float textHeight = mBounds.height();  
        canvas.drawText(text, getWidth() / 2 - textWidth / 2, getHeight() / 2  
                + textHeight / 2, mPaint);  
    }
   
    @Override  
    public void onClick(View v) {
    	
        new Thread(new Runnable(){
			@Override
			public void run() {
					try {
					List<Cannon> leftcannons = OestActivity.mGLView.mRenderer.ship.leftCannons;
					for(int i=0;i<leftcannons.size();i++)
					{
						leftcannons.get(i).fire();
						Thread.sleep(250);
					}
					return ;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				
			}
        }).start();
        OestActivity.mGLView.mRenderer.ship.lock.lock();
        OestActivity.mGLView.mRenderer.ship.Bbuoyance = 500f;
        OestActivity.mGLView.mRenderer.ship.simulate();
        OestActivity.mGLView.mRenderer.ship.lock.unlock();
    }
  
}  
