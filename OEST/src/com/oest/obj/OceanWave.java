package com.oest.obj;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Map;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.oest.base.ShaderLoader;
import com.oest.base.Utils;

public class OceanWave {
	
	public static int length = 10;
	public static float space = 10f;
	
	public static FloatBuffer vertext = Utils.allocateVertex(length*length);
	static FloatBuffer normal = Utils.allocateVertex(length*length);
	static FloatBuffer texture = Utils.allocateFloat(length*length*2);
	static IntBuffer index = IntBuffer.allocate(length*length*6 - length*6);
	
	private static int [] samples = new int[5];
	private static String [] textureImages = new String[5];
	
    protected static  Integer Program = null;
    
    protected static Integer PositionLocation= null;
    protected static Integer NormalLocation= null;
    protected static Integer TextureLocation =null;
    protected static Integer projectionLocation= null;
    protected static Integer viewLocation= null;
    protected static Integer modelLocation= null;
    protected static Integer sampleLocation =null;
    protected static Integer alphaLocation = null;
    static
    {
    	textureImages[0] = "images/wave1.png";
    	textureImages[1] = "images/wave2.png";
    	textureImages[2] = "images/wave3.png";
    	textureImages[3] = "images/wave4.png";
    	textureImages[4] = "images/wave5.png";
    }

    public OceanWave(Context context)
    {
    	
    }
    
	public static void InitOceanWave(Context context)
	{
		Utils.loadMaterial(context, textureImages , samples);
		
		for(int r =0;r<length;r++)
		{
			for(int c =0;c<length;c++)
			{
				vertext.put((c - (length/2)) *space);
				vertext.put(20f);
				vertext.put((r - (length/2)) *space);
				
				normal.put(0.0f);normal.put(0.0f);normal.put(0.0f);
				
				texture.put((float)c/(float)(length-1));
				texture.put((float)r/(float)(length-1));
				
				if(r < (length -1) && c < (length - 1) )
				{
					index.put(r*c+0);index.put(r*c+1);index.put((r+1)*c+0);
					index.put((r+1)*(c+1)+0);index.put(r*c+1);index.put((r+1)*c+0);
				}
			}
		}
		
		
		vertext.position(0);
		normal.position(0);
		texture.position(0);
		index.position(0);
		
	}
	
	

	public static void simluate()
	{
		int beginr =29+ (int)0/10;
		int beginc =29+ (int)0/10;

        vertext.position(0);
		for(int r =0;r<length;r++)
		{
			for(int c =0;c<length;c++)
			{
				
				vertext.put( OceanFFT.vertexBuffer.get(( (beginr+r)*OceanFFT.NPlus1 + c+beginc ) * 15));
				vertext.put( OceanFFT.vertexBuffer.get(( (beginr+r)*OceanFFT.NPlus1 + c+beginc ) * 15+1 )+1f);
				vertext.put( OceanFFT.vertexBuffer.get(( (beginr+r)*OceanFFT.NPlus1 + c+beginc ) * 15+2 ));

			}
		}
		vertext.position(0);

	}
	
    public static  void draw(float[] projectionM4,float[] viewM4, float[] modelM4 ,
    		float [] viewPos ,int sampleIndex,float alpha ) {
        
    	
		float [] oceanWaveModel = Arrays.copyOf(modelM4, 16);
		Matrix.scaleM(oceanWaveModel, 0, 10.f, 10.f, 10.f );

    	GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    	GLES20.glUseProgram(Program);
    	
        GLES20.glEnableVertexAttribArray(PositionLocation);
        GLES20.glVertexAttribPointer(
        		PositionLocation, 3, GLES20.GL_FLOAT, false, 3*4, vertext);
        
        GLES20.glEnableVertexAttribArray(NormalLocation);
        GLES20.glVertexAttribPointer(
        		NormalLocation , 3 , GLES20.GL_FLOAT, false, 12 , normal);
        
        GLES20.glEnableVertexAttribArray(TextureLocation);
        GLES20.glVertexAttribPointer(
        		TextureLocation , 2 , GLES20.GL_FLOAT, false, 8 , texture);
        

		GLES20.glUniformMatrix4fv(projectionLocation, 1, false , projectionM4, 0);
		GLES20.glUniformMatrix4fv(viewLocation, 1, false, viewM4 , 0);
		GLES20.glUniformMatrix4fv(modelLocation, 1, false, oceanWaveModel , 0);
        
		// Draw the cube.
    	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, samples[sampleIndex] );
		GLES20.glUniform1i(sampleLocation , GLES20.GL_TEXTURE0);
		GLES20.glUniform1f(alphaLocation, alpha);
			
	    GLES20.glDrawElements(
	                GLES20.GL_TRIANGLES, index.capacity() , GLES20.GL_UNSIGNED_INT, index);
		
        GLES20.glDisableVertexAttribArray(PositionLocation);
        GLES20.glDisableVertexAttribArray(NormalLocation);
        GLES20.glDisableVertexAttribArray(TextureLocation);
		
    }

	
    public static void initGLProgram(Context context)
    {
    	loadProgram(context,"glsl/oceanfft/oceanwave.v","glsl/oceanfft/oceanwave.f");
    	Utils.checkGlError(OceanWave.class.getName()+" create program "); 
    }
    
    
    private static void loadProgram(Context context,String vertexShader,String fragmentShader)
    {
        	Program = ShaderLoader.loadShader(context, vertexShader , fragmentShader );
        	PositionLocation = GLES20.glGetAttribLocation(Program, "Position");
        	NormalLocation = GLES20.glGetAttribLocation(Program, "Normal");
        	TextureLocation = GLES20.glGetAttribLocation(Program, "Texture");
        	
        	projectionLocation     = GLES20.glGetUniformLocation(Program, "Projection");
        	viewLocation           = GLES20.glGetUniformLocation(Program, "View");
        	modelLocation          = GLES20.glGetUniformLocation(Program, "Model");
        	sampleLocation = GLES20.glGetUniformLocation(Program, "sample");
        	alphaLocation =  GLES20.glGetUniformLocation(Program, "alpha");
    }

}
