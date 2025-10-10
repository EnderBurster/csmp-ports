package aureum.asta.disks.ports.charter.client.render;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.charter.common.entity.EpitaphShockwaveEntity;
import aureum.asta.disks.ports.charter.common.entity.LesserDivinityEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class EpitaphShockwaveRenderer extends EntityRenderer<EpitaphShockwaveEntity> {
    public static final Identifier TEXTURE = new Identifier("charter", "textures/entity/epitaph_shockwave.png");

    public EpitaphShockwaveRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.0F;
        this.shadowOpacity = 0.0F;
    }

    @Override
    public void render(EpitaphShockwaveEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);

        if(entity.age >= EpitaphShockwaveEntity.ACTIVATION_AGE)
        {
            renderShockwave(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        }
    }

    private void renderShockwave(EpitaphShockwaveEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
    {
        matrices.push();
        float scale = this.getAnimatedScale(entity, tickDelta);
        matrices.scale(scale, scale, scale);

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(TEXTURE));

        Matrix3f normalMatrix = matrices.peek().getNormalMatrix();

        int animatedAlpha = this.getAnimatedAlpha(entity, tickDelta);

        for (int i = 0; i < 3; i++)
        {
            if(i < 2)
            {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90 * i));
                Matrix4f matrix = matrices.peek().getPositionMatrix();
                buffer.vertex(matrix, -0.5F, -0.5F, 0.0F).color(255, 255, 255, animatedAlpha).texture(0.0F, 1.0F).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
                buffer.vertex(matrix,  0.5F, -0.5F, 0.0F).color(255, 255, 255, animatedAlpha).texture(1.0F, 1.0F).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
                buffer.vertex(matrix,  0.5F,  0.5F, 0.0F).color(255, 255, 255, animatedAlpha).texture(1.0F, 0.0F).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
                buffer.vertex(matrix, -0.5F,  0.5F, 0.0F).color(255, 255, 255, animatedAlpha).texture(0.0F, 0.0F).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
            }
            else
            {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
                Matrix4f matrix = matrices.peek().getPositionMatrix();
                buffer.vertex(matrix, -0.5F, -0.5F, 0.0F).color(255, 255, 255, animatedAlpha).texture(0.0F, 1.0F).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 1.0F, 0.0F, 0.0F).next();
                buffer.vertex(matrix,  0.5F, -0.5F, 0.0F).color(255, 255, 255, animatedAlpha).texture(1.0F, 1.0F).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 1.0F, 0.0F, 0.0F).next();
                buffer.vertex(matrix,  0.5F,  0.5F, 0.0F).color(255, 255, 255, animatedAlpha).texture(1.0F, 0.0F).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 1.0F, 0.0F, 0.0F).next();
                buffer.vertex(matrix, -0.5F,  0.5F, 0.0F).color(255, 255, 255, animatedAlpha).texture(0.0F, 0.0F).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 1.0F, 0.0F, 0.0F).next();
            }
        }

        /*Matrix4f matrix = matrices.peek().getPositionMatrix();
        buffer.vertex(matrix, -0.5F, -0.5F, 0.0F).color(255, 255, 255, animatedAlpha).texture(0.0F, 1.0F).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
        buffer.vertex(matrix,  0.5F, -0.5F, 0.0F).color(255, 255, 255, animatedAlpha).texture(1.0F, 1.0F).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
        buffer.vertex(matrix,  0.5F,  0.5F, 0.0F).color(255, 255, 255, animatedAlpha).texture(1.0F, 0.0F).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
        buffer.vertex(matrix, -0.5F,  0.5F, 0.0F).color(255, 255, 255, animatedAlpha).texture(0.0F, 0.0F).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();*/

        matrices.pop();
    }

    private float getAnimatedScale(EpitaphShockwaveEntity entity, float tickDelta)
    {
        float time = ((entity.age - EpitaphShockwaveEntity.ACTIVATION_AGE) + tickDelta) / 20.0F;
        return time * 70.0f;
    }

    private int getAnimatedAlpha(EpitaphShockwaveEntity entity, float tickDelta)
    {
        float time = ((entity.age - EpitaphShockwaveEntity.ACTIVATION_AGE) + tickDelta) / 20.0F;
        float maxAge = ((EpitaphShockwaveEntity.MAX_AGE - EpitaphShockwaveEntity.ACTIVATION_AGE)) / 20.0F;
        return (int) (255 * (1 - (time/maxAge)));
    }

    @Override
    public Identifier getTexture(EpitaphShockwaveEntity entity) {
        return null;
    }

    @Override
    public boolean shouldRender(EpitaphShockwaveEntity entity, Frustum frustum, double x, double y, double z) {
        Box beamBox = entity.getBoundingBox().expand(100, 100, 100);
        return frustum.isVisible(beamBox) || super.shouldRender(entity, frustum, x, y, z);
    }
}
