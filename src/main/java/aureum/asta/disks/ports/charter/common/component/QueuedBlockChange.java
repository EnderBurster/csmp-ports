package aureum.asta.disks.ports.charter.common.component;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class QueuedBlockChange {
   private ParrotEntity god;
   public BlockPos pos;
   public BlockState queuedState;
   public int age = 0;
   public int maxAge;

   public QueuedBlockChange(int maxAge, BlockPos pos, BlockState state) {
      this.maxAge = maxAge;
      this.pos = pos;
      this.queuedState = state;
   }

   public QueuedBlockChange() {
   }

   public void writeToNbt(NbtCompound tag) {
      tag.putInt("x", this.pos.getX());
      tag.putInt("y", this.pos.getY());
      tag.putInt("z", this.pos.getZ());
      tag.putInt("duration", this.maxAge);
      tag.putInt("age", this.age);
      tag.put("blockState", NbtHelper.fromBlockState(this.queuedState));
   }

   public void readFromNbt(NbtCompound tag) {
      this.pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
      this.maxAge = tag.getInt("duration");
      this.age = tag.getInt("age");
      RegistryEntryLookup<Block> registryEntryLookup = (RegistryEntryLookup<Block>)(MinecraftClient.getInstance().world != null ? MinecraftClient.getInstance().world.createCommandRegistryWrapper(RegistryKeys.BLOCK) : Registries.BLOCK.getReadOnlyWrapper());
      this.queuedState = NbtHelper.toBlockState(registryEntryLookup, tag.getCompound("blockState"));

   }

   public void tick(World wrld) {
      this.age++;
      if (this.god == null) {
         this.god = new ParrotEntity(EntityType.PARROT, wrld);
      }

      if (!wrld.getBlockState(this.pos).isAir()) {
         wrld.setBlockBreakingInfo(this.god.getId(), this.pos, (int)((float)this.age / (float)this.maxAge * 10.0F));
      }

      if (this.age >= this.maxAge - 1) {
         wrld.setBlockBreakingInfo(this.god.getId(), this.pos, -1);
      }
   }
}
