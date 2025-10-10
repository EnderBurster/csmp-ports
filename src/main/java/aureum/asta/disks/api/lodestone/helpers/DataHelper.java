package aureum.asta.disks.api.lodestone.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.joml.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Direction.Axis;

public final class DataHelper {
   public static Vec3d fromBlockPos(BlockPos pos) {
      return new Vec3d((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
   }

   public static Vector3f fromBlockPosVec3f(BlockPos pos) {
      return new Vector3f((float)pos.getX(), (float)pos.getY(), (float)pos.getZ());
   }

   public static Vec3d randPos(BlockPos pos, net.minecraft.util.math.random.Random rand, double min, double max) {
      double x = MathHelper.nextDouble(rand, min, max) + (double)pos.getX();
      double y = MathHelper.nextDouble(rand, min, max) + (double)pos.getY();
      double z = MathHelper.nextDouble(rand, min, max) + (double)pos.getZ();
      return new Vec3d(x, y, z);
   }

   public static Identifier prefix(String path) {
      return new Identifier("aureum-asta-disks", path);
   }

   public static <T, K extends Collection<T>> K reverseOrder(Supplier<K> reversed, Collection<T> items) {
      ArrayList<T> original = new ArrayList<>(items);
      K newCollection = (K)reversed.get();

      for (int i = items.size() - 1; i >= 0; i--) {
         newCollection.add(original.get(i));
      }

      return newCollection;
   }

   public static String toTitleCase(String givenString, String regex) {
      String[] stringArray = givenString.split(regex);
      StringBuilder stringBuilder = new StringBuilder();

      for (String string : stringArray) {
         stringBuilder.append(Character.toUpperCase(string.charAt(0))).append(string.substring(1)).append(regex);
      }

      return stringBuilder.toString().trim().replaceAll(regex, " ").substring(0, stringBuilder.length() - 1);
   }

   public static void writeNbt(NbtCompound nbt, DefaultedList<ItemStack> stacks, String key) {
      NbtList nbtList = new NbtList();

      for (int i = 0; i < stacks.size(); i++) {
         ItemStack itemStack = (ItemStack)stacks.get(i);
         if (!itemStack.isEmpty()) {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putByte("Slot", (byte)i);
            itemStack.writeNbt(nbtCompound);
            nbtList.add(nbtCompound);
         }
      }

      nbt.put(key, nbtList);
   }

   public static void readNbt(NbtCompound nbt, DefaultedList<ItemStack> stacks, String key) {
      NbtList nbtList = nbt.getList(key, 10);

      for (int i = 0; i < nbtList.size(); i++) {
         NbtCompound nbtCompound = nbtList.getCompound(i);
         int j = nbtCompound.getByte("Slot") & 255;
         if (j < stacks.size()) {
            stacks.set(j, ItemStack.fromNbt(nbtCompound));
         }
      }
   }

   public static int[] nextInts(Random rand, int count, int range) {
      int[] ints = new int[count];

      for (int i = 0; i < count; i++) {
          var ref = new Object() {
              int nextInt = 0;
          };
         do {
            ref.nextInt = rand.nextInt(range);
         } while (!Arrays.stream(ints).noneMatch(j -> j == ref.nextInt));

         ints[i] = ref.nextInt;
      }

      return ints;
   }

   public static <T> boolean hasDuplicate(T[] things) {
      Set<T> thingSet = new HashSet<>();
      return !Arrays.stream(things).allMatch(thingSet::add);
   }

   @SafeVarargs
   public static <T> Collection<T> takeAll(Collection<? extends T> src, T... items) {
      List<T> ret = Arrays.asList(items);

      for (T item : items) {
         if (!src.contains(item)) {
            return Collections.emptyList();
         }
      }

      return !src.removeAll(ret) ? Collections.emptyList() : ret;
   }

   public static <T> Collection<T> takeAll(Collection<T> src, Predicate<T> pred) {
      List<T> ret = new ArrayList<>();
      Iterator<T> iterable = src.iterator();

      while (iterable.hasNext()) {
         T item = iterable.next();
         if (pred.test(item)) {
            iterable.remove();
            ret.add(item);
         }
      }

      return ret.isEmpty() ? Collections.emptyList() : ret;
   }

   @SafeVarargs
   public static <T> Collection<T> getAll(Collection<? extends T> src, T... items) {
      return List.copyOf(getAll(src, t -> Arrays.<Object>stream(items).anyMatch(tAgain -> tAgain.getClass().isInstance(t))));
   }

   public static <T> Collection<T> getAll(Collection<T> src, Predicate<T> pred) {
      return src.stream().filter(pred).collect(Collectors.toList());
   }

   public static Vec3d circlePosition(Vec3d pos, float distance, float current, float total) {
      double angle = (double)(current / total) * (Math.PI * 2);
      double dx2 = (double)distance * Math.cos(angle);
      double dz2 = (double)distance * Math.sin(angle);
      Vec3d vector = new Vec3d(dx2, 0.0, dz2);
      double x = vector.x * (double)distance;
      double z = vector.z * (double)distance;
      return pos.add(new Vec3d(x, 0.0, z));
   }

   public static Vec3d rotatedCirclePosition(Vec3d pos, float distance, float current, float total, long gameTime, float time, float tickDelta) {
      return rotatedCirclePosition(pos, distance, distance, current, total, gameTime, time, tickDelta);
   }

   public static Vec3d rotatedCirclePosition(
      Vec3d pos, float distanceX, float distanceZ, float current, float total, long gameTime, float time, float tickDelta
   ) {
      double angle = (double)(current / total) * (Math.PI * 2);
      angle += (double)(((float)gameTime % time + tickDelta) / time) * (Math.PI * 2);
      double dx2 = (double)distanceX * Math.cos(angle);
      double dz2 = (double)distanceZ * Math.sin(angle);
      Vec3d vector2f = new Vec3d(dx2, 0.0, dz2);
      double x = vector2f.x * (double)distanceX;
      double z = vector2f.z * (double)distanceZ;
      return pos.add(x, 0.0, z);
   }

   public static ArrayList<Vec3d> blockOutlinePositions(World world, BlockPos pos) {
      ArrayList<Vec3d> arrayList = new ArrayList<>();
      double d0 = 0.5625;
      net.minecraft.util.math.random.Random random = world.random;

      for (Direction direction : Direction.values()) {
         BlockPos blockpos = pos.offset(direction);
         if (!world.getBlockState(blockpos).isOpaqueFullCube(world, blockpos)) {
            Axis direction$axis = direction.getAxis();
            double d1 = direction$axis == Axis.X ? 0.5 + d0 * (double)direction.getOffsetX() : (double)random.nextFloat();
            double d2 = direction$axis == Axis.Y ? 0.5 + d0 * (double)direction.getOffsetY() : (double)random.nextFloat();
            double d3 = direction$axis == Axis.Z ? 0.5 + d0 * (double)direction.getOffsetZ() : (double)random.nextFloat();
            arrayList.add(new Vec3d((double)pos.getX() + d1, (double)pos.getY() + d2, (double)pos.getZ() + d3));
         }
      }

      return arrayList;
   }

   public static float distSqr(float... a) {
      float d = 0.0F;

      for (float f : a) {
         d += f * f;
      }

      return d;
   }

   public static float distance(float... a) {
      return MathHelper.sqrt(distSqr(a));
   }
}
