/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oest.old;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import com.oest.app.R;

import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class Square {

    private  String vertexShaderCode = "";
    private  String fragmentShaderCode = "";
    private final FloatBuffer vertexBuffer;
    private  FloatBuffer mVNormalBuffer;
    private  FloatBuffer texturecoordBuffer;
    private float[] vNormal = new float[]{};
    private List<Vec3D> vNormalList = new ArrayList<Vec3D>();
    
    private List<Vec2D> texturecoordList = new ArrayList<Vec2D>();

    private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mMVPMatrixHandle;
    private  int mVNormalHandle;
    private  int mshininessHandle;
    private  int vLightPositionHandle;
    private  int vAmbientMaterialHandle;
    private  int vSpecularMaterialHandle;
    private  int vDiffuseMaterialHandle;
    private int texturecoordHandle ;
    private int twoDTextureHandle;
    private int timeHandle;
    private int ftimeHandle;
    private int textureCoodAddHandle;
    
    private int row = 150;
    private int colum = 150;
    private int faces = (row - 1)*(colum - 1)*2;
    private int orderLength = 0;
    public static float time = 0;
    public static int textureindex = 0;
    private int [] vbo = new int[6];
    
    public Square(Context context) {
    	
		
    	vNormal = new float[row * colum * 4];
    	vNormalList = new ArrayList<Vec3D>(row * colum);
    	
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 4 bytes per float)
        		row * colum * 3 * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        
        bb = ByteBuffer.allocateDirect(vNormal.length * 4);

        bb.order(ByteOrder.nativeOrder());
        mVNormalBuffer = bb.asFloatBuffer();

        bb = ByteBuffer.allocateDirect(row * colum * 2 * 4);
        bb.order(ByteOrder.nativeOrder());
        texturecoordBuffer = bb.asFloatBuffer();
        
        
       float halfy = (float)(row*0.05);
       float halfx = (float)(colum*0.05);
       for(int y = 0 ; y <row ;y ++ )
        {
        	//squareCoords[i] = squareCoords[i];
        	for(int  x = 0  ; x <colum ; x++ )
        	{
        		float fx = (float)(x*0.1f-halfx);
        		float fy = (float)(y*0.1f-halfy);
        		vertexBuffer.put(fx);
        		vertexBuffer.put(fy);
        		float d = (float)Math.abs(Math.sqrt(fx*fx+fy*fy));
        		float d2 = (float)Math.abs(Math.sqrt((fx+0.4)*(fx+0.4)+(fy+0.4)*(fy+0.4)));
        		
/*        		float fz = (float)(Math.sin(fx*9))/50 + (float)(Math.sin(d*9))/50+(float)(Math.sin(d2*9))/50;
*/        		/*float fz = (float)(Math.sin(d*9))/5;*/
        		
        		vertexBuffer.put(0f);
        		
        		vNormalList.add(new Vec3D());
        		
        		Vec2D texturecoord = new Vec2D();

        		texturecoord.x = (float)x*5f /(float)colum;
        		texturecoord.y = (float)y *5f /(float)row;
        		
        		texturecoordList.add(texturecoord);
        	}
        }
       // vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
        		faces* 3 * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        
        for(int r = 0; r < row - 1 ; r++)
        {
        for(int c = 0; c < colum-1 ; c++)
        {
        	short pointa = (short)(c+(r*row));
        	drawListBuffer.put(pointa);
        	short pointb = (short)(c+(r*row)+1);
        	drawListBuffer.put(pointb);
        	short pointc = (short)(c+(r*row)+row);
        	drawListBuffer.put(pointc);
        	
        	drawListBuffer.put((short)(c+(r*row)+1));
        	drawListBuffer.put((short)(c+(r*row)+row+1));
        	drawListBuffer.put((short)(c+(r*row)+row));
      	
        	orderLength += 6 ;
        	
        	Vec3D vec3a = new Vec3D(vertexBuffer.get((int)(pointa*3)),vertexBuffer.get((int)(pointa*3+1)),vertexBuffer.get((int)(pointa*3+2)));
        	Vec3D vec3b = new Vec3D(vertexBuffer.get((int)(pointb*3)),vertexBuffer.get((int)(pointb*3+1)),vertexBuffer.get((int)(pointb*3+2)));
        	Vec3D vec3c = new Vec3D(vertexBuffer.get((int)(pointc*3)),vertexBuffer.get((int)(pointc*3+1)),vertexBuffer.get((int)(pointc*3+2)));

        	Vec3D ab = vec3b.sub(vec3a);
        	Vec3D bc = vec3c.sub(vec3b);
        	Vec3D ca = vec3a.sub(vec3c);
        	
        	Vec3D faceNormal  = ab.cross(bc);
        	faceNormal.crossSelf(ca);
        	
        	vNormalList.set(pointa, faceNormal.add(vNormalList.get(pointa)).normalize());
        	vNormalList.set(pointb, faceNormal.add(vNormalList.get(pointb)).normalize());
        	vNormalList.set(pointc, faceNormal.add(vNormalList.get(pointc)).normalize());
        	
        }
        }
        
        drawListBuffer.position(0);
        
        for(int i=0;i<vNormalList.size();i++)
        {
        	Vec3D v = vNormalList.get(i);
        	vNormal[i*4] = v.x;
        	vNormal[i*4+1] = v.y;
        	vNormal[i*4+2] = v.z;
        	vNormal[i*4+3] = 1.0f;
        }
        for(int i=0;i<texturecoordList.size();i++)
        {
        	Vec2D v = texturecoordList.get(i);
        	texturecoordBuffer.put(v.x);
        	texturecoordBuffer.put(v.y);
        }
        mVNormalBuffer.put(vNormal);
        mVNormalBuffer.position(0);
        texturecoordBuffer.position(0);
        
		AssetManager am = null;
		am = context.getAssets();
		try{
		///fragment//
		InputStream fragmentglsl = am.open("glsl/square/fragmentShader.glsl");
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
		fragmentShaderCode = fragmentBuilder.toString();
		fragmentglsl.close();
		///fragment//
		///vertex//
		InputStream vertexglsl = am.open("glsl/square/vertexShader.glsl");
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
		vertexShaderCode = vertexBuilder.toString();
		vertexglsl.close();
		///vertex//
		}catch(IOException e)
		{
			Log.e("OEST", e.getMessage());
		}

        // prepare shaders and OpenGL program
        int vertexShader = OestGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = OestGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        
        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, row * colum * 3 * 4, vertexBuffer , GLES20.GL_STATIC_DRAW );
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        
        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
        
        loadTexture(context);
/*        vertexBuffer.position(0);
        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, row * colum * 3 * 4, vertexBuffer , GLES20.GL_DYNAMIC_DRAW);
*/        
        
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mVNormalHandle = GLES20.glGetAttribLocation(mProgram, "vNormal");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mshininessHandle = GLES20.glGetUniformLocation(mProgram, "shininess");
        vLightPositionHandle = GLES20.glGetUniformLocation(mProgram, "vLightPosition");
        vAmbientMaterialHandle = GLES20.glGetUniformLocation(mProgram, "vAmbientMaterial");
        vSpecularMaterialHandle = GLES20.glGetUniformLocation(mProgram, "vSpecularMaterial");
        vDiffuseMaterialHandle = GLES20.glGetUniformLocation(mProgram, "vDiffuseMaterial");
        
        texturecoordHandle =  GLES20.glGetAttribLocation(mProgram, "textureCood");
        twoDTextureHandle  = GLES20.glGetUniformLocation(mProgram, "myTexture");
        timeHandle  = GLES20.glGetUniformLocation(mProgram, "time");
        ftimeHandle = GLES20.glGetUniformLocation(mProgram, "ftime");
        textureCoodAddHandle = GLES20.glGetUniformLocation(mProgram,"textureCoodAdd");

    }
    public static int texturesLen = 20;
    public static float textureCoodAddx = 0;
    public static float textureCoodAddy = 0;
    
    private int [] textures = new int[texturesLen];
    
    public void loadTexture(Context context)
    {
    	for(int i = 0;i < texturesLen ;i++)
    	{
    		InputStream is = context.getResources().openRawResource(R.drawable.ic_launcher+i);
    		Bitmap bitmap = null;
    		bitmap = BitmapFactory.decodeStream(is);
    		
    		GLES20.glGenTextures(1, textures, i);
        	GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[i]);
        	
        	GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        	GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        	GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        	bitmap.recycle();
    	}
    }
    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
    private float timefft = 0;
    public void draw(float[] mvpMatrix) {
    	
/*		timefft += 1.0f/30.0f;
		ndk.oceanRender(timefft);
*/
        
    	GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[textureindex]);
    	
    	GLES20.glUseProgram(mProgram);
    	
    	/*vertexBuffer.position(0);*/
/*    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
    	GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, row * colum * 3 * 4 , vertexBuffer );
*/    	
    	//GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, row * colum * 3 * 4, vertexBuffer , GLES20.GL_DYNAMIC_DRAW);
    	
/*    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer( mPositionHandle, 3 ,GLES20.GL_FLOAT, false, 3*4 , 0 );
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
*/        
        //GLES20.glVertexAttribPointer(arg0, arg1, arg2, arg3, arg4, arg5);
       
    	GLES20.glEnableVertexAttribArray(mPositionHandle);
    	GLES20.glVertexAttribPointer( mPositionHandle, 3 ,GLES20.GL_FLOAT, false, 3*4 , vertexBuffer );
    	
        GLES20.glEnableVertexAttribArray(mVNormalHandle);
        GLES20.glVertexAttribPointer(
        		mVNormalHandle , 4 , GLES20.GL_FLOAT, false, 16 , mVNormalBuffer );
        
        GLES20.glEnableVertexAttribArray(texturecoordHandle);
        GLES20.glVertexAttribPointer(
        		texturecoordHandle , 2 , GLES20.GL_FLOAT, false, 2*4 , texturecoordBuffer );

        
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glUniform1f(mshininessHandle, 1.0f);
        GLES20.glUniform1f(timeHandle, time);
        GLES20.glUniform1f(ftimeHandle, time);
        GLES20.glUniform3f(vLightPositionHandle, 0f, 6f, -1f);
        GLES20.glUniform4f(vAmbientMaterialHandle, 0.4f, 0.4f, 0.4f,1f);
        GLES20.glUniform4f(vSpecularMaterialHandle, 0.8f, 0.8f, 0.8f, 1f);
        GLES20.glUniform4f(vDiffuseMaterialHandle, 0.5f, 0.5f, 0.5f, 1f);
        GLES20.glUniform1i(twoDTextureHandle, GLES20.GL_TEXTURE0);
        GLES20.glUniform2f(textureCoodAddHandle, textureCoodAddx, textureCoodAddy);
        
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, orderLength ,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mVNormalHandle);
        GLES20.glDisableVertexAttribArray(texturecoordHandle);
        //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[textureindex]);

    }

}