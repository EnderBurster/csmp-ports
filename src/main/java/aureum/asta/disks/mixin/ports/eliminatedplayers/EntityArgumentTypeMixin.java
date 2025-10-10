package aureum.asta.disks.mixin.ports.eliminatedplayers;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.charter.Charter;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;

@Mixin(EntityArgumentType.class)
public class EntityArgumentTypeMixin {
    @ModifyReturnValue(method = "getPlayers", at = @At("RETURN"))
    private static Collection<ServerPlayerEntity> eplayers$dontGetAllPlayersArgType(Collection<ServerPlayerEntity> original) {
        original.removeIf((serverPlayerEntity -> Charter.bannedUuids.contains(serverPlayerEntity.getUuid())));
        return original;
    }
}