package aureum.asta.disks.ports.elysium.armour;

import aureum.asta.disks.ports.elysium.machine.prism.EntityInPrismBeamCallback;
import java.util.OptionalDouble;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

public class ReceptiveEnchantment extends Enchantment implements EntityInPrismBeamCallback {
   protected ReceptiveEnchantment() {
      super(Rarity.RARE, EnchantmentTarget.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD});
      EntityInPrismBeamCallback.EVENT.register(this);
   }

   @Override
   public OptionalDouble entityInBeam(Entity entity, int power) {
      if (entity instanceof LivingEntity living && EnchantmentHelper.getEquipmentLevel(this, living) > 0) {
         ElysiumArmourComponent component = (ElysiumArmourComponent)living.getComponent(ElysiumArmourComponent.KEY);
         if (!component.shouldDischargeAfterTakingDamage() && (double)component.getCharge() <= (double)component.getMaxCharge() * 4.0) {
            component.addCharge(0.5F);
         }

         return OptionalDouble.of(0.0);
      }

      return OptionalDouble.empty();
   }
}
