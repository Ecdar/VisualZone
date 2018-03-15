package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.Constraints.Inequality;
import ZoneVisualizer.Constraints.SingleClockConstraint;
import ZoneVisualizer.Utility.LINQ;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class Zone {

    private double[][] vertices;
    private Face[] faces;

    public Zone(Collection<Constraint> constraints, Collection<Clock> clocks) {
        ArrayList<double[]> verts = new ArrayList<>();
        Map<Clock, Double> minBounds = clocks.stream().collect(Collectors.toMap(c -> c, c -> Double.NEGATIVE_INFINITY));
        Map<Clock, Boolean> minBoundsInclusive = clocks.stream().collect(Collectors.toMap(c -> c, c -> true));
        Map<Clock, Double> maxBounds = clocks.stream().collect(Collectors.toMap(c -> c, c -> Double.POSITIVE_INFINITY));
        Map<Clock, Boolean> maxBoundsInclusive = clocks.stream().collect(Collectors.toMap(c -> c, c -> true));

        for (SingleClockConstraint constraint : LINQ.<Constraint, SingleClockConstraint>ofType(constraints)) {
            if (constraint.getInequality() == Inequality.SmallerThan
                    || constraint.getInequality() == Inequality.SmallerThanEqual) {

            }
            else if (constraint.getInequality() == Inequality.GreaterThan
                    || constraint.getInequality() == Inequality.GreaterThanEqual) {
                if (constraint.getnValue() >= minBounds.get(constraint.getClock())) {


                    if (constraint.getInequality() == Inequality.GreaterThanEqual) {
                        minBoundsInclusive.put(constraint.getClock(), true);
                    }
                    else {
                        //todo Shouldn't always do this
                        minBoundsInclusive.put(constraint.getClock(), false);
                    }
                }
            }
        }

        vertices = verts.toArray(vertices);
    }

    private class Face {
        private int[] verticeIndexes;
    }
}
