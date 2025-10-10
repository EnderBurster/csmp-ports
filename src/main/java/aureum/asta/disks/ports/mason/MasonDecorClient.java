package aureum.asta.disks.ports.mason;

import aureum.asta.disks.ports.mason.client.*;
import aureum.asta.disks.ports.mason.init.MasonObjects;
import aureum.asta.disks.ports.mason.item.GlaiveItemRenderer;
import aureum.asta.disks.ports.mason.util.UpdatePressingUpDownPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;


public class MasonDecorClient implements ClientModInitializer {
    private static KeyBinding DESCEND;
    @Override
    public void onInitializeClient() {
        initParticles();
        EntityRendererRegistry.register(MasonObjects.SOULMOULD, SoulmouldEntityRenderer::new);
        EntityRendererRegistry.register(MasonObjects.SOUL_EXPLOSION, SoulExplosionRenderer::new);
        EntityRendererRegistry.register(MasonObjects.RIPPED_SOUL, RippedSoulEntityRenderer::new);
        EntityRendererRegistry.register(MasonObjects.BONEFLY, BoneflyEntityRenderer::new);
        EntityRendererRegistry.register(MasonObjects.RAVEN, RavenEntityRenderer::new);
        EntityRendererRegistry.register(MasonObjects.CHAINS, ChainsEntityRenderer::new);
        DESCEND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.aureum-asta-disks.descend", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_LEFT_SHIFT, // The keycode of the key
                "category.asta"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(world -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                UpdatePressingUpDownPacket.send(MinecraftClient.getInstance().options.jumpKey.isPressed(), DESCEND.isPressed());

            }
        });

        Identifier scytheId = Registries.ITEM.getId(MasonObjects.GLAIVE);
        GlaiveItemRenderer scytheItemRenderer = new GlaiveItemRenderer(scytheId);

        BuiltinItemRendererRegistry.INSTANCE.register(MasonObjects.GLAIVE, scytheItemRenderer);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(scytheItemRenderer);

        /*ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloader(scytheItemRenderer);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).addReloaderOrdering(ResourceManagerHelper.Client.MODELS, scytheItemRenderer.getQuiltId());
        BuiltinItemRendererRegistry.INSTANCE.register(MasonObjects.GLAIVE, scytheItemRenderer);*/

        ModelLoadingPlugin.register((manager) -> {
            manager.addModels(
                    new ModelIdentifier(scytheId.getNamespace(), scytheId.getPath() + "_gui", "inventory"),
                    new ModelIdentifier(scytheId.getNamespace(), scytheId.getPath() + "_handheld", "inventory")
            );
        });
    }

    public static final DefaultParticleType RAVEN_FEATHER = add("raven_feather");
    public static final DefaultParticleType RAVEN_FEATHER_ALBINO = add("raven_feather_albino");
    public static final DefaultParticleType RAVEN_FEATHER_GREEN = add("raven_feather_green");
    public static void initParticles() {
        ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
        registry.register(RAVEN_FEATHER, RavenFeatherParticle.Factory::new);
        registry.register(RAVEN_FEATHER_ALBINO, RavenFeatherParticle.Factory::new);
        registry.register(RAVEN_FEATHER_GREEN, RavenFeatherParticle.Factory::new);
    }

    private static DefaultParticleType add(String name) {
        return Registry.register(Registries.PARTICLE_TYPE, new Identifier(MasonDecor.MODID, name), FabricParticleTypes.simple());
    }
}
