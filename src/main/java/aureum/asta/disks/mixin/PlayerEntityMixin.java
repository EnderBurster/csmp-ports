package aureum.asta.disks.mixin;

import aureum.asta.disks.*;
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
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 2)
    private boolean enchancement$criticalTipper(boolean value, Entity target, @Local(ordinal = 2) float attackCooldownProgress) {
        if (forceCritical(target, attackCooldownProgress, this.getMainHandStack())) {
            return true;
        }
        return value;
    }

    @Unique
    private boolean forceCritical(Entity target, float attackCooldownProgress, ItemStack stack) {
        if (EnchancementUtil.hasEnchantment(AstaEnchantments.APEX, stack) && attackCooldownProgress > 0.9F) {
            return distanceTo(target) > getAttackRange(this, 5) - 0.6F;
        }
        return false;
    }
}