package aureum.asta.disks.ports.blast.client;

import aureum.asta.disks.ports.blast.client.particle.ConfettiParticle;
import aureum.asta.disks.ports.blast.client.particle.DryIceParticle;
import aureum.asta.disks.ports.blast.client.particle.FollyRedPaintParticle;
import aureum.asta.disks.ports.blast.client.renderers.AmethystShardEntityRenderer;
import aureum.asta.disks.ports.blast.client.renderers.BlastBlockEntityRenderer;
import aureum.asta.disks.ports.blast.client.renderers.IcicleEntityRenderer;
import aureum.asta.disks.ports.blast.common.entity.BombEntity;
import aureum.asta.disks.ports.blast.common.entity.ColdDiggerEntity;
import aureum.asta.disks.ports.blast.common.entity.StripminerEntity;
import aureum.asta.disks.ports.blast.common.init.BlastBlocks;
import aureum.asta.disks.ports.blast.common.init.BlastEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.Function;

import static aureum.asta.disks.ports.blast.common.Blast.FIREWORK_SYNC_PACKET_ID;

@Environment(EnvType.CLIENT)
public class BlastClient implements ClientModInitializer {

    // particle types
    public static DefaultParticleType DRY_ICE;
    public static DefaultParticleType CONFETTI;
    public static DefaultParticleType DRIPPING_FOLLY_RED_PAINT_DROP;
    public static DefaultParticleType FALLING_FOLLY_RED_PAINT_DROP;
    public static DefaultParticleType LANDING_FOLLY_RED_PAINT_DROP;

    public static void registerRenders() {
        registerItemEntityRenders(
                BlastEntities.BOMB,
                BlastEntities.TRIGGER_BOMB,
                BlastEntities.GOLDEN_BOMB,
                BlastEntities.GOLDEN_TRIGGER_BOMB,
                BlastEntities.DIAMOND_BOMB,
                BlastEntities.DIAMOND_TRIGGER_BOMB,
                BlastEntities.NAVAL_MINE,
                BlastEntities.CONFETTI_BOMB,
                BlastEntities.CONFETTI_TRIGGER_BOMB,
                BlastEntities.DIRT_BOMB,
                BlastEntities.DIRT_TRIGGER_BOMB,
                BlastEntities.PEARL_BOMB,
                BlastEntities.PEARL_TRIGGER_BOMB,
                BlastEntities.AMETHYST_BOMB,
                BlastEntities.AMETHYST_TRIGGER_BOMB,
                BlastEntities.FROST_BOMB,
                BlastEntities.FROST_TRIGGER_BOMB,
                BlastEntities.SLIME_BOMB,
                BlastEntities.SLIME_TRIGGER_BOMB,
                BlastEntities.PIPE_BOMB
        );
        registerBlockEntityRender(BlastEntities.GUNPOWDER_BLOCK, e -> BlastBlocks.GUNPOWDER_BLOCK.getDefaultState());
        registerBlockEntityRender(BlastEntities.STRIPMINER, StripminerEntity::getState);
        registerBlockEntityRender(BlastEntities.COLD_DIGGER, ColdDiggerEntity::getState);
        registerBlockEntityRender(BlastEntities.BONESBURRIER, e -> BlastBlocks.BONESBURRIER.getDefaultState());

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                BlastBlocks.GUNPOWDER_BLOCK, BlastBlocks.COLD_DIGGER,
                BlastBlocks.STRIPMINER, BlastBlocks.BONESBURRIER,
                BlastBlocks.REMOTE_DETONATOR
        );
        BlockRenderLayerMap.INSTANCE.putBlock(BlastBlocks.DRY_ICE, RenderLayer.getTranslucent());

        EntityRendererRegistry.register(BlastEntities.AMETHYST_SHARD, AmethystShardEntityRenderer::new);
        EntityRendererRegistry.register(BlastEntities.ICICLE, IcicleEntityRenderer::new);
    }

    @SafeVarargs
    private static void registerItemEntityRenders(EntityType<? extends FlyingItemEntity>... entityTypes) {
        for (EntityType<? extends FlyingItemEntity> entityType : entityTypes) {
            registerItemEntityRender(entityType);
        }
    }

    private static <T extends Entity & FlyingItemEntity> void registerItemEntityRender(EntityType<T> entityType) {
        EntityRendererRegistry.register(entityType, FlyingItemEntityRenderer::new);
    }

    private static <T extends BombEntity> void registerBlockEntityRender(EntityType<T> block, Function<T, BlockState> stateGetter) {
        EntityRendererRegistry.register(block, ctx -> new BlastBlockEntityRenderer<>(ctx, stateGetter));
    }

    @Override
    public void onInitializeClient() {
        registerRenders();

        // particles
        DRY_ICE = Registry.register(Registries.PARTICLE_TYPE, "aureum-asta-disks:dry_ice", FabricParticleTypes.simple(true));
        ParticleFactoryRegistry.getInstance().register(DRY_ICE, DryIceParticle.DefaultFactory::new);
        CONFETTI = Registry.register(Registries.PARTICLE_TYPE, "aureum-asta-disks:confetti", FabricParticleTypes.simple(true));
        ParticleFactoryRegistry.getInstance().register(CONFETTI, ConfettiParticle.DefaultFactory::new);

        DRIPPING_FOLLY_RED_PAINT_DROP = Registry.register(Registries.PARTICLE_TYPE, "aureum-asta-disks:dripping_folly_red_paint_drop", FabricParticleTypes.simple(true));
        ParticleFactoryRegistry.getInstance().register(DRIPPING_FOLLY_RED_PAINT_DROP, FollyRedPaintParticle.DrippingFollyRedPaintDropFactory::new);
        FALLING_FOLLY_RED_PAINT_DROP = Registry.register(Registries.PARTICLE_TYPE, "aureum-asta-disks:falling_folly_red_paint_drop", FabricParticleTypes.simple(true));
        ParticleFactoryRegistry.getInstance().register(FALLING_FOLLY_RED_PAINT_DROP, FollyRedPaintParticle.FallingFollyRedPaintDropFactory::new);
        LANDING_FOLLY_RED_PAINT_DROP = Registry.register(Registries.PARTICLE_TYPE, "aureum-asta-disks:landing_folly_red_paint_drop", FabricParticleTypes.simple(true));
        ParticleFactoryRegistry.getInstance().register(LANDING_FOLLY_RED_PAINT_DROP, FollyRedPaintParticle.LandingFollyRedPaintDropFactory::new);

        ClientPlayNetworking.registerGlobalReceiver(FIREWORK_SYNC_PACKET_ID, (client, handler, buf, responseSender) -> {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            double vx = buf.readDouble();
            double vy = buf.readDouble();
            double vz = buf.readDouble();
            NbtCompound fireworksNbt = buf.readNbt();

            client.execute(() -> {
                if (client.world != null) {
                    client.world.addFireworkParticle(x, y, z, vx, vy, vz, fireworksNbt);
                }
            });
        });
    }
}
