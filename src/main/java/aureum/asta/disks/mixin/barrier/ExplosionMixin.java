package aureum.asta.disks.mixin.barrier;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.blocks.BarrierBlock;
import aureum.asta.disks.cca.blocks.WaterBarrier;
import aureum.asta.disks.ports.charter.common.component.QueuedBlockChange;
import aureum.asta.disks.ports.charter.common.init.CharterBlocks;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({Explosion.class})
public abstract class ExplosionMixin {
    @Shadow
    @Final
    private World world;

    @Shadow
    @Nullable
    public abstract LivingEntity getCausingEntity();

    @Inject(
            method = {"affectWorld"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
                    shift = At.Shift.AFTER
            )},
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void asta$queueExploded(
            boolean particles,
            CallbackInfo ci,
            boolean beel,
            ObjectArrayList<Pair<ItemStack, BlockPos>> objectArrayList,
            boolean bl2,
            ObjectListIterator var5,
            BlockPos blockPos,
            BlockState state
    ) {
        for (WaterBarrier bar : (this.world.getComponent(AureumAstaDisks.KYRATOS)).barriers) {
            boolean bl = bar.shouldReverseBlockChange(blockPos) && (!(this.getCausingEntity() instanceof PlayerEntity pl) || !bar.isOwner(pl));
            if (bl && !(this.world.getBlockState(blockPos).getBlock() instanceof BarrierBlock)) {
                (this.world.getComponent(AureumAstaDisks.KYRATOS)).addBlockChange(new QueuedBlockChange(!state.isIn(CharterBlocks.BREAKABLES) && !(state.getBlock() instanceof PlantBlock) ? 200 : 204, blockPos, state));
                break;
            }
        }
    }

    @WrapWithCondition(
            method = {"affectWorld"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;dropStack(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)V"
            )}
    )
    private boolean asta$cancelBlockDrop(World world, BlockPos pos, ItemStack stack) {
        boolean bl = false;

        for (WaterBarrier bar : (world.getComponent(AureumAstaDisks.KYRATOS)).barriers) {
            bl = bar.shouldReverseBlockChange(pos)
                    && (!(this.getCausingEntity() instanceof PlayerEntity pl) || !bar.isOwner(pl))
                    && !(world.getBlockState(pos).getBlock() instanceof BarrierBlock);
            if (bl) {
                break;
            }
        }

        return !bl;
    }
}
