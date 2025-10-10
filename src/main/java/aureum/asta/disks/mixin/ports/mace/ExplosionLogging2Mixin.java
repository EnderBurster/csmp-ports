package aureum.asta.disks.mixin.ports.mace;

import aureum.asta.disks.ports.mace.FaithfulMace;
import aureum.asta.disks.ports.mace.PlayerEntityMaceInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerEntity.class})
public abstract class ExplosionLogging2Mixin implements PlayerEntityMaceInterface {
   public ExplosionLogging2Mixin() {
   }

   @Inject(
           method = {"attack"},
           at = {@At("HEAD")}
   )
   private void logggyyyyyyyyy(Entity target, CallbackInfo ci) {
      PlayerEntity thiscast = (PlayerEntity)(Object)this;
      if (FaithfulMace.superfluousLogging()) {
         FaithfulMace.MOGGER.info("Starting player attack with vel {}", thiscast.getVelocity());
      }

   }

   @Inject(
           method = {"attack"},
           at = {@At("TAIL")}
   )
   private void logggyyyyyyyyy2(Entity target, CallbackInfo ci) {
      PlayerEntity thiscast = (PlayerEntity)(Object)this;
      if (FaithfulMace.superfluousLogging()) {
         FaithfulMace.MOGGER.info("Ending player attack with vel {}", thiscast.getVelocity());
      }

   }
}
