package com.oest.nativeclass;


public class OceanNative {
	public OceanNative(int N,float A,float Wx,float Wy,float length,Object buffer)
	{
		initOcean(N,A,Wx,Wy,length,buffer);
	}
    public native String  stringTestNdk ();  
    public native String  stringTestNdk2 ();  
    public native float[] verticesArray();
    public native int[] indicesArray();
    public native int indicesSize();
    public native int verticesSize();
    public native void initOcean(int N,float A,float Wx,float Wy,float length,Object buffer);
    public native void oceanRender(float t);
    
    static {
        System.loadLibrary("oest");
    }
}
