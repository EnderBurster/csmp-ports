package aureum.asta.disks.ports.amarite.amarite.registry;

import aureum.asta.disks.AureumAstaDisks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import aureum.asta.disks.ports.amarite.amarite.items.MaskItem;
import aureum.asta.disks.ports.amarite.mialib.templates.MEnchantment;

public interface AmariteEnchantments {
   Enchantment DOUBLE_DASH = AureumAstaDisks.REGISTRY
      .register(
         "double_dash",
         new MEnchantment(Rarity.RARE, EnchantmentTarget.WEAPON, EquipmentSlot.MAINHAND)
            .setIsAcceptableItem((enchant, bool, stack) -> stack.isOf(AmariteItems.AMARITE_LONGSWORD))
            .setMinPower(1)
            .setMaxPower(100)
            .setCanAccept((enchant, bool, other) -> other != AmariteEnchantments.ACCUMULATE && bool)
      );
   Enchantment ACCUMULATE = AureumAstaDisks.REGISTRY
      .register(
         "accumulate",
         new MEnchantment(Rarity.RARE, EnchantmentTarget.WEAPON, EquipmentSlot.MAINHAND)
            .setIsAcceptableItem((enchant, bool, stack) -> stack.isOf(AmariteItems.AMARITE_LONGSWORD))
            .setMinPower(1)
            .setMaxPower(100)
            .setCanAccept((enchant, bool, other) -> other != DOUBLE_DASH && bool)
      );
   Enchantment MALIGNANCY = AureumAstaDisks.REGISTRY
           .register(
                   "malignancy",
                   new MEnchantment(Rarity.RARE, EnchantmentTarget.WEAPON, EquipmentSlot.MAINHAND)
                           .setIsAcceptableItem((enchant, bool, stack) -> stack.isOf(null))
                           .setMinPower(1)
                           .setMaxPower(100)
                           .setCanAccept((enchant, bool, other) -> other != DOUBLE_DASH && other != ACCUMULATE && bool)
           );
   Enchantment REBOUND = AureumAstaDisks.REGISTRY
           .register(
                   "rebound",
                   new MEnchantment(Rarity.RARE, EnchantmentTarget.WEAPON, EquipmentSlot.MAINHAND)
                           .setIsAcceptableItem((enchant, bool, stack) -> stack.isOf(AmariteItems.AMARITE_DISC))
                           .setMinPower(1)
                           .setMaxPower(100)
                           .setCanAccept((enchant, bool, other) -> other != AmariteEnchantments.PYLON && other != AmariteEnchantments.ORBIT && bool)
           );
   Enchantment PYLON = AureumAstaDisks.REGISTRY
           .register(
                   "pylon",
                   new MEnchantment(Rarity.RARE, EnchantmentTarget.WEAPON, EquipmentSlot.MAINHAND)
                           .setIsAcceptableItem((enchant, bool, stack) -> stack.isOf(null))
                           .setMinPower(1)
                           .setMaxPower(100)
                           .setCanAccept((enchant, bool, other) -> other != REBOUND && other != AmariteEnchantments.ORBIT && bool)
           );
   Enchantment ORBIT = AureumAstaDisks.REGISTRY
           .register(
                   "orbit",
                   new MEnchantment(Rarity.RARE, EnchantmentTarget.WEAPON, EquipmentSlot.MAINHAND)
                           .setIsAcceptableItem((enchant, bool, stack) -> stack.isOf(null))
                           .setMinPower(1)
                           .setMaxPower(100)
                           .setCanAccept((enchant, bool, other) -> other != REBOUND && other != PYLON && bool)
           );
   Enchantment ANONYMITY = AureumAstaDisks.REGISTRY
      .register(
         "anonymity",
         new MEnchantment(Rarity.RARE, EnchantmentTarget.WEARABLE)
            .setIsAcceptableItem((enchant, bool, stack) -> stack.getItem() instanceof MaskItem)
            .setMinPower(1)
            .setMaxPower(100)
            .setCanAccept((enchant, bool, other) -> other != AmariteEnchantments.CONCEALMENT && bool)
      );
   Enchantment CONCEALMENT = AureumAstaDisks.REGISTRY
      .register(
         "concealment",
         new MEnchantment(Rarity.RARE, EnchantmentTarget.WEARABLE)
            .setIsAcceptableItem((enchant, bool, stack) -> stack.getItem() instanceof MaskItem)
            .setMinPower(1)
            .setMaxPower(100)
            .setCanAccept((enchant, bool, other) -> other != ANONYMITY && bool)
      );

   static void init() {
   }
}
