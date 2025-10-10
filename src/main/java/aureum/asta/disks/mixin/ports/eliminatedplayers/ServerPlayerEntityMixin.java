package aureum.asta.disks.mixin.ports.eliminatedplayers;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.charter.Charter;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos blockPos, float f, GameProfile gameProfile, @Nullable PlayerPublicKey playerPublicKey) {
        super(world, blockPos, f, gameProfile);
    }

    @ModifyVariable(method = "onDeath", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/damage/DamageTracker;getDeathMessage()Lnet/minecraft/text/Text;"), index = 3)
    private Text eplayers$modifyDeathMessage(Text value) {
        if (Charter.bannedUuids.contains(this.getUuid())) {
            if (value.getContent() instanceof TranslatableTextContent translatable) {
                Object[] originalArgs = translatable.getArgs();
                Object[] args = new Object[originalArgs.length];
                System.arraycopy(originalArgs, 0, args, 0, originalArgs.length);

                if (args.length > 0 && args[0] instanceof MutableText nameText) {
                    args[0] = nameText.copy().mialib$withObfuscated(true);
                }

                return Text.translatable(translatable.getKey(), args).setStyle(value.getStyle());
            }
        }
        return value;
    }

    @Inject(method = "sendMessage(Lnet/minecraft/text/Text;Z)V", at = @At("HEAD"), cancellable = true)
    private void eplayers$dontSendMessage(Text message, boolean bl, CallbackInfo ci) {
        if(Charter.bannedUuids.contains(this.getUuid())) {
            ci.cancel();
        }
    }
}