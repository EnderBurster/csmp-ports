package aureum.asta.disks.index;

import aureum.asta.disks.AureumAstaDisks;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public interface ArsenalDamageTypes {
    RegistryKey<DamageType> BLOOD_SCYTHE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, AureumAstaDisks.id("blood_scythe"));
    RegistryKey<DamageType> SPEWING = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, AureumAstaDisks.id("spewing"));
}
