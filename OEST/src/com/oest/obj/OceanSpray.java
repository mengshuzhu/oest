package com.oest.obj;

import java.util.Arrays;

import android.content.Context;
import android.opengl.Matrix;

import com.oest.base.BaseDrawObject;

public class OceanSpray extends BaseDrawObject {

	public OceanSpray(Context context)
	{
		
	}
	private int count = 5;
	
	private float[] speedx = {(float)Math.random(),(float)Math.random(),(float)Math.random(),(float)Math.random(),(float)Math.random()};
	private float[] speedy = {220,210,230,220,200};
	private float[] speedz = {0,0,0,0,0};
	
	private float[] positionx = {(float)Math.random(),
			(float)Math.random(),
			(float)Math.random(),
			(float)Math.random(),
			(float)Math.random()};
	
	private float[] positiony = {10,10,10,10,10};
	private float[] positionz = {0,0,0,0,0};
	
	private float[] rotatey ={0,10,38,24,50};
	
	private float[] rotatez ={0,10,38,24,50};
	
	private float scale = 0.1f;
	
	@Override
	public void simulate() {
		
		float dt = 1f/20f;
		
		for(int i=0;i<count;i++)
		{
			positionx[i] += dt*speedx[i];
			positiony[i] += dt*speedy[i];
			positionz[i] += dt*speedz[i];
			speedy[i] -=9.8;
			
			rotatez[i] +=10;
		}
		
		scale +=0.03;
	}

	@Override
	public void draw(float[] projectionM4, float[] viewM4, float[] modelM4,
			float[] viewPos) {
		
		for(int i=0;i<count;i++)
		{
			float [] oceanSprayM4 = Arrays.copyOf(modelM4,modelM4.length);
	        Matrix.translateM(oceanSprayM4, 0, positionx[i] , positiony[i], positionz[i]);
	        Matrix.scaleM(oceanSprayM4, 0, scale, scale, scale);
	        Matrix.rotateM(oceanSprayM4, 0, 90, 1, 0 , 0);
	       // Matrix.rotateM(oceanSprayM4, 0, rotatey[i], 0, 1 , 0);
	       //  Matrix.rotateM(oceanSprayM4, 0, rotatez[i], 0, 0 , 1);
	        
	        
		    OceanWave.draw( projectionM4, viewM4, oceanSprayM4 ,
		        		viewPos ,
		        		i,
		        		0.8f
		        		);

		}
	}

	@Override
	public void initGLStatus(Context context) {
		// TODO Auto-generated method stub
		
	}
	

}
