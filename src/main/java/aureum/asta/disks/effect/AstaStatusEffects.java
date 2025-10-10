package aureum.asta.disks.effect;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.pickyourpoison.effect.EmptyStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class AstaStatusEffects {

    public static final StatusEffect BLEED = registerStatusEffect("bleed", new EmptyStatusEffect(StatusEffectCategory.HARMFUL, 0x821717));

    private static <T extends StatusEffect> T registerStatusEffect(String name, T effect) {
        Registry.register(Registries.STATUS_EFFECT, AureumAstaDisks.id(name), effect);
        return effect;
    }

    public static void init() {}
}
