package aureum.asta.disks.index;

import aureum.asta.disks.entity.BloodScytheEntity;
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

public interface ArsenalEntities
{
    Map<EntityType<? extends Entity>, Identifier> ENTITIES = new LinkedHashMap<>();
    EntityType<BloodScytheEntity> BLOOD_SCYTHE = createEntity(
            "blood_scythe",
            FabricEntityTypeBuilder.<BloodScytheEntity>create(SpawnGroup.MISC, BloodScytheEntity::new).disableSaving().dimensions(EntityDimensions.changing(5.0F, 0.2F)).build()
    );

    private static <T extends EntityType<? extends Entity>> T createEntity(String name, T entity) {
        ENTITIES.put(entity, new Identifier("aureum-asta-disks", name));
        return entity;
    }

    static void initialize() {
        ENTITIES.keySet().forEach(entityType -> Registry.register(Registries.ENTITY_TYPE, ENTITIES.get(entityType), entityType));
    }
}
