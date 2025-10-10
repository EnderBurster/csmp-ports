package aureum.asta.disks.mixin.barrier;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.amarite.mialib.interfaces.MItemStack;
import moriyashiine.enchancement.common.init.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements MItemStack {

    @Shadow public abstract Item getItem();

    @Unique
    LivingEntity ownerEntity = null;

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void asta$setOwner(World world, Entity entity, int slot, boolean selected, CallbackInfo ci)
    {
        if(entity instanceof LivingEntity living)
        {
            this.asta$setOwnerEntity(living);
        }
    }

    @Override
    public void asta$setOwnerEntity(LivingEntity newOwner)
    {
        ownerEntity = newOwner;
    }

    @Override
    public LivingEntity asta$getOwnerEntity()
    {
        return this.ownerEntity;
    }

}
