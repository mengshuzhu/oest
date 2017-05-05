            
            uniform mat4 Projection;
			uniform mat4 View;
			uniform mat4 Model;
            
            attribute vec4 Position;
            attribute vec4 Color;
            
            varying vec4 Frag_Color;
            varying vec4 Frag_WordPos;
            
            void main() {
          
            gl_Position = Projection * View * Model * Position;
            Frag_Color = Color;
            Frag_WordPos = Model * Position;
            
            }