package aureum.asta.disks.mixin.ports.eliminatedplayers;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.charter.Charter;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerEntity.class, priority = 0)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
    @Inject(method = "dropInventory", at = @At("HEAD"), cancellable = true)
    private void eplayers$dropInv(CallbackInfo ci) {
        if(Charter.bannedUuids.contains(this.getUuid())) {
            ci.cancel();
        }
    }

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void eplayers$getName(CallbackInfoReturnable<Text> cir) {
        if(Charter.bannedUuids.contains(this.getUuid())) {
            cir.setReturnValue(Text.literal("Mouthpiece"));
        }
    }
    @Inject(method = "getEntityName", at = @At("HEAD"), cancellable = true)
    private void eplayers$getEntityName(CallbackInfoReturnable<String> cir) {
        if(Charter.bannedUuids.contains(this.getUuid())) {
            cir.setReturnValue("Mouthpiece");
        }
    }
    @Inject(method = "addTellClickEvent", at = @At("HEAD"), cancellable = true)
    private void eplayers$removeClickEvent(MutableText component, CallbackInfoReturnable<MutableText> cir)
    {
        if(Charter.bannedUuids.contains(this.getUuid())) {
            cir.setReturnValue(component);
        }
    }
}