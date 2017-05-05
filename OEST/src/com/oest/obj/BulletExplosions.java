package com.oest.obj;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;

import com.oest.base.BaseDrawObject;
import com.oest.draw.BulletExplosion;
import com.oest.draw.WaterSpray;

public class BulletExplosions extends BaseDrawObject {

	private List<BulletExplosion> list = new LinkedList<BulletExplosion>();
	
	public BulletExplosions(Context context)
	{
		
	}
	
	public void add(BulletExplosion o)
	{
		list.add(o);
	}
	
	@Override
	public void simulate() {
		
		Iterator<BulletExplosion> it = list.iterator();
		while(it.hasNext())
		{
			BulletExplosion o = it.next();
			
			if(o.isAlive)
			{
				o.simulate();
			}
			else
			{
				it.remove();
			}
		}
		
	}

	@Override
	public void draw(float[] projectionM4, float[] viewM4, float[] modelM4,
			float[] viewPos) {
		
		Iterator<BulletExplosion> it = list.iterator();
		while(it.hasNext())
		{
			BulletExplosion o = it.next();
			o.draw(projectionM4, viewM4, modelM4, viewPos);
		}

	}

	@Override
	public void initGLStatus(Context context) {
		// TODO Auto-generated method stub
		
	}

	
}
