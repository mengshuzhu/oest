package com.oest.draw;

import java.util.Arrays;

import com.oest.app.OestActivity;
import com.oest.app.OestGLSurfaceView;
import com.oest.obj.CannonBullet;
import com.oest.obj.CannonFire;
import com.oest.obj.Obj3DData;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;


import toxi.geom.Vec3D;

public class Cannon {
	
	private Obj3DData cannon;
	private Vec3D position;
	private Vec3D direction;
	private Context context;
	public CannonFire cannonFire = null;
	public CannonBullet bullet = null;
	public CannonBullet drawBullet = null;
	
	public float[] bulletPosition = new float[4];
	public float[] bulletVelocity = new float[3];
	public float[] bulletAcceleration = new float[3];
	
	public Cannon(Context context,Vec3D position,Vec3D direction)
	{
		cannon = new Obj3DData(context,"3DMODLE/cylinder/cylinder.data");
		bullet = new CannonBullet(context);
		initBuffetStatus();
		
		this.position = position;
		this.direction = direction;
		this.context = context;
	} 
	
	private void initBuffetStatus()
	{
		bulletPosition[0] = 0f;
		bulletPosition[1] = 0f;
		bulletPosition[2] = 0f;
		bulletPosition[3] = 1f;
		
		bulletVelocity[0] = 2800f; 
		bulletVelocity[1] = 701f; 
		bulletVelocity[2] = 0f;
		
		bulletAcceleration[0] = 0f;
		bulletAcceleration[1] = -9.8f*100f;
		bulletAcceleration[2] = 0f;
	}

	public void simulate()
	{
		float dt = 1.0f/30.0f;
		if(drawBullet!=null)
		{
			bulletPosition[0] += bulletVelocity[0]*dt;
			bulletPosition[1] += bulletVelocity[1]*dt;
			bulletPosition[2] += bulletVelocity[2]*dt;
			
			bulletVelocity[0] += bulletAcceleration[0]*dt;
			bulletVelocity[1] += bulletAcceleration[1]*dt;
			bulletVelocity[2] += bulletAcceleration[2]*dt;
		}
		
		if(cannonFire!=null && !cannonFire.isOver)
		{
	         cannonFire.simulate(1.0f/10.0f);
		}
	}

	public void draw(float[] projectionM4,float[] viewM4,float[] cannonModel , float[] cannonUnRolateModel  ,float [] viewPos,float angleX)
	{
		float [] m4 = Arrays.copyOf(cannonModel, cannonModel.length);
		Matrix.translateM(m4, 0, position.x ,position.y , position.z);
		cannon.draw(projectionM4, viewM4, m4, viewPos);
		
		if(drawBullet!=null)
		{
			if(drawBullet.bulletModelM4 == null)
				drawBullet.bulletModelM4 = Arrays.copyOf(cannonUnRolateModel, cannonUnRolateModel.length);
			float [] bulletModelM4 = Arrays.copyOf(drawBullet.bulletModelM4, drawBullet.bulletModelM4.length);
			
			Matrix.translateM(bulletModelM4, 0, position.x ,position.y , position.z);
			
			Matrix.translateM(bulletModelM4, 0 , bulletPosition[0], bulletPosition[1], bulletPosition[2]);
			Matrix.scaleM(bulletModelM4, 0, 4, 4, 4);
			drawBullet.draw(projectionM4, viewM4, bulletModelM4 , viewPos);
			
			float [] srcPosition = {0,0,0,1};
			float [] positionWord = new float[4];
			
			Matrix.multiplyMV(positionWord, 0, bulletModelM4, 0, srcPosition, 0);

			if(positionWord[1] <0)
			{
				drawBullet = null;
				WaterSpray spray = new WaterSpray(context,positionWord);
				OestGLSurfaceView.mRenderer.Sprays.lock.lock();
				OestGLSurfaceView.mRenderer.Sprays.add(spray);
				OestGLSurfaceView.mRenderer.Sprays.lock.unlock();
				
				positionWord[1] += 100f;
				
				BulletExplosion explosion = new BulletExplosion(context,positionWord);
				
				OestGLSurfaceView.mRenderer.bulletExplosions.lock.lock();
				OestGLSurfaceView.mRenderer.bulletExplosions.add(explosion);
				OestGLSurfaceView.mRenderer.bulletExplosions.lock.unlock();
				
			}
				
		}
		
		if(cannonFire!=null && !cannonFire.isOver)
		{
			 float [] fm4 = Arrays.copyOf(cannonUnRolateModel, cannonUnRolateModel.length);
			 cannonFire.render(null, projectionM4, viewM4, fm4, viewPos ,angleX);
		}
	}
	
	public void fire()
	{
		cannonFire = new CannonFire(context ,position ,50,9.8f);
		drawBullet = bullet;
		drawBullet.bulletModelM4 = null;
		initBuffetStatus();
	}
}
