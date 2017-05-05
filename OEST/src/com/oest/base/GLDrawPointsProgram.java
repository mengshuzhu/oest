package com.oest.base;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;


import android.content.Context;
import android.opengl.GLES20;

public class GLDrawPointsProgram {
	
    protected static  Integer Program = null;
    
    protected static Integer PositionLocation= null;
    protected static Integer projectionLocation= null;
    protected static Integer viewLocation= null;
    protected static Integer modelLocation= null;
    protected int [] vbo_vertices = new int[1]; 
    
    public GLDrawPointsProgram(Context context,FloatBuffer vertex)
    {
		GLES20.glGenBuffers(1, vbo_vertices,0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertex.capacity() * 4 , vertex , GLES20.GL_DYNAMIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }
	public static void initGLProgram(Context context)
	{
		loadProgram(context,"glsl/program/drawPoints.v","glsl/program/drawPoints.f");
	}
    public  void draw(float[] projectionM4,float[] viewM4, float[] modelM4 ,
    		FloatBuffer vertex ) {
        
    	GLES20.glUseProgram(Program);
    	
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices[0]);
		GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertex.capacity()*4, vertex);

        GLES20.glEnableVertexAttribArray(PositionLocation);
        GLES20.glVertexAttribPointer(
        		PositionLocation, 3, GLES20.GL_FLOAT, false, 3*4, 0);
        
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		GLES20.glUniformMatrix4fv(projectionLocation, 1, false , projectionM4, 0);
		GLES20.glUniformMatrix4fv(viewLocation, 1, false, viewM4 , 0);
		GLES20.glUniformMatrix4fv(modelLocation, 1, false, modelM4 , 0);

		GLES20.glDrawArrays(GLES20.GL_POINTS , 0, vertex.capacity()/3);

		GLES20.glDisableVertexAttribArray(PositionLocation);
		
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
