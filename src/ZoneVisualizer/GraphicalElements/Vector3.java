package ZoneVisualizer.GraphicalElements;

import ZoneVisualizer.Utility.Utility;

import java.util.stream.Stream;

public class Vector3 {

    public static Vector3 right() { return new Vector3(1, 0, 0); }
    public static Vector3 up() { return new Vector3(0, -1, 0); }
    public static Vector3 forward() { return new Vector3(0, 0, 1); }
    public static Vector3 left() { return new Vector3(-1, 0, 0); }
    public static Vector3 down() { return new Vector3(0, 1, 0); }
    public static Vector3 back() { return new Vector3(0, 0, -1); }
    public static Vector3 zero() { return new Vector3(0, 0, 0); }
    public static Vector3 one() { return new Vector3(1, 1, 1); }

    public double x, y, z;

    public Vector3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getMagnitude(){
        return Math.sqrt(x*x + y*y + z*z);
    }

    public Vector3 getNormalized() {
        double magnitude = getMagnitude();
        return new Vector3(x / magnitude, y / magnitude, z / magnitude);
    }

    public Vector3 plus(Vector3 other) {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }

    public Vector3 minus(Vector3 other) {
        return new Vector3(x - other.x, y - other.y, z - other.z);
    }

    public Vector3 multiply(double factor) {
        return new Vector3(x * factor, y * factor, z * factor);
    }

    public Vector3 divide(double dividend) {
        return new Vector3(x / dividend, y / dividend, z / dividend);
    }

    public double dot(Vector3 other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vector3 crossProduct(Vector3 v) {
        double uvx, uvy, uvz;
        uvx = this.y * v.z - v.y * this.z;
        uvy = v.x * this.z - this.x * v.z;
        uvz = this.x * v.y - v.x * this.y;

        return new Vector3(uvx, uvy, uvz);
    }

    public double max() {
        double maximum = x > y ? x : y;
        return maximum < z ? z : maximum;
    }

    public Stream<Double> asStream() {
        return Stream.of(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if(super.equals(obj)) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Vector3 other = (Vector3)obj;
        if (Utility.almostEqualRelative(x, other.x) &&
            Utility.almostEqualRelative(y, other.y) &&
            Utility.almostEqualRelative(z, other.z)) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(x) ^ Double.hashCode(y) << 2 ^ Double.hashCode(z) >> 2;
    }

    @Override
    public String toString() {
        return String.format("(%g, %g, %g)", x, y, z);
    }

    @Override
    protected Object clone() {
        return new Vector3(x, y, z);
    }

    public Vector3 copy() {
        return new Vector3(x, y, z);
    }
}
