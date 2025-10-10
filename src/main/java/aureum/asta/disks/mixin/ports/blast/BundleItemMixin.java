package aureum.asta.disks.mixin.ports.blast;

import aureum.asta.disks.ports.blast.common.item.PipeBombItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BundleItem.class)
public class BundleItemMixin {
    @WrapOperation(method = "addToBundle", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;writeNbt(Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/nbt/NbtCompound;"))
    private static NbtCompound blast$addPipeBombToBundle(ItemStack pipeBombStack, NbtCompound nbtCompound, Operation<NbtCompound> operation) {
        if (pipeBombStack.getItem() instanceof PipeBombItem) {
            // arm pipe bomb
            pipeBombStack.getOrCreateNbt().putBoolean("Armed", true);

            // give pipe bomb a fake display stack
            ItemStack fakeStack = ItemStack.EMPTY;
            while (fakeStack.getRarity() != Rarity.COMMON || fakeStack.isEmpty() || fakeStack.getCount() > fakeStack.getMaxCount()) {
                Random random = Random.create();
                fakeStack = new ItemStack(Registries.ITEM.get(random.nextInt(Registries.ITEM.size())), pipeBombStack.getCount() * random.nextInt((64 / pipeBombStack.getMaxCount())));
                //System.out.println(fakeStack);
            }


            NbtCompound fakeStackNbt = new NbtCompound();
            fakeStack.writeNbt(fakeStackNbt);
            pipeBombStack.getOrCreateNbt().put("FakeBundleDisplayStack", fakeStackNbt);
        }

        return operation.call(pipeBombStack, nbtCompound);
    }
}
