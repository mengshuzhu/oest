package com.oest.draw;

import java.util.Arrays;

import com.oest.obj.OceanWave;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class ShipWave {

	int waveCount = 30;
	
	float moveX[] = new float[waveCount];
	float speedX = 3f;
	
	float moveZ[] = new float[waveCount];
	float speedZ = 0.2f;
	
/*	int sampleIndexs [] = {0,1,2,3,4,0,1,2,3,4,0,1,2,3,4,0,1,2,3,4,0,1,2,3,4,0,1,2,3,4};
*/
	int sampleIndexs [] = new int[waveCount];

	float [] angle = new float[waveCount];
	float [] angledirection = new float[waveCount];
	
	public ShipWave(Context context)
	{
		for(int i=0;i<waveCount;i++)
		{
			moveX[i] = (i-2)*-20 ;
			moveZ[i] = 0;
			angle[i] = 360*(float)Math.random();
		}
		
		//Arrays.fill(angle, 0.0f);
		Arrays.fill(angledirection, 1.0f);
		Arrays.fill(sampleIndexs,1);
	}
	public void simulate()
	{
		
	}
	
    public void draw(float[] projectionM4,float[] viewM4, float[] modelM4 ,float [] viewPos)
	{
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		for(int i=0;i<waveCount;i++)
		{
			moveX[i] -=speedX;
			if(i%2 == 0)
				moveZ[i] -=speedZ;
			else
				moveZ[i] +=speedZ;
			
	        float[] oceanwaveM4 = Arrays.copyOf(modelM4, modelM4.length);
	        Matrix.translateM(oceanwaveM4, 0, moveX[i] , 0, moveZ[i]);
	        float alpha = 1.0f;
	        if(moveX[i]< -400)
	        {
	        	alpha = (600+moveX[i])/200f;
	        }
	        
	        if(moveX[i] > 100)
	        {
	        	
	        }
	        
	        
	        // Matrix.scaleM(oceanwaveM4, 0, scale[i], 0 , scale[i]);
	        
	        Matrix.rotateM(oceanwaveM4, 0, angle[i] , 0, 1, 0);
	        OceanWave.draw( projectionM4, viewM4, oceanwaveM4 ,
	        		viewPos ,
	        		sampleIndexs[i],
	        		alpha-0.5f
	        		);
	        
	        if(moveX[i] < -600)
	        {
	        	moveZ[i] = 0;
	        	moveX[i] = 80f;
	        }
	        
	        if(angledirection[i] >0)
	        	angle[i] += 1f;
	        else
	        	angle[i] -= 1f;
	        
	        if(angle[i]<0)
	        	angledirection[i] = 1;
	        if(angle[i]>360)
	        	angledirection[i] = -1;

	        
	        	
		}
		
		GLES20.glDisable(GLES20.GL_BLEND);
		
	}
}
