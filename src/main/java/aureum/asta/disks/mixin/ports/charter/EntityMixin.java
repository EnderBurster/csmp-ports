package aureum.asta.disks.mixin.ports.charter;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import aureum.asta.disks.ports.charter.common.component.CharterPlayerComponent;
import aureum.asta.disks.ports.charter.common.component.CharterWorldComponent;
import aureum.asta.disks.ports.charter.common.component.DiamondOfProtection;
import aureum.asta.disks.ports.charter.common.component.GauntletMode;
import com.google.common.collect.ImmutableList.Builder;

import java.util.ArrayList;
import java.util.List;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Entity.class})
public abstract class EntityMixin {
   @Shadow public abstract World getWorld();

   @Shadow protected abstract boolean wouldPoseNotCollide(EntityPose pose);

   @Shadow public abstract void requestTeleport(double destX, double destY, double destZ);

   @ModifyVariable(
      method = {"move"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private Vec3d charter$slowForWheels(Vec3d value, MovementType movementType) {
      if (movementType == MovementType.SELF) {
         CharterPlayerComponent component = (CharterPlayerComponent)CharterComponents.PLAYER_COMPONENT.getNullable(this);
         if (component != null && component.mode == GauntletMode.WHEEL) {
            return value.multiply(0.1, 0.98, 0.1);
         }
      }

      return value;
   }

   @WrapOperation(
           method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
           at = @At(
                   value = "INVOKE",
                   target = "Lnet/minecraft/world/World;getEntityCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Ljava/util/List;"
           )
   )
   private List<VoxelShape> charter$addCustomCollisions(World world, Entity entity, Box box, Operation<List> original) {
      List<VoxelShape> base = original.call(world, entity, box);
      List<VoxelShape> mutable = new ArrayList<>(base);

      for (DiamondOfProtection dia : ((CharterWorldComponent)world.getComponent(CharterComponents.CHARTER)).diamonds) {
         if (!(entity instanceof PlayerEntity)) {
            mutable.add(dia.getVoxelShape());
         }
      }

      return mutable;
   }

   @Inject(
           method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
           at = @At(value = "HEAD"),
           cancellable = true
   )
   private void charter$removeCollisionWhenBroken(Vec3d movement, CallbackInfoReturnable<Vec3d> cir)
   {
      Entity entity = (Entity)(Object)this;

      if(entity instanceof PlayerEntity player && player.getComponent(CharterComponents.PLAYER_COMPONENT).divinityFlying) cir.setReturnValue(movement);
   }
}
