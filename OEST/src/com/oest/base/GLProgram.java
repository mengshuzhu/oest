package com.oest.base;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import android.content.Context;
import android.opengl.GLES20;

public class GLProgram {
	
    protected  Integer Program = null;
    
    protected  Integer PositionLocation= null;
    protected  Integer NormalLocation= null;
    protected  Integer projectionLocation= null;
    protected  Integer viewLocation= null;
    protected  Integer modelLocation= null;
    protected Integer ViewPosLocation = null;

	public GLProgram(Context context,String vertexShaderPath,String fragmentShaderPath)
	{
		loadProgram(context,vertexShaderPath,fragmentShaderPath);
	}
    public void draw(float[] projectionM4,float[] viewM4, float[] modelM4 ,float[] ViewPos,
    		FloatBuffer vertex ,FloatBuffer normal ,IntBuffer index) {
        
    	GLES20.glUseProgram(Program);
        GLES20.glEnableVertexAttribArray(PositionLocation);
        GLES20.glVertexAttribPointer(
        		PositionLocation, 3, GLES20.GL_FLOAT, false, 3*4, vertex);
        
        GLES20.glEnableVertexAttribArray(NormalLocation);
        GLES20.glVertexAttribPointer(
        		NormalLocation , 3 , GLES20.GL_FLOAT, false, 12 , normal);
        

		GLES20.glUniformMatrix4fv(projectionLocation, 1, false , projectionM4, 0);
		GLES20.glUniformMatrix4fv(viewLocation, 1, false, viewM4 , 0);
		GLES20.glUniformMatrix4fv(modelLocation, 1, false, modelM4 , 0);
		GLES20.glUniform3fv(ViewPosLocation, 1 , ViewPos , 0);
		
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, index.capacity() , GLES20.GL_UNSIGNED_INT, index);

		//GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertex.capacity()/3);

		GLES20.glDisableVertexAttribArray(PositionLocation);
        GLES20.glDisableVertexAttribArray(NormalLocation);
		
    }

    private void loadProgram(Context context,String vertexShader,String fragmentShader)
    {
    	if(Program == null)
    	{
        	Program = ShaderLoader.loadShader(context, vertexShader , fragmentShader );
        	PositionLocation = GLES20.glGetAttribLocation(Program, "Position");
        	NormalLocation = GLES20.glGetAttribLocation(Program, "Normal");
        	projectionLocation     = GLES20.glGetUniformLocation(Program, "Projection");
        	viewLocation           = GLES20.glGetUniformLocation(Program, "View");
        	modelLocation          = GLES20.glGetUniformLocation(Program, "Model");
        	ViewPosLocation 	= GLES20.glGetUniformLocation(Program, "ViewPos");
    	}
    }

}
