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

public class LongswordRubberbandComponent implements AutoSyncedComponent, CommonTickingComponent, AmariteLongswordItem.LongswordMode {
   private final PlayerEntity player;
   private final List<Entity> rubberTargets = new ArrayList<>();
   private int rubberCharge = 8000;
   public boolean rubberActive = false;
   public int rubberActiveTicks = 0;

   public LongswordRubberbandComponent(PlayerEntity player) {
      this.player = player;
   }

   public void serverTick() {
      if (this.rubberActive && !this.rubberTargets.isEmpty()) {
         Vec3d userAnchor = this.player.getEyePos().add(this.player.getRotationVec(1.0F).multiply(1.32));

         for (int i = 0; i < this.rubberTargets.size(); i++) {
            Entity target = this.rubberTargets.get(i);
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
      if (this.rubberActive) {
      }

      this.tick();
   }

   public void tick() {
      if (!this.rubberActive) {
         this.rubberActiveTicks = 0;
         if (this.rubberCharge < 666) {
            this.rubberCharge++;
            if (this.rubberCharge == 690) {
            }
         }
      } else {
         this.rubberActiveTicks++;
         if (!this.player.isCreative()) {
            this.rubberCharge -= 8;
         }

         if (this.rubberCharge <= 0
            || this.rubberActiveTicks > 2 && this.player.mialib$startedAttacking()
            || AmariteLongswordItem.getMode(this.player, this.player.getMainHandStack()) != this) {
            this.rubberActive = false;
         }
      }
   }

   @Override
   public void absorbDamage(float base) {
      this.rubberCharge += (int)(base * 20.0F);
      if (this.rubberCharge > 108) {
         this.rubberCharge = 108;
      }
   }

   @Override
   public void useAbility() {
      if (this.rubberCharge >= 54 || this.player.isCreative()) {
         this.rubberTargets.clear();
         this.rubberActive = true;
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
      return (float)this.rubberCharge / 1444.0F;
   }

   @Override
   public String getTranslationKey() {
      return AmariteItems.AMARITE_LONGSWORD.getTranslationKey() + ".rubber";
   }

   public void readFromNbt(@NotNull NbtCompound tag) {
      this.rubberCharge = tag.getInt("rubberCharge");
      this.rubberActive = tag.getBoolean("rubberActive");
   }

   public void writeToNbt(@NotNull NbtCompound tag) {
      if (this.rubberCharge != 0) {
         tag.putInt("rubberCharge", this.rubberCharge);
      }

      if (this.rubberActive) {
         tag.putBoolean("rubberActive", true);
      }
   }
}
