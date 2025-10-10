package aureum.asta.disks.mixin.ports.eliminatedplayers;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.charter.Charter;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow
    @Final
    private List<ServerPlayerEntity> players;

    @WrapWithCondition(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"))
    private boolean eplayers$playerLeave(PlayerManager manager, Text text, boolean bl, ClientConnection connect, ServerPlayerEntity player) {
        return !Charter.bannedUuids.contains(player.getUuid());
    }
    @Inject(method = "broadcast(Lnet/minecraft/text/Text;Z)V", at = @At("HEAD"), cancellable = true)
    private void eplayers$actuallyDontBroadcast(Text message, boolean overlay, CallbackInfo ci) {
        if (message.getContent() instanceof TranslatableTextContent translatable) {
            String key = translatable.getKey();

            // Grab first arg that is a Text
            Optional<Object> texts = Arrays.stream(translatable.getArgs())
                    .filter(obj -> obj instanceof Text)
                    .findFirst();

            if (key.equals("multiplayer.player.left")
                    && texts.isPresent()
                    && ((Text) texts.get()).getString().contains("Mouthpiece")) {
                ci.cancel();
            }
        }
    }
    @Inject(method = "broadcast(Lnet/minecraft/network/message/SignedMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)V", at = @At("HEAD"), cancellable = true)
    private void eplayers$noBroadcasty(SignedMessage message, Predicate<ServerPlayerEntity> shouldSendFiltered, @Nullable ServerPlayerEntity sender, MessageType.Parameters params, CallbackInfo ci) {
        if(message.getContent().getString().startsWith("/")) return;
        if(sender == null) return;
        if(Charter.bannedUuids.contains(sender.getUuid())) {
            ci.cancel();
        }
    }
    @ModifyReturnValue(method = "getPlayerNames", at = @At("RETURN"))
    private String[] eplayers$dontGetAllPlayersArgType(String[] original) {
        for(int i = 0; i < this.players.size(); ++i) {
            if(!Charter.bannedUuids.contains(this.players.get(i).getGameProfile().getId())) {
                original[i] = this.players.get(i).getGameProfile().getName();
            }
        }
        return original;
    }
}