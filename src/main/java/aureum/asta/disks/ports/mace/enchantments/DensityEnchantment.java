package aureum.asta.disks.ports.mace.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.apache.commons.lang3.mutable.MutableFloat;

public class DensityEnchantment extends Enchantment {
   protected DensityEnchantment() {
      super(Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
   }

   public void modifySmashDamagePerFallenBlock(ServerWorld world, int level, ItemStack stack, Entity user, DamageSource damageSource, MutableFloat smashDamagePerFallenBlock) {
      smashDamagePerFallenBlock.add((double)0.5F * (double)level);
   }

   public boolean isAcceptableItem(ItemStack stack) {
      return false;
   }

   public int getMinPower(int level) {
      return 1;
   }

   public int getMaxLevel() {
      return 5;
   }

   public boolean canAccept(Enchantment other) {
      return !(other instanceof BreachEnchantment) && super.canAccept(other);
   }
}
