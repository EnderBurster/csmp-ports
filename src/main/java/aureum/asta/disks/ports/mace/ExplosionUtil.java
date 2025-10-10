package aureum.asta.disks.ports.mace;

import aureum.asta.disks.ports.mace.packet.WindChargeExplosionS2CPacket;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Optional;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

public class ExplosionUtil {
   public ExplosionUtil() {
   }

   public static Explosion createExplosion(ServerWorld instance, @Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, World.ExplosionSourceType explosionSourceType, ParticleEffect smallParticle, ParticleEffect largeParticle, RegistryEntry<SoundEvent> soundEvent) {
      Explosion.DestructionType var10000;
      switch (explosionSourceType) {
         case NONE -> var10000 = Explosion.DestructionType.KEEP;
         case BLOCK -> var10000 = instance.getDestructionType(GameRules.BLOCK_EXPLOSION_DROP_DECAY);
         case MOB -> var10000 = instance.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) ? instance.getDestructionType(GameRules.MOB_EXPLOSION_DROP_DECAY) : Explosion.DestructionType.KEEP;
         case TNT -> var10000 = instance.getDestructionType(GameRules.TNT_EXPLOSION_DROP_DECAY);
         default -> throw new IncompatibleClassChangeError();
      }

      Explosion.DestructionType destructionType = var10000;
      Explosion explosion = new Explosion(instance, entity, damageSource, behavior, x, y, z, power, createFire, destructionType);
      explosion.collectBlocksAndDamageEntities();
      affectWorld(explosion, instance, x, y, z, smallParticle, largeParticle, soundEvent);
      return explosion;
   }

   private static void affectWorld(Explosion instance, ServerWorld world, double x, double y, double z, ParticleEffect smallParticle, ParticleEffect largeParticle, RegistryEntry<SoundEvent> soundEvent) {
      if (world.isClient) {
         world.playSound(x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F, false);
      }

      boolean bl = instance.shouldDestroy();
      Vec3d vec3d = new Vec3d(x, y, z);
      ParticleEffect particleEffect = isSmall(instance) ? smallParticle : largeParticle;

      for(ServerPlayerEntity serverPlayerEntity : world.getPlayers()) {
         if (serverPlayerEntity.squaredDistanceTo(vec3d) < (double)4096.0F) {
            Optional<Vec3d> optional = Optional.ofNullable((Vec3d)instance.getAffectedPlayers().get(serverPlayerEntity));
            sendWindChargeS2CExplosionPacket(serverPlayerEntity, vec3d, optional, particleEffect, soundEvent);
         }
      }

      if (bl) {
         ObjectArrayList<Pair<ItemStack, BlockPos>> objectArrayList = new ObjectArrayList();
         boolean bl2 = instance.getCausingEntity() instanceof PlayerEntity;
         Util.shuffle((ObjectArrayList)instance.getAffectedBlocks(), world.random);

         for(BlockPos blockPos : instance.getAffectedBlocks()) {
            BlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (!blockState.isAir()) {
               BlockPos blockPos2 = blockPos.toImmutable();
               world.getProfiler().push("explosion_blocks");
               if (block.shouldDropItemsOnExplosion(instance) && world instanceof ServerWorld) {
                  ServerWorld serverWorld = world;
                  BlockEntity blockEntityx = blockState.hasBlockEntity() ? world.getBlockEntity(blockPos) : null;
                  LootContext.Builder builder = (new LootContext.Builder(serverWorld)).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(blockPos)).parameter(LootContextParameters.TOOL, ItemStack.EMPTY).optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntityx).optionalParameter(LootContextParameters.THIS_ENTITY, instance.getEntity());
                  if (instance.destructionType == Explosion.DestructionType.DESTROY_WITH_DECAY) {
                     builder.parameter(LootContextParameters.EXPLOSION_RADIUS, instance.power);
                  }

                  blockState.onStacksDropped(serverWorld, blockPos, ItemStack.EMPTY, bl2);
                  blockState.getDroppedStacks(builder).forEach((stack) -> Explosion.tryMergeStack(objectArrayList, stack, blockPos2));
               }

               world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
               block.onDestroyedByExplosion(world, blockPos, instance);
               world.getProfiler().pop();
            }
         }

         ObjectListIterator var30 = objectArrayList.iterator();

         while(var30.hasNext()) {
            Pair<ItemStack, BlockPos> pair = (Pair)var30.next();
            Block.dropStack(world, (BlockPos)pair.getSecond(), (ItemStack)pair.getFirst());
         }
      }

      if (instance.createFire) {
         for(BlockPos blockPos3 : instance.getAffectedBlocks()) {
            if (instance.random.nextInt(3) == 0 && world.getBlockState(blockPos3).isAir() && world.getBlockState(blockPos3.down()).isOpaqueFullCube(world, blockPos3.down())) {
               world.setBlockState(blockPos3, AbstractFireBlock.getState(world, blockPos3));
            }
         }
      }

   }

   private static boolean isSmall(Explosion instance) {
      return instance.power < 2.0F || !shouldDestroyBlocks(instance);
   }

   private static boolean shouldDestroyBlocks(Explosion instance) {
      return instance.destructionType != Explosion.DestructionType.KEEP;
   }

   private static void sendWindChargeS2CExplosionPacket(ServerPlayerEntity serverPlayerEntity, Vec3d vec3d, Optional<Vec3d> optional, ParticleEffect particleEffect, RegistryEntry<SoundEvent> registryEntry) {
      try {
         WindChargeExplosionS2CPacket packet = new WindChargeExplosionS2CPacket(vec3d, optional, particleEffect, registryEntry);
         PacketByteBuf buf = PacketByteBufs.create();
         packet.write(buf);
         ServerPlayNetworking.send(serverPlayerEntity, FaithfulMace.EXPLOSION_WIND_CHARGE_S2C_PACKET_ID, buf);
      } catch (Exception e) {
         FaithfulMace.MOGGER.error("Exception sending WindChargeExplosionS2CPacket with parameters {} {} {} {}", new Object[]{vec3d, optional, particleEffect, registryEntry, e});
      }

   }

   public static void writeVec3d(ByteBuf buf, Vec3d vec) {
      buf.writeDouble(vec.getX());
      buf.writeDouble(vec.getY());
      buf.writeDouble(vec.getZ());
   }

   public static Vec3d readVec3d(ByteBuf buf) {
      return new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
   }

   public static <T extends ParticleEffect> T readParticleParameters(PacketByteBuf buf, ParticleType<T> type) {
      return (T)type.getParametersFactory().read(type, buf);
   }
}
