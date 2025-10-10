package aureum.asta.disks.mixin.ports.charter;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.charter.common.block.BrushableBlock;
import aureum.asta.disks.ports.charter.common.block.BrushableBlockEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SuspiciousSandBlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BrushItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrushItem.class)
public class BrushItemMixin {
    /*@WrapOperation(method = "usageTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private boolean charter$addBrushableBlocks(BlockState instance, Block block, Operation<Boolean> original)
    {
        if(instance.getBlock() instanceof BrushableBlock) return true;
        return original.call(instance, block);
    }*/

    @Inject(method = "usageTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private void charter$addBrushableBlocks(World world, LivingEntity user, ItemStack stack, int remainingUseTicks, CallbackInfo ci, @Local BlockState blockState, @Local BlockHitResult blockHitResult)
    {
        BlockPos blockPos = blockHitResult.getBlockPos();
        if (!world.isClient() && blockState.getBlock() instanceof BrushableBlock) {
            BlockEntity bl = world.getBlockEntity(blockPos);
            if (bl instanceof BrushableBlockEntity brushableBlock) {
                boolean hit = brushableBlock.brush(world.getTime(), (PlayerEntity) user, blockHitResult.getSide());
                if (hit) {
                    stack.damage(1, user, (userx) -> userx.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
                }
            }
        }
    }
}
