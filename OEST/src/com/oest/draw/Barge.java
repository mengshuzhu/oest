package com.oest.draw;

import android.content.Context;

import com.oest.base.BaseDrawObject;
import com.oest.obj.Obj3DData;

public class Barge extends BaseDrawObject {

	private Obj3DData data;
	
	public Barge(Context context)
	{
		data = new Obj3DData(context,"3DMODLE/ship/shop6.data");
	}
	
	@Override
	public void simulate() {
		
	}

	@Override
	public void draw(float[] projectionM4, float[] viewM4, float[] modelM4,
			float[] viewPos) {
		
		data.draw(projectionM4, viewM4, modelM4, viewPos);
		
	}

	@Override
	public void initGLStatus(Context context) {
		// TODO Auto-generated method stub
		
	}

}
