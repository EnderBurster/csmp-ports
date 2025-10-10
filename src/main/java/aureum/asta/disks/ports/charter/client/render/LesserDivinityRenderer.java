package aureum.asta.disks.ports.charter.client.render;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.charter.common.entity.LesserDivinityEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class LesserDivinityRenderer extends EntityRenderer<LesserDivinityEntity> {
    public static final Identifier TEXTURE = new Identifier("charter", "textures/entity/charter.png");
    public static final Identifier BEAM_TEXTURE = new Identifier("charter", "textures/entity/divinity_beam.png");
    private static final float MAX_ANIMATION_LIFE = 20.0f;

    public LesserDivinityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.0F;
        this.shadowOpacity = 0.0F;
    }

    @Override
    public void render(LesserDivinityEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int lightOld) {
        matrices.push();
        matrices.translate(0f, 0.01f, 0f);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));

        float scale = getAnimatedScale(entity, tickDelta);
        matrices.scale(scale, scale, scale);

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(TEXTURE));

        Matrix3f normalMatrix = matrices.peek().getNormalMatrix();

        int animatedAlpha = this.getAnimatedAlpha(entity, tickDelta);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        buffer.vertex(matrix, -0.5F, -0.5F, 0.0F).color(255, 255, 255, animatedAlpha).texture(0.0F, 1.0F).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
        buffer.vertex(matrix,  0.5F, -0.5F, 0.0F).color(255, 255, 255, animatedAlpha).texture(1.0F, 1.0F).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
        buffer.vertex(matrix,  0.5F,  0.5F, 0.0F).color(255, 255, 255, animatedAlpha).texture(1.0F, 0.0F).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
        buffer.vertex(matrix, -0.5F,  0.5F, 0.0F).color(255, 255, 255, animatedAlpha).texture(0.0F, 0.0F).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();

        matrices.pop();

        renderBeam(entity, yaw, tickDelta, matrices, vertexConsumers, lightOld);
    }

    private void renderBeam(LesserDivinityEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int lightOld)
    {
        matrices.push();

        VertexConsumer beamBuffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(BEAM_TEXTURE));

        if(entity.getBroken()) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-45));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45));
        }

        float halfWidth = getAnimatedBeamSize(entity, tickDelta);
        int segmentHeight = 32;
        int segments = 10;

        MinecraftClient mc = MinecraftClient.getInstance();
        Vec3d camPos = mc.gameRenderer.getCamera().getPos();
        double dx = entity.getX() - camPos.x;
        double dz = entity.getZ() - camPos.z;
        float yawToCam = (float) Math.atan2(dz, dx);
        float cos = MathHelper.cos(yawToCam);
        float sin = MathHelper.sin(yawToCam);
        float px = -sin * halfWidth;
        float pz = cos * halfWidth;

        Matrix4f mat = matrices.peek().getPositionMatrix();

        float vOffset = 0.0F;
        for (int i = 0; i < segments; i++) {
            float y0 = i * (segmentHeight);
            float y1 = (i + 1) * segmentHeight;

            float v0 = vOffset;
            float v1 = vOffset + (segmentHeight / 10.0F); // adjust divisor to control texture repeat

            Matrix3f normalMatrix = matrices.peek().getNormalMatrix();

            // Quad strip facing camera yaw
            beamBuffer.vertex(mat,  px, y0,  pz).color(255,247,170,200).texture(0, v0).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 1.0F, 1.0F, 1.0F).next();
            beamBuffer.vertex(mat, -px, y0, -pz).color(255,247,170,200).texture(1, v0).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 1.0F, 1.0F, 1.0F).next();
            beamBuffer.vertex(mat, -px, y1, -pz).color(255,247,170,200).texture(1, v1).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 1.0F, 1.0F, 1.0F).next();
            beamBuffer.vertex(mat,  px, y1,  pz).color(255,247,170,200).texture(0, v1).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 1.0F, 1.0F, 1.0F).next();

            vOffset += (segmentHeight / 4.0F);
        }

        matrices.pop();
    }

    private float getAnimatedScale(LesserDivinityEntity entity, float tickDelta) {
        float time = (entity.age + tickDelta) / 20.0F;
        if (time < 0.5F) {
            return 1.0F + 4.0F * time;
        } else if (time < 1.0F) {
            return 3.0F;
        } else if (time < 1.25F) {
            return 3.0F + 20.0F * (time - 1.0F);
        } else {
            return 8.0F;
        }

        /*float time = (entity.age + tickDelta) / MAX_ANIMATION_LIFE;
        if (time < 1.0F) {
            return 1.0F + 2.0F * time;
        } else if (time < 1.5f) {
            return 3.0F;
        } else if (time < 2.0f) {
            return 3.0F + 5.0F * (time - 2.0f);
        } else {
            return 8.0F;
        }*/
    }

    private int getAnimatedAlpha(LesserDivinityEntity entity, float tickDelta) {
        float time = (entity.age + tickDelta) / 20.0F;

        if (time < 1.0F) {
            return 128;
        } else if (time < 1.25F) {
            return (int)(255 * 2.0f * (time - 0.75F));
        } else {
            return 255;
        }
    }

    private float getAnimatedBeamSize(LesserDivinityEntity entity, float tickDelta) {
        float time = (entity.age + tickDelta) / 20.0F;

        if (time < 1.0F) {
            return 0.0f;
        } else if (time < 1.25F) {
            return 8.0f * (time - 1.0F);
        } else {
            return 2.0f;
        }
    }

    @Override
    public Identifier getTexture(LesserDivinityEntity entity) {
        return TEXTURE;
    }

    @Override
    public boolean shouldRender(LesserDivinityEntity entity, Frustum frustum, double x, double y, double z) {
        Box beamBox = entity.getBoundingBox().expand(0, 500, 0);
        return frustum.isVisible(beamBox) || super.shouldRender(entity, frustum, x, y, z);
    }
}
