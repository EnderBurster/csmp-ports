package aureum.asta.disks.ports.charter.common.component;

import com.mojang.authlib.GameProfile;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;

public class CharterArmComponent implements AutoSyncedComponent {
   private final PlayerEntity obj;
   public boolean handicap = false;
   public GameProfile armOwner;
   public UUID yeah = null;
   public ItemStack arm = ItemStack.EMPTY;

   public CharterArmComponent(PlayerEntity player) {
      this.obj = player;
   }

   public void readFromNbt(NbtCompound tag) {
      this.handicap = tag.getBoolean("handicap");
      if (tag.contains("yeah")) {
         this.yeah = tag.getUuid("yeah");
      }

      if (tag.contains("synth")) {
         this.arm = ItemStack.fromNbt(tag.getCompound("synth"));
      }

      if (tag.contains("owner") && NbtHelper.toGameProfile(tag.getCompound("owner")) != null) {
         this.armOwner = NbtHelper.toGameProfile(tag.getCompound("owner"));
      }
   }

   public boolean hasArm() {
      return !this.handicap || this.armOwner != null || this.arm != null && !this.arm.isEmpty();
   }

   public void sync() {
      CharterComponents.ARM_COMPONENT.sync(this.obj);
   }

   public void writeToNbt(NbtCompound tag) {
      tag.putBoolean("handicap", this.handicap);
      if (this.arm != null) {
         NbtCompound comp = new NbtCompound();
         this.arm.writeNbt(comp);
         tag.put("synth", comp);
      }

      if (this.yeah != null) {
         tag.putUuid("yeah", this.yeah);
      }

      if (this.armOwner != null) {
         NbtCompound compound = new NbtCompound();
         NbtHelper.writeGameProfile(compound, this.armOwner);
         tag.put("owner", compound);
      }
   }
}
