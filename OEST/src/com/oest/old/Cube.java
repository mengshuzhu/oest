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
import android.opengl.GLES20;
import android.opengl.Matrix;
import java.util.Arrays;

public class Cube {

    private Obj3D obj;

    public Cube(Context context) {
        obj = new Obj3D(context,"teapot.obj","glsl/obj3d.v","glsl/obj3d.f");
    }

    public void draw(float[] mvpMatrix , float[] projectionM4,float[] viewM4, float[] modelM4 ,float [] viewPos) {
        
		float[] m1 = Arrays.copyOf(modelM4, modelM4.length);
		Matrix.scaleM(m1, 0, 10.f, 10.f, 10.f);
		//Matrix.translateM(m1, 0, 1f, 0f, 0f);
		//Matrix.rotateM(m1, 0 , 60 , -1f, 0f, 0f);

    	obj.draw(mvpMatrix, projectionM4, viewM4, m1 ,viewPos);
    	
    }
}
