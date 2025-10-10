package aureum.asta.disks.api.lodestone.handlers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import aureum.asta.disks.api.lodestone.LodestoneLib;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.GenericScreenParticle;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.ScreenParticleEffect;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.ScreenParticleType;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.base.ScreenParticle;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.emitter.ItemParticleEmitter;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.emitter.ParticleEmitter;
import dev.emi.emi.screen.RecipeScreen;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.fabricmc.loader.api.FabricLoader;
import org.joml.Matrix4f;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import  net.minecraft.client.gui.screen.GameModeSelectionScreen;

public class ScreenParticleHandler {
   public static Map<Pair<ParticleTextureSheet, ScreenParticle.RenderOrder>, ArrayList<ScreenParticle>> PARTICLES = new HashMap<>();
   public static ArrayList<StackTracker> RENDERED_STACKS = new ArrayList<>();
   public static Map<Item, ParticleEmitter> EMITTERS = new HashMap<>();
   public static final Tessellator TESSELATOR = new Tessellator();
   public static boolean canSpawnParticles;
   public static boolean renderingHotbar;

   public static void clientTick() {
      PARTICLES.forEach((pair, particles) -> {
         Iterator<ScreenParticle> iterator = particles.iterator();

         while (iterator.hasNext()) {
            ScreenParticle particle = iterator.next();
            particle.tick();
            if (!particle.isAlive()) {
               iterator.remove();
            }
         }
      });
      canSpawnParticles = true;
   }

   public static void renderItem(ItemStack stack) {
      MinecraftClient minecraft = MinecraftClient.getInstance();
      if (minecraft.world != null && minecraft.player != null) {
         if (minecraft.isPaused()) {
            return;
         }

         if (!stack.isEmpty()) {
            ParticleEmitter emitter = EMITTERS.get(stack.getItem());
            if (emitter != null) {
               MatrixStack matrixStack = RenderSystem.getModelViewStack();
               ScreenParticle.RenderOrder renderOrder = ScreenParticle.RenderOrder.AFTER_EVERYTHING;
               Screen screen = minecraft.currentScreen;
               if (screen != null) {
                  if (!FabricLoader.getInstance().isModLoaded("emi") || !(screen instanceof RecipeScreen)) {
                     renderOrder = ScreenParticle.RenderOrder.BEFORE_TOOLTIPS;
                  }

                  if (renderingHotbar) {
                     renderOrder = ScreenParticle.RenderOrder.BEFORE_UI;
                  }
               }

               Matrix4f last = matrixStack.peek().getPositionMatrix();
               float x = last.m03();
               float y = last.m13();
               if (canSpawnParticles) {
                  emitter.tick(stack, x, y, renderOrder);
               }

               RENDERED_STACKS.add(new StackTracker(stack, renderOrder, x, y));
            }
         }
      }
   }

   public static void renderParticles() {
      MinecraftClient client = MinecraftClient.getInstance();
      Screen screen = client.currentScreen;
      if (FabricLoader.getInstance().isModLoaded("emi") && screen instanceof RecipeScreen) {
         renderParticles(ScreenParticle.RenderOrder.AFTER_EVERYTHING);
      }

      if (screen == null || screen instanceof ChatScreen || screen instanceof GameModeSelectionScreen) {
         renderParticles(ScreenParticle.RenderOrder.AFTER_EVERYTHING, ScreenParticle.RenderOrder.BEFORE_UI);
      }

      RENDERED_STACKS.clear();
      canSpawnParticles = false;
   }

   public static void renderParticles(ScreenParticle.RenderOrder... renderOrders) {
      MinecraftClient client = MinecraftClient.getInstance();

      try {
         for (Entry<Pair<ParticleTextureSheet, ScreenParticle.RenderOrder>, ArrayList<ScreenParticle>> next : PARTICLES.entrySet()) {
            ParticleTextureSheet type = (ParticleTextureSheet)next.getKey().getFirst();
            if (Arrays.stream(renderOrders).anyMatch(o -> o.equals(next.getKey().getSecond()))) {
               type.begin(TESSELATOR.getBuffer(), client.getTextureManager());

               for (ScreenParticle nex : next.getValue()) {
                  if (nex instanceof GenericScreenParticle genericScreenParticle) {
                     genericScreenParticle.trackStack();
                  }

                  nex.render(TESSELATOR.getBuffer());
               }

               type.draw(TESSELATOR);
            }
         }
      } catch (Exception var8) {
         LodestoneLib.LOGGER.info("What is this\n" + var8.getMessage());
      }
   }

   public static <T extends ScreenParticleEffect> ScreenParticle addParticle(T options, double pX, double pY, double pXSpeed, double pYSpeed) {
      MinecraftClient minecraft = MinecraftClient.getInstance();
      ScreenParticleType<T> type = (ScreenParticleType<T>)options.type;
      ScreenParticleType.Factory<T> provider = type.factory;
      ScreenParticle particle = provider.createParticle(minecraft.world, options, pX, pY, pXSpeed, pYSpeed);
      ArrayList<ScreenParticle> list = PARTICLES.computeIfAbsent(Pair.of(particle.getTextureSheet(), particle.renderOrder), a -> new ArrayList<>());
      list.add(particle);
      return particle;
   }

   public static void wipeParticles(ScreenParticle.RenderOrder... renderOrders) {
      PARTICLES.forEach((pair, particles) -> {
         if (!particles.isEmpty() && (renderOrders.length == 0 || Arrays.stream(renderOrders).anyMatch(o -> o.equals(pair.getSecond())))) {
            particles.clear();
         }
      });
   }

   public static void registerItemParticleEmitter(Item item, ParticleEmitter.EmitterSupplier emitter) {
      EMITTERS.put(item, new ParticleEmitter(emitter));
   }

   public static void registerItemParticleEmitter(ParticleEmitter.EmitterSupplier emitter, Item... items) {
      for (Item item : items) {
         EMITTERS.put(item, new ParticleEmitter(emitter));
      }
   }

   public static void registerItemParticleEmitter(net.minecraft.util.Pair<ItemParticleEmitter, Item[]> pair) {
      registerItemParticleEmitter(((ItemParticleEmitter)pair.getLeft())::particleTick, (Item[])pair.getRight());
   }

   public static record StackTracker(ItemStack stack, ScreenParticle.RenderOrder order, float xOrigin, float yOrigin) {
   }
}
