#version 150

// Vertex shader for SDF rounded rectangle
// Passes absolute pixel coordinates (no normalization)

in vec3 Position;
in vec4 Color;

uniform vec2 uScreenSize;    // screen width/height in pixels
uniform float uScale;        // gui scale factor

out vec4 vColor;
out vec2 vPixelCoord;        // absolute pixel position

void main() {
    // Convert normalized position [-1,1] to absolute pixel coordinates
    vec2 pixelPos = (Position.xy * 0.5 + 0.5) * uScreenSize;
    
    vPixelCoord = pixelPos;
    vColor = Color;
    
    gl_Position = vec4(Position, 1.0);
}
