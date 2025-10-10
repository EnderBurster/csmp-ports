package aureum.asta.disks.mixin.ports.amarite.amarite.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.Objects;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteEntities;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;

@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin extends Entity {
   @Shadow
   public abstract float getHealth();

   public LivingEntityMixin(EntityType<?> type, World world) {
      super(type, world);
   }

   @WrapOperation(
      method = {"damage"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V"
      )}
   )
   private void amarite$pull(LivingEntity instance, double strength, double x, double z, Operation<Void> original, @NotNull DamageSource source, float amount) {
      if (!Objects.equals(source.getName(), "amarite.accumulate")) {
         original.call(instance, strength, x, z);
      }
   }

   @Inject(
      method = {"setHealth"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void amarite$setHealth(float health, CallbackInfo ci) {
      LivingEntity entity = (LivingEntity)(Object)this;
      if (health > this.getHealth() && entity.hasStatusEffect(AmariteEntities.BUDDING)) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"dropEquipment"},
      at = {@At("TAIL")}
   )
   private void amarite$cookie(@NotNull DamageSource source, int lootingMultiplier, boolean allowDrops, CallbackInfo ci) {
      if (source.getAttacker() instanceof PlayerEntity && "d93dde4b-7b15-4e7f-a860-03a760f2aad7".equals(this.getUuidAsString())) {
         this.dropStack(AmariteItems.WIN_COOKIE.getDefaultStack());
      }

      if (source.getAttacker() instanceof PlayerEntity && ("2cbe79d0-c952-495f-b0ce-6fdea9199328".equals(this.getUuidAsString()) || "81b3fc89-b935-315d-9c8d-934b0ef00076".equals(this.getUuidAsString()))) {
         this.dropStack(AmariteItems.END_COOKIE.getDefaultStack());
      }

      LivingEntity entity = (LivingEntity)(Object)this;
      if (entity.hasStatusEffect(AmariteEntities.BUDDING)) {
         for (int i = 0; i < 3; i++) {
            this.dropStack(new ItemStack(AmariteItems.AMARITE_SHARD));
         }

         this.world.playSoundFromEntity(null, this, SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK, SoundCategory.PLAYERS, 1.0F, 0.75F);
      }
   }
}
