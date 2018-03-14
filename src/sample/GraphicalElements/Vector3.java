package sample.GraphicalElements;

import sample.Utility.Utility;

public class Vector3 {

    public static final Vector3 right = new Vector3(1, 0, 0),
                                up = new Vector3(0, -1, 0),
                                forward = new Vector3(0, 0, 1),
                                left = new Vector3(-1, 0, 0),
                                down = new Vector3(0, 1, 0),
                                back = new Vector3(0, 0, -1),
                                zero = new Vector3(0, 0, 0),
                                one = new Vector3(1, 1, 1);

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
    public String toString() {
        return String.format("(%g, %g, %g)", x, y, z);
    }

    @Override
    protected Object clone() {
        return new Vector3(x, y, z);
    }
}
