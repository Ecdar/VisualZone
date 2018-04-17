package ZoneVisualizer.Utility;

import ZoneVisualizer.GraphicalElements.Vector3;

import java.util.Comparator;

public class PointSortByAngleIn3D implements Comparator<Vector3> {

    private final Vector3 normal;
    private final Vector3 angleReference;
    private final Vector3 partitionVector;

    public PointSortByAngleIn3D(Vector3 normal, Vector3 pointReference) {
        this.normal = normal;

        angleReference = pointReference;
        partitionVector = angleReference.crossProduct(normal);
    }

    @Override
    public int compare(Vector3 v1, Vector3 v2) {
        double dotV1 = v1.dot(partitionVector),
               dotV2 = v2.dot(partitionVector);
        if (dotV1 >= 0) {
            if (dotV2 < 0) {
                return 1;
            }
            if (dotV1 == 0 && dotV2 == 0) {
                return doubleGreaterThanZeroComp(v1.dot(angleReference), v2.dot(angleReference));
            }
        }
        else {
            if (dotV2 >= 0) {
                return -1;
            }
        }
        Vector3 crossProduct = v1.crossProduct(v2);
        if (Double.isNaN(crossProduct.x)) {
            crossProduct.x = 0;
        }
        if (Double.isNaN(crossProduct.y)) {
            crossProduct.y = 0;
        }
        if (Double.isNaN(crossProduct.z)) {
            crossProduct.z = 0;
        }
        return crossProduct.dot(normal) < 0 ? 1 : -1;
    }

    private int doubleGreaterThanZeroComp(double dotRefV1, double dotRefV2) {
        if (dotRefV1 > 0) {
            if (dotRefV2 > 0) {
                return 0;
            }
            else {
                return 1;
            }
        }
        else {
            if (dotRefV2 > 0) {
                return -1;
            }
            else {
                return 0;
            }
        }
    }
}
