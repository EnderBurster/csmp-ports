package aureum.asta.disks.ports.amarite.mialib.raycasting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.jetbrains.annotations.NotNull;

public interface MRaycasting {
   BiPredicate<PlayerEntity, Entity> ANY_PLAYER_AND_ENTITY = (p, e) -> true;
   Predicate<Entity> ANY_ENTITY = e -> true;

   @NotNull
   static List<Entity> raycast(@NotNull PlayerEntity entity, double distance) {
      return raycast(entity, distance, ANY_PLAYER_AND_ENTITY);
   }

   @NotNull
   static List<Entity> raycast(@NotNull PlayerEntity entity, double distance, BiPredicate<PlayerEntity, Entity> filter) {
      return raycast(entity, distance, filter, 0);
   }

   @NotNull
   static List<Entity> raycast(@NotNull PlayerEntity entity, double distance, BiPredicate<PlayerEntity, Entity> filter, int maxHits) {
      return raycast(entity, distance, filter, maxHits, 0.0);
   }

   @NotNull
   static List<Entity> raycast(@NotNull PlayerEntity entity, double distance, BiPredicate<PlayerEntity, Entity> filter, int maxHits, double rayRadius) {
      return raycast(
         entity.getWorld(),
         entity.getEyePos(),
         anglesToVector((double)entity.getPitch(1.0F), (double)entity.getYaw(1.0F)),
         distance,
         e -> filter.test(entity, e),
         rayRadius,
         maxHits
      );
   }

   @NotNull
   static List<Entity> raycast(@NotNull World world, @NotNull Vec3d startPos, @NotNull Vec3d angle, double distance) {
      return raycast(world, startPos, angle, distance, ANY_ENTITY);
   }

   @NotNull
   static List<Entity> raycast(
      @NotNull World world, @NotNull Vec3d startPos, @NotNull Vec3d angle, double distance, @NotNull Predicate<Entity> filter
   ) {
      return raycast(world, startPos, angle, distance, filter, 0.0);
   }

   @NotNull
   static List<Entity> raycast(
      @NotNull World world,
      @NotNull Vec3d startPos,
      @NotNull Vec3d angle,
      double distance,
      @NotNull Predicate<Entity> filter,
      double rayRadius
   ) {
      return raycast(world, startPos, angle, distance, filter, rayRadius, 0);
   }

   @NotNull
   static List<Entity> raycast(
      @NotNull World world,
      @NotNull Vec3d startPos,
      @NotNull Vec3d angle,
      double distance,
      @NotNull Predicate<Entity> filter,
      double rayRadius,
      int maxHits
   ) {
      Vec3d endPosition = startPos.add(angle.multiply(distance));
      List<Entity> hitEntities = new ArrayList<>();

      for (Entity target : world.getOtherEntities(null, Box.of(startPos, 0.1, 0.1, 0.1).expand(distance, distance, distance), filter)) {
         boolean intersection = intersects(startPos, endPosition, target, rayRadius);
         if (intersection) {
            boolean visible = false;

            for (Vec3d pos : new Vec3d[]{
               target.getPos(),
               target.getPos().add(0.0, (double)(target.getHeight() / 2.0F), 0.0),
               target.getPos().add(0.0, (double)target.getHeight(), 0.0)
            }) {
               if (world.raycast(new RaycastContext(startPos, pos, ShapeType.COLLIDER, FluidHandling.NONE, target)).getType()
                  == Type.MISS) {
                  visible = true;
                  break;
               }
            }

            if (visible) {
               hitEntities.add(target);
               if (maxHits > 0 && hitEntities.size() >= maxHits) {
                  break;
               }
            }
         }
      }

      return hitEntities;
   }

   @NotNull
   static Vec3d anglesToVector(double pitch, double yaw) {
      double x = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
      double y = -Math.sin(Math.toRadians(pitch));
      double z = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
      return new Vec3d(x, y, z);
   }

   static boolean intersects(Vec3d start, @NotNull Vec3d end, @NotNull Entity entity, double rayRadius) {
      Vec3d entityMin = entity.getPos()
         .subtract((double)(entity.getWidth() / 2.0F) + rayRadius, rayRadius, (double)(entity.getWidth() / 2.0F) + rayRadius);
      Vec3d entityMax = entity.getPos()
         .add(
            (double)(entity.getWidth() / 2.0F) + rayRadius, (double)entity.getHeight() + rayRadius, (double)(entity.getWidth() / 2.0F) + rayRadius
         );
      return intersects(start, end, new Box(entityMin, entityMax), rayRadius);
   }

   static boolean intersects(Vec3d start, @NotNull Vec3d end, @NotNull Box box, double rayRadius) {
      Vec3d direction = end.subtract(start).normalize();
      Vec3d boxMin = new Vec3d(box.minX - rayRadius, box.minY - rayRadius, box.minZ - rayRadius);
      Vec3d boxMax = new Vec3d(box.maxX + rayRadius, box.maxY + rayRadius, box.maxZ + rayRadius);
      double tMin = 0.0;
      double tMax = Double.MAX_VALUE;

      for (Axis axis : Axis.values()) {
         if (Math.abs(direction.getComponentAlongAxis(axis)) < 1.0E-8) {
            if (start.getComponentAlongAxis(axis) < boxMin.getComponentAlongAxis(axis) || start.getComponentAlongAxis(axis) > boxMax.getComponentAlongAxis(axis)) {
               return false;
            }
         } else {
            double ood = 1.0 / direction.getComponentAlongAxis(axis);
            double t1 = (boxMin.getComponentAlongAxis(axis) - start.getComponentAlongAxis(axis)) * ood;
            double t2 = (boxMax.getComponentAlongAxis(axis) - start.getComponentAlongAxis(axis)) * ood;
            if (t1 > t2) {
               double temp = t1;
               t1 = t2;
               t2 = temp;
            }

            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) {
               return false;
            }
         }
      }

      return true;
   }
}
