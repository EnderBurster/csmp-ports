package aureum.asta.disks.ports.mason.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class SoulRipDamageSource extends DamageSource {
    private final Entity source;

    public static final RegistryKey<DamageType> SOUL_RIP_KEY = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("aureum-asta-disks", "soul_rip"));

    public static DamageSource playerRip(PlayerEntity attacker) {
        RegistryEntry<DamageType> damageType = attacker.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(SOUL_RIP_KEY);
        return new SoulRipDamageSource(damageType, attacker);
    }

    public SoulRipDamageSource(RegistryEntry<DamageType> type, Entity source) {
        super(type);
        this.source = source;
    }

    @Override
    public Entity getSource() {
        return this.source;
    }

    @Override
    public boolean isScaledWithDifficulty() {
        return this.source instanceof LivingEntity && !(this.source instanceof PlayerEntity);
    }

    @Override
    @Nullable
    public Vec3d getPosition() {
        return this.source.getPos();
    }

    @Override
    public String toString() {
        return "SoulRipDamageSource (" + this.source + ")";
    }
}
