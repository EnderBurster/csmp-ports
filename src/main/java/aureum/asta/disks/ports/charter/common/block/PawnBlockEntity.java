package aureum.asta.disks.ports.charter.common.block;

import aureum.asta.disks.ports.charter.common.init.CharterBlocks;
import aureum.asta.disks.ports.charter.common.item.ContractItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PawnBlockEntity extends BlockEntity implements AelpecyemIsCool {
   private final DefaultedList<ItemStack> ITEMS = DefaultedList.ofSize(1, ItemStack.EMPTY);

   public PawnBlockEntity(BlockPos pos, BlockState state) {
      super(CharterBlocks.PAWN_BLOCK_ENTITY, pos, state);
   }

   public PawnBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
   }

   public static void tick(World tickerWorld, BlockPos pos, BlockState tickerState, PawnBlockEntity blockEntity) {
      if (tickerWorld != null && !tickerWorld.isClient) {
         BlockState state = tickerWorld.getBlockState(pos.offset(Direction.DOWN).offset(Direction.DOWN));
         if ((
               (Boolean)tickerState.get(Properties.LIT) && blockEntity.isValid(0, blockEntity.getContract()) && state.isOf(Blocks.SOUL_FIRE)
                  || state.isOf(Blocks.SOUL_LANTERN)
                  || state.isOf(Blocks.SOUL_CAMPFIRE)
                  || state.isOf(Blocks.SOUL_TORCH)
                  || state.isOf(Blocks.SOUL_WALL_TORCH)
            )
            && !tickerWorld.isClient()
            && ContractItem.getIndebtedUUID(blockEntity.getContract()) != null
            && tickerWorld.getPlayerByUuid(ContractItem.getIndebtedUUID(blockEntity.getContract())) != null) {
            PlayerEntity player = tickerWorld.getPlayerByUuid(ContractItem.getIndebtedUUID(blockEntity.getContract()));

            assert player != null;
         }
      }
   }

   public void writeNbt(NbtCompound nbt) {
      super.writeNbt(this.toClientTag(nbt));
   }

   public void fromClientTag(NbtCompound tag) {
      this.ITEMS.clear();
      Inventories.readNbt(tag, this.ITEMS);
   }

   public NbtCompound toClientTag(NbtCompound tag) {
      Inventories.writeNbt(tag, this.ITEMS);
      return tag;
   }

   public void readNbt(NbtCompound nbt) {
      this.fromClientTag(nbt);
      super.readNbt(nbt);
   }

   public int getMaxCountPerStack() {
      return 1;
   }

   @Override
   public DefaultedList<ItemStack> getItems() {
      return this.ITEMS;
   }

   public boolean isValid(int slot, ItemStack stack) {
      return ContractItem.isViable(stack);
   }

   public ItemStack getContract() {
      return this.getStack(0);
   }
}
