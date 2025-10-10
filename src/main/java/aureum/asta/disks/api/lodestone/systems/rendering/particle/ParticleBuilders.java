package aureum.asta.disks.api.lodestone.systems.rendering.particle;

import aureum.asta.disks.api.lodestone.handlers.ScreenParticleHandler;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.ScreenParticleEffect;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.ScreenParticleType;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.base.ScreenParticle;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.world.WorldParticleEffect;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Box;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.util.math.Direction.Axis;

public class ParticleBuilders {
   public static WorldParticleBuilder create(ParticleType<?> type) {
      return new WorldParticleBuilder(type);
   }

   public static WorldParticleBuilder create(ParticleEffect type) {
      return new WorldParticleBuilder((ParticleType<?>)type);
   }

   public static ScreenParticleBuilder create(ScreenParticleType<?> type) {
      return new ScreenParticleBuilder(type);
   }

   public static class ScreenParticleBuilder {
      static Random random = new Random();
      ScreenParticleType<?> type;
      ScreenParticleEffect data;
      double vx = 0.0;
      double vy = 0.0;
      double dx = 0.0;
      double dy = 0.0;
      double maxXSpeed = 0.0;
      double maxYSpeed = 0.0;
      double maxXDist = 0.0;
      double maxYDist = 0.0;

      protected ScreenParticleBuilder(ScreenParticleType<?> type) {
         this.type = type;
         this.data = new ScreenParticleEffect(type);
      }

      public ScreenParticleBuilder overrideAnimator(SimpleParticleEffect.Animator animator) {
         this.data.animator = animator;
         return this;
      }

      public ScreenParticleBuilder overrideRenderType(ParticleTextureSheet renderType) {
         this.data.textureSheet = renderType;
         return this;
      }

      public ScreenParticleBuilder overrideRemovalProtocol(SimpleParticleEffect.SpecialRemovalProtocol removalProtocol) {
         this.data.removalProtocol = removalProtocol;
         return this;
      }

      public ScreenParticleBuilder overrideRenderOrder(ScreenParticle.RenderOrder renderOrder) {
         this.data.renderOrder = renderOrder;
         return this;
      }

      public ScreenParticleBuilder centerOnStack(ItemStack stack) {
         this.data.stack = stack;
         return this;
      }

      public ScreenParticleBuilder centerOnStack(ItemStack stack, float xOffset, float yOffset) {
         this.data.stack = stack;
         this.data.xOffset = xOffset;
         this.data.yOffset = yOffset;
         return this;
      }

      public ScreenParticleBuilder setColorEasing(Easing easing) {
         this.data.colorCurveEasing = easing;
         return this;
      }

      public ScreenParticleBuilder setColorCoefficient(float colorCoefficient) {
         this.data.colorCoefficient = colorCoefficient;
         return this;
      }

      public ScreenParticleBuilder setColor(float r, float g, float b) {
         return this.setColor(r, g, b, this.data.alpha1, r, g, b, this.data.alpha2);
      }

      public ScreenParticleBuilder setColor(float r, float g, float b, float a) {
         return this.setColor(r, g, b, a, r, g, b, a);
      }

      public ScreenParticleBuilder setColor(float r, float g, float b, float a1, float a2) {
         return this.setColor(r, g, b, a1, r, g, b, a2);
      }

      public ScreenParticleBuilder setColor(float r1, float g1, float b1, float r2, float g2, float b2) {
         return this.setColor(r1, g1, b1, this.data.alpha1, r2, g2, b2, this.data.alpha2);
      }

      public ScreenParticleBuilder setColor(float r1, float g1, float b1, float r2, float g2, float b2, float a) {
         return this.setColor(r1, g1, b1, a, r2, g2, b2, a);
      }

      public ScreenParticleBuilder setColor(float r1, float g1, float b1, float a1, float r2, float g2, float b2, float a2) {
         this.data.r1 = r1;
         this.data.g1 = g1;
         this.data.b1 = b1;
         this.data.alpha1 = a1;
         this.data.r2 = r2;
         this.data.g2 = g2;
         this.data.b2 = b2;
         this.data.alpha2 = a2;
         return this;
      }

      public ScreenParticleBuilder setColor(Color c1, Color c2) {
         this.data.r1 = (float)c1.getRed() / 255.0F;
         this.data.g1 = (float)c1.getGreen() / 255.0F;
         this.data.b1 = (float)c1.getBlue() / 255.0F;
         this.data.r2 = (float)c2.getRed() / 255.0F;
         this.data.g2 = (float)c2.getGreen() / 255.0F;
         this.data.b2 = (float)c2.getBlue() / 255.0F;
         return this;
      }

      public ScreenParticleBuilder setAlphaEasing(Easing startEasing, Easing endEasing) {
         this.data.alphaCurveStartEasing = startEasing;
         this.data.alphaCurveEndEasing = endEasing;
         return this;
      }

      public ScreenParticleBuilder setAlphaEasing(Easing easing) {
         this.data.alphaCurveStartEasing = easing;
         return this;
      }

      public ScreenParticleBuilder setAlphaCoefficient(float alphaCoefficient) {
         this.data.alphaCoefficient = alphaCoefficient;
         return this;
      }

      public ScreenParticleBuilder setAlpha(float alpha) {
         return this.setAlpha(alpha, alpha);
      }

      public ScreenParticleBuilder setAlpha(float alpha1, float alpha2) {
         return this.setAlpha(alpha1, alpha2, alpha2);
      }

      public ScreenParticleBuilder setAlpha(float alpha1, float alpha2, float alpha3) {
         this.data.alpha1 = alpha1;
         this.data.alpha2 = alpha2;
         this.data.alpha3 = alpha3;
         return this;
      }

      public ScreenParticleBuilder setScaleEasing(Easing startEasing, Easing endEasing) {
         this.data.scaleCurveStartEasing = startEasing;
         this.data.scaleCurveEndEasing = endEasing;
         return this;
      }

      public ScreenParticleBuilder setScaleEasing(Easing easing) {
         this.data.scaleCurveStartEasing = easing;
         return this;
      }

      public ScreenParticleBuilder setScaleCoefficient(float scaleCoefficient) {
         this.data.scaleCoefficient = scaleCoefficient;
         return this;
      }

      public ScreenParticleBuilder setScale(float scale) {
         return this.setScale(scale, scale);
      }

      public ScreenParticleBuilder setScale(float scale1, float scale2) {
         return this.setScale(scale1, scale2, scale2);
      }

      public ScreenParticleBuilder setScale(float scale1, float scale2, float scale3) {
         this.data.scale1 = scale1;
         this.data.scale2 = scale2;
         this.data.scale3 = scale3;
         return this;
      }

      public ScreenParticleBuilder setGravity(float gravity) {
         this.data.gravity = gravity;
         return this;
      }

      public ScreenParticleBuilder enableNoClip() {
         this.data.noClip = true;
         return this;
      }

      public ScreenParticleBuilder disableNoClip() {
         this.data.noClip = false;
         return this;
      }

      public ScreenParticleBuilder setSpinEasing(Easing easing) {
         this.data.spinCurveStartEasing = easing;
         return this;
      }

      public ScreenParticleBuilder setSpinEasing(Easing startEasing, Easing endEasing) {
         this.data.spinCurveStartEasing = startEasing;
         this.data.spinCurveEndEasing = endEasing;
         return this;
      }

      public ScreenParticleBuilder setSpinCoefficient(float spinCoefficient) {
         this.data.spinCoefficient = spinCoefficient;
         return this;
      }

      public ScreenParticleBuilder setSpinOffset(float spinOffset) {
         this.data.spinOffset = spinOffset;
         return this;
      }

      public ScreenParticleBuilder setSpin(float spin) {
         return this.setSpin(spin, spin);
      }

      public ScreenParticleBuilder setSpin(float spin1, float spin2) {
         return this.setSpin(spin1, spin2, spin2);
      }

      public ScreenParticleBuilder setSpin(float spin1, float spin2, float spin3) {
         this.data.spin1 = spin1;
         this.data.spin2 = spin2;
         this.data.spin3 = spin3;
         return this;
      }

      public ScreenParticleBuilder setLifetime(int lifetime) {
         this.data.lifetime = lifetime;
         return this;
      }

      public ScreenParticleBuilder setMotionCoefficient(float motionCoefficient) {
         this.data.motionCoefficient = motionCoefficient;
         return this;
      }

      public ScreenParticleBuilder randomMotion(double maxSpeed) {
         return this.randomMotion(maxSpeed, maxSpeed);
      }

      public ScreenParticleBuilder randomMotion(double maxXSpeed, double maxYSpeed) {
         this.maxXSpeed = maxXSpeed;
         this.maxYSpeed = maxYSpeed;
         return this;
      }

      public ScreenParticleBuilder addMotion(double vx, double vy) {
         this.vx += vx;
         this.vy += vy;
         return this;
      }

      public ScreenParticleBuilder setMotion(double vx, double vy) {
         this.vx = vx;
         this.vy = vy;
         return this;
      }

      public ScreenParticleBuilder setForcedMotion(Vec2f startingVelocity, Vec2f endingMotion) {
         this.data.forcedMotion = true;
         this.data.motionStyle = SimpleParticleEffect.MotionStyle.START_TO_END;
         this.data.startingVelocity = startingVelocity;
         this.data.endingMotion = endingMotion;
         return this;
      }

      public ScreenParticleBuilder setForcedMotion(Vec2f endingMotion) {
         this.data.forcedMotion = true;
         this.data.motionStyle = SimpleParticleEffect.MotionStyle.CURRENT_TO_END;
         this.data.endingMotion = endingMotion;
         return this;
      }

      public ScreenParticleBuilder disableForcedMotion() {
         this.data.forcedMotion = false;
         return this;
      }

      public ScreenParticleBuilder randomOffset(double maxDistance) {
         return this.randomOffset(maxDistance, maxDistance);
      }

      public ScreenParticleBuilder randomOffset(double maxXDist, double maxYDist) {
         this.maxXDist = maxXDist;
         this.maxYDist = maxYDist;
         return this;
      }

      public ScreenParticleBuilder spawnCircle(double x, double y, double distance, double currentCount, double totalCount) {
         double xSpeed = (double)random.nextFloat() * this.maxXSpeed;
         double ySpeed = (double)random.nextFloat() * this.maxYSpeed;
         double theta = (Math.PI * 2) / totalCount;
         double finalAngle = currentCount / totalCount + theta * currentCount;
         double dx2 = distance * Math.cos(finalAngle);
         double dz2 = distance * Math.sin(finalAngle);
         Vec3d vector2f = new Vec3d(dx2, 0.0, dz2);
         this.vx = vector2f.x * xSpeed;
         double yaw2 = (double)random.nextFloat() * Math.PI * 2.0;
         double pitch2 = (double)random.nextFloat() * Math.PI - (Math.PI / 2);
         double xDist = (double)random.nextFloat() * this.maxXDist;
         double yDist = (double)random.nextFloat() * this.maxYDist;
         this.dx = Math.sin(yaw2) * Math.cos(pitch2) * xDist;
         this.dy = Math.sin(pitch2) * yDist;
         this.data.xOrigin = (float)x;
         this.data.yOrigin = (float)y;
         ScreenParticleHandler.addParticle(this.data, x + this.dx + dx2, y + this.dy + dz2, this.vx, ySpeed);
         return this;
      }

      public ScreenParticleBuilder spawn(double x, double y) {
         double yaw = (double)random.nextFloat() * Math.PI * 2.0;
         double pitch = (double)random.nextFloat() * Math.PI - (Math.PI / 2);
         double xSpeed = (double)random.nextFloat() * this.maxXSpeed;
         double ySpeed = (double)random.nextFloat() * this.maxYSpeed;
         this.vx = this.vx + Math.sin(yaw) * Math.cos(pitch) * xSpeed;
         this.vy = this.vy + Math.sin(pitch) * ySpeed;
         double yaw2 = (double)random.nextFloat() * Math.PI * 2.0;
         double pitch2 = (double)random.nextFloat() * Math.PI - (Math.PI / 2);
         double xDist = (double)random.nextFloat() * this.maxXDist;
         double yDist = (double)random.nextFloat() * this.maxYDist;
         this.dx = Math.sin(yaw2) * Math.cos(pitch2) * xDist;
         this.dy = Math.sin(pitch2) * yDist;
         this.data.xOrigin = (float)x;
         this.data.yOrigin = (float)y;
         ScreenParticleHandler.addParticle(this.data, x + this.dx, y + this.dy, this.vx, this.vy);
         return this;
      }

      public ScreenParticleBuilder repeat(double x, double y, int n) {
         for (int i = 0; i < n; i++) {
            this.spawn(x, y);
         }

         return this;
      }

      public ScreenParticleBuilder repeatCircle(double x, double y, double distance, int times) {
         for (int i = 0; i < times; i++) {
            this.spawnCircle(x, y, distance, (double)i, (double)times);
         }

         return this;
      }
   }

   public static class WorldParticleBuilder {
      static Random random = new Random();
      ParticleType<?> type;
      WorldParticleEffect data;
      double vx = 0.0;
      double vy = 0.0;
      double vz = 0.0;
      double dx = 0.0;
      double dy = 0.0;
      double dz = 0.0;
      double maxXSpeed = 0.0;
      double maxYSpeed = 0.0;
      double maxZSpeed = 0.0;
      double maxXDist = 0.0;
      double maxYDist = 0.0;
      double maxZDist = 0.0;

      protected WorldParticleBuilder(ParticleType<?> type) {
         this.type = type;
         this.data = new WorldParticleEffect(type);
      }

      public WorldParticleBuilder overrideAnimator(SimpleParticleEffect.Animator animator) {
         this.data.animator = animator;
         return this;
      }

      public WorldParticleBuilder overrideRenderType(ParticleTextureSheet renderType) {
         this.data.textureSheet = renderType;
         return this;
      }

      public WorldParticleBuilder overrideRemovalProtocol(SimpleParticleEffect.SpecialRemovalProtocol removalProtocol) {
         this.data.removalProtocol = removalProtocol;
         return this;
      }

      public WorldParticleBuilder setColorEasing(Easing easing) {
         this.data.colorCurveEasing = easing;
         return this;
      }

      public WorldParticleBuilder setColorCoefficient(float colorCoefficient) {
         this.data.colorCoefficient = colorCoefficient;
         return this;
      }

      public WorldParticleBuilder setColor(float r, float g, float b) {
         return this.setColor(r, g, b, this.data.alpha1, r, g, b, this.data.alpha2);
      }

      public WorldParticleBuilder setColor(float r, float g, float b, float a) {
         return this.setColor(r, g, b, a, r, g, b, a);
      }

      public WorldParticleBuilder setColor(float r, float g, float b, float a1, float a2) {
         return this.setColor(r, g, b, a1, r, g, b, a2);
      }

      public WorldParticleBuilder setColor(float r1, float g1, float b1, float r2, float g2, float b2) {
         return this.setColor(r1, g1, b1, this.data.alpha1, r2, g2, b2, this.data.alpha2);
      }

      public WorldParticleBuilder setColor(float r1, float g1, float b1, float r2, float g2, float b2, float a) {
         return this.setColor(r1, g1, b1, a, r2, g2, b2, a);
      }

      public WorldParticleBuilder setColor(float r1, float g1, float b1, float a1, float r2, float g2, float b2, float a2) {
         this.data.r1 = r1;
         this.data.g1 = g1;
         this.data.b1 = b1;
         this.data.alpha1 = a1;
         this.data.r2 = r2;
         this.data.g2 = g2;
         this.data.b2 = b2;
         this.data.alpha2 = a2;
         return this;
      }

      public WorldParticleBuilder setColor(Color c) {
         return this.setColor(c, c);
      }

      public WorldParticleBuilder setColor(Color c1, Color c2) {
         this.data.r1 = (float)c1.getRed() / 255.0F;
         this.data.g1 = (float)c1.getGreen() / 255.0F;
         this.data.b1 = (float)c1.getBlue() / 255.0F;
         this.data.r2 = (float)c2.getRed() / 255.0F;
         this.data.g2 = (float)c2.getGreen() / 255.0F;
         this.data.b2 = (float)c2.getBlue() / 255.0F;
         return this;
      }

      public WorldParticleBuilder setAlphaEasing(Easing startEasing, Easing endEasing) {
         this.data.alphaCurveStartEasing = startEasing;
         this.data.alphaCurveEndEasing = endEasing;
         return this;
      }

      public WorldParticleBuilder setAlphaEasing(Easing easing) {
         this.data.alphaCurveStartEasing = easing;
         return this;
      }

      public WorldParticleBuilder setAlphaCoefficient(float alphaCoefficient) {
         this.data.alphaCoefficient = alphaCoefficient;
         return this;
      }

      public WorldParticleBuilder setAlpha(float alpha) {
         return this.setAlpha(alpha, alpha);
      }

      public WorldParticleBuilder setAlpha(float alpha1, float alpha2) {
         return this.setAlpha(alpha1, alpha2, alpha2);
      }

      public WorldParticleBuilder setAlpha(float alpha1, float alpha2, float alpha3) {
         this.data.alpha1 = alpha1;
         this.data.alpha2 = alpha2;
         this.data.alpha3 = alpha3;
         return this;
      }

      public WorldParticleBuilder setScaleEasing(Easing startEasing, Easing endEasing) {
         this.data.scaleCurveStartEasing = startEasing;
         this.data.scaleCurveEndEasing = endEasing;
         return this;
      }

      public WorldParticleBuilder setScaleEasing(Easing easing) {
         this.data.scaleCurveStartEasing = easing;
         return this;
      }

      public WorldParticleBuilder setScaleCoefficient(float scaleCoefficient) {
         this.data.scaleCoefficient = scaleCoefficient;
         return this;
      }

      public WorldParticleBuilder setScale(float scale) {
         return this.setScale(scale, scale);
      }

      public WorldParticleBuilder setScale(float scale1, float scale2) {
         return this.setScale(scale1, scale2, scale2);
      }

      public WorldParticleBuilder setScale(float scale1, float scale2, float scale3) {
         this.data.scale1 = scale1;
         this.data.scale2 = scale2;
         this.data.scale3 = scale3;
         return this;
      }

      public WorldParticleBuilder setGravity(float gravity) {
         this.data.gravity = gravity;
         return this;
      }

      public WorldParticleBuilder enableNoClip() {
         this.data.noClip = true;
         return this;
      }

      public WorldParticleBuilder disableNoClip() {
         this.data.noClip = false;
         return this;
      }

      public WorldParticleBuilder setSpinEasing(Easing easing) {
         this.data.spinCurveStartEasing = easing;
         return this;
      }

      public WorldParticleBuilder setSpinEasing(Easing startEasing, Easing endEasing) {
         this.data.spinCurveStartEasing = startEasing;
         this.data.spinCurveEndEasing = endEasing;
         return this;
      }

      public WorldParticleBuilder setSpinCoefficient(float spinCoefficient) {
         this.data.spinCoefficient = spinCoefficient;
         return this;
      }

      public WorldParticleBuilder setSpinOffset(float spinOffset) {
         this.data.spinOffset = spinOffset;
         return this;
      }

      public WorldParticleBuilder setSpin(float spin) {
         return this.setSpin(spin, spin);
      }

      public WorldParticleBuilder setSpin(float spin1, float spin2) {
         return this.setSpin(spin1, spin2, spin2);
      }

      public WorldParticleBuilder setSpin(float spin1, float spin2, float spin3) {
         this.data.spin1 = spin1;
         this.data.spin2 = spin2;
         this.data.spin3 = spin3;
         return this;
      }

      public WorldParticleBuilder setLifetime(int lifetime) {
         this.data.lifetime = lifetime;
         return this;
      }

      public WorldParticleBuilder setMotionCoefficient(float motionCoefficient) {
         this.data.motionCoefficient = motionCoefficient;
         return this;
      }

      public WorldParticleBuilder randomMotion(double maxSpeed) {
         return this.randomMotion(maxSpeed, maxSpeed, maxSpeed);
      }

      public WorldParticleBuilder randomMotion(double maxHSpeed, double maxVSpeed) {
         return this.randomMotion(maxHSpeed, maxVSpeed, maxHSpeed);
      }

      public WorldParticleBuilder randomMotion(double maxXSpeed, double maxYSpeed, double maxZSpeed) {
         this.maxXSpeed = maxXSpeed;
         this.maxYSpeed = maxYSpeed;
         this.maxZSpeed = maxZSpeed;
         return this;
      }

      public WorldParticleBuilder addMotion(double vx, double vy, double vz) {
         this.vx += vx;
         this.vy += vy;
         this.vz += vz;
         return this;
      }

      public WorldParticleBuilder setMotion(double vx, double vy, double vz) {
         this.vx = vx;
         this.vy = vy;
         this.vz = vz;
         return this;
      }

      public WorldParticleBuilder setMotion(Vec3d v) {
         this.vx = v.getX();
         this.vy = v.getY();
         this.vz = v.getZ();
         return this;
      }

      public WorldParticleBuilder setForcedMotion(Vector3f startingVelocity, Vector3f endingMotion) {
         this.data.forcedMotion = true;
         this.data.motionStyle = SimpleParticleEffect.MotionStyle.START_TO_END;
         this.data.startingVelocity = startingVelocity;
         this.data.endingMotion = endingMotion;
         return this;
      }

      public WorldParticleBuilder setForcedMotion(Vector3f endingMotion) {
         this.data.forcedMotion = true;
         this.data.motionStyle = SimpleParticleEffect.MotionStyle.CURRENT_TO_END;
         this.data.endingMotion = endingMotion;
         return this;
      }

      public WorldParticleBuilder disableForcedMotion() {
         this.data.forcedMotion = false;
         return this;
      }

      public WorldParticleBuilder randomOffset(double maxDistance) {
         return this.randomOffset(maxDistance, maxDistance, maxDistance);
      }

      public WorldParticleBuilder randomOffset(double maxHDist, double maxVDist) {
         return this.randomOffset(maxHDist, maxVDist, maxHDist);
      }

      public WorldParticleBuilder randomOffset(double maxXDist, double maxYDist, double maxZDist) {
         this.maxXDist = maxXDist;
         this.maxYDist = maxYDist;
         this.maxZDist = maxZDist;
         return this;
      }

      public WorldParticleBuilder spawnCircle(
         World level, double x, double y, double z, double distance, double currentCount, double totalCount
      ) {
         double xSpeed = (double)random.nextFloat() * this.maxXSpeed;
         double ySpeed = (double)random.nextFloat() * this.maxYSpeed;
         double zSpeed = (double)random.nextFloat() * this.maxZSpeed;
         double theta = (Math.PI * 2) / totalCount;
         double finalAngle = currentCount / totalCount + theta * currentCount;
         double dx2 = distance * Math.cos(finalAngle);
         double dz2 = distance * Math.sin(finalAngle);
         Vec3d vector2f = new Vec3d(dx2, 0.0, dz2);
         this.vx = vector2f.x * xSpeed;
         this.vz = vector2f.z * zSpeed;
         double yaw2 = (double)random.nextFloat() * Math.PI * 2.0;
         double pitch2 = (double)random.nextFloat() * Math.PI - (Math.PI / 2);
         double xDist = (double)random.nextFloat() * this.maxXDist;
         double yDist = (double)random.nextFloat() * this.maxYDist;
         double zDist = (double)random.nextFloat() * this.maxZDist;
         this.dx = Math.sin(yaw2) * Math.cos(pitch2) * xDist;
         this.dy = Math.sin(pitch2) * yDist;
         this.dz = Math.cos(yaw2) * Math.cos(pitch2) * zDist;
         level.addParticle(this.data, x + this.dx + dx2, y + this.dy, z + this.dz + dz2, this.vx, ySpeed, this.vz);
         return this;
      }

      public WorldParticleBuilder spawn(World level, double x, double y, double z) {
         double yaw = (double)random.nextFloat() * Math.PI * 2.0;
         double pitch = (double)random.nextFloat() * Math.PI - (Math.PI / 2);
         double xSpeed = (double)random.nextFloat() * this.maxXSpeed;
         double ySpeed = (double)random.nextFloat() * this.maxYSpeed;
         double zSpeed = (double)random.nextFloat() * this.maxZSpeed;
         this.vx = this.vx + Math.sin(yaw) * Math.cos(pitch) * xSpeed;
         this.vy = this.vy + Math.sin(pitch) * ySpeed;
         this.vz = this.vz + Math.cos(yaw) * Math.cos(pitch) * zSpeed;
         double yaw2 = (double)random.nextFloat() * Math.PI * 2.0;
         double pitch2 = (double)random.nextFloat() * Math.PI - (Math.PI / 2);
         double xDist = (double)random.nextFloat() * this.maxXDist;
         double yDist = (double)random.nextFloat() * this.maxYDist;
         double zDist = (double)random.nextFloat() * this.maxZDist;
         this.dx = Math.sin(yaw2) * Math.cos(pitch2) * xDist;
         this.dy = Math.sin(pitch2) * yDist;
         this.dz = Math.cos(yaw2) * Math.cos(pitch2) * zDist;
         level.addParticle(this.data, x + this.dx, y + this.dy, z + this.dz, this.vx, this.vy, this.vz);
         return this;
      }

      public WorldParticleBuilder evenlySpawnAtEdges(World level, BlockPos pos) {
         this.evenlySpawnAtEdges(level, pos, Direction.values());
         return this;
      }

      public WorldParticleBuilder evenlySpawnAtEdges(World level, BlockPos pos, Direction... directions) {
         for (Direction direction : directions) {
            double yaw = (double)random.nextFloat() * Math.PI * 2.0;
            double pitch = (double)random.nextFloat() * Math.PI - (Math.PI / 2);
            double xSpeed = (double)random.nextFloat() * this.maxXSpeed;
            double ySpeed = (double)random.nextFloat() * this.maxYSpeed;
            double zSpeed = (double)random.nextFloat() * this.maxZSpeed;
            this.vx = this.vx + Math.sin(yaw) * Math.cos(pitch) * xSpeed;
            this.vy = this.vy + Math.sin(pitch) * ySpeed;
            this.vz = this.vz + Math.cos(yaw) * Math.cos(pitch) * zSpeed;
            Axis direction$axis = direction.getAxis();
            double d0 = 0.5625;
            this.dx = direction$axis == Axis.X ? 0.5 + d0 * (double)direction.getOffsetX() : random.nextDouble();
            this.dy = direction$axis == Axis.Y ? 0.5 + d0 * (double)direction.getOffsetY() : random.nextDouble();
            this.dz = direction$axis == Axis.Z ? 0.5 + d0 * (double)direction.getOffsetZ() : random.nextDouble();
            level.addParticle(
               this.data,
               (double)pos.getX() + this.dx,
               (double)pos.getY() + this.dy,
               (double)pos.getZ() + this.dz,
               this.vx,
               this.vy,
               this.vz
            );
         }

         return this;
      }

      public WorldParticleBuilder evenlySpawnAtAlignedEdges(World level, BlockPos pos, BlockState state) {
         return this.evenlySpawnAtAlignedEdges(level, pos, state, 100);
      }

      public WorldParticleBuilder evenlySpawnAtAlignedEdges(World level, BlockPos pos, BlockState state, int max) {
         VoxelShape voxelShape = state.getOutlineShape(level, pos);
         if (voxelShape.isEmpty()) {
            voxelShape = VoxelShapes.fullCube();
         }

         int[] c = new int[1];
         int perBoxMax = max / voxelShape.getBoundingBoxes().size();
         Supplier<Boolean> r = () -> {
            c[0]++;
            if (c[0] >= perBoxMax) {
               c[0] = 0;
               return true;
            } else {
               return false;
            }
         };
         Vec3d v = Vec3d.of(pos);
         voxelShape.forEachBox((x1, y1, z1, x2, y2, z2) -> {
            Vec3d b = v.add(x1, y1, z1);
            Vec3d e = v.add(x2, y2, z2);
            List<Runnable> runs = new ArrayList<>();
            runs.add(() -> this.spawnLine(level, b, v.add(x2, y1, z1)));
            runs.add(() -> this.spawnLine(level, b, v.add(x1, y2, z1)));
            runs.add(() -> this.spawnLine(level, b, v.add(x1, y1, z2)));
            runs.add(() -> this.spawnLine(level, v.add(x1, y2, z1), v.add(x2, y2, z1)));
            runs.add(() -> this.spawnLine(level, v.add(x1, y2, z1), v.add(x1, y2, z2)));
            runs.add(() -> this.spawnLine(level, e, v.add(x2, y2, z1)));
            runs.add(() -> this.spawnLine(level, e, v.add(x1, y2, z2)));
            runs.add(() -> this.spawnLine(level, e, v.add(x2, y1, z2)));
            runs.add(() -> this.spawnLine(level, v.add(x2, y1, z1), v.add(x2, y1, z2)));
            runs.add(() -> this.spawnLine(level, v.add(x1, y1, z2), v.add(x2, y1, z2)));
            runs.add(() -> this.spawnLine(level, v.add(x2, y1, z1), v.add(x2, y2, z1)));
            runs.add(() -> this.spawnLine(level, v.add(x1, y1, z2), v.add(x1, y2, z2)));
            Collections.shuffle(runs);

            for (Runnable runnable : runs) {
               runnable.run();
               if (r.get()) {
                  break;
               }
            }
         });
         return this;
      }

      public WorldParticleBuilder spawnLine(World world, Vec3d one, Vec3d two) {
         double yaw = (double)random.nextFloat() * Math.PI * 2.0;
         double pitch = (double)random.nextFloat() * Math.PI - (Math.PI / 2);
         double xSpeed = (double)random.nextFloat() * this.maxXSpeed;
         double ySpeed = (double)random.nextFloat() * this.maxYSpeed;
         double zSpeed = (double)random.nextFloat() * this.maxZSpeed;
         this.vx = this.vx + Math.sin(yaw) * Math.cos(pitch) * xSpeed;
         this.vy = this.vy + Math.sin(pitch) * ySpeed;
         this.vz = this.vz + Math.cos(yaw) * Math.cos(pitch) * zSpeed;
         double yaw2 = (double)random.nextFloat() * Math.PI * 2.0;
         double pitch2 = (double)random.nextFloat() * Math.PI - (Math.PI / 2);
         double xDist = (double)random.nextFloat() * this.maxXDist;
         double yDist = (double)random.nextFloat() * this.maxYDist;
         double zDist = (double)random.nextFloat() * this.maxZDist;
         this.dx = Math.sin(yaw2) * Math.cos(pitch2) * xDist;
         this.dy = Math.sin(pitch2) * yDist;
         this.dz = Math.cos(yaw2) * Math.cos(pitch2) * zDist;
         Vec3d pos = one.lerp(two, random.nextDouble());
         world.addParticle(this.data, pos.x + this.dx, pos.y + this.dy, pos.z + this.dz, this.vx, this.vy, this.vz);
         return this;
      }

      public WorldParticleBuilder spawnAtEdges(World level, BlockPos pos) {
         Direction direction = Direction.values()[level.random.nextInt(Direction.values().length)];
         double yaw = (double)random.nextFloat() * Math.PI * 2.0;
         double pitch = (double)random.nextFloat() * Math.PI - (Math.PI / 2);
         double xSpeed = (double)random.nextFloat() * this.maxXSpeed;
         double ySpeed = (double)random.nextFloat() * this.maxYSpeed;
         double zSpeed = (double)random.nextFloat() * this.maxZSpeed;
         this.vx = this.vx + Math.sin(yaw) * Math.cos(pitch) * xSpeed;
         this.vy = this.vy + Math.sin(pitch) * ySpeed;
         this.vz = this.vz + Math.cos(yaw) * Math.cos(pitch) * zSpeed;
         Axis direction$axis = direction.getAxis();
         double d0 = 0.5625;
         this.dx = direction$axis == Axis.X ? 0.5 + d0 * (double)direction.getOffsetX() : random.nextDouble();
         this.dy = direction$axis == Axis.Y ? 0.5 + d0 * (double)direction.getOffsetY() : random.nextDouble();
         this.dz = direction$axis == Axis.Z ? 0.5 + d0 * (double)direction.getOffsetZ() : random.nextDouble();
         level.addParticle(
            this.data,
            (double)pos.getX() + this.dx,
            (double)pos.getY() + this.dy,
            (double)pos.getZ() + this.dz,
            this.vx,
            this.vy,
            this.vz
         );
         return this;
      }

      public WorldParticleBuilder spawnAtAABBBoundaries(World level, Box aabb) {
         Direction direction = Direction.values()[level.random.nextInt(Direction.values().length)];
         double yaw = (double)random.nextFloat() * Math.PI * 2.0;
         double pitch = (double)random.nextFloat() * Math.PI - (Math.PI / 2);
         double xSpeed = (double)random.nextFloat() * this.maxXSpeed;
         double ySpeed = (double)random.nextFloat() * this.maxYSpeed;
         double zSpeed = (double)random.nextFloat() * this.maxZSpeed;
         this.vx = this.vx + Math.sin(yaw) * Math.cos(pitch) * xSpeed;
         this.vy = this.vy + Math.sin(pitch) * ySpeed;
         this.vz = this.vz + Math.cos(yaw) * Math.cos(pitch) * zSpeed;
         Axis direction$axis = direction.getAxis();
         double xSize = aabb.getXLength();
         double ySize = aabb.getYLength();
         double zSize = aabb.getZLength();
         double d0 = 0.5;
         this.dx = direction$axis == Axis.X ? d0 * xSize : random.nextDouble();
         this.dy = direction$axis == Axis.Y ? d0 * ySize : random.nextDouble();
         this.dz = direction$axis == Axis.Z ? d0 * zSize : random.nextDouble();
         Vec3d pos = aabb.getCenter();
         level.addParticle(this.data, pos.x + this.dx, pos.y + this.dy, pos.z + this.dz, this.vx, this.vy, this.vz);
         return this;
      }

      public WorldParticleBuilder repeat(World level, double x, double y, double z, int n) {
         for (int i = 0; i < n; i++) {
            this.spawn(level, x, y, z);
         }

         return this;
      }

      public WorldParticleBuilder repeatEdges(World level, BlockPos pos, int n) {
         for (int i = 0; i < n; i++) {
            this.spawnAtEdges(level, pos);
         }

         return this;
      }

      public WorldParticleBuilder evenlyRepeatEdges(World level, BlockPos pos, int n) {
         for (int i = 0; i < n; i++) {
            this.evenlySpawnAtEdges(level, pos);
         }

         return this;
      }

      public WorldParticleBuilder evenlyRepeatEdges(World level, BlockPos pos, int n, Direction... directions) {
         for (int i = 0; i < n; i++) {
            this.evenlySpawnAtEdges(level, pos, directions);
         }

         return this;
      }

      public WorldParticleBuilder repeatCircle(World level, double x, double y, double z, double distance, int times) {
         for (int i = 0; i < times; i++) {
            this.spawnCircle(level, x, y, z, distance, (double)i, (double)times);
         }

         return this;
      }
   }
}
