package com.oest.draw;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.oest.base.ShaderLoader;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

public class WaterWave {

    protected static  Integer Program = null;
    
    protected static Integer PositionLocation= null;
    protected static Integer projectionLocation= null;
    protected static Integer viewLocation= null;
    protected static Integer modelLocation= null;

	public Lock lock = new ReentrantLock();
	
	private FloatBuffer vertex;
	private FloatBuffer outLine;
	private FloatBuffer velocity;
	
	
	public WaterWave(Context context,FloatBuffer outLine)
	{
		this.outLine = outLine;
		ByteBuffer buffer = ByteBuffer.allocateDirect( 10 * outLine.capacity()* 4 );
		buffer.order(ByteOrder.nativeOrder());
		vertex = buffer.asFloatBuffer();
		
		buffer = ByteBuffer.allocateDirect( 10 * outLine.capacity()* 4 );
		buffer.order(ByteOrder.nativeOrder());
		velocity = buffer.asFloatBuffer();

		
		for(int i=0;i<(outLine.capacity()/3);i++)
		{
			for(int j=0;j<10;j++)
			{
				vertex.put(outLine.get(i*3));
				vertex.put(outLine.get(i*3+1));
				vertex.put(outLine.get(i*3+2));
				
				velocity.put(-10*(float)Math.random());
				velocity.put(0);
				
				if(outLine.get(i*3+2) > 0f)
					velocity.put(10*(float)Math.random());
				else
					velocity.put(-10*(float)Math.random());
			}
		}
		
		vertex.position(0);
		velocity.position(0);
	}
	
	public void simulate()
	{
		float dt = 1f/30f;
		for(int i=0;i<(vertex.capacity()/3);i++)
		{
			vertex.put(i*3,vertex.get(i*3) +  velocity.get(i*3) *dt );
			vertex.put(i*3+1,vertex.get(i*3+1) +  velocity.get(i*3+1) *dt);
			vertex.put(i*3+2,vertex.get(i*3+2) + velocity.get(i*3+2)*dt );
		}
	}
	
	public void draw(float[] projectionM4,float[] viewM4, float[] modelM4 ,float [] viewPos)
	{
    	GLES20.glUseProgram(Program);
    	
        GLES20.glEnableVertexAttribArray(PositionLocation);
        GLES20.glVertexAttribPointer(
        		PositionLocation, 3, GLES20.GL_FLOAT, false, 3*4, vertex );

		GLES20.glUniformMatrix4fv(projectionLocation, 1, false , projectionM4, 0);
		GLES20.glUniformMatrix4fv(viewLocation, 1, false, viewM4 , 0);
		GLES20.glUniformMatrix4fv(modelLocation, 1, false, modelM4 , 0);

		GLES20.glDrawArrays(GLES20.GL_POINTS , 0, vertex.capacity()/3);

		GLES20.glDisableVertexAttribArray(PositionLocation);

	}
	
    public static void initGLProgram(Context context)
    {
    	loadProgram(context,"glsl/program/waterWave.v","glsl/program/waterWave.f");
    }
    
    private static void loadProgram(Context context,String vertexShader,String fragmentShader)
    {
    	Program = ShaderLoader.loadShader(context, vertexShader , fragmentShader );
    	PositionLocation = GLES20.glGetAttribLocation(Program, "Position");
    	projectionLocation     = GLES20.glGetUniformLocation(Program, "Projection");
    	viewLocation           = GLES20.glGetUniformLocation(Program, "View");
    	modelLocation          = GLES20.glGetUniformLocation(Program, "Model");
    }
   
}
