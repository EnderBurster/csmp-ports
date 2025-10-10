package aureum.asta.disks.mixin.ports.mace.client;

import aureum.asta.disks.ports.mace.FaithfulMace;
import aureum.asta.disks.ports.mace.PlayerEntityMaceInterface;
import aureum.asta.disks.ports.mace.client.MaceClientUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({WorldRenderer.class})
public abstract class MaceSmashWorldEventMixin implements PlayerEntityMaceInterface {
   @Shadow
   private @Nullable ClientWorld world;

   public MaceSmashWorldEventMixin() {
   }

   @Inject(
           method = {"processWorldEvent"},
           at = {@At("HEAD")},
           cancellable = true
   )
   private void checkForMaceWorldEvent(int eventId, BlockPos pos, int data, CallbackInfo ci) {
      if (eventId == 2013) {
         if (this.world == null) {
            FaithfulMace.MOGGER.error("World was null when receiving mace smash world event? passing error...");
         }

         MaceClientUtil.spawnSmashAttackParticles(this.world, pos, data);
         ci.cancel();
      }

   }
}