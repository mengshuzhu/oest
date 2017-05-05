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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.oest.draw.Ship;

import toxi.geom.Vec3D;
import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class OestGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
   
    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];
    private final float[] ymRotationMatrix = new float[16];
    private final float[] zmRotationMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    private float zmAngle = 0;
    private float ymAngle = 0;
    private Context context;
    private SurfaceView view;
    private OestActivity activity;
    public static  Thread simulateThread = null;
    public static boolean simulateThreadContinue =true;
    
    public OestGLRenderer(Context context,SurfaceView view)
    {
    	this.view = view;
    	this.context = context;
    	activity = (OestActivity)context;
    	for(int i=0;i<16;i++)
    		mModelMatrix[i] = 1.0f;
    }
    
    private Triangle mTriangle;
    private Square   mSquare;
    private Cube cube;
    private Sphere sphere;
    private Bmw bmw;
    private OceanFFT ocean;
    private HalfSphere sky;
    public Ship ship;
    public CircleFireGLProgram cannonfireProgram;

    private Fire fire,fire1;
    
    
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glDepthRangef(0.0f,10.0f);
        GLES20.glClearDepthf(10.0f);
        
        ocean = new OceanFFT(context); 
       
        CannonFire.initGLProgram(context);
        Obj3DData.initGLProgram(context);
        
        ship = new Ship(context); 
        
        fire = new Fire(context,50,9.8f);
        fire1 = new Fire(context,50,9.8f);
         
        lastTime = System.currentTimeMillis();
        
        
        if(true)
        {
        	if(simulateThread!=null&&simulateThread.isAlive())
        	{
        		try {
        			simulateThreadContinue = false;
					simulateThread.join();
					simulateThreadContinue = true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	
        	simulateThread  = new Thread(new Runnable(){
        		
    			public void run() {
    				while (simulateThreadContinue) {
    					try {
    						  Thread.sleep(50);
    				          long begin = System.currentTimeMillis();
    				          ship.lock.lock();
    				          ship.simulate();
    				          ship.lock.unlock();
    				          Long shipST = System.currentTimeMillis()-begin;
    				          long bt = System.currentTimeMillis();
    				          ocean.lock.lock();
    				          ocean.simulate();
    				          ocean.lock.unlock();
    				          Long oceanST = System.currentTimeMillis()-bt;
    					} catch (InterruptedException e) {
    						e.printStackTrace();
    					}
    				}
    			}
            	
            });
        	
        	simulateThread.start();
        }

    }
    
    public void move(float dx ,float dy)
    {
    	moveX +=dx;
    	moveY +=dy;
    }
    public void TouchMove(float dx ,float dy)
    {
    	addAngleX(dx);
    	//addAngleY(dy);
    }
    float moveX = 0f;
    float moveY = 0f;
    float centerX = 0.0f;
    float centerY = 0.0f;
    float centerZ = 0.0f;
    float eyeX = 0.0f;
    float eyeY = 950.0f;
    float eyeZ = 900.0f;
    
    float angleX = 0;
    float angleY =(float)( Math.PI/2 )- (float) Math.atan(10);
    
    float [] viewPos = new float[3];
    private void addAngleX(float addAngle)
    {
    	float d = (float) Math.sqrt(eyeX*eyeX+eyeZ*eyeZ);
    	angleX -= addAngle;
    	eyeX = (float) (Math.sin(angleX) * d);
    	eyeZ = (float) (Math.cos(angleX) * d);
    	
   }
    
    private void addAngleY(float addAngle)
    {
    	float d = (float) Math.sqrt(eyeY*eyeY+eyeZ*eyeZ);
    	angleY += addAngle;
    	if( angleY > 0.15f &&  angleY < 1.5f)
    	{
        	eyeY = (float) (Math.sin(angleY) * d);
        	eyeZ = (float) (Math.cos(angleY) * d);
        	//Log.e("angleY",Float.valueOf(angleY).toString());
    	}
    	else
    	{
    		angleY -= addAngle;
    	}
    	//Log.e("angleY", Float.valueOf(angleY).toString());
    }
    public static Long drawTime = new Long(0);
    public static Long fps = new Long(0);
    public Long lastTime = 0L;
    long time = 0;
    float angle = 0;
	@Override
    public void onDrawFrame(GL10 unused) { 
    	
		long tm = System.currentTimeMillis();
        float[] scratch = new float[16];
        float dt = 1/30f;
        time += dt;
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        //GLES20.glEnable(GLES20.GL_CULL_FACE);
        
        //GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA); 
        
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ , centerX, centerY, centerZ, 0f, 1.0f, 0.0f);
        
        // Matrix.perspectiveM(mProjectionMatrix, 0, 90, 1, 0, 3);

        Matrix.setRotateM(ymRotationMatrix, 0, ymAngle, 1.0f , 0, 0);
        Matrix.setRotateM(zmRotationMatrix, 0, zmAngle, 0, 0, 1.0f);
        
        Matrix.multiplyMM(mRotationMatrix, 0, ymRotationMatrix, 0, zmRotationMatrix, 0);
        
        // Matrix.multiplyMM(mViewMatrix,0,mViewMatrix,0,mRotationMatrix,0);
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        // Draw square
         
         //sphere.draw(scratch,mProjectionMatrix, mViewMatrix, mRotationMatrix);
       
         viewPos[0] = eyeX;
         viewPos[1] = eyeY;
         viewPos[2] = eyeZ;
         //bmw.draw(scratch,mProjectionMatrix, mViewMatrix, mRotationMatrix , viewPos);
         // cube.draw(scratch,mProjectionMatrix, mViewMatrix, mRotationMatrix , viewPos);
         
          long to = System.currentTimeMillis();
          ocean.lock.lock();
          ocean.draw(scratch, mProjectionMatrix, mViewMatrix, mRotationMatrix , viewPos);
          ocean.lock.unlock();
          Long oceanDT = System.currentTimeMillis()-to;
         
         //cannonFire.simulate(1.0f/10.0f);
         // cannonFire.render(scratch, mProjectionMatrix, mViewMatrix, mRotationMatrix , viewPos);
         //sky.draw(scratch, mProjectionMatrix, mViewMatrix, mRotationMatrix , viewPos);
         // surface.draw(mProjectionMatrix, mViewMatrix, mRotationMatrix,viewPos);
         
         float [] modelMatrix = Arrays.copyOf(mRotationMatrix, mRotationMatrix.length);
         Matrix.translateM(modelMatrix, 0, moveX ,0 , moveY);
 		 Matrix.scaleM(modelMatrix, 0, 0.5f, 0.5f, 0.5f);
 		 // Matrix.rotateM(modelMatrix, 0, 90, -1, 0, 0);
 		 to = System.currentTimeMillis();
 		 ship.lock.lock();
 		 ship.draw(mProjectionMatrix, mViewMatrix, modelMatrix , viewPos);
 		 ship.lock.unlock();
 		 Long shipDT = System.currentTimeMillis()-to;
 		 
 		 OestActivity.activity.showMessage(String.format("%d %d ", oceanDT,shipDT));
        //  testObj.render(scratch, mProjectionMatrix, mViewMatrix, modelMatrix , viewPos);
 		 
 		 float [] cannonModleM4 = Arrays.copyOf(mRotationMatrix, mRotationMatrix.length);
 		 Matrix.translateM(cannonModleM4, 0, -500, 0,0);

         GLES20.glEnable(GLES20.GL_BLEND);
         GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);

         float [] fireM4 = Arrays.copyOf(mRotationMatrix, mRotationMatrix.length);
         
         Matrix.translateM(fireM4, 0, 0, 0,500);
         Matrix.rotateM(fireM4, 0, angleX*(180f/3.14159f) , 0, 1f, 0);
         
         //fire.simulate(dt,mViewMatrix, mRotationMatrix  , viewPos);
         //fire.render(scratch, mProjectionMatrix, mViewMatrix, fireM4 , viewPos);

         Matrix.translateM(mRotationMatrix, 0, 0, 0,700);
         Matrix.rotateM(mRotationMatrix, 0, angleX*(180f/3.14159f) , 0, 1f, 0);
         //fire1.simulate(dt ,mViewMatrix, mRotationMatrix  , viewPos);
         //fire1.render(scratch, mProjectionMatrix, mViewMatrix, mRotationMatrix  , viewPos);
         

         GLES20.glDisable(GLES20.GL_BLEND);
         
         drawTime = System.currentTimeMillis() - tm;
         fps++;
         lastTime = System.currentTimeMillis();

    }
	
	
	 
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 9050);

    }

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
    * Utility method for debugging OpenGL calls. Provide the name of the call
    * just after making it:
    *
    * <pre>
    * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
    * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
    *
    * If the operation is not successful, the check throws an error.
    *
    * @param glOperation - Name of the OpenGL call to check.
    */
    public static void checkGlError(String glOperation) { 
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

	public float getZmAngle() {
		return zmAngle;
	}

	public void setZmAngle(float zmAngle) {
		this.zmAngle = zmAngle;
	}

	public float getYmAngle() {
		return ymAngle;
	}

	public void setYmAngle(float ymAngle) {
		this.ymAngle = ymAngle;
	}

}