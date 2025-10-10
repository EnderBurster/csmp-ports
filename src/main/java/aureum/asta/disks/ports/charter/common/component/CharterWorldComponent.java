package aureum.asta.disks.ports.charter.common.component;

import aureum.asta.disks.ports.charter.common.block.CharterStoneBlock;
import aureum.asta.disks.ports.charter.common.init.CharterParticles;
import aureum.asta.disks.api.lodestone.setup.LodestoneParticles;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders.WorldParticleBuilder;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect.SpecialRemovalProtocol;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class CharterWorldComponent implements AutoSyncedComponent, ClientTickingComponent {
   public final ArrayList<DiamondOfProtection> diamonds = new ArrayList<>();
   public final World zaWarudo;
   private final List<UUID> charterOwners = new ArrayList<>();
   private final List<QueuedBlockChange> queuedBlockChanges = new ArrayList<>();

   public CharterWorldComponent(World world) {
      this.zaWarudo = world;
      this.charterOwners.add(UUID.fromString("1ece513b-8d36-4f04-9be2-f341aa8c9ee2"));
   }

   public void readFromNbt(NbtCompound tag) {
      NbtList memberListTag = tag.getList("ownerList", 11);
      this.charterOwners.clear();

      for (NbtElement member : memberListTag) {
         this.charterOwners.add(NbtHelper.toUuid(member));
      }

      NbtList list = tag.getList("diamonds", 10);
      this.diamonds.clear();
      list.forEach(dNbt -> {
         DiamondOfProtection yea = new DiamondOfProtection();
         yea.readFromNbt((NbtCompound)dNbt);
         this.diamonds.add(yea);
      });
      NbtList stateList = tag.getList("stateList", 10);
      this.queuedBlockChanges.clear();
      stateList.forEach(dfNbt -> {
         QueuedBlockChange q = new QueuedBlockChange();
         q.readFromNbt((NbtCompound)dfNbt);
         this.queuedBlockChanges.add(q);
      });
   }

   public boolean isInCharteredGrounds(Vec3d vec) {
      for (DiamondOfProtection dia : ((CharterWorldComponent)this.zaWarudo.getComponent(CharterComponents.CHARTER)).diamonds) {
         if (dia.isPosInside(vec)) {
            return true;
         }
      }

      return false;
   }

   public void writeToNbt(NbtCompound tag) {
      NbtList list = new NbtList();
      this.diamonds.forEach(dia -> {
         NbtCompound dNbt = new NbtCompound();
         dia.writeToNbt(dNbt);
         list.add(dNbt);
      });
      tag.put("diamonds", list);
      NbtList l = new NbtList();

      for (UUID member : new ArrayList<>(this.charterOwners)) {
         l.add(NbtHelper.fromUuid(member));
      }

      tag.put("ownerList", l);
      NbtList blockList = new NbtList();
      this.queuedBlockChanges.forEach(deathfount -> {
         NbtCompound deathfountNbt = new NbtCompound();
         deathfount.writeToNbt(deathfountNbt);
         blockList.add(deathfountNbt);
      });
      tag.put("stateList", blockList);
   }

   public boolean isInCharter(PlayerEntity player) {
      return this.charterOwners.contains(player.getUuid());
   }

   public void serverTick() {
      int i = this.diamonds.size() + this.queuedBlockChanges.size();
      this.diamonds.removeIf(dia -> !(this.zaWarudo.getBlockState(dia.getQuery()).getBlock() instanceof CharterStoneBlock) && dia.shouldQuery());
      this.queuedBlockChanges.forEach(s -> s.tick(this.zaWarudo));
      this.queuedBlockChanges.removeIf(h -> shouldProcQueuedBlock(h, this.zaWarudo));
      if (i != this.diamonds.size() + this.queuedBlockChanges.size()) {
         this.zaWarudo.syncComponent(CharterComponents.CHARTER);
      }
   }

   public static boolean shouldProcQueuedBlock(QueuedBlockChange change, World world) {
      boolean bl = change.age >= change.maxAge;
      if (change.age == change.maxAge) {
         BlockState st = world.getBlockState(change.pos);
         if (!st.isAir() && world instanceof ServerWorld s) {
            BlockEntity tileentity = st.hasBlockEntity() ? world.getBlockEntity(change.pos) : null;
            Block.getDroppedStacks(st, s, change.pos, tileentity).forEach(stack -> {
               if (!stack.isEmpty() && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
                  ItemScatterer.spawn(world, change.pos, DefaultedList.ofSize(1, stack));
               }
            });
            st.onStacksDropped(s, change.pos, ItemStack.EMPTY, false);
         }

         world.setBlockState(change.pos, change.queuedState, 3);
      }

      return bl;
   }

   public void addBlockChange(QueuedBlockChange change) {
      if (!(change.queuedState.getBlock() instanceof CharterStoneBlock)
         && this.queuedBlockChanges.stream().noneMatch(queuedBlockChange -> queuedBlockChange.pos.equals(change.pos))) {
         this.queuedBlockChanges.add(change);
         this.zaWarudo.syncComponent(CharterComponents.CHARTER);
      }
   }

   @Environment(EnvType.CLIENT)
   public void clientTick() {
      Random random = this.zaWarudo.random;
      if (this.zaWarudo.getTime() % 4L == 0L) {
         WorldParticleBuilder squareBuilder = ParticleBuilders.create(CharterParticles.SQUARE)
            .setScale(0.025F + random.nextFloat() * 0.05F, 0.0F)
            .setScaleEasing(Easing.SINE_IN)
            .setAlpha(0.0F, 0.9F + random.nextFloat() * 0.1F, 0.0F)
            .setAlphaEasing(Easing.SINE_OUT, Easing.SINE_IN)
            .setAlphaCoefficient(1.2F)
            .setColorCoefficient(0.65F)
            .setColorEasing(Easing.SINE_IN)
            .setSpinEasing(Easing.SINE_IN, Easing.CUBIC_OUT)
            .setSpinCoefficient(1.5F)
            .enableNoClip();
         WorldParticleBuilder smokeBuilder = ParticleBuilders.create(LodestoneParticles.SMOKE_PARTICLE)
            .setAlpha(0.0F, 0.06F, 0.04F)
            .setAlphaEasing(Easing.SINE_IN)
            .setScale(0.0F, 0.2F, 0.1F)
            .setScaleEasing(Easing.SINE_IN)
            .setColorEasing(Easing.SINE_IN)
            .randomOffset(0.1F, 0.2F)
            .overrideRemovalProtocol(SpecialRemovalProtocol.ENDING_CURVE_INVISIBLE)
            .enableNoClip();

         for (QueuedBlockChange c : this.queuedBlockChanges) {
            int lifetime = (int)(40.0 / ((double)random.nextFloat() * 0.8 + 0.2));
            int spinDirection = random.nextBoolean() ? 1 : -1;
            int spinOffset = random.nextInt(360);
            float spinStrength = 0.5F + random.nextFloat() * 0.25F;
            float colorTilt = (0.7F + random.nextFloat() * 0.3F) / 255.0F;
            Color startingColor = new Color(249.0F * colorTilt, 186.0F * colorTilt, 78.0F * colorTilt);
            Color endingColor = new Color(154.0F * colorTilt, 107.0F * colorTilt, 86.0F * colorTilt);
            squareBuilder.setLifetime(lifetime)
               .setSpin(0.0F, spinStrength * (float)spinDirection, 0.0F)
               .setSpinOffset((float)spinOffset)
               .setColor(startingColor, endingColor)
               .evenlySpawnAtAlignedEdges(this.zaWarudo, c.pos, c.queuedState, 9);
            smokeBuilder.setLifetime(lifetime)
               .setSpin(0.0F, spinStrength * (float)spinDirection * 0.1F, 0.0F)
               .setSpinOffset((float)spinOffset)
               .setColor(startingColor, endingColor)
               .evenlySpawnAtAlignedEdges(this.zaWarudo, c.pos, c.queuedState, 15);
         }
      }
   }
}
