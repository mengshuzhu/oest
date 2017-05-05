/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oest.old;

import com.oest.app.R;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class OestActivity extends Activity {

    public static OestGLSurfaceView mGLView;
    public static OestActivity activity;
    private static  Thread perSecondThread = null;
    private static Thread requestRenderThred = null;
    
    public TextView text;
    public TextView message ;
    public CircleButtonView button;
    public CircleView circle;
    public FireButtonLeft firebuttonLeft;
    public FireButtonRight firebuttonRight;
    
    public Handler perSecondHandler = new Handler() {
        public void handleMessage(Message msg) {
             switch (msg.what) {
                  case 1:
                	  String fps = new Long(OestGLRenderer.fps).toString();
                	  //System.out.println(fps);
                	  text.setText(fps);
                	  OestGLRenderer.fps = 0L;
                  break;   
             }   
             super.handleMessage(msg);   
        }   
    };
    public Handler perDrawHandler = new Handler() {
        public void handleMessage(Message msg) {
             switch (msg.what) {
                  case 1:   
                	  OestGLRenderer.fps++;
                  break;   
             }   
             super.handleMessage(msg);   
        }   
    };  
    public Handler showMessageHandler = new Handler() {
        public void handleMessage(Message msg) {
        	 message.setText(msg.obj.toString());
             super.handleMessage(msg);   
        }   
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        activity = this;
        mGLView = new OestGLSurfaceView(this);
        setContentView(R.layout.rela);
        RelativeLayout layout = (RelativeLayout) this.findViewById(R.id.rela);
        
        mGLView.setOnTouchListener(new View.OnTouchListener(){
        	float downx ;
        	float downy ;
        	@Override
        	public boolean onTouch(View paramView, MotionEvent event ) {
    		
        	float x = event.getX();
        	float y = event.getY();
        	if(event.getX() < paramView.getWidth() /2)
        	{
                switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                	LayoutParams paramb;
                	float d = (float) Math.sqrt((x-downx)*(x-downx) + (y-downy)*(y-downy));
                	if(d < circle.getHeight()/2)
                	{
                    	paramb = (LayoutParams) button.getLayoutParams();
                    	paramb.topMargin = (int) event.getY() - button.getHeight()/2;
                    	paramb.leftMargin = (int) event.getX() - button.getWidth()/2;
                    	button.setLayoutParams(paramb);
                	}
                    break;
                case MotionEvent.ACTION_DOWN:
                	
                	System.out.println("ACTION_DOWN");
                	LayoutParams params = (LayoutParams) circle.getLayoutParams();
                	params.topMargin = (int) event.getY() - circle.getHeight()/2;
                	params.leftMargin = (int) event.getX() - circle.getWidth()/2;
                	circle.setLayoutParams(params);
                	
                	paramb = (LayoutParams) button.getLayoutParams();
                	paramb.topMargin = (int) event.getY() - button.getHeight()/2;
                	paramb.leftMargin = (int) event.getX() - button.getWidth()/2;
                	button.setLayoutParams(paramb);

                	downx = event.getX();
                	downy = event.getY();
                	circle.setVisibility(View.VISIBLE);
                	button.setVisibility(View.VISIBLE);
                	break;
                case MotionEvent.ACTION_UP:
                	System.out.println("ACTION_UP");
                	circle.setVisibility(View.INVISIBLE);
                	button.setVisibility(View.INVISIBLE);
                	break;
                }
        	}
    		return false;
    	}
        });
        layout.addView(mGLView);
        
        text = new TextView(this);
        text.setText("hello");
        layout.addView(text);
        
        message = new TextView(this);
        message.setText("message");
        RelativeLayout.LayoutParams messageLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        messageLayout.topMargin = 0;
        messageLayout.leftMargin = 200;
        layout.addView(message,messageLayout);
        
        
        button = new CircleButtonView(this);
        
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
        params.topMargin = 150;
        //button.setLayoutParams(params);
        layout.addView(button,params);
        button.setVisibility(View.INVISIBLE);
        
        RelativeLayout.LayoutParams cparams = new RelativeLayout.LayoutParams(200, 200);
        cparams.topMargin = 350;
        firebuttonRight = new FireButtonRight(this);
        layout.addView(firebuttonRight,cparams);
        
        cparams = new RelativeLayout.LayoutParams(200, 200);
        cparams.topMargin = 350;
        cparams.leftMargin = 220;
        
        firebuttonLeft = new FireButtonLeft(this);
        layout.addView(firebuttonLeft,cparams);
        
        
        RelativeLayout.LayoutParams dparams = new RelativeLayout.LayoutParams(250, 250);
        dparams.topMargin = 450;
        circle = new CircleView(this);
        //circle.setLayoutParams(params);
        layout.addView(circle,dparams);
        circle.setVisibility(View.INVISIBLE);
        
        if(perSecondThread == null)
        {
        	perSecondThread  = new Thread(new Runnable(){

    			@Override
    			public void run() {
    				while (true) {
    					try {
    						Thread.sleep(1000);
    	                    Message message = new Message();   
    	                    message.what = 1;   
    	                    activity.perSecondHandler.sendMessage(message);   

    					} catch (InterruptedException e) {
    						e.printStackTrace();
    					}
    				}
    			}
            	
            });
        	
        	perSecondThread.start();

        }
        if(requestRenderThred == null)
        {
        	requestRenderThred  = new Thread(new Runnable(){

    			@Override
    			public void run() {
    				while (true) {
    					try {
							Thread.sleep(10);
							mGLView.requestRender();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
    					
    				}
    			}
            	
            });
        	requestRenderThred.start();
        }
        
        System.out.println("onCreate");
    }
    public void showMessage(String messageStr)
    {
    	 Message message = new Message();   
        message.what = 1;   
        message.obj = new String(messageStr);
        activity.showMessageHandler.sendMessage(message);   
    }
    @Override
    protected void onPause() {
       // if(thread!= null)
        //thread.stop();

        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
        System.out.println("onPause");
        
    	if(OestGLRenderer.simulateThread!=null&&OestGLRenderer.simulateThread.isAlive())
    	{
    		try {
    			OestGLRenderer.simulateThreadContinue = false;
    			OestGLRenderer.simulateThread.join();
    			OestGLRenderer.simulateThreadContinue = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}

    }


	@Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
        System.out.println("onResume");
    }

}