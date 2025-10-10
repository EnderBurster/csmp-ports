package aureum.asta.disks.ports.amarite.mialib.interfaces;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public interface MParticle {
    default void renderCustom(MatrixStack matrices, VertexConsumerProvider vertexConsumer, Camera camera, float tickDelta) {
    }
}
