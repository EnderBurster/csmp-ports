package aureum.asta.disks.ports.charter.common.util;

import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import java.awt.Color;
import java.util.Date;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class CharterUtils {
   public static EntityHitResult hitscanEntity(World world, LivingEntity user, double distance, Predicate<Entity> targetPredicate) {
      Vec3d vec3d = user.getCameraPosVec(1.0F);
      Vec3d vec3d2 = user.getRotationVec(1.0F);
      Vec3d vec3d3 = vec3d.add(vec3d2.x * distance, vec3d2.y * distance, vec3d2.z * distance);
      double squareDistance = Math.pow(distance, 2.0);
      return ProjectileUtil.getEntityCollision(
         world, user, vec3d, vec3d3, user.getBoundingBox().stretch(vec3d2.multiply(squareDistance)).expand(1.0, 1.0, 1.0), targetPredicate
      );
   }

   public static BlockHitResult hitscanBlock(World world, LivingEntity user, double distance, FluidHandling fluidHandling, Predicate<Block> targetPredicate) {
      Vec3d vec3d = user.getCameraPosVec(1.0F);
      Vec3d vec3d2 = user.getRotationVec(1.0F);
      Vec3d vec3d3 = vec3d.add(vec3d2.x * distance, vec3d2.y * distance, vec3d2.z * distance);
      double squareDistance = Math.pow(distance, 2.0);
      vec3d3.multiply(squareDistance);
      return world.raycast(new RaycastContext(vec3d, vec3d3, ShapeType.OUTLINE, fluidHandling, user));
   }

   public static Color interpolateColour(Easing easing, float delta, Color c1, Color c2) {
      int r = MathHelper.clamp((int)MathHelper.lerp(easing.ease(delta, 0.0F, 1.0F, 1.0F), (float)c1.getRed(), (float)c2.getRed()), 0, 255);
      int g = MathHelper.clamp((int)MathHelper.lerp(easing.ease(delta, 0.0F, 1.0F, 1.0F), (float)c1.getGreen(), (float)c2.getGreen()), 0, 255);
      int b = MathHelper.clamp((int)MathHelper.lerp(easing.ease(delta, 0.0F, 1.0F, 1.0F), (float)c1.getBlue(), (float)c2.getBlue()), 0, 255);
      return new Color(r, g, b);
   }

   public static void attemptBan(Entity entity, MinecraftServer server,  int minutes, String reason) {
      if(entity instanceof ServerPlayerEntity player && player.getServer() != null)
      {
         PlayerManager pm = server.getPlayerManager();

         Date expires = minutes <= 0 ? null : new Date(System.currentTimeMillis() + (minutes * 60L * 1000L));

         BannedPlayerEntry banEntry = new BannedPlayerEntry(player.getGameProfile(), new Date(), "Charter", expires, reason);

         pm.getUserBanList().add(banEntry);
         player.networkHandler.disconnect(net.minecraft.text.Text.literal(reason));
      }
   }
}
