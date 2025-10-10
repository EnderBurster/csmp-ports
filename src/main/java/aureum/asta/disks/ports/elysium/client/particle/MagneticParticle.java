package aureum.asta.disks.ports.elysium.client.particle;

import aureum.asta.disks.api.lodestone.util.math.Quaternion;
import aureum.asta.disks.ports.amarite.mialib.util.Vec3f;
import aureum.asta.disks.ports.elysium.particles.MagneticWaveParticleOption;
import java.util.function.Consumer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MagneticParticle extends SpriteBillboardParticle {
   private static final float SCALE_BY_TICK = (float)Math.pow(1.1F, 0.2F);
   private static final float REVERSED_SCALE_BY_TICK = (float)Math.pow(1.1F, -0.2F);
   private static final Vec3f ROTATION_VECTOR = (Vec3f)Util.make(new Vec3f(0.5F, 0.5F, 0.5F), Vec3f::normalize);
   private final SpriteProvider sprites;
   private final float yRot;
   private final float xRot;
   private final float targetScale;
   private final float initialScale;
   protected final boolean isReversed;
   private float prevQuadSize;
   private float prevAlpha;

   public MagneticParticle(
      ClientWorld clientLevel,
      SpriteProvider sprites,
      float yRot,
      float xRot,
      double d,
      double e,
      double f,
      boolean isReversed,
      float initialScale,
      float targetScale,
      int lifetime
   ) {
      super(clientLevel, d, e, f);
      this.yRot = yRot;
      this.xRot = xRot;
      this.targetScale = targetScale;
      this.gravityStrength = 0.0F;
      this.sprites = sprites;
      this.setSpriteForAge(sprites);
      this.collidesWithWorld = false;
      this.maxAge = lifetime;
      this.velocityMultiplier = 1.0F;
      this.isReversed = isReversed;
      this.initialScale = initialScale;
      this.scale = isReversed ? this.targetScale : this.initialScale;
      this.prevQuadSize = this.scale;
      this.setAlpha(isReversed ? 0.0F : 1.0F);
      this.prevAlpha = this.alpha;
   }

   public ParticleTextureSheet getType() {
      return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      super.tick();
      this.setSpriteForAge(this.sprites);
      this.prevAlpha = this.alpha;
      this.prevQuadSize = this.scale;
      float delta = this.getDelta();
      this.scale = MathHelper.lerp(1.0F - delta, this.initialScale, this.targetScale);
      this.setBoundingBoxSpacing(0.2F * this.scale, 0.2F * this.scale);
      this.setAlpha(delta);
   }

   public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
      this.renderRotatedParticle(vertexConsumer, camera, tickDelta, quaternion -> {
         quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion(this.yRot));
         quaternion.hamiltonProduct(Vec3f.POSITIVE_X.getRadialQuaternion(this.xRot));
      });
      this.renderRotatedParticle(vertexConsumer, camera, tickDelta, quaternion -> {
         quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion(this.yRot - (float) Math.PI));
         quaternion.hamiltonProduct(Vec3f.POSITIVE_X.getRadialQuaternion(-this.xRot));
      });
   }

   protected float getDelta() {
      return (float)(1.0 - Math.pow(this.isReversed ? (double)(this.maxAge - this.age) : (double)this.age, 3.0) / Math.pow((double)this.maxAge, 3.0));
   }

   public float getSize(float tickDelta) {
      return MathHelper.lerp(tickDelta, this.prevQuadSize, this.scale);
   }

   public int getBrightness(float tint) {
      return 240;
   }

   private void renderRotatedParticle(VertexConsumer consumer, Camera camera, float f, Consumer<Quaternion> quaternion) {
      Vec3d vec3 = camera.getPos();
      float g = (float)(MathHelper.lerp((double)f, this.prevPosX, this.x) - vec3.getX()) + (float)this.random.nextGaussian() * 0.01F;
      float h = (float)(MathHelper.lerp((double)f, this.prevPosY, this.y) - vec3.getY()) + (float)this.random.nextGaussian() * 0.01F;
      float i = (float)(MathHelper.lerp((double)f, this.prevPosZ, this.z) - vec3.getZ()) + (float)this.random.nextGaussian() * 0.01F;
      Quaternion quaternion2 = new Quaternion(ROTATION_VECTOR, 0.0F, true);
      quaternion.accept(quaternion2);
      Vec3f[] vector3fs = new Vec3f[]{new Vec3f(-1.0F, -1.0F, 0.0F), new Vec3f(-1.0F, 1.0F, 0.0F), new Vec3f(1.0F, 1.0F, 0.0F), new Vec3f(1.0F, -1.0F, 0.0F)};
      float j = this.getSize(f);

      for (int k = 0; k < 4; k++) {
         Vec3f vector3f = vector3fs[k];
         vector3f.rotate(quaternion2);
         vector3f.scale(j);
         vector3f.scale(this.targetScale);
         vector3f.add(g, h, i);
      }

      int k = this.getBrightness(f);
      float alpha = MathHelper.lerp(f, this.prevAlpha, this.alpha);
      this.makeCornerVertex(consumer, vector3fs[0], this.getMaxU(), this.getMaxV(), k, alpha);
      this.makeCornerVertex(consumer, vector3fs[1], this.getMaxU(), this.getMinV(), k, alpha);
      this.makeCornerVertex(consumer, vector3fs[2], this.getMinU(), this.getMinV(), k, alpha);
      this.makeCornerVertex(consumer, vector3fs[3], this.getMinU(), this.getMaxV(), k, alpha);
   }

   private void makeCornerVertex(VertexConsumer consumer, Vec3f vec3f, float f, float g, int i, float alpha) {
      consumer.vertex((double)vec3f.getX(), (double)vec3f.getY(), (double)vec3f.getZ())
         .texture(f, g)
         .color(this.red, this.green, this.blue, alpha)
         .light(i)
         .next();
   }

   public static class Provider implements ParticleFactory<MagneticWaveParticleOption> {
      private final SpriteProvider sprites;

      public Provider(SpriteProvider spriteSet) {
         this.sprites = spriteSet;
      }

      public Particle createParticle(
         MagneticWaveParticleOption type, ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         double speed = Math.sqrt(xSpeed * xSpeed + ySpeed * ySpeed + zSpeed * zSpeed);
         int lifetime = (int)Math.ceil((double)type.distance() / speed);
         MagneticParticle particle = new MagneticParticle(
            level,
            this.sprites,
            (float)MathHelper.atan2(xSpeed, zSpeed),
            (float)MathHelper.atan2(ySpeed, -Math.sqrt(xSpeed * xSpeed + zSpeed * zSpeed)),
            x,
            y,
            z,
            type.isReversed(),
            0.5F,
            type.widthScale(),
            lifetime
         );
         particle.setVelocity(xSpeed, ySpeed, zSpeed);
         particle.setColor(1.0F, 1.0F, 1.0F);
         return particle;
      }
   }

   public static class SimpleProvider implements ParticleFactory<DefaultParticleType> {
      private final SpriteProvider sprites;

      public SimpleProvider(SpriteProvider spriteSet) {
         this.sprites = spriteSet;
      }

      public Particle createParticle(DefaultParticleType type, ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         MagneticParticle particle = new MagneticParticle(
            level,
            this.sprites,
            (float)MathHelper.atan2(xSpeed, zSpeed),
            (float)MathHelper.atan2(ySpeed, -Math.sqrt(xSpeed * xSpeed + zSpeed * zSpeed)),
            x,
            y,
            z,
            false,
            0.5F,
            1.75F,
            25
         );
         particle.setVelocity(xSpeed, ySpeed, zSpeed);
         particle.setColor(1.0F, 1.0F, 1.0F);
         return particle;
      }
   }
}
