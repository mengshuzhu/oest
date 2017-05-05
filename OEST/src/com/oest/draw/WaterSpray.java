package com.oest.draw;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.oest.base.ShaderLoader;
import com.oest.base.Utils;

public class WaterSpray {

    protected static Integer Program = null;
    
    protected static Integer PositionLocation= null;
    protected static Integer projectionLocation= null;
    protected static Integer viewLocation= null;
    protected static Integer modelLocation= null;
    
    protected static Integer sampleLocation =null;
    protected static Integer TextureLocation =null;
    
	private static int [] samples = new int[5];
	private static String [] textureImages = new String[5];

	
    public Lock lock = new ReentrantLock();
    public boolean isAlive = true;
    
    private int count = 200;
    private FloatBuffer vertex = Utils.allocateVertex(count*6);
    private FloatBuffer texture = Utils.allocateFloat(count*6*2);
    
    private float [][]  positions = new float[count][3];
    private float [][]  velocitys = new float[count][3];
    
    private float [] movePosition ;
    
    public WaterSpray(Context context , float [] movePosition)
    {
    	
    	this.movePosition = movePosition;
    	
    	for(int i=0;i<count;i++)
    	{
    		float D = (float)Math.random()*30f;
    		float A = 2*3.14f*(float)Math.random();
    		
    		positions[i][0] = (float)Math.sin(A)*D;
    		positions[i][1] = 0f;
    		positions[i][2] = (float)Math.cos(A)*D;
    		
    		float V = 20+10f*((float)Math.random());
    		float Angle = 2*3.14f*(float)Math.random();
    		float Vx = (float)Math.sin(Angle)*V;
    		float Vz =  (float)Math.cos(Angle)*V;
    		
    		velocitys[i][0] = Vx;
    		velocitys[i][1] = 300*(float)Math.random();
    		velocitys[i][2] = Vz;
    	}
    	
    	generateTriangle();
    }
    
    public void simulate()
    {
    	float dt = 1f/30f;
    	
    	boolean live = false;
    	
    	for(int i=0;i<count;i++)
    	{
    		positions[i][0] += velocitys[i][0] * dt;
    		positions[i][1] += velocitys[i][1] * dt;
    		positions[i][2] += velocitys[i][2] * dt;
    		
    		if(positions[i][1] > 0)
    			live = true;
    		
    		velocitys[i][1] -= 20f;
    	}
    	
    	isAlive = live;
    	
    	generateTriangle();
    }
    
    private void generateTriangle()
    {
    	vertex.position(0);
    	texture.position(0);
    	for(int i=0;i<count;i++)
    	{
    		vertex.put(positions[i][0]);
    		vertex.put(positions[i][1]);
    		vertex.put(positions[i][2]);
    		texture.put(0);texture.put(0);
    		
    		vertex.put(positions[i][0]+16f);
    		vertex.put(positions[i][1]);
    		vertex.put(positions[i][2]);
    		texture.put(1);texture.put(0);

    		vertex.put(positions[i][0]);
    		vertex.put(positions[i][1]+16f);
    		vertex.put(positions[i][2]);
    		texture.put(0);texture.put(1);
    		
    		//////////////////////////////
    		
    		vertex.put(positions[i][0]+16f);
    		vertex.put(positions[i][1]+16f);
    		vertex.put(positions[i][2]);
    		texture.put(1);texture.put(1);
    		
    		vertex.put(positions[i][0]+16f);
    		vertex.put(positions[i][1]);
    		vertex.put(positions[i][2]);
    		texture.put(1);texture.put(0);
    		
    		vertex.put(positions[i][0]);
    		vertex.put(positions[i][1]+16f);
    		vertex.put(positions[i][2]);
    		texture.put(0);texture.put(1);
    		
    	}
    	texture.position(0);
    	vertex.position(0);
    }
    
	public void draw(float[] projectionM4,float[] viewM4, float[] modelM4 ,float [] viewPos)
	{
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
    	GLES20.glUseProgram(Program);
    	
        GLES20.glEnableVertexAttribArray(PositionLocation);
        GLES20.glVertexAttribPointer(
        		PositionLocation, 3, GLES20.GL_FLOAT, false, 3*4, vertex );
        
        GLES20.glEnableVertexAttribArray(TextureLocation);
        GLES20.glVertexAttribPointer(
        		TextureLocation , 2 , GLES20.GL_FLOAT, false, 8 , texture);


		GLES20.glUniformMatrix4fv(projectionLocation, 1, false , projectionM4, 0);
		GLES20.glUniformMatrix4fv(viewLocation, 1, false, viewM4 , 0);
		
		float [] model = Arrays.copyOf(modelM4, modelM4.length);
				
		Matrix.translateM(model, 0, movePosition[0], movePosition[1], movePosition[2]);
		
		GLES20.glUniformMatrix4fv(modelLocation, 1, false, model , 0);


		int sampleIndex = 0;
    	for(int i=0;i<count;i++)
    	{
    		
        	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, samples[sampleIndex++]);
        	
        	if(sampleIndex == samples.length)
        		sampleIndex = 0;
        	
    		GLES20.glUniform1i(sampleLocation , GLES20.GL_TEXTURE0);
    		GLES20.glDrawArrays(GLES20.GL_TRIANGLES , i*6 , 6 );
    		
    	}
		

		GLES20.glDisableVertexAttribArray(PositionLocation);
		GLES20.glDisableVertexAttribArray(TextureLocation);
	}
	
    public static void initGLProgram(Context context)
    {
    	loadProgram(context,"glsl/water/waterSpray.v","glsl/water/waterSpray.f");
    	
    	textureImages[0] = "images/wave1.png";
    	textureImages[1] = "images/wave2.png";
    	textureImages[2] = "images/wave3.png";
    	textureImages[3] = "images/wave4.png";
    	textureImages[4] = "images/wave5.png";
    	
    	Utils.loadMaterial(context, textureImages , samples);

    }
    
    private static void loadProgram(Context context,String vertexShader,String fragmentShader)
    {
    	Program = ShaderLoader.loadShader(context, vertexShader , fragmentShader );
    	PositionLocation = GLES20.glGetAttribLocation(Program, "Position");
    	projectionLocation     = GLES20.glGetUniformLocation(Program, "Projection");
    	viewLocation           = GLES20.glGetUniformLocation(Program, "View");
    	modelLocation          = GLES20.glGetUniformLocation(Program, "Model");
    	
    	TextureLocation = GLES20.glGetAttribLocation(Program, "Texture");
    	sampleLocation = GLES20.glGetUniformLocation(Program, "sample");
    }
}
