package com.oest.base;

import android.opengl.Matrix;
import toxi.geom.Vec3D;

public class Vec3 extends Vec3D {
	public Vec3()
	{
		super();
	}
	public Vec3(float x,float y,float z)
	{
		super(x,y,z);
	}
	public Vec3(float array[])
	{
		super(array[0],array[1],array[2]);
	}

}
