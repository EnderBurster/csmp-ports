package aureum.asta.disks.mixin.client;

import aureum.asta.disks.ports.impaled.MialeeMisc;
import aureum.asta.disks.client.sound.BlastJumpingSoundInstance;
import aureum.asta.disks.entity.IPlayerTargeting;
import aureum.asta.disks.util.BlastJumper;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements IPlayerTargeting, BlastJumper {
    @Unique @Nullable LivingEntity lastTarget;
    @Unique int targetDecayTime;

    @Shadow
    @Final
    protected MinecraftClient client;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) { super(world, profile); }

    @Override
    public LivingEntity mialeeMisc$getLastTarget() {
        return this.lastTarget;
    }

    @Override
    public void mialeeMisc$setLastTarget(LivingEntity target) {
        this.lastTarget = target;
        this.targetDecayTime = 60;
        if (target != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(target.getId());
            ClientPlayNetworking.send(MialeeMisc.targetPacket, buf);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void mialeeMisc$decayTarget(CallbackInfo ci) {
        if (this.targetDecayTime > 0) {
            this.targetDecayTime--;
            if (this.targetDecayTime == 0) {
                this.mialeeMisc$setLastTarget(null);
            }
        }
    }

    @Inject(method = "onTrackedDataSet", at = @At("HEAD"))
    public void fireworkfrenzy$onTrackedDataSet(TrackedData<?> data, CallbackInfo info) {
        BlastJumpingSoundInstance soundInstance = new BlastJumpingSoundInstance((ClientPlayerEntity) (Object) this);

        if(isBlastJumping() && !client.getSoundManager().isPlaying(soundInstance))
            client.getSoundManager().play(soundInstance);
    }
}