package aureum.asta.disks.ports.charter.common.damage;

import aureum.asta.disks.ports.charter.Charter;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class CharterDamageSources {
    public static final RegistryKey<DamageType> EPITAPH = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Charter.id("epitaph"));
    public static final RegistryKey<DamageType> EPITAPH_BAN = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Charter.id("epitaph_ban"));
    public static final RegistryKey<DamageType> LESSER_DIVINITY = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Charter.id("lesser_divinity"));

    private final DamageSource epitaph;
    private final DamageSource epitaph_ban;
    private final DamageSource lesser_divinity;

    public CharterDamageSources(DamageSources damageSources) {
        this.epitaph = damageSources.create(EPITAPH);
        this.epitaph_ban = damageSources.create(EPITAPH_BAN);
        this.lesser_divinity = damageSources.create(LESSER_DIVINITY);
    }

    public DamageSource epitaph() {
        return epitaph;
    }

    public DamageSource epitaph_ban() {
        return epitaph_ban;
    }

    public DamageSource lesser_divinity() {
        return lesser_divinity;
    }
}
