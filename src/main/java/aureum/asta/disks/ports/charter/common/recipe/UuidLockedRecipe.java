package aureum.asta.disks.ports.charter.common.recipe;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.charter.Charter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.world.World;

import java.util.UUID;

public class UuidLockedRecipe extends ShapedRecipe {
    private final UUID allowedPlayer;

    public UuidLockedRecipe(ShapedRecipe base, UUID allowedPlayer) {
        super(base.getId(), base.getGroup(), base.getCategory(), base.getWidth(), base.getHeight(), base.getIngredients(), base.getOutput(null));
        this.allowedPlayer = allowedPlayer;
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        // Check if there's a player and UUID matches
        if (MinecraftClient.getInstance() == null || MinecraftClient.getInstance().player == null || !Charter.bannedUuids.contains(MinecraftClient.getInstance().player.getUuid())) {
            return false;
        }
        // Fall back to normal shaped recipe matching
        return super.matches(inv, world);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CharterRecipes.UUID_LOCKED_SHAPED_SERIALIZER;
    }

    public UUID getAllowedPlayer() {
        return allowedPlayer;
    }
}