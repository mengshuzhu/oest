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
package com.oest.app;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.oest.base.BaseDrawObject;
import com.oest.base.GLDrawLineProgram;
import com.oest.base.GLDrawPointsProgram;
import com.oest.base.Location;
import com.oest.base.RawTriangleProgram;
import com.oest.base.TriangleColorFanProgram;
import com.oest.base.Utils;
import com.oest.draw.Barge;
import com.oest.draw.BulletExplosion;
import com.oest.draw.Common3DModel;
import com.oest.draw.Model3DLocation;
import com.oest.draw.Ship;
import com.oest.draw.ShipWave;
import com.oest.draw.WaterSpray;
import com.oest.draw.WaterWave;
import com.oest.draw.smallBoat;
import com.oest.obj.BulletExplosions;
import com.oest.obj.CannonFire;
import com.oest.obj.Fire;
import com.oest.obj.Obj3DData;
import com.oest.obj.OceanFFT;
import com.oest.obj.OceanSpray;
import com.oest.obj.OceanWave;
import com.oest.obj.WaterSprayS;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
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
    private final float[] modleM4 = new float[16];

    private float zmAngle = 0;
    private float ymAngle = 0;
    private Context context;
    private SurfaceView view;
    private OestActivity activity;
    public static  Thread simulateThread = null;
    public static boolean simulateThreadContinue =true;
    
    public static  Thread simulateThread2 = null;
    public static boolean simulateThreadContinue2 =true;
    
    public static  Thread simulateThread3 = null;
    public static boolean simulateThreadContinue3 =true;

    
    public static List<BaseDrawObject> drawObjs = null;
    
    public OestGLRenderer(Context context,SurfaceView view)
    {
    	this.view = view;
    	this.context = context;
    	activity = (OestActivity)context;
    	for(int i=0;i<16;i++)
    		mModelMatrix[i] = 1.0f;
    }
    
    private OceanFFT ocean;
    public Ship ship;

    private Fire fire,fire1;
    private WaterSpray spray;
    public WaterSprayS Sprays;
    public BulletExplosions bulletExplosions = new BulletExplosions(context);
    
    public Barge barge;
    
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glDepthRangef(0.0f,10.0f);
        GLES20.glClearDepthf(10.0f);
        
        ocean = new OceanFFT(context); 
        
        bulletExplosions.lock.lock();
        bulletExplosions.add(new BulletExplosion(context,new float[]{0,100,0}));
        bulletExplosions.lock.unlock();
        
        CannonFire.initGLProgram(context);
        Obj3DData.initGLProgram(context);
        GLDrawLineProgram.initGLProgram(context);
        GLDrawPointsProgram.initGLProgram(context);
        WaterWave.initGLProgram(context);
        WaterSpray.initGLProgram(context);
        RawTriangleProgram.initGLProgram(context);
        TriangleColorFanProgram.initGLProgram(context);
        BulletExplosion.initGLProgram(context);
        
        OceanWave.initGLProgram(context);
        OceanWave.InitOceanWave(context);
        
        ship = new Ship(context);  
        fire = new Fire(context,50,9.8f);
        fire1 = new Fire(context,50,9.8f);
        //spray = new WaterSpray(context,new float[]{0,0,400f});
        
        Sprays = new WaterSprayS(context); 
        //explosion = new BulletExplosion(context,new float[]{0,50,100});;
        barge = new Barge(context);
        
        if(drawObjs == null)
        {
            drawObjs = new LinkedList<BaseDrawObject>();
            drawObjs.add( new smallBoat(context,new float[]{0,0,100}));
            drawObjs.add( new smallBoat(context,new float[]{0,0,200}));
            drawObjs.add( new smallBoat(context,new float[]{0,0,-100}));
            drawObjs.add( new smallBoat(context,new float[]{0,0,-200}));
            drawObjs.add(new Common3DModel(context,"3DMODLE/rock/rock.data",new float[]{0,0,-300},new float[]{0.3f,0.3f,0.3f}));
        
            drawObjs.add(new Common3DModel(context,"3DMODLE/boat116/boat116.data",new float[]{-300,0,0},new float[]{30,30,30}));

            //drawObjs.add(new Common3DModel(context,"3DMODLE/island/island.data",new float[]{-400,20,0},new float[]{0.02f,0.02f,0.02f}));

            //drawObjs.add(new Common3DModel(context,"3DMODLE/island2/island2.data",new float[]{-400,200,0},new float[]{1f,1f,1f}));

            //drawObjs.add(new Common3DModel(context,"3DMODLE/tree101/tree101.data",new float[]{400,200,0},new float[]{1f,1f,1f}));

            Obj3DData tree101 = new Obj3DData(context,"3DMODLE/tree101/tree101.data");
            
            
            
            for(int i=0;i<40;i++)
            {
            	tree101.locations.add(new Location(new float[]{(float)Math.random()*400,10,(float)Math.random()*400},new float[]{0.1f,0.1f,0.1f},new float[]{(float)Math.random()*180f,0,1,0}));
            }
            
            drawObjs.add(tree101);
            
            drawObjs.add(new Common3DModel(context,"3DMODLE/island4/island4.data",new float[]{200,0,200},new float[]{1f,1f,1f}));


        }
        else
        {
			 Iterator<BaseDrawObject> it =  drawObjs.iterator();
			 while(it.hasNext())
			 {
				 BaseDrawObject obj = it.next();
				 obj.initGLStatus(context);
			 }
        }
        
        startSimulate();
        
        lastTime = System.currentTimeMillis();
        
        tempf.put(-320f);tempf.put(20f);tempf.put(0f);
        
        tempf.put(320f);tempf.put(20f);tempf.put(0f);
        
        tempf.put(0f);tempf.put(20f);tempf.put(-320f);
        
        tempf.put(0f);tempf.put(20f);tempf.put(320f);

        
        tempf.position(0);

    }
    
    private void startSimulate()
    {
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
    						  Thread.sleep(20);
    				          ocean.lock.lock();
    				          ocean.simulate();
    				          ocean.lock.unlock();
    				          
/*    				          spray.lock.lock();
    				          spray.simulate();
    				          spray.lock.unlock();
*/    				          
    					} catch (InterruptedException e) {
    						e.printStackTrace();
    					}
    				}
    			}
            	
            });
        	
        	simulateThread.start();
        }
        
        
        if(true)
        {
        	if(simulateThread2!=null&&simulateThread2.isAlive())
        	{
        		try {
        			simulateThreadContinue2 = false;
					simulateThread2.join();
					simulateThreadContinue2 = true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	
        	simulateThread2  = new Thread(new Runnable(){
        		
    			public void run() {
    				while (simulateThreadContinue2) {
    					try {
    						  Thread.sleep(20);
    				          ship.lock.lock();
    				          ship.simulate();
    				          ship.lock.unlock();
    				          
    				          bulletExplosions.lock.lock();
    				          bulletExplosions.simulate();
    				          bulletExplosions.lock.unlock();
    				          
    				          Sprays.lock.lock();
    				          Sprays.simulate();
    				          Sprays.lock.unlock();
    				          
    					} catch (InterruptedException e) {
    						e.printStackTrace();
    					}
    				}
    			}
            	
            });
        	
        	simulateThread2.start();
        }
        
        
        if(true)
        {
        	if(simulateThread3!=null&&simulateThread3.isAlive())
        	{
        		try {
        			simulateThreadContinue3 = false;
        			simulateThread3.join();
					simulateThreadContinue3 = true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	
        	simulateThread3  = new Thread(new Runnable(){
        		
    			public void run() {
    				while (simulateThreadContinue3) {
    					try {
    						 Thread.sleep(20);
    						  
    						 Iterator<BaseDrawObject> it =  drawObjs.iterator();
    						 
    						 while(it.hasNext())
    						 {
    							 BaseDrawObject obj = it.next();
    							 if(obj.isAlive)
    							 {
    								 obj.lock.lock();
    								 obj.simulate();
    								 obj.lock.unlock();
    							 }
    							 else
    							 {
    								 it.remove();
    							 }
    						 }
    						 
    					} catch (InterruptedException e) {
    						e.printStackTrace();
    					}
    				}
    			}
            	
            });
        	
        	simulateThread3.start();
        }
    }
    public void onPause()
    {
        System.out.println("glrender onPause");
        
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
    	
    	if(OestGLRenderer.simulateThread2!=null&&OestGLRenderer.simulateThread2.isAlive())
    	{
    		try {
    			OestGLRenderer.simulateThreadContinue2 = false;
    			OestGLRenderer.simulateThread2.join();
    			OestGLRenderer.simulateThreadContinue2 = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	
    	if(OestGLRenderer.simulateThread3!=null&&OestGLRenderer.simulateThread3.isAlive())
    	{
    		try {
    			OestGLRenderer.simulateThreadContinue3 = false;
    			OestGLRenderer.simulateThread3.join();
    			OestGLRenderer.simulateThreadContinue3 = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}

    }
    
    public void onResume() {
    	
	}
    
    public void move(float dx ,float dy)
    {
    	///moveX +=dx;
    	///MoveZ +=dy;
    }
    public void TouchMove(float dx ,float dy)
    {
    	addAngleX((dx-dy)/2);
    	//addAngleY(dy);
    }
    
    public float shipSpeed = 0f;
    
    public float shipAccele = 0f;
    
    public float shipDragF = 0f;
    
    public float shipForceF = 0f;
    
    public float shipMass = 10f;
    
    public float shipDirection = 0f;
    public float shipDirectionChange = 0f;
    public float shipViewAngle = 0f;
    
    float oceanMoveX = 0f;
    float oceanMoveZ = 0f;
    
    float globalMoveX = 0f;
    float globalMoveZ = 0f;

    
    float oceanWaveMoveX0 = 0f;
    float oceanWaveMoveZ0 = 0f;
    
    float oceanWaveMoveX1 = 0f;
    float oceanWaveMoveZ1 = 0f;

    
    float oceanWaveMoveX2 = 0f;
    float oceanWaveMoveZ2 = 0f;

    
    float centerX = 0.0f;
    float centerY = 0.0f;
    float centerZ = 0.0f;
    float eyeX = 0.0f;
    float eyeY = 950.0f;
    float eyeZ = 900.0f;
    
    float angleX = 0;
    float angleY =(float)( Math.PI/2 )- (float) Math.atan(10);
    
    float [] viewPos = new float[3];
    public static Long drawTime = new Long(0);
    public static Long fps = new Long(0);
    public Long lastTime = 0L;
    long time = 0;
    float angle = 0;
    
    FloatBuffer tempf = Utils.allocateVertex(4);
    
	@Override
    public void onDrawFrame(GL10 unused) { 
    	
        float dt = 1/30f;
        time += dt;
        
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ , centerX, centerY, centerZ, 0f, 1.0f, 0.0f);
        
        Matrix.setRotateM(ymRotationMatrix, 0, ymAngle, 1.0f , 0, 0);
        Matrix.setRotateM(zmRotationMatrix, 0, zmAngle, 0, 0, 1.0f);
        
        Matrix.multiplyMM(modleM4, 0, ymRotationMatrix, 0, zmRotationMatrix, 0);
        
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);


        float [] projectionM4 = mProjectionMatrix;
        float [] viewM4 = mViewMatrix;
        float [] modelM4 = modleM4;
        
       
         viewPos[0] = eyeX;
         viewPos[1] = eyeY;
         viewPos[2] = eyeZ;
         
         Sprays.lock.lock();
         Sprays.draw(projectionM4, viewM4, modelM4, viewM4);
         Sprays.lock.unlock();
         
         
         shipAccele = (shipForceF - shipDragF)/shipMass;
         
         shipSpeed +=shipAccele;
         
         float K = 0.3f;
         if(shipSpeed > 0)
        	 shipDragF = shipSpeed*shipSpeed*K;
         else
        	 shipDragF = -( shipSpeed*shipSpeed*K);
         
         
         oceanMoveX -=shipSpeed * Math.cos(shipDirection* (3.14/180));
         oceanMoveZ +=shipSpeed * Math.sin(shipDirection* (3.14/180));
         
         globalMoveX -=shipSpeed * Math.cos(shipDirection* (3.14/180));
         globalMoveZ +=shipSpeed * Math.sin(shipDirection* (3.14/180));
         
         
         if(oceanMoveX < -320f)
        	 oceanMoveX = oceanMoveX + 640f;
         
         if(oceanMoveZ < -320f)
        	 oceanMoveZ = oceanMoveZ + 640f;
         
         if(oceanMoveX > 320f)
        	 oceanMoveX = oceanMoveX - 640f;
         
         if(oceanMoveZ > 320f)
        	 oceanMoveZ = oceanMoveZ - 640f;

         
         float[] oceanM4 = Arrays.copyOf(modleM4, modleM4.length);
         Matrix.translateM(oceanM4, 0, oceanMoveX ,0 , oceanMoveZ);
         
         	 ocean.lock.lock();
          	 ocean.draw( mProjectionMatrix, mViewMatrix, oceanM4 , viewPos);
             ocean.lock.unlock();
         
         float [] boatM4 = Arrays.copyOf(modleM4, modleM4.length);
         Matrix.translateM(boatM4, 0, globalMoveX ,0 , globalMoveZ);
         
         
         float [] golbalTranslate = Arrays.copyOf(modleM4, modleM4.length);
         Matrix.translateM(golbalTranslate, 0, globalMoveX ,0 , globalMoveZ);
         
		 Iterator<BaseDrawObject> it =  drawObjs.iterator();
		 while(it.hasNext())
		 {
			 BaseDrawObject obj = it.next();
			 if(obj.isAlive)
			 {
				 obj.lock.lock();
				 obj.draw(projectionM4, viewM4, golbalTranslate , viewM4);
				 obj.lock.unlock();
			 }
		 }
		 
         
         GLDrawLineProgram.draw(mProjectionMatrix, mViewMatrix, modleM4  ,tempf);
        
         float [] shipFontPoint = new float[]{100,0,0,1};
         float [] shipFontPointWord = new float[4];
         float [] shipFontPointView = new float[4];
         
         float [] shipModel = Arrays.copyOf(modleM4, modleM4.length);
         //Matrix.translateM(shipModel, 0, moveX ,0 , moveY);
 		 Matrix.scaleM(shipModel, 0, 0.5f, 0.5f, 0.5f);
 		 Matrix.rotateM(shipModel , 0, shipDirection , 0, 1 , 0);
 		 
 		 if(shipSpeed > 0)
 			 shipDirection += shipDirectionChange;
 		 else
 			shipDirection += 0;
 		
 		 Matrix.multiplyMV(shipFontPointWord, 0, shipModel, 0, shipFontPoint, 0);
 		 Matrix.multiplyMV(shipFontPointView, 0, mViewMatrix, 0, shipFontPointWord , 0);
 		
 		 if(shipFontPointView[0] == 0f)
 			shipFontPointView[0] = 0.00001f;
 		 
 		 shipViewAngle = (float)Math.atan(shipFontPointView[1]/shipFontPointView[0])* (180f/3.14f);
 		
 		 if(shipFontPointView[0]<0 && shipFontPointView[1] >0)
 		 {
 			shipViewAngle = 90f+ shipViewAngle + 90f;
 		 }
 		 else if(shipFontPointView[0]<0 && shipFontPointView[1] < 0)
 		 {
 			shipViewAngle += 180f;
 		 }
 		 else if(shipFontPointView[0]>0 && shipFontPointView[1] <0)
 		 {
 			shipViewAngle +=360f;
 		 }
 		 
 		 	 ship.lock.lock();
 	 		 ship.draw(mProjectionMatrix, mViewMatrix, shipModel , viewPos);
 	 		 ship.lock.unlock();
 	 	
 	 	
 	 	bulletExplosions.lock.lock();
 	 	long begin = System.currentTimeMillis();
 	 	bulletExplosions.draw(projectionM4, viewM4, modelM4, viewPos);
 	 	long end = System.currentTimeMillis() - begin;
 	 	bulletExplosions.lock.unlock();
 	 	
 	 	OestActivity.activity.showMessage("bulletExplosions :"+Long.valueOf(end).toString());
 	 	
        fps++;
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
    private void addAngleX(float addAngle)
    {
    	float d = (float) Math.sqrt(eyeX*eyeX+eyeZ*eyeZ);
    	angleX -= addAngle;
    	eyeX = (float) (Math.sin(angleX) * d);
    	eyeZ = (float) (Math.cos(angleX) * d);
    	
   }
    
    private void addAngleY(float addAngle)
    {
    	if(addAngle >0)
    	{
    		eyeY = eyeY*0.95f;
    	}
    	else
    	{
    		eyeY = eyeY*1.05f;
    	}
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