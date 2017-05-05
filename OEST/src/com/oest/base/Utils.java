package com.oest.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.oest.app.OestActivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class Utils {

	public static FloatBuffer allocateFloat(int floatCount)
	{
		ByteBuffer temp  = ByteBuffer.allocateDirect(floatCount *4);
		temp.order(ByteOrder.nativeOrder());
		return temp.asFloatBuffer();
	}
	
	public static FloatBuffer allocateVertex(int vertexCount)
	{
		return allocateFloat(vertexCount*3);
	}
	
	private static boolean useExternalRes = true;
	
	public static InputStream getResInputStream(String resPath)
	{
		InputStream is = null;
		
		if(useExternalRes)
		{
			File file = new File("/sdcard/oest",resPath);
			try {
				is = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException("FileNotFoundException  "+file.toString());
			}
		}
		else
		{
			AssetManager am = null;
			am = OestActivity.context.getAssets();
			
			try {
				is = am.open(resPath);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("read res failed "+resPath);
			}
		}
		
		return is;
	}
	public static void closeIS(InputStream is)
	{
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("closeIS IOException ");
		}

	}
	public static void loadMaterial(Context context,String[] resPath ,int [] samples)
	{
		GLES20.glGenTextures(samples.length , samples , 0 );
		
		for(int i=0;i<samples.length;i++)
		{
			createTexture(context,resPath[i],samples[i]);
		}
	}
	public static void loadMaterial(Context context,String resPath ,int [] samples)
    {
		GLES20.glGenTextures(1 , samples , 0 );
		createTexture(context,resPath,samples[0]);
    }
	
	private static void createTexture(Context context,String resPath,int texture)
	{
		InputStream is = null;
		is = Utils.getResInputStream(resPath);
			
		Bitmap bitmap = null;
		bitmap = BitmapFactory.decodeStream(is);
		
		//GLES20.glGenTextures(1 , samples , 0 );
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
    	GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
    	GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
    	GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();
		closeIS(is);
		
		checkGlError(Utils.class.getName()+" load material ");

	}
    
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("glERROR", glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
    
	public static void copyFloat(float [] tfloat,float[] sfloat,int count)
	{
		for(int i=0;i<count;i++)
		{
			tfloat[i] = sfloat[i];
		}
	}



}
