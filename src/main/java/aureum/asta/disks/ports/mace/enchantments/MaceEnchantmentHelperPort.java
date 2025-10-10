package aureum.asta.disks.ports.mace.enchantments;


import aureum.asta.disks.ports.mace.EnchantmentSmashDamageInterface;
import aureum.asta.disks.ports.mace.FaithfulMace;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableFloat;

public class MaceEnchantmentHelperPort {
   public MaceEnchantmentHelperPort() {
   }

   public static void forEachEnchantAfterPostHitMaceForWindBurst(ItemStack stack, LivingEntity userAndAttacker, float fallDist) {
      if (userAndAttacker instanceof PlayerEntity && !stack.isEmpty()) {
         NbtList nbtList = stack.getEnchantments();

         for(int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(nbtCompound)).ifPresent((enchantment) -> {
               if (enchantment instanceof WindBurstEnchantment) {
                  ((WindBurstEnchantment)enchantment).onTargetDamagedCustomLocation(userAndAttacker, EnchantmentHelper.getLevelFromNbt(nbtCompound), fallDist);
               }

            });
         }
      }

   }

   public static float damageUtilDotGetDamageLeft(LivingEntity armorWearer, float damageAmount, DamageSource damageSource, float armor, float armorToughness) {
      float i;
      label16: {
         float f = 2.0F + armorToughness / 4.0F;
         float g = MathHelper.clamp(armor - damageAmount / f, armor * 0.2F, 20.0F);
         float h = g / 25.0F;
         Entity attacker = damageSource.getAttacker();
         ItemStack itemStack = attacker instanceof LivingEntity ? ((LivingEntity)attacker).getMainHandStack() : null;
         if (itemStack != null) {
            World var12 = armorWearer.getWorld();
            if (var12 instanceof ServerWorld) {
               ServerWorld serverWorld = (ServerWorld)var12;
               i = MathHelper.clamp(getArmorEffectiveness(serverWorld, itemStack, armorWearer, damageSource, h), 0.0F, 1.0F);
               break label16;
            }
         }

         i = h;
      }

      float j = 1.0F - i;
      return damageAmount * j;
   }

   public static float getArmorEffectiveness(ServerWorld world, ItemStack stack, Entity user, DamageSource damageSource, float baseArmorEffectiveness) {
      try {
         MutableFloat mutableFloat = new MutableFloat(baseArmorEffectiveness);
         forEachEnchantment((ItemStack)stack, (Consumer)((enchantment, level) -> {
            if (enchantment instanceof EnchantmentSmashDamageInterface) {
               enchantment.modifyArmorEffectiveness(world, level, stack, user, damageSource, mutableFloat);
            }

         }));
         if (FaithfulMace.superfluousLogging()) {
            FaithfulMace.MOGGER.info(mutableFloat.floatValue() + " effectiveness diff");
         }

         return mutableFloat.floatValue();
      } catch (Exception e) {
         FaithfulMace.MOGGER.error("Exception caught while calculating armor effectiveness with stack {} user {} baseValue {} enchantments {}: ", new Object[]{stack, user, baseArmorEffectiveness, stack.getEnchantments(), e});
         return baseArmorEffectiveness;
      }
   }

   public static float getSmashDamagePerFallenBlock(ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource, float baseSmashDamagePerFallenBlock) {
      try {
         MutableFloat mutableFloat = new MutableFloat(baseSmashDamagePerFallenBlock);
         forEachEnchantment((Consumer)((enchantment, level) -> {
            if (enchantment instanceof EnchantmentSmashDamageInterface) {
               enchantment.modifySmashDamagePerFallenBlock(world, level, stack, target, damageSource, mutableFloat);
            }

         }), (ItemStack)stack);
         return mutableFloat.floatValue();
      } catch (Exception e) {
         FaithfulMace.MOGGER.error("Exception caught while calculating extra base damage with stack {} target {} baseValue {} enchantments {}: ", new Object[]{stack, target, baseSmashDamagePerFallenBlock, stack.getEnchantments(), e});
         return baseSmashDamagePerFallenBlock;
      }
   }

   private static void forEachEnchantment(ItemStack stack, Consumer consumer) {
      forEachEnchantment(consumer, stack);
   }

   public static void forEachEnchantment(Consumer consumer, ItemStack stack) {
      if (!stack.isEmpty()) {
         NbtList nbtList = stack.getEnchantments();

         for(int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(nbtCompound)).ifPresent((enchantment) -> consumer.accept(enchantment, EnchantmentHelper.getLevelFromNbt(nbtCompound)));
         }
      }

   }

   @FunctionalInterface
   public interface Consumer {
      void accept(Enchantment var1, int var2);
   }
}
