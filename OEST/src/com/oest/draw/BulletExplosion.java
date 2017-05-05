package com.oest.draw;

import java.nio.FloatBuffer;
import java.util.Arrays;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.oest.base.BaseDrawObject;
import com.oest.base.RawTriangleProgram;
import com.oest.base.TriangleColorFanProgram;
import com.oest.base.Utils;

public class BulletExplosion extends BaseDrawObject {

    private static int fanCount = 50;
    private static int verexPerFan = 10;
    private static float radius = 40f;
    
    private FloatBuffer vertex = Utils.allocateVertex(fanCount*verexPerFan);
    private FloatBuffer texture = Utils.allocateFloat(fanCount*verexPerFan*2);
    private FloatBuffer color = Utils.allocateFloat(fanCount*verexPerFan*4);
    
    private float [][]  positions = new float[fanCount][3];
    private float [][]  velocitys = new float[fanCount][3];
    private float [] moveAngle = new float[fanCount];
    
    private float [] movePositions ;
    private float age = 0;
    private static int [] color_vbo = new int[1];
    private static int [] vertex_vbo = new int[1];
    
    public BulletExplosion(Context context,float[] initPositions)
    {
    	movePositions = initPositions;
    	
    	for(int i=0;i<fanCount;i++)
    	{
    		positions[i][0] = 0f;
    		positions[i][1] = 0f;
    		positions[i][2] = i;
    		
    		float V = 500*((float)Math.random());
    		float Angle = 2*3.14f*(float)Math.random();
    		float Vx = (float)Math.sin(Angle)*V;
    		float Vz =  (float)Math.cos(Angle)*V;

    		moveAngle[i] =Angle;
    		
    		velocitys[i][0] = Vx;
    		velocitys[i][1] = Vz;
    		velocitys[i][2] = 0;
    	}
    	
    	generateTriangle();

/*		GLES20.glGenBuffers(1, vertex_vbo,0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertex_vbo[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertex.capacity() * 4 , vertex , GLES20.GL_DYNAMIC_DRAW);

		GLES20.glGenBuffers(1, color_vbo,0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, color_vbo[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, color.capacity() * 4 , color , GLES20.GL_DYNAMIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
*/		
		
    }
    
    private void generateTriangle()
    {
    	vertex.position(0);
    	texture.position(0);
    	color.position(0);
    	
    	for(int i=0;i<fanCount;i++)
    	{
    		vertex.put(positions[i][0]);
    		vertex.put(positions[i][1]);
    		vertex.put(positions[i][2]);
    		
    		//color.put(0.6f);color.put(0.05f);color.put(0.01f);color.put(1.0f);
    		
    		for(int f=0; f< (verexPerFan-1); ++f)
   	     	{
   	    	 vertex.put(positions[i][0]+ radius*(float)Math.cos(2*Math.PI/(verexPerFan-2)*f));
   	    	 vertex.put(positions[i][1]+ radius*(float)Math.sin(2*Math.PI/(verexPerFan-2)*f));
   	    	 vertex.put(positions[i][2]);
   	    	 
   	    	// color.put(0.6f);color.put(0.05f);color.put(0.01f);color.put(0.0f);
   	    	
   	     	}
   	     }
    	
    	if(age < 1.5f)
    	{
        	for(int i=0;i<fanCount;i++)
        	{
        		color.put(0.6f);color.put(0.05f);color.put(0.01f);color.put(1.0f);
        		for(int f=0; f< (verexPerFan-1); ++f)
       	     	{
       	    	 color.put(0.6f);color.put(0.05f);color.put(0.01f);color.put(0.0f);
       	     	}

        	}
    	}
    	else 
    	{
    		for(int i=0;i<color.capacity();i++)
    		{
    			color.put(i,color.get(i)*0.9f);
    		}
    	}
    	
    	if(age >2 )
    	{
    		isAlive = false;
    	}
 		
    	texture.position(0);
    	vertex.position(0);
    	color.position(0);
    	
    }

    
	@Override
	public void simulate() {
		
		
		if(!isAlive)
			return ;
		
    	float dt = 1f/30f;
    	age +=dt;
    		
    	for(int i=0;i<fanCount;i++)
    	{
    		
    		positions[i][0] += velocitys[i][0] * dt;
    		positions[i][1] += velocitys[i][1] * dt;
    		positions[i][2] += velocitys[i][2] * dt;
    		
/*    		float V = 20;
    		float Angle = moveAngle[i];
    		float Vx = (float)Math.sin(Angle)*V;
    		float Vy =  (float)Math.cos(Angle)*V;
*/
    		float Vx = velocitys[i][0]/10f;
    		float Vy = velocitys[i][1]/10f;
    		
    		velocitys[i][0] -= Vx;
    		velocitys[i][1] -= Vy;
    		
    	}
    	
    	generateTriangle();

	}

	@Override
	public void draw(float[] projectionM4, float[] viewM4, float[] modelM4,
			float[] viewPos) {
		if(!isAlive)
			return ;

		float[] drawModel = Arrays.copyOf(modelM4, modelM4.length);
		Matrix.translateM(drawModel, 0, movePositions[0],  movePositions[1],  movePositions[2]);
		
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);

		TriangleColorFanProgram.draw(projectionM4, viewM4, drawModel, viewPos,
				color , vertex, 
				color_vbo, vertex_vbo,
				fanCount , verexPerFan, true );
		
/*		TriangleColorFanProgram.draw(projectionM4, viewM4, drawModel, viewPos,
				color , vertex,
				fanCount , verexPerFan );
*/
		GLES20.glDisable(GLES20.GL_BLEND);
		
	}

	public static void initGLProgram (Context context) {
		
	    FloatBuffer vertex = Utils.allocateVertex(fanCount*verexPerFan);
	    FloatBuffer color = Utils.allocateFloat(fanCount*verexPerFan*4);
	    
		GLES20.glGenBuffers(1, vertex_vbo,0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertex_vbo[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertex.capacity()*4 , vertex  , GLES20.GL_DYNAMIC_DRAW);

		GLES20.glGenBuffers(1, color_vbo,0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, color_vbo[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, color.capacity()*4 , color  , GLES20.GL_DYNAMIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}

	@Override
	public void initGLStatus(Context context) {
		
	}

}
