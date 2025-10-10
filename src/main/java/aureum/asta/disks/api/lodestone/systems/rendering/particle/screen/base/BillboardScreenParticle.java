package aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.base;

import aureum.asta.disks.ports.amarite.mialib.util.Vec3f;
import aureum.asta.disks.api.lodestone.util.math.Quaternion;
import org.joml.Vector3f;
import net.minecraft.world.World;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public abstract class BillboardScreenParticle extends ScreenParticle {
   protected float quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;

   protected BillboardScreenParticle(World clientWorld, double pX, double pY) {
      super(clientWorld, pX, pY);
   }

   protected BillboardScreenParticle(World clientWorld, double pX, double pY, double pXSpeed, double pYSpeed) {
      super(clientWorld, pX, pY, pXSpeed, pYSpeed);
   }

   public void rotate(Quaternion rotation, Vector3f vector) {
      Quaternion quaternion = new Quaternion(rotation);
      quaternion.hamiltonProduct(new Quaternion(vector.x(), vector.y(), vector.z(), 0.0F));
      Quaternion quaternion2 = new Quaternion(rotation);
      quaternion2.conjugate();
      quaternion.hamiltonProduct(quaternion2);
      vector.set(quaternion.x(), quaternion.y(), quaternion.z());
   }

   @Override
   public void render(BufferBuilder bufferBuilder) {
      MinecraftClient client = MinecraftClient.getInstance();
      float tickDelta = client.getTickDelta();
      float size = this.getQuadSize(tickDelta) * 10.0F;
      float u0 = this.getMinU();
      float u1 = this.getMaxU();
      float v0 = this.getMinV();
      float v1 = this.getMaxV();
      float roll = MathHelper.lerp(tickDelta, this.prevAngle, this.angle);
      Vector3f[] vectors = new Vector3f[]{
         new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
      };
      Quaternion rotation = new Quaternion(new Vec3f(0.0f, 0.0f, 1.0f), roll, false);
      //class_1158 rotation = Vector3f.POSITIVE_Z.method_23626(roll);

      for (int i = 0; i < 4; i++) {
         Vector3f vector3f = vectors[i];
         rotate(rotation, vector3f);
         vector3f.mul(size);
         vector3f.add((float)this.x, (float)this.y, 0.0F);
      }

      int z = 390;
      bufferBuilder.vertex((double)vectors[0].x(), (double)vectors[0].y(), (double)z)
         .texture(u1, v1)
         .color(this.red, this.green, this.blue, this.alpha)
         .light(15728880)
         .next();
      bufferBuilder.vertex((double)vectors[1].x(), (double)vectors[1].y(), (double)z)
         .texture(u1, v0)
         .color(this.red, this.green, this.blue, this.alpha)
         .light(15728880)
         .next();
      bufferBuilder.vertex((double)vectors[2].x(), (double)vectors[2].y(), (double)z)
         .texture(u0, v0)
         .color(this.red, this.green, this.blue, this.alpha)
         .light(15728880)
         .next();
      bufferBuilder.vertex((double)vectors[3].x(), (double)vectors[3].y(), (double)z)
         .texture(u0, v1)
         .color(this.red, this.green, this.blue, this.alpha)
         .light(15728880)
         .next();
   }

   public float getQuadSize(float tickDelta) {
      return this.quadSize;
   }

   protected abstract float getMinU();

   protected abstract float getMaxU();

   protected abstract float getMinV();

   protected abstract float getMaxV();
}
