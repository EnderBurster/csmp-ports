package aureum.asta.disks.mixin.ports.enchancement;

import aureum.asta.disks.ports.enchancement.ClientScreenDataCache;
import aureum.asta.disks.ports.enchancement.MEnchantingTableScreen;
import com.llamalad7.mixinextras.sugar.Local;
import moriyashiine.enchancement.client.screen.EnchantingTableScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantingTableScreen.class)
public class EnchantingTableScreenMixin implements MEnchantingTableScreen{

    @Unique
    private static final int BOOKSHELF_Y = 9;

    @Unique
    private int bookshelfCount = 0;

    @Inject(method = "drawBackground",
    at = @At(value = "INVOKE", target = "Lmoriyashiine/enchancement/client/screen/EnchantingTableScreen;drawBook(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"))
    private void createBookshelfs(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci)
    {
        int posX = (((EnchantingTableScreen)(Object)this).width - ((EnchantingTableScreen)(Object)this).backgroundWidth) / 2;
        int posY = (((EnchantingTableScreen)(Object)this).height - ((EnchantingTableScreen)(Object)this).backgroundHeight) / 2 - 16;
        ItemStack bookshelfStack = new ItemStack(Items.BOOKSHELF);

// Draw item
        ((EnchantingTableScreen)(Object)this).client.getItemRenderer().renderInGui(matrices, bookshelfStack, posX + 154, posY + BOOKSHELF_Y);

// MatrixStack operations
        matrices.push();
        matrices.scale(0.5F, 0.5F, 0.5F);

// Draw tooltip text (scaled)
// Need to scale positions because of the MatrixStack scale
        String bookshelfCountText = ClientScreenDataCache.pendingBookshelfCount + "/15";
        int scaledX = (posX + 178) * 2;
        int scaledY = (posY + BOOKSHELF_Y + 20) * 2;

        ((EnchantingTableScreen)(Object)this).client.currentScreen.renderTooltip(matrices, Text.literal(bookshelfCountText), scaledX, scaledY);

        matrices.pop();
    }

    @Override
    public int getBookshelfCount()
    {
        return bookshelfCount;
    }

    @Override
    public void setBookshelfCount(int bookshelfCountNew)
    {
        bookshelfCount = bookshelfCountNew;
    }

}
