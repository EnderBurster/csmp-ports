package aureum.asta.disks.ports.impaled;

import aureum.asta.disks.entity.IPlayerTargeting;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class MialeeMisc implements ModInitializer {
    public static final String MOD_ID = "mialeemisc";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Identifier clickConsumePacket = id("click_consume");
    public static final Identifier targetPacket = id("target");
    public static final Identifier floatyPacket = id("floaty");
    public static final Identifier cooldownPacket = id("cooldown");

    @Override
    public void onInitialize() {
//        MialeeMiscConfig.loadConfig();
//        MialeeMiscConfig.saveConfig();
        ServerPlayNetworking.registerGlobalReceiver(targetPacket, (minecraftServer, serverPlayer, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
            int id = packetByteBuf.readInt();
            minecraftServer.execute(() -> {
                if (serverPlayer instanceof IPlayerTargeting targeting) {
                    if (serverPlayer.getWorld().getEntityById(id) instanceof LivingEntity living) {
                        targeting.mialeeMisc$setLastTarget(living);
                    }
                }
            });
        });
    }

    public static ItemStack enchantStack(ItemStack stack, EnchantmentLevelEntry ... entry) {
        for (EnchantmentLevelEntry enchantmentLevelEntry : entry) {
            stack.addEnchantment(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
        }
        return stack;
    }

    public static ItemStack enchantedBook(EnchantmentLevelEntry ... entry) {
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        for (EnchantmentLevelEntry enchantmentLevelEntry : entry) {
            EnchantedBookItem.addEnchantment(stack, enchantmentLevelEntry);
        }
        return stack;
    }

    public static <T> Registry<T> createRegistry(Identifier id, Class<T> clazz) {
        return FabricRegistryBuilder.createSimple(clazz, id).buildAndRegister();
    }

    public static Identifier id(String ... path) {
        return namedId(MOD_ID, path);
    }

    public static Identifier namedId(String namespace, String ... path) {
        return new Identifier(namespace, String.join(".", path));
    }
}
