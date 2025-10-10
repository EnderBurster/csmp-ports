package aureum.asta.disks.mixin.ports.arsenal;

import aureum.asta.disks.ports.amarite.mialib.interfaces.MEntity;
import aureum.asta.disks.index.ArsenalStatusEffects;
import aureum.asta.disks.ports.impaled.item.HellforkItem;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements MEntity {
    @ModifyReturnValue(
            method = {"isTeammate"},
            at = {@At("RETURN")}
    )
    public boolean arsenal$preventStunnedMobsFromTargeting(boolean original) {
        return ((Object)this) instanceof LivingEntity livingEntity ? livingEntity.hasStatusEffect(ArsenalStatusEffects.STUN) : original;
    }
}
