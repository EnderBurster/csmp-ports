package aureum.asta.disks.ports.amarite.amarite.items;

import aureum.asta.disks.ports.other.ReachEntityAttributes;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.UUID;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.World;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.cca.LongswordAccumulateComponent;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteEnchantments;
import aureum.asta.disks.ports.amarite.mialib.MiaLib;
import aureum.asta.disks.ports.amarite.mialib.cca.HoldingComponent;
import aureum.asta.disks.ports.amarite.mialib.templates.MToolMaterial;

public class AmariteLongswordItem extends SwordItem implements Vanishable {
   private static final MToolMaterial MATERIAL = MToolMaterial.EMPTY.copy().setMiningSpeedMultiplier(10.5F).setMiningLevel(4).setEnchantability(36);
   private static final UUID ATTACK_REACH_MODIFIER_ID = UUID.fromString("76a8dee3-3e7e-4e11-ba46-a19b0c724567");
   private static final UUID REACH_MODIFIER_ID = UUID.fromString("a31c8afc-a716-425d-89cd-0d373380e6e7");
   private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

   public AmariteLongswordItem(FabricItemSettings settings) {
      super(MATERIAL, 8, -2.7F, settings);
      Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
      builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", 7.5, Operation.ADDITION));
      builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", -2.7, Operation.ADDITION));
      builder.put(ReachEntityAttributes.ATTACK_RANGE, new EntityAttributeModifier(ATTACK_REACH_MODIFIER_ID, "Weapon modifier", 0.75, Operation.ADDITION));
      builder.put(ReachEntityAttributes.REACH, new EntityAttributeModifier(REACH_MODIFIER_ID, "Weapon modifier", 1.5, Operation.ADDITION));
      this.attributeModifiers = builder.build();
   }

   public TypedActionResult<ItemStack> use(World world, @NotNull PlayerEntity user, Hand hand) {
      user.setCurrentHand(hand);
      return TypedActionResult.consume(user.getStackInHand(hand));
   }

   public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
      if (selected && entity instanceof PlayerEntity player && player.getMainHandStack() == stack) {
         HoldingComponent component = (HoldingComponent)MiaLib.HOLDING.get(player);
         if (!world.isClient() && component.hasBeenUsing() && component.startedAttacking()) {
            getMode(player, stack).useAbility();
         }

         if (component.stoppedUsing() && !((LongswordAccumulateComponent)Amarite.ACCUMULATE.get(player)).accumulateActive) {
            player.resetLastAttackedTicks();
         }
      }

      super.inventoryTick(stack, world, entity, slot, selected);
   }

   public int mialib$enchantLevel(Enchantment enchantment, ItemStack stack, int level) {
      return enchantment == Enchantments.SWEEPING ? level + 5 : super.mialib$enchantLevel(enchantment, stack, level);
   }

   public ActionResult mialib$checkEnchantment(EnchantmentTarget target, Enchantment enchantment) {
      return enchantment != AmariteEnchantments.DOUBLE_DASH && enchantment != AmariteEnchantments.ACCUMULATE && enchantment != AmariteEnchantments.MALIGNANCY ? ActionResult.FAIL : ActionResult.SUCCESS;
   }

   @Environment(EnvType.CLIENT)
   public int mialib$getNameColor(ItemStack stack) {
      return getMode(MinecraftClient.getInstance().player, stack).getModeColor();
   }

   @Environment(EnvType.CLIENT)
   public void appendTooltip(ItemStack stack, @Nullable World world, @NotNull List<Text> tooltip, TooltipContext context) {
      AmariteLongswordItem.LongswordMode mode = getMode(MinecraftClient.getInstance().player, stack);
      tooltip.add(
         Text.translatable(
               this.getOrCreateTranslationKey() + ".desc_1", new Object[]{MinecraftClient.getInstance().options.useKey.getBoundKeyLocalizedText().mialib$withColor(7699656)}
            )
            .formatted(Formatting.GRAY)
      );
      tooltip.add(
         Text.translatable(
               this.getOrCreateTranslationKey() + ".desc_2",
               new Object[]{
                  MinecraftClient.getInstance().options.attackKey.getBoundKeyLocalizedText().mialib$withColor(7699656),
                  Text.translatable(mode.getTranslationKey()).mialib$withColor(mode.getModeColor())
               }
            )
            .formatted(Formatting.GRAY)
      );
   }

   @Environment(EnvType.CLIENT)
   public void mialib$renderCustomBar(ItemRenderer drawContext, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, MatrixStack matrices) {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null) {
         AmariteLongswordItem.LongswordMode mode = getMode(player, stack);

         RenderSystem.disableDepthTest();
         float width = mode.getChargeProgress();
         int k = x + 2;
         int l = y + 13;
         int color = width >= 1.0F ? mode.getModeColor() : -8355712;

         DrawableHelper.fill(matrices, k, l, k + 13, l + 2, ColorHelper.Argb.getArgb(255, 0, 0, 0));

         if (mode.getModeColor() != -11627802)
         {
            DrawableHelper.fill(matrices, k, l, k + (int)(Math.max(0.0F, (Math.min(1.0F, width) * 13.0F))), l + 1, color);
            DrawableHelper.fill(matrices, k, l, k + (int)(Math.max(0.0F, (Math.min(1.0F, width - 1.0F) * 13.0F))), l + 1, ColorHelper.Argb.getArgb(255, 240, 204, 98));
         }
         else
         {
            color = width >= 0.5F ? mode.getModeColor() : -8355712;
            DrawableHelper.fill(matrices, k, l, k + (int)(Math.max(0.0F,(Math.min(1.0F, width * 2.0F) * 6.0F))), l + 1, color);
            DrawableHelper.fill(matrices, k, l, k + (int)(Math.max(0.0F,(Math.min(1.0F, width * 2.0F - 2.0F) * 6.0F))), l + 1, ColorHelper.Argb.getArgb(255, 240, 204, 98));

            color = width >= 1.0F ? mode.getModeColor() : -8355712;
            DrawableHelper.fill(matrices, k + 7, l, k + 7 + (int)(Math.max(0.0F,(Math.min(1.0F, width * 2.0F - 1.0F) * 6.0F))), l + 1, color);
            DrawableHelper.fill(matrices, k + 7, l, k + 7 + (int)(Math.max(0.0F,(Math.min(1.0F, width * 2.0F - 3.0F) * 6.0F))), l + 1, ColorHelper.Argb.getArgb(255, 240, 204, 98));

         }

         RenderSystem.enableDepthTest();

         /*RenderSystem.disableDepthTest();
         //RenderSystem.disableTexture();
         RenderSystem.disableBlend();
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferBuilder = tessellator.getBuffer();
         float width = mode.getChargeProgress();
         int color = width >= 1.0F ? mode.getModeColor() : -8355712;
         this.mialib$renderGuiQuad(bufferBuilder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
         if (mode.getModeColor() != -11627802) {
            this.mialib$renderGuiQuad(
               bufferBuilder, x + 2, y + 13, (int)(Math.min(1.0F, width) * 13.0F), 1, color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, 255
            );
            this.mialib$renderGuiQuad(bufferBuilder, x + 2, y + 13, (int)(Math.min(1.0F, width - 1.0F) * 13.0F), 1, 240, 204, 98, 255);
         } else {
            color = width >= 0.5F ? mode.getModeColor() : -8355712;
            this.mialib$renderGuiQuad(
               bufferBuilder, x + 2, y + 13, (int)(Math.min(1.0F, width * 2.0F) * 6.0F), 1, color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, 255
            );
            this.mialib$renderGuiQuad(bufferBuilder, x + 2, y + 13, (int)(Math.min(1.0F, width * 2.0F - 2.0F) * 6.0F), 1, 240, 204, 98, 255);
            color = width >= 1.0F ? mode.getModeColor() : -8355712;
            this.mialib$renderGuiQuad(
               bufferBuilder, x + 9, y + 13, (int)(Math.min(1.0F, width * 2.0F - 1.0F) * 6.0F), 1, color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, 255
            );
            this.mialib$renderGuiQuad(bufferBuilder, x + 9, y + 13, (int)(Math.min(1.0F, width * 2.0F - 3.0F) * 6.0F), 1, 240, 204, 98, 255);
         }

         RenderSystem.enableBlend();
         //RenderSystem.enableTexture();
         RenderSystem.enableDepthTest();*/
      }
   }

   public boolean hasGlint(ItemStack stack) {
      return false;
   }

   public boolean isEnchantable(ItemStack stack) {
      return true;
   }

   public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
      return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
   }

   /*public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
      if (this.isIn(group)) {
         for (Enchantment enchantment : new Enchantment[]{null, AmariteEnchantments.DOUBLE_DASH, AmariteEnchantments.ACCUMULATE}) {
            ItemStack stack = new ItemStack(this);
            if (enchantment != null) {
               stack.addEnchantment(enchantment, enchantment.getMaxLevel());
            }

            stacks.add(stack);
         }
      }
   }*/

   @NotNull
   public static AmariteLongswordItem.LongswordMode getMode(PlayerEntity user, ItemStack stack) {
      if (EnchantmentHelper.getLevel(AmariteEnchantments.DOUBLE_DASH, stack) > 0) {
         return (AmariteLongswordItem.LongswordMode)Amarite.DOUBLE_DASH.get(user);
      } else if (EnchantmentHelper.getLevel(AmariteEnchantments.ACCUMULATE, stack) > 0) {
         return (AmariteLongswordItem.LongswordMode)Amarite.ACCUMULATE.get(user);
      } else {
         return EnchantmentHelper.getLevel(AmariteEnchantments.MALIGNANCY, stack) > 0
                 ? (AmariteLongswordItem.LongswordMode)Amarite.MALIGNANCY.get(user)
                 : (AmariteLongswordItem.LongswordMode)Amarite.DASH.get(user);
      }
   }

   @NotNull
   public static AmariteLongswordItem.LongswordMode[] getModes(PlayerEntity user) {
      return new AmariteLongswordItem.LongswordMode[]{
              (AmariteLongswordItem.LongswordMode)Amarite.DASH.get(user),
              (AmariteLongswordItem.LongswordMode)Amarite.DOUBLE_DASH.get(user),
              (AmariteLongswordItem.LongswordMode)Amarite.ACCUMULATE.get(user),
              (AmariteLongswordItem.LongswordMode)Amarite.MALIGNANCY.get(user)
      };
   }

   public interface LongswordMode {
      int OVERCHARGE_COLOR = -996254;
      int UNCHARGED_COLOR = -8355712;

      void absorbDamage(float var1);

      void useAbility();

      int getModeColor();

      int getSwordTint();

      float getChargeProgress();

      String getTranslationKey();
   }
}
