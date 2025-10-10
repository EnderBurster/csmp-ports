package aureum.asta.disks.mixin.ports.pickyourpoison;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.cca.PickYourPoisonEntityComponents;
import aureum.asta.disks.init.AstaEnchantments;
import aureum.asta.disks.ports.impaled.SincereLoyalty;
import aureum.asta.disks.ports.impaled.TridentRecaller;
import com.llamalad7.mixinextras.sugar.Local;
import moriyashiine.enchancement.common.util.EnchancementUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
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

    @Inject(method = "applyDamage", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/PlayerEntity;getHealth()F", ordinal = 0), cancellable = true)
    private void saveFromNumbness(DamageSource source, float amount, CallbackInfo ci) {
        if (!getWorld().isClient && getHealth() - amount <= 0) {
            PickYourPoisonEntityComponents.NUMBNESS_DAMAGE.maybeGet(this).ifPresent(retributionComponent -> {
                if (retributionComponent.getDamageAccumulated() > 0 && !retributionComponent.isFromLicking()) {
                    this.setHealth(1.0f);
                    ci.cancel();
                }
            });
        }
    }
}