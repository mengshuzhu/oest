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

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class OestGLSurfaceView extends GLSurfaceView {

    public final OestGLRenderer mRenderer;
   

    public OestGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new OestGLRenderer(context,this);
        setRenderer(mRenderer);
        
        
        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    private float pointx ;
    private float pointy ;
    
    private float downpointx ;
    private float downpointy ;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.


        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
            	
                float dx = e.getX() - pointx;
                float dy = e.getY() - pointy;
                if(downpointx < getWidth()/2 )
                {
                	mRenderer.move(dx, dy);
                }
                else
                mRenderer.TouchMove(dx / getWidth() *4 , dy /getHeight()*4 );
                
                requestRender();
                pointx = e.getX();
                pointy = e.getY();
                break;
            case MotionEvent.ACTION_DOWN:
                pointx = e.getX();
                pointy = e.getY();
                downpointx = pointx;
                downpointy = pointy;
            	break;
            case MotionEvent.ACTION_UP:
            	break;
        }

        return true;
    }

}
