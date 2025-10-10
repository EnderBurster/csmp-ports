package aureum.asta.disks.mixin.ports.charter;

import aureum.asta.disks.ports.charter.common.component.CharterArmComponent;
import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import aureum.asta.disks.ports.charter.common.component.CharterPlayerComponent;
import aureum.asta.disks.ports.charter.common.entity.ChainsEntity;
import aureum.asta.disks.ports.charter.common.entity.EpitaphChainsEntity;
import aureum.asta.disks.ports.charter.common.interfaces.LockedTransport;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PlayerEntity.class})
public abstract class PlayerEntityMixin extends LivingEntity {
   @Shadow
   @Final
   private PlayerAbilities abilities;

   @Shadow
   public abstract PlayerInventory getInventory();

   @Shadow
   public abstract HungerManager getHungerManager();

   @Shadow
   public abstract ItemStack getEquippedStack(EquipmentSlot slot);

   @Shadow
   public abstract Arm getMainArm();

   @Shadow
   public abstract boolean isCreative();

   protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
      super(entityType, world);
   }

   @Inject(
      method = {"isPartVisible"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void charter$isPartVisible(PlayerModelPart part, CallbackInfoReturnable<Boolean> cir) {
      if ((
            part.equals(PlayerModelPart.LEFT_SLEEVE) && this.getMainArm() == Arm.RIGHT
               || part.equals(PlayerModelPart.RIGHT_SLEEVE) && this.getMainArm() == Arm.LEFT
         )
         && (
            ((CharterArmComponent)this.getComponent(CharterComponents.ARM_COMPONENT)).handicap
               || ((CharterArmComponent)this.getComponent(CharterComponents.ARM_COMPONENT)).armOwner != null
               || ((CharterArmComponent)this.getComponent(CharterComponents.ARM_COMPONENT)).arm != null
         )) {
         cir.setReturnValue(false);
      }
   }

   @Inject(
      method = {"shouldDismount"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void charter$dismount(CallbackInfoReturnable<Boolean> cir) {
      if (this.getVehicle() instanceof LockedTransport) {
         cir.setReturnValue(false);
      }
   }

   @Inject(
      method = {"getDisplayName"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void charter$name(CallbackInfoReturnable<Text> cir) {
      if (((CharterPlayerComponent)this.getComponent(CharterComponents.PLAYER_COMPONENT)).newName != null) {
         cir.setReturnValue(Text.literal(((CharterPlayerComponent)this.getComponent(CharterComponents.PLAYER_COMPONENT)).newName));
      }
   }
}
