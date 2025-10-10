package aureum.asta.disks.ports.mace.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
   public static final EntityType<WindChargeEntity> WIND_CHARGE;

   public ModEntities() {
   }

   public static void initialize() {
   }

   static {
      WIND_CHARGE = (EntityType) Registry.register(Registries.ENTITY_TYPE, Identifier.of("aureum-asta-disks", "wind_charge"), FabricEntityTypeBuilder.create(SpawnGroup.MISC, WindChargeEntity::new).dimensions(new EntityDimensions(0.3125F, 0.3125F, true)).trackRangeBlocks(4).trackedUpdateRate(10).build());
   }
}
