package aureum.asta.disks.ports.amarite.amarite.cca;

import aureum.asta.disks.ports.amarite.mialib.util.MMath;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import aureum.asta.disks.ports.amarite.amarite.items.AmariteLongswordItem;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;

public class LongswordDrainComponent implements AutoSyncedComponent, CommonTickingComponent, AmariteLongswordItem.LongswordMode {
   private final PlayerEntity player;
   private final List<Entity> drainVictims = new ArrayList<>();
   private int drainBoost = 8000;
   public boolean drained = false;
   public int drainTime = 0;

   public LongswordDrainComponent(PlayerEntity player) {
      this.player = player;
   }

   public void serverTick() {
      if (this.drained && !this.drainVictims.isEmpty()) {
         for (Entity target : this.drainVictims) {
            target.damage(target.getDamageSources().freeze(), 1.0F);
         }
      }

      this.tick();
   }

   public void clientTick() {
      if (this.drained) {
      }

      this.tick();
   }

   public void tick() {
      if (!this.drained) {
         this.drainTime = 0;
         if (this.drainBoost < 666) {
            this.drainBoost++;
            if (this.drainBoost == 690) {
            }
         }
      } else {
         this.drainTime++;
         if (!this.player.isCreative()) {
            this.drainBoost -= 8;
         }

         if (this.drainBoost <= 0
            || this.drainTime > 2 && this.player.mialib$startedAttacking()
            || AmariteLongswordItem.getMode(this.player, this.player.getMainHandStack()) != this) {
            this.drained = false;
         }
      }
   }

   @Override
   public void absorbDamage(float base) {
      this.drainBoost += (int)(base * 20.0F);
      if (this.drainBoost > 108) {
         this.drainBoost = 108;
      }
   }

   @Override
   public void useAbility() {
      if (this.drainBoost >= 54 || this.player.isCreative()) {
         this.drainVictims.clear();
         this.drained = true;
      }
   }

   @Override
   public int getModeColor() {
      return -95873007;
   }

   @Override
   public int getSwordTint() {
      float percent = MathHelper.clamp(this.getChargeProgress(), 0.0F, 1.0F);
      int r = (int)(255.0F - percent * (float)(255 - (this.getModeColor() >> 16 & 0xFF)));
      int g = (int)(255.0F - percent * (float)(255 - (this.getModeColor() >> 8 & 0xFF)));
      int b = (int)(255.0F - percent * (float)(255 - (this.getModeColor() & 0xFF)));
      return MMath.packRgb(r, g, b);
   }

   @Override
   public float getChargeProgress() {
      return (float)this.drainBoost / 1444.0F;
   }

   @Override
   public String getTranslationKey() {
      return AmariteItems.AMARITE_LONGSWORD.getTranslationKey() + ".drain";
   }

   public void readFromNbt(@NotNull NbtCompound tag) {
      this.drainBoost = tag.getInt("drainCharge");
      this.drained = tag.getBoolean("drainActive");
   }

   public void writeToNbt(@NotNull NbtCompound tag) {
      if (this.drainBoost != 0) {
         tag.putInt("drainCharge", this.drainBoost);
      }

      if (this.drained) {
         tag.putBoolean("drainActive", true);
      }
   }
}
