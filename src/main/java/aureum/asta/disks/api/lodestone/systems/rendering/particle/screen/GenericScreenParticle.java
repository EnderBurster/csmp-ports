package aureum.asta.disks.api.lodestone.systems.rendering.particle.screen;

import aureum.asta.disks.api.lodestone.handlers.ScreenParticleHandler;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.base.SpriteBillboardScreenParticle;
import java.awt.Color;
import net.fabricmc.fabric.impl.client.particle.FabricSpriteProviderImpl;
import net.minecraft.client.texture.Sprite;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.util.math.ColorHelper.Argb;

public class GenericScreenParticle extends SpriteBillboardScreenParticle {
   public ScreenParticleEffect data;
   private final ParticleTextureSheet textureSheet;
   protected final FabricSpriteProviderImpl spriteProvider;
   private final Vec2f startingVelocity;
   float[] hsv1 = new float[3];
   float[] hsv2 = new float[3];

   public GenericScreenParticle(
      World clientWorld, ScreenParticleEffect data, FabricSpriteProviderImpl spriteSet, double x, double y, double xMotion, double yMotion
   ) {
      super(clientWorld, x, y);
      this.data = data;
      this.textureSheet = data.textureSheet;
      this.spriteProvider = spriteSet;
      this.angle = data.spinOffset + data.spin1;
      if (!data.forcedMotion) {
         this.velocityX = xMotion;
         this.velocityY = yMotion;
      }

      this.setRenderOrder(data.renderOrder);
      this.setMaxAge(data.lifetime);
      this.gravityStrength = data.gravity;
      this.velocityMultiplier = 1.0F;
      this.startingVelocity = data.motionStyle == SimpleParticleEffect.MotionStyle.START_TO_END
         ? data.startingVelocity
         : new Vec2f((float)xMotion, (float)yMotion);
      Color.RGBtoHSB((int)(255.0F * Math.min(1.0F, data.r1)), (int)(255.0F * Math.min(1.0F, data.g1)), (int)(255.0F * Math.min(1.0F, data.b1)), this.hsv1);
      Color.RGBtoHSB((int)(255.0F * Math.min(1.0F, data.r2)), (int)(255.0F * Math.min(1.0F, data.g2)), (int)(255.0F * Math.min(1.0F, data.b2)), this.hsv2);
      this.updateTraits();
      if (this.getAnimator().equals(SimpleParticleEffect.Animator.RANDOM_SPRITE)) {
         this.setSprite(this.spriteProvider);
      }

      if (this.getAnimator().equals(SimpleParticleEffect.Animator.FIRST_INDEX) || this.getAnimator().equals(SimpleParticleEffect.Animator.WITH_AGE)) {
         this.setSprite(0);
      }

      if (this.getAnimator().equals(SimpleParticleEffect.Animator.LAST_INDEX)) {
         this.setSprite(this.spriteProvider.getSprites().size() - 1);
      }

      this.updateTraits();
   }

   public SimpleParticleEffect.Animator getAnimator() {
      return this.data.animator;
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

   protected void updateTraits() {
      this.pickColor(this.data.colorCurveEasing.ease(this.getCurve(this.data.colorCoefficient), 0.0F, 1.0F, 1.0F));
      if (this.data.isTrinaryScale()) {
         float trinaryAge = this.getCurve(this.data.scaleCoefficient);
         if (trinaryAge >= 0.5F) {
            this.quadSize = MathHelper.lerp(this.data.scaleCurveEndEasing.ease(trinaryAge - 0.5F, 0.0F, 1.0F, 0.5F), this.data.scale2, this.data.scale3);
         } else {
            this.quadSize = MathHelper.lerp(this.data.scaleCurveStartEasing.ease(trinaryAge, 0.0F, 1.0F, 0.5F), this.data.scale1, this.data.scale2);
         }
      } else {
         this.quadSize = MathHelper.lerp(
            this.data.scaleCurveStartEasing.ease(this.getCurve(this.data.scaleCoefficient), 0.0F, 1.0F, 1.0F), this.data.scale1, this.data.scale2
         );
      }

      if (this.data.isTrinaryAlpha()) {
         float trinaryAge = this.getCurve(this.data.alphaCoefficient);
         if (trinaryAge >= 0.5F) {
            this.alpha = MathHelper.lerp(this.data.alphaCurveEndEasing.ease(trinaryAge - 0.5F, 0.0F, 1.0F, 0.5F), this.data.alpha2, this.data.alpha3);
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
         Vec2f currentMotion = this.data.motionStyle == SimpleParticleEffect.MotionStyle.START_TO_END
            ? this.startingVelocity
            : new Vec2f((float)this.velocityX, (float)this.velocityY);
         this.velocityX = (double)MathHelper.lerp(
            this.data.motionEasing.ease(motionAge, 0.0F, 1.0F, 1.0F), currentMotion.x, this.data.endingMotion.x
         );
         this.velocityY = (double)MathHelper.lerp(
            this.data.motionEasing.ease(motionAge, 0.0F, 1.0F, 1.0F), currentMotion.y, this.data.endingMotion.y
         );
      } else {
         this.velocityX = this.velocityX * (double)this.data.motionCoefficient;
         this.velocityY = this.velocityY * (double)this.data.motionCoefficient;
      }
   }

   @Override
   public void tick() {
      this.updateTraits();
      if (this.data.animator.equals(SimpleParticleEffect.Animator.WITH_AGE)) {
         this.setSpriteForAge(this.spriteProvider);
      }

      super.tick();
   }

   public void trackStack() {
      for (ScreenParticleHandler.StackTracker renderedStack : ScreenParticleHandler.RENDERED_STACKS) {
         if (renderedStack.stack().equals(this.data.stack) && renderedStack.order().equals(this.data.renderOrder)) {
            this.x = (double)(renderedStack.xOrigin() + this.data.xOffset) + this.totalX;
            this.y = (double)(renderedStack.yOrigin() + this.data.yOffset) + this.totalY;
            break;
         }
      }
   }

   @Override
   public ParticleTextureSheet getTextureSheet() {
      return this.textureSheet;
   }
}
