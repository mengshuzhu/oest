            
            uniform mat4 Projection;
			uniform mat4 View;
			uniform mat4 Model;
            
            attribute vec4 Position;
             
            void main() {
            
            gl_PointSize = 4.0;
            gl_Position = Projection * View * Model * Position;
            
            }