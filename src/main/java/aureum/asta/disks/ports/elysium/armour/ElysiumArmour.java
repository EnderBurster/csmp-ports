package aureum.asta.disks.ports.elysium.armour;

import aureum.asta.disks.ports.elysium.Elysium;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.ActionResult;

public class ElysiumArmour {
   public static final ArmorMaterial ELYSIUM_MATERIAL = new ElysiumArmorMaterial();
   public static final TagKey<Item> ELYSIUM_ARMOUR_TAG = TagKey.of(Registries.ITEM.getKey(), Elysium.id("elysium_armour"));
   public static final TagKey<Item> EXPERIMENTAL_ELYSIUM_ARMOUR_TAG = TagKey.of(Registries.ITEM.getKey(), Elysium.id("experimental_elysium_armour"));
   public static ElysiumArmourItem ELYSIUM_HELMET;
   public static ElysiumArmourItem ELYSIUM_CHESTPLATE;
   public static ElysiumArmourItem ELYSIUM_LEGGINGS;
   public static ElysiumArmourItem ELYSIUM_BOOTS;
   public static ArmorItem EXPERIMENTAL_ELYSIUM_HELMET;
   public static ArmorItem EXPERIMENTAL_ELYSIUM_CHESTPLATE;
   public static ArmorItem EXPERIMENTAL_ELYSIUM_LEGGINGS;
   public static ArmorItem EXPERIMENTAL_ELYSIUM_BOOTS;
   public static StatusEffect ELYSIUM_VULNERABILITY;
   public static RecipeSerializer<ElysiumUpgradeRecipe> ELYSIUM_UPGRADE_RECIPE_SERIALIZER;

   public static void registerAll() {
      ELYSIUM_HELMET = (ElysiumArmourItem) Registry.register(
              Registries.ITEM, Elysium.id("elysium_helmet"), new ElysiumArmourItem(ELYSIUM_MATERIAL, ArmorItem.Type.HELMET, new Settings())
      );
      ELYSIUM_CHESTPLATE = (ElysiumArmourItem)Registry.register(
              Registries.ITEM, Elysium.id("elysium_chestplate"), new ElysiumArmourItem(ELYSIUM_MATERIAL,  ArmorItem.Type.CHESTPLATE, new Settings())
      );
      ELYSIUM_LEGGINGS = (ElysiumArmourItem)Registry.register(
              Registries.ITEM, Elysium.id("elysium_leggings"), new ElysiumArmourItem(ELYSIUM_MATERIAL,  ArmorItem.Type.LEGGINGS, new Settings())
      );
      ELYSIUM_BOOTS = (ElysiumArmourItem)Registry.register(
              Registries.ITEM, Elysium.id("elysium_boots"), new ElysiumArmourItem(ELYSIUM_MATERIAL,  ArmorItem.Type.BOOTS, new Settings())
      );
      EXPERIMENTAL_ELYSIUM_HELMET = (ArmorItem)Registry.register(
              Registries.ITEM,
              Elysium.id("experimental_elysium_helmet"),
              new ElysiumArmourItem(ArmorMaterials.IRON, ArmorItem.Type.HELMET, new Settings())
      );
      EXPERIMENTAL_ELYSIUM_CHESTPLATE = (ArmorItem)Registry.register(
              Registries.ITEM,
              Elysium.id("experimental_elysium_chestplate"),
              new ElysiumArmourItem(ArmorMaterials.IRON, ArmorItem.Type.CHESTPLATE, new Settings())
      );
      EXPERIMENTAL_ELYSIUM_LEGGINGS = (ArmorItem)Registry.register(
              Registries.ITEM,
              Elysium.id("experimental_elysium_leggings"),
              new ElysiumArmourItem(ArmorMaterials.IRON, ArmorItem.Type.LEGGINGS, new Settings())
      );
      EXPERIMENTAL_ELYSIUM_BOOTS = (ArmorItem)Registry.register(
              Registries.ITEM,
              Elysium.id("experimental_elysium_boots"),
              new ElysiumArmourItem(ArmorMaterials.IRON, ArmorItem.Type.BOOTS, new Settings())
      );
      ELYSIUM_VULNERABILITY = (StatusEffect)Registry.register(
              Registries.STATUS_EFFECT, Elysium.id("elysium_vulnerability"), new ElysiumVulnerabilityMobEffect()
      );
      ELYSIUM_UPGRADE_RECIPE_SERIALIZER = (RecipeSerializer<ElysiumUpgradeRecipe>)Registry.register(
              Registries.RECIPE_SERIALIZER, Elysium.id("elysium_upgrade"), new ElysiumUpgradeRecipe.Serializer()
      );
   }

   public static boolean isAffectedByElysiumVulnerability(DamageSource source) {
      return !source.isOf(DamageTypes.MAGIC) && !source.isOf(DamageTypes.ON_FIRE) && !source.isOf(DamageTypes.FREEZE) && !source.isOf(DamageTypes.WITHER);
   }

   public static void init() {
      registerAll();

      AttackEntityCallback.EVENT.register((AttackEntityCallback)(player, level, hand, entity, hitResult) -> {
         if (!level.isClient()) {
            ElysiumArmourComponent.KEY.maybeGet(player).ifPresent(comp -> {
               if (comp.shouldDischargeWhenAttacking() && entity instanceof LivingEntity living) {
                  comp.dischargeVuln(living);
               }
            });
         }

         return ActionResult.PASS;
      });
   }
}
