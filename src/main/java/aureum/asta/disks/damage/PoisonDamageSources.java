package aureum.asta.disks.damage;

import aureum.asta.disks.AureumAstaDisks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class PoisonDamageSources {
    public static final RegistryKey<DamageType> BATRACHOTOXIN = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, AureumAstaDisks.id("batrachotoxin"));
    public static final RegistryKey<DamageType> STIMULATION = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, AureumAstaDisks.id("stimulation"));
    public static final RegistryKey<DamageType> BACKLASH = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, AureumAstaDisks.id("backlash"));

    private final DamageSource backlash;
    private final DamageSource batrachotoxin;
    private final DamageSource stimulation;

    public PoisonDamageSources(DamageSources damageSources) {
        this.backlash = damageSources.create(BACKLASH);
        this.batrachotoxin = damageSources.create(BATRACHOTOXIN);
        this.stimulation = damageSources.create(STIMULATION);
    }

    public DamageSource backlash() {
        return backlash;
    }

    public DamageSource batrachotoxin() {
        return batrachotoxin;
    }

    public DamageSource stimulation() {
        return stimulation;
    }
}
