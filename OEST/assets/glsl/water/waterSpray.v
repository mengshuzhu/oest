            
            uniform mat4 Projection;
			uniform mat4 View;
			uniform mat4 Model;
            
            attribute vec4 Position;
            attribute vec2 Texture;
            
			varying vec2 Frag_Texture;
            
             
            void main() {
            
            gl_Position = Projection * View * Model * Position;
            Frag_Texture = Texture.xy;
            
            }