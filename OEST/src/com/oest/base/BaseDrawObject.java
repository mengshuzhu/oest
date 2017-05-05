package com.oest.base;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;

public abstract class BaseDrawObject {

	public float [] drawModelM4 = new float[16];
	public float [] modelM4 = new float[16];
	
	public float [] position = new float[3];
	public float [] scale = new float[]{1,1,1};
	
	public Lock lock = new ReentrantLock();
	public boolean isAlive = true;
	public BaseDrawObject()
	{
		position[0] = 0f;
		position[1] = 0f;
		position[2] = 0f;
	}
	public abstract void initGLStatus(Context context);
	public abstract void simulate();
	public abstract void draw(float[] projectionM4,float[] viewM4, float[] modelM4 ,float [] viewPos);
	public void copyFloat(float [] tfloat,float[] sfloat,int count)
	{
		for(int i=0;i<count;i++)
		{
			tfloat[i] = sfloat[i];
		}
	}
}
