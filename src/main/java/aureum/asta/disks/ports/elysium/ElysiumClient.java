package aureum.asta.disks.ports.elysium;

import aureum.asta.disks.ports.elysium.client.ClientboundPrismLaserPacketClient;
import aureum.asta.disks.ports.elysium.client.ElysiumArmourRenderer;
import aureum.asta.disks.ports.elysium.client.ElysiumPrismRenderer;
import aureum.asta.disks.ports.elysium.client.GlowEffectManager;
import aureum.asta.disks.ports.elysium.client.cheirosiphon.CheirosiphonItemRenderer;
import aureum.asta.disks.ports.elysium.client.cheirosiphon.ClientboundAirblastFxPacketClient;
import aureum.asta.disks.ports.elysium.client.particle.ArcParticle;
import aureum.asta.disks.ports.elysium.client.particle.MagneticParticle;
import aureum.asta.disks.ports.elysium.machine.ElysiumMachines;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.particle.FlameParticle.Factory;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class ElysiumClient implements ClientModInitializer {
   public void onInitializeClient() {
      EntityRendererRegistry.register(Elysium.CHEIROSIPHON_FLAME, EmptyEntityRenderer::new);
      EntityRendererRegistry.register(Elysium.GHASTLY_FIREBALL, c -> new FlyingItemEntityRenderer(c, 3.0F, true));
      BlockEntityRendererFactories.register(ElysiumMachines.ELYSIUM_PRISM_BLOCK_ENTITY, ElysiumPrismRenderer::new);
      ClientboundAirblastFxPacketClient.init();
      ClientboundPrismLaserPacketClient.init();
      BlockRenderLayerMap.INSTANCE.putBlock(Elysium.ELYSIUM_FIRE, RenderLayer.getCutout());
      ParticleFactoryRegistry.getInstance().register(Elysium.ELYSIUM_FLAME_PARTICLE, Factory::new);
      ParticleFactoryRegistry.getInstance().register(Elysium.MAGNETIC_WAVE_PARTICLE, MagneticParticle.Provider::new);
      ParticleFactoryRegistry.getInstance().register(Elysium.MAGNETIC_PULSE_PARTICLE, MagneticParticle.SimpleProvider::new);
      ParticleFactoryRegistry.getInstance().register(Elysium.ARC_PARTICLE, new ArcParticle.Provider());
      GlowEffectManager.INSTANCE.init();
      CheirosiphonItemRenderer.setup(Elysium.CHEIROSIPHON);
   }
}
