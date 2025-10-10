package aureum.asta.disks.ports.elysium.armour;

import aureum.asta.disks.ports.elysium.cheirosiphon.CheirosiphonItem;
import aureum.asta.disks.ports.elysium.machine.gravitator.GravitatorBlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class GravitatingEnchantment extends Enchantment implements ElysiumArmourHurtDischargeCallback, ElysiumArmourVulnDischargeCallback {
   private final boolean isPushing;

   protected GravitatingEnchantment(boolean isPushing) {
      super(Rarity.RARE, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
      this.isPushing = isPushing;
      ElysiumArmourHurtDischargeCallback.EVENT.register(this);
      ElysiumArmourVulnDischargeCallback.EVENT.register(this);
   }

   @Override
   public boolean handleHurtDischarge(ElysiumArmourComponent armour, LivingEntity wearer, @Nullable Entity target) {
      if (EnchantmentHelper.getEquipmentLevel(this, wearer) == 0) {
         return false;
      } else {
         double chargeFactor = (double)armour.getCharge() / (double)armour.getMaxCharge();
         if (target == null) {
            GravitatorBlockEntity.pushEntity(new Vec3d(0.0, 4.0 * chargeFactor, 0.0), wearer);
            if (!wearer.world.isClient()) {
               CheirosiphonItem.ClientboundAirblastFxPacket.sendToTracking(wearer, new Vec3d(0.0, -0.4, 0.0), wearer.getEyePos());
            }
         } else {
            Vec3d pushVec = target.getPos()
               .subtract(wearer.getPos())
               .normalize()
               .multiply(4.0 * chargeFactor, 2.5 * chargeFactor, 4.0 * chargeFactor)
               .multiply(this.isPushing ? 1.0 : -1.0);
            GravitatorBlockEntity.pushEntity(pushVec, target);
            if (!wearer.world.isClient()) {
               CheirosiphonItem.ClientboundAirblastFxPacket.sendToTracking(
                  wearer, pushVec.multiply(0.4), this.isPushing ? wearer.getEyePos() : wearer.getEyePos().subtract(pushVec)
               );
            }
         }

         armour.setChargeNoSync(0);
         return true;
      }
   }

   @Override
   public boolean handleVulnDischarge(ElysiumArmourComponent armour, LivingEntity wearer, LivingEntity target) {
      if (EnchantmentHelper.getEquipmentLevel(this, wearer) == 0) {
         return false;
      } else {
         double chargeFactor = (double)armour.getCharge() / (double)armour.getMaxCharge();
         Vec3d pushVec = target.getPos()
            .subtract(wearer.getPos())
            .normalize()
            .multiply(2.0 * chargeFactor, 1.5 * chargeFactor, 2.0 * chargeFactor)
            .multiply(this.isPushing ? 1.0 : -1.0)
            .add(0.0, 0.2, 0.0);
         GravitatorBlockEntity.pushEntity(pushVec, target);
         if (!wearer.world.isClient()) {
            CheirosiphonItem.ClientboundAirblastFxPacket.sendToTracking(
               wearer, pushVec.multiply(0.4), this.isPushing ? wearer.getEyePos() : wearer.getEyePos().subtract(pushVec)
            );
         }

         armour.dischargeVulnDefault(target);
         return true;
      }
   }
}
