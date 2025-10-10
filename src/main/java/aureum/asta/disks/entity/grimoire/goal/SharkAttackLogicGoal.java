package aureum.asta.disks.entity.grimoire.goal;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.entity.grimoire.SharkEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.MathHelper;

import java.util.EnumSet;

public class SharkAttackLogicGoal extends Goal {
    private final SharkEntity shark;
    private int delay;
    private double targetX;
    private double targetY;
    private double targetZ;

    public SharkAttackLogicGoal(SharkEntity entity) {
        this.shark = entity;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
    }

    public boolean canStart() {
        LivingEntity target = this.shark.getTarget();
        return target != null && target.isAlive();
    }

    public void start() {
        this.delay = 0;
    }

    public void stop() {
        this.shark.getNavigation().stop();
    }

    public void tick() {
        LivingEntity target = this.shark.getTarget();
              if (target != null) {
            double distance = this.shark.squaredDistanceTo(this.targetX, this.targetY, this.targetZ);
            if (--this.delay <= 0 && (this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0 || target.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= 1.0) || this.shark.getNavigation().isIdle()) {
                this.targetX = target.getX();
                this.targetY = target.getY();
                this.targetZ = target.getZ();
                this.delay = 4 + this.shark.getRandom().nextInt(6);
                if (distance > 1024.0) {
                    this.delay += 10;
                } else if (distance > 256.0) {
                    this.delay += 5;
                }

                this.shark.getMoveControl().moveTo(targetX, targetY, targetZ, (double)2.0F);
            }

            this.shark.setAttackState(2);
            this.shark.dashSlashTicks = 0;
        }
    }
}