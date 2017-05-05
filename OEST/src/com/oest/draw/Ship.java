package com.oest.draw;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.oest.base.GLDrawLineProgram;
import com.oest.base.GLDrawPointsProgram;
import com.oest.obj.Obj3DData;

import toxi.geom.Vec3D;
import android.content.Context;
import android.opengl.Matrix;


public class Ship {
	
	private Obj3DData data;
	private Context context;
	
    public List<Cannon> rightCannons = new ArrayList<Cannon>();
    public List<Cannon> leftCannons = new ArrayList<Cannon>();
    private ShipWave shipwave;
    
    public Lock lock = new ReentrantLock();
    
    private GLDrawPointsProgram drawPoints;
    public FloatBuffer  outLine,outLine2;
    private WaterWave waterWave;
    
	public Ship(Context context)
	{
		this.context = context;
		data = new Obj3DData(context,"3DMODLE/ship/shop6.data");
		shipwave = new ShipWave(context); 
		
		ByteBuffer tempBuffer = ByteBuffer.allocateDirect(2000*3*4);
		tempBuffer.order(ByteOrder.nativeOrder());
		outLine = tempBuffer.asFloatBuffer();
		
		for(int i=0;i<(data.vertex.capacity()-3);i+=3)
		{
			float x = data.vertex.get(i);
			float y = data.vertex.get(i+1);
			float z = data.vertex.get(i+2);
			
			if(y > 30 && y < 40 )
			{
				outLine.put(x);
				outLine.put(y);
				outLine.put(z);
			}
		}
		
		int outLineFloatCount = outLine.position();
		int outLinePointCount = outLine.position()/3;
		
		
		tempBuffer = ByteBuffer.allocateDirect(outLineFloatCount*4);
		tempBuffer.order(ByteOrder.nativeOrder());
		outLine2 = tempBuffer.asFloatBuffer();

		for(int i=0;i<outLineFloatCount;i++)
		{
			outLine2.put(i, outLine.get(i));
		}
		
		waterWave = new WaterWave(context,outLine2);
		drawPoints = new GLDrawPointsProgram(context,outLine2);
		/////////////
		Cannon cannon;  
        cannon = new Cannon(context,new Vec3D(0,300,-300),new Vec3D(1,0,0));
        rightCannons.add(cannon);
        cannon =  new Cannon(context,new Vec3D(0,300,0),new Vec3D(1,0,0));
        rightCannons.add(cannon);
        cannon =  new Cannon(context,new Vec3D(0,300,300),new Vec3D(1,0,0));
        rightCannons.add(cannon);
        cannon =  new Cannon(context,new Vec3D(0,300,600),new Vec3D(1,0,0));
        rightCannons.add(cannon);
        cannon =  new Cannon(context,new Vec3D(0,300,900),new Vec3D(1,0,0));
        rightCannons.add(cannon);
        
        cannon = new Cannon(context,new Vec3D(0,300,300),new Vec3D(1,0,0));
        leftCannons.add(cannon);
        cannon =  new Cannon(context,new Vec3D(0,300,0),new Vec3D(1,0,0));
        leftCannons.add(cannon);
        cannon =  new Cannon(context,new Vec3D(0,300,-300),new Vec3D(1,0,0));
        leftCannons.add(cannon);
        cannon =  new Cannon(context,new Vec3D(0,300,-600),new Vec3D(1,0,0));
        leftCannons.add(cannon);
        cannon =  new Cannon(context,new Vec3D(0,300,-900),new Vec3D(1,0,0));
        leftCannons.add(cannon);
	}
	private float angleZ = 20;
	private float time = 0f;
	private float heightPointA = 100f;
	private float pointASpeed = 0f;
	private float gravity = -9.8f;
	public float buoyance = 9.8f;
	
	private float heightPointB = 100f;
	private float pointBSpeed = 0f;
	public float Bbuoyance = 9.8f;
	private float angleX = 0f;
	
	public void simulate()
	{
		 float dt = 0.2f;
		 time += dt;
		 
		 pointASpeed += dt * gravity;
		 pointASpeed += dt * buoyance;
		 
		 float move = pointASpeed * dt;
		 
		 heightPointA += move;
		 
		 heightPointA = heightPointA*0.995f;
		 
		 angleZ = heightPointA/15f;
		 
		 buoyance = 9.8f - heightPointA/10;
		 
		 /////
		 pointBSpeed += dt * gravity;
		 pointBSpeed += dt * Bbuoyance;
		 move = pointBSpeed * dt;
		 heightPointB += move;
		 heightPointB = heightPointB*0.995f;
		 
		 angleX = heightPointB/10f;
		 
		 Bbuoyance = 9.8f - heightPointB/10;
		 
		 
		 for(int i=0;i<rightCannons.size();i++)
		 {
			 rightCannons.get(i).simulate();
		 }
		 for(int i=0;i<leftCannons.size();i++)
		 {
			 leftCannons.get(i).simulate();
		 }
		 
		 waterWave.simulate();
	}
	
    public void draw(float[] projectionM4,float[] viewM4, float[] modelM4 ,float [] viewPos)
	{
    	
    	
    	 float [] shipM4 =  Arrays.copyOf(modelM4, modelM4.length);
    	 float [] unRoateshipM4 =  Arrays.copyOf(modelM4, modelM4.length);
    	 
    	 float [] shipwaveModel = Arrays.copyOf(modelM4, modelM4.length);
    	 
    	 Matrix.scaleM(shipwaveModel, 0, 2f, 2f, 2f);
    	 shipwave.draw(projectionM4, viewM4, shipwaveModel , viewPos);
    	 
     	 Matrix.rotateM(shipM4 , 0, angleZ , 0, 0, 1);
     	 Matrix.rotateM(shipM4 , 0, angleX , 1 , 0, 0);

    	 //Matrix.rotateM(shipM4 , 0, angleZ , 0, 0, 1);
    	 // Matrix.rotateM(shipM4 , 0, 90, -1, 0, 0);
    	 
		 data.draw(projectionM4, viewM4, shipM4 , viewPos);
		 
		 float [] outLineM4 =  Arrays.copyOf(modelM4, modelM4.length);
		 
		 //waterWave.draw(projectionM4, viewM4, outLineM4 , viewPos);
		 
		 Matrix.translateM(outLineM4, 0, 0, 0 , 200); 
		 Matrix.scaleM(outLineM4, 0, 1, 1, 1);
		 
		 //waterWave.draw(projectionM4, viewM4, outLineM4 , viewPos);
		 
         float [] rightCannonM4 = Arrays.copyOf(shipM4, shipM4.length);
         float [] unRotateRightCannonM4 = Arrays.copyOf(unRoateshipM4, unRoateshipM4.length);
         
         Matrix.scaleM(rightCannonM4, 0, 0.3f, 0.3f, 0.3f);
         Matrix.translateM(rightCannonM4, 0 , 0 , 0 , 240 );
		 Matrix.rotateM(rightCannonM4, 0, -90, 0, 1, 0 );
		 
         Matrix.scaleM(unRotateRightCannonM4, 0, 0.3f, 0.3f, 0.3f);
         Matrix.translateM(unRotateRightCannonM4, 0 , 0 , 0 , 240 );
		 Matrix.rotateM(unRotateRightCannonM4, 0, -90, 0, 1, 0 );
		 
		 float [] rightCannonFireM4 = Arrays.copyOf(unRotateRightCannonM4, unRotateRightCannonM4.length);
     	 
		 for(int i=0;i<rightCannons.size();i++)
		 {
			 rightCannons.get(rightCannons.size()-1-i).draw( projectionM4, viewM4, rightCannonM4 , rightCannonFireM4 , viewPos ,1);
		 }
		 
		 float [] leftCannonM4 = Arrays.copyOf(shipM4, shipM4.length);
		 float [] unRotateLeftCannonM4 = Arrays.copyOf(unRoateshipM4, unRoateshipM4.length);
		 
		 
         Matrix.scaleM(leftCannonM4, 0, 0.3f, 0.3f, 0.3f);
         Matrix.translateM(leftCannonM4, 0 , 0 , 0 , -240 );
		 Matrix.rotateM(leftCannonM4, 0, 90, 0, 1, 0 );
		 
         Matrix.scaleM(unRotateLeftCannonM4, 0, 0.3f, 0.3f, 0.3f);
         Matrix.translateM(unRotateLeftCannonM4, 0 , 0 , 0 , -240 );
		 Matrix.rotateM(unRotateLeftCannonM4, 0, 90, 0, 1, 0 );

		 float [] leftCannonFireM4 =  Arrays.copyOf(unRotateLeftCannonM4, unRotateLeftCannonM4.length);
		 for(int i=0;i<leftCannons.size();i++)
		 {
			 leftCannons.get(leftCannons.size()-1-i).draw( projectionM4, viewM4, leftCannonM4 , leftCannonFireM4 , viewPos ,1);
		 }

	}
}
