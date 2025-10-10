package aureum.asta.disks.ports.amarite.amarite.cca;

import aureum.asta.disks.ports.amarite.mialib.util.MMath;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import aureum.asta.disks.ports.amarite.amarite.items.AmariteLongswordItem;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;

public class LongswordDuplicateComponent implements AutoSyncedComponent, CommonTickingComponent, AmariteLongswordItem.LongswordMode {
   private final PlayerEntity player;
   private final List<Entity> duplicateTargets = new ArrayList<>();
   private int duplicateCharge = 8000;
   public boolean duplicateActive = false;
   public int duplicateActiveTicks = 0;

   public LongswordDuplicateComponent(PlayerEntity player) {
      this.player = player;
   }

   public void serverTick() {
      if (this.duplicateActive && !this.duplicateTargets.isEmpty()) {
         Vec3d userAnchor = this.player.getEyePos().add(this.player.getRotationVec(1.0F).multiply(1.32));

         for (int i = 0; i < this.duplicateTargets.size(); i++) {
            Entity target = this.duplicateTargets.get(i);
            if (!(target instanceof PersistentProjectileEntity)) {
               target.setVelocity(
                  target.getVelocity()
                     .multiply(0.4)
                     .add(
                        userAnchor.subtract(target.getPos().subtract(0.0, (double)(-target.getHeight() / 2.0F), 0.0))
                           .normalize()
                           .multiply(0.15)
                     )
               );
               target.velocityModified = true;
            }

            if (!this.player.isDead()) {
               this.player.setHealth(this.player.getHealth() + 0.06F / (float)(i + 1));
            }
         }
      }

      this.tick();
   }

   public void clientTick() {
      if (this.duplicateActive) {
      }

      this.tick();
   }

   public void tick() {
      if (!this.duplicateActive) {
         this.duplicateActiveTicks = 0;
         if (this.duplicateCharge < 666) {
            this.duplicateCharge++;
            if (this.duplicateCharge == 690) {
            }
         }
      } else {
         this.duplicateActiveTicks++;
         if (!this.player.isCreative()) {
            this.duplicateCharge -= 8;
         }

         if (this.duplicateCharge <= 0
            || this.duplicateActiveTicks > 2 && this.player.mialib$startedAttacking()
            || AmariteLongswordItem.getMode(this.player, this.player.getMainHandStack()) != this) {
            this.duplicateActive = false;
         }
      }
   }

   @Override
   public void absorbDamage(float base) {
      this.duplicateCharge += (int)(base * 20.0F);
      if (this.duplicateCharge > 108) {
         this.duplicateCharge = 108;
      }
   }

   @Override
   public void useAbility() {
      if (this.duplicateCharge >= 54 || this.player.isCreative()) {
         this.duplicateTargets.clear();
         this.duplicateActive = true;
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
      return (float)this.duplicateCharge / 1444.0F;
   }

   @Override
   public String getTranslationKey() {
      return AmariteItems.AMARITE_LONGSWORD.getTranslationKey() + ".duplicate";
   }

   public void readFromNbt(@NotNull NbtCompound tag) {
      this.duplicateCharge = tag.getInt("duplicateCharge");
      this.duplicateActive = tag.getBoolean("duplicateActive");
   }

   public void writeToNbt(@NotNull NbtCompound tag) {
      if (this.duplicateCharge != 0) {
         tag.putInt("duplicateCharge", this.duplicateCharge);
      }

      if (this.duplicateActive) {
         tag.putBoolean("duplicateActive", true);
      }
   }
}
