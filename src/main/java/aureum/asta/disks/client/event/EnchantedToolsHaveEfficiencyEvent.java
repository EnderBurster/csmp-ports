package aureum.asta.disks.client.event;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class EnchantedToolsHaveEfficiencyEvent implements ItemTooltipCallback {
    @Override
    public void getTooltip(ItemStack stack, TooltipContext tooltipContext, List<Text> list) {
    }
}
