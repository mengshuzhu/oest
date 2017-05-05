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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

public class Obj3DMate {
	
	public  FloatBuffer vertex;
	public  FloatBuffer normal;
	public  FloatBuffer texture;
	public  Map<Integer,ByteBuffer> index = new HashMap<Integer,ByteBuffer>();
	public  Map<Integer,IntBuffer> indexDirect = new HashMap<Integer,IntBuffer>();

	
	public ByteBuffer vertexbyteBuffer;
	public ByteBuffer normalbyteBuffer;
	public ByteBuffer texturebyteBuffer;
	public ByteBuffer indexbyteBuffer;
	public ByteBuffer materialBuffer;
	
	private Map<String,Map<String,Object>> material;
	
    private static final int VERTEXT_HEAD = 10;
    private static final int NORMAL_HEAD = 11;
    private static final int TEXTURE_HEAD = 13;
    private static final int INDEX_HEAD = 12;
    private static final int MATERIAL_HEAD = 14;
    
    protected  Integer Program = null;
    
    protected  Integer PositionLocation= null;
    protected  Integer NormalLocation= null;
    protected  Integer  textCoorLocation = null;
    protected  Integer projectionLocation= null;
    protected  Integer viewLocation= null;
    protected  Integer modelLocation= null;
    protected  Integer viewPosLocation= null;
    protected  Integer sampleLocation = null;

    public Obj3DMate(Context context,String res,String vertexShader,String fragmentShader)
    {
    	try {
    		long begin = System.currentTimeMillis();
    		AssetManager am = null;
    		am = context.getAssets();
    		InputStream is = am.open(res);
			while(readChunk(is))
			{
				
			}
			
			loadMaterial(context);
			
			System.out.println("load time "+ (System.currentTimeMillis() -begin));
			
			loadProgram(context,vertexShader,fragmentShader);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    int [] textures;
    private void loadMaterial(Context context)
    {
    	int size = material.size();
    	textures = new int[size];
		GLES20.glGenTextures(size , textures, 0 );
    	GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

		AssetManager am = null;
		am = context.getAssets();

    	for(Entry<String,Map<String,Object>> entry :material.entrySet())
    	{
    		Map map = entry.getValue();
    		Integer mindex = (Integer) map.get("index");
    		if(mindex != 0)
    		{
        		InputStream is = null;
    			try {
    				is = am.open((String)map.get("map_Ka"));
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
        		Bitmap bitmap = null;
        		bitmap = BitmapFactory.decodeStream(is);
        		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[mindex]);
        		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        		bitmap.recycle();

    		}
    	}

    }
	byte[] headByteBuffer = new byte[8];
	ByteBuffer headByte = ByteBuffer.wrap(headByteBuffer);
    private boolean readChunk(InputStream is)
    {
    	try {
    	
		int headlen = is.read(headByte.array(), 0, 8);
		IntBuffer HeadInt = headByte.asIntBuffer();
		if(headlen != 8)
		{
			System.out.println("readChunk end headlen = " + headlen);
			return false;
		}
		int headSign  = HeadInt.get(0);
		switch(headSign)
		{
			case VERTEXT_HEAD:
				int len = HeadInt.get(1);
				vertexbyteBuffer = ByteBuffer.allocateDirect(len);
				vertexbyteBuffer.order(ByteOrder.nativeOrder());
				vertex = vertexbyteBuffer.asFloatBuffer();
				
				int readLean = is.read(vertexbyteBuffer.array() , 4 , len);
				for(int i=0;i<3;i++)
					System.out.println("vertex "+vertex.get(i));
				
				if(readLean == len)
					System.out.println("read VERTEXT "+len+" sucessed");
				else
				{
					System.err.println("read VERTEXT "+len+" failed actal "+readLean);
					return false;
				}

			break;
			case NORMAL_HEAD:
				
				 len = HeadInt.get(1);
				normalbyteBuffer = ByteBuffer.allocateDirect(len);
				normalbyteBuffer.order(ByteOrder.nativeOrder());
				normal = normalbyteBuffer.asFloatBuffer();
				
				 readLean = is.read(normalbyteBuffer.array() , 4 , len);
				if(readLean == len)
					System.out.println("read NORMAL "+len+" sucessed");
				else
				{
					System.err.println("read NORMAL "+len+" failed actal "+readLean);
					return false;
				}

			break;
			case TEXTURE_HEAD:
				 len = HeadInt.get(1);
				 texturebyteBuffer = ByteBuffer.allocateDirect(len);
				 texture = texturebyteBuffer.asFloatBuffer();
				 
				 readLean = is.read(texturebyteBuffer.array() ,4 , len);
				 
					if(readLean == len)
						System.out.println("read TEXTURE "+len+" sucessed");
					else
					{
						System.err.println("read TEXTURE "+len+" failed actal "+readLean);
						return false;
					}
			break;
			case INDEX_HEAD:
				 len = HeadInt.get(1);
				 int lenindex = len-4;
				 indexbyteBuffer = ByteBuffer.wrap(new byte[lenindex]);
				 ByteBuffer materailIndex = ByteBuffer.wrap(new byte[4]);
				 
				 			is.read(materailIndex.array() , 0, 4);
				 readLean = is.read(indexbyteBuffer.array() , 0, lenindex);
				 Integer mindex = Integer.valueOf(materailIndex.asIntBuffer().get(0));
				 index.put(mindex, indexbyteBuffer);
				 IntBuffer directInt = IntBuffer.allocate(lenindex/4);
				 directInt.put(indexbyteBuffer.asIntBuffer());
				 directInt.position(0);
				 indexDirect.put(mindex, directInt);
				 
				 if(directInt.capacity() >3)
				 {
						for(int i=0;i<3;i++)
							System.out.println("directInt "+directInt.get(i));
				 }

				if(readLean == (lenindex))
					System.out.println("read INDEX "+(lenindex)+" sucessed material "+mindex);
				else
				{
					System.err.println("read INDEX "+lenindex+" failed actal "+readLean);
					return false;
				}

			break;
			case MATERIAL_HEAD:
				 len = HeadInt.get(1);
				 materialBuffer = ByteBuffer.wrap(new byte[len]);
				 readLean = is.read(materialBuffer.array() , 0, len);
				 ByteArrayInputStream materailInput = new ByteArrayInputStream(materialBuffer.array());
				 ObjectInputStream materialstream = new ObjectInputStream(materailInput);
				 material = (Map<String,Map<String,Object>>)materialstream.readObject();
				 System.out.println(material.toString());
				 materialstream.close();
				 
				 if(readLean == len)
					System.out.println("read Material "+len+" sucessed");
				 else
				 {
					System.err.println("read Material "+len+" failed actal "+readLean);
					return false;
				 }

			break;
			default:
				System.err.println(" not a vild chunk");
				return false;
		}
		
		return true;
		
    	} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
    	
    }

    
    public void draw(float[] mvpMatrix , float[] projectionM4,float[] viewM4, float[] modelM4 ,float [] viewPos) {
        
    	GLES20.glUseProgram(Program);
    	
    	GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glEnableVertexAttribArray(PositionLocation);
        GLES20.glVertexAttribPointer(
        		PositionLocation, 3, GLES20.GL_FLOAT, false, 3*4, this.vertex);
        
        GLES20.glEnableVertexAttribArray(NormalLocation);
        GLES20.glVertexAttribPointer(
        		NormalLocation , 3 , GLES20.GL_FLOAT, false, 12 , this.normal );
        
        GLES20.glEnableVertexAttribArray(textCoorLocation);
        GLES20.glVertexAttribPointer(
        		textCoorLocation , 3 , GLES20.GL_FLOAT, false, 12 , this.texture );


		GLES20.glUniformMatrix4fv(projectionLocation, 1, false , projectionM4, 0);
		GLES20.glUniformMatrix4fv(viewLocation, 1, false, viewM4 , 0);
		
		Matrix.scaleM(modelM4, 0, 1.f, 1.f, 1.f);
		Matrix.rotateM(modelM4, 0, 90, -1, 0, 0);
		GLES20.glUniformMatrix4fv(modelLocation, 1, false, modelM4 , 0);
		GLES20.glUniform3fv(viewPosLocation, 1 , viewPos, 0);
        // Draw the cube.
		for(Entry<Integer,IntBuffer> entry:indexDirect.entrySet())
		{
			Integer mat = entry.getKey();
			IntBuffer Intb = entry.getValue();
			
			if(mat > 0)
			{
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[mat]);
				GLES20.glUniform1i(sampleLocation, GLES20.GL_TEXTURE0);
		        GLES20.glDrawElements(GLES20.GL_TRIANGLES, Intb.capacity() , GLES20.GL_UNSIGNED_INT, Intb );
			}
			
		}
		

        // Disable vertex arrays.
        GLES20.glDisableVertexAttribArray(PositionLocation);
        GLES20.glDisableVertexAttribArray(NormalLocation);
        GLES20.glDisableVertexAttribArray(textCoorLocation);
        
    }

    
    private void loadProgram(Context context,String vertexShader,String fragmentShader)
    {
    	if(Program == null)
    	{
        	Program = ShaderLoader.loadShader(context, vertexShader , fragmentShader );
        	PositionLocation = GLES20.glGetAttribLocation(Program, "Position");
        	NormalLocation = GLES20.glGetAttribLocation(Program, "Normal");
        	textCoorLocation = GLES20.glGetAttribLocation(Program, "Texture");
        	
        	projectionLocation     = GLES20.glGetUniformLocation(Program, "Projection");
        	viewLocation           = GLES20.glGetUniformLocation(Program, "View");
        	modelLocation          = GLES20.glGetUniformLocation(Program, "Model");
        	viewPosLocation  =   GLES20.glGetUniformLocation(Program, "viewPos");
        	sampleLocation = GLES20.glGetUniformLocation(Program, "sample");
    	}
    }



}
