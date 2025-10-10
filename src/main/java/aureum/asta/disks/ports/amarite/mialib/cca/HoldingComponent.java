package aureum.asta.disks.ports.amarite.mialib.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import aureum.asta.disks.ports.amarite.mialib.MiaLib;

public class HoldingComponent implements AutoSyncedComponent, CommonTickingComponent {
   private final PlayerEntity player;
   private boolean attacking = false;
   private boolean using = false;
   private int tickAttacking = 0;
   private int tickUsing = 0;
   private boolean wasAttacking = false;
   private boolean wasUsing = false;

   public HoldingComponent(PlayerEntity player) {
      this.player = player;
   }

   public void sync() {
      MiaLib.HOLDING.sync(this.player);
   }

   public boolean isAttacking() {
      return this.attacking;
   }

   public boolean startedAttacking() {
      return this.tickAttacking == 1;
   }

   public boolean hasBeenAttacking() {
      return this.attacking && this.wasAttacking;
   }

   public boolean stoppedAttacking() {
      return !this.attacking && this.wasAttacking;
   }

   public void setAttacking(boolean attacking) {
      this.attacking = attacking;
      this.sync();
   }

   public boolean isUsing() {
      return this.using;
   }

   public boolean startedUsing() {
      return this.tickUsing == 1;
   }

   public boolean hasBeenUsing() {
      return this.using && this.wasUsing;
   }

   public boolean stoppedUsing() {
      return !this.using && this.wasUsing;
   }

   public void setUsing(boolean using) {
      this.using = using;
      this.sync();
   }

   public int getTickAttacking() {
      return this.tickAttacking;
   }

   public int getTickUsing() {
      return this.tickUsing;
   }

   public void tick() {
      if (this.attacking) {
         this.tickAttacking++;
      } else {
         this.tickAttacking = 0;
      }

      if (this.using) {
         this.tickUsing++;
      } else {
         this.tickUsing = 0;
      }

      this.wasAttacking = this.attacking;
      this.wasUsing = this.using;
   }

   public void readFromNbt(@NotNull NbtCompound tag) {
      this.attacking = tag.getBoolean("attacking");
      this.using = tag.getBoolean("using");
      this.tickAttacking = tag.getInt("tickAttacking");
      this.tickUsing = tag.getInt("tickUsing");
      this.wasAttacking = tag.getBoolean("wasAttacking");
      this.wasUsing = tag.getBoolean("wasUsing");
   }

   public void writeToNbt(@NotNull NbtCompound tag) {
      tag.putBoolean("attacking", this.attacking);
      tag.putBoolean("using", this.using);
      tag.putInt("tickAttacking", this.tickAttacking);
      tag.putInt("tickUsing", this.tickUsing);
      tag.putBoolean("wasAttacking", this.wasAttacking);
      tag.putBoolean("wasUsing", this.wasUsing);
   }
}
