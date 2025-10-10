package aureum.asta.disks.mixin.ports.impaled;

import aureum.asta.disks.ports.impaled.LoyalTrident;
import aureum.asta.disks.ports.impaled.storage.LoyalTridentStorage;
import aureum.asta.disks.ports.other.ReachEntityAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.UUID;

@Mixin(Item.class)
abstract class ItemMixin implements ItemConvertible {

    @Inject(
            method = "inventoryTick",
            at = @At("RETURN")
    )
    private void updateTridentInInventory(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (entity.age % 10 == 0 && !entity.world.isClient && entity instanceof PlayerEntity) {
            UUID trueOwner = LoyalTrident.getTrueOwner(stack);
            if (Objects.equals(trueOwner, entity.getUuid())) {
                NbtCompound loyaltyData = Objects.requireNonNull(stack.getSubNbt(LoyalTrident.MOD_NBT_KEY));
                if (!Objects.equals(entity.getEntityName(), loyaltyData.getString(LoyalTrident.OWNER_NAME_NBT_KEY))) {
                    loyaltyData.putString(LoyalTrident.OWNER_NAME_NBT_KEY, entity.getEntityName());
                }
            } else if (trueOwner != null) {
                LoyalTridentStorage.get((ServerWorld) world).memorizeTrident(trueOwner, LoyalTrident.getTridentUuid(stack), (PlayerEntity) entity);
            }
        }
    }
}
