package aureum.asta.disks.ports.amarite.amarite.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.GameRenderer;
import org.jetbrains.annotations.Nullable;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import aureum.asta.disks.ports.amarite.amarite.Amarite;

@Environment(EnvType.CLIENT)
public class MirrorScreen extends HandledScreen<MirrorScreenHandler> {
   static final Identifier TEXTURE = Amarite.id("textures/gui/container/beacon.png");
   private static final Text PRIMARY_POWER_TEXT = Text.translatable("block.minecraft.beacon.primary");
   private static final Text SECONDARY_POWER_TEXT = Text.translatable("block.minecraft.beacon.secondary");
   private final List<MirrorScreen.BeaconButtonWidget> buttons = Lists.newArrayList();
   @Nullable
   StatusEffect primaryEffect;
   @Nullable
   StatusEffect secondaryEffect;

   public MirrorScreen(MirrorScreenHandler handler, PlayerInventory inventory, Text title) {
      super(handler, inventory, title);
      this.backgroundWidth = 230;
      this.backgroundHeight = 219;
      handler.addListener(new ScreenHandlerListener() {
         public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
         }

         public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
            MirrorScreen.this.primaryEffect = ((MirrorScreenHandler)handler).getPrimaryEffect();
            MirrorScreen.this.secondaryEffect = ((MirrorScreenHandler)handler).getSecondaryEffect();
         }
      });
   }

   private <T extends ClickableWidget & MirrorScreen.BeaconButtonWidget> void addButton(T button) {
      this.addDrawableChild(button);
      this.buttons.add(button);
   }

   protected void init() {
      super.init();
      this.buttons.clear();
      this.addButton(new MirrorScreen.DoneButtonWidget(this.x + 164, this.y + 107));
      this.addButton(new MirrorScreen.CancelButtonWidget(this.x + 190, this.y + 107));

      for (int i = 0; i <= 2; i++) {
         int j = BeaconBlockEntity.EFFECTS_BY_LEVEL[i].length;
         int k = j * 22 + (j - 1) * 2;

         for (int l = 0; l < j; l++) {
            StatusEffect statusEffect = BeaconBlockEntity.EFFECTS_BY_LEVEL[i][l];
            MirrorScreen.EffectButtonWidget effectButtonWidget = new MirrorScreen.EffectButtonWidget(
               this.x + 76 + l * 24 - k / 2, this.y + 22 + i * 25, statusEffect, true, i
            );
            effectButtonWidget.active = false;
            this.addButton(effectButtonWidget);
         }
      }

      int j = BeaconBlockEntity.EFFECTS_BY_LEVEL[3].length + 1;
      int k = j * 22 + (j - 1) * 2;

      for (int l = 0; l < j - 1; l++) {
         StatusEffect statusEffect = BeaconBlockEntity.EFFECTS_BY_LEVEL[3][l];
         MirrorScreen.EffectButtonWidget effectButtonWidget = new MirrorScreen.EffectButtonWidget(
            this.x + 167 + l * 24 - k / 2, this.y + 47, statusEffect, false, 3
         );
         effectButtonWidget.active = false;
         this.addButton(effectButtonWidget);
      }

      MirrorScreen.EffectButtonWidget effectButtonWidget2 = new MirrorScreen.LevelTwoEffectButtonWidget(
         this.x + 167 + (j - 1) * 24 - k / 2, this.y + 47, BeaconBlockEntity.EFFECTS_BY_LEVEL[0][0]
      );
      effectButtonWidget2.visible = false;
      this.addButton(effectButtonWidget2);
   }

   public void handledScreenTick() {
      super.handledScreenTick();
      this.tickButtons();
   }

   void tickButtons() {
      int i = ((MirrorScreenHandler)this.handler).getProperties();
      this.buttons.forEach(button -> button.tick(i));
   }

   protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
      drawCenteredTextWithShadow(matrices, this.textRenderer, PRIMARY_POWER_TEXT, 62, 10, 14737632);
      drawCenteredTextWithShadow(matrices, this.textRenderer, SECONDARY_POWER_TEXT, 169, 10, 14737632);

      for (MirrorScreen.BeaconButtonWidget beaconButtonWidget : this.buttons) {
         if (beaconButtonWidget.shouldRenderTooltip()) {
            beaconButtonWidget.renderTooltip(matrices, mouseX - this.x, mouseY - this.y);
            break;
         }
      }
   }

   protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
      RenderSystem.setShader(GameRenderer::getPositionTexProgram);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, TEXTURE);
      int i = (this.width - this.backgroundWidth) / 2;
      int j = (this.height - this.backgroundHeight) / 2;
      this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
     // this.itemRenderer.field_4730 = 100.0F;
      this.itemRenderer.renderInGuiWithOverrides(matrices, new ItemStack(Items.NETHERITE_INGOT), i + 20, j + 109, 0, 100);
      this.itemRenderer.renderInGuiWithOverrides(matrices, new ItemStack(Items.EMERALD), i + 41, j + 109, 0, 100);
      this.itemRenderer.renderInGuiWithOverrides(matrices, new ItemStack(Items.DIAMOND), i + 41 + 22, j + 109, 0, 100);
      this.itemRenderer.renderInGuiWithOverrides(matrices, new ItemStack(Items.GOLD_INGOT), i + 42 + 44, j + 109, 0, 100);
      this.itemRenderer.renderInGuiWithOverrides(matrices, new ItemStack(Items.IRON_INGOT), i + 42 + 66, j + 109, 0, 100);
      //this.itemRenderer.field_4730 = 0.0F;
   }

   public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
      this.renderBackground(matrices);
      super.render(matrices, mouseX, mouseY, delta);
      this.drawMouseoverTooltip(matrices, mouseX, mouseY);
   }

   @Environment(EnvType.CLIENT)
   abstract static class BaseButtonWidget extends PressableWidget implements MirrorScreen.BeaconButtonWidget {
      private boolean disabled;

      protected BaseButtonWidget(int x, int y) {
         super(x, y, 22, 22, ScreenTexts.EMPTY);
      }

      protected BaseButtonWidget(int x, int y, Text message) {
         super(x, y, 22, 22, message);
      }

      public void getSidesShape(MatrixStack matrices, int mouseX, int mouseY, float delta) {
         RenderSystem.setShader(GameRenderer::getPositionTexProgram);
         RenderSystem.setShaderTexture(0, MirrorScreen.TEXTURE);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         int j = 0;
         if (!this.active) {
            j += this.width * 2;
         } else if (this.disabled) {
            j += this.width;
         } else if (this.isHovered()) {
            j += this.width * 3;
         }

         this.drawTexture(matrices, this.getX(), this.getY(), j, 219, this.width, this.height);
         this.renderExtra(matrices);
      }

      protected abstract void renderExtra(MatrixStack var1);

      public boolean isDisabled() {
         return this.disabled;
      }

      public void setDisabled(boolean disabled) {
         this.disabled = disabled;
      }

      @Override
      public boolean shouldRenderTooltip() {
         return this.hovered;
      }

      /*public void appendNarrations(NarrationMessageBuilder builder) {
         this.appendDefaultNarrations(builder);
      }*/
   }

   @Environment(EnvType.CLIENT)
   interface BeaconButtonWidget {
      boolean shouldRenderTooltip();

      void renderTooltip(MatrixStack var1, int var2, int var3);

      void tick(int var1);
   }

   @Environment(EnvType.CLIENT)
   class CancelButtonWidget extends MirrorScreen.IconButtonWidget {
      public CancelButtonWidget(int x, int y) {
         super(x, y, 112, 220, ScreenTexts.CANCEL);
      }

      public void onPress() {
         MirrorScreen.this.client.player.closeHandledScreen();
      }

      @Override
      public void tick(int level) {
      }

      @Override
      protected void appendClickableNarrations(NarrationMessageBuilder builder) {
      }
   }

   @Environment(EnvType.CLIENT)
   class DoneButtonWidget extends MirrorScreen.IconButtonWidget {
      public DoneButtonWidget(int x, int y) {
         super(x, y, 90, 220, ScreenTexts.DONE);
      }

      public void onPress() {
         ClientPlayNetworking.send(
            Amarite.id("mirror"),
            (PacketByteBuf)PacketByteBufs.create()
               .writeInt(Registries.STATUS_EFFECT.getRawId(MirrorScreen.this.primaryEffect))
               .writeInt(Registries.STATUS_EFFECT.getRawId(MirrorScreen.this.secondaryEffect))
               .asByteBuf()
         );
      }

      @Override
      public void tick(int level) {
         this.active = ((MirrorScreenHandler)MirrorScreen.this.handler).hasPayment();
      }

      @Override
      protected void appendClickableNarrations(NarrationMessageBuilder builder) {

      }
   }

   @Environment(EnvType.CLIENT)
   class EffectButtonWidget extends MirrorScreen.BaseButtonWidget {
      private final boolean primary;
      protected final int level;
      private StatusEffect effect;
      private Sprite sprite;
      private Text tooltip;

      public EffectButtonWidget(int x, int y, StatusEffect statusEffect, boolean primary, int level) {
         super(x, y);
         this.primary = primary;
         this.level = level;
         this.init(statusEffect);
      }

      protected void init(StatusEffect statusEffect) {
         this.effect = statusEffect;
         this.sprite = MinecraftClient.getInstance().getStatusEffectSpriteManager().getSprite(statusEffect);
         this.tooltip = this.getEffectName(statusEffect);
      }

      protected MutableText getEffectName(StatusEffect statusEffect) {
         return Text.translatable(statusEffect.getTranslationKey());
      }

      public void onPress() {
         if (!this.isDisabled()) {
            if (this.primary) {
               MirrorScreen.this.primaryEffect = this.effect;
            } else {
               MirrorScreen.this.secondaryEffect = this.effect;
            }

            MirrorScreen.this.tickButtons();
         }
      }

      @Override
      public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
         MirrorScreen.this.renderTooltip(matrices, this.tooltip, mouseX, mouseY);
      }

      @Override
      protected void renderExtra(MatrixStack matrices) {
         RenderSystem.setShaderTexture(0, this.sprite.getAtlasId());
         drawSprite(matrices, this.getX() + 2, this.getY() + 2, 0, 18, 18, this.sprite);
      }

      @Override
      public void tick(int level) {
         this.active = this.level < level;
         this.setDisabled(this.effect == (this.primary ? MirrorScreen.this.primaryEffect : MirrorScreen.this.secondaryEffect));
      }

      protected MutableText getNarrationMessage() {
         return this.getEffectName(this.effect);
      }

      @Override
      protected void appendClickableNarrations(NarrationMessageBuilder builder) {

      }
   }

   @Environment(EnvType.CLIENT)
   abstract class IconButtonWidget extends MirrorScreen.BaseButtonWidget {
      private final int u;
      private final int v;

      protected IconButtonWidget(int x, int y, int u, int v, Text message) {
         super(x, y, message);
         this.u = u;
         this.v = v;
      }

      @Override
      protected void renderExtra(MatrixStack matrices) {
         this.drawTexture(matrices, this.getX() + 2, this.getY() + 2, this.u, this.v, 18, 18);
      }

      @Override
      public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
         MirrorScreen.this.renderTooltip(matrices, MirrorScreen.this.title, mouseX, mouseY);
      }
   }

   @Environment(EnvType.CLIENT)
   class LevelTwoEffectButtonWidget extends MirrorScreen.EffectButtonWidget {
      public LevelTwoEffectButtonWidget(int x, int y, StatusEffect statusEffect) {
         super(x, y, statusEffect, false, 3);
      }

      @Override
      protected MutableText getEffectName(StatusEffect statusEffect) {
         return Text.translatable(statusEffect.getTranslationKey()).append(" II");
      }

      @Override
      public void tick(int level) {
         if (MirrorScreen.this.primaryEffect != null) {
            this.visible = true;
            this.init(MirrorScreen.this.primaryEffect);
            super.tick(level);
         } else {
            this.visible = false;
         }
      }
   }
}
