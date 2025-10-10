package aureum.asta.disks.mixin.barrier;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.cca.blocks.WaterBarrier;
import aureum.asta.disks.cca.world.KyratosWorldComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(ProjectileUtil.class)
public class ProjectileUtilMixin {

    @Inject(method = "getCollision", at = @At("RETURN"), cancellable = true)
    private static void asta$collideWithBarrier(Entity entity, Predicate<Entity> predicate, CallbackInfoReturnable<HitResult> cir)
    {
        if(entity == null || entity.world == null)
        {
            return;
        }

        Vec3d start = entity.getPos();
        Vec3d end = start.add(entity.getVelocity());

        KyratosWorldComponent worldComponent = entity.world.getComponent(AureumAstaDisks.KYRATOS);

        for(WaterBarrier barrier : worldComponent.barriers)
        {
            if(worldComponent.atBarrier(end, barrier)
                    && entity instanceof ProjectileEntity projectile
                    && projectile.getOwner() != null
                    && !worldComponent.hasPassItem(entity)
                    && !worldComponent.maybeInOutBarrier(projectile.getOwner().getPos(), barrier))
            {
                BlockHitResult barrierHit = new BlockHitResult(
                        end,
                        Direction.getFacing(entity.getVelocity().x, entity.getVelocity().y, entity.getVelocity().z),
                        new BlockPos((int)end.getX(), (int)end.getY(), (int)end.getZ()),
                        true
                );
                cir.setReturnValue(barrierHit);
                break;
            }
        }

        /*if (entity.world.getComponent(AureumAstaDisks.KYRATOS).atBarrier(end)
                && entity instanceof ProjectileEntity projectile
                && projectile.getOwner() != null
                && !entity.world.getComponent(AureumAstaDisks.KYRATOS).hasPassItem(projectile.getOwner())
                && !entity.world.getComponent(AureumAstaDisks.KYRATOS).insideBarrier(projectile.getOwner().getPos())) {
            BlockHitResult barrierHit = new BlockHitResult(
                    end,
                    Direction.getFacing(entity.getVelocity().x, entity.getVelocity().y, entity.getVelocity().z),
                    new BlockPos((int)end.getX(), (int)end.getY(), (int)end.getZ()),
                    true
            );
            cir.setReturnValue(barrierHit);
        }*/
    }

}
