package aureum.asta.disks.ports.charter.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.UUID;

public class UuidLockedShapedRecipeSerializer implements RecipeSerializer<UuidLockedRecipe> {

    @Override
    public UuidLockedRecipe read(Identifier id, JsonObject json) {
        // First let vanilla parse it as a normal ShapedRecipe
        ShapedRecipe base = RecipeSerializer.SHAPED.read(id, json);

        // Read UUID from JSON
        UUID uuid = UUID.fromString(JsonHelper.getString(json, "uuid"));

        return new UuidLockedRecipe(base, uuid);
    }

    @Override
    public UuidLockedRecipe read(Identifier id, PacketByteBuf buf) {
        ShapedRecipe base = RecipeSerializer.SHAPED.read(id, buf);
        UUID uuid = buf.readUuid();
        return new UuidLockedRecipe(base, uuid);
    }

    @Override
    public void write(PacketByteBuf buf, UuidLockedRecipe recipe) {
        RecipeSerializer.SHAPED.write(buf, recipe);
        buf.writeUuid(recipe.getAllowedPlayer());
    }
}