package aureum.asta.disks.mixin.ports.amarite.mialib.mixin.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import aureum.asta.disks.ports.amarite.mialib.MiaLib;
import aureum.asta.disks.ports.amarite.mialib.cca.HoldingComponent;
import aureum.asta.disks.ports.amarite.mialib.interfaces.MPlayerEntity;

@Mixin({PlayerEntity.class})
public abstract class PlayerEntityMixin extends Entity implements MPlayerEntity {
   public PlayerEntityMixin(EntityType<?> type, World world) {
      super(type, world);
   }

   @Override
   public boolean mialib$isAttacking() {
      return ((HoldingComponent)MiaLib.HOLDING.get(this)).isAttacking();
   }

   @Override
   public boolean mialib$startedAttacking() {
      return ((HoldingComponent)MiaLib.HOLDING.get(this)).startedAttacking();
   }

   @Override
   public boolean mialib$hasBeenAttacking() {
      return ((HoldingComponent)MiaLib.HOLDING.get(this)).hasBeenAttacking();
   }

   @Override
   public boolean mialib$stoppedAttacking() {
      return ((HoldingComponent)MiaLib.HOLDING.get(this)).stoppedAttacking();
   }

   @Override
   public int mialib$getAttackingTime() {
      return ((HoldingComponent)MiaLib.HOLDING.get(this)).getTickAttacking();
   }

   @Override
   public boolean mialib$isUsing() {
      return ((HoldingComponent)MiaLib.HOLDING.get(this)).isUsing();
   }

   @Override
   public boolean mialib$startedUsing() {
      return ((HoldingComponent)MiaLib.HOLDING.get(this)).startedUsing();
   }

   @Override
   public boolean mialib$hasBeenUsing() {
      return ((HoldingComponent)MiaLib.HOLDING.get(this)).hasBeenUsing();
   }

   @Override
   public boolean mialib$stoppedUsing() {
      return ((HoldingComponent)MiaLib.HOLDING.get(this)).stoppedUsing();
   }

   @Override
   public int mialib$getUsingTime() {
      return ((HoldingComponent)MiaLib.HOLDING.get(this)).getTickUsing();
   }
}
