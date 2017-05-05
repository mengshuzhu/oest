            uniform mat4 uMVPMatrix;
            attribute vec4 vPosition;
            attribute vec3 vNormal;
            attribute vec2 textureCood;
        	uniform vec2 textureCoodAdd;
            
            uniform vec3 vLightPosition;
            uniform vec4 vAmbientMaterial;
            uniform vec4 vSpecularMaterial;
            uniform vec4 vDiffuseMaterial;
            uniform float shininess;
            uniform float time;
            
            varying vec4 _vColor;
            varying vec2 vTexCoord;
            varying vec4 vpos ;
            
            void main() {
            
            vec3 N = vNormal;
            vec3 L = normalize(vLightPosition);
            vec3 E = vec3(0, 0, 1);
            vec3 H = normalize(L + E);
            float df = max(0.0, dot(N, L));
            float sf = max(0.0, dot(N, H));
            sf = pow(sf, shininess); 
            _vColor = vAmbientMaterial*shininess + df * vDiffuseMaterial*shininess + sf * vSpecularMaterial*shininess;
            
            
            float pz = 0.0;
            
            float ppx = vPosition.x;
            float ppy = vPosition.y;
            
            float d = abs(sqrt(ppx*ppx+ppy*ppy));
            
           // pz = pz + (sin(sqrt(d)*8.0+sqrt(time) )/20.0)+ (sin(d*18.0+time)/40.0);
            
            ppx = ppx + 40.4;
            ppy = ppy + 40.4;
            
            float d2 = abs(sqrt(ppx*ppx+ppy*ppy));
            
            //pz = pz + (sin(sqrt(d2)*7.0+sqrt(time) )/20.0);
            
            
            pz = pz +sin(vPosition.x*5.0+time)/20.0 + sin(vPosition.x*10.0+time)/50.0;
            
            
            pz = pz +sin(vPosition.y*6.0+time)/22.0 + sin(vPosition.y*11.0+time)/62.0;
            
            
           // pz = sin(vPosition.x*5.0+time)/20.0 ;
            
            gl_Position = uMVPMatrix * vec4(vPosition.x,vPosition.y, 0 ,1.0);
            
            vTexCoord = textureCood + textureCoodAdd;
            
            vpos = vec4(vPosition.x,vPosition.y, pz ,1.0);
            }