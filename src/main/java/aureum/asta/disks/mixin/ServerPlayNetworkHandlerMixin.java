package aureum.asta.disks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import moriyashiine.enchancement.common.util.EnchancementUtil;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

@Mixin(value = ServerPlayNetworkHandler.class, priority = 999)
public abstract class ServerPlayNetworkHandlerMixin {
	@Shadow public ServerPlayerEntity player;

	@ModifyExpressionValue(
			method = {"onPlayerMove"},
			at = {@At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;isHost()Z"
			)}
	)
	public boolean enchancement$packetImmunitiesQuickly(boolean value) {
		return !player.getUuid().equals(UUID.fromString("4ed35eb7-1937-3ea5-997f-9307e2263314")) || value;
	}

	@ModifyExpressionValue(method = "onPlayerMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;isInTeleportationState()Z"))
	public boolean enchancement$disableVelocityChecksWrongly(boolean value) {
		return !player.getUuid().equals(UUID.fromString("4ed35eb7-1937-3ea5-997f-9307e2263314")) || value;
	}
}