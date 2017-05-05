package com.oest.old;

import java.util.Arrays;

import android.content.Context;
import android.opengl.Matrix;

public class Sphere {
	
    private Obj3D obj;

    public Sphere(Context context) {
        obj = new Obj3D(context,"sphere.obj","glsl/obj3d.v","glsl/obj3d.f");
    }

    public void draw(float[] mvpMatrix , float[] projectionM4,float[] viewM4, float[] modelM4 ,float [] viewPos) {
        
		float[] m1 = Arrays.copyOf(modelM4, modelM4.length);
		Matrix.scaleM(m1, 0, 10.f, 10.f, 10.f);
		Matrix.translateM(m1, 0, 10f, 0f, 0f);
    	obj.draw(mvpMatrix, projectionM4, viewM4, m1 ,viewPos);
    	
    }

}
