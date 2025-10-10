package aureum.asta.disks.ports.amarite.amarite.items;

import aureum.asta.disks.ports.amarite.amarite.cca.DiscPylonComponent;
import aureum.asta.disks.ports.amarite.amarite.entities.PylonEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.text.Text;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.ItemRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.cca.DiscComponent;
import aureum.asta.disks.ports.amarite.amarite.entities.DiscEntity;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteEnchantments;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteEntities;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteSoundEvents;
import aureum.asta.disks.ports.amarite.mialib.MiaLib;
import aureum.asta.disks.ports.amarite.mialib.cca.HoldingComponent;

public class AmariteDiscItem extends MusicDiscItem {
   public AmariteDiscItem(Settings settings) {
      super(15, AmariteSoundEvents.PACIFICUS, settings, 97);
   }

   public TypedActionResult<ItemStack> use(World world, @NotNull PlayerEntity user, Hand hand) {
      ItemStack stack = user.getStackInHand(hand);
      DiscComponent discComponent = (DiscComponent)Amarite.DISC.get(user);
      int next = discComponent.getNextAvailableDisc();
      if (next == -1) {
         return TypedActionResult.consume(stack);
      } else {
         user.incrementStat(Stats.USED.getOrCreateStat(this));
         if (!world.isClient) {
            DiscEntity discEntity = (DiscEntity)AmariteEntities.DISC.create(world);
            assert discEntity != null;
            if(EnchantmentHelper.getLevel(AmariteEnchantments.PYLON, stack) > 0)
            {
                discEntity.pylon = true;
            }
            else if(EnchantmentHelper.getLevel(AmariteEnchantments.ORBIT, stack) > 0)
            {
               discEntity.orbit = true;
            }
             discEntity.setOwner(user);
             discEntity.refreshPositionAndAngles(user.getX(), user.getEyeY() - 0.1, user.getZ(), user.getYaw(), user.getPitch());
             discEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 0.0F);
             discComponent.setDiscEntity(next, discEntity);
             world.spawnEntity(discEntity);
             world.playSoundFromEntity(null, discEntity, AmariteSoundEvents.DISC_THROW, SoundCategory.PLAYERS, 1.0F, 2.0F);
             user.getItemCooldownManager().set(this, 1);
         }

         return TypedActionResult.success(stack);
      }
   }

   public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
      if (selected && entity instanceof ServerPlayerEntity player && player.getMainHandStack() == stack) {
         HoldingComponent component = (HoldingComponent)MiaLib.HOLDING.get(player);
         if (!world.isClient() && component.startedAttacking()) {
            this.discUsage(player);
         }
      }

      super.inventoryTick(stack, world, entity, slot, selected);
   }

   public void discUsage(@NotNull ServerPlayerEntity user) {
      ItemStack stack = user.getMainHandStack();
      DiscComponent discComponent = (DiscComponent)Amarite.DISC.get(user);
      if (EnchantmentHelper.getLevel(AmariteEnchantments.REBOUND, stack) > 0) {
         for (int i = 0; i < discComponent.discIds.size(); i++) {
            if (discComponent.reboundCharge <= 0) {
               return;
            }

            DiscEntity disc = discComponent.getDiscEntity(i);
            if (disc != null) {
               Box box = disc.getBoundingBox().expand(16.0);
               List<LivingEntity> targets = disc.world.getEntitiesByClass(LivingEntity.class, box, e -> DiscEntity.isValidTarget(disc, e));
               if (!targets.isEmpty()) {
                  targets.sort((first, second) -> (int)(first.distanceTo(disc) * 100.0F - second.distanceTo(disc) * 100.0F));
                  disc.trackingTarget = targets.get(0);
                  disc.setUsedRebound(true);
                  disc.getDataTracker().set(DiscEntity.TRACKING_TICKS, 16);
                  disc.setNoGravity(true);
                  disc.setVelocity(0.0, 0.0, 0.0);
                  disc.world.playSoundFromEntity(null, disc, AmariteSoundEvents.DISC_REBOUND, disc.getSoundCategory(), 1.6F, 1.0F);
                  discComponent.reboundCharge--;
               }
            }
         }
      } else if (EnchantmentHelper.getLevel(AmariteEnchantments.PYLON, stack) > 0) {
         if (!user.isCreative() && user.mialib$isCoolingDown(Amarite.id("pylon"))) {
            return;
         }

         PylonEntity pylon = (PylonEntity)AmariteEntities.PYLON.create(user.world);
         if (pylon != null) {
            pylon.setOwner(user);
            pylon.refreshPositionAndAngles(user.getX(), user.getEyeY() - 0.1, user.getZ(), user.getYaw(), user.getPitch());
            pylon.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 0.0F);
            ((DiscPylonComponent)Amarite.PYLON.get(pylon)).pylonCharge = 160;
            user.world.spawnEntity(pylon);
            user.world.playSoundFromEntity(null, pylon, AmariteSoundEvents.DISC_THROW, SoundCategory.PLAYERS, 1.0F, 0.7F);
            user.swingHand(Hand.OFF_HAND);
            user.mialib$setCooldown(Amarite.id("pylon"), 240);
         }
      } else if (EnchantmentHelper.getLevel(AmariteEnchantments.ORBIT, stack) > 0) {
         if (!user.isCreative() && discComponent.orbitCharge < 24.0F) {
            return;
         }

         discComponent.orbitDuration = 160;
         //discComponent.orbitCharge = 0.0F;
         discComponent.sync();

         for (int i = 0; i < 3; i++) {
            if (discComponent.getDiscEntity(i) == null && discComponent.getDiscDurability(i) > 0) {
               DiscEntity discEntity = (DiscEntity)AmariteEntities.DISC.create(user.world);
                assert discEntity != null;
                discEntity.orbit = true;
                discEntity.setOwner(user);
                Vec3d offset = new Vec3d(Math.cos((i + 1) * Math.toRadians(240.0)) * 2.0, 0.0, Math.sin((i + 1) * Math.toRadians(240.0)) * 2.0);
                discEntity.refreshPositionAndAngles(
                        offset.x + user.getX(),
                        user.getEyeY() - 0.1,
                        offset.z + user.getZ(),
                        user.getYaw(),
                        user.getPitch()
                );
                discEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 0.0F);
                discComponent.setDiscEntity(i, discEntity);
                user.world.spawnEntity(discEntity);
            }
         }
      } else {
         for (int ix = 0; ix < discComponent.discIds.size(); ix++) {
            DiscEntity disc = discComponent.getDiscEntity(ix);
            if (disc != null && !disc.hasUsedRecall()) {
               disc.trackingTarget = user;
               disc.setReturning(false);
               disc.setUsedRecall(true);
               disc.getDataTracker().set(DiscEntity.TRACKING_TICKS, 12);
               disc.setNoGravity(true);
               disc.setVelocity(0.0, 0.0, 0.0);
               disc.world.playSoundFromEntity(null, disc, AmariteSoundEvents.DISC_REBOUND, disc.getSoundCategory(), 1.6F, 1.35F);
            }
         }
      }
   }

   public ActionResult mialib$checkEnchantment(EnchantmentTarget target, Enchantment enchantment) {
      return enchantment == AmariteEnchantments.REBOUND ? ActionResult.SUCCESS : ActionResult.FAIL;
   }

   @Environment(EnvType.CLIENT)
   public int mialib$getNameColor(ItemStack stack) {
      return this.getModeColor(stack);
   }

   @Environment(EnvType.CLIENT)
   public void appendTooltip(ItemStack stack, @Nullable World world, @NotNull List<Text> tooltip, TooltipContext context) {
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
                  Text.translatable(this.getModeTranslationKey(stack)).mialib$withColor(this.getModeColor(stack))
               }
            )
            .formatted(Formatting.GRAY)
      );
      tooltip.add(Text.translatable(this.getOrCreateTranslationKey() + ".desc").formatted(Formatting.GRAY));
   }

   @Environment(EnvType.CLIENT)
   public void mialib$renderCustomBar(ItemRenderer drawContext, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, MatrixStack matrices) {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null) {

         RenderSystem.disableDepthTest();
         int k = x + 2;
         int l = y + 13;
         DrawableHelper.fill(matrices, k, l, k + 13, l + 2, ColorHelper.Argb.getArgb(255, 0, 0, 0));

         int color = this.getModeColor(stack);

         if(EnchantmentHelper.getLevel(AmariteEnchantments.REBOUND, stack) > 0)
         {
            DrawableHelper.fill(matrices, k, l - 2, k + 13, l + 2, ColorHelper.Argb.getArgb(255, 0, 0, 0));
            int reboundCharge = ((DiscComponent)Amarite.DISC.get(player)).reboundCharge;

            for (int i = 0; i < reboundCharge; i++)
            {
               DrawableHelper.fill(matrices, k + 1 + i*4, l - 2, k + 1 + i*4 + 3, l - 1, ColorHelper.Argb.getArgb(255, color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF));
            }
         }
         else if(EnchantmentHelper.getLevel(AmariteEnchantments.PYLON, stack) > 0)
         {
            DrawableHelper.fill(matrices, k, l - 2, k + 13, l + 2, ColorHelper.Argb.getArgb(255, 0, 0, 0));
            int pylonColor = player.mialib$isCoolingDown(Amarite.id("pylon")) ? -8355712 : color;
            DrawableHelper.fill(matrices, k + 1, l - 2, k + (int)((1.0F - player.mialib$getCooldown(Amarite.id("pylon"), 1.0F)) * 11.0F) + 1, l - 1, ColorHelper.Argb.getArgb(255, pylonColor >> 16 & 0xFF, pylonColor >> 8 & 0xFF, pylonColor & 0xFF));
         }
         else if(EnchantmentHelper.getLevel(AmariteEnchantments.ORBIT, stack) > 0)
         {
            DrawableHelper.fill(matrices, k, l - 2, k + 13, l + 2, ColorHelper.Argb.getArgb(255, 0, 0, 0));
            float charge = ((DiscComponent)Amarite.DISC.get(player)).orbitCharge;
            int orbitColor = charge < 24.0F ? -8355712 : color;
            DrawableHelper.fill(matrices, k + 1, l - 2, k + (int)(Math.min(1.0F, charge / 24.0F) * 11.0F) + 1, l - 1, ColorHelper.Argb.getArgb(255, orbitColor >> 16 & 0xFF, orbitColor >> 8 & 0xFF, orbitColor & 0xFF));
         }

         for (int i = 0; i < DiscComponent.DISC_COUNT; i++)
         {
            int disc = ((DiscComponent)Amarite.DISC.get(player)).getDiscDurability(i);
            DrawableHelper.fill(matrices, k + 1 + i*4, l, k + 1 + i*4 + disc, l + 1, ColorHelper.Argb.getArgb(255, color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF));
         }

         RenderSystem.enableDepthTest();

         /*
         RenderSystem.disableDepthTest();
         //RenderSystem.disableTexture();
         RenderSystem.disableBlend();
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferBuilder = tessellator.getBuffer();

         this.mialib$renderGuiQuad(bufferBuilder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);

         int color = this.getModeColor(stack);
         if (EnchantmentHelper.getLevel(AmariteEnchantments.REBOUND, stack) > 0) {

            this.mialib$renderGuiQuad(bufferBuilder, x + 2, y + 11, 13, 2, 0, 0, 0, 255);

            int reboundCharge = ((DiscComponent)Amarite.DISC.get(player)).reboundCharge;

            for (int i = 0; i < reboundCharge; i++) {
               this.mialib$renderGuiQuad(bufferBuilder, x + 3 + i * 4, y + 11, 3, 1, color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, 255);
            }
         }

         for (int i = 0; i < 3; i++) {
            int disc = ((DiscComponent)Amarite.DISC.get(player)).getDiscDurability(i);
            this.mialib$renderGuiQuad(bufferBuilder, x + 3 + i * 4, y + 13, disc, 1, color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, 255);
         }

         RenderSystem.enableBlend();
         //RenderSystem.enableTexture();
         RenderSystem.enableDepthTest();*/
      }
   }

   public boolean mialib$shouldHideInHand(LivingEntity entity, Hand hand, ItemStack stack) {
      if (entity instanceof PlayerEntity player) {
         DiscComponent discComponent = (DiscComponent)Amarite.DISC.get(player);
         int next = discComponent.getNextAvailableDisc();
         return next == -1;
      } else {
         return super.mialib$shouldHideInHand(entity, hand, stack);
      }
   }

   /*public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
      if (this.isIn(group)) {
         for (Enchantment enchantment : new Enchantment[]{null, AmariteEnchantments.REBOUND}) {
            ItemStack stack = new ItemStack(this);
            if (enchantment != null) {
               stack.addEnchantment(enchantment, enchantment.getMaxLevel());
            }

            stacks.add(stack);
         }
      }
   }*/

   public int getModeColor(ItemStack stack) {
      if (EnchantmentHelper.getLevel(AmariteEnchantments.REBOUND, stack) > 0) {
         return 7466555;
      } else if (EnchantmentHelper.getLevel(AmariteEnchantments.PYLON, stack) > 0) {
         return 3917038;
      } else {
         return EnchantmentHelper.getLevel(AmariteEnchantments.ORBIT, stack) > 0 ? 16767516 : 14771072;
      }
   }

   public String getModeTranslationKey(ItemStack stack) {
      if (EnchantmentHelper.getLevel(AmariteEnchantments.REBOUND, stack) > 0) {
         return this.getTranslationKey() + ".rebound";
      } else if (EnchantmentHelper.getLevel(AmariteEnchantments.PYLON, stack) > 0) {
         return this.getTranslationKey() + ".pylon";
      } else {
         return EnchantmentHelper.getLevel(AmariteEnchantments.ORBIT, stack) > 0 ? this.getTranslationKey() + ".orbit" : this.getTranslationKey() + ".recall";
      }
   }

   public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
      return false;
   }

   public boolean hasGlint(ItemStack stack) {
      return false;
   }

   public boolean isEnchantable(ItemStack stack) {
      return true;
   }

   public int getEnchantability() {
      return 36;
   }
}
