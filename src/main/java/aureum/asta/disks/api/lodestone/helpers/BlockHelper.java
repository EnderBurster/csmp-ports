package aureum.asta.disks.api.lodestone.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.entity.ai.pathing.PathNode;

public class BlockHelper {
   private BlockHelper() {
   }

   public static BlockState getBlockStateWithExistingProperties(BlockState oldState, BlockState newState) {
      BlockState finalState = newState;

      for (Property<?> property : oldState.getProperties()) {
         if (newState.contains(property)) {
            finalState = newStateWithOldProperty(oldState, finalState, property);
         }
      }

      return finalState;
   }

   public static BlockState setBlockStateWithExistingProperties(World level, BlockPos pos, BlockState newState, int flags) {
      BlockState oldState = level.getBlockState(pos);
      BlockState finalState = getBlockStateWithExistingProperties(oldState, newState);
      level.updateListeners(pos, oldState, finalState, flags);
      level.setBlockState(pos, finalState, flags);
      return finalState;
   }

   public static <T extends Comparable<T>> BlockState newStateWithOldProperty(BlockState oldState, BlockState newState, Property<T> property) {
      return (BlockState)newState.with(property, oldState.get(property));
   }

   public static void saveBlockPos(NbtCompound compoundNBT, BlockPos pos) {
      compoundNBT.putInt("X", pos.getX());
      compoundNBT.putInt("Y", pos.getY());
      compoundNBT.putInt("Z", pos.getZ());
   }

   public static void saveBlockPos(NbtCompound compoundNBT, BlockPos pos, String extra) {
      compoundNBT.putInt(extra + "_X", pos.getX());
      compoundNBT.putInt(extra + "_Y", pos.getY());
      compoundNBT.putInt(extra + "_Z", pos.getZ());
   }

   public static BlockPos loadBlockPos(NbtCompound tag) {
      return tag.contains("X") ? new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z")) : null;
   }

   public static BlockPos loadBlockPos(NbtCompound tag, String extra) {
      return tag.contains(extra + "_X")
         ? new BlockPos(tag.getInt(extra + "_X"), tag.getInt(extra + "_Y"), tag.getInt(extra + "_Z"))
         : null;
   }

   public static <T> Collection<T> getBlockEntities(Class<T> type, World level, BlockPos pos, int range, Predicate<T> predicate) {
      return getBlockEntitiesStream(type, level, pos, range, predicate).collect(Collectors.toSet());
   }

   public static <T> Stream<T> getBlockEntitiesStream(Class<T> type, World level, BlockPos pos, int range, Predicate<T> predicate) {
      return getBlockEntitiesStream(type, level, pos, range, range, range, predicate);
   }

   public static <T> Collection<T> getBlockEntities(Class<T> type, World level, BlockPos pos, int x, int z, Predicate<T> predicate) {
      return getBlockEntitiesStream(type, level, pos, x, z, predicate).collect(Collectors.toSet());
   }

   public static <T> Stream<T> getBlockEntitiesStream(Class<T> type, World level, BlockPos pos, int x, int z, Predicate<T> predicate) {
      return getBlockEntitiesStream(type, level, pos, x, z).filter(predicate);
   }

   public static <T> Collection<T> getBlockEntities(Class<T> type, World level, BlockPos pos, int x, int y, int z, Predicate<T> predicate) {
      return getBlockEntitiesStream(type, level, pos, x, y, z, predicate).collect(Collectors.toSet());
   }

   public static <T> Stream<T> getBlockEntitiesStream(Class<T> type, World level, BlockPos pos, int x, int y, int z, Predicate<T> predicate) {
      return getBlockEntitiesStream(type, level, pos, x, y, z).filter(predicate);
   }

   public static <T> Collection<T> getBlockEntities(Class<T> type, World level, BlockPos pos, int range) {
      return getBlockEntities(type, level, pos, range, range, range);
   }

   public static <T> Stream<T> getBlockEntitiesStream(Class<T> type, World level, BlockPos pos, int range) {
      return getBlockEntitiesStream(type, level, pos, range, range, range);
   }

   public static <T> Collection<T> getBlockEntities(Class<T> type, World level, BlockPos pos, int x, int z) {
      return getBlockEntitiesStream(type, level, pos, x, z).collect(Collectors.toSet());
   }

   public static <T> Stream<T> getBlockEntitiesStream(Class<T> type, World level, BlockPos pos, int x, int z) {
      return getBlockEntitiesStream(
         type,
         level,
         new Box(
            (double)pos.getX() - (double)x,
            (double)pos.getY(),
            (double)pos.getZ() - (double)z,
            (double)pos.getX() + (double)x,
            (double)pos.getY() + 1.0,
            (double)pos.getZ() + (double)z
         )
      );
   }

   public static <T> Collection<T> getBlockEntities(Class<T> type, World level, BlockPos pos, int x, int y, int z) {
      return getBlockEntitiesStream(type, level, pos, x, y, z).collect(Collectors.toSet());
   }

   public static <T> Stream<T> getBlockEntitiesStream(Class<T> type, World level, BlockPos pos, int x, int y, int z) {
      return getBlockEntitiesStream(type, level, pos, -x, -y, -z, x, y, z);
   }

   public static <T> Collection<T> getBlockEntities(Class<T> type, World level, BlockPos pos, int x1, int y1, int z1, int x2, int y2, int z2) {
      return getBlockEntitiesStream(type, level, pos, x1, y1, z1, x2, y2, z2).collect(Collectors.toSet());
   }

   public static <T> Stream<T> getBlockEntitiesStream(Class<T> type, World level, BlockPos pos, int x1, int y1, int z1, int x2, int y2, int z2) {
      return getBlockEntitiesStream(
         type,
         level,
         new Box(
            (double)pos.getX() + 1.5 + (double)x1,
            (double)pos.getY() + 1.5 + (double)y1,
            (double)pos.getZ() + 1.5 + (double)z1,
            (double)pos.getX() + 0.5 + (double)x2,
            (double)pos.getY() + 0.5 + (double)y2,
            (double)pos.getZ() + 0.5 + (double)z2
         )
      );
   }

   public static <T> Collection<T> getBlockEntities(Class<T> type, World world, Box bb) {
      return getBlockEntitiesStream(type, world, bb).collect(Collectors.toSet());
   }

   public static <T> Stream<T> getBlockEntitiesStream(Class<T> type, World world, Box bb) {
      return IntStream.iterate((int)Math.floor(bb.minX), i -> (double)i < Math.ceil(bb.maxX) + 16.0, i -> i + 16)
         .boxed()
         .flatMap(i -> IntStream.iterate((int)Math.floor(bb.minZ), j -> (double)j < Math.ceil(bb.maxZ) + 16.0, j -> j + 16).boxed().flatMap(j -> {
               Chunk c = world.getChunk(new BlockPos(i, 0, j));
               return c.getBlockEntityPositions().stream();
            }))
         .filter(p -> bb.contains((double)p.getX() + 0.5, (double)p.getY() + 0.5, (double)p.getZ() + 0.5))
         .<BlockEntity>map(world::getBlockEntity)
         .filter(type::isInstance)
         .map(type::cast);
   }

   public static Collection<BlockPos> getBlocks(BlockPos pos, int range, Predicate<BlockPos> predicate) {
      return getBlocksStream(pos, range, predicate).collect(Collectors.toSet());
   }

   public static Stream<BlockPos> getBlocksStream(BlockPos pos, int range, Predicate<BlockPos> predicate) {
      return getBlocksStream(pos, range, range, range, predicate);
   }

   public static Collection<BlockPos> getBlocks(BlockPos pos, int x, int y, int z, Predicate<BlockPos> predicate) {
      return getBlocksStream(pos, x, y, z, predicate).collect(Collectors.toSet());
   }

   public static Stream<BlockPos> getBlocksStream(BlockPos pos, int x, int y, int z, Predicate<BlockPos> predicate) {
      return getBlocksStream(pos, x, y, z).filter(predicate);
   }

   public static Collection<BlockPos> getBlocks(BlockPos pos, int x, int y, int z) {
      return getBlocksStream(pos, x, y, z).collect(Collectors.toSet());
   }

   public static Stream<BlockPos> getBlocksStream(BlockPos pos, int x, int y, int z) {
      return getBlocksStream(pos, -x, -y, -z, x, y, z);
   }

   public static Collection<BlockPos> getBlocks(BlockPos pos, int x1, int y1, int z1, int x2, int y2, int z2) {
      return getBlocksStream(pos, x1, y1, z1, x2, y2, z2).collect(Collectors.toSet());
   }

   public static Stream<BlockPos> getBlocksStream(BlockPos pos, int x1, int y1, int z1, int x2, int y2, int z2) {
      return IntStream.rangeClosed(x1, x2)
         .boxed()
         .flatMap(i -> IntStream.rangeClosed(y1, y2).boxed().flatMap(j -> IntStream.rangeClosed(z1, z2).boxed().map(k -> pos.add(i, j, k))));
   }

   public static Collection<BlockPos> getPlaneOfBlocks(BlockPos pos, int range, Predicate<BlockPos> predicate) {
      return getPlaneOfBlocksStream(pos, range, predicate).collect(Collectors.toSet());
   }

   public static Stream<BlockPos> getPlaneOfBlocksStream(BlockPos pos, int range, Predicate<BlockPos> predicate) {
      return getPlaneOfBlocksStream(pos, range, range, predicate);
   }

   public static Collection<BlockPos> getPlaneOfBlocks(BlockPos pos, int x, int z, Predicate<BlockPos> predicate) {
      return getPlaneOfBlocksStream(pos, x, z, predicate).collect(Collectors.toSet());
   }

   public static Stream<BlockPos> getPlaneOfBlocksStream(BlockPos pos, int x, int z, Predicate<BlockPos> predicate) {
      return getPlaneOfBlocksStream(pos, x, z).filter(predicate);
   }

   public static Collection<BlockPos> getPlaneOfBlocks(BlockPos pos, int x, int z) {
      return getPlaneOfBlocksStream(pos, x, z).collect(Collectors.toSet());
   }

   public static Stream<BlockPos> getPlaneOfBlocksStream(BlockPos pos, int x, int z) {
      return getPlaneOfBlocksStream(pos, -x, -z, x, z);
   }

   public static Collection<BlockPos> getPlaneOfBlocks(BlockPos pos, int x1, int z1, int x2, int z2) {
      return getPlaneOfBlocksStream(pos, x1, z1, x2, z2).collect(Collectors.toSet());
   }

   public static Stream<BlockPos> getPlaneOfBlocksStream(BlockPos pos, int x1, int z1, int x2, int z2) {
      return IntStream.rangeClosed(x1, x2).boxed().flatMap(x -> IntStream.rangeClosed(z1, z2).boxed().map(z -> pos.add(x, 0, z)));
   }

   public static Collection<BlockPos> getSphereOfBlocks(BlockPos pos, float range, Predicate<BlockPos> predicate) {
      return getSphereOfBlocksStream(pos, range, predicate).collect(Collectors.toSet());
   }

   public static Stream<BlockPos> getSphereOfBlocksStream(BlockPos pos, float range, Predicate<BlockPos> predicate) {
      return getSphereOfBlocksStream(pos, range, range).filter(predicate);
   }

   public static Collection<BlockPos> getSphereOfBlocks(BlockPos pos, float width, float height, Predicate<BlockPos> predicate) {
      return getSphereOfBlocksStream(pos, width, height, predicate).collect(Collectors.toSet());
   }

   public static Stream<BlockPos> getSphereOfBlocksStream(BlockPos pos, float width, float height, Predicate<BlockPos> predicate) {
      return getSphereOfBlocksStream(pos, width, height).filter(predicate);
   }

   public static Collection<BlockPos> getSphereOfBlocks(BlockPos pos, float range) {
      return getSphereOfBlocksStream(pos, range).collect(Collectors.toSet());
   }

   public static Stream<BlockPos> getSphereOfBlocksStream(BlockPos pos, float range) {
      return getSphereOfBlocksStream(pos, range, range);
   }

   public static Collection<BlockPos> getSphereOfBlocks(BlockPos pos, float width, float height) {
      return getSphereOfBlocksStream(pos, width, height).collect(Collectors.toSet());
   }

   public static Stream<BlockPos> getSphereOfBlocksStream(BlockPos pos, float width, float height) {
      return IntStream.rangeClosed((int)(-width), (int)width)
         .boxed()
         .flatMap(
            x -> IntStream.rangeClosed((int)(-height), (int)height).boxed().flatMap(y -> IntStream.rangeClosed((int)(-width), (int)width).boxed().filter(z -> {
                     double d = Math.sqrt((double)(x * x + y * y + z * z));
                     return d <= (double)width;
                  }).map(z -> pos.add(x, y, z)))
         );
   }

   public static Collection<BlockPos> getNeighboringBlocks(BlockPos current) {
      return getBlocks(current, -1, -1, -1, 1, 1, 1);
   }

   public static Stream<BlockPos> getNeighboringBlocksStream(BlockPos current) {
      return getBlocksStream(current, -1, -1, -1, 1, 1, 1);
   }

   public static Collection<BlockPos> getPath(BlockPos start, BlockPos end, int speed, boolean inclusive, World level) {
      ParrotEntity parrot = new ParrotEntity(EntityType.PARROT, level);
      parrot.setPos((double)start.getX() + 0.5, (double)start.getY() - 0.5, (double)start.getZ() + 0.5);
      parrot.getNavigation().startMovingTo((double)end.getX() + 0.5, (double)end.getY() - 0.5, (double)end.getZ() + 0.5, (double)speed);
      Path path = parrot.getNavigation().getCurrentPath();
      parrot.discard();
      int nodes = path != null ? path.getLength() : 0;
      ArrayList<BlockPos> positions = new ArrayList<>();

      for (int i = 0; i < nodes; i++) {
         PathNode node = path.getNode(i);
         positions.add(new BlockPos(node.x, (int)(node.y - 0.5), node.z));
      }

      if (!inclusive) {
         positions.remove(0);
         positions.remove(positions.size() - 1);
      }

      return positions;
   }

   public static void updateState(World level, BlockPos pos) {
      updateState(level.getBlockState(pos), level, pos);
   }

   public static void updateState(BlockState state, World level, BlockPos pos) {
      level.updateListeners(pos, state, state, 2);
      level.markDirty(pos);
   }

   public static void updateAndNotifyState(World level, BlockPos pos) {
      updateAndNotifyState(level.getBlockState(pos), level, pos);
   }

   public static void updateAndNotifyState(BlockState state, World level, BlockPos pos) {
      updateState(state, level, pos);
      state.updateNeighbors(level, pos, 2);
      level.updateComparators(pos, state.getBlock());
   }

   public static Vec3d fromBlockPos(BlockPos pos) {
      return new Vec3d((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
   }

   public static Vec3d withinBlock(Random rand, BlockPos pos) {
      double x = (double)pos.getX() + rand.nextDouble();
      double y = (double)pos.getY() + rand.nextDouble();
      double z = (double)pos.getZ() + rand.nextDouble();
      return new Vec3d(x, y, z);
   }
}
