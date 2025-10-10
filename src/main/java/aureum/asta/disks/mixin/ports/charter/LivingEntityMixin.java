package aureum.asta.disks.mixin.ports.charter;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.charter.common.component.CharterArmComponent;
import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import aureum.asta.disks.ports.charter.common.component.CharterPlayerComponent;
import aureum.asta.disks.ports.charter.common.component.EyeState;
import aureum.asta.disks.ports.charter.common.damage.CharterDamageSources;
import aureum.asta.disks.ports.charter.common.entity.BrokenDivinityEntity;
import aureum.asta.disks.ports.charter.common.entity.ChainsEntity;
import aureum.asta.disks.ports.charter.common.entity.EpitaphChainsEntity;
import aureum.asta.disks.ports.charter.common.entity.LesserDivinityEntity;
import aureum.asta.disks.ports.charter.common.init.CharterItems;
import aureum.asta.disks.ports.charter.common.item.ContractItem;
import aureum.asta.disks.ports.charter.common.util.CharterUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class})
abstract class LivingEntityMixin extends Entity {
   @Shadow
   public abstract ItemStack getStackInHand(Hand var1);

   @Shadow
   public abstract boolean hasStatusEffect(StatusEffect var1);

   @Shadow public abstract boolean tryAttack(Entity target);

   @Shadow public abstract float getYaw(float tickDelta);

   @Shadow protected abstract void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition);

   public LivingEntityMixin(EntityType<?> entityType, World world) {
      super(entityType, world);
   }

   @Inject(
      method = {"damage"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void charter$damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
      if (!this.world.isClient) {
         LivingEntity cmp = (LivingEntity)(Object)this;
         if (cmp instanceof PlayerEntity pl) {
            CharterPlayerComponent cmpx = (CharterPlayerComponent)this.getComponent(CharterComponents.PLAYER_COMPONENT);

            if (source.getAttacker() instanceof PlayerEntity player
               && ((CharterPlayerComponent)player.getComponent(CharterComponents.PLAYER_COMPONENT)).ownerUUID != null
               && ((CharterPlayerComponent)player.getComponent(CharterComponents.PLAYER_COMPONENT)).ownerUUID.equals(this.getUuid())) {
               player.damage(source, amount);
               cir.setReturnValue(false);
            }

            if (source.getAttacker() instanceof PlayerEntity player
               && player.getUuid().equals(cmpx.ownerUUID)
               && player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof ContractItem
               && ContractItem.isViable(player.getStackInHand(Hand.MAIN_HAND))
               && ContractItem.getIndebtedUUID(player.getStackInHand(Hand.MAIN_HAND)).equals(this.getUuid())
               && !((CharterArmComponent)pl.getComponent(CharterComponents.ARM_COMPONENT)).handicap) {
               ItemStack stack = player.getStackInHand(Hand.OFF_HAND);
               if (stack.getItem() instanceof SwordItem) {
                  ((CharterArmComponent)pl.getComponent(CharterComponents.ARM_COMPONENT)).handicap = true;
                  ((CharterArmComponent)pl.getComponent(CharterComponents.ARM_COMPONENT)).armOwner = null;
                  CharterComponents.ARM_COMPONENT.sync(pl);
                  ItemStack st = CharterItems.HAND.getDefaultStack();
                  NbtCompound compound = new NbtCompound();
                  NbtHelper.writeGameProfile(compound, pl.getGameProfile());
                  st.getOrCreateNbt().put("owner", compound);
                  st.getOrCreateNbt().putUuid("yeah", player.getUuid());
                  st.getOrCreateNbt().putString("name", this.getDisplayName().getString());
                  player.giveItemStack(st);
                  if (!this.getStackInHand(Hand.OFF_HAND).isEmpty()) {
                     this.dropStack(this.getStackInHand(Hand.OFF_HAND));
                     this.getStackInHand(Hand.OFF_HAND).decrement(this.getStackInHand(Hand.OFF_HAND).getCount());
                  }
               }

               if (stack.getItem().equals(Items.SPIDER_EYE)) {
                  cmpx.eyes = EyeState.BLINDED;
                  cmpx.sync();
               }
            }
         }
      }
   }

   @Inject(
      method = {"tryUseTotem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void charter$tryUseTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
      LivingEntity entity = (LivingEntity)(Object)this;

      if(source.isOf(CharterDamageSources.EPITAPH_BAN)) cir.setReturnValue(false);

      if(source.getAttacker() != null && source.getAttacker() instanceof PlayerEntity attacker && attacker.getOffHandStack().isOf(CharterItems.LESSER_DIVINITY))
      {
         if (!(this.getVehicle() instanceof LesserDivinityEntity)) {
            LesserDivinityEntity divinity = new LesserDivinityEntity(this.getWorld());
            divinity.setPosition(this.getPos().add(0.0, 0.0, 0.0));
            divinity.setYaw(this.getYaw());
            this.startRiding(divinity, true);
            this.getWorld().spawnEntity(divinity);

            if(attacker.getOffHandStack().getDamage() == 2) divinity.setLastUse(true);
         }

         attacker.getOffHandStack().damage(1, attacker, p -> p.sendToolBreakStatus(attacker.preferredHand));
         entity.setHealth(entity.getMaxHealth());
         cir.setReturnValue(true);
      }
      else if(source.getAttacker() != null && source.getAttacker() instanceof PlayerEntity attacker && attacker.getOffHandStack().isOf(CharterItems.BROKEN_LESSER_DIVINITY))
      {
         if (!(this.getVehicle() instanceof BrokenDivinityEntity)) {
            BrokenDivinityEntity divinity = new BrokenDivinityEntity(this.getWorld());
            divinity.setPosition(this.getPos().add(0.0, 0.0, 0.0));
            divinity.setYaw(this.getYaw());
            this.startRiding(divinity, true);
            this.getWorld().spawnEntity(divinity);

            if(attacker.getOffHandStack().getDamage() == 2) divinity.setLastUse(true);
         }

         attacker.getOffHandStack().damage(1, attacker, p -> p.sendToolBreakStatus(attacker.preferredHand));
         entity.setHealth(entity.getMaxHealth());
         cir.setReturnValue(true);
      }

      if (entity instanceof PlayerEntity player) {
         if(player.getComponent(CharterComponents.PLAYER_COMPONENT).tantalus)
         {
            player.setHealth(1);
            cir.setReturnValue(true);
         }

         if(source.isOf(CharterDamageSources.EPITAPH) && source.getAttacker() != null)
         {
            if (!(this.getVehicle() instanceof EpitaphChainsEntity)) {
               EpitaphChainsEntity chains = new EpitaphChainsEntity(this.getWorld(), source.getAttacker().getUuid(), true);
               chains.setPosition(this.getPos().add(0.0, 0.3, 0.0));
               chains.setYaw(this.getYaw());
               AureumAstaDisks.LOGGER.info("Chains Yaw: {}", this.getYaw());
               this.startRiding(chains, true);
               this.getWorld().spawnEntity(chains);
            }

            player.setHealth(player.getMaxHealth());
            cir.setReturnValue(true);
         }
         else if(source.getAttacker() != null && source.getAttacker().getUuid().equals(((CharterPlayerComponent)this.getComponent(CharterComponents.PLAYER_COMPONENT)).ownerUUID) && !source.isOf(DamageTypes.OUT_OF_WORLD))
         {
            if (!(this.getVehicle() instanceof ChainsEntity)) {
               ChainsEntity chains = new ChainsEntity(
                       this.getWorld(), ((CharterPlayerComponent)this.getComponent(CharterComponents.PLAYER_COMPONENT)).ownerUUID
               );
               chains.setPosition(this.getPos().add(0.0, 0.3, 0.0));
               chains.setYaw(this.getYaw());
               this.startRiding(chains, true);
               this.getWorld().spawnEntity(chains);
            }

            player.setHealth(1.0F);
            cir.setReturnValue(true);
         }
      }
   }

   @Inject(
           method = {"onDeath"},
           at = {@At("TAIL")}
   )
   private void charter$attemptBan(DamageSource source, CallbackInfo ci) {
      LivingEntity entity = (LivingEntity)(Object)this;

      if(entity.getVehicle() instanceof LesserDivinityEntity || source.isOf(CharterDamageSources.EPITAPH_BAN))
      {
         CharterUtils.attemptBan(entity, entity.getServer(), -1, "Your existence was repurposed.");
      }
   }
}
