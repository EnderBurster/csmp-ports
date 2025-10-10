package aureum.asta.disks.mixin.ports.charter;

import aureum.asta.disks.ports.charter.common.component.CharterArmComponent;
import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import aureum.asta.disks.ports.charter.common.item.GauntletItem;
import aureum.asta.disks.ports.charter.common.util.GauntletPacket;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
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
   protected int attackCooldown;
   @Shadow
   @Nullable
   public HitResult crosshairTarget;
   @Shadow
   @Final
   public GameOptions options;
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
   public void charter$gauntlet(CallbackInfo info) {
      if (this.player != null
         && this.player.getStackInHand(this.player.getActiveHand()).getItem() instanceof GauntletItem
         && this.player.getAttackCooldownProgress(0.5F) == 1.0F
         && !this.player.getItemCooldownManager().isCoolingDown(this.player.getMainHandStack().getItem())
         && this.crosshairTarget != null) {
         GauntletPacket.send(this.crosshairTarget.getType() == Type.ENTITY ? ((EntityHitResult)this.crosshairTarget).getEntity() : null);
         if (this.crosshairTarget.getType() == Type.BLOCK) {
            this.player.resetLastAttackedTicks();
         }
      }

      if (!info.isCancelled() && this.attackQueued) {
         this.attackQueued = false;
      }
   }

   @WrapWithCondition(
      method = {"handleInputEvents"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
      )}
   )
   private boolean charter$hand(ClientPlayNetworkHandler instance, Packet packet) {
      return this.player != null && ((CharterArmComponent)this.player.getComponent(CharterComponents.ARM_COMPONENT)).hasArm();
   }
}
