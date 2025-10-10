package aureum.asta.disks.api.lodestone.setup;

import aureum.asta.disks.api.lodestone.helpers.DataHelper;
import aureum.asta.disks.mixin.ports.lodestone.FabricSpriteProviderImplAccessor;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.ScreenParticleEffect;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.ScreenParticleType;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.type.LodestoneScreenParticleType;
import java.util.ArrayList;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.SpriteProvider;

public class LodestoneScreenParticles {
   public static final ArrayList<ScreenParticleType<?>> PARTICLE_TYPES = new ArrayList<>();
   public static final ScreenParticleType<ScreenParticleEffect> WISP = registerType(new LodestoneScreenParticleType());
   public static final ScreenParticleType<ScreenParticleEffect> SMOKE = registerType(new LodestoneScreenParticleType());
   public static final ScreenParticleType<ScreenParticleEffect> SPARKLE = registerType(new LodestoneScreenParticleType());
   public static final ScreenParticleType<ScreenParticleEffect> TWINKLE = registerType(new LodestoneScreenParticleType());
   public static final ScreenParticleType<ScreenParticleEffect> STAR = registerType(new LodestoneScreenParticleType());

   public static void registerParticleFactories() {
      registerProvider(WISP, new LodestoneScreenParticleType.Factory(getSpriteSet(DataHelper.prefix("wisp"))));
      registerProvider(SMOKE, new LodestoneScreenParticleType.Factory(getSpriteSet(DataHelper.prefix("smoke"))));
      registerProvider(SPARKLE, new LodestoneScreenParticleType.Factory(getSpriteSet(DataHelper.prefix("sparkle"))));
      registerProvider(TWINKLE, new LodestoneScreenParticleType.Factory(getSpriteSet(DataHelper.prefix("twinkle"))));
      registerProvider(STAR, new LodestoneScreenParticleType.Factory(getSpriteSet(DataHelper.prefix("star"))));
   }

   public static <T extends ScreenParticleEffect> ScreenParticleType<T> registerType(ScreenParticleType<T> type) {
      PARTICLE_TYPES.add(type);
      return type;
   }

   public static <T extends ScreenParticleEffect> void registerProvider(ScreenParticleType<T> type, ScreenParticleType.Factory<T> provider) {
      type.factory = provider;
   }

   public static SpriteProvider getSpriteSet(Identifier resourceLocation) {
      MinecraftClient client = MinecraftClient.getInstance();
      return FabricSpriteProviderImplAccessor.FabricSpriteProviderImpl(client.particleManager, (SpriteProvider)client.particleManager.spriteAwareFactories.get(resourceLocation));
   }
}
