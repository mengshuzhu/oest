package com.oest.draw;

import android.content.Context;
import android.opengl.Matrix;

import com.oest.base.BaseDrawObject;
import com.oest.obj.Obj3DData;

public class smallBoat extends BaseDrawObject {

	private Obj3DData data;
	
	public smallBoat(Context context,float [] position)
	{
		super();
		data = new Obj3DData(context,"3DMODLE/smallBoat/smallboat.data");
		this.copyFloat(this.position, position, 3);
	}
	
	@Override
	public void simulate() {
		float dt = 1/30f;
		position[0] +=2;
	}

	@Override
	public void draw(float[] projectionM4, float[] viewM4, float[] modelM4,
			float[] viewPos) {
		
		this.copyFloat(drawModelM4, modelM4,modelM4.length);
		
		Matrix.translateM(drawModelM4, 0, position[0], position[1], position[2]);
		data.draw(projectionM4, viewM4, drawModelM4, viewPos);
		
	}

	@Override
	public void initGLStatus(Context context) {
		data.initGLStatus(context);
	}

}
