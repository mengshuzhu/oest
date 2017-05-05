package com.oest.old;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.util.Log;

public class ShaderLoader {
	public static int loadShader(Context context,String vertexRes,String fragmentRes)
	{
		
			AssetManager am = null;
			am = context.getAssets();
			String fragmentCode;
			String vertexCode;
			try {
				
			InputStream fragmentglsl;
			fragmentglsl = am.open(fragmentRes);
		    InputStreamReader fragmentisr = new InputStreamReader(fragmentglsl);
			BufferedReader fragmentin = new BufferedReader(fragmentisr);
			String line = "";
			line = "";
			StringBuilder fragmentBuilder = new StringBuilder();
			while( line != null)
			{
				line = fragmentin.readLine();
				if(line != null)
				{
					fragmentBuilder.append(line);
					fragmentBuilder.append("\r\n");
				}
			}
			fragmentCode = fragmentBuilder.toString();
			fragmentglsl.close();
			///fragment//
			///vertex//
			InputStream vertexglsl = am.open(vertexRes);
		    InputStreamReader vertexisr = new InputStreamReader(vertexglsl);
			BufferedReader vertexin = new BufferedReader(vertexisr);
			line = "";
			StringBuilder vertexBuilder = new StringBuilder();
			while( line != null)
			{
				line = vertexin.readLine();
				if(line != null)
				{
					vertexBuilder.append(line);
					vertexBuilder.append("\r\n");
				}
			}
			vertexCode = vertexBuilder.toString();
			vertexglsl.close();
			///vertex//
		} catch (IOException e) {
				e.printStackTrace();
				Log.e("ShaderLoader Ioexception ", e.getMessage());
				return 0;
		}
		int  program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, loadShader(GLES20.GL_VERTEX_SHADER, vertexCode));
        GLES20.glAttachShader( program, loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode));
        GLES20.glLinkProgram(program);
        
        checkGlError("create program");
        Log.e("shaderLoader", vertexRes +" shaderLoader end");
        return program;
        
	}
	private static int loadShader(int type, String shaderCode){
	        int shader = GLES20.glCreateShader(type);
	        GLES20.glShaderSource(shader, shaderCode);
	        GLES20.glCompileShader(shader);
	        return shader;
	}
	
    public static void checkGlError(String glOperation) { 
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("glERROR", glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }


}
