package aureum.asta.disks.client.render.grimoire;

import aureum.asta.disks.entity.grimoire.VortexEntity;
import aureum.asta.disks.entity.grimoire.VortexProjectileEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class GrimoireVortexEntityRenderer<T extends VortexEntity> extends EntityRenderer<T> {
    public static final Identifier TEXTURE = new Identifier("aureum-asta-disks", "textures/entity/blood_scythe.png");

    public GrimoireVortexEntityRenderer(EntityRendererFactory.Context context) {super(context);}

    public void render(T page, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light)
    {
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(g, page.prevYaw, page.getYaw()) - 90.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(g, page.prevPitch, page.getPitch())));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45.0F));
        matrixStack.scale(0.4F, 0.4F, 0.4F);
        matrixStack.translate(-4.0, 0.0, 0.0);

        for (int u = 0; u < 4; u++)
        {
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
        }

        matrixStack.pop();
        super.render(page, f, g, matrixStack, vertexConsumerProvider, light);
    }

    public Identifier getTexture(T entity) {
        return TEXTURE;
    }

    public void vertex(
            Matrix4f positionMatrix,
            Matrix3f normalMatrix,
            VertexConsumer vertexConsumer,
            int x,
            int y,
            int z,
            float u,
            float v,
            int normalX,
            int normalZ,
            int normalY,
            int light
    ) {
        vertexConsumer.vertex(positionMatrix, (float)x, (float)y, (float)z)
                .color(255, 255, 255, 255)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(normalMatrix, (float)normalX, (float)normalY, (float)normalZ)
                .next();
    }
}
