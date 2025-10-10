package aureum.asta.disks.entity.grimoire.goal;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.entity.grimoire.SharkEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.EnumSet;

public class SharkChargeTargetGoal extends Goal {
    private final SharkEntity shark;
    private int delay;
    private double targetX = 0;
    private double targetY = 0;
    private double targetZ = 0;

    public SharkChargeTargetGoal(SharkEntity entity) {
        this.shark = entity;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    public boolean canStart() {
        LivingEntity target = this.shark.getTarget();
        return target != null && target.isAlive();
    }

    public boolean shouldContinue() {
        return this.shark.getMoveControl().isMoving() && this.shark.getAttackState() == 1 && this.shark.getTarget() != null && this.shark.getTarget().isAlive();
    }

    public void start() {
        LivingEntity livingEntity = this.shark.getTarget();
        if (livingEntity != null) {
            Vec3d vec3d = livingEntity.getEyePos();
            this.shark.getMoveControl().moveTo(vec3d.x, vec3d.y, vec3d.z, (double)1.0F);
        }

        this.shark.setAttackState(1);
        this.shark.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
    }

    public void stop() {
        this.shark.setAttackState(0);
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity livingEntity = this.shark.getTarget();
        if (livingEntity != null) {
            double distance = this.shark.squaredDistanceTo(this.targetX, this.targetY, this.targetZ);
            if (--this.delay <= 0 && (this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0 || livingEntity.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= 1.0) || this.shark.getNavigation().isIdle())
            {
                this.targetX = livingEntity.getX();
                this.targetY = livingEntity.getY();
                this.targetZ = livingEntity.getZ();

                this.delay = 4 + this.shark.getRandom().nextInt(6);
                if(distance > 1024.0)
                {
                    this.delay += 10;
                }
                else if(distance > 256.0)
                {
                    this.delay += 5;
                }

                if (!this.shark.getNavigation().startMovingTo(livingEntity, 0.5)) {
                    this.delay += 15;
                }
            }


                if (this.shark.getBoundingBox().intersects(livingEntity.getBoundingBox())) {
                AureumAstaDisks.LOGGER.info("Entity: {}", livingEntity);
                this.shark.tryAttack(livingEntity);
                this.shark.setAttackState(0);
            } else {
                AureumAstaDisks.LOGGER.info("Entity (Doesn't intersect): {}", livingEntity);
                double d = this.shark.squaredDistanceTo(livingEntity);
                if (d < (double)9.0F) {
                    Vec3d vec3d = livingEntity.getEyePos();
                    this.shark.getMoveControl().moveTo(vec3d.x, vec3d.y, vec3d.z, (double)1.0F);
                }
            }

        }
    }
}
