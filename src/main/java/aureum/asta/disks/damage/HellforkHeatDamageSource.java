package aureum.asta.disks.damage;

import aureum.asta.disks.ports.impaled.Impaled;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class HellforkHeatDamageSource {
    public static final RegistryKey<DamageType> HELLFORK_HEAT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Impaled.MODID, "hellfork_heat"));

    public static DamageSource of(World world, RegistryKey<DamageType> key)
    {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }

    //public static final DamageSource HELLFORK_HEAT = ((HellforkHeatDamageSource) new HellforkHeatDamageSource("hellfork_heat").setBypassesArmor()).setUnblockable();

    /*protected HellforkHeatDamageSource(String name) {
        super(name);
    }*/
}
