package aureum.asta.disks.mixin;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.cca.blocks.WaterBarrier;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(
        value = {Entity.class},
        priority = 1000
)
public abstract class EnityMixin {
    /*@Inject(
            method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Vec3d;lengthSquared()D"
            )
    )
    private void asta$injectCustomCollisions(Vec3d movement, CallbackInfoReturnable<Vec3d> cir, @Local List<VoxelShape> list) {
        VoxelShape forcefield = Border.voxelShape;
        //AureumAstaDisks.LOGGER.info("asta$injectCustomCollisions fired, list size={}", list.size());
        if (forcefield != null && Border.borderBox.contains(((Entity)(Object)this).getPos()) && !hasPassItem((Entity)(Object)this)) {
            list.add(forcefield);
            AureumAstaDisks.LOGGER.info("Forcefield added");
        }
    }*/

    @Shadow public World world;

    @Shadow protected abstract Vec3d positionInPortal(Direction.Axis portalAxis, BlockLocating.Rectangle portalRect);

    @Shadow public abstract boolean entityDataRequiresOperator();

    /*@WrapOperation(
            method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getEntityCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Ljava/util/List;"
            )
    )
    private List<VoxelShape> asta$addCustomCollisions(World world, Entity entity, Box box, Operation<List> original) {
        List<VoxelShape> base = original.call(world, entity, box);

        ArrayList<BlockPos> blockPositions = Border.getActiveBlocks();
        if (blockPositions.isEmpty()) return original.call(world, entity, box);

        for (BlockPos pos : blockPositions) {
            if (pos == null || (world.getBlockEntity(pos) instanceof BarrierBlockEntity barrier && !barrier.isActive()) || !(world.getBlockEntity(pos) instanceof BarrierBlockEntity barrier)) return original.call(world, entity, box);
            Box barrierBox = new Box(pos).expand(barrier.getSize());
            VoxelShape barrierShape = barrier.createHollowShape(barrierBox);

            if(barrierShape != null && barrierBox.contains(entity.getPos()) && !hasPassItem(entity))
            {
                List<VoxelShape> mutable = new ArrayList<>(base);
                mutable.add(barrierShape);
                AureumAstaDisks.LOGGER.info("Test");
                return mutable;
            }
        };

        /*VoxelShape forcefield = Border.voxelShape;

        AureumAstaDisks.LOGGER.info("Forcefield: {}", forcefield);

        if (forcefield != null && Border.borderBox.contains(entity.getPos()) && !hasPassItem(entity)) {
            List<VoxelShape> mutable = new ArrayList<>(base);
            mutable.add(forcefield);
            AureumAstaDisks.LOGGER.info("Test");
            return mutable;
        }

        return original.call(world, entity, box);
    }*/

    @WrapOperation(
            method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getEntityCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Ljava/util/List;"
            )
    )
    private List<VoxelShape> asta$addCustomCollisions(World world, Entity entity, Box box, Operation<List> original) {
        List<VoxelShape> base = original.call(world, entity, box);
        List<VoxelShape> mutable = new ArrayList<>(base);

        for (WaterBarrier bar : (world.getComponent(AureumAstaDisks.KYRATOS)).barriers) {
            boolean checkInside = bar.isReversed() != bar.isPosInside(entity.getPos());
            if (bar.isActive() && checkInside && !(world.getComponent(AureumAstaDisks.KYRATOS).hasPassItem(entity))) {
                mutable.add(bar.createHollowShape());
            }
        }

        return mutable;
    }
}
