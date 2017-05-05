package com.oest.obj;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class FireParticle {
	
    public  FloatBuffer vertex;
    public  FloatBuffer colors;
    public  IntBuffer index;
    public  int indexCount = 0;
    public int n = 10;
    public int radius ;
	public FireParticle(int radius,int n)
	{
		this.radius = radius;
		this.n = n;
    	ByteBuffer byteBuffer = ByteBuffer.allocateDirect((n+1)*12);
		byteBuffer.order(ByteOrder.nativeOrder());
		vertex = byteBuffer.asFloatBuffer();

    	byteBuffer = ByteBuffer.allocateDirect((n+1)*16);
		byteBuffer.order(ByteOrder.nativeOrder());
		colors = byteBuffer.asFloatBuffer();
		index = IntBuffer.allocate(n*3 );
		
		vertex.position(0);
		colors.position(0);
		index.position(0);
		indexCount = 0;
	}
	
	
    public void refresh(float x,float y ,float z,float r,float g,float b)
    {
    	indexCount = 0;
		vertex.position(0);
		colors.position(0);
		index.position(0);

		vertex.put(x);
		vertex.put(y);
		vertex.put(z);
		
		colors.put(r);
		colors.put(g);
		colors.put(b);
		colors.put(1.0f);
		
		
	     for(int i=0; i<n; ++i)
	     {
	    	 vertex.put(x+ radius*(float)Math.cos(2*Math.PI/n*i));
	    	 vertex.put(y+ radius*(float)Math.sin(2*Math.PI/n*i));
	    	 vertex.put(z);
	    	 
	 		colors.put(r);
			colors.put(g);
			colors.put(b);
			colors.put(0.0f);
			if(i+1 == n)
			{
				index.put(0);
				index.put(i+1);
				index.put(1);
			}
			else
			{
				index.put(0);
				index.put(i+1);
				index.put(i+2);
			}
			
			indexCount+= 3;
	     }
	     
		vertex.position(0);
		colors.position(0);
		index.position(0);
		
    }

}
