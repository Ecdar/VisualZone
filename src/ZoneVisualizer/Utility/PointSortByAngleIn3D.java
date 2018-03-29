package ZoneVisualizer.Utility;

import ZoneVisualizer.GraphicalElements.Vector3;

import java.util.Comparator;

public class PointSortByAngleIn3D implements Comparator<Vector3> {

    private final Vector3 center;
    private final Vector3 normal;
    private final Vector3 angleReference;
    private final Vector3 partitionVector;

    public PointSortByAngleIn3D(Vector3 center, Vector3 normal, Vector3 pointReference) {
        this.center = center;
        this.normal = normal;

        angleReference = pointReference.minus(center);
        partitionVector = angleReference.crossProduct(normal);
    }

    @Override
    public int compare(Vector3 v1, Vector3 v2) {
        Vector3 u1 = v1.minus(center),
                u2 = v2.minus(center);
        double dotV1 = u1.dot(partitionVector),
               dotV2 = u2.dot(partitionVector),
               dotRefV1, dotRefV2;
        int quadrant;
        if (dotV1 >= 0) {
            if (dotV2 < 0) {
                return 1;
            }
            dotRefV1 = u1.dot(angleReference);
            dotRefV2 = u1.dot(angleReference);
            if (dotRefV1 < 0) {
                if (dotRefV2 >= 0) {
                    return 1;
                }
                quadrant = 1;
            }
            else {
                if (dotRefV2 < 0) {
                    return -1;
                }
                quadrant = 2;
            }
        }
        else {
            if (dotV2 >= 0) {
                return -1;
            }
            dotRefV1 = u1.dot(angleReference);
            dotRefV2 = u1.dot(angleReference);
            if (dotRefV1 < 0) {
                if (dotRefV2 >= 0) {
                    return 1;
                }
                quadrant = 3;
            }
            else {
                if (dotRefV2 < 0) {
                    return -1;
                }
                quadrant = 4;
            }
        }
        switch (quadrant) {
            case 1:
            case 2:
                return Double.compare(dotRefV1, dotRefV2);
            case 3:
            case 4:
                return -Double.compare(dotRefV1, dotRefV2);
            default:
                return 0;
        }
    }
}
