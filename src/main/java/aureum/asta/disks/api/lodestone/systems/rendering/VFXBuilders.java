package aureum.asta.disks.api.lodestone.systems.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import aureum.asta.disks.api.lodestone.helpers.RenderHelper;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat.DrawMode;

public class VFXBuilders {
   public static ScreenVFXBuilder createScreen() {
      return new ScreenVFXBuilder();
   }

   public static WorldVFXBuilder createWorld() {
      return new WorldVFXBuilder();
   }

   public static class ScreenVFXBuilder {
      public float r = 1.0F;
      public float g = 1.0F;
      public float b = 1.0F;
      public float a = 1.0F;
      public int light = -1;
      public float u0 = 0.0F;
      public float v0 = 0.0F;
      public float u1 = 1.0F;
      public float v1 = 1.0F;
      public float x0 = 0.0F;
      public float y0 = 0.0F;
      public float x1 = 1.0F;
      public float y1 = 1.0F;
      public int zLevel;
      public VertexFormat format;
      public Supplier<ShaderProgram> shader = GameRenderer::getPositionTexProgram;
      public Identifier texture;
      public ScreenVertexPlacementSupplier supplier;
      public BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();

      public ScreenVFXBuilder setPosTexDefaultFormat() {
         this.supplier = (b, l, x, y, u, v) -> b.vertex(l, x, y, (float)this.zLevel).texture(u, v).next();
         this.format = VertexFormats.POSITION_TEXTURE;
         return this;
      }

      public ScreenVFXBuilder setPosColorDefaultFormat() {
         this.supplier = (b, l, x, y, u, v) -> b.vertex(l, x, y, (float)this.zLevel).color(this.r, this.g, this.b, this.a).next();
         this.format = VertexFormats.POSITION_COLOR;
         return this;
      }

      public ScreenVFXBuilder setPosColorTexDefaultFormat() {
         this.supplier = (b, l, x, y, u, v) -> b.vertex(l, x, y, (float)this.zLevel)
               .color(this.r, this.g, this.b, this.a)
               .texture(u, v)
               .next();
         this.format = VertexFormats.POSITION_COLOR_TEXTURE;
         return this;
      }

      public ScreenVFXBuilder setPosColorTexLightmapDefaultFormat() {
         this.supplier = (b, l, x, y, u, v) -> b.vertex(l, x, y, (float)this.zLevel)
               .color(this.r, this.g, this.b, this.a)
               .texture(u, v)
               .light(this.light)
               .next();
         this.format = VertexFormats.POSITION_COLOR_TEXTURE_LIGHT;
         return this;
      }

      public ScreenVFXBuilder setFormat(VertexFormat format) {
         this.format = format;
         return this;
      }

      public ScreenVFXBuilder setShaderTexture(Identifier texture) {
         this.texture = texture;
         return this;
      }

      public ScreenVFXBuilder setShader(Supplier<ShaderProgram> shader) {
         this.shader = shader;
         return this;
      }

      public ScreenVFXBuilder setShader(ShaderProgram shader) {
         this.shader = () -> shader;
         return this;
      }

      public ScreenVFXBuilder setVertexSupplier(ScreenVertexPlacementSupplier supplier) {
         this.supplier = supplier;
         return this;
      }

      public ScreenVFXBuilder overrideBufferBuilder(BufferBuilder builder) {
         this.bufferbuilder = builder;
         return this;
      }

      public ScreenVFXBuilder setLight(int light) {
         this.light = light;
         return this;
      }

      public ScreenVFXBuilder setColor(Color color) {
         return this.setColor((float)color.getRed(), (float)color.getGreen(), (float)color.getBlue());
      }

      public ScreenVFXBuilder setColor(Color color, float a) {
         return this.setColor(color).setAlpha(a);
      }

      public ScreenVFXBuilder setColor(float r, float g, float b, float a) {
         return this.setColor(r, g, b).setAlpha(a);
      }

      public ScreenVFXBuilder setColor(float r, float g, float b) {
         this.r = r / 255.0F;
         this.g = g / 255.0F;
         this.b = b / 255.0F;
         return this;
      }

      public ScreenVFXBuilder setAlpha(float a) {
         this.a = a;
         return this;
      }

      public ScreenVFXBuilder setPositionWithWidth(float x, float y, float width, float height) {
         return this.setPosition(x, y, x + width, y + height);
      }

      public ScreenVFXBuilder setPosition(float x0, float y0, float x1, float y1) {
         this.x0 = x0;
         this.y0 = y0;
         this.x1 = x1;
         this.y1 = y1;
         return this;
      }

      public ScreenVFXBuilder setZLevel(int z) {
         this.zLevel = z;
         return this;
      }

      public ScreenVFXBuilder setUVWithWidth(float u, float v, float width, float height, float canvasSize) {
         return this.setUVWithWidth(u, v, width, height, canvasSize, canvasSize);
      }

      public ScreenVFXBuilder setUVWithWidth(float u, float v, float width, float height, float canvasSizeX, float canvasSizeY) {
         return this.setUVWithWidth(u / canvasSizeX, v / canvasSizeY, width / canvasSizeX, height / canvasSizeY);
      }

      public ScreenVFXBuilder setUVWithWidth(float u, float v, float width, float height) {
         this.u0 = u;
         this.v0 = v;
         this.u1 = u + width;
         this.v1 = v + height;
         return this;
      }

      public ScreenVFXBuilder setUV(float u0, float v0, float u1, float v1, float canvasSize) {
         return this.setUV(u0, v0, u1, v1, canvasSize, canvasSize);
      }

      public ScreenVFXBuilder setUV(float u0, float v0, float u1, float v1, float canvasSizeX, float canvasSizeY) {
         return this.setUV(u0 / canvasSizeX, v0 / canvasSizeY, u1 / canvasSizeX, v1 / canvasSizeY);
      }

      public ScreenVFXBuilder setUV(float u0, float v0, float u1, float v1) {
         this.u0 = u0;
         this.v0 = v0;
         this.u1 = u1;
         this.v1 = v1;
         return this;
      }

      public ScreenVFXBuilder begin() {
         this.bufferbuilder.begin(DrawMode.QUADS, this.format);
         return this;
      }

      public ScreenVFXBuilder blit(MatrixStack stack) {
         Matrix4f last = stack.peek().getPositionMatrix();
         RenderSystem.setShader(this.shader);
         if (this.texture != null) {
            RenderSystem.setShaderTexture(0, this.texture);
         }

         this.supplier.placeVertex(this.bufferbuilder, last, this.x0, this.y1, this.u0, this.v1);
         this.supplier.placeVertex(this.bufferbuilder, last, this.x1, this.y1, this.u1, this.v1);
         this.supplier.placeVertex(this.bufferbuilder, last, this.x1, this.y0, this.u1, this.v0);
         this.supplier.placeVertex(this.bufferbuilder, last, this.x0, this.y0, this.u0, this.v0);
         return this;
      }

      public ScreenVFXBuilder blit(MatrixStack stack, Consumer<ScreenVFXBuilder> gradientConsumer) {
         Matrix4f last = stack.peek().getPositionMatrix();
         RenderSystem.setShader(this.shader);
         if (this.texture != null) {
            RenderSystem.setShaderTexture(0, this.texture);
         }

         this.supplier.placeVertex(this.bufferbuilder, last, this.x0, this.y1, this.u0, this.v1);
         this.supplier.placeVertex(this.bufferbuilder, last, this.x1, this.y1, this.u1, this.v1);
         gradientConsumer.accept(this);
         this.supplier.placeVertex(this.bufferbuilder, last, this.x1, this.y0, this.u1, this.v0);
         this.supplier.placeVertex(this.bufferbuilder, last, this.x0, this.y0, this.u0, this.v0);
         return this;
      }

      public ScreenVFXBuilder run(Consumer<ScreenVFXBuilder> consumer) {
         consumer.accept(this);
         return this;
      }

      public ScreenVFXBuilder end() {
         BufferRenderer.drawWithGlobalProgram(this.bufferbuilder.end());
         return this;
      }

      public ScreenVFXBuilder draw(MatrixStack stack) {
         if (this.bufferbuilder.isBuilding()) {
            this.bufferbuilder.end();
         }

         this.begin();
         this.blit(stack);
         this.end();
         return this;
      }

      public interface ScreenVertexPlacementSupplier {
         void placeVertex(BufferBuilder var1, Matrix4f var2, float var3, float var4, float var5, float var6);
      }
   }

   public static class WorldVFXBuilder {
      float r = 1.0F;
      float g = 1.0F;
      float b = 1.0F;
      float a = 1.0F;
      float xOffset = 0.0F;
      float yOffset = 0.0F;
      float zOffset = 0.0F;
      int light = 15728880;
      float u0 = 0.0F;
      float v0 = 0.0F;
      float u1 = 1.0F;
      float v1 = 1.0F;
      VertexFormat format;
      WorldVertexPlacementSupplier supplier;

      public WorldVFXBuilder setPosColorDefaultFormat() {
         return this.setVertexSupplier((c, l, x, y, z, u, v) -> {
            if (l == null) {
               c.vertex((double)x, (double)y, (double)z).color(this.r, this.g, this.b, this.a).next();
            } else {
               c.vertex(l, x, y, z).color(this.r, this.g, this.b, this.a).next();
            }
         }).setFormat(VertexFormats.POSITION_COLOR);
      }

      public WorldVFXBuilder setPosColorLightmapDefaultFormat() {
         return this.setVertexSupplier((c, l, x, y, z, u, v) -> {
            if (l == null) {
               c.vertex((double)x, (double)y, (double)z).color(this.r, this.g, this.b, this.a).light(this.light).next();
            } else {
               c.vertex(l, x, y, z).color(this.r, this.g, this.b, this.a).light(this.light).next();
            }
         }).setFormat(VertexFormats.POSITION_COLOR_LIGHT);
      }

      public WorldVFXBuilder setPosTexDefaultFormat() {
         return this.setVertexSupplier((c, l, x, y, z, u, v) -> {
            if (l == null) {
               c.vertex((double)x, (double)y, (double)z).texture(u, v).next();
            } else {
               c.vertex(l, x, y, z).texture(u, v).next();
            }
         }).setFormat(VertexFormats.POSITION_TEXTURE);
      }

      public WorldVFXBuilder setPosColorTexDefaultFormat() {
         return this.setVertexSupplier((c, l, x, y, z, u, v) -> {
            if (l == null) {
               c.vertex((double)x, (double)y, (double)z).color(this.r, this.g, this.b, this.a).texture(u, v).next();
            } else {
               c.vertex(l, x, y, z).color(this.r, this.g, this.b, this.a).texture(u, v).next();
            }
         }).setFormat(VertexFormats.POSITION_COLOR_TEXTURE);
      }

      public WorldVFXBuilder setPosColorTexLightmapDefaultFormat() {
         return this.setVertexSupplier(
               (c, l, x, y, z, u, v) -> {
                  if (l == null) {
                     c.vertex((double)x, (double)y, (double)z)
                        .color(this.r, this.g, this.b, this.a)
                        .texture(u, v)
                        .light(this.light)
                        .next();
                  } else {
                     c.vertex(l, x, y, z).color(this.r, this.g, this.b, this.a).texture(u, v).light(this.light).next();
                  }
               }
            )
            .setFormat(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
      }

      public WorldVFXBuilder setFormat(VertexFormat format) {
         this.format = format;
         return this;
      }

      public WorldVFXBuilder setVertexSupplier(WorldVertexPlacementSupplier supplier) {
         this.supplier = supplier;
         return this;
      }

      public WorldVFXBuilder setColorWithAlpha(Color color) {
         return this.setColor((float)color.getRed(), (float)color.getGreen(), (float)color.getBlue(), (float)color.getAlpha() / 255.0F);
      }

      public WorldVFXBuilder setColor(Color color) {
         return this.setColor((float)color.getRed(), (float)color.getGreen(), (float)color.getBlue());
      }

      public WorldVFXBuilder setColor(Color color, float a) {
         return this.setColor(color).setAlpha(a);
      }

      public WorldVFXBuilder setColor(float r, float g, float b, float a) {
         return this.setColor(r, g, b).setAlpha(a);
      }

      public WorldVFXBuilder setColor(float r, float g, float b) {
         this.r = r / 255.0F;
         this.g = g / 255.0F;
         this.b = b / 255.0F;
         return this;
      }

      public WorldVFXBuilder setAlpha(float a) {
         this.a = a;
         return this;
      }

      public WorldVFXBuilder setOffset(float xOffset, float yOffset, float zOffset) {
         this.xOffset = xOffset;
         this.yOffset = yOffset;
         this.zOffset = zOffset;
         return this;
      }

      public WorldVFXBuilder setLight(int light) {
         this.light = light;
         return this;
      }

      public WorldVFXBuilder setUV(float u0, float v0, float u1, float v1) {
         this.u0 = u0;
         this.v0 = v0;
         this.u1 = u1;
         this.v1 = v1;
         return this;
      }

      public WorldVFXBuilder renderTrail(
         VertexConsumer vertexConsumer, MatrixStack stack, List<Vector4f> trailSegments, Function<Float, Float> widthFunc
      ) {
         return this.renderTrail(vertexConsumer, stack, trailSegments, widthFunc, f -> {
         });
      }

      public WorldVFXBuilder renderTrail(
         VertexConsumer vertexConsumer, MatrixStack stack, List<Vector4f> trailSegments, Function<Float, Float> widthFunc, Consumer<Float> vfxOperator
      ) {
         return this.renderTrail(vertexConsumer, stack.peek().getPositionMatrix(), trailSegments, widthFunc, vfxOperator);
      }

      public WorldVFXBuilder renderTrail(
         VertexConsumer vertexConsumer, Matrix4f pose, List<Vector4f> trailSegments, Function<Float, Float> widthFunc, Consumer<Float> vfxOperator
      ) {
         if (trailSegments.size() < 3) {
            return this;
         } else {
            trailSegments = trailSegments.stream()
               .map(v -> new Vector4f(v.x, v.y, v.z, v.w()))
               .collect(Collectors.toList());

            for (Vector4f pos : trailSegments) {
               pos.add(this.xOffset, this.yOffset, this.zOffset, 0.0F);
               transform(pose, pos);
            }

            int count = trailSegments.size() - 1;
            float increment = 1.0F / (float)(count - 1);
            ArrayList<TrailPoint> points = new ArrayList<>();

            for (int i = 0; i < count; i++) {
               float width = widthFunc.apply(increment * (float)i);
               Vector4f start = trailSegments.get(i);
               Vector4f end = trailSegments.get(i + 1);
               points.add(new TrailPoint(RenderHelper.midpoint(start, end), RenderHelper.screenSpaceQuadOffsets(start, end, width)));
            }

            return this.renderPoints(vertexConsumer, points, this.u0, this.v0, this.u1, this.v1, vfxOperator);
         }
      }

      public static Vector4f transform(Matrix4f matrix, Vector4f vector)
      {
         float f = vector.x;
         float g = vector.y;
         float h = vector.z;
         float i = vector.w;

         vector.x =matrix.m00() * f + matrix.m01() * g + matrix.m02() * h + matrix.m03() * i;
         vector.y = matrix.m10() * f + matrix.m11() * g + matrix.m12() * h + matrix.m13() * i;
         vector.z = matrix.m20() * f + matrix.m21() * g + matrix.m22() * h + matrix.m23() * i;
         vector.w = matrix.m30() * f + matrix.m31() * g + matrix.m32() * h + matrix.m33() * i;

         return vector;
      }

      public WorldVFXBuilder renderPoints(
         VertexConsumer vertexConsumer, List<TrailPoint> trailPoints, float u0, float v0, float u1, float v1, Consumer<Float> vfxOperator
      ) {
         int count = trailPoints.size() - 1;
         float increment = 1.0F / (float)count;
         vfxOperator.accept(0.0F);
         trailPoints.get(0).renderStart(vertexConsumer, this.supplier, u0, v0, u1, MathHelper.lerp(increment, v0, v1));

         for (int i = 1; i < count; i++) {
            float current = MathHelper.lerp((float)i * increment, v0, v1);
            vfxOperator.accept(current);
            trailPoints.get(i).renderMid(vertexConsumer, this.supplier, u0, current, u1, current);
         }

         vfxOperator.accept(1.0F);
         trailPoints.get(count).renderEnd(vertexConsumer, this.supplier, u0, MathHelper.lerp((float)count * increment, v0, v1), u1, v1);
         return this;
      }

      public WorldVFXBuilder renderBeam(VertexConsumer vertexConsumer, MatrixStack stack, Vec3d start, Vec3d end, float width) {
         MinecraftClient minecraft = MinecraftClient.getInstance();
         start.add((double)this.xOffset, (double)this.yOffset, (double)this.zOffset);
         end.add((double)this.xOffset, (double)this.yOffset, (double)this.zOffset);
         stack.translate(-start.x, -start.y, -start.z);
         Vec3d cameraPosition = minecraft.getBlockEntityRenderDispatcher().camera.getPos();
         Vec3d delta = end.subtract(start);
         Vec3d normal = start.subtract(cameraPosition)
            .crossProduct(delta)
            .normalize()
            .multiply((double)(width / 2.0F), (double)(width / 2.0F), (double)(width / 2.0F));
         Matrix4f last = stack.peek().getPositionMatrix();
         Vec3d[] positions = new Vec3d[]{start.subtract(normal), start.add(normal), end.add(normal), end.subtract(normal)};
         this.supplier
            .placeVertex(vertexConsumer, last, (float)positions[0].x, (float)positions[0].y, (float)positions[0].z, this.u0, this.v1);
         this.supplier
            .placeVertex(vertexConsumer, last, (float)positions[1].x, (float)positions[1].y, (float)positions[1].z, this.u1, this.v1);
         this.supplier
            .placeVertex(vertexConsumer, last, (float)positions[2].x, (float)positions[2].y, (float)positions[2].z, this.u1, this.v0);
         this.supplier
            .placeVertex(vertexConsumer, last, (float)positions[3].x, (float)positions[3].y, (float)positions[3].z, this.u0, this.v0);
         stack.translate(start.x, start.y, start.z);
         return this;
      }

      public WorldVFXBuilder renderQuad(VertexConsumer vertexConsumer, MatrixStack stack, float size) {
         return this.renderQuad(vertexConsumer, stack, size, size);
      }

      public WorldVFXBuilder renderQuad(VertexConsumer vertexConsumer, MatrixStack stack, float width, float height) {
         Vector3f[] positions = new Vector3f[]{
            new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F)
         };
         return this.renderQuad(vertexConsumer, stack, positions, width, height);
      }

      public WorldVFXBuilder renderQuad(VertexConsumer vertexConsumer, MatrixStack stack, Vector3f[] positions, float size) {
         return this.renderQuad(vertexConsumer, stack, positions, size, size);
      }

      public WorldVFXBuilder renderQuad(VertexConsumer vertexConsumer, MatrixStack stack, Vector3f[] positions, float width, float height) {
         Matrix4f last = stack.peek().getPositionMatrix();
         stack.translate((double)this.xOffset, (double)this.yOffset, (double)this.zOffset);

         for (Vector3f position : positions) {
            position.mul(width, height, width);
         }

         this.supplier.placeVertex(vertexConsumer, last, positions[0].x(), positions[0].y(), positions[0].z(), this.u0, this.v1);
         this.supplier.placeVertex(vertexConsumer, last, positions[1].x(), positions[1].y(), positions[1].z(), this.u1, this.v1);
         this.supplier.placeVertex(vertexConsumer, last, positions[2].x(), positions[2].y(), positions[2].z(), this.u1, this.v0);
         this.supplier.placeVertex(vertexConsumer, last, positions[3].x(), positions[3].y(), positions[3].z(), this.u0, this.v0);
         stack.translate((double)(-this.xOffset), (double)(-this.yOffset), (double)(-this.zOffset));
         return this;
      }

      public WorldVFXBuilder renderScreenSpaceQuad(VertexConsumer vertexConsumer, MatrixStack stack, float size) {
         return this.renderScreenSpaceQuad(vertexConsumer, stack, size, size);
      }

      public WorldVFXBuilder renderScreenSpaceQuad(VertexConsumer vertexConsumer, MatrixStack stack, float width, float height) {
         Vector3f[] positions = new Vector3f[]{
            new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F)
         };
         return this.renderScreenSpaceQuad(vertexConsumer, stack, positions, width, height);
      }

      public WorldVFXBuilder renderScreenSpaceQuad(VertexConsumer vertexConsumer, MatrixStack stack, Vector3f[] positions, float size) {
         return this.renderScreenSpaceQuad(vertexConsumer, stack, positions, size, size);
      }

      public WorldVFXBuilder renderScreenSpaceQuad(VertexConsumer vertexConsumer, MatrixStack stack, Vector3f[] positions, float width, float height) {
         Matrix4f last = stack.peek().getPositionMatrix();
         stack.translate((double)this.xOffset, (double)this.yOffset, (double)this.zOffset);

         for (Vector3f position : positions) {
            position.mul(width, height, width);
            transform(stack.peek().getNormalMatrix(), position);
         }

         this.supplier.placeVertex(vertexConsumer, last, positions[0].x(), positions[0].y(), positions[0].z(), this.u0, this.v1);
         this.supplier.placeVertex(vertexConsumer, last, positions[1].x(), positions[1].y(), positions[1].z(), this.u1, this.v1);
         this.supplier.placeVertex(vertexConsumer, last, positions[2].x(), positions[2].y(), positions[2].z(), this.u1, this.v0);
         this.supplier.placeVertex(vertexConsumer, last, positions[3].x(), positions[3].y(), positions[3].z(), this.u0, this.v0);
         stack.translate((double)(-this.xOffset), (double)(-this.yOffset), (double)(-this.zOffset));
         return this;
      }

      public static Vector3f transform(Matrix3f matrix, Vector3f vector)
      {
         float f = vector.x;
         float g = vector.y;
         float h = vector.z;

         vector.x =matrix.m00() * f + matrix.m01() * g + matrix.m02() * h;
         vector.y = matrix.m10() * f + matrix.m11() * g + matrix.m12() * h;
         vector.z = matrix.m20() * f + matrix.m21() * g + matrix.m22() * h;

         return vector;
      }

      public WorldVFXBuilder renderSphere(VertexConsumer vertexConsumer, MatrixStack stack, float radius, int longs, int lats) {
         Matrix4f last = stack.peek().getPositionMatrix();
         float startU = 0.0F;
         float startV = 0.0F;
         float endU = (float) (Math.PI * 2);
         float endV = (float) Math.PI;
         float stepU = (endU - startU) / (float)longs;
         float stepV = (endV - startV) / (float)lats;

         for (int i = 0; i < longs; i++) {
            for (int j = 0; j < lats; j++) {
               float u = (float)i * stepU + startU;
               float v = (float)j * stepV + startV;
               float un = i + 1 == longs ? endU : (float)(i + 1) * stepU + startU;
               float vn = j + 1 == lats ? endV : (float)(j + 1) * stepV + startV;
               Vector3f p0 = RenderHelper.parametricSphere(u, v, radius);
               Vector3f p1 = RenderHelper.parametricSphere(u, vn, radius);
               Vector3f p2 = RenderHelper.parametricSphere(un, v, radius);
               Vector3f p3 = RenderHelper.parametricSphere(un, vn, radius);
               float textureU = u / endU * radius;
               float textureV = v / endV * radius;
               float textureUN = un / endU * radius;
               float textureVN = vn / endV * radius;
               RenderHelper.vertexPosColorUVLight(
                  vertexConsumer, last, p0.y(), p0.y(), p0.z(), this.r, this.g, this.b, this.a, textureU, textureV, this.light
               );
               RenderHelper.vertexPosColorUVLight(
                  vertexConsumer, last, p2.y(), p2.y(), p2.z(), this.r, this.g, this.b, this.a, textureUN, textureV, this.light
               );
               RenderHelper.vertexPosColorUVLight(
                  vertexConsumer, last, p1.y(), p1.y(), p1.z(), this.r, this.g, this.b, this.a, textureU, textureVN, this.light
               );
               RenderHelper.vertexPosColorUVLight(
                  vertexConsumer, last, p3.y(), p3.y(), p3.z(), this.r, this.g, this.b, this.a, textureUN, textureVN, this.light
               );
               RenderHelper.vertexPosColorUVLight(
                  vertexConsumer, last, p1.y(), p1.y(), p1.z(), this.r, this.g, this.b, this.a, textureU, textureVN, this.light
               );
               RenderHelper.vertexPosColorUVLight(
                  vertexConsumer, last, p2.y(), p2.y(), p2.z(), this.r, this.g, this.b, this.a, textureUN, textureV, this.light
               );
            }
         }

         return this;
      }

      public interface WorldVertexPlacementSupplier {
         void placeVertex(VertexConsumer var1, Matrix4f var2, float var3, float var4, float var5, float var6, float var7);
      }
   }
}
