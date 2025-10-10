package aureum.asta.disks.blocks.client;

import aureum.asta.disks.blocks.AmpBlockEntity;
import aureum.asta.disks.blocks.CreationBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

import java.util.List;

@Environment(EnvType.CLIENT)
public class PedestalBlockRenderer implements BlockEntityRenderer<AmpBlockEntity> {

    public PedestalBlockRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(AmpBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        List<ItemStack> items = entity.getItems();
        long time = System.currentTimeMillis();
        double rotation = (time % 8000L) / 8000.0 * 360.0;

        int count = 0;
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                matrices.push();

                double angle = Math.toRadians(rotation + (360.0 / items.size()) * count);
                double radius = 0.5;
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;

                matrices.translate(0.5, 0.8, 0.5);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) rotation));

                int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());

                MinecraftClient.getInstance().getItemRenderer().renderItem(
                        stack, ModelTransformationMode.GROUND,
                        lightAbove, overlay, matrices, vertexConsumers,
                        entity.getWorld(), 0
                );

                matrices.pop();
                count++;
            }
        }
    }
}