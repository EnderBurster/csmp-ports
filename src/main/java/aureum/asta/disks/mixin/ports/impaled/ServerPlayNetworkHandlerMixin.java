package aureum.asta.disks.mixin.ports.impaled;

import aureum.asta.disks.cca.BackWeaponComponent;
import aureum.asta.disks.ports.impaled.LoyalTrident;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
abstract class ServerPlayNetworkHandlerMixin implements ServerPlayPacketListener {
    @Shadow public ServerPlayerEntity player;

    @ModifyVariable(method = "onCreativeInventoryAction", at = @At("STORE"/*value = "INVOKE_ASSIGN", target = "Lnet/minecraft/network/packet/c2s/play/CreativeInventoryActionC2SPacket;getItemStack()Lnet/minecraft/item/ItemStack;"*/), ordinal = 0)
    private ItemStack removeTridentUuid(ItemStack copiedStack) {
        NbtCompound NbtCompound = copiedStack.getSubNbt(LoyalTrident.MOD_NBT_KEY);
        if (NbtCompound != null) {
            NbtCompound.remove(LoyalTrident.TRIDENT_UUID_NBT_KEY);  // prevent stupid copies of the exact same trident
        }
        return copiedStack;
    }
}
