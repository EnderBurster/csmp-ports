package aureum.asta.disks.blocks;

import aureum.asta.disks.init.AstaBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AmpRune extends BlockWithEntity {
   public AmpRune(Settings settings) {
      super(settings);
   }

   @Override
   public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return this.crateOutlineShape();
   }

   private VoxelShape crateOutlineShape()
   {
      return VoxelShapes.cuboid(0f, 0f, 0f, 1f, 10f/16f, 1f);
   }

   @Override
   public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new AmpBlockEntity(pos, state);
   }

   @Override
   public BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.MODEL;
   }

   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
      return checkType(type, AstaBlockEntities.AMP_BLOCK, AmpBlockEntity::tick);
   }

   @Override
   public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      if (!world.isClient) {
         AmpBlockEntity be = (AmpBlockEntity) world.getBlockEntity(pos);
         List<ItemStack> items = be.getItems();

         for(ItemStack stack : items)
         {
            if(stack.isEmpty() || be.isLocked()) continue;
            dropStack(world, pos, stack);
         }
      }

      super.onBreak(world, pos, state, player);
   }

   @Override
   public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      if (!world.isClient) {
         AmpBlockEntity be = (AmpBlockEntity) world.getBlockEntity(pos);
         ItemStack held = player.getStackInHand(hand);

         if (!be.getItems().get(0).isEmpty()) {
            ItemStack removed = be.removeLastItem();
            be.sync();
            if (!removed.isEmpty()) {
               player.getInventory().offerOrDrop(removed);
            }
         } else {
            ItemStack copy = held.copy();
            copy.setCount(1);
            if (be.addItem(copy)) {
               held.decrement(1);
               be.sync();
            }
         }
      }
      return ActionResult.SUCCESS;
   }
}