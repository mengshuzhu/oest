package com.oest.old;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import toxi.geom.Vec3D;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

public class Obj3D {
	
	public  FloatBuffer vertex;
	public  int vertexLength = 0;
	public  FloatBuffer normal;
	public  int normalLength = 0;
	public  FloatBuffer texture;
	public  int textureLength = 0;

	public  IntBuffer index;
	public  int indexLength = 0;
	
	private List<Vec3D> vertexList = new ArrayList<Vec3D>();
	private List<Vec3D> vertexNormalList = new ArrayList<Vec3D>();
	private List<Vec3D> textureList =  new ArrayList<Vec3D>();
	
	private List<Vec3D> normalList = new ArrayList<Vec3D>();
	
    protected  Integer Program = null;
    protected  int PositionLocation;
    protected  int NormalLocation;
    
    protected  int projectionLocation;
    protected  int viewLocation;
    protected  int modelLocation;
    protected  int viewPosLocation;
    
    
    public Obj3D(Context context,String modelRes ,String vertexShader,String fragmentShader)
    {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(102400 * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertex = byteBuffer.asFloatBuffer();
        vertex.position(0);
        
        byteBuffer = ByteBuffer.allocateDirect(102400 * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        normal = byteBuffer.asFloatBuffer();
        normal.position(0);
        
        byteBuffer = ByteBuffer.allocateDirect(102400 * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        texture = byteBuffer.asFloatBuffer();
        texture.position(0);
        
        index = IntBuffer.allocate(102400*4);
        index.position(0);
        loadObj(context,modelRes);
        loadProgram(context,vertexShader,fragmentShader);
        
        
        for(Vec3D d3:vertexNormalList)
        {
        	normal.put(d3.x);
        	normal.put(d3.y);
        	normal.put(d3.z);
        }
        normal.position(0);
    }
    
    public void draw(float[] mvpMatrix , float[] projectionM4,float[] viewM4, float[] modelM4 ,float [] viewPos) {
        
    	GLES20.glUseProgram(Program);
        
        GLES20.glEnableVertexAttribArray(PositionLocation);
        GLES20.glVertexAttribPointer(
        		PositionLocation, 3, GLES20.GL_FLOAT, false, 3*4, this.vertex);
        
        GLES20.glEnableVertexAttribArray(NormalLocation);
        GLES20.glVertexAttribPointer(
        		NormalLocation , 3, GLES20.GL_FLOAT, false, 3*4 , this.normal );

		GLES20.glUniformMatrix4fv(projectionLocation, 1, false , projectionM4, 0);
		GLES20.glUniformMatrix4fv(viewLocation, 1, false, viewM4 , 0);
		GLES20.glUniformMatrix4fv(modelLocation, 1, false, modelM4 , 0);
		GLES20.glUniform3fv(viewPosLocation, 1 , viewPos, 0);
        // Draw the cube.
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, this.indexLength , GLES20.GL_UNSIGNED_INT, this.index);

        // Disable vertex arrays.
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
        	viewPosLocation  =   GLES20.glGetUniformLocation(Program, "viewPos");

    	}
    }
    private void loadObj(Context context,String modelRes)
    {
		AssetManager am = null;
		am = context.getAssets();
		try{
		    InputStream is = am.open(modelRes);
		    InputStreamReader isr = new InputStreamReader(is);
			BufferedReader in = new BufferedReader(isr);
			
			String line = "";
			while( line != null)
			{
				line = in.readLine();
				if(line != null)
				{
					parseLine(line);
				}
			}
			
			in.close();
			isr.close();
			is.close();
			
		}catch(Exception e) {
			Log.e("Obj3D error", e.getMessage());
		}
		
		vertex.position(0);
		normal.position(0);
		index.position(0);
		texture.position(0);

    }
    
    
	private void parseLine(String line)
	{
		if(line.startsWith("v "))
		{
			line = line.substring(1);
			line = line.trim();
			String vs[] = line.split("\\s");
			vertex.put(Float.parseFloat(vs[0]));
			vertex.put(Float.parseFloat(vs[1]));
			vertex.put(Float.parseFloat(vs[2]));
			
			Vec3D v3 = new Vec3D(Float.parseFloat(vs[0]),Float.parseFloat(vs[1]),Float.parseFloat(vs[2]));
			vertexList.add(v3);
			vertexNormalList.add(null);
			vertexLength += 3;
		}
		else if(line.startsWith("vn "))
		{
			line = line.substring(2);
			line = line.trim();
			String nvs[] = line.split("\\s");
			normal.put(Float.parseFloat(nvs[0]));
			normal.put(Float.parseFloat(nvs[1]));
			normal.put(Float.parseFloat(nvs[2]));
			normal.put(1.0f);
			normalLength += 4;
			Vec3D v3 = new Vec3D(Float.parseFloat(nvs[0]),Float.parseFloat(nvs[1]),Float.parseFloat(nvs[2]));
			normalList.add(v3);
		}
		else if(line.startsWith("vt "))
		{
			line = line.substring(2);
			line = line.trim();
			String nvs[] = line.split("\\s");
			texture.put(Float.parseFloat(nvs[0]));
			texture.put(Float.parseFloat(nvs[1]));
			texture.put(Float.parseFloat(nvs[2]));
			
			textureLength += 3;
			Vec3D v3 = new Vec3D(Float.parseFloat(nvs[0]),Float.parseFloat(nvs[1]),Float.parseFloat(nvs[2]));
			textureList.add(v3);
		}
		else if(line.startsWith("f "))
		{
			 line = line.substring(1);
			 line = line.trim();
			 String fs[] = line.split("\\s");
			 String IntStr = fs[0].substring(0, fs[0].indexOf("/"));
			 int index1 = Integer.parseInt(IntStr)-1;
			 index.put(index1);
			 IntStr = fs[0].substring(fs[0].lastIndexOf("/")+1);
			 int noramIndex1 = Integer.parseInt(IntStr)-1;
			 addVertexNormal(index1,noramIndex1);
			 
			 
			 IntStr = fs[1].substring(0, fs[1].indexOf("/"));
			 int index2 = Integer.parseInt(IntStr)-1;
			 index.put(index2);
			 IntStr = fs[1].substring(fs[1].lastIndexOf("/")+1);
			 int noramIndex2 = Integer.parseInt(IntStr)-1;
			 addVertexNormal(index2,noramIndex2);

			 
			 IntStr = fs[2].substring(0, fs[2].indexOf("/"));
			 int index3 = Integer.parseInt(IntStr)-1;
			 index.put(index3);
			 IntStr = fs[2].substring(fs[2].lastIndexOf("/")+1);
			 int noramIndex3 = Integer.parseInt(IntStr)-1;
			 addVertexNormal(index3,noramIndex3);
			 
			 indexLength += 3;
		}

	}
	
	void addVertexNormal(int vindex,int nindex)
	{
		 Vec3D nv = vertexNormalList.get(vindex);
		 if(nv == null)
		 {
			 vertexNormalList.set(vindex, normalList.get(nindex));
		 }
		 else
		 {
			 Vec3D newNV =  nv.add(normalList.get(nindex));
			 vertexNormalList.set(vindex,newNV);
		 }

	}

}
