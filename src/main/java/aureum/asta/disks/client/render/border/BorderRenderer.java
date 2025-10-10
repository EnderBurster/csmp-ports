package aureum.asta.disks.client.render.border;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.init.AstaShaders;
import aureum.asta.disks.ports.charter.Charter;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import ladysnake.satin.impl.ManagedUniform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static net.minecraft.client.render.RenderPhase.MIPMAP_BLOCK_ATLAS_TEXTURE;
import static net.minecraft.client.render.RenderPhase.TRANSLUCENT_TRANSPARENCY;


public class BorderRenderer {
    private static final Identifier FORCEFIELD = AureumAstaDisks.id("textures/misc/border.png");

    public static void drawBaseCube(MatrixStack matrices, float size, Vec3d pos)
    {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(
                GlStateManager.SrcFactor.SRC_ALPHA,
                GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA
        );
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        //RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);

        Supplier<ShaderProgram> linearShader = AstaShaders.BORDER_LINEAR.getInstance();
        Supplier<ShaderProgram> radialShader = AstaShaders.BORDER_RADIAL.getInstance();
        RenderSystem.setShaderTexture(0, FORCEFIELD);

        long time = Util.getMeasuringTimeMs();
        float duration = 16000f;

        duration *= 3;

        float cycle = (time % duration) / duration;

        float textureSize = 40;

        Vector4f foreground = new Vector4f(0.0f, 0.75f, 1.0f, 0.8f);
        Vector4f background = new Vector4f(0.0f, 0.2f, 0.63f, 0.7f);

        ManagedUniform managedUniform = new ManagedUniform("Size");
        managedUniform.findUniformTarget(linearShader.get());
        managedUniform.set(textureSize);

        managedUniform = new ManagedUniform("Time");
        managedUniform.findUniformTarget(linearShader.get());
        managedUniform.set(cycle);

        managedUniform = new ManagedUniform("BackgroundColor");
        managedUniform.findUniformTarget(linearShader.get());
        managedUniform.set(background);

        managedUniform = new ManagedUniform("ForegroundColor");
        managedUniform.findUniformTarget(linearShader.get());
        managedUniform.set(foreground);

        managedUniform = new ManagedUniform("Size");
        managedUniform.findUniformTarget(radialShader.get());
        managedUniform.set(textureSize);

        managedUniform = new ManagedUniform("Time");
        managedUniform.findUniformTarget(radialShader.get());
        managedUniform.set(cycle);

        managedUniform = new ManagedUniform("BackgroundColor");
        managedUniform.findUniformTarget(radialShader.get());
        managedUniform.set(background);

        managedUniform = new ManagedUniform("ForegroundColor");
        managedUniform.findUniformTarget(radialShader.get());
        managedUniform.set(foreground);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);

        float min = -size, max = size+1;

        int boxColor = ColorHelper.Argb.getArgb(255, 255, 255, 255);

        Vec3d cam = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
        record Face(Runnable draw, double dist) {}
        List<Face> faces = new ArrayList<>();

        double mid = (min + max) * 0.5;

        double worldMinX = pos.getX() + min;
        double worldMaxX = pos.getX() + max;
        double worldMidX = pos.getX() + mid;

        double worldMinY = pos.getY() + min;
        double worldMaxY = pos.getY() + max;
        double worldMidY = pos.getY() + mid;

        double worldMinZ = pos.getZ() + min;
        double worldMaxZ = pos.getZ() + max;
        double worldMidZ = pos.getZ() + mid;

        double distNorth = cam.squaredDistanceTo(worldMidX, worldMidY, worldMinZ);
        double distSouth = cam.squaredDistanceTo(worldMidX, worldMidY, worldMaxZ);
        double distWest = cam.squaredDistanceTo(worldMinX, worldMidY, worldMidZ);
        double distEast = cam.squaredDistanceTo(worldMaxX, worldMidY, worldMidZ);
        double distTop = cam.squaredDistanceTo(worldMidX, worldMaxY, worldMidZ);
        double distBottom = cam.squaredDistanceTo(worldMidX, worldMinY, worldMidZ);


        faces.add(new Face(() -> {
            BufferRenderer.drawWithGlobalProgram(buffer.end());
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);

            RenderSystem.setShader(linearShader);
            buffer.vertex(matrix, min, min, min).color(boxColor).texture(0, 0).next();
            buffer.vertex(matrix, max, min, min).color(boxColor).texture(textureSize, 0).next();
            buffer.vertex(matrix, max, max, min).color(boxColor).texture(textureSize, textureSize).next();
            buffer.vertex(matrix, min, max, min).color(boxColor).texture(0, textureSize).next();}, distNorth));

        faces.add(new Face(() -> {
            BufferRenderer.drawWithGlobalProgram(buffer.end());
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
            RenderSystem.setShader(linearShader);
            buffer.vertex(matrix, min, min, max).color(boxColor).texture(0, 0).next();
            buffer.vertex(matrix, max, min, max).color(boxColor).texture(textureSize, 0).next();
            buffer.vertex(matrix, max, max, max).color(boxColor).texture(textureSize, textureSize).next();
            buffer.vertex(matrix, min, max, max).color(boxColor).texture(0, textureSize).next();}, distSouth));

        faces.add(new Face(() -> {
            BufferRenderer.drawWithGlobalProgram(buffer.end());
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
            RenderSystem.setShader(linearShader);
            buffer.vertex(matrix, min, min, min).color(boxColor).texture(0, 0).next();
            buffer.vertex(matrix, min, min, max).color(boxColor).texture(textureSize, 0).next();
            buffer.vertex(matrix, min, max, max).color(boxColor).texture(textureSize, textureSize).next();
            buffer.vertex(matrix, min, max, min).color(boxColor).texture(0, textureSize).next();}, distWest));

        faces.add(new Face(() -> {
            BufferRenderer.drawWithGlobalProgram(buffer.end());
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
            RenderSystem.setShader(linearShader);
            buffer.vertex(matrix, max, min, min).color(boxColor).texture(0, 0).next();
            buffer.vertex(matrix, max, min, max).color(boxColor).texture(textureSize, 0).next();
            buffer.vertex(matrix, max, max, max).color(boxColor).texture(textureSize, textureSize).next();
            buffer.vertex(matrix, max, max, min).color(boxColor).texture(0, textureSize).next();}, distEast));

        faces.add(new Face(() -> {
            BufferRenderer.drawWithGlobalProgram(buffer.end());
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
            RenderSystem.setShader(radialShader);

            ManagedUniform managedUniformT = new ManagedUniform("Reversed");
            managedUniformT.findUniformTarget(radialShader.get());
            managedUniformT.set(0);
            buffer.vertex(matrix, min, max, min).color(boxColor).texture(0, 0).next();
            buffer.vertex(matrix, max, max, min).color(boxColor).texture(textureSize, 0).next();
            buffer.vertex(matrix, max, max, max).color(boxColor).texture(textureSize, textureSize).next();
            buffer.vertex(matrix, min, max, max).color(boxColor).texture(0, textureSize).next();}, distTop));

        faces.add(new Face(() -> {
            BufferRenderer.drawWithGlobalProgram(buffer.end());
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
            RenderSystem.setShader(radialShader);

            ManagedUniform managedUniformB = new ManagedUniform("Reversed");
            managedUniformB.findUniformTarget(radialShader.get());
            managedUniformB.set(1);

            buffer.vertex(matrix, min, min, min).color(boxColor).texture(0, 0).next();
            buffer.vertex(matrix, max, min, min).color(boxColor).texture(textureSize, 0).next();
            buffer.vertex(matrix, max, min, max).color(boxColor).texture(textureSize, textureSize).next();
            buffer.vertex(matrix, min, min, max).color(boxColor).texture(0, textureSize).next();}, distBottom));

        faces.sort((a, b) -> Double.compare(b.dist, a.dist));

        // Render
        for (Face f : faces) {
            f.draw().run();
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);

        drawInnerGlow(matrices, 2, pos);
    }

    public static void drawInnerGlow(MatrixStack matrices, float size, Vec3d pos)
    {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        Supplier<ShaderProgram> bottomShader = AstaShaders.BORDER_BOTTOM.getInstance();

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(
                GlStateManager.SrcFactor.SRC_ALPHA,
                GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA
        );
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.setShaderTexture(0, FORCEFIELD);
        RenderSystem.setShader(bottomShader);

        long time = Util.getMeasuringTimeMs();
        float duration = 8000f;

        float cycle = (time % duration) / duration;

        float min = -size, max = size+1;

        Vector4f background = new Vector4f(0.0f, 0.2f, 0.63f, 0.3f);
        Vector4f foreground = new Vector4f(0.0f, 0.75f, 1.0f, 0.6f);
        int boxColor = ColorHelper.Argb.getArgb(255, 255, 255, 255);

        ManagedUniform managedUniform = new ManagedUniform("Size");
        managedUniform.findUniformTarget(bottomShader.get());
        managedUniform.set(size);

        managedUniform = new ManagedUniform("Time");
        managedUniform.findUniformTarget(bottomShader.get());
        managedUniform.set(cycle);

        managedUniform = new ManagedUniform("BackgroundColor");
        managedUniform.findUniformTarget(bottomShader.get());
        managedUniform.set(background);

        managedUniform = new ManagedUniform("ForegroundColor");
        managedUniform.findUniformTarget(bottomShader.get());
        managedUniform.set(foreground);

        ManagedUniform managedUniformB = new ManagedUniform("Reversed");
        managedUniformB.findUniformTarget(bottomShader.get());
        managedUniformB.set(1);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);

        buffer.vertex(matrix, min, 0.01f, min).color(boxColor).texture(0, 0).next();
        buffer.vertex(matrix, max, 0.01f, min).color(boxColor).texture(size, 0).next();
        buffer.vertex(matrix, max, 0.01f, max).color(boxColor).texture(size, size).next();
        buffer.vertex(matrix, min, 0.01f, max).color(boxColor).texture(0, size).next();

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
    }
}
