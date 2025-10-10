package aureum.asta.disks.mixin.ports.mason;

import aureum.asta.disks.ports.mason.init.MasonObjects;
import aureum.asta.disks.ports.mason.util.GlaivePacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MinecraftClient.class})
public abstract class MinecraftClientMixin {
   @Shadow
   @Nullable
   public ClientPlayerEntity player;
   @Shadow
   public int attackCooldown;
   @Shadow
   @Nullable
   public HitResult crosshairTarget;
   @Unique
   private boolean attackQueued = false;

   @Inject(
      method = {"handleInputEvents"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/MinecraftClient;doAttack()Z",
         ordinal = 0
      )}
   )
   public void glaiveStab(CallbackInfo info) {
      if (player != null
         && player.getStackInHand(player.getActiveHand()).isOf(MasonObjects.GLAIVE)
         && player.getAttackCooldownProgress(0.5F) == 1.0F
         && !player.getItemCooldownManager().isCoolingDown(player.getMainHandStack().getItem())
         && crosshairTarget != null) {
         GlaivePacket.send(crosshairTarget.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)crosshairTarget).getEntity() : null);
         if (crosshairTarget.getType() == HitResult.Type.BLOCK) {
            player.resetLastAttackedTicks();
         }
      }

      if (!info.isCancelled() && this.attackQueued) {
         this.attackQueued = false;
      }
   }
}
