package aureum.asta.disks.item.client;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.item.custom.GrimoireItem;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

public class GrimoireItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer, SimpleSynchronousResourceReloadListener {
    private final Identifier id;
    private final Identifier grimoireId;
    private ItemRenderer itemRenderer;
    private BakedModel inventoryGrimoireModel;
    public BakedModel worldGrimoireModel;
    public final BookModel bookModel;

    public GrimoireItemRenderer(Identifier grimoireId) {
        this.id = new Identifier(grimoireId.getNamespace(), grimoireId.getPath() + "_renderer");
        this.grimoireId = grimoireId;
        this.bookModel = new BookModel(BookModel.getTexturedModelData().createModel());
    }

    public Identifier getFabricId() {
        return this.id;
    }

    public void reload(ResourceManager manager) {
        MinecraftClient mc = MinecraftClient.getInstance();
        this.itemRenderer = mc.getItemRenderer();
        this.inventoryGrimoireModel = mc.getBakedModelManager().getModel(new ModelIdentifier(this.grimoireId.getNamespace(), this.grimoireId.getPath() + "_gui", "inventory"));
        this.worldGrimoireModel = mc.getBakedModelManager().getModel(new ModelIdentifier(this.grimoireId.getNamespace(), this.grimoireId.getPath() + "_handheld", "inventory"));
    }

    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (mode == ModelTransformationMode.GUI || mode == ModelTransformationMode.GROUND || mode == ModelTransformationMode.FIXED) {
            matrices.pop();
            matrices.push();
            this.itemRenderer.renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, this.inventoryGrimoireModel);
        } else {
            boolean leftHanded = switch (mode) {
                case FIRST_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND -> true;
                default -> false;
            };

            boolean thirdPerson = switch (mode) {
                case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> true;
                default -> false;
            };

            matrices.push();

            assert MinecraftClient.getInstance().world != null;
            float time = (MinecraftClient.getInstance().world.getTime() + MinecraftClient.getInstance().getTickDelta());

            if(!thirdPerson)
            {
                matrices.translate(-0.2, -0.15, 0.0);
            }
            else {
                matrices.translate(0.0f, Math.sin(time * 0.1f)*0.1, 0.0F);
            }

            if (thirdPerson && !leftHanded)
            {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            }

            float tickDelta = MinecraftClient.getInstance().getTickDelta();

            float g = HeldBookAnimation.ticks + tickDelta;

            float l = MathHelper.lerp(tickDelta, HeldBookAnimation.pageAngle, HeldBookAnimation.nextPageAngle);
            float m = MathHelper.fractionalPart(l + 0.25F) * 1.6F - 0.3F;
            float n = MathHelper.fractionalPart(l + 0.75F) * 1.6F - 0.3F;
            float o = 1.0f;

            if(stack.getItem() instanceof GrimoireItem grimoire && grimoire.getTicksSinceLastShoot() > 100)
            {
                float ticks = MathHelper.lerp(tickDelta, grimoire.getTicksSinceLastShootPrev(), grimoire.getTicksSinceLastShoot());
                o = 1.0f - ((ticks - 100)/20f);
            }

            this.bookModel.setPageAngles(g, MathHelper.clamp(m, 0.0F, 1.0F), MathHelper.clamp(n, 0.0F, 1.0F), o);

            Identifier texture = AureumAstaDisks.id("textures/entity/grimoire_handheld.png");
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(texture));

            this.bookModel.render(matrices, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

            matrices.pop();
        }
    }
}