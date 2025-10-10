package aureum.asta.disks.ports.amarite.amarite.cca;

import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.entities.MalignancyEntity;
import aureum.asta.disks.ports.amarite.amarite.items.AmariteLongswordItem;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteEntities;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteSoundEvents;
import aureum.asta.disks.ports.amarite.mialib.util.MMath;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

public class LongswordMalignancyComponent implements AutoSyncedComponent, CommonTickingComponent, AmariteLongswordItem.LongswordMode {
   public static final int MALIGNANCY_MAX_CHARGE = 800;
   public static final int MALIGNANCY_COLOR = -10639338;
   private final PlayerEntity player;
   private int malignancyCharge = 800;

   public LongswordMalignancyComponent(PlayerEntity player) {
      this.player = player;
   }

   private void sync() {
      Amarite.MALIGNANCY.sync(this.player);
   }

   public void tick() {
   }

   @Override
   public void absorbDamage(float base) {
   }

   @Override
   public void useAbility() {
   }

   @Override
   public int getModeColor() {
      return -10639338;
   }

   @Override
   public int getSwordTint() {
      float percent = MathHelper.clamp(this.getChargeProgress(), 0.0F, 1.0F);
      int r = (int)(255.0F - percent * 162.0F);
      int g = (int)(255.0F - percent * 87.0F);
      int b = (int)(255.0F - percent * 233.0F);
      return MMath.packRgb(r, g, b);
   }

   @Override
   public float getChargeProgress() {
      return this.malignancyCharge / 800.0F;
   }

   @Override
   public String getTranslationKey() {
      return AmariteItems.AMARITE_LONGSWORD.getTranslationKey() + ".malignancy";
   }

   public void readFromNbt(@NotNull NbtCompound tag) {
      this.malignancyCharge = tag.getInt("malignancyCharge");
   }

   public void writeToNbt(@NotNull NbtCompound tag) {
      if (this.malignancyCharge != 0) {
         tag.putInt("malignancyCharge", this.malignancyCharge);
      }
   }
}
