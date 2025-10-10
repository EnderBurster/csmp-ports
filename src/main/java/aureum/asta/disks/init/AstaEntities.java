package aureum.asta.disks.init;

import aureum.asta.disks.entity.grimoire.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public interface AstaEntities {
    Map<EntityType<? extends Entity>, Identifier> ENTITIES = new LinkedHashMap<>();

    EntityType<PageEntity> GRIMOIRE_PAGE = createEntity(
            "grimoire_page",
            FabricEntityTypeBuilder.<PageEntity>create(SpawnGroup.MISC, PageEntity::new).disableSaving().dimensions(EntityDimensions.changing(1F, 0.5F)).build()
    );

    EntityType<VortexProjectileEntity> GRIMOIRE_VORTEX_PROJECTILE = createEntity(
            "grimoire_vortex_projectile",
            FabricEntityTypeBuilder.<VortexProjectileEntity>create(SpawnGroup.MISC, VortexProjectileEntity::new).disableSaving().dimensions(EntityDimensions.changing(1F, 0.5F)).build()
    );

    EntityType<VortexEntity> GRIMOIRE_VORTEX = createEntity(
            "grimoire_vortex",
            FabricEntityTypeBuilder.<VortexEntity>create(SpawnGroup.MISC, VortexEntity::new).dimensions(EntityDimensions.changing(1F, 1F)).build()
    );

    EntityType<AquabladeEntity> GRIMOIRE_AQUABLADE = createEntity(
            "grimoire_aquablade",
            FabricEntityTypeBuilder.<AquabladeEntity>create(SpawnGroup.MISC, AquabladeEntity::new).disableSaving().dimensions(EntityDimensions.changing(3F, 0.5F)).build()
    );

    EntityType<SharkEntity> GRIMOIRE_SHARK = createEntity("grimoire_shark", FabricEntityTypeBuilder.<SharkEntity>create(SpawnGroup.MISC, SharkEntity::new).dimensions(EntityDimensions.changing(5F, 2F)).build());

    private static <T extends EntityType<? extends Entity>> T createEntity(String name, T entity) {
        ENTITIES.put(entity, new Identifier("aureum-asta-disks", name));
        return entity;
    }

    static void initialize() {
        ENTITIES.keySet().forEach(entityType -> Registry.register(Registries.ENTITY_TYPE, ENTITIES.get(entityType), entityType));
    }
}
