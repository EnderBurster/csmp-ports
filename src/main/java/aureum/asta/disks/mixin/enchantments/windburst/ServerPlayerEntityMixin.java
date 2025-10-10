package aureum.asta.disks.mixin.enchantments.windburst;

import aureum.asta.disks.interfaces.WindBurstHolder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
	/*@WrapOperation(method = "onExplodeBy", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setIgnoreFallDamageFromCurrentExplosion(Z)V"))
	private void enchancement$rebalanceEnchantments(ServerPlayerEntity instance, boolean ignoreFallDamage, Operation<Void> original, Entity entity) {
		original.call(instance, ignoreFallDamage && (!(entity instanceof WindBurstHolder windBurstHolder) || !windBurstHolder.enchancement$fromWindBurst()));
	}*/
}