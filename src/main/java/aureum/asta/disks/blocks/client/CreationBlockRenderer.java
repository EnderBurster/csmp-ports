package aureum.asta.disks.blocks.client;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect;
import aureum.asta.disks.blocks.CreationBlockEntity;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteParticles;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

@Environment(EnvType.CLIENT)
public class CreationBlockRenderer implements BlockEntityRenderer<CreationBlockEntity> {

    public CreationBlockRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(CreationBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        List<ItemStack> items = entity.getItems();
        long time = System.currentTimeMillis();
        double rotation = (time % 8000L) / 8000.0 * 360.0;

        ItemStack stack = entity.getItems().get(0);

        if (!stack.isEmpty()) {
            matrices.push();
            matrices.translate(0.5, 1.2, 0.5);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) rotation));

            int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());

            MinecraftClient.getInstance().getItemRenderer().renderItem(
                    stack, ModelTransformationMode.GROUND,
                    lightAbove, overlay, matrices, vertexConsumers,
                    entity.getWorld(), 0
            );

            matrices.pop();
        }

        if(entity.isCrafting())
        {
            for(int i = 0; i < 4; i++)
            {
                this.spawnAmpParticles(entity.getWorld(), 90*i, entity);
            }
        }
    }

    private void spawnAmpParticles(World world, float AngleXZ, CreationBlockEntity entity)
    {
        double angleXZ = Math.toRadians(AngleXZ);
        double slope = Math.toRadians(15);

        for (int i = 0; i < 4; i++) {
            double dist = (double)world.getRandom().nextFloat() * 3;
            Vec3d direction = new Vec3d(Math.cos(angleXZ) * Math.cos(slope), -Math.sin(slope), Math.sin(angleXZ) * Math.cos(slope)).normalize();

            Vec3d point = entity.getPos().toCenterPos().add(direction.multiply(dist)).add(0, 0.8, 0);
            Vec3d motion = direction.multiply(-dist / 36.0);
            ParticleBuilders.create(AmariteParticles.ACCUMULATION)
                    .overrideAnimator(SimpleParticleEffect.Animator.WITH_AGE)
                    .setLifetime(36)
                    .setAlpha(1.0F, 0.0F)
                    .setAlphaEasing(Easing.CUBIC_IN)
                    .setColorCoefficient(0.8F)
                    .setColorEasing(Easing.QUAD_IN_OUT)
                    .setSpinEasing(Easing.SINE_IN)
                    .setColor(0.145f, 0.098f, 0.859f, 0f, 0.871f, 1.0F)
                    .setScale((float)(dist / 16.0), 0.12F)
                    .setSpinOffset((float)world.getRandom().nextInt(360))
                    .setSpin(world.getRandom().nextBoolean() ? 0.5F : -0.5F)
                    .setMotion(motion.x, motion.y, motion.z)
                    .spawn(world, point.x, point.y, point.z);
        }
    }
}