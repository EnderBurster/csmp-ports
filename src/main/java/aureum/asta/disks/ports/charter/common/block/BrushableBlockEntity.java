package aureum.asta.disks.ports.charter.common.block;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.charter.common.init.CharterBlocks;
import com.mojang.logging.LogUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BrushableBlockEntity extends BlockEntity {
    private int brushesCount;
    private long nextDustTime;
    private long nextBrushTime;
    private ItemStack item;
    @Nullable
    private Direction hitDirection;
    @Nullable
    private Identifier lootTable;
    private long lootTableSeed;

    protected Block currentBlock;
    protected Block baseBlock;

    public BrushableBlockEntity(BlockPos pos, BlockState state, Block newCurrentBlock, Block newBaseBlock) {
        super(CharterBlocks.SUSPICIOUS_DIRT_ENTITY, pos, state);
        this.item = ItemStack.EMPTY;
        this.currentBlock = newCurrentBlock;
        this.baseBlock = newBaseBlock;
    }

    public BrushableBlockEntity(BlockPos pos, BlockState state) {
        super(CharterBlocks.SUSPICIOUS_DIRT_ENTITY, pos, state);
        this.item = ItemStack.EMPTY;
        this.currentBlock = CharterBlocks.SUSPICIOUS_DIRT;
        this.baseBlock = Blocks.DIRT;
    }

    public void updateBlockData(Block newCurrentBlock, Block newBaseBlock)
    {
        this.currentBlock = newCurrentBlock;
        this.baseBlock = newBaseBlock;
    }

    public boolean brush(long worldTime, PlayerEntity player, Direction hitDirection) {
        if (this.hitDirection == null) {
            this.hitDirection = hitDirection;
        }

        this.nextDustTime = worldTime + 40L;
        if (worldTime >= this.nextBrushTime && this.world instanceof ServerWorld) {
            this.nextBrushTime = worldTime + 10L;
            int i = this.getDustedLevel();
            if (++this.brushesCount >= 10) {
                this.finishBrushing(player);
                return true;
            } else {
                this.world.scheduleBlockTick(this.getPos(), this.currentBlock, 40);
                int j = this.getDustedLevel();
                if (i != j) {
                    BlockState blockState = this.getCachedState();
                    BlockState blockState2 = (BlockState)blockState.with(Properties.DUSTED, j);
                    this.world.setBlockState(this.getPos(), blockState2, 3);
                }

                return false;
            }
        } else {
            return false;
        }
    }

    private void finishBrushing(PlayerEntity player) {
        if (this.world != null && this.world.getServer() != null) {
            this.spawnItem(player);
            this.world.syncWorldEvent(3008, this.getPos(), Block.getRawIdFromState(this.getCachedState()));
            this.world.setBlockState(this.pos, this.baseBlock.getDefaultState(), 3);
        }
    }

    private void spawnItem(PlayerEntity player) {
        if (this.world != null && this.world.getServer() != null) {
            if (!this.item.isEmpty()) {
                double d = (double) EntityType.ITEM.getWidth();
                double e = (double)1.0F - d;
                double f = d / (double)2.0F;
                Direction direction = (Direction) Objects.requireNonNullElse(this.hitDirection, Direction.UP);
                BlockPos blockPos = this.pos.offset(direction, 1);
                double g = Math.floor((double)blockPos.getX()) + (double)0.5F * e + f;
                double h = Math.floor((double)blockPos.getY() + (double)0.5F) + (double)(EntityType.ITEM.getHeight() / 2.0F);
                double i = Math.floor((double)blockPos.getZ()) + (double)0.5F * e + f;
                ItemEntity itemEntity = new ItemEntity(this.world, g, h, i, this.item.split(this.world.random.nextInt(21) + 10));
                itemEntity.setVelocity(Vec3d.ZERO);
                this.world.spawnEntity(itemEntity);
                this.item = ItemStack.EMPTY;
            }

        }
    }

    public void scheduledTick() {
        if (this.world != null) {
            if (this.brushesCount != 0 && this.world.getTime() >= this.nextDustTime) {
                int i = this.getDustedLevel();
                this.brushesCount = Math.max(0, this.brushesCount - 2);
                int j = this.getDustedLevel();
                if (i != j) {
                    this.world.setBlockState(this.getPos(), (BlockState)this.getCachedState().with(Properties.DUSTED, j), 3);
                }

                int k = 4;
                this.nextDustTime = this.world.getTime() + 4L;
            }

            if (this.brushesCount == 0) {
                this.hitDirection = null;
                this.nextDustTime = 0L;
                this.nextBrushTime = 0L;
            } else {
                this.world.scheduleBlockTick(this.getPos(), currentBlock, (int)(this.nextDustTime - this.world.getTime()));
            }

        }
    }

    private boolean readLootTableFromNbt(NbtCompound nbt) {
        if (nbt.contains("loot_table", 8)) {
            this.lootTable = new Identifier(nbt.getString("loot_table"));
            this.lootTableSeed = nbt.getLong("loot_table_seed");
            return true;
        } else {
            return false;
        }
    }

    private boolean writeLootTableToNbt(NbtCompound nbt) {
        if (this.lootTable == null) {
            return false;
        } else {
            nbt.putString("loot_table", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                nbt.putLong("loot_table_seed", this.lootTableSeed);
            }

            return true;
        }
    }

    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = super.toInitialChunkDataNbt();
        if (this.hitDirection != null) {
            nbtCompound.putInt("hit_direction", this.hitDirection.ordinal());
        }

        nbtCompound.put("item", this.item.writeNbt(new NbtCompound()));
        return nbtCompound;
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public void readNbt(NbtCompound nbt) {
        if (!this.readLootTableFromNbt(nbt) && nbt.contains("item")) {
            this.item = ItemStack.fromNbt(nbt.getCompound("item"));
        }

        if (nbt.contains("hit_direction")) {
            this.hitDirection = Direction.values()[nbt.getInt("hit_direction")];
        }

    }

    protected void writeNbt(NbtCompound nbt) {
        if (!this.writeLootTableToNbt(nbt)) {
            nbt.put("item", this.item.writeNbt(new NbtCompound()));
        }

    }

    public void setLootTable(Identifier lootTable, long seed) {
        this.lootTable = lootTable;
        this.lootTableSeed = seed;
    }

    private int getDustedLevel() {
        if (this.brushesCount == 0) {
            return 0;
        } else if (this.brushesCount < 3) {
            return 1;
        } else {
            return this.brushesCount < 6 ? 2 : 3;
        }
    }

    @Nullable
    public Direction getHitDirection() {
        return this.hitDirection;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public void setItem(ItemStack newItem) {
        this.item = newItem;
    }
}
