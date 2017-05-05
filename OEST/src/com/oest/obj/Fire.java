package com.oest.obj;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.oest.base.CircleFireGLProgram;

import toxi.geom.Vec3D;
import android.content.Context;
import android.opengl.Matrix;

public class Fire {
	
    int ptlCount;
    float gravity;
    int indexCount = 0;
    
    protected  FloatBuffer vertex;
    protected  FloatBuffer colors;
    protected  IntBuffer index;

	private List<FireParticle> fires = new ArrayList<FireParticle>();
	private List<Particle> particles =new ArrayList<Particle>();
	
	private CircleFireGLProgram render ;
	
	public Fire(Context context,int _count,float _gravity)
	{
    	ptlCount=_count;
    	gravity=_gravity;

		render = new CircleFireGLProgram(context);
		
		for(int i=0;i<_count;i++)
		{
			FireParticle object = new FireParticle(50,10);
			fires.add(object);
			Particle particle = new Particle();
			particles.add(particle);
			initOne(particle);
		}
		
    	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(fires.get(0).vertex.capacity()*4*_count);
		byteBuffer.order(ByteOrder.nativeOrder());
		vertex = byteBuffer.asFloatBuffer();

    	byteBuffer = ByteBuffer.allocateDirect(fires.get(0).colors.capacity()*4*_count);
		byteBuffer.order(ByteOrder.nativeOrder());
		colors = byteBuffer.asFloatBuffer();
		
		index = IntBuffer.allocate(fires.get(0).index.capacity()*_count);
		
	}

	void initOne(Particle particle) {
		
    	particle.position.x = 0;
		particle.position.y = 0;
		particle.position.z = 0;
		particle.color.x = 1.0f;
		particle.color.y = 1.0f;
		particle.color.z = 1.0f;
		particle.alpha = 1.0f;
		particle.velocity.x = ((float)Math.random() - 0.5f )*100;
		particle.velocity.y = ((float)Math.random() - 0.5f )*150+150;
		particle.velocity.z = ((float)Math.random() - 0.5f )*100;
		
		particle.life = 1.5f*(float)Math.random();
		particle.age = 0;

	}

	private float colorIntF(int i)
	{
		return (float)i/(float)255;
	}
	float time = 0;
	void simulate(float dt,float[] viewM4, float[] modelM4 ,float [] viewPos) {
		
		time += dt;
		
		vertex.position(0);
		colors.position(0);
		index.position(0);
		indexCount = 0;

    	for(int i=0;i< ptlCount ;i++)
    	{
    		Particle particle = particles.get(i);
    		FireParticle fire = fires.get(i);
    		
    		if(particle.life > particle.age)
    		{
    			particle.position.addSelf(particle.velocity.x*dt, particle.velocity.y*dt, particle.velocity.z*dt);
    			particle.age += dt;
    			        		
        		if(particle.age > particle.life*0.5 )
        		{
        			particle.color.x = colorIntF(252);
        			particle.color.y = colorIntF(56);
        			particle.color.z = colorIntF(19);
        		}
        		else if(particle.age > particle.life*0.1 )
        		{
        			particle.color.x = colorIntF(255);
        			particle.color.y = colorIntF(252);
        			particle.color.z = colorIntF(107);
        		}
        		else
        		{
        			particle.color.x = colorIntF(255);
        			particle.color.y = colorIntF(255);
        			particle.color.z = colorIntF(255);
        		}
    		}
    		else
    		{
    			initOne(particle);
    		}
    	}
    	Vec3D eye = new Vec3D(viewPos[0],viewPos[1],viewPos[2]);
    	for(int i=0;i< ptlCount ;i++)
    	{
    		Particle particle = particles.get(i);
    		particle.position4F[0] = particle.position.x;
    		particle.position4F[1] = particle.position.y;
    		particle.position4F[2] = particle.position.z;
    		particle.position4F[3] = 1.0f;
    		
    		Matrix.multiplyMV(particle.modelPos4F, 0, modelM4, 0, particle.position4F ,0);
    		Matrix.multiplyMV(particle.viewPos4F, 0, viewM4, 0, particle.modelPos4F ,0);
    		
    		particle.distantToEye = -particle.position.z;
    	}
    	ComparatorParticle comparator=new ComparatorParticle();
    	Collections.sort(particles , comparator);

    	for(int i=0;i< ptlCount ;i++)
    	{
    		Particle particle = particles.get(i);
    		FireParticle fire = fires.get(i);
    		fire.refresh(particle.position.x, particle.position.y, particle.position.z, particle.color.x, particle.color.y, particle.color.z);
    		vertex.put(fire.vertex);
    		colors.put(fire.colors);
    		int temp = vertex.position()/3;
    		for(int j=0;j<fire.indexCount;j++)
    		{
    			index.put(fire.index.get(j)+temp);
    		}
    		indexCount += fire.indexCount;
    	}
    	
		vertex.position(0);
		colors.position(0);
		index.position(0);
	}
	public void render(float[] mvpMatrix , float[] projectionM4,float[] viewM4, float[] modelM4 ,float [] viewPos)
	{
		render.beginRender(mvpMatrix, projectionM4, viewM4,  viewPos, vertex, colors);
		render.render( modelM4, index, indexCount);
		render.endRender();
	}

	class ComparatorParticle implements Comparator<Particle>{
				  
		 public int compare(Particle arg0, Particle arg1)
		 {
			 if(arg0.distantToEye < arg1.distantToEye)
			 return 1;
			 else if(arg0.distantToEye > arg1.distantToEye)
			 return -1;
			 else
			 return 0;
		 }
	}



}
