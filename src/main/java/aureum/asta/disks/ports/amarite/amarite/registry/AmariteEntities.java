package aureum.asta.disks.ports.amarite.amarite.registry;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.amarite.amarite.client.MalignancyEntityRenderer;
import aureum.asta.disks.ports.amarite.amarite.client.PylonEntityRenderer;
import aureum.asta.disks.ports.amarite.amarite.entities.MalignancyEntity;
import aureum.asta.disks.ports.amarite.amarite.entities.PylonEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.effect.StatusEffectCategory;
import aureum.asta.disks.ports.amarite.amarite.client.DiscEntityRenderer;
import aureum.asta.disks.ports.amarite.amarite.effects.BuddingStatusEffect;
import aureum.asta.disks.ports.amarite.amarite.entities.DiscEntity;

public interface AmariteEntities {
   EntityType<DiscEntity> DISC = AureumAstaDisks.REGISTRY
      .register(
         "disc",
         FabricEntityTypeBuilder.create(SpawnGroup.MISC, DiscEntity::new)
            .dimensions(EntityDimensions.fixed(0.8F, 0.8F))
            .trackRangeChunks(6)
            .disableSaving()
            .trackedUpdateRate(1)
            .build()
      );
   EntityType<PylonEntity> PYLON = AureumAstaDisks.REGISTRY
           .register(
                   "pylon",
                   FabricEntityTypeBuilder.create(SpawnGroup.MISC, PylonEntity::new)
                           .dimensions(EntityDimensions.fixed(0.5F, 1.0F))
                           .trackRangeChunks(6)
                           .disableSaving()
                           .trackedUpdateRate(1)
                           .build()
           );
   EntityType<MalignancyEntity> MALIGNANCY = AureumAstaDisks.REGISTRY
           .register(
                   "malignancy",
                   FabricEntityTypeBuilder.create(SpawnGroup.MISC, MalignancyEntity::new)
                           .dimensions(EntityDimensions.fixed(0.2F, 1.2F))
                           .trackRangeChunks(6)
                           .disableSaving()
                           .trackedUpdateRate(1)
                           .build()
           );
   StatusEffect BUDDING = AureumAstaDisks.REGISTRY.register("budding", new BuddingStatusEffect(StatusEffectCategory.HARMFUL, 11145642));

   static void init() {
   }

   @Environment(EnvType.CLIENT)
   static void initRenderers() {
      EntityRendererRegistry.register(DISC, DiscEntityRenderer::new);
      EntityRendererRegistry.register(PYLON, PylonEntityRenderer::new);
      EntityRendererRegistry.register(MALIGNANCY, MalignancyEntityRenderer::new);
   }
}
