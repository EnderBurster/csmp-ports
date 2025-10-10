package aureum.asta.disks.mixin.ports.mace.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin({ExplosiveProjectileEntity.class})
public abstract class AllowNoParticleForWindChargeMixin {
   public AllowNoParticleForWindChargeMixin() {
   }

   @WrapOperation(
           at = {@At(
                   value = "INVOKE",
                   target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"
           )},
           method = {"tick"}
   )
   private void init(World instance, ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Operation<Void> original) {
      if (parameters != null) {
         original.call(instance, parameters, x, y, z, velocityX, velocityY, velocityZ);
      }

   }
}
