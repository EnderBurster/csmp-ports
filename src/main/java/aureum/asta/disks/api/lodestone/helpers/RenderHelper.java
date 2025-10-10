package aureum.asta.disks.api.lodestone.helpers;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Optional;
import java.util.function.Supplier;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.Vec2f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.RenderLayer.MultiPhase;

public final class RenderHelper {
   public static final int FULL_BRIGHT = 15728880;

   public static ShaderProgram getShader(RenderLayer type) {
      if (type instanceof MultiPhase compositeRenderType) {
         Optional<Supplier<ShaderProgram>> shader = compositeRenderType.phases.program.supplier;
         if (shader.isPresent()) {
            return shader.get().get();
         }
      }

      return null;
   }

   public static void vertexPos(VertexConsumer vertexConsumer, Matrix4f last, float x, float y, float z) {
      vertexConsumer.vertex(last, x, y, z).next();
   }

   public static void vertexPosUV(VertexConsumer vertexConsumer, Matrix4f last, float x, float y, float z, float u, float v) {
      vertexConsumer.vertex(last, x, y, z).texture(u, v).next();
   }

   public static void vertexPosUVLight(VertexConsumer vertexConsumer, Matrix4f last, float x, float y, float z, float u, float v, int light) {
      vertexConsumer.vertex(last, x, y, z).texture(u, v).light(light).next();
   }

   public static void vertexPosColor(VertexConsumer vertexConsumer, Matrix4f last, float x, float y, float z, float r, float g, float b, float a) {
      vertexConsumer.vertex(last, x, y, z).color(r, g, b, a).next();
   }

   public static void vertexPosColorUV(
      VertexConsumer vertexConsumer, Matrix4f last, float x, float y, float z, float r, float g, float b, float a, float u, float v
   ) {
      vertexConsumer.vertex(last, x, y, z).color(r, g, b, a).texture(u, v).next();
   }

   public static void vertexPosColorUVLight(
      VertexConsumer vertexConsumer, Matrix4f last, float x, float y, float z, float r, float g, float b, float a, float u, float v, int light
   ) {
      vertexConsumer.vertex(last, x, y, z).color(r, g, b, a).texture(u, v).light(light).next();
   }

   public static Vector3f parametricSphere(float u, float v, float r) {
      return new Vector3f(
         MathHelper.cos(u) * MathHelper.sin(v) * r,
         MathHelper.cos(v) * r,
         MathHelper.sin(u) * MathHelper.sin(v) * r
      );
   }

   public static Vec2f screenSpaceQuadOffsets(Vector4f start, Vector4f end, float width) {
      float x = -start.x;
      float y = -start.y;
      if (Math.abs(start.z) > 0.0F) {
         float ratio = end.z / start.z;
         x = end.x + x * ratio;
         y = end.y + y * ratio;
      } else if (Math.abs(end.z) <= 0.0F) {
         x += end.x;
         y += end.y;
      }

      if (start.z > 0.0F) {
         x = -x;
         y = -y;
      }

      if (x * x + y * y > 0.0F) {
         float normalize = width * 0.5F / DataHelper.distance(x, y);
         x *= normalize;
         y *= normalize;
      }

      return new Vec2f(-y, x);
   }

   public static Vector4f midpoint(Vector4f a, Vector4f b) {
      return new Vector4f(
         (a.x + b.x) * 0.5F,
         (a.y + b.y) * 0.5F,
         (a.z + b.z) * 0.5F,
         (a.w() + b.w()) * 0.5F
      );
   }

   public static Vec2f worldPosToTexCoord(Vector3f worldPos, MatrixStack viewModelStack) {
      Matrix4f viewMat = viewModelStack.peek().getPositionMatrix();
      Matrix4f projMat = RenderSystem.getProjectionMatrix();
      Vector3f localPos = new Vector3f(worldPos.x, worldPos.y, worldPos.z);
      localPos.sub(new Vector3f((float) MinecraftClient.getInstance().gameRenderer.getCamera().getPos().getX(), (float) MinecraftClient.getInstance().gameRenderer.getCamera().getPos().getY(), (float) MinecraftClient.getInstance().gameRenderer.getCamera().getPos().getZ()));
      Vector4f pos = new Vector4f(localPos, 1.0F);
      transform(viewMat, pos);
      transform(projMat, pos);
      normalizeProjectiveCoordinates(pos);
      return new Vec2f((pos.x + 1.0F) / 2.0F, (pos.y + 1.0F) / 2.0F);
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

   public static Vector4f normalizeProjectiveCoordinates(Vector4f vector) {
      vector.x /= vector.w;
      vector.y /= vector.w;
      vector.z /= vector.w;
      vector.w = 1.0F;

      return vector;
   }
}
