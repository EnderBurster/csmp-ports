package aureum.asta.disks.ports.amarite.amarite.cca;

import aureum.asta.disks.ports.amarite.amarite.registry.AmariteDamageTypes;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteEntities;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import aureum.asta.disks.ports.amarite.amarite.Amarite;

public class BuddedComponent implements AutoSyncedComponent, CommonTickingComponent {
   public static final int BUD_MAX_TIME = 4800;
   private final PlayerEntity player;
   public int budTime = 0;

   public BuddedComponent(PlayerEntity playerEntity) {
      this.player = playerEntity;
   }

   public void sync() {
      Amarite.BUDDED.sync(this.player);
   }

   public void tick() {
      if (this.budTime > 0) {
         this.budTime--;
         this.sync();
      }
   }

   public void serverTick()
   {
      if (this.budTime > 0 && this.budTime % 60 == 0 && this.player.hasStatusEffect(AmariteEntities.BUDDING))
      {
         this.player.damage(this.player.getDamageSources().create(AmariteDamageTypes.BUDDING), 1.0F);
      }

      this.tick();
   }

   public int getBudTime() {
      return this.budTime;
   }

   public void setBudTime(int budTime) {
      this.budTime = budTime;
   }

   public void readFromNbt(@NotNull NbtCompound tag) {
      this.budTime = tag.getInt("budtime");
   }

   public void writeToNbt(@NotNull NbtCompound tag) {
      tag.putInt("budtime", this.budTime);
   }
}
