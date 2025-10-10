package aureum.asta.disks.ports.elysium.machine.electrode;

import aureum.asta.disks.ports.elysium.ElysiumDamageSources;
import aureum.asta.disks.ports.elysium.ElysiumSounds;
import aureum.asta.disks.ports.elysium.ElysiumUtil;
import aureum.asta.disks.ports.elysium.armour.ElysiumArmourComponent;
import aureum.asta.disks.ports.elysium.machine.BeamPowered;
import aureum.asta.disks.ports.elysium.machine.ElysiumMachineBlock;
import aureum.asta.disks.ports.elysium.machine.ElysiumMachines;
import aureum.asta.disks.ports.elysium.particles.ArcParticleOption;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Clearable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.jetbrains.annotations.Nullable;

public class ElectrodeBlockEntity extends BlockEntity implements Clearable, SidedInventory, NamedScreenHandlerFactory, BeamPowered {
   private static final int NUM_SLOTS = 9;
   @Nullable
   private BlockPos beamSourcePos;
   private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(9, ItemStack.EMPTY);
   private final Box shockAABB;
   @Nullable
   private UUID ownerUUID;

   public ElectrodeBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(ElysiumMachines.ELECTRODE_BE, blockPos, blockState);
      this.shockAABB = new Box(blockPos).expand(12.0);
   }

   public void setOwner(UUID uuid) {
      this.ownerUUID = uuid;
   }

   public static void tick(World level, BlockPos pos, BlockState state, ElectrodeBlockEntity be) {
      int power = (Integer)state.get(ElysiumMachines.ELYSIUM_POWER);
      if (be.getBeamSourcePos() != null) {
         int actualPower = be.getBeamPower(level, pos);
         if (actualPower == 0) {
            be.setBeamSourcePos(null);
         }

         if (actualPower != power) {
            Direction neighbourDir = ((Direction)state.get(Properties.FACING)).getOpposite();
            BlockPos neighbourPos = pos.offset(neighbourDir);
            level.setBlockState(pos, state.getStateForNeighborUpdate(neighbourDir, level.getBlockState(neighbourPos), level, pos, neighbourPos));
         }
      }

      boolean hasRod = (Boolean)state.get(ElectrodeBlock.HAS_ROD);
      int charges = (Integer)state.get(ElectrodeBlock.CHARGES);
      if (power >= 1) {
         if (charges < 4 && level.getTime() % (20L * (long)(5 - power)) == 0L) {
            level.setBlockState(pos, (BlockState)state.with(ElectrodeBlock.CHARGES, ++charges), 2);
         }

         if (charges > 0 && level.getTime() % 10L == 0L) {
            Vec3d vec3Pos = Vec3d.ofCenter(pos);
            List<LivingEntity> entities = level.getEntitiesByClass(LivingEntity.class, be.shockAABB, $ -> true)
               .stream()
               .filter(e -> !be.isImmune(e))
               .filter(e -> e.squaredDistanceTo(vec3Pos) <= (double)(hasRod ? 100 : 36))
               .filter(e -> level.raycast(new RaycastContext(e.getEyePos(), vec3Pos, ShapeType.COLLIDER, FluidHandling.WATER, e)).getBlockPos().equals(pos))
               .filter(e -> getConductivity(e) >= 0.0)
               .sorted(Comparator.comparingDouble(e -> e.squaredDistanceTo(vec3Pos) + getConductivity(e)))
               .limit(hasRod ? 1L : 3L)
               .toList();

            for (LivingEntity entity : entities) {
               ElysiumArmourComponent component = (ElysiumArmourComponent)ElysiumArmourComponent.KEY.getNullable(entity);
               if (component != null && component.hasElysiumArmour()) {
                  component.addCharge(hasRod ? 6.0F : 4.0F);
               } else {
                  entity.damage(entity.world.getDamageSources().create(ElysiumDamageSources.ELECTRODE), hasRod ? 8.0F : 4.0F);
               }

               if (hasRod) {
                  Vec3d knockbackDir = vec3Pos.subtract(entity.getPos()).multiply(1.0, 0.0, 1.0).normalize();
                  entity.takeKnockback(1.1, knockbackDir.x, knockbackDir.z);
               }

               ((ServerWorld)level)
                  .spawnParticles(
                     new ArcParticleOption(entity.getX(), entity.getBodyY(0.5), entity.getZ()),
                     vec3Pos.x,
                     vec3Pos.y + (hasRod ? 1.0 : 0.5),
                     vec3Pos.z,
                     1,
                     0.0,
                     0.0,
                     0.0,
                     0.0
                  );
            }

            if (entities.size() > 0) {
               level.setBlockState(pos, (BlockState)state.with(ElectrodeBlock.CHARGES, --charges), 2);
               level.playSound(null, pos, ElysiumSounds.ELECTRODE_ZAP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
         }
      }
   }

   private boolean isImmune(LivingEntity entity) {
      return entity.isSpectator()
         || entity instanceof PlayerEntity player && player.getAbilities().creativeMode
         || ElysiumArmourComponent.KEY.maybeGet(entity).filter(ElysiumArmourComponent::hasElysiumArmour).isEmpty()
            && (Objects.equals(entity.getUuid(), this.ownerUUID) || this.stacks.stream().anyMatch(s -> s.getName().getString().equals(entity.getEntityName())));
   }

   protected void writeNbt(NbtCompound nbt) {
      super.writeNbt(nbt);
      NbtList inv = new NbtList();
      this.stacks.stream().map(s -> s.writeNbt(new NbtCompound())).forEach(inv::add);
      nbt.put("stacks", inv);
      if (this.ownerUUID != null) {
         nbt.putUuid("owner", this.ownerUUID);
      }
   }

   public void readNbt(NbtCompound nbt) {
      super.readNbt(nbt);
      NbtList listTag = nbt.getList("stacks", 10);
      List<ItemStack> inv = listTag.stream().map(NbtCompound.class::cast).<ItemStack>map(ItemStack::fromNbt).toList();

      for (int i = 0; i < this.stacks.size() && i < inv.size(); i++) {
         this.stacks.set(i, inv.get(i));
      }

      if (nbt.containsUuid("owner")) {
         this.ownerUUID = nbt.getUuid("owner");
      }
   }

   private static double getConductivity(Entity entity) {
      double entityMagnetism = ElysiumMachines.ENTITY_CONDUCTIVITY.get(entity.getType()).orElse(0.0);
      double itemMagnetism = ElysiumUtil.getItemForEntity(entity).<Double>flatMap(ElysiumMachines.ITEM_CONDUCTIVITY::get).orElse(0.0);
      double armourMagnetism = entity instanceof LivingEntity lE ? getArmourConductivity(lE) : 0.0;
      return entityMagnetism + itemMagnetism + armourMagnetism;
   }

   private static double getArmourConductivity(LivingEntity entity) {
      double count = 0.0;

      for (ItemStack armour : entity.getArmorItems()) {
         count += ElysiumMachines.ITEM_CONDUCTIVITY.get(armour.getItem()).orElse(0.0);
      }

      return count;
   }

   public int[] getAvailableSlots(Direction side) {
      int[] slots = new int[9];
      int i = 0;

      while (i < 9) {
         slots[i] = i++;
      }

      return slots;
   }

   public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
      return slot < 9;
   }

   public boolean canExtract(int slot, ItemStack stack, Direction dir) {
      return slot < 9;
   }

   public int size() {
      return 9;
   }

   public boolean isEmpty() {
      return this.stacks.stream().allMatch(ItemStack::isEmpty);
   }

   public ItemStack getStack(int slot) {
      return slot < 9 ? (ItemStack)this.stacks.get(slot) : ItemStack.EMPTY;
   }

   public ItemStack removeStack(int slot, int amount) {
      ItemStack removing = this.getStack(slot);
      return removing.isEmpty() ? ItemStack.EMPTY : removing.split(amount);
   }

   public ItemStack removeStack(int slot) {
      ItemStack removing = this.getStack(slot);
      this.setStack(slot, ItemStack.EMPTY);
      return removing;
   }

   public void setStack(int slot, ItemStack stack) {
      if (slot < 9) {
         this.stacks.set(slot, stack);
      }
   }

   public boolean canBeUsedBy(PlayerEntity player) {
      return Objects.equals(player.getUuid(), this.ownerUUID) || player.isCreativeLevelTwoOp();
   }

   public boolean canPlayerUse(PlayerEntity player) {
      if (this.world == null || this.world.getBlockEntity(this.pos) != this) {
         return false;
      } else {
         return !this.canBeUsedBy(player)
            ? false
            : !(player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
      }
   }

   public void clear() {
      this.stacks.clear();
   }

   public Text getDisplayName() {
      return Text.translatable("container.elysium.electrode");
   }

   @Nullable
   public ScreenHandler createMenu(int i, PlayerInventory inventory, PlayerEntity player) {
      return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X1, i, inventory, this, 1);
   }

   @Nullable
   @Override
   public BlockPos getBeamSourcePos() {
      return this.beamSourcePos;
   }

   @Override
   public void setBeamSourcePos(@Nullable BlockPos pos) {
      this.beamSourcePos = pos;
   }

   @Override
   public boolean canAcceptBeam(Direction beamDir) {
      return ((ElysiumMachineBlock)ElysiumMachines.ELECTRODE.getFirst()).isReceivingSide(this.getCachedState(), beamDir.getOpposite());
   }
}
