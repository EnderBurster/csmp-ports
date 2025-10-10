package aureum.asta.disks.ports.mason.entity.goal;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.charter.Charter;
import aureum.asta.disks.ports.mason.entity.TameableHostileEntity;
import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;

public class TamedAttackWithOwnerGoal<T extends TameableHostileEntity> extends TrackTargetGoal {
    private final T tamed;
    private LivingEntity attacking;
    private int lastAttackTime;

    public TamedAttackWithOwnerGoal(T tamed) {
        super((MobEntity)tamed, false);
        this.tamed = tamed;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    public boolean canStart() {
        if (this.tamed.isTamed()) {
            LivingEntity livingEntity = this.tamed.getOwner();
            if (livingEntity == null) {
                return false;
            } else {
                if(this.attacking == null || Charter.bannedUuids.contains(this.attacking.getUuid())) return false;
                int i = livingEntity.getLastAttackTime();
                return i != this.lastAttackTime && this.canTrack(this.attacking, TargetPredicate.DEFAULT) && this.tamed.canAttackWithOwner(this.attacking, livingEntity);
            }
        } else {
            return false;
        }
    }

    public void start() {
        this.mob.setTarget(this.attacking);
        LivingEntity livingEntity = this.tamed.getOwner();
        if (livingEntity != null) {
            this.lastAttackTime = livingEntity.getLastAttackTime();
        }

        super.start();
    }
}
