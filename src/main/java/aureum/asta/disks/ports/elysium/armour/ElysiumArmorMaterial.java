package aureum.asta.disks.ports.elysium.armour;

import aureum.asta.disks.ports.elysium.Elysium;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Lazy;
import net.minecraft.util.Util;

import java.util.EnumMap;
import java.util.Map;

class ElysiumArmorMaterial implements ArmorMaterial {
   private static final Map<ArmorItem.Type, Integer> BASE_DURABILITY = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
      map.put(ArmorItem.Type.BOOTS, 13);
      map.put(ArmorItem.Type.LEGGINGS, 15);
      map.put(ArmorItem.Type.CHESTPLATE, 16);
      map.put(ArmorItem.Type.HELMET, 11);
   });

   private static final Map<ArmorItem.Type, Integer> PROTECTION_VALUES = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
      map.put(ArmorItem.Type.BOOTS, 3);
      map.put(ArmorItem.Type.LEGGINGS, 6);
      map.put(ArmorItem.Type.CHESTPLATE, 8);
      map.put(ArmorItem.Type.HELMET, 3);
   });

   private static final int DURABILITY_MULTIPLIER = 33;

   @Override
   public int getDurability(ArmorItem.Type type) {
      return BASE_DURABILITY.get(type) * DURABILITY_MULTIPLIER;
   }

   @Override
   public int getProtection(ArmorItem.Type type) {
      return PROTECTION_VALUES.get(type);
   }

   public int getEnchantability() {
      return 5;
   }

   public SoundEvent getEquipSound() {
      return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
   }

   public Ingredient getRepairIngredient() {
      return Ingredient.ofItems(new ItemConvertible[]{Elysium.ELYSIUM_INGOT});
   }

   public String getName() {
      return "elysium";
   }

   public float getToughness() {
      return 2.0F;
   }

   public float getKnockbackResistance() {
      return 0.0F;
   }
}
