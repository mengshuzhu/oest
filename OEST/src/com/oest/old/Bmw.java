package com.oest.old;

import java.util.Arrays;

import android.content.Context;
import android.opengl.Matrix;

public class Bmw {
	
    private Obj3D obj;

    public Bmw(Context context) {
        obj = new Obj3D(context,"bmw.obj","glsl/obj3d.v","glsl/obj3d.f");
    }

    public void draw(float[] mvpMatrix , float[] projectionM4,float[] viewM4, float[] modelM4 ,float [] viewPos) {
        
		float[] m1 = Arrays.copyOf(modelM4, modelM4.length);
		Matrix.scaleM(m1, 0, 1.f, 1.f, 1.f);
		//Matrix.rotateM(m1, 0, 90, -1f, 0, 0);
		Matrix.translateM(m1, 0, 0f, 0f, 0f);
		Matrix.rotateM(m1, 0, 90, -1f, 0, 0);
    	obj.draw(mvpMatrix, projectionM4, viewM4, m1 , viewPos);
    	
    }

}
