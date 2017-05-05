package com.oest.obj;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;








import com.oest.base.GLDrawLineProgram;
import com.oest.base.RawTriangleProgram;
import com.oest.base.ShaderLoader;
import com.oest.base.Utils;
import com.oest.nativeclass.OceanNative;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class OceanFFT {
	
    private int Program;
    private OceanNative ndk;
    private float time = 0f; 
    
	int [] vbo_vertices = new int[1];
	int [] vbo_indices = new int[1];
	public static FloatBuffer vertexBuffer;
	public IntBuffer indexBuffer;
	public boolean geometry = false;
	
    private  int PositionLocation;
    private  int NormalLocation;
    private int projectionLocation;
    private int viewLocation;
    private int modelLocation; 
    private int viewPosLocation;
    private int sampleLocation;
    
    public static int N = 64;
    public static int NPlus1 = N+1;
    
    private int bufferLen = NPlus1* NPlus1 * 15 *4;
    private ByteBuffer  directBuffer ;
    private FloatBuffer normalLineBuffer;
    
    public Lock lock = new ReentrantLock();
    private OceanWave oceanwave;
    
    private int [] samples = new int[1];
    
	public OceanFFT(Context context)
	{
		directBuffer = ByteBuffer.allocateDirect(bufferLen);
		directBuffer.order(ByteOrder.nativeOrder());
		
		ByteBuffer temp  = ByteBuffer.allocateDirect(NPlus1* NPlus1 * 6 *4);
		temp.order(ByteOrder.nativeOrder());
		normalLineBuffer = temp.asFloatBuffer();
		
		
		ndk = new OceanNative(N,0.0001f,8f,8f,64,directBuffer);
		oceanwave = new OceanWave(context);
		
		float[] vertexarray = ndk.verticesArray();
		int[] indexarray =  ndk.indicesArray();
				
        vertexBuffer = directBuffer.asFloatBuffer();
        vertexBuffer.position(0);
        
        ByteBuffer bb = ByteBuffer.allocateDirect(indexarray.length * 4);
        bb.order(ByteOrder.nativeOrder());
        indexBuffer = bb.asIntBuffer();
        indexBuffer.position(0);
        indexBuffer.put(indexarray);
        indexBuffer.position(0);
        
        createProgram(context);
        
		GLES20.glGenBuffers(1, vbo_vertices,0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexarray.length * 4 , vertexBuffer , GLES20.GL_DYNAMIC_DRAW);

		GLES20.glGenBuffers(1, vbo_indices,0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vbo_indices[0]);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, (ndk.indicesSize())*4 , indexBuffer , GLES20.GL_STATIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0 );
 
		Utils.loadMaterial(context, "images/20170501222112.jpg", samples);

	}
	
	

	public void simulate()
	{
		time += 1.0f/20.0f;
		ndk.oceanRender(time); 
		
		OceanWave.simluate();
	}
	
	public void draw(float[] projectionM4,float[] viewM4, float[] modelM4 ,float [] viewPos)
	{
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		GLES20.glUseProgram(Program);
		
		GLES20.glUniformMatrix4fv(projectionLocation, 1, false , projectionM4, 0);
		GLES20.glUniformMatrix4fv(viewLocation, 1, false, viewM4 , 0);
		GLES20.glUniform3fv(viewPosLocation, 1 , viewPos, 0);
		
    	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, samples[0]);
		GLES20.glUniform1i(sampleLocation , GLES20.GL_TEXTURE0);

		
        vertexBuffer.position(0);
        
		
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices[0]);
		GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, ndk.verticesSize()*4*15, vertexBuffer );
		
		GLES20.glEnableVertexAttribArray(PositionLocation);
        GLES20.glVertexAttribPointer( PositionLocation  , 3 ,GLES20.GL_FLOAT, false, 15*4 , 0 );
        
  
		GLES20.glEnableVertexAttribArray(NormalLocation);
		GLES20.glVertexAttribPointer(NormalLocation, 3, GLES20.GL_FLOAT, false, 15 * 4 , 12 );

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0 );
		
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vbo_indices[0]);

		for (int j = -1; j < 4 ; j++) {
			for (int i = -1; i < 4 ; i++) {
				float [] m = Arrays.copyOf(modelM4, 16);
				Matrix.scaleM(m, 0, 10.f, 10.f, 10.f );
				Matrix.translateM(m, 0, 64f*j - 32*2, 0f, 64f*i - 32*2 );
				GLES20.glUniformMatrix4fv(modelLocation , 1, false, m , 0);
				GLES20.glDrawElements(geometry ? GLES20.GL_LINES : GLES20.GL_TRIANGLES, ndk.indicesSize(), GLES20.GL_UNSIGNED_INT, 0);
				
			}
		}

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0 );
		
	
	}
	
	private String vertexFile = "glsl/oceanfft/ocean.v";
	private String fragmentFile = "glsl/oceanfft/ocean.f";
	private String geometryFile = null;
	
	private void createProgram(Context context)
	{
		Program = ShaderLoader.loadShader(context, vertexFile, fragmentFile);
    	PositionLocation = GLES20.glGetAttribLocation(Program, "Position");
    	NormalLocation = GLES20.glGetAttribLocation(Program, "Normal");
    	projectionLocation     = GLES20.glGetUniformLocation(Program, "Projection");
    	viewLocation           = GLES20.glGetUniformLocation(Program, "View");
    	modelLocation          = GLES20.glGetUniformLocation(Program, "Model");
    	viewPosLocation  =   GLES20.glGetUniformLocation(Program, "viewPos");
    	sampleLocation =  GLES20.glGetUniformLocation(Program, "sample");
	}
	

}
