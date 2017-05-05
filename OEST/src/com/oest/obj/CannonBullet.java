package com.oest.obj;

import android.content.Context;

public class CannonBullet {

	private Obj3DData sphere;
	public float[] bulletModelM4 = null;
	
	public CannonBullet(Context context)
	{
		sphere = new Obj3DData(context,"3DMODLE/smallSphere/smallSphere.data");
	}
	
    public void draw(float[] projectionM4,float[] viewM4, float[] modelM4 ,float [] viewPos)
	{
    	sphere.draw(projectionM4, viewM4, modelM4, viewPos);
	}
	
}
