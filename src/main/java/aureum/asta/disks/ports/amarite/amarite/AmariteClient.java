package aureum.asta.disks.ports.amarite.amarite;

import aureum.asta.disks.ports.amarite.amarite.items.AmariteLongswordItem;
import aureum.asta.disks.ports.amarite.amarite.items.AmariteMirrorItem;
import aureum.asta.disks.ports.amarite.mialib.MiaLibClient;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect.Animator;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteBlocks;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteEntities;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteParticles;

import static aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems.AMARITE_LONGSWORD;
import static aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems.AMARITE_MIRROR;

public class AmariteClient implements ClientModInitializer {
   public void onInitializeClient() {
      AmariteBlocks.initClient();
      AmariteEntities.initRenderers();
      AmariteItems.initClient();
      AmariteParticles.initFactories();
      FabricLoader.getInstance()
         .getModContainer("aureum-asta-disks")
         .ifPresent(
            modContainer -> ResourceManagerHelper.registerBuiltinResourcePack(
                  Amarite.id("gooberfied"), modContainer, "§dGooberfied Amarite", ResourcePackActivationType.NORMAL
               )
         );
      ClientPlayNetworking.registerGlobalReceiver(
         Amarite.id("budgrow"),
         (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            client.execute(
               () -> {
                  if (client.world != null) {
                     Vec3d point = Vec3d.ofCenter(pos);
                     float r = 0.6745098F;
                     float g = 0.3882353F;
                     float b = 0.8784314F;

                     for (int i = 0; i < 32; i++) {
                        ParticleBuilders.create(AmariteParticles.AMARITE)
                           .overrideAnimator(Animator.WITH_AGE)
                           .setLifetime(16)
                           .setAlpha(0.6F, 0.0F)
                           .setAlphaEasing(Easing.CUBIC_IN)
                           .setColorCoefficient(0.8F)
                           .setColorEasing(Easing.CIRC_OUT)
                           .setSpinEasing(Easing.SINE_IN)
                           .setColor(r, g, b, 1.0F)
                           .setScale(0.24F, 0.12F)
                           .setSpinOffset((float)client.world.getRandom().nextInt(360))
                           .setSpin(client.world.getRandom().nextBoolean() ? 0.5F : -0.5F)
                           .randomMotion(0.32F)
                           .randomOffset(0.5)
                           .spawn(client.world, point.x, point.y, point.z);
                     }
                  }
               }
            );
         }
      );

      initItemColors();
   }

   private void initItemColors()
   {
       ColorProviderRegistry.ITEM.register((ItemColorProvider)(stack, tintIndex) -> {

                   if (tintIndex == 0) {
                       PlayerEntity player = (PlayerEntity)(MiaLibClient.renderingEntityWithItem instanceof PlayerEntity
                               ? (PlayerEntity)MiaLibClient.renderingEntityWithItem
                               : MinecraftClient.getInstance().player);
                       if (player != null) {
                           return AmariteLongswordItem.getMode(player, stack).getSwordTint();
                       }
                   }

                   NbtCompound nbt = stack.getNbt();
                   return nbt != null && nbt.contains("malignant", 3) ? nbt.getInt("malignant") : 16777215;
               },
               new ItemConvertible[]{AMARITE_LONGSWORD}
       );
       ColorProviderRegistry.ITEM
               .register(
                       (ItemColorProvider)(stack, tintIndex) -> tintIndex != 1
                               ? 16777215
                               : AmariteMirrorItem.getMirrorColor(
                               stack,
                               MinecraftClient.getInstance().player == null
                                       ? 1.0F
                                       : MinecraftClient.getInstance().player.getItemCooldownManager().getCooldownProgress(stack.getItem(), 1.0F)
                       ),
                       new ItemConvertible[]{AMARITE_MIRROR}
               );
   }
}
