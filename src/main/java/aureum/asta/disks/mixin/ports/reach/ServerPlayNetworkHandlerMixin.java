package aureum.asta.disks.mixin.ports.reach;

import aureum.asta.disks.cca.BackWeaponComponent;
import aureum.asta.disks.ports.impaled.LoyalTrident;
import aureum.asta.disks.ports.other.ReachEntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
abstract class ServerPlayNetworkHandlerMixin implements ServerPlayPacketListener {
    @Shadow public ServerPlayerEntity player;

    /**
     * Prevents players from interacting with entities that are too far away.
     *
     * <p>Attack range is further checked in PlayerEntityInteractionHandlerMixin.
     */
    @Redirect(
        method = "onPlayerInteractEntity(Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket;)V",
        at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;MAX_BREAK_SQUARED_DISTANCE:D", opcode = Opcodes.GETSTATIC))
    private double getActualAttackRange() {
        return ReachEntityAttributes.getSquaredReachDistance(this.player, ServerPlayNetworkHandler.MAX_BREAK_SQUARED_DISTANCE);
    }

    @Redirect(
        method = "onPlayerInteractBlock(Lnet/minecraft/network/packet/c2s/play/PlayerInteractBlockC2SPacket;)V",
        at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;MAX_BREAK_SQUARED_DISTANCE:D", opcode = Opcodes.GETSTATIC))
    private double getActualReachDistance() {
        return ReachEntityAttributes.getSquaredReachDistance(this.player, ServerPlayNetworkHandler.MAX_BREAK_SQUARED_DISTANCE);
    }

    @ModifyConstant(
        method = "onPlayerInteractBlock(Lnet/minecraft/network/packet/c2s/play/PlayerInteractBlockC2SPacket;)V",
        require = 1, allow = 1, constant = @Constant(doubleValue = 64.0))
    private double getActualReachDistance(final double reachDistance) {
        return ReachEntityAttributes.getSquaredReachDistance(this.player, reachDistance);
    }
}
