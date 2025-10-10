package aureum.asta.disks.mixin.ports.eliminatedplayers;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.charter.Charter;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    public abstract void readCustomDataFromNbt(NbtCompound nbt);

    public LivingEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @WrapWithCondition(method = "tickStatusEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
    private boolean eplayers$dontSpawnEffectParticles(World world, ParticleEffect particleEffect, double a, double b, double c, double d, double e, double f) {
        return !((LivingEntity)(Object) this instanceof PlayerEntity player && Charter.bannedUuids.contains(player.getUuid()));
    }
    @Inject(method = "canTarget(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void eplayers$canTarget(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if(entity instanceof PlayerEntity player &&  Charter.bannedUuids.contains(player.getUuid()))
            cir.setReturnValue(false);
    }

    @Override
    public boolean isTeammate(Entity other) {
        if(other instanceof PlayerEntity player &&  Charter.bannedUuids.contains(player.getUuid()) && !(((LivingEntity) (Object) this) instanceof PlayerEntity)) {
            return true;
        }
        return super.isTeammate(other);
    }
}