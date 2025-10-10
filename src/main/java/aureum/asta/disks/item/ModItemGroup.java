package aureum.asta.disks.item;

import aureum.asta.disks.init.AstaBlocks;
import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.charter.common.init.CharterItems;
import aureum.asta.disks.ports.pickyourpoison.PickYourPoison;
import aureum.asta.disks.ports.blast.common.init.BlastItems;
import aureum.asta.disks.ports.impaled.init.ImpaledItems;
import aureum.asta.disks.ports.mason.init.MasonObjects;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroup
{
    public static ItemGroup AureumAsta;
    public static ItemGroup ImpaledGroup;
    public static ItemGroup BlastGroup;
    public static ItemGroup Poison;
    public static ItemGroup Mason;
    public static ItemGroup Charter;
    public static ItemGroup Kyratos;

    public static void registerItemGroup()
    {
        AureumAsta = FabricItemGroup.builder(new Identifier(AureumAstaDisks.MOD_ID, "aureum-asta-disks"))
                .displayName(Text.literal("Aureum Asta"))
                .icon(() -> new ItemStack(AstaItems.SCYTHE)).build();

        ImpaledGroup = FabricItemGroup.builder(new Identifier(AureumAstaDisks.MOD_ID, "impaled"))
                .displayName(Text.literal("Impaled"))
                .icon(() -> new ItemStack(ImpaledItems.ATLAN)).build();
        BlastGroup = FabricItemGroup.builder(new Identifier(AureumAstaDisks.MOD_ID, "blast"))
                .displayName(Text.literal("Blast"))
                .icon(() -> new ItemStack(BlastItems.BOMB)).build();
        Poison = FabricItemGroup.builder(new Identifier(AureumAstaDisks.MOD_ID, "pick-you-poison"))
                .displayName(Text.literal("Pick Your Poison"))
                .icon(() -> new ItemStack(PickYourPoison.GOLDEN_POISON_DART_FROG_BOWL)).build();
        Mason = FabricItemGroup.builder(new Identifier(AureumAstaDisks.MOD_ID, "mason-decor"))
                .displayName(Text.literal("Mason Decor"))
                .icon(() -> new ItemStack(MasonObjects.SOULMOULD_ITEM)).build();
        Charter = FabricItemGroup.builder(new Identifier(aureum.asta.disks.ports.charter.Charter.MODID, "charter"))
                .displayName(Text.literal("Charter"))
                .icon(() -> new ItemStack(CharterItems.LESSER_DIVINITY)).build();
        Kyratos = FabricItemGroup.builder(new Identifier(AureumAstaDisks.MOD_ID, "kyratos"))
                .displayName(Text.literal("Kyratos"))
                .icon(() -> new ItemStack(AstaBlocks.KYRATOS)).build();
    }

}
