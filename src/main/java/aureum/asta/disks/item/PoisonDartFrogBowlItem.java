package aureum.asta.disks.item;

import aureum.asta.disks.cca.PickYourPoisonEntityComponents;
import aureum.asta.disks.ports.pickyourpoison.PickYourPoison;
import aureum.asta.disks.entity.PoisonDartFrogEntity;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class PoisonDartFrogBowlItem extends Item {
    public final net.minecraft.util.Identifier texture;

    public PoisonDartFrogBowlItem(Settings settings, net.minecraft.util.Identifier frogTexture) {
        super(settings);
        texture = frogTexture;
    }

    public static PoisonDartFrogEntity.Type getFrogType(Item item) {
        PoisonDartFrogEntity.Type type = null;

        if (item == PickYourPoison.BLUE_POISON_DART_FROG_BOWL) {
            type = PoisonDartFrogEntity.Type.BLUE;
        } else if (item == PickYourPoison.GOLDEN_POISON_DART_FROG_BOWL) {
            type = PoisonDartFrogEntity.Type.GOLDEN;
        } else if (item == PickYourPoison.GREEN_POISON_DART_FROG_BOWL) {
            type = PoisonDartFrogEntity.Type.GREEN;
        } else if (item == PickYourPoison.ORANGE_POISON_DART_FROG_BOWL) {
            type = PoisonDartFrogEntity.Type.ORANGE;
        } else if (item == PickYourPoison.CRIMSON_POISON_DART_FROG_BOWL) {
            type = PoisonDartFrogEntity.Type.CRIMSON;
        } else if (item == PickYourPoison.RED_POISON_DART_FROG_BOWL) {
            type = PoisonDartFrogEntity.Type.RED;
        } else if (item == PickYourPoison.LUXALAMANDER_BOWL) {
            type = PoisonDartFrogEntity.Type.LUXINTRUS;
        } else if (item == PickYourPoison.RANA_BOWL) {
            type = PoisonDartFrogEntity.Type.RANA;
        }

        return type;
    }

    public net.minecraft.item.ItemStack finishUsing(net.minecraft.item.ItemStack stack, World world, LivingEntity user) {
        super.finishUsing(stack, world, user);
        if (user instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) user;
            Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        if (!world.isClient && PoisonDartFrogEntity.getFrogPoisonEffect(getFrogType(stack.getItem())) != null) {
            user.addStatusEffect(PoisonDartFrogEntity.getFrogPoisonEffect(getFrogType(stack.getItem())));

            if (PoisonDartFrogEntity.getFrogPoisonEffect(getFrogType(stack.getItem())).getEffectType() == PickYourPoison.NUMBNESS) {
                PickYourPoisonEntityComponents.NUMBNESS_DAMAGE.get(user).setFromLicking(true);
            }
        }

        return stack;
    }

    @Override
    public net.minecraft.util.ActionResult useOnBlock(ItemUsageContext context) {
        PoisonDartFrogEntity.Type type = getFrogType(context.getStack().getItem());
        if (type != null && context.getPlayer() != null && context.getPlayer().isSneaking()) {
            PoisonDartFrogEntity poisonDartFrog = new PoisonDartFrogEntity(PickYourPoison.POISON_DART_FROG, context.getWorld());

            BlockHitResult blockHitResult = BucketItem.raycast(context.getWorld(), context.getPlayer(), RaycastContext.FluidHandling.SOURCE_ONLY);
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getSide();
            BlockPos blockPos2 = blockPos.offset(direction);

            poisonDartFrog.setPosition(blockPos2.getX() + .5f, blockPos2.getY(), blockPos2.getZ() + .5f);
            poisonDartFrog.setPoisonDartFrogType(type);
            poisonDartFrog.fromBowl = true;

            if (context.getStack().hasCustomName()) {
                poisonDartFrog.setCustomName(context.getStack().getName());
            }

            context.getWorld().playSound(context.getPlayer(), context.getBlockPos(), PickYourPoison.ITEM_POISON_DART_FROG_BOWL_EMPTY, SoundCategory.NEUTRAL, 1.0f, 1.4f);
            context.getWorld().spawnEntity(poisonDartFrog);

            if (context.getPlayer() != null && !context.getPlayer().getAbilities().creativeMode) {
                context.getPlayer().setStackInHand(context.getHand(), new net.minecraft.item.ItemStack(Items.BOWL));
            }

            return net.minecraft.util.ActionResult.SUCCESS;
        }

        return super.useOnBlock(context);
    }

    public int getMaxUseTime(net.minecraft.item.ItemStack stack) {
        return 20;
    }

    public net.minecraft.util.UseAction getUseAction(net.minecraft.item.ItemStack stack) {
        return net.minecraft.util.UseAction.DRINK;
    }

    public SoundEvent getDrinkSound() {
        return PickYourPoison.ITEM_POISON_DART_FROG_BOWL_LICK;
    }

    public SoundEvent getEatSound() {
        return PickYourPoison.ITEM_POISON_DART_FROG_BOWL_LICK;
    }

    public net.minecraft.util.TypedActionResult<net.minecraft.item.ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (PoisonDartFrogEntity.getFrogPoisonEffect(getFrogType(user.getStackInHand(hand).getItem())) != null && user.hasStatusEffect(PoisonDartFrogEntity.getFrogPoisonEffect(getFrogType(user.getStackInHand(hand).getItem())).getEffectType())) {
            return net.minecraft.util.TypedActionResult.pass(user.getStackInHand(hand));
        }
        return ItemUsage.consumeHeldItem(world, user, hand);
    }
}
