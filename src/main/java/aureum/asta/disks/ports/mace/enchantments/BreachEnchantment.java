package aureum.asta.disks.ports.mace.enchantments;

import aureum.asta.disks.ports.mace.FaithfulMace;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.apache.commons.lang3.mutable.MutableFloat;

public class BreachEnchantment extends Enchantment {
   protected BreachEnchantment() {
      super(Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
   }

   public void modifyArmorEffectiveness(ServerWorld world, int level, ItemStack stack, Entity user, DamageSource damageSource, MutableFloat armorEffectiveness) {
      if (FaithfulMace.superfluousLogging()) {
         FaithfulMace.MOGGER.info("modifying {}", -0.15 * (double)level);
      }

      armorEffectiveness.add(-0.15 * (double)level);
   }

   public boolean isAcceptableItem(ItemStack stack) {
      return false;
   }

   public int getMinPower(int level) {
      return 1;
   }

   public int getMaxLevel() {
      return 4;
   }

   public boolean canAccept(Enchantment other) {
      return !(other instanceof DensityEnchantment) && super.canAccept(other);
   }
}

