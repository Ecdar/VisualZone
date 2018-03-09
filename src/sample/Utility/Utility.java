package sample.Utility;

public class Utility {
    public static boolean almostEqualrelative(float a, float b) {
        if (a == b) {
            return true;
        }
        float greater = (a > b) ? a : b;
        float ulp = Math.ulp(greater);

        return Math.abs(a - b) < ulp;
    }

    public static boolean almostEqualrelative(double a, double b) {
        if (a == b) {
            return true;
        }
        double greater = (a > b) ? a : b;
        double ulp = Math.ulp(greater);

        return Math.abs(a - b) < ulp;
    }
}
