package aureum.asta.disks.ports.charter.common.init;

import aureum.asta.disks.ports.charter.Charter;
import aureum.asta.disks.ports.charter.common.entity.*;
import aureum.asta.disks.ports.charter.common.entity.living.BloodflyEntity;
import aureum.asta.disks.ports.charter.common.item.GoldweaveItemEntity;
import java.util.LinkedHashMap;
import java.util.Map;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.EntityType.Builder;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class CharterEntities {
   private static final Map<Identifier, EntityType<?>> ENTITY_TYPES = new LinkedHashMap<>();
   private static final Map<EntityType<? extends Entity>, Identifier> ENTITIES = new LinkedHashMap<>();

   public static final EntityType<ChainsEntity> CHAINS = createEntity(
           "chains",
           FabricEntityTypeBuilder.<ChainsEntity>create(SpawnGroup.MISC, ChainsEntity::new).dimensions(EntityDimensions.changing(0.95F, 2.0F)).build()
   );

   public static final EntityType<EpitaphChainsEntity> EPITAPH_CHAINS = createEntity(
           "epitaph_chains",
           FabricEntityTypeBuilder.<EpitaphChainsEntity>create(SpawnGroup.MISC, EpitaphChainsEntity::new).dimensions(EntityDimensions.changing(0.95F, 2.0F)).build()
   );

   public static final EntityType<LesserDivinityEntity> LESSER_DIVINITY_ENTITY = createEntity(
           "lesser_divinity_entity",
           FabricEntityTypeBuilder.<LesserDivinityEntity>create(SpawnGroup.MISC, LesserDivinityEntity::new).dimensions(EntityDimensions.changing(1F, 1F)).build()
   );

   public static final EntityType<BrokenDivinityEntity> BROKEN_DIVINITY_ENTITY = createEntity(
           "broken_divinity_entity",
           FabricEntityTypeBuilder.<BrokenDivinityEntity>create(SpawnGroup.MISC, BrokenDivinityEntity::new).dimensions(EntityDimensions.changing(1F, 1F)).build()
   );

   public static final EntityType<EpitaphShockwaveEntity> SHOCKWAVE_ENTITY = createEntity(
           "shockwave_entity",
           FabricEntityTypeBuilder.<EpitaphShockwaveEntity>create(SpawnGroup.MISC, EpitaphShockwaveEntity::new).dimensions(EntityDimensions.changing(1F, 1F)).build()
   );

   public static final EntityType<GoldweaveItemEntity> GOLDWEAVE_ITEM = register(
      "gauntlet_item",
      EntityType.Builder.<GoldweaveItemEntity>create((e, w) -> new GoldweaveItemEntity(w), SpawnGroup.MISC)
         .setDimensions(1.2F, 1.2F)
         .maxTrackingRange(10)
         .build(Charter.id("goldweave_item").toString())
   );

   public static final EntityType<BloodflyEntity> BLOODFLY = createEntity(
           "bloodfly",
           BloodflyEntity.createBloodflyAttributes(),
           FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, BloodflyEntity::new)
                   .dimensions(EntityDimensions.fixed(1.0F, 1.0F))
                   .build()
   );

   public static <T extends Entity> EntityType<T> register(String id, EntityType<T> type) {
      ENTITY_TYPES.put(new Identifier("charter", id), type);
      return type;
   }

   private static <T extends EntityType<? extends Entity>> T createEntity(String name, T entity) {
      ENTITIES.put(entity, new Identifier("charter", name));
      return entity;
   }

   private static <T extends LivingEntity> EntityType<T> createEntity(
      String name, net.minecraft.entity.attribute.DefaultAttributeContainer.Builder attributes, EntityType<T> type
   ) {
      DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(type, attributes.build());
      ENTITY_TYPES.put(new Identifier("charter", name), type);
      return type;
   }

   public static void init() {
      ENTITY_TYPES.forEach((id, entityType) -> Registry.register(Registries.ENTITY_TYPE, id, entityType));
      ENTITIES.keySet().forEach(entityType -> Registry.register(Registries.ENTITY_TYPE, ENTITIES.get(entityType), entityType));
   }
}
