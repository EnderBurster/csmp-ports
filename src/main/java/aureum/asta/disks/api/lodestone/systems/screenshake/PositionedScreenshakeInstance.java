package aureum.asta.disks.api.lodestone.systems.screenshake;

import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import org.joml.Vector3f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3fc;

public class PositionedScreenshakeInstance extends ScreenshakeInstance {
   public Vec3d position;
   public float falloffDistance;
   public float maxDistance;
   public float minDot;
   public final Easing falloffEasing;

   public PositionedScreenshakeInstance(int duration, Vec3d position, float falloffDistance, float minDot, float maxDistance, Easing falloffEasing) {
      super(duration);
      this.position = position;
      this.falloffDistance = falloffDistance;
      this.minDot = minDot;
      this.maxDistance = maxDistance;
      this.falloffEasing = falloffEasing;
   }

   @Override
   public float updateIntensity(Camera camera, Random random) {
      float intensity = super.updateIntensity(camera, random);
      float distance = (float)this.position.distanceTo(camera.getPos());
      if (distance > this.maxDistance) {
         return 0.0F;
      } else {
         float distanceMultiplier = 1.0F;
         if (distance > this.falloffDistance) {
            float remaining = this.maxDistance - this.falloffDistance;
            float current = distance - this.falloffDistance;
            distanceMultiplier = 1.0F - current / remaining;
         }

         Vector3f lookDirection = camera.getHorizontalPlane();
         Vec3d directionToScreenshake = this.position.subtract(camera.getPos()).normalize();
         float angle = Math.max(this.minDot, lookDirection.dot(new Vector3f((float) directionToScreenshake.getX(), (float) directionToScreenshake.getY(), (float) directionToScreenshake.getZ())));
         return intensity * distanceMultiplier * angle;
      }
   }
}
