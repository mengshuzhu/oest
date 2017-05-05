            
            uniform mat4 Projection;
			uniform mat4 View;
			uniform mat4 Model;
            attribute vec4 Position;
           	attribute vec3 Normal;
           	varying vec3 Frag_Normal;
			varying vec3 Frag_Pos;
			varying vec2 Frag_Texture;
           	
            void main() {
            
            vec4 temp = View * Model * Position;
          
            gl_Position = Projection * temp;
            
            Frag_Normal = vec3( mat3(Model) * Normal );
			Frag_Pos = vec3( Model * Position);
            Frag_Texture = vec2(Position.x/64.0,Position.z/64.0);
              
            }