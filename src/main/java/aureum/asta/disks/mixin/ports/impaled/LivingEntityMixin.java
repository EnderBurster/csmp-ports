package aureum.asta.disks.mixin.ports.impaled;

import aureum.asta.disks.entity.ElderTridentEntity;
import aureum.asta.disks.ports.amarite.mialib.interfaces.MLivingEntity;
import aureum.asta.disks.ports.impaled.SincereLoyalty;
import aureum.asta.disks.ports.impaled.init.ImpaledItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity implements MLivingEntity {
    LivingEntityMixin(final EntityType<?> type, final World world) {
        super(type, world);
    }

    @Unique
    private @Nullable Consumer<ItemStack> impaled$dropSink;

    @Inject(method = "drop", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;shouldDropLoot()Z"))
    private void drop(DamageSource source, CallbackInfo ci) {
        Entity directSource = source.getSource();

        if (directSource instanceof ElderTridentEntity) {
            this.impaled$dropSink = ((ElderTridentEntity) directSource).getStackFetcher();
        }

        if (((Object) this) instanceof ElderGuardianEntity && (directSource instanceof PlayerEntity player && player.getMainHandStack().isIn(SincereLoyalty.TRIDENTS) || (directSource instanceof TridentEntity && EnchantmentHelper.getLoyalty(((TridentEntityAccessor) directSource).impaled$getTridentStack()) > 0))) {
            this.dropStack(new ItemStack(ImpaledItems.ELDER_GUARDIAN_EYE));
            this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.NEUTRAL, 1.0f, 1.0f, true);
        }
    }

    @Inject(method = "drop", at = @At("RETURN"))
    private void endDrop(DamageSource source, CallbackInfo ci) {
        this.impaled$dropSink = null;
    }

    @Override
    public void impaled$dropStack(ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> cir) {
        if (this.impaled$dropSink != null) {
            this.impaled$dropSink.accept(stack);
            cir.setReturnValue(null);
        }
    }
}
