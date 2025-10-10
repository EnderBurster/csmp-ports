package aureum.asta.disks.cca.blocks;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.api.lodestone.handlers.ScreenshakeHandler;
import aureum.asta.disks.api.lodestone.setup.LodestoneParticles;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect;
import aureum.asta.disks.api.lodestone.systems.screenshake.PositionedScreenshakeInstance;
import aureum.asta.disks.api.lodestone.systems.screenshake.ScreenshakeInstance;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteParticles;
import aureum.asta.disks.sound.AstaSounds;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import java.util.UUID;

public class WaterBarrier {
   private Box area;
   private UUID owner = null;
   private BlockPos center = null;
   private float size = 5;

   private boolean reversed = true;

   private boolean active = false;
   private boolean activating = false;
   private boolean changingSize = false;

   private DefaultedList<Boolean> ampBlocks = DefaultedList.ofSize(4, false);;

   private int activatingLifetime = 0;
   private final int maxActivatingLifetime = 80;

   private int shrinkTicksRemaining = 0;
   private float startSize = size;
   private float targetSize = size;
   private int totalTicks = 1200;

   double shrinkPerStep = 0.1;

   public WaterBarrier(double size, BlockPos pos) {
      this.center = pos;
      this.area = new Box(pos).expand(size);
      this.size = (float) size;
      this.startSize = (float) size;
      this.targetSize = (float) size;
   }

   public WaterBarrier() {
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
      //Vec3i vector = new Vec3i((int)Math.floor(this.area.getCenter().x), (int)Math.floor(this.area.getCenter().y), (int)Math.floor(this.area.getCenter().z));
      return center;
   }

   public boolean shouldQuery() {
      return true;
   }

   public boolean getActivating()
   {
      return this.activating;
   }

   public void setActivating(boolean newActivating)
   {
      this.activating = newActivating;
      this.activatingLifetime = this.maxActivatingLifetime;
   }

   public int getActivatingLifetime()
   {
      return this.activatingLifetime;
   }

   public int getMaxActivatingLifetime()
   {
      return this.maxActivatingLifetime;
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

      nbt.putInt("CenterX", this.center.getX());
      nbt.putInt("CenterY", this.center.getY());
      nbt.putInt("CenterZ", this.center.getZ());

      nbt.putInt("ShrinkTicksRemaining", this.shrinkTicksRemaining);
      nbt.putInt("TotalTicks", this.totalTicks);
      //nbt.putInt("AmpBlocks", this.ampBlocks);

      nbt.putFloat("Size", this.size);
      nbt.putFloat("TargetSize", this.targetSize);
      nbt.putFloat("StartSize", this.startSize);

      nbt.putBoolean("Active", this.active);
      nbt.putBoolean("ChangingSize", this.changingSize);

      byte[] array = new byte[this.ampBlocks.size()];
      for (int i = 0; i < this.ampBlocks.size(); i++) {
         array[i] = (byte) (this.ampBlocks.get(i) ? 1 : 0);
      }
      nbt.putByteArray("AmpBlocks", array);

      //nbt.putBoolean("Reversed", this.reversed);
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
      this.center = new BlockPos(tag.getInt("CenterX"), tag.getInt("CenterY"), tag.getInt("CenterZ"));

      this.shrinkTicksRemaining = tag.getInt("ShrinkTicksRemaining");
      this.totalTicks = tag.getInt("TotalTicks");
      //this.ampBlocks = tag.getInt("AmpBlocks");

      this.size = tag.getFloat("Size");
      this.targetSize = tag.getFloat("TargetSize");
      this.startSize = tag.getFloat("StartSize");

      this.active = tag.getBoolean("Active");
      this.changingSize = tag.getBoolean("ChangingSize");

      byte[] array = tag.getByteArray("AmpBlocks");
      this.ampBlocks = DefaultedList.ofSize(array.length, false);
      for (int i = 0; i < array.length; i++) {
         this.ampBlocks.set(i, array[i] == 1);
      }

      //this.reversed = tag.getBoolean("Reversed");
   }

   public boolean isActive()
   {
      return this.active;
   }

   public void setActive(boolean newActive, DefaultedList<Boolean> ampBlockAmount)
   {
      this.active = newActive;
      this.ampBlocks = ampBlockAmount;
   }

   public int getAmpBlocks()
   {
      int blocks = 0;
       for (Boolean ampBlock : this.ampBlocks) {
           if (ampBlock) {
               blocks++;
           }
       }
      return blocks;
   }

   public int getAmpBlocks(DefaultedList<Boolean> amplifBlocks)
   {
      int blocks = 0;
      for (Boolean ampBlock : amplifBlocks) {
         if (ampBlock) {
            blocks++;
         }
      }
      return blocks;
   }

   public void setSize(int size)
   {
      this.size = size;
      this.targetSize = size;
   }

   public boolean isReversed()
   {
      return this.reversed;
   }

   public void setReversed(boolean newReversed)
   {
      this.reversed = newReversed;
   }

   public boolean isOwner(PlayerEntity plr) {
      return plr.world.getComponent(AureumAstaDisks.KYRATOS).hasPassItem(plr);
      //return plr.getUuid().equals(this.owner);
   }

   public boolean isPosInside(BlockPos pos) {
      return this.isPosInside(pos.toCenterPos());
   }

   public boolean shouldReverseBlockChange(BlockPos pos) {
      return this.shouldReverseBlockChange(pos.toCenterPos());
   }

   public boolean shouldReverseBlockChange(Vec3d pos) {
      return this.isPosInside(pos) && this.getAmpBlocks() >= 3;
   }

   public boolean shouldDisableEnchantments(BlockPos pos) {
      return this.shouldDisableEnchantments(pos.toCenterPos());
   }

   public boolean shouldDisableEnchantments(Vec3d pos) {
      return this.isPosInside(pos) && this.getAmpBlocks() >= 4;
   }

   public boolean isPosInside(Vec3d pos) {
      float realSize = reversed ? this.size : this.size + 0.5f;
      Box box = new Box(this.center).expand(realSize);
      return this.active && this.getAmpBlocks() >= 2 && box.contains(pos);
   }

   public boolean isAtBorder(Vec3d pos)
   {
      float realSize1 = this.size - 1f;
      Box box1 = new Box(this.center).expand(realSize1);

      float realSize2 = this.size + 1f;
      Box box2 = new Box(this.center).expand(realSize2);

      return this.active && !box1.contains(pos) && box2.contains(pos);
   }

   public Box getArea() {
      return this.area;
   }

   public BlockPos getCenter() {
      return this.center;
   }

   public float getSize()
   {
      return this.size;
   }

   public VoxelShape getVoxelShape() {
      return VoxelShapes.cuboid(
         this.area.minX,
         this.area.minY,
         this.area.minZ,
         this.area.maxX,
         this.area.maxY,
         this.area.maxZ
      );
   }

   public VoxelShape createHollowShape() {
      float realSize = reversed ? this.size : this.size + 0.5f;
      Box box = new Box(this.center).expand(realSize);

      double minX = box.minX;
      double minY = box.minY;
      double minZ = box.minZ;
      double maxX = box.maxX;
      double maxY = box.maxY;
      double maxZ = box.maxZ;

      double thickness = 0.5;

      VoxelShape shape = VoxelShapes.empty();

      // Left wall
      shape = VoxelShapes.union(shape, VoxelShapes.cuboid(minX, minY, minZ, minX+thickness, maxY, maxZ));
      // Right wall
      shape = VoxelShapes.union(shape, VoxelShapes.cuboid(maxX-thickness, minY, minZ, maxX, maxY, maxZ));
      // Front wall
      shape = VoxelShapes.union(shape, VoxelShapes.cuboid(minX+thickness, minY, minZ, maxX-thickness, maxY, minZ+thickness));
      // Back wall
      shape = VoxelShapes.union(shape, VoxelShapes.cuboid(minX+thickness, minY, maxZ-thickness, maxX-thickness, maxY, maxZ));
      // Bottom
      shape = VoxelShapes.union(shape, VoxelShapes.cuboid(minX+thickness, minY, minZ+thickness, maxX-thickness, minY+thickness, maxZ-thickness));
      // Top
      shape = VoxelShapes.union(shape, VoxelShapes.cuboid(minX+thickness, maxY-thickness, minZ+thickness, maxX-thickness, maxY, maxZ-thickness));

      return shape;
   }

   private static double getIntersectionSize(Box barrierBox, Vec3d movement, Box entityBox) {
      if (movement.x > 0) return barrierBox.maxX - entityBox.minX;
      if (movement.x < 0) return entityBox.maxX - barrierBox.minX;
      if (movement.y > 0) return barrierBox.maxY - entityBox.minY;
      if (movement.y < 0) return entityBox.maxY - barrierBox.minY;
      if (movement.z > 0) return barrierBox.maxZ - entityBox.minZ;
      if (movement.z < 0) return entityBox.maxZ - barrierBox.minZ;
      return 0;
   }

   public void handleBorderCollision(World world) {
      if(!this.changingSize) return;

      //Reversed
      Box lastBox = new Box(this.getCenter()).expand(this.getSize() - this.shrinkPerStep);
      Box currentBox = new Box(this.getCenter()).expand(this.getSize());

      if(!reversed)
      {
         for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, currentBox.expand(1), e -> true)) {
            if (!currentBox.contains(entity.getBoundingBox().getCenter())) {
               // Entity is partly outside -> push inward
               Vec3d pos = entity.getPos();

               double clampedX = MathHelper.clamp(pos.x, currentBox.minX + 0.5, currentBox.maxX - 0.5);
               double clampedY = MathHelper.clamp(pos.y, currentBox.minY + 0.5, currentBox.maxY - 1.5);
               double clampedZ = MathHelper.clamp(pos.z, currentBox.minZ + 0.5, currentBox.maxZ - 0.5);

               Vec3d displacement = new Vec3d(clampedX - pos.x, clampedY - pos.y, clampedZ - pos.z);

               if (!displacement.equals(Vec3d.ZERO)) {
                  entity.addVelocity(displacement);
                  entity.velocityModified = true;
                  entity.move(MovementType.PISTON, displacement); // slight lift to avoid suffocation
               }
            }
         }
      }

      /*if (lastBox != null && this.changingSize) {
         Vec3d movement = new Vec3d(
                 currentBox.minX - lastBox.minX,
                 currentBox.minY - lastBox.minY,
                 currentBox.minZ - lastBox.minZ
         );

         Box swept = currentBox.union(lastBox);

         for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, swept.expand(0.1), e -> true)) {
            if (entity.getBoundingBox().intersects(currentBox)) {
               double overlap = getIntersectionSize(currentBox, movement, entity.getBoundingBox());
               if (overlap > 0) {
                  entity.move(MovementType.PISTON,
                          movement.normalize().multiply(overlap + 0.01));
               }
            }
         }
      }*/

      /*if (entity instanceof ServerPlayerEntity serverPlayer) {
         Vec3d pos = entity.getPos();
         Vec3d nextPos = entity.getPos().add(entity.getVelocity());

         double minY = box.minY + 0.5;
         if (!box.contains(nextPos) || !box.expand(0.05).contains(entity.getPos())) {
            double clampedX = MathHelper.clamp(nextPos.x, box.minX + 0.2, box.maxX - 0.2);
            double clampedY = pos.y;
            if (pos.y < minY) {
               clampedY = minY;
            }
            double clampedZ = MathHelper.clamp(nextPos.z, box.minZ + 0.2, box.maxZ - 0.2);

            serverPlayer.networkHandler.requestTeleport(clampedX, clampedY, clampedZ, entity.getYaw(), entity.getPitch());

            if (entity.getVelocity().y < 0) {
               entity.setVelocity(entity.getVelocity().x, 0.1, entity.getVelocity().z);
               entity.velocityModified = true;
            }
         }
      }*/
   }

   public void updateBorder() {
      if (this.shrinkTicksRemaining > 0 && this.size != this.targetSize) {
         // How much to shrink per step
         double totalShrink = this.startSize - this.targetSize;
         int totalSteps = (int)Math.ceil(totalShrink / shrinkPerStep);

         // How many ticks between each step
         int ticksPerStep = this.totalTicks / totalSteps;
         if(ticksPerStep <= 0)
         {
            ticksPerStep = 1;
         }

         // Shrink only when we hit a "step"
         if (this.shrinkTicksRemaining % ticksPerStep == 0) {
            this.size = (float) Math.max(this.targetSize, this.size - shrinkPerStep);
         }

         this.changingSize = true;
         this.shrinkTicksRemaining--;
      }
      else if(this.changingSize)
      {
         this.changingSize = false;
         this.shrinkTicksRemaining = 0;
         this.size = this.targetSize;
      }
   }

   public void adjustBorderSize(float start, float target, int durationTicks)
   {
      this.startSize = start;
      this.size = start;
      this.targetSize = target;
      this.shrinkTicksRemaining = durationTicks;
      this.totalTicks = durationTicks;
   }

   public void clientTick(World world)
   {
      if(this.activatingLifetime != 0)
      {
         this.activatingLifetime -= 1;
      }
      else
      {
         this.activating = false;
      }

      if(this.activatingLifetime == this.maxActivatingLifetime - 5)
      {
         this.spawnExplosion(world, this.center.toCenterPos());
      }
      else if(this.activatingLifetime == this.maxActivatingLifetime - 30)
      {
         ScreenshakeHandler.addScreenshake(new PositionedScreenshakeInstance(this.activatingLifetime, this.center.toCenterPos(), 5, 1, this.size*2 + 5, Easing.QUINTIC_OUT).setIntensity(1.6f, 0.0f).setEasing(Easing.BOUNCE_IN_OUT));
      }

      /*if(this.active && this.activatingLifetime < this.maxActivatingLifetime - 8)
      {
         spawnClientParticles(world, this.center.toCenterPos());
      }*/

      this.handleBorderCollision(world);
      this.updateBorder();

      for(int i = 0; i < this.ampBlocks.size(); i++)
      {
         if(this.ampBlocks.get(i))
         {
            this.spawnAmpParticles(world, 90*(i-1));
         }
      }
   }

   private void spawnAmpParticles(World world, float AngleXZ)
   {
      double angleXZ = Math.toRadians(AngleXZ);
      double slope = Math.toRadians(15);

      for (int i = 0; i < 4; i++) {
         double dist = (double)world.getRandom().nextFloat() * 3;
         Vec3d direction = new Vec3d(Math.cos(angleXZ) * Math.cos(slope), -Math.sin(slope), Math.sin(angleXZ) * Math.cos(slope)).normalize();

         Vec3d point = this.center.toCenterPos().add(direction.multiply(dist)).add(0, 0.8, 0);
         Vec3d motion = direction.multiply(-dist / 36.0);
         ParticleBuilders.create(AmariteParticles.ACCUMULATION)
                 .overrideAnimator(SimpleParticleEffect.Animator.WITH_AGE)
                 .setLifetime(36)
                 .setAlpha(1.0F, 0.0F)
                 .setAlphaEasing(Easing.CUBIC_IN)
                 .setColorCoefficient(0.8F)
                 .setColorEasing(Easing.QUAD_IN_OUT)
                 .setSpinEasing(Easing.SINE_IN)
                 .setColor(0.145f, 0.098f, 0.859f, 0f, 0.871f, 1.0F)
                 .setScale((float)(dist / 16.0), 0.12F)
                 .setSpinOffset((float)world.getRandom().nextInt(360))
                 .setSpin(world.getRandom().nextBoolean() ? 0.5F : -0.5F)
                 .setMotion(motion.x, motion.y, motion.z)
                 .spawn(world, point.x, point.y, point.z);
      }
   }

   public static void spawnClientParticles(World world, Vec3d origin) {
      double radius = 3.0;
      int rings = 4;
      int baseParticles = 10;
      double heightStep = 1.2;

      for (int ring = 0; ring < rings; ring++) {
         double currentHeight = (rings * heightStep) - (ring * heightStep);
         double currentRadius = radius * (ring / (double) rings);

         if(currentRadius <= 0.3) continue;

         int particlesThisRing = Math.max(4, (int)(baseParticles * (currentRadius / radius)));

         for (int i = 0; i < particlesThisRing; i++) {
            double angle = (world.getTime() * 0.2) + (ring * 0.5);

            double offsetX = Math.cos(angle) * currentRadius;
            double offsetZ = Math.sin(angle) * currentRadius;

            double px = origin.getX() + offsetX;
            double py = origin.getY() + currentHeight - 1;
            double pz = origin.getZ() + offsetZ;

            ParticleBuilders.create(LodestoneParticles.WISP_PARTICLE)
                    .setLifetime((int) Math.max(Math.ceil(40 * ((float)ring / (float) rings)), 1))
                    .overrideAnimator(SimpleParticleEffect.Animator.WITH_AGE)
                    .setAlphaEasing(Easing.BOUNCE_IN_OUT)
                    .setColorCoefficient(2.0f)
                    .setColorEasing(Easing.ELASTIC_OUT)
                    .setSpinEasing(Easing.SINE_IN)
                    .setScaleEasing(Easing.SINE_IN_OUT)
                    .setColor(0f, 0.098f, 1f, 0f, 0.078f, 0.788f, 0.8f)
                    .setAlpha(1.0f, 0.5f)
                    .setScale(0.15F + world.random.nextFloat()*0.05f, 0.015f)
                    .setSpin(world.random.nextBoolean() ? 0.1F : -0.1F)
                    .randomMotion(0.04, 0.01)
                    .randomOffset(0.1f, 0.1f)
                    .spawn(world, px, py, pz);

         }
      }
   }

   public void spawnExplosion(World world, Vec3d origin)
   {
      for (int i = 0; i < 60; i++)
      {
         ParticleBuilders.create(LodestoneParticles.WISP_PARTICLE)
                 .setLifetime(15)
                 .overrideAnimator(SimpleParticleEffect.Animator.WITH_AGE)
                 .setAlphaEasing(Easing.BOUNCE_IN_OUT)
                 .setColorCoefficient(2.0f)
                 .setColorEasing(Easing.ELASTIC_OUT)
                 .setSpinEasing(Easing.SINE_IN)
                 .setScaleEasing(Easing.SINE_IN_OUT)
                 .setColor(0f, 0.098f, 1f, 0f, 0.078f, 0.788f, 0.8f)
                 .setAlpha(1.0f, 0.5f)
                 .setScale(0.2F + world.random.nextFloat()*0.05f, 0.015f)
                 .setSpin(world.random.nextBoolean() ? 0.1F : -0.1F)
                 .randomMotion(1, 1)
                 .randomOffset(0.1f, 0.1f)
                 .spawn(world, origin.getX(), origin.getY() + 1.5f, origin.getZ());
      }
   }

   public static void spawnFountainClient(World world, Vec3d origin) {
      double radius = 2.0;
      int rings = 20;
      int baseParticles = 5;
      double heightStep = 1.2;

      for (int ring = 0; ring < rings; ring++) {
         double currentHeight = (rings * heightStep) - (ring * heightStep);
         double currentRadius = radius * (ring / (double) rings);
         int particlesThisRing = Math.max(2, (int)(baseParticles * (currentRadius / radius)));

         for (int i = 0; i < particlesThisRing; i++) {
            double angle = (world.getTime() * 0.2) + (ring * 0.5);

            double offsetX = Math.cos(angle) * currentRadius;
            double offsetZ = Math.sin(angle) * currentRadius;

            double px = origin.getX() + offsetX;
            double py = origin.getY() + currentHeight - 1;
            double pz = origin.getZ() + offsetZ;

            ParticleBuilders.create(LodestoneParticles.WISP_PARTICLE)
                    .setLifetime((int) Math.max(Math.ceil(40 * ((float)ring / (float) rings)), 1))
                    .overrideAnimator(SimpleParticleEffect.Animator.WITH_AGE)
                    .setAlphaEasing(Easing.BOUNCE_IN_OUT)
                    .setColorCoefficient(2.0f)
                    .setColorEasing(Easing.ELASTIC_OUT)
                    .setSpinEasing(Easing.SINE_IN)
                    .setScaleEasing(Easing.SINE_IN_OUT)
                    .setColor(0f, 0.098f, 1f, 0f, 0.078f, 0.788f, 0.8f)
                    .setAlpha(1.0f, 0.5f)
                    .setScale(0.1F + world.random.nextFloat()*0.05f, 0.01f)
                    .setSpin(world.random.nextBoolean() ? 0.1F : -0.1F)
                    .randomMotion(0.04, 0.01)
                    .randomOffset(0.1f, 0.1f)
                    .spawn(world, px, py, pz);

         }
      }
   }

   public void tick(World world) {
      if(this.activatingLifetime != 0)
      {
         this.activatingLifetime -= 1;
      }
      else
      {
         this.activating = false;
      }

      if(this.activatingLifetime == this.maxActivatingLifetime - 26)
      {
         world.playSound(null, this.center, AstaSounds.BARRIER_ACTIVATE, SoundCategory.PLAYERS, 4.0f, 1.0f);
      }

      this.handleBorderCollision(world);
      this.updateBorder();
   }

}
