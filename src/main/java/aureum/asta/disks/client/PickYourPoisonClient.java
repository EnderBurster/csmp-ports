package aureum.asta.disks.client;

import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import aureum.asta.disks.client.render.entity.PoisonDartEntityRenderer;
import aureum.asta.disks.client.render.entity.PoisonDartFrogEntityRenderer;
import aureum.asta.disks.client.render.model.FrogOnHeadModel;
import aureum.asta.disks.ports.pickyourpoison.PickYourPoison;

public class PickYourPoisonClient implements ClientModInitializer {
    private static final ManagedShaderEffect BLACK_SCREEN = ShaderEffectManager.getInstance()
            .manage(new Identifier("aureum-asta-disks", "shaders/post/blackscreen.json"));

    @Override
    public void onInitializeClient() {
        // MODEL LAYERS
        EntityRendererRegistry.register(PickYourPoison.POISON_DART_FROG, PoisonDartFrogEntityRenderer::new);
        EntityRendererRegistry.register(PickYourPoison.POISON_DART, PoisonDartEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(FrogOnHeadModel.MODEL_LAYER, FrogOnHeadModel::getTexturedModelData);

        // COMA SHADER
        ShaderEffectRenderCallback.EVENT.register(tickDelta -> {
            if (PickYourPoison.isComatose(MinecraftClient.getInstance().player)) {
                BLACK_SCREEN.render(tickDelta);
            }
        });

        // TRINKETS COMPAT
        if (PickYourPoison.isTrinketsLoaded) {
            TrinketsCompat.registerFrogTrinketRenderers(PickYourPoison.getAllFrogBowls());
        }
    }

}
