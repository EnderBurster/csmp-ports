package aureum.asta.disks.ports.amarite.amarite.registry;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public interface AmariteDamageTypes {
   public static final RegistryKey<DamageType> BUDDING = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("aureum-asta-disks", "amarite_budding"));
   public static final RegistryKey<DamageType> DASH = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("aureum-asta-disks", "dash"));
   public static final RegistryKey<DamageType> ACCUMULATE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("aureum-asta-disks", "accumulate"));
   public static final RegistryKey<DamageType> A_DISC = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("aureum-asta-disks", "disc"));

   public static DamageSource of(World world, RegistryKey<DamageType> key) {
      return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
   }

   static void init() {
   }
}
