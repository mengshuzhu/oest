package com.oest.obj;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;

import com.oest.base.BaseDrawObject;
import com.oest.draw.WaterSpray;

public class WaterSprayS extends BaseDrawObject {

	private List<WaterSpray> list = new LinkedList<WaterSpray>();
	
	public WaterSprayS(Context context)
	{
		WaterSpray spray = new WaterSpray(context,new float[]{0,0,400f});
		list.add(spray);
	}
	
	public void add(WaterSpray spray)
	{
		list.add(spray);
	}
	
	@Override
	public void simulate() {
		
		Iterator<WaterSpray> it = list.iterator();
		while(it.hasNext())
		{
			WaterSpray o = it.next();
			
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
		
		Iterator<WaterSpray> it = list.iterator();
		while(it.hasNext())
		{
			WaterSpray o = it.next();
			o.draw(projectionM4, viewM4, modelM4, viewPos);
		}

	}

	@Override
	public void initGLStatus(Context context) {
		// TODO Auto-generated method stub
		
	}

	
}
