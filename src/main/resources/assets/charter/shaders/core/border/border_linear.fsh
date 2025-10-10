#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform vec4 BackgroundColor;
uniform vec4 ForegroundColor;
uniform float Size;
uniform float Time;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

float calculateEffectPosition(float time, float quadSize, float width) {
    return (quadSize) * time;
}

float calculateEffectWidth(float baseWidth, float quadSize) {
    return baseWidth*0.5 / quadSize;
}

float calculateLinearScore(vec2 uv, float center, float effectWidth, float quadSize)
{
    float normalizedY = uv.y / quadSize;
    float normalizedCenter = ((center) / quadSize);
    float dy = abs(normalizedY - normalizedCenter) / effectWidth;
    return 1.0 - min(dy, 1.0);
}

float calculateLinearScores(vec2 uv, float center, float borderWidth, float quadSize)
{
    float score1 = calculateLinearScore(uv, center, borderWidth, quadSize);
    float score2 = calculateLinearScore(uv, center + 0.5 * quadSize, borderWidth, quadSize);
    float score3 = calculateLinearScore(uv, center + 1 * quadSize, borderWidth, quadSize);

    return (score1+score2+score3);
}

float calculateSquareScore(vec2 uv, float squareSize, float borderWidth, float quadSize)
{
    // Center the UV coordinates (0,0 is now at the center of the quad)
    vec2 centeredUV = uv - vec2(quadSize / 2.0);

    // Normalize to make calculations easier (squareSize becomes the half-size)
    centeredUV = centeredUV / (quadSize / 2.0);

    // Calculate distance from center (normalized to 0-1 where 1.0 = edge of square)
    vec2 distFromCenter = abs(centeredUV);
    float maxDist = max(distFromCenter.x, distFromCenter.y);

    // Calculate where the square edge should be (normalized)
    float squareEdge = squareSize / quadSize;

    // Calculate distance from the square edge
    float edgeDistance = maxDist / squareEdge;

    // Calculate gradient that peaks at the edge and falls off both sides
    float gradient = 1.0 - abs(edgeDistance - 1.0) / (borderWidth / squareEdge);

    // Apply smoothstep for a nicer transition
    return smoothstep(0.0, 1.0, gradient);
}

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    if (color.a < 0.1) {
        discard;
    }

    float width = Size*0.28;
    float effectDuration = 1.0; // Duration of one complete effect cycle

    int amount = 5;

    float spawnInterval = effectDuration / amount; // Time between spawns

    float totalScore = 0.0;

    // Create a continuous sequence of lines
    for (int i = 0; i < amount; i++) {
        // Calculate time with wrapping for continuous effect
        float wrappedTime = mod(Time + float(i) * spawnInterval, effectDuration);

        float actualTime = (1.0 - wrappedTime) * 2.0;
        float normalizedWidth = calculateEffectWidth(width, Size);

        float linearTime = max(0.0, actualTime);
        float animatedCenter = calculateEffectPosition(linearTime, Size, normalizedWidth);
        float score = calculateLinearScore(texCoord0, animatedCenter - Size * 0.5, normalizedWidth, Size);

        // Add falloff for subsequent lines
        float lineFalloff = 1.0 - (float(i) * 0.25);
        totalScore = max(totalScore, score);
    }

    vec4 finalColor = mix(BackgroundColor, ForegroundColor, totalScore);

    fragColor = vec4(finalColor * color.rgba);
}