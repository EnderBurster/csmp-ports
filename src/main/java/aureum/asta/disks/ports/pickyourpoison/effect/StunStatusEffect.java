package aureum.asta.disks.ports.pickyourpoison.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class StunStatusEffect extends StatusEffect {
   public StunStatusEffect() {
      super(StatusEffectCategory.HARMFUL, 16765472);
   }

   public boolean canApplyUpdateEffect(int duration, int amplifier) {
      return true;
   }

   public void applyUpdateEffect(LivingEntity entity, int amplifier) {
      super.applyUpdateEffect(entity, amplifier);
   }

   public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
      super.onRemoved(entity, attributes, amplifier);
   }
}
