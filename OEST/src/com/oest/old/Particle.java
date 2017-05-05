package com.oest.old;

import android.opengl.Matrix;
import toxi.geom.Vec3D;

public class Particle {

	Vec3D color = new Vec3D();
	float alpha;
	Vec3D position = new Vec3D();
	Vec3D velocity = new Vec3D();
	Vec3D acceleration = new Vec3D(); 
    float age; 
    float life;
    float size;
    float distantToEye;
    float [] position4F = new float[4];
    float [] modelPos4F = new float[4];
    float [] viewPos4F = new float[4];
}
