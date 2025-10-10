package aureum.asta.disks.ports.charter.common.component;

import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.joml.Vector3i;

public class DiamondOfProtection {
   private Box area;
   private UUID owner = null;

   public DiamondOfProtection(double size, double x, double y, double z) {
      this.area = Box.of(new Vec3d(x, y, z), size, size, size);
   }

   public DiamondOfProtection() {
   }

   public void setOwner(UUID uuid) {
      this.owner = uuid;
   }

   public boolean isValidForRemoval(BlockPos pos) {
      return this.area.getCenter().x == (double)pos.getX() + 0.5
         && this.area.getCenter().z == (double)pos.getZ() + 0.5
         && this.area.getCenter().z == (double)pos.getZ() + 0.5;
   }

   public BlockPos getQuery() {
      Vec3i vector = new Vec3i((int)this.area.getCenter().x, (int)this.area.getCenter().y, (int)this.area.getCenter().z);
      return new BlockPos(vector);
   }

   public boolean shouldQuery() {
      return true;
   }

   public void writeToNbt(NbtCompound nbt) {
      NbtCompound boxCompound = new NbtCompound();
      boxCompound.put("Center", NbtHelper.fromBlockPos(new BlockPos((int) this.area.getCenter().x, (int) this.area.getCenter().y, (int) this.area.getCenter().z)));
      boxCompound.putDouble("LengthX", this.area.getXLength());
      boxCompound.putDouble("LengthY", this.area.getYLength());
      boxCompound.putDouble("LengthZ", this.area.getZLength());
      nbt.put("box", boxCompound);
      if (this.owner != null) {
         nbt.putUuid("OwnerUuid", this.owner);
      }
   }

   public boolean isOwner(PlayerEntity plr) {
      return plr.getUuid().equals(this.owner) || ((CharterWorldComponent)plr.world.getComponent(CharterComponents.CHARTER)).isInCharter(plr);
   }

   public boolean isPosInside(BlockPos pos) {
      return this.area.contains((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
   }

   public boolean isPosInside(Vec3d pos) {
      return this.area.contains(pos.getX(), pos.getY(), pos.getZ());
   }

   public void readFromNbt(NbtCompound tag) {
      NbtCompound boxCompound = (NbtCompound)tag.get("box");
      this.area = Box.of(
         Vec3d.of(NbtHelper.toBlockPos(boxCompound.getCompound("Center"))),
         boxCompound.getDouble("LengthX"),
         boxCompound.getDouble("LengthY"),
         boxCompound.getDouble("LengthZ")
      );
      if (tag.contains("OwnerUuid")) {
         this.owner = tag.getUuid("OwnerUuid");
      }
   }

   public Box getArea() {
      return this.area.stretch(1.0, 100.0, 1.0);
   }

   public VoxelShape getVoxelShape() {
      return VoxelShapes.cuboid(
         Math.floor(this.area.minX),
         Double.NEGATIVE_INFINITY,
         Math.floor(this.area.minZ),
         Math.ceil(this.area.maxX),
         Double.POSITIVE_INFINITY,
         Math.ceil(this.area.maxZ)
      );
   }

   public void tick() {
   }
}
