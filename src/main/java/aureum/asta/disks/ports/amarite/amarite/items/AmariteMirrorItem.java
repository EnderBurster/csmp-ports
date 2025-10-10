package aureum.asta.disks.ports.amarite.amarite.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.StringHelper;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteSoundEvents;
import aureum.asta.disks.mixin.ports.amarite.mialib.mixin.accessors.BeaconBlockEntityAccessor;

public class AmariteMirrorItem extends Item {
   public static final Map<StatusEffect, Integer> MIRROR_COOLDOWNS = new HashMap<>();

   public AmariteMirrorItem(Settings settings) {
      super(settings);
   }

   public ActionResult useOnBlock(@NotNull ItemUsageContext context) {
      World world = context.getWorld();
      if (!world.isClient() && context.getPlayer() != null && context.getPlayer().isSneaking()) {
         ItemStack itemStack = context.getStack();
         BlockPos pos = context.getBlockPos();
         BlockState state = world.getBlockState(pos);
         if (state.isOf(Blocks.BEACON)) {
            if (world.getBlockEntity(pos) instanceof BeaconBlockEntityAccessor beaconBlock) {
               StatusEffect primaryEffect = beaconBlock.getPrimary();
               StatusEffect secondaryEffect = beaconBlock.getSecondary();
               if (writeEffects(itemStack, primaryEffect, secondaryEffect)) {
                  world.playSoundFromEntity(null, context.getPlayer(), AmariteSoundEvents.MIRROR_EXTRACT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                  if (world instanceof ServerWorld serverWorld) {
                     Vec3d hitPos = context.getHitPos();
                     serverWorld.spawnParticles(
                        ParticleTypes.END_ROD, hitPos.getX(), hitPos.getY(), hitPos.getZ(), 12, 0.15, 0.15, 0.15, 0.1
                     );
                  }
               }
            }
         } else if (state.isOf(Blocks.CONDUIT) && writeEffects(itemStack, StatusEffects.CONDUIT_POWER, null)) {
            itemStack.getOrCreateNbt().putBoolean("Conduit", true);
            world.playSoundFromEntity(null, context.getPlayer(), AmariteSoundEvents.MIRROR_EXTRACT, SoundCategory.PLAYERS, 1.0F, 0.9F);
            if (world instanceof ServerWorld serverWorld) {
               Vec3d hitPos = context.getHitPos();
               serverWorld.spawnParticles(ParticleTypes.END_ROD, hitPos.getX(), hitPos.getY(), hitPos.getZ(), 12, 0.15, 0.15, 0.15, 0.1);
            }
         }
      }

      return super.useOnBlock(context);
   }

   public TypedActionResult<ItemStack> use(World world, @NotNull PlayerEntity user, Hand hand) {
      if (!user.isSneaking()) {
         ItemStack itemStack = user.getStackInHand(hand);
         useMirror(world, user, itemStack, true);
      }

      return super.use(world, user, hand);
   }

   public static void useMirror(@NotNull World world, PlayerEntity user, ItemStack itemStack, boolean particles) {
      StatusEffect primaryEffect = getPrimary(itemStack);
      StatusEffect secondaryEffect = getSecondary(itemStack);
      if (!world.isClient() && primaryEffect != null) {
         boolean success = false;
         boolean repeat = user.hasStatusEffect(primaryEffect);
         if (user.addStatusEffect(new StatusEffectInstance(primaryEffect, 300, primaryEffect == secondaryEffect ? 1 : 0, particles, particles, true))) {
            success = true;
         }

         if (primaryEffect != secondaryEffect && secondaryEffect != null) {
            if (user.hasStatusEffect(secondaryEffect)) {
               repeat = true;
            }

            if (user.addStatusEffect(new StatusEffectInstance(secondaryEffect, 300, 0, particles, particles, true))) {
               success = true;
            }
         }

         if (success) {
            user.getItemCooldownManager().set(itemStack.getItem(), getCooldown(itemStack));
            if (!repeat) {
               world.playSoundFromEntity(null, user, AmariteSoundEvents.MIRROR_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
         }
      }
   }

   public static boolean writeEffects(@NotNull ItemStack stack, StatusEffect primary, StatusEffect secondary) {
      boolean changed = false;
      NbtCompound compoundTag = stack.getOrCreateNbt();
      int primaryId = StatusEffect.getRawId(primary);
      if (!compoundTag.contains("Primary") || compoundTag.getInt("Primary") != primaryId) {
         compoundTag.putInt("Primary", primaryId);
         changed = true;
      }

      int secondaryId = StatusEffect.getRawId(secondary);
      if (!compoundTag.contains("Secondary") || compoundTag.getInt("Secondary") != secondaryId) {
         compoundTag.putInt("Secondary", secondaryId);
         changed = true;
      }

      return changed;
   }

   @Nullable
   public static StatusEffect getPrimary(@NotNull ItemStack stack) {
      return stack.getNbt() == null ? null : StatusEffect.byRawId(stack.getNbt().getInt("Primary"));
   }

   @Nullable
   public static StatusEffect getSecondary(@NotNull ItemStack stack) {
      return stack.getNbt() == null ? null : StatusEffect.byRawId(stack.getNbt().getInt("Secondary"));
   }

   public static int getCooldown(ItemStack stack) {
      StatusEffect primaryEffect = getPrimary(stack);
      StatusEffect secondaryEffect = getSecondary(stack);
      return MIRROR_COOLDOWNS.getOrDefault(primaryEffect, 160) + MIRROR_COOLDOWNS.getOrDefault(secondaryEffect, 0);
   }

   public int mialib$getNameColor(ItemStack stack) {
      return getMirrorColor(stack, 0.0F);
   }

   public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
      StatusEffect primaryEffect = getPrimary(stack);
      StatusEffect secondaryEffect = getSecondary(stack);
      boolean doubled = primaryEffect == secondaryEffect;
      if (primaryEffect == null) {
         tooltip.add(Text.translatable("item.aureum-asta-disks.amarite_mirror.empty").formatted(Formatting.GRAY));
      } else {
         MutableText primaryText = Text.translatable(primaryEffect.getTranslationKey());
         if (doubled) {
            primaryText = Text.translatable("potion.withAmplifier", new Object[]{primaryText, Text.translatable("potion.potency.1")});
         }

         primaryText = Text.translatable("potion.withDuration", new Object[]{primaryText, StringHelper.formatTicks(300)});
         tooltip.add(primaryText.formatted(primaryEffect.getCategory().getFormatting()));
         if (secondaryEffect != null && !doubled) {
            MutableText secondaryText = Text.translatable(secondaryEffect.getTranslationKey());
            secondaryText = Text.translatable("potion.withDuration", new Object[]{secondaryText, StringHelper.formatTicks(300)});
            tooltip.add(secondaryText.formatted(secondaryEffect.getCategory().getFormatting()));
         }

         tooltip.add(
            Text.translatable("item.aureum-asta-disks.amarite_mirror.cooldown", new Object[]{StringHelper.formatTicks(getCooldown(stack))})
               .formatted(Formatting.GRAY)
         );
      }

      super.appendTooltip(stack, world, tooltip, context);
   }

   public static int getMirrorColor(ItemStack stack, float charge) {
      float i = 2.0F;
      float r = 0.0F;
      float g = 0.0F;
      float b = 0.0F;
      StatusEffect primaryEffect = getPrimary(stack);
      if (primaryEffect != null) {
         int primaryColor = primaryEffect.getColor();
         r += charge + (1.0F - charge) * (float)(primaryColor >> 16 & 0xFF) / 255.0F;
         g += charge + (1.0F - charge) * (float)(primaryColor >> 8 & 0xFF) / 255.0F;
         b += charge + (1.0F - charge) * (float)(primaryColor & 0xFF) / 255.0F;
      } else {
         r++;
         g++;
         b++;
         i--;
      }

      StatusEffect secondaryEffect = getSecondary(stack);
      if (secondaryEffect != null) {
         int secondaryColor = secondaryEffect.getColor();
         r += charge + (1.0F - charge) * (float)(secondaryColor >> 16 & 0xFF) / 255.0F;
         g += charge + (1.0F - charge) * (float)(secondaryColor >> 8 & 0xFF) / 255.0F;
         b += charge + (1.0F - charge) * (float)(secondaryColor & 0xFF) / 255.0F;
      } else {
         r++;
         g++;
         b++;
         i--;
      }

      if (i > 0.0F) {
         r /= i;
         g /= i;
         b /= i;
      }

      return (int)(r * 255.0F) << 16 | (int)(g * 255.0F) << 8 | (int)(b * 255.0F);
   }
}
