package aureum.asta.disks.api.lodestone.systems.rendering.particle;

import net.minecraft.client.particle.ParticleTextureSheet;

public class SimpleParticleEffect {
   public ParticleTextureSheet textureSheet = ParticleTextureSheets.ADDITIVE;
   public Animator animator = Animator.FIRST_INDEX;
   public SpecialRemovalProtocol removalProtocol = SpecialRemovalProtocol.NONE;
   public float r1 = 1.0F;
   public float g1 = 1.0F;
   public float b1 = 1.0F;
   public float r2 = 1.0F;
   public float g2 = 1.0F;
   public float b2 = 1.0F;
   public float colorCoefficient = 1.0F;
   public Easing colorCurveEasing = Easing.LINEAR;
   public float scale1 = 1.0F;
   public float scale2 = 0.0F;
   public float scale3 = 0.0F;
   public float scaleCoefficient = 1.0F;
   public Easing scaleCurveStartEasing = Easing.LINEAR;
   public Easing scaleCurveEndEasing = Easing.LINEAR;
   public float alpha1 = 1.0F;
   public float alpha2 = 0.0F;
   public float alpha3 = 0.0F;
   public float alphaCoefficient = 1.0F;
   public Easing alphaCurveStartEasing = Easing.LINEAR;
   public Easing alphaCurveEndEasing = Easing.LINEAR;
   public boolean forcedMotion = false;
   public MotionStyle motionStyle = MotionStyle.START_TO_END;
   public float motionCoefficient = 1.0F;
   public Easing motionEasing = Easing.LINEAR;
   public float spin1 = 0.0F;
   public float spin2 = 0.0F;
   public float spin3 = 0.0F;
   public float spinCoefficient = 1.0F;
   public float spinOffset = 0.0F;
   public Easing spinCurveStartEasing = Easing.LINEAR;
   public Easing spinCurveEndEasing = Easing.LINEAR;
   public int lifetime = 20;
   public float gravity = 0.0F;
   public boolean noClip = false;

   public boolean isTrinaryScale() {
      return this.scale2 != this.scale3;
   }

   public boolean isTrinaryAlpha() {
      return this.alpha2 != this.alpha3;
   }

   public boolean isTrinarySpin() {
      return this.spin2 != this.spin3;
   }

   public static enum Animator {
      FIRST_INDEX,
      LAST_INDEX,
      WITH_AGE,
      RANDOM_SPRITE;
   }

   public static enum MotionStyle {
      START_TO_END,
      CURRENT_TO_END;
   }

   public static enum SpecialRemovalProtocol {
      NONE,
      INVISIBLE,
      ENDING_CURVE_INVISIBLE;
   }
}
