package com.oest.base;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class CircleFireGLProgram {

    protected  Integer Program = null;
    protected  Integer PositionLocation= null;
    protected Integer ColorLocation = null;
    
    protected  Integer projectionLocation= null;
    protected  Integer viewLocation= null;
    protected  Integer modelLocation= null;

    public CircleFireGLProgram(Context context)
    {
		loadProgram(context,"glsl/particle.v","glsl/particle.f");
    }
        
    public void beginRender(float[] mvpMatrix , float[] projectionM4,float[] viewM4  ,float [] viewPos,
    		FloatBuffer vertex,FloatBuffer colors )
    {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
    	
    	GLES20.glUseProgram(Program);
    	
        GLES20.glEnableVertexAttribArray(PositionLocation);
        GLES20.glVertexAttribPointer(
        		PositionLocation, 3, GLES20.GL_FLOAT, false, 3*4, vertex);
        
        GLES20.glEnableVertexAttribArray(ColorLocation);
        GLES20.glVertexAttribPointer(
        		ColorLocation, 4 , GLES20.GL_FLOAT, false, 4*4, colors);

        
		GLES20.glUniformMatrix4fv(projectionLocation, 1, false , projectionM4, 0);
		GLES20.glUniformMatrix4fv(viewLocation, 1, false, viewM4 , 0);

        
    }
    public void render(float[] modelM4,IntBuffer index,int indexCount)
    {
		GLES20.glUniformMatrix4fv(modelLocation, 1, false, modelM4 , 0);
		
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, indexCount , GLES20.GL_UNSIGNED_INT, index);
    }
    public void endRender()
    {
        GLES20.glDisableVertexAttribArray(PositionLocation);
        GLES20.glDisableVertexAttribArray(ColorLocation);
        GLES20.glDisable(GLES20.GL_BLEND);
    }
    
    private void loadProgram(Context context,String vertexShader,String fragmentShader)
    {
    	if(Program == null)
    	{
        	Program = ShaderLoader.loadShader(context, vertexShader , fragmentShader );
        	PositionLocation = GLES20.glGetAttribLocation(Program, "Position");
        	ColorLocation = GLES20.glGetAttribLocation(Program, "Color");
        			
        	projectionLocation     = GLES20.glGetUniformLocation(Program, "Projection");
        	viewLocation           = GLES20.glGetUniformLocation(Program, "View");
        	modelLocation          = GLES20.glGetUniformLocation(Program, "Model");
    	}
    	checkGlError("loadProgram");
    }
    
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("glERROR", glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }



}
