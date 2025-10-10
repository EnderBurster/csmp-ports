package aureum.asta.disks.init;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.blocks.AmpBlockEntity;
import aureum.asta.disks.blocks.BarrierBlockEntity;
import aureum.asta.disks.blocks.CreationBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.ObjectUtils;

public class AstaBlockEntities {
    public static <T extends BlockEntityType<?>> T register(String path, T blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, AureumAstaDisks.id(path), blockEntityType);
    }

    public static final BlockEntityType<BarrierBlockEntity> BARRIER_BLOCK = register(
            "barrier_block",
            BlockEntityType.Builder.create(BarrierBlockEntity::new, AstaBlocks.PROTECTION_RUNE).build(null)
    );

    public static final BlockEntityType<CreationBlockEntity> CREATION_BLOCK = register(
            "creation_block",
            BlockEntityType.Builder.create(CreationBlockEntity::new, AstaBlocks.CREATION_RUNE).build(null)
    );

    public static final BlockEntityType<AmpBlockEntity> AMP_BLOCK = register(
            "amp_block",
            BlockEntityType.Builder.create(AmpBlockEntity::new, AstaBlocks.AMP_RUNE).build(null)
    );

    public static void initialize() {
    }
}
