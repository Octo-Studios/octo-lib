package it.hurts.octostudios.octolib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class VectorUtils {

    public static final Vec3 X_VEC = new Vec3(1, 0, 0);
    public static final Vec3 Y_VEC = new Vec3(0, 1, 0);
    public static final Vec3 Z_VEC = new Vec3(0, 0, 1);

    public static void saveToNBT(String name, CompoundTag tag, Vec3 vec3) {
        CompoundTag tag1 = new CompoundTag();
        tag1.putDouble("x", vec3.x);
        tag1.putDouble("y", vec3.y);
        tag1.putDouble("z", vec3.z);
        tag.put(name, tag1);
    }

    public static Vec3 loadFromNBT(String name, CompoundTag tag) {
        CompoundTag tag1 = tag.getCompound(name);
        return new Vec3(
                tag1.getDouble("x"),
                tag1.getDouble("y"),
                tag1.getDouble("z")
        );
    }

    public static Vec3 parse(BlockPos pos) {
        return new Vec3(pos.getX(), pos.getY(), pos.getZ());
    }

    public static Vec3 parse(Entity entity) {
        return new Vec3(entity.position().x, entity.position().y, entity.position().z);
    }
    
    public static Vec3 catmullromVec(float f, Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
        return new Vec3(
                Mth.catmullrom(f, (float) v1.x, (float) v2.x, (float) v3.x, (float) v4.x),
                Mth.catmullrom(f, (float) v1.y, (float) v2.y, (float) v3.y, (float) v4.y),
                Mth.catmullrom(f, (float) v1.z, (float) v2.z, (float) v3.z, (float) v4.z)
        );
    }

    public static Vec3 rotate(Vec3 v, Vec3 axis, double angle) {
        angle = Math.toRadians(angle);
        double sinAngle = Math.sin(angle);
        double cosAngle = Math.cos(angle);
        double k = 1 - cosAngle;
        double a = axis.x;
        double b = axis.y;
        double c = axis.z;
        double m00 = a * a * k + cosAngle;
        double m01 = a * b * k - c * sinAngle;
        double m02 = a * c * k + b * sinAngle;
        double m10 = b * a * k + c * sinAngle;
        double m11 = b * b * k + cosAngle;
        double m12 = b * c * k - a * sinAngle;
        double m20 = c * a * k - b * sinAngle;
        double m21 = c * b * k + a * sinAngle;
        double m22 = c * c * k + cosAngle;
        double x = v.x * m00 + v.y * m01 + v.z * m02;
        double y = v.x * m10 + v.y * m11 + v.z * m12;
        double z = v.x * m20 + v.y * m21 + v.z * m22;
        return new Vec3(x, y, z);
    }

}
