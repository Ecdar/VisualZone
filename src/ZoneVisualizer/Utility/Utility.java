package ZoneVisualizer.Utility;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.Constraints.SingleClockConstraint;
import ZoneVisualizer.GraphicalElements.Vector3;
import ZoneVisualizer.Zones.Vertex;

import java.util.Optional;

public class Utility {
    public static boolean almostEqualRelative(float a, float b) {
        if (a == b) {
            return true;
        }
        float greater = (a > b) ? a : b;
        float ulp = Math.ulp(greater);

        return Math.abs(a - b) < ulp;
    }

    public static boolean almostEqualRelative(double a, double b) {
        if (a == b) {
            return true;
        }
        double greater = (a > b) ? a : b;
        double ulp = Math.ulp(greater);

        return Math.abs(a - b) < ulp;
    }

    public static boolean isVertexInfinite(Vertex v, Clock dimension) {
        Optional<Constraint> scc = v.getConstraints(dimension).stream().filter(c -> c instanceof SingleClockConstraint).findFirst();
        if (!scc.isPresent()) {
            return false;
        }
        return !Double.isFinite(scc.get().getnValue());
    }
}
