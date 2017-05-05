package com.oest.draw;

import java.util.Arrays;



import com.oest.base.GLProgram;

import android.content.Context;
import android.opengl.Matrix;

public class SurfaceDraw {
	
	private SurfaceMesh mesh;
	private GLProgram program;
	private float time = 0f;
	public SurfaceDraw(Context context)
	{
		mesh = new SurfaceMesh(64,64);
		program = new GLProgram(context,"glsl/program/surface.v","glsl/program/surface.f");
	}
	
	public void draw(float[] projectionM4,float[] viewM4, float[] modelM4,float[] ViewPos)
	{
		mesh.calculateNose(time+=0.1f);
		mesh.calculateNormal();
		float[] m4 = Arrays.copyOf(modelM4, modelM4.length);
		Matrix.translateM(m4, 0, -800, 0 , -800);
		program.draw(projectionM4, viewM4, m4,ViewPos , mesh.vertex, mesh.normal,mesh.index);
	}
}
