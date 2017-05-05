            precision mediump float;
            varying vec3 Frag_Normal;
            varying vec3 Frag_Position;
            
            uniform vec3 ViewPos;
            
            void main() {
            
            float diffuseStrength = 1.0;
            float specularStrength = 1.0;
            
            vec3 lightColor = vec3(1.0,1.0,1.0);
            vec3 objectColor = vec3(1.0,1.0,1.0);
            vec3 lightDir = normalize(vec3(1.0,-1.0,0.0));
            
            vec3 normal = normalize(Frag_Normal);
	
			float diff = max(dot(normal, lightDir), 0.0);
			vec3 diffuse =diffuseStrength * diff * lightColor;
			
			vec3 reflectDir = reflect(-lightDir, normal);
			vec3 viewDir = normalize(ViewPos - Frag_Position);
			float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32.0);
			vec3 specular = specularStrength * spec * lightColor;
			
			vec3 result = (specular+ diffuse) * objectColor;
			
			gl_FragColor = vec4( result ,1.0);
			
            }