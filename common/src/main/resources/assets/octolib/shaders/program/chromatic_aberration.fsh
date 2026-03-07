#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 ScreenSize;
uniform float Strength;

in vec2 texCoord;
out vec4 fragColor;

float inBounds(vec2 uv) {
    return step(0.0, uv.x) * step(0.0, uv.y) * step(uv.x, 1.0) * step(uv.y, 1.0);
}

void main() {
    float FringeBoost = 2.5;

    vec2 center = vec2(0.5, 0.5);
    vec2 dir = texCoord - center;
    float dist = length(dir);
    vec2 nDir = dist > 0.0001 ? dir / dist : vec2(0.0);

    float aberr = Strength * (0.35 + pow(dist, 1.5));
    vec2 offset = nDir * aberr;

    vec2 rUV = texCoord + offset;
    vec2 gUV = texCoord;
    vec2 bUV = texCoord - offset;

    vec4 base = texture(DiffuseSampler, texCoord);

    float rMask = inBounds(rUV);
    float bMask = inBounds(bUV);

    float rSample = texture(DiffuseSampler, clamp(rUV, 0.0, 1.0)).r;
    float gSample = texture(DiffuseSampler, gUV).g;
    float bSample = texture(DiffuseSampler, clamp(bUV, 0.0, 1.0)).b;

    float r = mix(base.r, rSample, rMask);
    float g = gSample;
    float b = mix(base.b, bSample, bMask);

    vec4 aberrColor = vec4(r, g, b, base.a);
    vec4 fringe = aberrColor - base;
    fringe.rgb *= FringeBoost;

    float mask = smoothstep(0.0, 1.0, dist);
    vec4 result = base + fringe * mask;

    fragColor = clamp(result, 0.0, 1.0);
}