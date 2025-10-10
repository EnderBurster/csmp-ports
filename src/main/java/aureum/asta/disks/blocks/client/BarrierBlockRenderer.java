package aureum.asta.disks.blocks.client;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.blocks.BarrierBlockEntity;
import aureum.asta.disks.cca.blocks.WaterBarrier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class BarrierBlockRenderer implements BlockEntityRenderer<BarrierBlockEntity> {
    private final BookModel book;

    public BarrierBlockRenderer(BlockEntityRendererFactory.Context ctx) {
        this.book = new BookModel(ctx.getLayerModelPart(EntityModelLayers.BOOK));
    }

    @Override
    public void render(BarrierBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        WaterBarrier barrier = entity.getWorld().getComponent(AureumAstaDisks.KYRATOS).getBarrier(entity.getPos());

        if (barrier == null || !barrier.isActive() || barrier.getActivatingLifetime() > barrier.getMaxActivatingLifetime() - 6) return;

        matrices.push();
        matrices.translate(0.5, 1.25, 0.5);
        //matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternionf(80));
        matrices.scale(2f, 2f, 2f);

        Identifier texture = AureumAstaDisks.id("textures/entity/grimoire_handheld.png");
        int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());

        float g = (float)entity.ticks + tickDelta;
        matrices.translate(0.0F, 0.1F + MathHelper.sin(g * 0.1F) * 0.01F, 0.0F);

        float h;
        for(h = entity.bookRotation - entity.lastBookRotation; h >= (float)Math.PI; h -= ((float)Math.PI * 2F)) {
        }

        while(h < -(float)Math.PI) {
            h += ((float)Math.PI * 2F);
        }

        float k = entity.lastBookRotation + h * tickDelta;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(-k));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(80.0F));
        float l = MathHelper.lerp(tickDelta, entity.pageAngle, entity.nextPageAngle);
        float m = MathHelper.fractionalPart(l + 0.25F) * 1.6F - 0.3F;
        float n = MathHelper.fractionalPart(l + 0.75F) * 1.6F - 0.3F;
        float o = MathHelper.lerp(tickDelta, entity.pageTurningSpeed, entity.nextPageTurningSpeed);
        this.book.setPageAngles(g, MathHelper.clamp(m, 0.0F, 1.0F), MathHelper.clamp(n, 0.0F, 1.0F), o);

        /*
        float time = (float)entity.getWorld().getTime() + tickDelta;
        this.book.setPageAngles(time, 0.0f, 0.0f, 1.0f);*/
        this.book.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(texture)), lightAbove, overlay, 1f, 1f, 1f, 1f);

        matrices.pop();
    }
}