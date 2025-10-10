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

public class LongswordCrystalRushComponent implements AutoSyncedComponent, CommonTickingComponent, AmariteLongswordItem.LongswordMode {
   private final PlayerEntity player;
   private final List<Entity> crtTargets = new ArrayList<>();
   private int crtCharge = 8000;
   public boolean crtActive = false;
   public int crtActiveTicks = 0;

   public LongswordCrystalRushComponent(PlayerEntity player) {
      this.player = player;
   }

   public void serverTick() {
      if (this.crtActive && !this.crtTargets.isEmpty()) {
         Vec3d userAnchor = this.player.getEyePos().add(this.player.getRotationVec(1.0F).multiply(1.32));

         for (int i = 0; i < this.crtTargets.size(); i++) {
            Entity target = this.crtTargets.get(i);
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
      if (this.crtActive) {
      }

      this.tick();
   }

   public void tick() {
      if (!this.crtActive) {
         this.crtActiveTicks = 0;
         if (this.crtCharge < 666) {
            this.crtCharge++;
            if (this.crtCharge == 690) {
            }
         }
      } else {
         this.crtActiveTicks++;
         if (!this.player.isCreative()) {
            this.crtCharge -= 8;
         }

         if (this.crtCharge <= 0
            || this.crtActiveTicks > 2 && this.player.mialib$startedAttacking()
            || AmariteLongswordItem.getMode(this.player, this.player.getMainHandStack()) != this) {
            this.crtActive = false;
         }
      }
   }

   @Override
   public void absorbDamage(float base) {
      this.crtCharge += (int)(base * 20.0F);
      if (this.crtCharge > 108) {
         this.crtCharge = 108;
      }
   }

   @Override
   public void useAbility() {
      if (this.crtCharge >= 54 || this.player.isCreative()) {
         this.crtTargets.clear();
         this.crtActive = true;
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
      return (float)this.crtCharge / 1444.0F;
   }

   @Override
   public String getTranslationKey() {
      return AmariteItems.AMARITE_LONGSWORD.getTranslationKey() + ".crt";
   }

   public void readFromNbt(@NotNull NbtCompound tag) {
      this.crtCharge = tag.getInt("crtCharge");
      this.crtActive = tag.getBoolean("crtActive");
   }

   public void writeToNbt(@NotNull NbtCompound tag) {
      if (this.crtCharge != 0) {
         tag.putInt("crtCharge", this.crtCharge);
      }

      if (this.crtActive) {
         tag.putBoolean("crtActive", true);
      }
   }
}
