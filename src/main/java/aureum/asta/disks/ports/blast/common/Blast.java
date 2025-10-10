package aureum.asta.disks.ports.blast.common;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.blast.common.init.BlastBlocks;
import aureum.asta.disks.ports.blast.common.init.BlastEntities;
import aureum.asta.disks.ports.blast.common.init.BlastItems;
import aureum.asta.disks.ports.blast.common.init.BlastSoundEvents;
import aureum.asta.disks.ports.blast.common.recipe.PipeBombRecipe;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;


public class Blast implements ModInitializer {
    public static final String MODID = "aureum-asta-disks";

    public static final Identifier FIREWORK_SYNC_PACKET_ID = AureumAstaDisks.id("firework_sync");

    public static final SpecialRecipeSerializer<PipeBombRecipe> PIPE_BOMB = Registry.register(Registries.RECIPE_SERIALIZER, AureumAstaDisks.id("pipe_bomb_recipe"), new SpecialRecipeSerializer<>(PipeBombRecipe::new));

    public static final TrackedDataHandler<Direction> FACING = new TrackedDataHandler<>() {
        public void write(PacketByteBuf packetByteBuf, Direction direction) {
            packetByteBuf.writeEnumConstant(direction);
        }

        public Direction read(PacketByteBuf packetByteBuf) {
            return packetByteBuf.readEnumConstant(Direction.class);
        }

        public Direction copy(Direction direction) {
            return direction;
        }
    };

    @Override
    public void onInitialize() {
        TrackedDataHandlerRegistry.register(FACING);
        BlastSoundEvents.initialize();
        BlastEntities.init();
        BlastItems.init();
        BlastBlocks.init();
    }
}


