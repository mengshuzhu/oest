 			uniform mat4 uMVPMatrix;
            uniform vec3 vLightPosition;
            uniform vec4 vAmbientMaterial;
            uniform vec4 vSpecularMaterial;
            uniform vec4 vDiffuseMaterial;
            uniform float shininess;
            
            uniform mat4 Projection;
			uniform mat4 View;
			uniform mat4 Model;
            
            attribute vec3 vPosition;
            attribute vec4 vNormal;
            
            varying vec3 normal_vector;
			varying vec3 Frag_Pos;
            
            void main() {
            
            gl_Position = Projection * View * Model * vPosition;
            
            normal_vector = vec3( mat3(Model) * vNormal );
			Frag_Pos = vec3(Model * vPosition);
            
            }