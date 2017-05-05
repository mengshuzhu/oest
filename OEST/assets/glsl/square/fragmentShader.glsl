            precision mediump float;
            varying vec4 _vColor;
            uniform sampler2D myTexture;
			varying vec2 vTexCoord;
			varying vec4 vpos ;
			uniform float ftime;
            
            float veclen(vec3 v3)
            {
              return sqrt(v3.x*v3.x + v3.y*v3.y + v3.z*v3.z);
            }
            
            void main() {
            
            float nosiv = 0.0;
			float cosy ;
			float tmepx;
			float addv = 0.0;
			for(float i=-10.0;i<10.0;i+=1.0)
			{
				tmepx  = vpos.x - i;
				cosy  = cos(vpos.y)/abs(i);
				if(tmepx+cosy > -0.11 && tmepx+cosy < 0.11)
				{
				addv = tmepx+cosy;
				nosiv += 0.2;
				}
			}
			
			float siny ;
			float tempv ;
			for(float s = -10.0;s< 10.0;s+=1.0)
			{
				siny = sin(vpos.y)/abs(s-5.0);
				tempv = vpos.x +s;
				if(siny +tempv  > -0.2 && siny +tempv < 0.2)
				{
				addv = siny +tempv;
				nosiv = 0.2;
				}
					
			}
			
			if(nosiv >0.0)
             gl_FragColor = vec4(30.0/255.0 ,130.0/255.0,160.0/255.0,0.5)+addv;
             else
             gl_FragColor = vec4(30.0/255.0 ,130.0/255.0,160.0/255.0,0.5);
		            
                        
            }
