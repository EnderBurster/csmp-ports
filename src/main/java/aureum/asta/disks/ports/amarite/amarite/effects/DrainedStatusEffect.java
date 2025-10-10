package aureum.asta.disks.ports.amarite.amarite.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import aureum.asta.disks.ports.amarite.amarite.items.AmariteLongswordItem;

public class DrainedStatusEffect extends StatusEffect {
   public DrainedStatusEffect(StatusEffectCategory statusEffectCategory, int color) {
      super(statusEffectCategory, color);
   }

   public void applyUpdateEffect(LivingEntity entity, int amplifier) {
      if (entity instanceof PlayerEntity player) {
         for (AmariteLongswordItem.LongswordMode mode : AmariteLongswordItem.getModes(player)) {
            mode.absorbDamage(8.0F);
         }
      }

      entity.timeUntilRegen = 0;
      entity.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 1, amplifier, true, true, true));
   }

   public boolean canApplyUpdateEffect(int duration, int amplifier) {
      int i = 40 >> amplifier;
      return i > 0 ? duration % i == 0 : true;
   }
}
