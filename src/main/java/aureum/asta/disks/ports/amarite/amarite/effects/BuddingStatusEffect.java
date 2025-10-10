package aureum.asta.disks.ports.amarite.amarite.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import aureum.asta.disks.ports.amarite.amarite.items.AmariteLongswordItem;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteDamageTypes;

public class BuddingStatusEffect extends StatusEffect {
   public BuddingStatusEffect(StatusEffectCategory statusEffectCategory, int color) {
      super(statusEffectCategory, color);
   }

   public void applyUpdateEffect(LivingEntity entity, int amplifier) {
      if (entity instanceof PlayerEntity player) {
         for (AmariteLongswordItem.LongswordMode mode : AmariteLongswordItem.getModes(player)) {
            mode.absorbDamage(8.0F);
         }
      }

      entity.timeUntilRegen = 0;
      //entity.damage(AmariteDamageTypes.of(entity.world, AmariteDamageTypes.BUDDING), 1.0F);
      entity.damage(entity.getDamageSources().create(AmariteDamageTypes.BUDDING), 1.0F);
      entity.timeUntilRegen = 0;
   }

   public boolean canApplyUpdateEffect(int duration, int amplifier) {
      int i = 40 >> amplifier;
      return i > 0 ? duration % i == 0 : true;
   }
}
