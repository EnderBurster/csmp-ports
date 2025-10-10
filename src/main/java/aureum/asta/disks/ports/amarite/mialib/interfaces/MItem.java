package aureum.asta.disks.ports.amarite.mialib.interfaces;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MItem {
   Identifier[] EMPTY = new Identifier[0];

   default boolean mialib$shouldSmelt(
      World world, BlockState state, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack stack
   ) {
      return false;
   }

   default boolean mialib$attack(World world, ItemStack stack, LivingEntity attacker, Entity target) {
      return false;
   }

   default DamageSource mialib$setDamageSource(LivingEntity entity, DamageSources sources) {
      return null;
   }

   default void mialib$killEntity(World world, ItemStack stack, LivingEntity user, LivingEntity victim) {
   }

   default Identifier[] mialib$cooldownDisplays() {
      return EMPTY;
   }

   @Environment(EnvType.CLIENT)
   default ArmPose mialib$pose(LivingEntity entity, Hand hand, ItemStack stack) {
      return null;
   }

   @Environment(EnvType.CLIENT)
   default boolean mialib$shouldHideInHand(LivingEntity entity, Hand hand, ItemStack stack) {
      return false;
   }

   default int mialib$enchantLevel(Enchantment enchantment, ItemStack stack, int level) {
      return level;
   }

   default int mialib$enchant(Enchantment enchantment, ItemStack stack, int level) {
      return level;
   }

   default ActionResult mialib$checkEnchantment(EnchantmentTarget target, Enchantment enchantment) {
      return ActionResult.PASS;
   }

   default int mialib$getNameColor(ItemStack stack) {
      return -1;
   }

   @Environment(EnvType.CLIENT)
   default void mialib$renderCustomBar(ItemRenderer drawContext, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, MatrixStack matrices) {
   }

   default void mialib$renderGuiQuad(@NotNull BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
      RenderSystem.setShader(GameRenderer::getPositionColorProgram);
      buffer.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      buffer.vertex((double)x, (double)y, 0.0).color(red, green, blue, alpha).next();
      buffer.vertex((double)x, (double)(y + height), 0.0).color(red, green, blue, alpha).next();
      buffer.vertex((double)(x + width), (double)(y + height), 0.0).color(red, green, blue, alpha).next();
      buffer.vertex((double)(x + width), (double)y, 0.0).color(red, green, blue, alpha).next();
      BufferRenderer.drawWithGlobalProgram(buffer.end());
   }

    default float getBonusAttackDamage(Entity target, float baseAttackDamage, DamageSource damageSource)
    {
       return 0.0F;
    }
}
