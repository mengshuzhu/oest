            precision mediump float;
            
            uniform vec3 viewPos;
            uniform sampler2D sample;
            uniform int hasSample;
            uniform vec3 kdColor;
            
            varying vec3 Frag_Normal;
			varying vec3 Frag_Pos;
			varying vec2 Frag_Texture;
            
            void main() {
              	float specularStrength = 1.9;
	float diffuseStrength = 0.5;
	vec3 lightColor = vec3(0.9,0.9,0.9);
	// vec3 viewPos = vec3(10.0,300.0,500.0);
	 vec3 objectColor ; 
	 //vec3 objectColor = vec3(Frag_Texture,1.0);
	if(hasSample == 1)
	 objectColor = vec3(texture2D(sample,Frag_Texture));
	else
	 objectColor = kdColor;
	
	vec3 ambient = vec3(0.4,0.4,0.4);
	
	vec3 norm = normalize(Frag_Normal);
	//vec3 lightDir = normalize(light_position - Frag_Pos);
	vec3 lightDir = normalize(vec3(1.0,1.0,0.0));
	
	float diff = max(dot(norm, lightDir), 0.0);
	vec3 diffuse =diffuseStrength * diff * lightColor;
	
	vec3 viewDir = normalize(viewPos - Frag_Pos);
	vec3 reflectDir = reflect(-lightDir, norm);
	
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32.0);
	vec3 specular = specularStrength * spec * lightColor;
	  vec3 result = (ambient + diffuse + specular) * objectColor;
	//	vec3 result = (ambient + specular) * objectColor;
	//	vec3 result = ambient * objectColor + diff * objectColor;
	
	gl_FragColor = vec4(result , 1.0 );
	

            }