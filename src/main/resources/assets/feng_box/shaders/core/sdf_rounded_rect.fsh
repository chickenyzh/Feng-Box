#version 150

// Fragment shader for SDF rounded rectangle
// Distance field calculation in absolute pixel units

in vec4 vColor;
in vec2 vPixelCoord;

uniform vec2 uRectPos;       // rectangle top-left corner (pixels)
uniform vec2 uRectSize;      // rectangle width/height (pixels)
uniform float uRadius;       // corner radius (pixels)
uniform float uSmoothness;   // anti-alias transition width (pixels, typically 1.0 ~ 1.5)
uniform float uBorderWidth;  // border width (pixels, 0 = filled)
uniform int uDebug;          // debug mode: 1 = output raw SDF distance

out vec4 fragColor;

// SDF for rounded rectangle (absolute pixel coordinates)
float sdRoundRect(vec2 p, vec2 b, float r) {
    // p: pixel position relative to rectangle center
    // b: half-size of rectangle (width/2, height/2)
    // r: corner radius
    vec2 d = abs(p) - b + vec2(r);
    return length(max(d, 0.0)) + min(max(d.x, d.y), 0.0) - r;
}

void main() {
    // Calculate pixel position relative to rectangle center
    vec2 rectCenter = uRectPos + uRectSize * 0.5;
    vec2 p = vPixelCoord - rectCenter;
    vec2 halfSize = uRectSize * 0.5;
    
    // Compute signed distance
    float dist = sdRoundRect(p, halfSize, uRadius);
    
    // Debug mode: output raw distance as grayscale
    if (uDebug > 0) {
        float debugVal = clamp(dist / 20.0 + 0.5, 0.0, 1.0);
        fragColor = vec4(vec3(debugVal), 1.0);
        return;
    }
    
    // Anti-aliased alpha using smoothstep
    // Transition from fully inside (dist < -smoothness) to fully outside (dist > smoothness)
    float alpha = 1.0 - smoothstep(-uSmoothness, uSmoothness, dist);
    
    // Border mode
    if (uBorderWidth > 0.0) {
        float innerDist = dist + uBorderWidth;
        float innerAlpha = 1.0 - smoothstep(-uSmoothness, uSmoothness, innerDist);
        alpha = alpha - innerAlpha;
    }
    
    // Apply vertex color
    fragColor = vec4(vColor.rgb, vColor.a * alpha);
}
