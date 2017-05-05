package com.oest.base;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.opengl.GLES20;

public class TriangleColorFanProgram {

    protected static Integer Program = null;
    
    protected static Integer PositionLocation= null;
    protected static Integer colorLocation = null;
    
    protected static Integer projectionLocation= null;
    protected static Integer viewLocation= null;
    protected static Integer modelLocation= null;
    
	public static void draw(float[] projectionM4,float[] viewM4, float[] modelM4 ,
			float [] viewPos,FloatBuffer color ,FloatBuffer vertex,int FanCount,int vertexPerFan)
	{
    	GLES20.glUseProgram(Program);
    	
        GLES20.glEnableVertexAttribArray(PositionLocation);
        GLES20.glVertexAttribPointer(
        		PositionLocation, 3, GLES20.GL_FLOAT, false, 3*4, vertex );
        
        GLES20.glEnableVertexAttribArray(colorLocation);
        GLES20.glVertexAttribPointer(
        		colorLocation, 4, GLES20.GL_FLOAT, false, 4*4, color );


		GLES20.glUniformMatrix4fv(projectionLocation, 1, false , projectionM4, 0);
		GLES20.glUniformMatrix4fv(viewLocation, 1, false, viewM4 , 0);
		GLES20.glUniformMatrix4fv(modelLocation, 1, false, modelM4 , 0);

		for(int i=0;i<FanCount;i++)
		{
			GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN , vertexPerFan *i , vertexPerFan );
		}
		
		GLES20.glDisableVertexAttribArray(PositionLocation);
		GLES20.glDisableVertexAttribArray(colorLocation);
	}
	
	public static void draw(float[] projectionM4,float[] viewM4, float[] modelM4 ,
			 float [] viewPos,FloatBuffer color , FloatBuffer vertex,int [] color_vbo,
			 int vertex_vbo[] , int FanCount,int vertexPerFan ,boolean usevbo )
	{
    	GLES20.glUseProgram(Program);
    	
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertex_vbo[0] );
		GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertex.capacity()*4, vertex );

        GLES20.glEnableVertexAttribArray(PositionLocation);
        GLES20.glVertexAttribPointer(
        		PositionLocation, 3, GLES20.GL_FLOAT, false, 3*4, 0 );
        
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, color_vbo[0] );
		GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, color.capacity()*4, color );
        
        GLES20.glEnableVertexAttribArray(colorLocation);
        GLES20.glVertexAttribPointer(
        		colorLocation, 4, GLES20.GL_FLOAT, false, 4*4, 0 );

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0 );

		GLES20.glUniformMatrix4fv(projectionLocation, 1, false , projectionM4, 0);
		GLES20.glUniformMatrix4fv(viewLocation, 1, false, viewM4 , 0);
		GLES20.glUniformMatrix4fv(modelLocation, 1, false, modelM4 , 0);

		for(int i=0;i<FanCount;i++)
		{
			GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN , vertexPerFan *i , vertexPerFan );
		}
		
		GLES20.glDisableVertexAttribArray(PositionLocation);
		GLES20.glDisableVertexAttribArray(colorLocation);
	}

	
    public static void initGLProgram(Context context)
    {
    	loadProgram(context,"glsl/program/rawTriangleColor.v","glsl/program/rawTriangleColor.f");
    }
    
    private static void loadProgram(Context context,String vertexShader,String fragmentShader)
    {
    	Program = ShaderLoader.loadShader(context, vertexShader , fragmentShader );
    	PositionLocation = GLES20.glGetAttribLocation(Program, "Position");
    	colorLocation = GLES20.glGetAttribLocation(Program, "Color");
    	
    	projectionLocation     = GLES20.glGetUniformLocation(Program, "Projection");
    	viewLocation           = GLES20.glGetUniformLocation(Program, "View");
    	modelLocation          = GLES20.glGetUniformLocation(Program, "Model");
    }

}
