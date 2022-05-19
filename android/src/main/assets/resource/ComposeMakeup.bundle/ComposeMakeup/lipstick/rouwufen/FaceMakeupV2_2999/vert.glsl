attribute vec2 attPosition;
attribute vec2 attUV;
attribute float attOpacity;
attribute float attOpacitySmooth;

uniform mat4 uMVPMatrix;
uniform mat4 uSTMatrix;
uniform mat4 uSegMatrix;

varying vec2 inputTexCoord;
varying vec2 segTexCoord;
varying vec2 maskTexCoord;
varying float varOpacity;
varying float varOpacitySmooth;

void main() {
    gl_Position = uMVPMatrix * vec4(attPosition.xy, 0.0, 1.0);
    inputTexCoord = gl_Position.xy * 0.5 + 0.5;

    maskTexCoord = (uSTMatrix * vec4(attUV.xy, 0.0, 1.0)).xy;
    segTexCoord = (uSegMatrix * vec4(attPosition.xy, 0.0, 1.0)).xy;
    
    varOpacity = attOpacity;
    varOpacitySmooth = attOpacitySmooth;
}