package aureum.asta.disks.ports.blast.common.item;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.blast.common.entity.PipeBombEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class PipeBombItem extends Item {
    public PipeBombItem(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        if (!playerEntity.isCreative()) {
            for (int i = 0; i < playerEntity.getInventory().size(); i++) {
                if (playerEntity.getInventory().getStack(i).getItem() instanceof BombItem || playerEntity.getInventory().getStack(i).getItem() instanceof TriggerBombItem) {
                    playerEntity.getItemCooldownManager().set(playerEntity.getInventory().getStack(i).getItem(), 20);
                }
            }
        }

        ItemStack stackInHand = playerEntity.getStackInHand(hand);

        this.playSoundEffects(world, playerEntity);

        if (!world.isClient) {
            PipeBombEntity entity = PipeBombEntity.fromItemStack(world, stackInHand, playerEntity);
            entity.setOwner(playerEntity);
            world.spawnEntity(entity);
        }

        if (!playerEntity.getAbilities().creativeMode) {
            stackInHand.decrement(1);
        }
        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        return new TypedActionResult<>(ActionResult.SUCCESS, stackInHand);
    }

    public void playSoundEffects(World world, PlayerEntity playerEntity) {
        world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (playerEntity.getRandom().nextFloat() * 0.4F + 0.8F));
        world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.BLOCK_TRIPWIRE_CLICK_OFF, SoundCategory.NEUTRAL, 0.5F, 1.0f);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (stack.getOrCreateNbt().getBoolean("Armed") && entity instanceof PlayerEntity player) {
            if (!world.isClient) {
                for (int i = 0; i < stack.getCount(); i++) {
                    this.playSoundEffects(world, player);
                    PipeBombEntity pipeBombEntity = PipeBombEntity.fromItemStack(world, stack, player);
                    pipeBombEntity.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 0.0f, 1.0F);
                    pipeBombEntity.setOwner(player);
                    world.spawnEntity(pipeBombEntity);
                }
                stack.decrement(stack.getCount());
            }
        }
    }
}
