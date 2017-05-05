/*
 * Copyright (C) 2014 The Android Open Source Project
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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.io.InputStream;
import java.util.Arrays;

import com.oest.app.R;

public class HalfSphere extends Obj3D
{

    public String svres ="glsl/halfsphere.v";
    public String sfres ="glsl/halfsphere.f";
    private int [] textures = new int[2];
    private int myTextureLocation;
    private int textureLocation;
    public HalfSphere(Context context) {
        super(context,"halfSphere.obj","glsl/halfsphere.v","glsl/halfsphere.f");
        loadTexture(context);
        myTextureLocation  = GLES20.glGetUniformLocation(Program, "myTexture");
        textureLocation = GLES20.glGetAttribLocation(Program, "Texture");
    }
    public void loadTexture(Context context)
    {
    		InputStream is = context.getResources().openRawResource(R.drawable.sky);
    		Bitmap bitmap = null;
    		bitmap = BitmapFactory.decodeStream(is);
    		GLES20.glGenTextures(1, textures, 0 );
        	GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        	GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        	GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        	GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        	bitmap.recycle();
    }

    public void draw(float[] mvpMatrix , float[] projectionM4,float[] viewM4, float[] modelM4 ,float [] viewPos) {
        
		float[] m1 = Arrays.copyOf(modelM4, modelM4.length);
		Matrix.scaleM(m1, 0, 30.f, 30.f, 30.f);
		Matrix.translateM(m1, 0, 0f,-10f, 0f);
		Matrix.rotateM(m1, 0 , 90 , -1f, 0f, 0f);
    	// super.draw(mvpMatrix, projectionM4, viewM4, m1 ,viewPos);
        
    	GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
    	GLES20.glUseProgram(Program);
        GLES20.glEnableVertexAttribArray(PositionLocation);
        GLES20.glVertexAttribPointer(
        		PositionLocation, 3, GLES20.GL_FLOAT, false, 3*4, this.vertex);
        
        GLES20.glEnableVertexAttribArray(textureLocation);
        GLES20.glVertexAttribPointer(
        		textureLocation, 3, GLES20.GL_FLOAT, false, 3*4, this.texture);
        
        GLES20.glEnableVertexAttribArray(NormalLocation);
        GLES20.glVertexAttribPointer(
        		NormalLocation , 4, GLES20.GL_FLOAT, false, 16 , this.normal );

		GLES20.glUniformMatrix4fv(projectionLocation, 1, false , projectionM4, 0);
		GLES20.glUniformMatrix4fv(viewLocation, 1, false, viewM4 , 0);
		GLES20.glUniformMatrix4fv(modelLocation, 1, false, m1 , 0);
		GLES20.glUniform3fv(viewPosLocation, 1 , viewPos, 0);
		GLES20.glUniform1i(myTextureLocation, GLES20.GL_TEXTURE0);
		 
        // Draw the cube.
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, this.indexLength , GLES20.GL_UNSIGNED_INT, this.index);

        // Disable vertex arrays.
        GLES20.glDisableVertexAttribArray(PositionLocation);
        GLES20.glDisableVertexAttribArray(NormalLocation);

    }
}
