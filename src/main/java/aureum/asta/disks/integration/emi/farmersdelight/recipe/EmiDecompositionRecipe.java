package aureum.asta.disks.integration.emi.farmersdelight.recipe;

import aureum.asta.disks.integration.emi.farmersdelight.FarmersDelightPlugin;
import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.registry.TagsRegistry;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.DrawableWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.block.Block;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EmiDecompositionRecipe implements EmiRecipe {
    private static final Identifier GUI_TEXTURE = new Identifier(FarmersDelightMod.MOD_ID, "textures/gui/rei/decomposition.png");
    private static final EmiTexture BACKGROUND = new EmiTexture(GUI_TEXTURE, 8, 9, 102, 40);

    private static final DrawableWidget.DrawableWidgetConsumer NULL_RENDERABLE = (arg0, arg1, arg2, arg3) -> {
    };

    private static final PrimitiveIterator.OfInt ids = IntStream.iterate(0, n -> n + 1).iterator();

    protected final EmiRecipeCategory category;
    protected Identifier id;
    protected final List<EmiIngredient> catalysts;
    protected List<EmiIngredient> input;
    protected List<EmiStack> output;
    protected int width, height;
    protected boolean supportsRecipeTree;

    public EmiDecompositionRecipe(List<EmiIngredient> input, List<EmiStack> output, Collection<Block> catalysts) {

        this.category = FarmersDelightPlugin.DECOMPOSITION;
        this.id = new Identifier(String.format(
                "emi:%s/decomposition/%d",
                FarmersDelightMod.MOD_ID,
                ids.nextInt()));
        this.width = 102;
        this.height = catalysts.isEmpty() ? 48 : 64;
        this.supportsRecipeTree = false;
        this.input = input;
        this.output = output;

        this.catalysts = List.of(EmiIngredient.of(catalysts.stream()
                .map(Ingredient::ofItems)
                .map(EmiIngredient::of)
                .toList()));
    }

    @Override
    public void addWidgets(WidgetHolder gui) {
        gui.addTexture(BACKGROUND, 0, 0);

        gui.addSlot(input.get(0), 0, 16)
                .drawBack(false);

        gui.addSlot(output.get(0), 84, 16)
                .drawBack(false);

        gui.addSlot(EmiIngredient.of(Registries.BLOCK.getEntryList(TagsRegistry.COMPOST_ACTIVATORS).stream().flatMap(RegistryEntryList::stream).map(RegistryEntry::value).map(v->EmiStack.of(v.asItem())).collect(Collectors.toList())) , 55, 44)
                .customBackground(GUI_TEXTURE, 119, 0, 18, 18)
                .catalyst(true);

        gui.addDrawable(33, 30, 13, 13, NULL_RENDERABLE)
                .tooltip(((mouseX, mouseY) -> List.of(TooltipComponent.of(EmiPort.translatable("farmersdelight.rei.decomposition.light").asOrderedText()))));

        gui.addDrawable(46, 30, 13, 13, NULL_RENDERABLE)
                .tooltip(((mouseX, mouseY) -> List.of(TooltipComponent.of(EmiPort.translatable("farmersdelight.rei.decomposition.fluid").asOrderedText()))));

        gui.addDrawable(59, 30, 13, 13, NULL_RENDERABLE)
                .tooltip(((mouseX, mouseY) -> List.of(TooltipComponent.of(EmiPort.translatable("farmersdelight.rei.decomposition.accelerators").asOrderedText()))));
    }


    @Override
    public boolean supportsRecipeTree() {
        return supportsRecipeTree;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return category;
    }

    @Override
    public @Nullable Identifier getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return input;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return output;
    }

    @Override
    public int getDisplayWidth() {
        return width;
    }

    @Override
    public int getDisplayHeight() {
        return height;
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return catalysts;
    }
}
