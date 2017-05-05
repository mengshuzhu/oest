package com.oest.old;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

public class Obj3DData {
	
	public  FloatBuffer vertex;
	public  FloatBuffer normal;
	public  FloatBuffer texture;
	public  List<IntBuffer> indexList = new ArrayList<IntBuffer>();
	public List<Map<String,String>> material;
	int [] vbo_vertices = new int[3];
	int [] vbo_indices ; 
	
    private static final int VERTEXT_HEAD = 10;
    private static final int NORMAL_HEAD = 11;
    private static final int TEXTURE_HEAD = 13;
    private static final int INDEX_HEAD = 12;
    private static final int MATERIAL_HEAD = 14;
    private static final int INVALID_SAMPLE = -250;
    private String res;
    private int [] samples;
    private float [] Kds;
    
    protected static  Integer Program = null;
    
    protected static Integer PositionLocation= null;
    protected static Integer NormalLocation= null;
    protected static Integer TextureLocation =null;
    protected static Integer projectionLocation= null;
    protected static Integer viewLocation= null;
    protected static Integer modelLocation= null;
    protected static Integer viewPosLocation= null;
    protected static Integer sampleLocation =null;
    protected static Integer hasSampleLocation = null;
    protected static Integer kdColorLocation = null;

    public Obj3DData(Context context,String res)
    {
    	this.res = res;
    	try {
    		long begin = System.currentTimeMillis();
    		AssetManager am = null;
    		am = context.getAssets();
    		InputStream is = am.open(res);
			while(readChunk(is))
			{
				
			}
			loadMaterial(context);
			checkGlError("com.oest.android.opengl.Obj3DData.Obj3DData load Material");
			
			GLES20.glGenBuffers(3, vbo_vertices,0);
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices[0]);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertex.capacity() * 4 , vertex , GLES20.GL_STATIC_DRAW );
			
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices[1]);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, normal.capacity() * 4 , normal , GLES20.GL_STATIC_DRAW );

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices[2]);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, texture.capacity() * 4 , texture , GLES20.GL_STATIC_DRAW );

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			
			vbo_indices = new int[indexList.size()];
			GLES20.glGenBuffers(indexList.size(), vbo_indices ,0);
			for(int i=0;i<indexList.size();i++)
			{
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vbo_indices[i]);
				GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexList.get(i).capacity()* 4 , indexList.get(i) , GLES20.GL_STATIC_DRAW );
			}
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void loadMaterial(Context context)
    {
    	int size = material.size();
    	samples = new int[size];
    	Kds = new float[size*3];
    	

		AssetManager am = null;
		am = context.getAssets();

    	for(int i=0; i< material.size();i++)
    	{
    		Map<String,String> oneMaterial = material.get(i);
    		String map_Ka = (String)oneMaterial.get("map_Ka");
    		String Kd = (String)oneMaterial.get("Kd");
    		if(map_Ka != null)
    		{
        		InputStream is = null;
        		if(res.lastIndexOf("/") != -1)
        		map_Ka = res.substring(0,res.lastIndexOf("/")+1) + map_Ka;
        			
    			try {
    				is = am.open(map_Ka);
    			} catch (IOException e) {
    				e.printStackTrace();
    				System.err.println("load material exception");
    				throw new RuntimeException("load material exception ");
    			}
        		Bitmap bitmap = null;
        		bitmap = BitmapFactory.decodeStream(is);
        		
        		GLES20.glGenTextures(1 , samples , i );
        		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, samples[i]);
            	GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            	GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            	GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        		bitmap.recycle();
	
    		}
    		else
    		{
    			samples[i] = INVALID_SAMPLE;
    			float [] result = new float[3];
    			parseFloatLine(Kd,result);
    			Kds[i*3] = result[0];
    			Kds[i*3+1] = result[1];
    			Kds[i*3+2] = result[2];
    		}

    	}

    }
    
    private void parseFloatLine(String line,float[] result)
	{
		String nvs[] = line.split("\\s");
		result[0] = Float.parseFloat(nvs[0]);
		result[1] = Float.parseFloat(nvs[1]);
		result[2] = Float.parseFloat(nvs[2]);
	}


    private boolean readChunk(InputStream is)
    {
    	try {
    	
		byte[] headByteBuffer = new byte[8];
		ByteBuffer headByte = ByteBuffer.wrap(headByteBuffer);
		////
		int headlen = is.read(headByte.array(), 0, 8);
		IntBuffer HeadInt = headByte.asIntBuffer();
		if(headlen != 8)
		{
			System.out.println("readChunk end headlen = " + headlen);
			return false;
		}
		int headSign  = HeadInt.get(0);
		int len = HeadInt.get(1);
		if(headSign == VERTEXT_HEAD)
		{
			
			ByteBuffer vertexbyteBuffer = ByteBuffer.allocateDirect(len+4);
			vertexbyteBuffer.order(ByteOrder.nativeOrder());
			vertex = vertexbyteBuffer.asFloatBuffer();
			int readLean = is.read(vertexbyteBuffer.array() , 4 , len);
			
			if(readLean == len)
				System.out.println("read VERTEXT "+len+" sucessed");
			else
			{
				System.err.println("read VERTEXT "+len+" failed actal "+readLean);
				return false;
			}
		}
		else if(headSign == NORMAL_HEAD)
		{
			ByteBuffer normalbyteBuffer = ByteBuffer.allocateDirect(len+4);
			normalbyteBuffer.order(ByteOrder.nativeOrder());
			normal = normalbyteBuffer.asFloatBuffer();
			int readLean = is.read(normalbyteBuffer.array() , 4 , len);

			if(readLean == len)
				System.out.println("read TEXTURE "+len+" sucessed");
			else
			{
				System.err.println("read TEXTURE "+len+" failed actal "+readLean);
				return false;
			}
		}
		else if(headSign == TEXTURE_HEAD)
		{
			ByteBuffer textureByteBuffer = ByteBuffer.allocateDirect(len+4);
			textureByteBuffer.order(ByteOrder.nativeOrder());
			texture = textureByteBuffer.asFloatBuffer();
			int readLean = is.read(textureByteBuffer.array() , 4 , len);
			if(readLean == len)
				System.out.println("read NORMAL "+len+" sucessed");
			else
			{
				System.err.println("read NORMAL "+len+" failed actal "+readLean);
				return false;
			}
		}
		else if(headSign == INDEX_HEAD)
		{
			ByteBuffer indexbyteBuffer = ByteBuffer.allocateDirect(len);
			IntBuffer tempindex = indexbyteBuffer.asIntBuffer();
			int readLean = is.read(indexbyteBuffer.array() , 4 , len);
			
			IntBuffer index = IntBuffer.allocate(len/4);
			index.put(tempindex);
			index.position(0);
			
			indexList.add(index);
			
			if(readLean == len)
				System.out.println("read INDEX "+len+" sucessed");
			else
			{
				System.err.println("read INDEX "+len+" failed actal "+readLean);
				return false;
			}
		}
		else if(headSign == MATERIAL_HEAD )
		{
			 byte [] materialBuffer = new byte[len];
			 is.read(materialBuffer , 0 , len);
			 ByteArrayInputStream materailInput = new ByteArrayInputStream(materialBuffer);
			 ObjectInputStream materialstream = new ObjectInputStream(materailInput);
			 try {
				material = (List<Map<String,String>>)materialstream.readObject();
			 } catch (ClassNotFoundException e) {
				e.printStackTrace();
			 }
			 
			 System.out.println(material.toString());
			 materialstream.close();
		}
		else
		{
			System.err.println(" not a vild chunk");
			is.skip(len);
			return true;
		}
		
		return true;
		
    	} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
    	
    }
    
    public void draw(float[] projectionM4,float[] viewM4, float[] modelM4 ,float [] viewPos) {
        
    	GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    	GLES20.glUseProgram(Program);
    	
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices[0]);
        GLES20.glEnableVertexAttribArray(PositionLocation);
        GLES20.glVertexAttribPointer(
        		PositionLocation, 3, GLES20.GL_FLOAT, false, 3*4, 0);
        
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices[1]);
        GLES20.glEnableVertexAttribArray(NormalLocation);
        GLES20.glVertexAttribPointer(
        		NormalLocation , 3 , GLES20.GL_FLOAT, false, 12 , 0);
        
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices[2]);
        GLES20.glEnableVertexAttribArray(TextureLocation);
        GLES20.glVertexAttribPointer(
        		TextureLocation , 3 , GLES20.GL_FLOAT, false, 12 , 0);
        
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		GLES20.glUniformMatrix4fv(projectionLocation, 1, false , projectionM4, 0);
		GLES20.glUniformMatrix4fv(viewLocation, 1, false, viewM4 , 0);
		
/*		float [] m4 = Arrays.copyOf(modelM4, modelM4.length);
		
		Matrix.scaleM(m4, 0, 1.f, 1.f, 1.f);
		Matrix.rotateM(m4, 0, 90, -1, 0, 0);
*/		
		GLES20.glUniformMatrix4fv(modelLocation, 1, false, modelM4 , 0);
		GLES20.glUniform3fv(viewPosLocation, 1 , viewPos, 0);
        
		// Draw the cube.
		for(int i=0;i<indexList.size();i++)
		{
			if( samples[i] != INVALID_SAMPLE)
			{
		    	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, samples[i]);
				GLES20.glUniform1i(sampleLocation , GLES20.GL_TEXTURE0);
				GLES20.glUniform1i(hasSampleLocation, 1);
			}
			else
			{
				GLES20.glUniform1i(hasSampleLocation, 0);
				GLES20.glUniform3f(kdColorLocation, Kds[i*3], Kds[i*3+1], Kds[i*3+2]);
			}
			
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vbo_indices[i]);
	        GLES20.glDrawElements(
	                GLES20.GL_TRIANGLES, indexList.get(i).capacity() , GLES20.GL_UNSIGNED_INT, 0);
		}
		
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        // Disable vertex arrays.
        GLES20.glDisableVertexAttribArray(PositionLocation);
        GLES20.glDisableVertexAttribArray(NormalLocation);
        GLES20.glDisableVertexAttribArray(TextureLocation);
		
    }
    
    public static void initGLProgram(Context context)
    {
    	loadProgram(context,"glsl/obj3dmate.v","glsl/obj3dmate.f");
    }
    
    private static void loadProgram(Context context,String vertexShader,String fragmentShader)
    {
        	Program = ShaderLoader.loadShader(context, vertexShader , fragmentShader );
        	PositionLocation = GLES20.glGetAttribLocation(Program, "Position");
        	NormalLocation = GLES20.glGetAttribLocation(Program, "Normal");
        	projectionLocation     = GLES20.glGetUniformLocation(Program, "Projection");
        	viewLocation           = GLES20.glGetUniformLocation(Program, "View");
        	modelLocation          = GLES20.glGetUniformLocation(Program, "Model");
        	viewPosLocation  =   GLES20.glGetUniformLocation(Program, "viewPos");
        	TextureLocation = GLES20.glGetAttribLocation(Program, "Texture");
        	sampleLocation = GLES20.glGetUniformLocation(Program, "sample");
        	hasSampleLocation = GLES20.glGetUniformLocation(Program, "hasSample");
        	kdColorLocation  = GLES20.glGetUniformLocation(Program, "kdColor");
    }
    
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("glERROR", glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }




}
