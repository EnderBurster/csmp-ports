package aureum.asta.disks.api.lodestone.systems.rendering.particle.world;

import aureum.asta.disks.api.lodestone.config.ClientConfig;
import aureum.asta.disks.api.lodestone.handlers.RenderHandler;
import aureum.asta.disks.api.lodestone.setup.LodestoneRenderLayers;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleTextureSheets;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect;
import java.awt.Color;

import aureum.asta.disks.ports.elysium.client.GlowEffectManager;
import net.fabricmc.fabric.impl.client.particle.FabricSpriteProviderImpl;
import net.minecraft.client.texture.Sprite;
import org.joml.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ColorHelper.Argb;

public class GenericParticle extends SpriteBillboardParticle {
   protected WorldParticleEffect data;
   private final ParticleTextureSheet textureSheet;
   protected final FabricSpriteProviderImpl spriteProvider;
   private final Vector3f startingVelocity;
   private boolean reachedPositiveAlpha;
   private boolean reachedPositiveScale;
   float[] hsv1 = new float[3];
   float[] hsv2 = new float[3];

   public GenericParticle(
      ClientWorld world,
      WorldParticleEffect data,
      FabricSpriteProviderImpl spriteProvider,
      double x,
      double y,
      double z,
      double velocityX,
      double velocityY,
      double velocityZ
   ) {
      super(world, x, y, z);
      this.data = data;
      this.textureSheet = data.textureSheet;
      this.spriteProvider = spriteProvider;
      this.angle = data.spinOffset + data.spin1;
      if (!data.forcedMotion) {
         this.velocityX = velocityX;
         this.velocityY = velocityY;
         this.velocityZ = velocityZ;
      }

      this.setMaxAge(data.lifetime);
      this.gravityStrength = data.gravity;
      this.collidesWithWorld = !data.noClip;
      this.velocityMultiplier = 1.0F;
      this.startingVelocity = data.motionStyle == SimpleParticleEffect.MotionStyle.START_TO_END
         ? data.startingVelocity
         : new Vector3f((float)velocityX, (float)velocityY, (float)velocityZ);
      Color.RGBtoHSB((int)(255.0F * Math.min(1.0F, data.r1)), (int)(255.0F * Math.min(1.0F, data.g1)), (int)(255.0F * Math.min(1.0F, data.b1)), this.hsv1);
      Color.RGBtoHSB((int)(255.0F * Math.min(1.0F, data.r2)), (int)(255.0F * Math.min(1.0F, data.g2)), (int)(255.0F * Math.min(1.0F, data.b2)), this.hsv2);
      if (spriteProvider != null) {
         if (this.getAnimator().equals(SimpleParticleEffect.Animator.RANDOM_SPRITE)) {
            this.setSprite(spriteProvider);
         }

         if (this.getAnimator().equals(SimpleParticleEffect.Animator.FIRST_INDEX) || this.getAnimator().equals(SimpleParticleEffect.Animator.WITH_AGE)) {
            this.setSprite(0);
         }

         if (this.getAnimator().equals(SimpleParticleEffect.Animator.LAST_INDEX)) {
            this.setSprite(spriteProvider.getSprites().size() - 1);
         }
      }

      this.updateTraits();
   }

   public void setSprite(int spriteIndex) {
      if (spriteIndex < this.spriteProvider.getSprites().size() && spriteIndex >= 0) {
         this.setSprite((Sprite)this.spriteProvider.getSprites().get(spriteIndex));
      }
   }

   public void pickColor(float colorCoeff) {
      float h = MathHelper.lerpAngleDegrees(colorCoeff, 360.0F * this.hsv1[0], 360.0F * this.hsv2[0]) / 360.0F;
      float s = MathHelper.lerp(colorCoeff, this.hsv1[1], this.hsv2[1]);
      float v = MathHelper.lerp(colorCoeff, this.hsv1[2], this.hsv2[2]);
      int packed = Color.HSBtoRGB(h, s, v);
      float r = (float)Argb.getRed(packed) / 255.0F;
      float g = (float)Argb.getGreen(packed) / 255.0F;
      float b = (float)Argb.getBlue(packed) / 255.0F;
      this.setColor(r, g, b);
   }

   public float getCurve(float multiplier) {
      return MathHelper.clamp((float)this.age * multiplier / (float)this.maxAge, 0.0F, 1.0F);
   }

   public SimpleParticleEffect.Animator getAnimator() {
      return this.data.animator;
   }

   protected void updateTraits() {
      if (this.data.removalProtocol != SimpleParticleEffect.SpecialRemovalProtocol.INVISIBLE
            && (
               this.data.removalProtocol != SimpleParticleEffect.SpecialRemovalProtocol.ENDING_CURVE_INVISIBLE
                  || !(this.getCurve(this.data.scaleCoefficient) > 0.5F) && !(this.getCurve(this.data.alphaCoefficient) > 0.5F)
            )
         || (!this.reachedPositiveAlpha || !(this.alpha <= 0.0F)) && (!this.reachedPositiveScale || !(this.scale <= 0.0F))) {
         if (this.alpha > 0.0F) {
            this.reachedPositiveAlpha = true;
         }

         if (this.scale > 0.0F) {
            this.reachedPositiveScale = true;
         }

         this.pickColor(this.data.colorCurveEasing.ease(this.getCurve(this.data.colorCoefficient), 0.0F, 1.0F, 1.0F));
         if (this.data.isTrinaryScale()) {
            float trinaryAge = this.getCurve(this.data.scaleCoefficient);
            if (trinaryAge >= 0.5F) {
               this.scale = MathHelper.lerp(
                  this.data.scaleCurveEndEasing.ease(trinaryAge - 0.5F, 0.0F, 1.0F, 0.5F), this.data.scale2, this.data.scale3
               );
            } else {
               this.scale = MathHelper.lerp(
                  this.data.scaleCurveStartEasing.ease(trinaryAge, 0.0F, 1.0F, 0.5F), this.data.scale1, this.data.scale2
               );
            }
         } else {
            this.scale = MathHelper.lerp(
               this.data.scaleCurveStartEasing.ease(this.getCurve(this.data.scaleCoefficient), 0.0F, 1.0F, 1.0F), this.data.scale1, this.data.scale2
            );
         }

         if (this.data.isTrinaryAlpha()) {
            float trinaryAge = this.getCurve(this.data.alphaCoefficient);
            if (trinaryAge >= 0.5F) {
               this.alpha = MathHelper.lerp(
                  this.data.alphaCurveStartEasing.ease(trinaryAge - 0.5F, 0.0F, 1.0F, 0.5F), this.data.alpha2, this.data.alpha3
               );
            } else {
               this.alpha = MathHelper.lerp(this.data.alphaCurveStartEasing.ease(trinaryAge, 0.0F, 1.0F, 0.5F), this.data.alpha1, this.data.alpha2);
            }
         } else {
            this.alpha = MathHelper.lerp(
               this.data.alphaCurveStartEasing.ease(this.getCurve(this.data.alphaCoefficient), 0.0F, 1.0F, 1.0F), this.data.alpha1, this.data.alpha2
            );
         }

         this.prevAngle = this.angle;
         if (this.data.isTrinarySpin()) {
            float trinaryAge = this.getCurve(this.data.spinCoefficient);
            if (trinaryAge >= 0.5F) {
               this.angle = this.angle
                  + MathHelper.lerp(this.data.spinCurveEndEasing.ease(trinaryAge - 0.5F, 0.0F, 1.0F, 0.5F), this.data.spin2, this.data.spin3);
            } else {
               this.angle = this.angle
                  + MathHelper.lerp(this.data.spinCurveStartEasing.ease(trinaryAge, 0.0F, 1.0F, 0.5F), this.data.spin1, this.data.spin2);
            }
         } else {
            this.angle = this.angle
               + MathHelper.lerp(
                  this.data.spinCurveStartEasing.ease(this.getCurve(this.data.alphaCoefficient), 0.0F, 1.0F, 1.0F), this.data.spin1, this.data.spin2
               );
         }

         if (this.data.forcedMotion) {
            float motionAge = this.getCurve(this.data.motionCoefficient);
            Vector3f currentMotion = this.data.motionStyle == SimpleParticleEffect.MotionStyle.START_TO_END
               ? this.startingVelocity
               : new Vector3f((float)this.velocityX, (float)this.velocityY, (float)this.velocityZ);
            this.velocityX = (double)MathHelper.lerp(
               this.data.motionEasing.ease(motionAge, 0.0F, 1.0F, 1.0F), currentMotion.x(), this.data.endingMotion.x()
            );
            this.velocityY = (double)MathHelper.lerp(
               this.data.motionEasing.ease(motionAge, 0.0F, 1.0F, 1.0F), currentMotion.y(), this.data.endingMotion.y()
            );
            this.velocityZ = (double)MathHelper.lerp(
               this.data.motionEasing.ease(motionAge, 0.0F, 1.0F, 1.0F), currentMotion.z(), this.data.endingMotion.z()
            );
         } else {
            this.velocityX = this.velocityX * (double)this.data.motionCoefficient;
            this.velocityY = this.velocityY * (double)this.data.motionCoefficient;
            this.velocityZ = this.velocityZ * (double)this.data.motionCoefficient;
         }
      } else {
         this.markDead();
      }
   }

   public void tick() {
      this.updateTraits();
      if (this.data.animator.equals(SimpleParticleEffect.Animator.WITH_AGE)) {
         this.setSpriteForAge(this.spriteProvider);
      }

      super.tick();
   }

   public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
      VertexConsumer consumer = vertexConsumer;
      if (ClientConfig.DELAYED_RENDERING) {
         if (this.getType().equals(ParticleTextureSheets.ADDITIVE)) {
            consumer = RenderHandler.DELAYED_RENDER.getBuffer(GlowEffectManager.unwrap(LodestoneRenderLayers.ADDITIVE_PARTICLE));
         }

         if (this.getType().equals(ParticleTextureSheets.TRANSPARENT)) {
            consumer = RenderHandler.DELAYED_RENDER.getBuffer(GlowEffectManager.unwrap(LodestoneRenderLayers.TRANSPARENT_PARTICLE));
         }
      }

      super.buildGeometry(consumer, camera, tickDelta);
   }

   public ParticleTextureSheet getType() {
      return this.textureSheet;
   }
}
