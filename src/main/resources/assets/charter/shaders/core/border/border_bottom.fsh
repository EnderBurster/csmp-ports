#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float Size;
uniform float Time;
uniform int Reversed;
uniform vec4 BackgroundColor;
uniform vec4 ForegroundColor;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

float calculateEffectPosition(float time, float quadSize) {
    return quadSize * time;
}

float calculateEffectWidth(float baseWidth, float quadSize) {
    return baseWidth / quadSize;
}

float calculateScore(float center, float width)
{
    float actualWidth = Size/width;

    //float dx = (abs(texCoord0.x - center)/(Size/width));
    float dy = (abs(texCoord0.y - center)/(Size/width));

    //float scoreX = 1 - min(dx, 1);
    float scoreY = 1 - min(dy, 1);

    float score = min(scoreY, 1);

    return score;
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

    float width = Size*0.15;
    float effectDuration = 1.0; // Duration of one complete effect cycle

    int amount = 2;

    float spawnInterval = effectDuration / amount; // Time between spawns

    float totalScore = 0.0;

    // Create a continuous sequence of squares
    for (int i = 0; i < amount; i++) {
        // Calculate time with wrapping for continuous effect
        float wrappedTime = mod(Time + float(i) * spawnInterval, effectDuration);

        float actualTime = wrappedTime;

        if(Reversed == 1) {
            actualTime = (1.0 - wrappedTime);
        }

        float animatedSize = (Size + width) - ((Size + width) * actualTime);
        float calculatedSize = calculateEffectPosition(actualTime, Size);
        float normalizedWidth = calculateEffectWidth(width, Size);
        float score = calculateSquareScore(texCoord0, calculatedSize, normalizedWidth, Size);

        float fadeIn = smoothstep(0.0, width, animatedSize);
        score *= fadeIn;

        // Add falloff for subsequent squares
        float squareFalloff = 1.0 - (float(i) * 0.2);
        totalScore = max(totalScore, score);
    }

    //float t = pow(Time, 2.0);
    //vec4 intermediateColor = mix(ForegroundColor, BackgroundColor, t);

    // Center UVs: (0.0, 0.0) is the center
    vec2 centeredUV = texCoord0 - vec2(Size/2, Size/2);

    // Normalize to 0–1 range from center to edge
    vec2 distFromCenter = abs(centeredUV) * 2.0;

    // Square distance = how far you are from center in square terms
    float squareDist = max(distFromCenter.x, distFromCenter.y); // 0 = center, 1 = edge

    // Proximity factor (1 at center, 0 at edges)
    float proximity = 1.0 - smoothstep(0.0, 1.0, squareDist);

    // Optional skew
    float t = pow(proximity, 3.0/8.0);

    // Mix colors
    vec4 intermediateColor = mix(BackgroundColor, ForegroundColor, t);

    vec4 finalColor = mix(vec4(0f,0f,0f,0f), intermediateColor, totalScore);

    fragColor = finalColor * color.rgba;
}