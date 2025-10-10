package aureum.asta.disks.mixin.ports.arsenal;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
abstract class ItemMixin implements ItemConvertible {
    @Inject(
            method = {"use"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void arsenal$throw(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        Item item = (Item)(Object)this;
        if (item == Items.FIRE_CHARGE) {
            ItemStack stack = user.getStackInHand(hand);
            if (!world.isClient()) {
                world.syncWorldEvent(null, 1018, user.getBlockPos(), 0);
                Vec3d vec3d = user.getRotationVec(1.0F).normalize().multiply(2.0);
                SmallFireballEntity smallFireballEntity = new SmallFireballEntity(world, user, vec3d.getX(), vec3d.getY(), vec3d.getZ());
                smallFireballEntity.setPosition(smallFireballEntity.getX(), user.getEyeY(), smallFireballEntity.getZ());
                world.spawnEntity(smallFireballEntity);
                stack.decrement(1);
                user.getItemCooldownManager().set(Items.FIRE_CHARGE, 6);
            }

            cir.setReturnValue(TypedActionResult.success(stack));
        }
    }
}
