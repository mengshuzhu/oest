            
            uniform mat4 Projection;
			uniform mat4 View;
			uniform mat4 Model;
            
            attribute vec4 Position;
            attribute vec3 Normal;
            
            varying vec3 Frag_Normal;
			varying vec3 Frag_Pos;
            
            void main() {
          
            gl_Position = Projection * View * Model * Position;
            
            Frag_Normal = vec3( mat3(Model) * Normal );
			Frag_Pos = vec3(Model * Position);
            
            }