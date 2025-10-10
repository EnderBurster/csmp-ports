package aureum.asta.disks.ports.charter;

import aureum.asta.disks.blocks.client.PedestalBlockRenderer;
import aureum.asta.disks.init.AstaBlockEntities;
import aureum.asta.disks.mixin.ports.elysium.PistonMovingBlockEntityMixin;
import aureum.asta.disks.ports.charter.client.model.*;
import aureum.asta.disks.ports.charter.client.render.*;
import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import aureum.asta.disks.ports.charter.common.component.CharterWorldComponent;
import aureum.asta.disks.ports.charter.common.entity.EpitaphShockwaveEntity;
import aureum.asta.disks.ports.charter.common.init.CharterBlocks;
import aureum.asta.disks.ports.charter.common.init.CharterEntities;
import aureum.asta.disks.ports.charter.common.init.CharterItems;
import aureum.asta.disks.ports.charter.common.init.CharterParticles;
import aureum.asta.disks.ports.charter.common.item.ContractItem;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.api.layered.modifier.MirrorModifier;
import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotsBlock;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.Random;

public class CharterClient implements ClientModInitializer {
   public static final EntityModelLayer BLOODFLY_MODEL_LAYER = new EntityModelLayer(Charter.id("bloodfly"), "main");
   public static final EntityModelLayer ORNATE_HORNS_MODEL_LAYER = new EntityModelLayer(new Identifier("charter", "ornate_horns"), "main");
   public static final EntityModelLayer RIVETED_HORNS_MODEL_LAYER = new EntityModelLayer(new Identifier("charter", "rivet_horns"), "main");
   public static final EntityModelLayer CHAINS_MODEL_LAYER = new EntityModelLayer(new Identifier("charter", "chains"), "main");
   public static final EntityModelLayer EPITAPH_CHAINS_MODEL_LAYER = new EntityModelLayer(new Identifier("charter", "epitaph_chains"), "main");
   public static final EntityModelLayer HAND_MODEL_LAYER = new EntityModelLayer(new Identifier("charter", "hand"), "main");
   public static final EntityModelLayer HAND_MODEL_LAYER_SLIM = new EntityModelLayer(new Identifier("charter", "hand_slim"), "main");

   public void onInitializeClient() {
      CharterParticles.registerFactories();
      ModelPredicateProviderRegistry.register(
         CharterItems.CONTRACT, Charter.id("signed"), (stack, world, entity, i) -> ContractItem.isViable(stack) ? 1.0F : 0.0F
      );
      EntityRendererRegistry.register(CharterEntities.GOLDWEAVE_ITEM, GoldweaveItemEntityRenderer::new);
      EntityRendererRegistry.register(CharterEntities.CHAINS, ChainsEntityRenderer::new);
      EntityRendererRegistry.register(CharterEntities.EPITAPH_CHAINS, EpitaphChainsEntityRenderer::new);
      EntityRendererRegistry.register(CharterEntities.LESSER_DIVINITY_ENTITY, LesserDivinityRenderer::new);
      EntityRendererRegistry.register(CharterEntities.BROKEN_DIVINITY_ENTITY, LesserDivinityRenderer::new);
      EntityRendererRegistry.register(CharterEntities.SHOCKWAVE_ENTITY, EpitaphShockwaveRenderer::new);
      EntityRendererRegistry.register(CharterEntities.BLOODFLY, BloodflyEntityRenderer::new);

      BlockRenderLayerMap.INSTANCE.putBlock(CharterBlocks.CHARTER_STONE, RenderLayer.getCutout());

      //BuiltinItemRendererRegistry.INSTANCE.register(CharterItems.DUSK_EPITAPH, new DuskEpitaphRenderer());

      EntityModelLayerRegistry.registerModelLayer(BLOODFLY_MODEL_LAYER, BloodflyEntityModel::getTexturedModelData);
      EntityModelLayerRegistry.registerModelLayer(ORNATE_HORNS_MODEL_LAYER, OrnateHornsModel::getTexturedModelData);
      EntityModelLayerRegistry.registerModelLayer(RIVETED_HORNS_MODEL_LAYER, RivetedHornsModel::getTexturedModelData);
      EntityModelLayerRegistry.registerModelLayer(CHAINS_MODEL_LAYER, ChainsEntityModel::getTexturedModelData);
      EntityModelLayerRegistry.registerModelLayer(EPITAPH_CHAINS_MODEL_LAYER, EpitaphChainsEntityModel::getTexturedModelData);
      EntityModelLayerRegistry.registerModelLayer(HAND_MODEL_LAYER, () -> HandModel.getTexturedModelData(false));
      EntityModelLayerRegistry.registerModelLayer(HAND_MODEL_LAYER_SLIM, () -> HandModel.getTexturedModelData(true));

      BlockEntityRendererFactories.register(CharterBlocks.SUSPICIOUS_DIRT_ENTITY, BrushableBlockEntityRenderer::new);
      BlockEntityRendererFactories.register(CharterBlocks.SUSPICIOUS_SAND_ENTITY, BrushableBlockEntityRenderer::new);

      /*ClientTickEvents.START_WORLD_TICK.register(
              world -> ((CharterWorldComponent)world.getComponent(CharterComponents.CHARTER))
                      .clientTick(MinecraftClient.getInstance(), world)
      );*/

      initGauntlets();
      initEpitaph();

      initTestAnimation();
   }

   private static void initGauntlets() {
      Identifier gauntlet = Registries.ITEM.getId(CharterItems.ADVANCE_GAUNTLET);
      GauntletItemRenderer scytheItemRenderer = new GauntletItemRenderer(gauntlet);

      //ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerReloader(scytheItemRenderer);

      BuiltinItemRendererRegistry.INSTANCE.register(CharterItems.ADVANCE_GAUNTLET, scytheItemRenderer);
      ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(scytheItemRenderer);

      ModelLoadingPlugin.register((manager) -> {
         manager.addModels(
                 new ModelIdentifier(gauntlet.getNamespace(), gauntlet.getPath() + "_gui", "inventory"),
                 new ModelIdentifier(gauntlet.getNamespace(), gauntlet.getPath() + "_handheld", "inventory"),
                 new ModelIdentifier(gauntlet.getNamespace(), gauntlet.getPath() + "_handheld_left", "inventory")
         );
      });

      Identifier bastionGauntlet = Registries.ITEM.getId(CharterItems.BASTION_GAUNTLET);
      scytheItemRenderer = new GauntletItemRenderer(bastionGauntlet);
      ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(scytheItemRenderer);

      BuiltinItemRendererRegistry.INSTANCE.register(CharterItems.BASTION_GAUNTLET, scytheItemRenderer);
      ModelLoadingPlugin.register((manager) -> {
         manager.addModels(
                 new ModelIdentifier(bastionGauntlet.getNamespace(), bastionGauntlet.getPath() + "_gui", "inventory"),
                 new ModelIdentifier(bastionGauntlet.getNamespace(), bastionGauntlet.getPath() + "_handheld", "inventory"),
                 new ModelIdentifier(bastionGauntlet.getNamespace(), bastionGauntlet.getPath() + "_handheld_left", "inventory")
         );
      });
      Identifier hand = Registries.ITEM.getId(CharterItems.HAND);
      CharterHandItemRenderer renderer = new CharterHandItemRenderer(hand);
      ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(renderer);
      BuiltinItemRendererRegistry.INSTANCE.register(CharterItems.HAND, renderer);
   }

   private static void initEpitaph() {
      Identifier epitaph = Registries.ITEM.getId(CharterItems.DUSK_EPITAPH);
      DuskEpitaphRenderer scytheItemRenderer = new DuskEpitaphRenderer(epitaph);

      BuiltinItemRendererRegistry.INSTANCE.register(CharterItems.DUSK_EPITAPH, scytheItemRenderer);
      ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(scytheItemRenderer);

      ModelLoadingPlugin.register((manager) -> {
         manager.addModels(
                 new ModelIdentifier(epitaph.getNamespace(), epitaph.getPath() + "_gui", "inventory"),
                 new ModelIdentifier(epitaph.getNamespace(), epitaph.getPath() + "_in_hand", "inventory")
         );
      });
   }

   public static void initTestAnimation()
   {
      //You might use the EVENT to register new animations, or you can use Mixin.
      PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(new Identifier("charter", "animation"), 42, (player) -> {
         if (player instanceof ClientPlayerEntity) {
            //animationStack.addAnimLayer(42, testAnimation); //Add and save the animation container for later use.
            ModifierLayer<IAnimation> testAnimation =  new ModifierLayer<>();

            //testAnimation.addModifierBefore(new SpeedModifier(0.5f)); //This will be slow
            //testAnimation.addModifierBefore(new MirrorModifier(true)); //Mirror the animation
            return testAnimation;
         }
         return null;
      });

      PlayerAnimationAccess.REGISTER_ANIMATION_EVENT.register((player, animationStack) -> {
         ModifierLayer<IAnimation> layer = new ModifierLayer<>();
         animationStack.addAnimLayer(69, layer);
         PlayerAnimationAccess.getPlayerAssociatedData(player).set(new Identifier("charter", "test"), layer);
      });
   }

   public static void playTestAnimation() {
      //Use this for setting an animation without fade
      //PlayerAnimTestmod.testAnimation.setAnimation(new KeyframeAnimationPlayer(AnimationRegistry.animations.get("two_handed_vertical_right_right")));

      ModifierLayer<IAnimation> testAnimation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(MinecraftClient.getInstance().player).get(new Identifier("charter", "animation"));
      /*if (new Random().nextBoolean()) {
         testAnimation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(MinecraftClient.getInstance().player).get(new Identifier("charter", "animation"));
      } else {
         testAnimation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(MinecraftClient.getInstance().player).get(new Identifier("charter", "test"));
      }*/

      testAnimation.replaceAnimationWithFade(AbstractFadeModifier.functionalFadeIn(20, (modelName, type, value) -> value),
              new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation(new Identifier("charter", "animation.player.epitaph"))).setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL).setFirstPersonConfiguration(new FirstPersonConfiguration().setShowRightArm(true).setShowLeftItem(false))
      );

      /*if (testAnimation.getAnimation() != null && new Random().nextBoolean()) {
         //It will fade out from the current animation, null as newAnimation means no animation.
         testAnimation.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(20, Ease.LINEAR), null);
      } else {
         //Fade from current animation to a new one.
         //Will not fade if there is no animation currently.
         testAnimation.replaceAnimationWithFade(AbstractFadeModifier.functionalFadeIn(20, (modelName, type, value) -> value),
                 //new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation(new Identifier("charter", "two_handed_slash_vertical_right"))).setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL).setFirstPersonConfiguration(new FirstPersonConfiguration().setShowRightArm(true).setShowLeftItem(false))
                 new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation(new Identifier("charter", "animation.player.epitaph"))).setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL).setFirstPersonConfiguration(new FirstPersonConfiguration().setShowRightArm(true).setShowLeftItem(false))
         );
      }*/

   }
}
