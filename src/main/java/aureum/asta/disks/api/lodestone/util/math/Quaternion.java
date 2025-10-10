package aureum.asta.disks.api.lodestone.util.math;

import aureum.asta.disks.ports.amarite.mialib.util.Vec3f;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;

public final class Quaternion {
    public static final Quaternion IDENTITY = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
    private float x;
    private float y;
    private float z;
    private float w;

    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Quaternion(Vec3f axis, float rotationAngle, boolean degrees) {
        if (degrees) {
            rotationAngle *= ((float)Math.PI / 180F);
        }

        float f = sin(rotationAngle / 2.0F);
        this.x = axis.getX() * f;
        this.y = axis.getY() * f;
        this.z = axis.getZ() * f;
        this.w = cos(rotationAngle / 2.0F);
    }

    public Quaternion(float x, float y, float z, boolean degrees) {
        if (degrees) {
            x *= ((float)Math.PI / 180F);
            y *= ((float)Math.PI / 180F);
            z *= ((float)Math.PI / 180F);
        }

        float f = sin(0.5F * x);
        float g = cos(0.5F * x);
        float h = sin(0.5F * y);
        float i = cos(0.5F * y);
        float j = sin(0.5F * z);
        float k = cos(0.5F * z);
        this.x = f * i * k + g * h * j;
        this.y = g * h * k - f * i * j;
        this.z = f * h * k + g * i * j;
        this.w = g * i * k - f * h * j;
    }

    public Quaternion(Quaternion other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
    }

    public static Quaternion fromEulerYxz(float x, float y, float z) {
        Quaternion quaternion = IDENTITY.copy();
        quaternion.hamiltonProduct(new Quaternion(0.0F, (float)Math.sin((double)(x / 2.0F)), 0.0F, (float)Math.cos((double)(x / 2.0F))));
        quaternion.hamiltonProduct(new Quaternion((float)Math.sin((double)(y / 2.0F)), 0.0F, 0.0F, (float)Math.cos((double)(y / 2.0F))));
        quaternion.hamiltonProduct(new Quaternion(0.0F, 0.0F, (float)Math.sin((double)(z / 2.0F)), (float)Math.cos((double)(z / 2.0F))));
        return quaternion;
    }

    public static Quaternion fromEulerXyzDegrees(Vector3f vector) {
        return fromEulerXyz((float)Math.toRadians((double)vector.x()), (float)Math.toRadians((double)vector.y()), (float)Math.toRadians((double)vector.z()));
    }

    public static Quaternion fromEulerXyz(Vector3f vector) {
        return fromEulerXyz(vector.x(), vector.y(), vector.z());
    }

    public static Quaternion fromEulerXyz(float x, float y, float z) {
        Quaternion quaternion = IDENTITY.copy();
        quaternion.hamiltonProduct(new Quaternion((float)Math.sin((double)(x / 2.0F)), 0.0F, 0.0F, (float)Math.cos((double)(x / 2.0F))));
        quaternion.hamiltonProduct(new Quaternion(0.0F, (float)Math.sin((double)(y / 2.0F)), 0.0F, (float)Math.cos((double)(y / 2.0F))));
        quaternion.hamiltonProduct(new Quaternion(0.0F, 0.0F, (float)Math.sin((double)(z / 2.0F)), (float)Math.cos((double)(z / 2.0F))));
        return quaternion;
    }

    public Vector3f toEulerYxz() {
        float f = this.getW() * this.getW();
        float g = this.x() * this.x();
        float h = this.y() * this.y();
        float i = this.z() * this.z();
        float j = f + g + h + i;
        float k = 2.0F * this.getW() * this.x() - 2.0F * this.y() * this.z();
        float l = (float)Math.asin((double)(k / j));
        return Math.abs(k) > 0.999F * j ? new Vector3f(2.0F * (float)Math.atan2((double)this.x(), (double)this.getW()), l, 0.0F) : new Vector3f((float)Math.atan2((double)(2.0F * this.y() * this.z() + 2.0F * this.x() * this.getW()), (double)(f - g - h + i)), l, (float)Math.atan2((double)(2.0F * this.x() * this.y() + 2.0F * this.getW() * this.z()), (double)(f + g - h - i)));
    }

    public Vector3f toEulerYxzDegrees() {
        Vector3f vec3f = this.toEulerYxz();
        return new Vector3f((float)Math.toDegrees((double)vec3f.x()), (float)Math.toDegrees((double)vec3f.y()), (float)Math.toDegrees((double)vec3f.z()));
    }

    public Vector3f toEulerXyz() {
        float f = this.getW() * this.getW();
        float g = this.x() * this.x();
        float h = this.y() * this.y();
        float i = this.z() * this.z();
        float j = f + g + h + i;
        float k = 2.0F * this.getW() * this.x() - 2.0F * this.y() * this.z();
        float l = (float)Math.asin((double)(k / j));
        return Math.abs(k) > 0.999F * j ? new Vector3f(l, 2.0F * (float)Math.atan2((double)this.y(), (double)this.getW()), 0.0F) : new Vector3f(l, (float)Math.atan2((double)(2.0F * this.x() * this.z() + 2.0F * this.y() * this.getW()), (double)(f - g - h + i)), (float)Math.atan2((double)(2.0F * this.x() * this.y() + 2.0F * this.getW() * this.z()), (double)(f - g + h - i)));
    }

    public Vector3f toEulerXyzDegrees() {
        Vector3f vec3f = this.toEulerXyz();
        return new Vector3f((float)Math.toDegrees((double)vec3f.x()), (float)Math.toDegrees((double)vec3f.y()), (float)Math.toDegrees((double)vec3f.z()));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Quaternion quaternion = (Quaternion)o;
            if (Float.compare(quaternion.x, this.x) != 0) {
                return false;
            } else if (Float.compare(quaternion.y, this.y) != 0) {
                return false;
            } else if (Float.compare(quaternion.z, this.z) != 0) {
                return false;
            } else {
                return Float.compare(quaternion.w, this.w) == 0;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.x);
        i = 31 * i + Float.floatToIntBits(this.y);
        i = 31 * i + Float.floatToIntBits(this.z);
        i = 31 * i + Float.floatToIntBits(this.w);
        return i;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Quaternion[").append(this.getW()).append(" + ");
        stringBuilder.append(this.x()).append("i + ");
        stringBuilder.append(this.y()).append("j + ");
        stringBuilder.append(this.z()).append("k]");
        return stringBuilder.toString();
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public float z() {
        return this.z;
    }

    public float getW() {
        return this.w;
    }

    public void hamiltonProduct(Quaternion other) {
        float f = this.x();
        float g = this.y();
        float h = this.z();
        float i = this.getW();
        float j = other.x();
        float k = other.y();
        float l = other.z();
        float m = other.getW();
        this.x = i * j + f * m + g * l - h * k;
        this.y = i * k - f * l + g * m + h * j;
        this.z = i * l + f * k - g * j + h * m;
        this.w = i * m - f * j - g * k - h * l;
    }

    public void scale(float scale) {
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
        this.w *= scale;
    }

    public void conjugate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }

    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    private static float cos(float value) {
        return (float)Math.cos((double)value);
    }

    private static float sin(float value) {
        return (float)Math.sin((double)value);
    }

    public void normalize() {
        float f = this.x() * this.x() + this.y() * this.y() + this.z() * this.z() + this.getW() * this.getW();
        if (f > 1.0E-6F) {
            float g = (float) MathHelper.fastInverseSqrt(f);
            this.x *= g;
            this.y *= g;
            this.z *= g;
            this.w *= g;
        } else {
            this.x = 0.0F;
            this.y = 0.0F;
            this.z = 0.0F;
            this.w = 0.0F;
        }

    }

    public Quaternion copy() {
        return new Quaternion(this);
    }
}
