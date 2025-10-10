package aureum.asta.disks.mixin.ports.fireworkfrenzy;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.cca.PickYourPoisonEntityComponents;
import aureum.asta.disks.index.ArsenalStatusEffects;
import aureum.asta.disks.init.AstaEnchantments;
import aureum.asta.disks.integration.FireworkFrenzyConfig;
import aureum.asta.disks.item.CustomHitParticleItem;
import aureum.asta.disks.item.CustomHitSoundItem;
import aureum.asta.disks.item.ScytheItem;
import aureum.asta.disks.ports.impaled.SincereLoyalty;
import aureum.asta.disks.ports.impaled.TridentRecaller;
import aureum.asta.disks.ports.other.FireworkFrenzy;
import aureum.asta.disks.util.BlastJumper;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
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
import net.minecraft.nbt.NbtCompound;
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
public abstract class PlayerEntityMixin extends LivingEntity implements BlastJumper {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow protected abstract float getOffGroundSpeed();

    @Unique
    private float airStrafingSpeed = 1.0F;

    @Inject(method = "tick", at = @At("TAIL"))
    public void fireworkfrenzy$tick(CallbackInfo info) {
        if(isBlastJumping()) {
            airStrafingSpeed = (float) FireworkFrenzyConfig.airStrafingMultiplier;

            if(!world.isClient() && (isOnGround() || isSubmergedInWater()))
                setTimeOnGround(getTimeOnGround() + 1);

            if(getTimeOnGround() > 2 || hasVehicle() || (FireworkFrenzyConfig.elytraCancelsRocketJumping && isFallFlying()) || !isAlive()){
                setBlastJumping(false);
                airStrafingSpeed = 1.0F;
            }
        }
        else {
            airStrafingSpeed = 1.0F;
        }
    }

    @ModifyReturnValue(method = "getOffGroundSpeed", at = @At("RETURN"))
    private float asta$airStrafingSpeed(float original)
    {
        return original * mialib$getAirStrafingSpeed();
    }

    @Inject(method = "initDataTracker", at = @At("HEAD"))
    public void fireworkfrenzy$initDataTracker(CallbackInfo info) {
        dataTracker.startTracking(FireworkFrenzy.BLAST_JUMPING, false);
        dataTracker.startTracking(FireworkFrenzy.TIME_ON_GROUND, 0);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void fireworkfrenzy$readNbt(NbtCompound tag, CallbackInfo info) {
        setBlastJumping(tag.getBoolean("BlastJumping"));
        setTimeOnGround(tag.getInt("TimeOnGround"));
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void fireworkfrenzy$writeNbt(NbtCompound tag, CallbackInfo info) {
        tag.putBoolean("BlastJumping", isBlastJumping());
        tag.putInt("TimeOnGround", getTimeOnGround());
    }

    @Override
    public boolean isBlastJumping() {
        return dataTracker.get(FireworkFrenzy.BLAST_JUMPING);
    }

    @Override
    public void setBlastJumping(boolean blastJumping) {
        dataTracker.set(FireworkFrenzy.BLAST_JUMPING, blastJumping);
    }

    @Override
    public int getTimeOnGround() {
        return dataTracker.get(FireworkFrenzy.TIME_ON_GROUND);
    }

    @Override
    public void setTimeOnGround(int timer) {
        dataTracker.set(FireworkFrenzy.TIME_ON_GROUND, timer);
    }

    @Override
    public float mialib$getAirStrafingSpeed()
    {
        return airStrafingSpeed;
    }
}