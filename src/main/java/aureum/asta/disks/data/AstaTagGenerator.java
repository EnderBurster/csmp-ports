package aureum.asta.disks.data;

import aureum.asta.disks.init.AstaBlocks;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteBlocks;
import aureum.asta.disks.ports.blast.common.init.BlastBlocks;
import aureum.asta.disks.ports.elysium.Elysium;
import aureum.asta.disks.ports.elysium.machine.ElysiumMachines;
import aureum.asta.disks.ports.mason.init.MasonObjects;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

import java.util.concurrent.CompletableFuture;

public class AstaTagGenerator extends FabricTagProvider<Block> {

    public AstaTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.BLOCK, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        //Wall
        TagKey<Block> wallTag = BlockTags.WALLS;
        addTag(wallTag, AstaBlocks.KYRATOS_BRICK_WALL);
        addTag(wallTag, AstaBlocks.WHITE_SANDSTONE_WALL);
        addTag(wallTag, AstaBlocks.RED_SANDSTONE_WALL);
        addTag(wallTag, AstaBlocks.PINK_SANDSTONE_WALL);
        addTag(wallTag, AstaBlocks.YELLOW_SANDSTONE_WALL);
        addTag(wallTag, AstaBlocks.BLUE_SANDSTONE_WALL);
        addTag(wallTag, AstaBlocks.BLUE_BRICK_WALL);
        addTag(wallTag, AstaBlocks.DARK_BLUE_BRICK_WALL);
        addTag(wallTag, AstaBlocks.PURPLE_BRICK_WALL);
        addTag(wallTag, AstaBlocks.WHITE_BRICK_WALL);
        addTag(wallTag, AmariteBlocks.AMETHYST_BRICK_WALL);

        // Pickaxe
        TagKey<Block> pickaxeTag = BlockTags.PICKAXE_MINEABLE;
        addTag(pickaxeTag, AstaBlocks.KYRATOS_PILLAR);

        addTag(pickaxeTag, AstaBlocks.KYRATOS);
        addTag(pickaxeTag, AstaBlocks.KYRATOS_BRICK_STAIRS);
        addTag(pickaxeTag, AstaBlocks.KYRATOS_BRICK_SLAB);
        addTag(pickaxeTag, AstaBlocks.KYRATOS_BRICK_WALL);
        addTag(pickaxeTag, AstaBlocks.CHISELED_KYRATOS_PRESSURE_PLATE);
        addTag(pickaxeTag, AstaBlocks.CHISELED_KYRATOS_BUTTON);

        addTag(pickaxeTag, AstaBlocks.WHITE_SANDSTONE);
        addTag(pickaxeTag, AstaBlocks.WHITE_SANDSTONE_STAIRS);
        addTag(pickaxeTag, AstaBlocks.WHITE_SANDSTONE_SLAB);
        addTag(pickaxeTag, AstaBlocks.WHITE_SANDSTONE_WALL);
        addTag(pickaxeTag, AstaBlocks.WHITE_SANDSTONE_PRESSURE_PLATE);
        addTag(pickaxeTag, AstaBlocks.WHITE_SANDSTONE_BUTTON);

        addTag(pickaxeTag, AstaBlocks.RED_SANDSTONE);
        addTag(pickaxeTag, AstaBlocks.RED_SANDSTONE_STAIRS);
        addTag(pickaxeTag, AstaBlocks.RED_SANDSTONE_SLAB);
        addTag(pickaxeTag, AstaBlocks.RED_SANDSTONE_WALL);
        addTag(pickaxeTag, AstaBlocks.RED_SANDSTONE_PRESSURE_PLATE);
        addTag(pickaxeTag, AstaBlocks.RED_SANDSTONE_BUTTON);

        addTag(pickaxeTag, AstaBlocks.PINK_SANDSTONE);
        addTag(pickaxeTag, AstaBlocks.PINK_SANDSTONE_STAIRS);
        addTag(pickaxeTag, AstaBlocks.PINK_SANDSTONE_SLAB);
        addTag(pickaxeTag, AstaBlocks.PINK_SANDSTONE_WALL);
        addTag(pickaxeTag, AstaBlocks.PINK_SANDSTONE_PRESSURE_PLATE);
        addTag(pickaxeTag, AstaBlocks.PINK_SANDSTONE_BUTTON);

        addTag(pickaxeTag, AstaBlocks.YELLOW_SANDSTONE);
        addTag(pickaxeTag, AstaBlocks.YELLOW_SANDSTONE_STAIRS);
        addTag(pickaxeTag, AstaBlocks.YELLOW_SANDSTONE_SLAB);
        addTag(pickaxeTag, AstaBlocks.YELLOW_SANDSTONE_WALL);
        addTag(pickaxeTag, AstaBlocks.YELLOW_SANDSTONE_PRESSURE_PLATE);
        addTag(pickaxeTag, AstaBlocks.YELLOW_SANDSTONE_BUTTON);

        addTag(pickaxeTag, AstaBlocks.BLUE_SANDSTONE);
        addTag(pickaxeTag, AstaBlocks.BLUE_SANDSTONE_STAIRS);
        addTag(pickaxeTag, AstaBlocks.BLUE_SANDSTONE_SLAB);
        addTag(pickaxeTag, AstaBlocks.BLUE_SANDSTONE_WALL);
        addTag(pickaxeTag, AstaBlocks.BLUE_SANDSTONE_PRESSURE_PLATE);
        addTag(pickaxeTag, AstaBlocks.BLUE_SANDSTONE_BUTTON);

        addTag(pickaxeTag, AstaBlocks.BLUE_BRICK);
        addTag(pickaxeTag, AstaBlocks.BLUE_BRICK_STAIRS);
        addTag(pickaxeTag, AstaBlocks.BLUE_BRICK_SLAB);
        addTag(pickaxeTag, AstaBlocks.BLUE_BRICK_WALL);
        addTag(pickaxeTag, AstaBlocks.BLUE_BRICK_PRESSURE_PLATE);
        addTag(pickaxeTag, AstaBlocks.BLUE_BRICK_BUTTON);

        addTag(pickaxeTag, AstaBlocks.DARK_BLUE_BRICK);
        addTag(pickaxeTag, AstaBlocks.DARK_BLUE_BRICK_STAIRS);
        addTag(pickaxeTag, AstaBlocks.DARK_BLUE_BRICK_SLAB);
        addTag(pickaxeTag, AstaBlocks.DARK_BLUE_BRICK_WALL);
        addTag(pickaxeTag, AstaBlocks.DARK_BLUE_BRICK_PRESSURE_PLATE);
        addTag(pickaxeTag, AstaBlocks.DARK_BLUE_BRICK_BUTTON);

        addTag(pickaxeTag, AstaBlocks.PURPLE_BRICK);
        addTag(pickaxeTag, AstaBlocks.PURPLE_BRICK_STAIRS);
        addTag(pickaxeTag, AstaBlocks.PURPLE_BRICK_SLAB);
        addTag(pickaxeTag, AstaBlocks.PURPLE_BRICK_WALL);
        addTag(pickaxeTag, AstaBlocks.PURPLE_BRICK_PRESSURE_PLATE);
        addTag(pickaxeTag, AstaBlocks.PURPLE_BRICK_BUTTON);

        addTag(pickaxeTag, AstaBlocks.WHITE_BRICK);
        addTag(pickaxeTag, AstaBlocks.WHITE_BRICK_STAIRS);
        addTag(pickaxeTag, AstaBlocks.WHITE_BRICK_SLAB);
        addTag(pickaxeTag, AstaBlocks.WHITE_BRICK_WALL);
        addTag(pickaxeTag, AstaBlocks.WHITE_BRICK_PRESSURE_PLATE);
        addTag(pickaxeTag, AstaBlocks.WHITE_BRICK_BUTTON);

        addTag(pickaxeTag, AstaBlocks.PROTECTION_RUNE);
        addTag(pickaxeTag, AstaBlocks.CREATION_RUNE);
        addTag(pickaxeTag, AstaBlocks.AMP_RUNE);

        addTag(pickaxeTag, AstaBlocks.KYRATOS_GLASS);
        addTag(pickaxeTag, AstaBlocks.KYRATOS_GLASS_PANE);

        addTag(pickaxeTag, AmariteBlocks.AMETHYST_BRICKS);
        addTag(pickaxeTag, AmariteBlocks.AMETHYST_BRICK_WALL);
        addTag(pickaxeTag, AmariteBlocks.AMETHYST_BRICK_STAIRS);
        addTag(pickaxeTag, AmariteBlocks.AMETHYST_BRICK_SLAB);
        addTag(pickaxeTag, AmariteBlocks.AMETHYST_PILLAR);
        addTag(pickaxeTag, AmariteBlocks.CHISELED_AMETHYST);
        addTag(pickaxeTag, AmariteBlocks.CHISELED_AMETHYST_PRESSURE_PLATE);
        addTag(pickaxeTag, AmariteBlocks.CHISELED_AMETHYST_BUTTON);

        addTag(pickaxeTag, AmariteBlocks.AMARITE_BLOCK);
        addTag(pickaxeTag, AmariteBlocks.BUDDING_AMARITE);
        addTag(pickaxeTag, AmariteBlocks.AMARITE_CLUSTER);
        addTag(pickaxeTag, AmariteBlocks.PARTIAL_AMARITE_BUD);
        addTag(pickaxeTag, AmariteBlocks.FRESH_AMARITE_BUD);
        addTag(pickaxeTag, AmariteBlocks.AMARITE_SPARK);
        addTag(pickaxeTag, AmariteBlocks.POTTED_YELLOW_CARNATION);

        addTag(pickaxeTag, BlastBlocks.DRY_ICE);
        addTag(pickaxeTag, BlastBlocks.REMOTE_DETONATOR);
        addTag(pickaxeTag, BlastBlocks.BONESBURRIER);

        addTag(pickaxeTag, MasonObjects.TORCHLIGHT);
        addTag(pickaxeTag, MasonObjects.SOULLIGHT);

        //Axe
        TagKey<Block> axeTag = BlockTags.AXE_MINEABLE;
        addTag(axeTag, BlastBlocks.STRIPMINER);
        addTag(axeTag, BlastBlocks.COLD_DIGGER);

        addTag(axeTag, AstaBlocks.KYRATOS_DOOR);

        addTag(axeTag, Blocks.BAMBOO_MOSAIC);
        addTag(axeTag, Blocks.BAMBOO_MOSAIC_SLAB);
        addTag(axeTag, Blocks.BAMBOO_MOSAIC_STAIRS);
        addTag(axeTag, BlockTags.ALL_HANGING_SIGNS);
        addTag(axeTag, BlockTags.BAMBOO_BLOCKS);
        addTag(axeTag, Blocks.CHISELED_BOOKSHELF);

        //Shovel
        TagKey<Block> shovelTag = BlockTags.SHOVEL_MINEABLE;
        addTag(shovelTag, BlastBlocks.GUNPOWDER_BLOCK);
        addTag(shovelTag, BlastBlocks.FOLLY_RED_PAINT);
        addTag(shovelTag, BlastBlocks.FRESH_FOLLY_RED_PAINT);
        addTag(shovelTag, BlastBlocks.DRIED_FOLLY_RED_PAINT);

        //Hoe
        TagKey<Block> hoeTag = BlockTags.HOE_MINEABLE;
        addTag(hoeTag, Blocks.CHERRY_LEAVES);
        addTag(hoeTag, Blocks.PINK_PETALS);
    }

    private void addTag(TagKey<Block> tag, TagKey<Block> tags) {
        getOrCreateTagBuilder(tag).addOptionalTag(tags);
    }

    protected void addTag(TagKey<Block> tag, Block block)
    {
        getOrCreateTagBuilder(tag).add(block);
    }

}
