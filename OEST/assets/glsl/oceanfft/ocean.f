            precision mediump float;
            varying vec3 Frag_Normal;
			varying vec3 Frag_Pos;
			varying vec2 Frag_Texture;
			
            uniform vec3 viewPos;
            
            uniform sampler2D sample;
            
            void main() {
    
    float specularStrength = 1.0;
	float diffuseStrength = 0.8;
	vec3 lightColor = vec3(0.7,0.7,0.7);
	
	//vec3 objectColor  = vec3(50.0/255.0,150.0/255.0,200.0/255.0);
	//vec3 objectColor = vec3(texture2D(sample,Frag_Texture));
	vec3 objectColor  = vec3(30.0/255.0,130.0/255.0,200.0/255.0);
	
	vec3 ambient = vec3(0.1,0.1,0.1);
	
	vec3 norm = normalize(Frag_Normal);
	//vec3 lightDir = normalize(light_position - Frag_Pos);
	vec3 lightDir = normalize(vec3(0.0,1.0,0.0));
	float diff =  max(dot(norm, lightDir), 0.0);
	
	vec3 diffuse = diffuseStrength * diff * lightColor;
	
	vec3 viewDir = normalize(viewPos - Frag_Pos);
	vec3 reflectDir = reflect(-lightDir, norm);
	
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), 2.0);
	vec3 specular = specularStrength * spec * lightColor;
	vec3 result = (ambient+diffuse+specular) * objectColor;
	// vec3 result = (specular) * objectColor;
	
	gl_FragColor = vec4(result, 1.0);

            }