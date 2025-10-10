package aureum.asta.disks.mixin.ports.amarite.mialib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import aureum.asta.disks.ports.amarite.mialib.MiaLib;

@Mixin({MinecraftClient.class})
public class MinecraftClientMixin {
   @Shadow
   @Final
   public GameOptions options;
   @Shadow
   @Nullable
   public ClientPlayerEntity player;
   @Unique
   private boolean mialib$holding = false;
   @Unique
   private boolean mialib$using = false;

   @WrapOperation(
      method = {"handleInputEvents"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/MinecraftClient;handleBlockBreaking(Z)V"
      )}
   )
   private void mialib$holding(MinecraftClient instance, boolean bl, Operation<Void> original) {
      boolean holding = this.options.attackKey.isPressed();
      if (holding != this.mialib$holding) {
         this.mialib$holding = holding;
         PacketByteBuf buf = PacketByteBufs.create();
         buf.writeBoolean(holding);
         ClientPlayNetworking.send(MiaLib.id("attacking"), buf);
      }

      boolean using = this.options.useKey.isPressed();
      if (using != this.mialib$using) {
         this.mialib$using = using;
         PacketByteBuf buf = PacketByteBufs.create();
         buf.writeBoolean(using);
         ClientPlayNetworking.send(MiaLib.id("using"), buf);
      }

      original.call(instance, bl);
   }
}
