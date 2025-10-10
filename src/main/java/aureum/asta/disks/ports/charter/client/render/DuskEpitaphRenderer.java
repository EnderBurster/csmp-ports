package aureum.asta.disks.ports.charter.client.render;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.amarite.mialib.util.Vec3f;
import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;

public class DuskEpitaphRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer, SimpleSynchronousResourceReloadListener {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final Identifier id;
    private final Identifier epitaphId;
    private ItemRenderer itemRenderer;
    private BakedModel inventoryScytheModel;
    public BakedModel worldScytheModel;

    public DuskEpitaphRenderer(Identifier tridentId) {
        this.id = new Identifier(tridentId.getNamespace(), tridentId.getPath() + "_renderer");
        this.epitaphId = tridentId;
    }

    public void reload(ResourceManager manager) {
        MinecraftClient mc = MinecraftClient.getInstance();
        this.itemRenderer = mc.getItemRenderer();
        this.inventoryScytheModel = mc.getBakedModelManager().getModel(new ModelIdentifier(this.epitaphId.getNamespace(), this.epitaphId.getPath() + "_gui", "inventory"));
        this.worldScytheModel = mc.getBakedModelManager().getModel(new ModelIdentifier(this.epitaphId.getNamespace(), this.epitaphId.getPath() + "_in_hand", "inventory"));
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        drawBaseItem(stack, mode, matrices, vertexConsumers, light, overlay);
        /*if(stack.asta$getOwnerEntity() instanceof PlayerEntity player)
        {
            AureumAstaDisks.LOGGER.info("Render Beam: {}", player.getComponent(CharterComponents.PLAYER_COMPONENT).renderBeam());
        }*/
        if (mode == ModelTransformationMode.GUI || mode == ModelTransformationMode.GROUND || mode == ModelTransformationMode.FIXED || !(stack.asta$getOwnerEntity() instanceof PlayerEntity player && player.getComponent(CharterComponents.PLAYER_COMPONENT).renderBeam())) return;

        matrices.push();

        if(mode == ModelTransformationMode.THIRD_PERSON_RIGHT_HAND || mode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND)
        {
            matrices.translate(0.0F, 0.0F, 0.125);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-10));
        }
        else matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(22));

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(
                GlStateManager.SrcFactor.SRC_ALPHA,
                GlStateManager.DstFactor.ONE
        );
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);

        buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);

        float size = 8f;
        float height = 80.0F;

        drawPyramid(matrices, buffer, light, overlay, size, height, ColorHelper.Argb.getArgb(255, 255, 255, 255));
        drawPyramid(matrices, buffer, light, overlay, size + 1, height, ColorHelper.Argb.getArgb(80, 255, 255, 230));
        drawPyramid(matrices, buffer, light, overlay, size + 5, height, ColorHelper.Argb.getArgb(20, 255, 255, 50));

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        MatrixStack.Entry entry = matrices.peek();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(entry.getPositionMatrix(), size, height, size).color(255, 255, 255, 255).next();
        buffer.vertex(entry.getPositionMatrix(), size, height, -size).color(255, 255, 255, 255).next();
        buffer.vertex(entry.getPositionMatrix(), -size, height, size).color(255, 255, 255, 255).next();
        buffer.vertex(entry.getPositionMatrix(), -size, height, -size).color(255, 255, 255, 255).next();

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);

        matrices.pop();
    }

    private void drawBaseItem(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        if (mode == ModelTransformationMode.GUI || mode == ModelTransformationMode.GROUND) {
            matrices.pop();
            matrices.push();
            this.itemRenderer.renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, this.inventoryScytheModel);
        } else {
            matrices.pop();
            matrices.push();
            boolean leftHanded = switch (mode) {
                case FIRST_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND -> true;
                default -> false;
            };
            this.itemRenderer.renderItem(stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, this.worldScytheModel);
        }
    }

    private void drawPyramid(MatrixStack matrices, VertexConsumer buffer, int light, int overlay, float size, float height, int color) {
        MatrixStack.Entry entry = matrices.peek();

        Vec3f top = new Vec3f(0, 0, 0);
        Vec3f[] base = {
                new Vec3f(-size, height, -size),
                new Vec3f(size, height, -size),
                new Vec3f(size, height, size),
                new Vec3f(-size, height, size)
        };

        for (int i = 0; i < 4; i++) {
            Vec3f v1 = base[i];
            Vec3f v2 = base[(i + 1) % 4];
            drawTriangle(entry, buffer, v1, v2, top, light, overlay, color);
        }
    }

    private void drawTriangle(MatrixStack.Entry entry, VertexConsumer buffer, Vec3f v1, Vec3f v2, Vec3f v3, int light, int overlay, int color) {
        buffer.vertex(entry.getPositionMatrix(), v1.getX(), v1.getY(), v1.getZ()).color(ColorHelper.Argb.getRed(color), ColorHelper.Argb.getGreen(color), ColorHelper.Argb.getBlue(color), ColorHelper.Argb.getAlpha(color)).next();
        buffer.vertex(entry.getPositionMatrix(), v2.getX(), v2.getY(), v2.getZ()).color(ColorHelper.Argb.getRed(color), ColorHelper.Argb.getGreen(color), ColorHelper.Argb.getBlue(color), ColorHelper.Argb.getAlpha(color)).next();
        buffer.vertex(entry.getPositionMatrix(), v3.getX(), v3.getY(), v3.getZ()).color(ColorHelper.Argb.getRed(color), ColorHelper.Argb.getGreen(color), ColorHelper.Argb.getBlue(color), ColorHelper.Argb.getAlpha(color)).next();
    }

    @Override
    public Identifier getFabricId() {
        return this.id;
    }
}