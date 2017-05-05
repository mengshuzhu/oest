package com.oest.draw;

import android.content.Context;
import android.opengl.Matrix;

import com.oest.base.BaseDrawObject;
import com.oest.obj.Obj3DData;

public class Common3DModel extends BaseDrawObject {

	
	private Obj3DData data;
	
	public Common3DModel(Context context,String modelPath ,float [] position)
	{
		super();
		data = new Obj3DData(context,modelPath);
		this.copyFloat(this.position, position, 3);
	}
	public Common3DModel(Context context,String modelPath ,float [] position,float [] scale)
	{
		super();
		data = new Obj3DData(context,modelPath);
		this.copyFloat(this.position, position, 3);
		this.copyFloat(this.scale, scale, 3);
	}


	
	@Override
	public void simulate() {
		
	}

	@Override
	public void draw(float[] projectionM4, float[] viewM4, float[] modelM4,
			float[] viewPos) {
		
		this.copyFloat(drawModelM4, modelM4,modelM4.length);
		
		Matrix.translateM(drawModelM4, 0, position[0], position[1], position[2]);
		Matrix.scaleM(drawModelM4, 0, scale[0], scale[1], scale[2]);
		
		data.draw(projectionM4, viewM4, drawModelM4, viewPos);

	}
	@Override
	public void initGLStatus(Context context) {
		data.initGLStatus(context);
	}

}
