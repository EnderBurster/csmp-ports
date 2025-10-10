package aureum.asta.disks.api.lodestone.systems.rendering;

import net.minecraft.client.gl.Uniform;

public class UniformData {
   public final String uniformName;
   public final int uniformType;

   public UniformData(String uniformName, int uniformType) {
      this.uniformName = uniformName;
      this.uniformType = uniformType;
   }

   public void setUniformValue(Uniform uniform) {
   }

   public static class FloatUniformData extends UniformData {
      public final float[] array;

      public FloatUniformData(String uniformName, int uniformType, float[] array) {
         super(uniformName, uniformType);
         this.array = array;
      }

      @Override
      public void setUniformValue(Uniform uniform) {
         if (this.uniformType <= 7) {
            uniform.setForDataType(this.array[0], this.array[1], this.array[2], this.array[3]);
         } else {
            uniform.set(this.array);
         }
      }
   }

   public static class IntegerUniformData extends UniformData {
      public final int[] array;

      public IntegerUniformData(String uniformName, int uniformType, int[] array) {
         super(uniformName, uniformType);
         this.array = array;
      }

      @Override
      public void setUniformValue(Uniform uniform) {
         uniform.setForDataType(this.array[0], this.array[1], this.array[2], this.array[3]);
      }
   }
}
