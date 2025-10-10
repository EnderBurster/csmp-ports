package aureum.asta.disks.mixin.ports.enchancement;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.client.AureumAstaDisksClient;
import aureum.asta.disks.ports.enchancement.SyncEnchantingTableBookshelfCountPayload;
import moriyashiine.enchancement.common.screenhandlers.EnchantingTableScreenHandler;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantingTableScreenHandler.class)
public class EnchantingTableScreenHandlerMixin {

    @Unique
    private ScreenHandlerContext context = null;

    @Shadow
    private static boolean canAccessPowerProvider(World world, BlockPos tablePos, BlockPos providerOffset) {
        return false;
    }

    @Unique
    private int calculateBookshelfCount() {
        float[] bookshelfCountArray = new float[]{0.0F};
        this.context.run((world, pos) -> {
            for(BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
                if (canAccessPowerProvider(world, pos, offset)) {
                    int var10002 = (int) bookshelfCountArray[0]++;
                } else {
                    BlockEntity patt9496$temp = world.getBlockEntity(pos.add(offset));
                    if (patt9496$temp instanceof ChiseledBookshelfBlockEntity) {
                        ChiseledBookshelfBlockEntity chiseledBookshelfBlockEntity = (ChiseledBookshelfBlockEntity)patt9496$temp;
                        if (world.getBlockState(pos.add(offset.getX() / 2, offset.getY(), offset.getZ() / 2)).isReplaceable()) {
                            bookshelfCountArray[0] += (float)chiseledBookshelfBlockEntity.getOpenSlotCount() / 3.0F;
                        }
                    }
                }
            }

        });
       return  Math.min(15, (int)bookshelfCountArray[0]);
    }

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V",
    at = @At(value = "INVOKE", target = "Lmoriyashiine/enchancement/common/screenhandlers/EnchantingTableScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;", ordinal = 0))
    public void setBookshelfs(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, CallbackInfo ci)
    {
        this.context = context;
        if (playerInventory.player instanceof ServerPlayerEntity player) {
            SyncEnchantingTableBookshelfCountPayload.send(player, calculateBookshelfCount());
        }
    }

}
