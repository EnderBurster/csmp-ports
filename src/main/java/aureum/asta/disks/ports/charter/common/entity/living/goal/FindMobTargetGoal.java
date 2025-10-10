package aureum.asta.disks.ports.charter.common.entity.living.goal;

import aureum.asta.disks.ports.charter.common.entity.living.BloodflyEntity;
import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

public class FindMobTargetGoal extends Goal {
   private final BloodflyEntity phantom;
   private int delay = 20;

   public FindMobTargetGoal(BloodflyEntity phantomEntity) {
      this.phantom = phantomEntity;
   }

   public boolean canStart() {
      if (this.delay > 0) {
         this.delay--;
         return false;
      } else {
         this.delay = 60;
         List<LivingEntity> list = this.phantom
            .world
            .getEntitiesByClass(LivingEntity.class, this.phantom.getBoundingBox().expand(16.0, 64.0, 16.0), livingEntityx -> {
               if (livingEntityx instanceof PlayerEntity pl && (this.phantom.ownerUuid == null || pl.getUuid() == this.phantom.ownerUuid)) {
                  return true;
               }

               return false;
            });
         if (!list.isEmpty()) {
            list.sort(Comparator.comparing(Entity::getY).reversed());

            for (LivingEntity livingEntity : list) {
               if (this.phantom.isTarget(livingEntity, TargetPredicate.DEFAULT)) {
                  this.phantom.setTarget(livingEntity);
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean shouldContinue() {
      LivingEntity livingEntity = this.phantom.getTarget();
      return livingEntity != null ? this.phantom.isTarget(livingEntity, TargetPredicate.DEFAULT) : false;
   }
}
