            precision mediump float;
            
            uniform sampler2D sample;
            uniform float alpha;
            
			varying vec2 Frag_Texture;
			
            
            void main() {

	 		vec3 objectColor = vec3(texture2D(sample,Frag_Texture));
			
			if( objectColor.r < 0.8 )
				discard;
		
			gl_FragColor = vec4(objectColor , alpha );

            }