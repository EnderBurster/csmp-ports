package aureum.asta.disks.entity.grimoire.goal;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.entity.grimoire.SharkEntity;
import aureum.asta.disks.ports.mason.init.MasonObjects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.List;

public class SharkDashSlashGoal extends Goal {
    private final SharkEntity shark;
    
    public SharkDashSlashGoal(SharkEntity entity) {
        this.shark = entity;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
    }

    public boolean canStart() {
        return this.shark.getAttackState() == 2 && this.shark.getTarget() != null;
    }

    public void tick() {
        int ticks = this.shark.dashSlashTicks;
        LivingEntity target = this.shark.getTarget();
        this.shark.lookAtEntity(this.shark.getTarget(), 80.0F, 80.0F);
        if (ticks == 9 || ticks == 12 || ticks == 15) {
            Vec3d vec3d = this.shark.getVelocity();
            Vec3d vec3d2 = new Vec3d(target.getX() - this.shark.getX(), target.getY() - this.shark.getY(), target.getZ() - this.shark.getZ());
            vec3d2 = vec3d2.normalize().multiply(1.0).add(vec3d);
            this.shark.setVelocity(vec3d2.x, vec3d2.y, vec3d2.z);

            this.shark.setVelocity(vec3d2.x, vec3d2.y, vec3d2.z);
        }

        if (ticks == 10 || ticks == 13 || ticks == 15) {
            this.shark.playSound(SoundEvents.ENTITY_PHANTOM_BITE, 1.0F, 1.0F);
            List<LivingEntity> entities = this.shark.getWorld().getEntitiesByClass(LivingEntity.class, this.shark.getBoundingBox().expand(4.0, 3.0, 4.0), (livingEntity) -> {
                if (livingEntity != this.shark && livingEntity != this.shark.getOwner()) {
                    label19: {
                        if (livingEntity instanceof SharkEntity otherShark) {
                            if (otherShark.getOwner() == this.shark.getOwner()) {
                                break label19;
                            }
                        }

                        if (this.shark.distanceTo(livingEntity) <= 4.0F + livingEntity.getWidth() / 2.0F && livingEntity.getY() <= this.shark.getY() + 3.0) {
                            return true;
                        }
                    }
                }

                return false;
            });

            for (LivingEntity entity : entities) {
                Vec3d vec = entity.getPos().subtract(this.shark.getPos()).normalize().negate();
                entity.takeKnockback(1.0, vec.x, vec.z);
                this.shark.tryAttack(entity);
            }
        }
        
    }
}