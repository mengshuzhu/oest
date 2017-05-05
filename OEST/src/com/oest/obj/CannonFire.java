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

import com.oest.base.ShaderLoader;

import toxi.geom.Vec3D;


import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class CannonFire {
	
	public int ptlCount;
	public float gravity;
    public int indexCount = 0;
    public boolean isOver = false;
    
    protected  FloatBuffer vertex;
    protected  FloatBuffer colors;
    protected  IntBuffer index;
    protected Vec3D position;

	private List<FireParticle> fires = new ArrayList<FireParticle>();
	private List<Particle> particles =new ArrayList<Particle>();
	
	private static Circle circle ;
	
    public static void initGLProgram(Context context)
    {
    	circle = new Circle(250,20,new float[]{0.5f,0.1f,0.0f});
    	Circle.initGLProgram(context);
    }
	
	public CannonFire(Context context,Vec3D position,int _count,float _gravity)
	{
    	ptlCount=_count;
    	gravity=_gravity;
    	this.position = position;

		for(int i=0;i<_count;i++)
		{
			FireParticle object = new FireParticle(150,10);
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
		
/*    	particle.position.x = position.x+((float)Math.random() - 0.5f )*150;
		particle.position.y = position.y+((float)Math.random() - 0.5f )*150;
		particle.position.z = position.z;
*/		particle.position.x = this.position.x;
		particle.position.y = this.position.y;
		particle.position.z = this.position.z;

		particle.color.x = 1.0f;
		particle.color.y = 1.0f;
		particle.color.z = 1.0f;
		particle.alpha = 1.0f;
		particle.velocity.z = ((float)Math.random() - 0.5f )*50;
		particle.velocity.y = ((float)Math.random())*20;
		particle.velocity.x = ((float)Math.random())* 1500;
		
		particle.acceleration.x = -800;
		particle.acceleration.y = 0;
		particle.acceleration.z = 0;
		
		particle.life = 3+(float)Math.random();
		particle.age = 0;
	}

	private float colorIntF(int i)
	{
		return (float)i/(float)255;
	}
	
	public void simulate(float dt) {
		
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
    			particle.velocity.addSelf(particle.acceleration.x*dt, particle.acceleration.y*dt, particle.acceleration.z*dt);
    			if(particle.velocity.x < 0)
    				particle.velocity.x = 0;
    			
    			particle.age += dt;
    			if(particle.age > 2.2)
    			{
    				if(particle.color.x > colorIntF(20))
        			particle.color.x = particle.color.x *0.8f;
    				if(particle.color.y > colorIntF(20))
        			particle.color.y = particle.color.y *0.8f;
    				if(particle.color.z > colorIntF(20))
        			particle.color.z = particle.color.z *0.8f;
    			}
    			else if(particle.age > 1.2 )
        		{
        			particle.color.x = colorIntF(252);
        			particle.color.y = colorIntF(56);
        			particle.color.z = colorIntF(19);
        		}
        		else if(particle.age > 0.3 )
        		{
      		
        			particle.color.x = colorIntF(252);
        			particle.color.y = colorIntF(56);
        			particle.color.z = colorIntF(19);

        		}
        		else
        		{
      		
        			particle.color.x = colorIntF(252);
        			particle.color.y = colorIntF(56);
        			particle.color.z = colorIntF(19);
        		}
    		}
    		else
    		{
    			// initOne(particle);
    		}
    	}
    	
    	ComparatorParticle comparator=new ComparatorParticle();
    	Collections.sort(particles , comparator);

    	for(int i=0;i< ptlCount ;i++)
    	{
    		Particle particle = particles.get(i);
    		FireParticle fire = fires.get(i);
    		if(particle.life > particle.age)
    		{
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
    	}
    	
		vertex.position(0);
		colors.position(0);
		index.position(0);
		
		if(indexCount == 0)
			isOver = true;
	}
	
	
	public void render(float[] mvpMatrix , float[] projectionM4,float[] viewM4, float[] modelM4 ,float [] viewPos,float angleX)
	{
		//render.beginRender(mvpMatrix, projectionM4, viewM4,  viewPos, circle.vertex, circle.colors);
		circle.beginRender(mvpMatrix, projectionM4, viewM4, viewPos);
		for(int i=0;i< ptlCount ;i++)
    	{
    		Particle particle = particles.get(i);
    		float[] tempM4 = Arrays.copyOf(modelM4, modelM4.length);
    		
    		Matrix.translateM(tempM4, 0, particle.position.x, particle.position.y, particle.position.z);
    		Matrix.rotateM(tempM4, 0, 90 , 1, 0 , 0);
    		
    		circle.refreshColor(new float[]{particle.color.x,particle.color.y,particle.color.z});
    		//render.render( tempM4, circle.index, circle.indexCount);
    		circle.render(tempM4);
    	}
		//render.endRender();
		circle.endRender();
	}
	
	static class Circle {
		
	    public  FloatBuffer vertex;
	    public  FloatBuffer colors;
	    public  IntBuffer index;
	    public  int indexCount = 0;
	    public  int fans ;
	    public  int radius ;
	    
		int [] vbo_vertices = new int[2];
		int [] vbo_indices = new int[1];
	    
	    private static  Integer Program = null;
	    private static Integer PositionLocation= null;
	    private static Integer ColorLocation = null;
	    
	    private static Integer projectionLocation= null;
	    private static Integer viewLocation= null;
	    private static Integer modelLocation= null;
	    

		public Circle(int radius,int fans,float [] initColor)
		{
			this.radius = radius;
			this.fans = fans;
	    	ByteBuffer byteBuffer = ByteBuffer.allocateDirect((fans+1)*12);
			byteBuffer.order(ByteOrder.nativeOrder());
			vertex = byteBuffer.asFloatBuffer();

	    	byteBuffer = ByteBuffer.allocateDirect((fans+1)*16);
			byteBuffer.order(ByteOrder.nativeOrder());
			colors = byteBuffer.asFloatBuffer();
			index = IntBuffer.allocate(fans*3 );

			vertex.put(0);
			vertex.put(0);
			vertex.put(0);
			
			colors.put(initColor[0]);
			colors.put(initColor[1]);
			colors.put(initColor[2]);
			colors.put(0.2f);
			
			
		     for(int i=0; i<fans; ++i)
		     {
		    	 vertex.put( radius*(float)Math.cos(2*Math.PI/fans*i));
		    	 vertex.put( radius*(float)Math.sin(2*Math.PI/fans*i));
		    	 vertex.put(0);
		    	 
		 		colors.put(initColor[0]);
				colors.put(initColor[1]);
				colors.put(initColor[2]);
				colors.put(0.0f);
				if(i+1 == fans)
				{
					index.put(0);
					index.put(i+1);
					index.put(1);
				}
				else
				{
					index.put(0);
					index.put(i+1);
					index.put(i+2);
				}
				
				indexCount+= 3;
		     }
		     
			vertex.position(0);
			colors.position(0);
			index.position(0);
			
			GLES20.glGenBuffers(2, vbo_vertices,0);
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices[0]);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertex.capacity() * 4 , vertex , GLES20.GL_STATIC_DRAW);
			
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices[1]);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, colors.capacity() * 4 , colors , GLES20.GL_DYNAMIC_DRAW);
			
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			
			GLES20.glGenBuffers(1, vbo_indices ,0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vbo_indices[0]);
			GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, index.capacity()* 4 ,index , GLES20.GL_STATIC_DRAW );
			
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		}
		public void refreshColor(float [] refreshColor)
		{
			 colors.position(0);
			 colors.put(refreshColor[0]);
			 colors.put(refreshColor[1]);
			 colors.put(refreshColor[2]);
			 colors.put(0.2f);
			
		     for(int i=0; i<fans; ++i)
		     {
		 		colors.put(refreshColor[0]);
				colors.put(refreshColor[1]);
				colors.put(refreshColor[2]);
				colors.put(0.0f);
		     }
		     
			colors.position(0);
		}
		
	    void beginRender(float[] mvpMatrix , float[] projectionM4,float[] viewM4  ,float [] viewPos )
	    {
	        GLES20.glEnable(GLES20.GL_BLEND);
	        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
	    	
	    	GLES20.glUseProgram(Program);
	    	
	    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices[0]);
	        GLES20.glEnableVertexAttribArray(PositionLocation);
	        GLES20.glVertexAttribPointer(
	        		PositionLocation, 3, GLES20.GL_FLOAT, false, 3*4, 0);
	        
	        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices[1]);
			GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, colors.capacity()*4 , colors );

	        GLES20.glEnableVertexAttribArray(ColorLocation);
	        GLES20.glVertexAttribPointer(
	        		ColorLocation, 4 , GLES20.GL_FLOAT, false, 4*4, 0);
	        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	        
			GLES20.glUniformMatrix4fv(projectionLocation, 1, false , projectionM4, 0);
			GLES20.glUniformMatrix4fv(viewLocation, 1, false, viewM4 , 0);

			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vbo_indices[0]);
	    }
	    public void render(float[] modelM4)
	    {
			GLES20.glUniformMatrix4fv(modelLocation, 1, false, modelM4 , 0);
	        GLES20.glDrawElements(
	                GLES20.GL_TRIANGLES, indexCount , GLES20.GL_UNSIGNED_INT, 0);
	    }
	    public void endRender()
	    {
	    	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	    	 
	        GLES20.glDisableVertexAttribArray(PositionLocation);
	        GLES20.glDisableVertexAttribArray(ColorLocation);
	        GLES20.glDisable(GLES20.GL_BLEND);
	    }
	    private static void initGLProgram(Context context)
	    {
	    	loadProgram(context,"glsl/particle.v","glsl/particle.f");
	    }
	    
	    private static void loadProgram(Context context,String vertexShader,String fragmentShader)
	    {
	        	Program = ShaderLoader.loadShader(context, vertexShader , fragmentShader );
	        	PositionLocation = GLES20.glGetAttribLocation(Program, "Position");
	        	ColorLocation = GLES20.glGetAttribLocation(Program, "Color");
	        			
	        	projectionLocation     = GLES20.glGetUniformLocation(Program, "Projection");
	        	viewLocation           = GLES20.glGetUniformLocation(Program, "View");
	        	modelLocation          = GLES20.glGetUniformLocation(Program, "Model");
	        	checkGlError(" com.oest.android.opengl.CannonFire.Circle loadProgram");
	    }
	    
	    private static void checkGlError(String glOperation) {
	        int error;
	        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
	            Log.e("glERROR", glOperation + ": glError " + error);
	            throw new RuntimeException(glOperation + ": glError " + error);
	        }
	    }

		
	}
	

static	class ComparatorParticle implements Comparator<Particle>{
		 public int compare(Particle arg0, Particle arg1) {
			
			 if(arg0.position.y > arg1.position.y)
			 return 1;
			 else if(arg0.position.y < arg1.position.y)
			 return -1;
			 else
			 return 0;
		 }
}

}
