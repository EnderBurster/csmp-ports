package aureum.asta.disks.blocks;

import aureum.asta.disks.init.AstaBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class AmpBlockEntity extends BlockEntity {

    //I probably should change this into a TrackedData of ItemStack
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private boolean locked = false;

    public AmpBlockEntity(BlockPos pos, BlockState state) {
        super(AstaBlockEntities.AMP_BLOCK, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items, true);
        nbt.putBoolean("Locked", locked);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        this.locked = nbt.getBoolean("Locked");

        this.items.clear();
        Inventories.readNbt(nbt, items);
    }

    public boolean isLocked()
    {
        return this.locked;
    }

    public void setLocked(boolean newLocked)
    {
        this.locked = newLocked;
    }

    public static void tick(World world, BlockPos pos, BlockState state, AmpBlockEntity blockEntity) {
    }

    @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    public boolean addItem(ItemStack stack) {
        if(locked) return false;

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, stack);
                return true;
            }
        }
        return false;
    }

    public ItemStack removeLastItem() {
        if(locked) return ItemStack.EMPTY;

        for (int i = items.size() - 1; i >= 0; i--) {
            if (!items.get(i).isEmpty()) {
                ItemStack stack = items.get(i);
                items.set(i, ItemStack.EMPTY);
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void sync() {
        if (this.world != null) {
            this.markDirty();
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_LISTENERS);
        }
    }

}
