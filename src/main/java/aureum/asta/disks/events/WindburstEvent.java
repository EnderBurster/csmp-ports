package aureum.asta.disks.events;

import aureum.asta.disks.interfaces.WindBurstHolder;
import aureum.asta.disks.ports.mace.FaithfulMace;
import aureum.asta.disks.ports.mace.enchantments.MaceEnchants;
import aureum.asta.disks.ports.mace.entity.WindChargeEntity;
import aureum.asta.disks.ports.mace.item.MaceItem;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class WindburstEvent implements UseItemCallback {
    @Override
    public TypedActionResult<ItemStack> interact(PlayerEntity player, World world, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!player.getItemCooldownManager().isCoolingDown(stack.getItem()) && stack.getItem() instanceof MaceItem && EnchantmentHelper.get(stack).containsKey(MaceEnchants.WIND_BURST)) {
            if (!world.isClient) {
                WindChargeEntity windChargeEntity = WindChargeEntity.create(player, world, player.getPos().getX(), player.getEyePos().getY(), player.getPos().getZ());
                windChargeEntity.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 1.5F, 1.0F);
                ((WindBurstHolder) windChargeEntity).enchancement$setFromWindBurst(true);
                world.spawnEntity(windChargeEntity);
            }
            world.playSound(null, player.getX(), player.getY(), player.getZ(), FaithfulMace.ENTITY_WIND_CHARGE_THROW_SOUND_EVENT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
            player.getItemCooldownManager().set(stack.getItem(), 20);
            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
            stack.damage(1, player, (p) -> p.sendToolBreakStatus(hand));
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.pass(stack);
    }
}
