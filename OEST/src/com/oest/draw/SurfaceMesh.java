package com.oest.draw;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import toxi.geom.Vec3D;

public class SurfaceMesh {

	public  FloatBuffer vertex;
	public  FloatBuffer normal;
	public  IntBuffer index;

	private int row;
	private int colum;
	private float space = 10f;
	
	public SurfaceMesh(int row,int colum)
	{
		this.row = row;
		this.colum = colum;
    	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(row * colum * 3 * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		vertex = byteBuffer.asFloatBuffer();
		
    	byteBuffer = ByteBuffer.allocateDirect(row * colum * 3 * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		normal = byteBuffer.asFloatBuffer();
		
		int faces = (row - 1)*(colum - 1)*2;
		index = IntBuffer.allocate(faces*3);
		
	       for(int y = 0 ; y <row ;y ++ )
	       {
	        	for(int  x = 0  ; x <colum ; x++ )
	        	{
	        		float fx = (float)(x*space);
	        		float fy = (float)(y*space);
	        		vertex.put(fx);
	        		//vertex.put( 20f*(float)Math.sin(fx * (3.14f/180f)));
	        		vertex.put(0);
	        		vertex.put(fy);
	        	}
	       }
	       
	        for(int r = 0; r < row - 1 ; r++)
	        {
	        for(int c = 0; c < colum-1 ; c++)
	        {
	        	int pointa = (short)(c+(r*row));
	        	index.put(pointa);
	        	int pointb = (short)(c+(r*row)+1);
	        	index.put(pointb);
	        	int pointc = (short)(c+(r*row)+row);
	        	index.put(pointc);
	        	
	        	index.put((short)(c+(r*row)+1));
	        	index.put((short)(c+(r*row)+row+1));
	        	index.put((short)(c+(r*row)+row));
	        }
	        }

	        vertex.position(0);
	        index.position(0);
	        normal.position(0);
	}
	public void calculateNose(float time)
	{
		for(int i=0;i<10;i++)
		{
			float centerX = (float)Math.random()*space*row;
			float centerZ = (float)Math.random()*space*colum;
			
		       for(int rowv = 0 ; rowv <row ;rowv ++ )
		       {
		        	for(int  columv = 0  ; columv <colum ; columv++ )
		        	{
		        		int vertextIndex = colum*rowv + columv;
		        		float x = vertex.get(vertextIndex*3);
		        		float z = vertex.get(vertextIndex*3+2);
		        		float toCenterX = x-centerX;
		        		float toCcenterZ = z-centerZ;
		        		float distantToZero = (float) Math.sqrt(toCenterX*toCenterX+toCcenterZ*toCcenterZ);
		        		if(i == 0)
		        		{
		        			vertex.put(vertextIndex*3+1,0);
		        		}
		        		
		        		float lastY = vertex.get(vertextIndex*3+1);
		        		vertex.put(vertextIndex*3+1 ,lastY + 1f*(float)Math.sin(distantToZero * (3.14f/45f)) + time);
		        	}
		       }

		}
	}
	public void calculateNormal()
	{
		Vec3D thisVertextIndexV = new Vec3D();
		Vec3D nextVertextIndexV = new Vec3D();
		Vec3D prevVertextIndexV = new Vec3D();
		Vec3D leftVertextIndexV = new Vec3D();
		Vec3D rightVertextIndexV = new Vec3D();
		
	       for(int rowv = 0 ; rowv <row ;rowv ++ )
	       {
	        	for(int  columv = 0  ; columv <colum ; columv++ )
	        	{
	        		int thisVertextIndex = colum*rowv + columv;
	        		int nextVertextIndex = colum*rowv + columv+1;
	        		int prevVertextIndex = colum*rowv + columv-1;
	        		int leftVertextIndex = colum*(rowv-1) + columv;
	        		int rightVertextIndex = colum*(rowv+1) + columv;
	        		
	        		if(rowv-1 <0 || rowv+1 >= row || columv-1 < 0 || columv+1 >= colum)
	        		{
		        		normal.put(thisVertextIndex*3, 0);
		        		normal.put(thisVertextIndex*3+1, 1);
		        		normal.put(thisVertextIndex*3+2, 0);
	        		}
	        		else
	        		{
		        		thisVertextIndexV.x = vertex.get(thisVertextIndex*3);
		        		thisVertextIndexV.y = vertex.get(thisVertextIndex*3+1);
		        		thisVertextIndexV.z = vertex.get(thisVertextIndex*3+2);
		        		
		        		nextVertextIndexV.x = vertex.get(nextVertextIndex*3);
		        		nextVertextIndexV.y = vertex.get(nextVertextIndex*3+1);
		        		nextVertextIndexV.z = vertex.get(nextVertextIndex*3+2);

		        		prevVertextIndexV.x = vertex.get(prevVertextIndex*3);
		        		prevVertextIndexV.y = vertex.get(prevVertextIndex*3+1);
		        		prevVertextIndexV.z = vertex.get(prevVertextIndex*3+2);

		        		leftVertextIndexV.x = vertex.get(leftVertextIndex*3);
		        		leftVertextIndexV.y = vertex.get(leftVertextIndex*3+1);
		        		leftVertextIndexV.z = vertex.get(leftVertextIndex*3+2);

		        		rightVertextIndexV.x = vertex.get(rightVertextIndex*3);
		        		rightVertextIndexV.y = vertex.get(rightVertextIndex*3+1);
		        		rightVertextIndexV.z = vertex.get(rightVertextIndex*3+2);
		        		
		        		nextVertextIndexV.subSelf(thisVertextIndexV);
		        		prevVertextIndexV.subSelf(thisVertextIndexV);
		        		leftVertextIndexV.subSelf(thisVertextIndexV);
		        		rightVertextIndexV.subSelf(thisVertextIndexV);
		        		
		        		nextVertextIndexV.crossSelf(rightVertextIndexV);
		        		prevVertextIndexV.crossSelf(leftVertextIndexV);
		        		
		        		nextVertextIndexV.addSelf(prevVertextIndexV);
		        		
		        		normal.put(thisVertextIndex*3, nextVertextIndexV.x);
		        		normal.put(thisVertextIndex*3+1, nextVertextIndexV.y);
		        		normal.put(thisVertextIndex*3+2, nextVertextIndexV.z);
	        		}
	        		
	        	}
	       }
		
	}
	
}
