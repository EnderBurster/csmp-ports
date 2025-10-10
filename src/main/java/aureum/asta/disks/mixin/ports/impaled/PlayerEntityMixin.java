package aureum.asta.disks.mixin.ports.impaled;

import aureum.asta.disks.enchantment.BetterImpaling;
import aureum.asta.disks.ports.impaled.SincereLoyalty;
import aureum.asta.disks.ports.impaled.TridentRecaller;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements TridentRecaller {

    @NotNull
    @Unique
    private TridentRecaller.RecallStatus recallingTrident = TridentRecaller.RecallStatus.NONE;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F"), ordinal = 1)
    private float getAttackDamage(float baseDamage, Entity target) {
        return baseDamage + BetterImpaling.getAttackDamage(this.getMainHandStack(), target);
    }

    @Override
    public RecallStatus getCurrentRecallStatus() {
        return this.recallingTrident;
    }

    @Override
    public void updateRecallStatus(RecallStatus recallingTrident) {
        if (this.recallingTrident != recallingTrident) {
            this.recallingTrident = recallingTrident;
            if (!this.world.isClient) {
                PacketByteBuf res = PacketByteBufs.create();
                res.writeInt(this.getId());
                res.writeEnumConstant(recallingTrident);
                Packet<?> packet = ServerPlayNetworking.createS2CPacket(SincereLoyalty.RECALLING_MESSAGE_ID, res);
                ((ServerPlayerEntity) (Object) this).networkHandler.sendPacket(packet);
                for (ServerPlayerEntity player : PlayerLookup.tracking(this)) {
                    player.networkHandler.sendPacket(packet);
                }
            }
        }
    }

}
