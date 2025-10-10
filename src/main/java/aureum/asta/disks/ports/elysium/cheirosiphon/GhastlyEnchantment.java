package aureum.asta.disks.ports.elysium.cheirosiphon;

import aureum.asta.disks.ports.elysium.CustomEnchantment;
import aureum.asta.disks.ports.elysium.Elysium;
import aureum.asta.disks.ports.elysium.ElysiumSounds;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;

public class GhastlyEnchantment extends Enchantment implements CustomEnchantment, CheirosiphonAirblastCallback {
   public GhastlyEnchantment() {
      super(Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
      CheirosiphonAirblastCallback.EVENT.register(this);
   }

   public boolean isAcceptableItem(ItemStack stack) {
      return this.customCanEnchant(stack);
   }

   @Override
   public boolean customCanEnchant(ItemStack stack) {
      return stack.isOf(Elysium.CHEIROSIPHON);
   }

   @Override
   public boolean handleAirblast(LivingEntity user, ItemStack cheirosiphon) {
      if (EnchantmentHelper.getLevel(this, cheirosiphon) > 0) {
         Vec3d direction = user.getRotationVec(1.0F).normalize();
         GhastlyFireball ball = new GhastlyFireball(user.world, user, direction.getX(), direction.getY(), direction.getZ(), 1);
         ball.setPosition(user.getX() + direction.getX(), user.getEyeY() + direction.getY(), user.getZ() + direction.getZ());
         ball.powerX = direction.getX() * 0.2;
         ball.powerY = direction.getY() * 0.2;
         ball.powerZ = direction.getZ() * 0.2;
         user.world.spawnEntity(ball);
         if (user instanceof PlayerEntity player) {
            player.getItemCooldownManager().set(cheirosiphon.getItem(), 30);
         }

         user.world.playSound(null, user.getX(), user.getY(), user.getZ(), ElysiumSounds.CHEIROSIPHON_GHASTLY_BLAST, SoundCategory.NEUTRAL, 1.0F, 1.0F);
         return true;
      } else {
         return false;
      }
   }
}
