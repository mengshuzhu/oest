package com.oest.draw;

import android.content.Context;
import android.opengl.Matrix;

import com.oest.base.BaseDrawObject;
import com.oest.base.Utils;
import com.oest.obj.Obj3DData;

public class Model3DLocation extends BaseDrawObject {

	private Obj3DData data;
	private float [] position;
	private float [] scale;
	private float [] rotate;
	private float [] modelM4 = new float[16];
	
	public Model3DLocation(Obj3DData data,float [] position,float[] scale,float [] rotate)
	{
		this.data = data;
		this.position = position;
		this.scale = scale;
		this.rotate = rotate;
	}
	
	@Override
	public void initGLStatus(Context context) {
		
	}

	@Override
	public void simulate() {
		
	}

	@Override
	public void draw(float[] projectionM4, float[] viewM4, float[] modelM4,
			float[] viewPos) {
		
		Utils.copyFloat(this.modelM4, modelM4, 16);
		Matrix.translateM(this.modelM4, 0, position[0], position[1], position[2]);
		Matrix.scaleM(this.modelM4, 0, scale[0],scale[1], scale[2]);
		Matrix.rotateM(this.modelM4, 0, rotate[0], rotate[1], rotate[2], rotate[3]);
		
		data.draw(projectionM4, viewM4, this.modelM4 , viewPos);
	}

}
