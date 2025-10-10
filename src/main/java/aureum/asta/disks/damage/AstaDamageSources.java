package aureum.asta.disks.damage;

import aureum.asta.disks.AureumAstaDisks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class AstaDamageSources {
    public static final RegistryKey<DamageType> BLEED = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, AureumAstaDisks.id("bleed"));

    private final DamageSource bleed;

    public AstaDamageSources(DamageSources damageSources) {
        this.bleed = damageSources.create(BLEED);
    }

    public DamageSource bleed() {
        return bleed;
    }
}
