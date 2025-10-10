package aureum.asta.disks.mixin.ports.arsenal;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.cca.PickYourPoisonEntityComponents;
import aureum.asta.disks.index.ArsenalStatusEffects;
import aureum.asta.disks.init.AstaEnchantments;
import aureum.asta.disks.item.CustomHitParticleItem;
import aureum.asta.disks.item.CustomHitSoundItem;
import aureum.asta.disks.item.ScytheItem;
import aureum.asta.disks.ports.impaled.SincereLoyalty;
import aureum.asta.disks.ports.impaled.TridentRecaller;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import moriyashiine.enchancement.common.util.EnchancementUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static aureum.asta.disks.ports.other.ReachEntityAttributes.getAttackRange;
import static aureum.asta.disks.ports.other.ReachEntityAttributes.getSquaredAttackRange;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Shadow public abstract float getAttackCooldownProgress(float baseTime);

    @Shadow public abstract void disableShield(boolean sprinting);

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "attack",
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"
            )}
    )
    private void enableSweepingAttack(Entity target, CallbackInfo info, @Local(ordinal = 3) LocalBooleanRef localRef)
    {
        if (this.getStackInHand(Hand.MAIN_HAND).getItem() instanceof ScytheItem) {
            localRef.set(true);
        }
    }

    @Inject(
            method = "spawnSweepAttackParticles",
            at = {@At(
                    value = "HEAD"
            )
            },
            cancellable = true
    )
    private void removeParticles(CallbackInfo info)
    {
        if (this.getStackInHand(Hand.MAIN_HAND).getItem() instanceof ScytheItem) {
            info.cancel();
        }
    }

    @Inject(
            method = {"attack"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;addCritParticles(Lnet/minecraft/entity/Entity;)V"
            )}
    )
    private void scytheReelTargetOnCrit(Entity target, CallbackInfo ci) {
        if (this.getStackInHand(Hand.MAIN_HAND).getItem() instanceof ScytheItem) {
            float strength = 0.3F;

            if (target instanceof LivingEntity livingEntity) {
                strength = (float)(0.25 * (1.0 - livingEntity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)));
                livingEntity.addStatusEffect(new StatusEffectInstance(ArsenalStatusEffects.STUN, 10, 0, false, false, false));
            }

            target.setVelocity(this.getPos().subtract(target.getPos()).multiply((double)strength));
            target.velocityModified = true;
        }
    }

    @Inject(
            method = {"takeShieldHit"},
            at = {@At("HEAD")}
    )
    protected void scytheDisableShield(LivingEntity attacker, CallbackInfo ci) {
        if (attacker.getMainHandStack().getItem() instanceof ScytheItem) {
            this.disableShield(true);
        }
    }

    @Inject(
            method = {"attack"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F"
            )}
    )
    private void spawnCustomHitParticlesAndPlayCustomHitSound(Entity target, CallbackInfo ci) {
        if (this.getAttackCooldownProgress(0.5F) > 0.9F) {
            if (this.getMainHandStack().getItem() instanceof CustomHitParticleItem customHitParticleItem) {
                customHitParticleItem.spawnHitParticles((PlayerEntity)(LivingEntity)this);
            }

            if (this.getMainHandStack().getItem() instanceof CustomHitSoundItem customHitSoundItem) {
                customHitSoundItem.playHitSound((PlayerEntity)(LivingEntity)this);
            }
        }
    }
}