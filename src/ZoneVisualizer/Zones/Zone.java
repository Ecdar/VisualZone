package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.*;
import ZoneVisualizer.Utility.LINQ;

import java.util.*;
import java.util.stream.Collectors;

public class Zone {

    private double[][] vertices;
    private Face[] faces;

    public Zone(Collection<Constraint> constraints, Collection<Clock> clocks) {
        ArrayList<double[]> verts = new ArrayList<>();

        reduceConstraints(constraints);

        vertices = verts.toArray(vertices);
    }

    //Removes any (trivial) redundant constraints and also checks for (trivial) emptiness
    //Returns true if this zone is empty
    private boolean reduceConstraints(Collection<Constraint> constraints) {
        Map<Clock, List<SingleClockConstraint>> singleClockConstraints =
                LINQ.<Constraint, SingleClockConstraint>ofType(constraints).stream()
                        .collect(Collectors.groupingBy((SingleClockConstraint c) -> c.getClock()));

        for (Map.Entry<Clock, List<SingleClockConstraint>> constraintsOfClock : singleClockConstraints.entrySet()) {
            List<SingleClockConstraint> sortedConstraints = constraintsOfClock.getValue().stream()
                    .sorted(this::singleClockConstraintComparator)
                    .collect(Collectors.toList());
            SingleClockConstraint max = sortedConstraints.get(0);
            SingleClockConstraint min = sortedConstraints.get(sortedConstraints.size() - 1);

            if (min.getnValue() > max.getnValue()
                    || (min.getnValue() == max.getnValue()
                        && (!(min.getInequality() == Inequality.GreaterThanEqual)
                            || !(max.getInequality() == Inequality.SmallerThanEqual)))) {
                return true;
            }

            sortedConstraints.remove(max);
            sortedConstraints.remove(min);
            for (SingleClockConstraint c : sortedConstraints) {
                constraints.remove(c);
            }
        }

        //todo handle two clock constraints

        return false;
    }

    //Sorts constraints so max bounds are first, and min bounds last
    //Some trickery as max bounds are smallest values first and min bounds are largest values first
    private int singleClockConstraintComparator(SingleClockConstraint c1, SingleClockConstraint c2) {
        int ret = Double.compare(c1.getnValue(), c2.getnValue());
        if (c1.getInequality() == Inequality.GreaterThan
                || c1.getInequality() == Inequality.GreaterThanEqual) {
            if (c2.getInequality() == Inequality.GreaterThan
                    || c2.getInequality() == Inequality.GreaterThanEqual) {
                if (ret != 0) {
                    return -ret;
                }
                if (c1.getInequality() == c2.getInequality()) {
                    return 0;
                }
                if (c1.getInequality() == Inequality.GreaterThan) {
                    return -1;
                }
                return 1;
            }
            else {
                return -1;
            }
        }
        else {
            if (c2.getInequality() == Inequality.SmallerThan
                    || c2.getInequality() == Inequality.SmallerThanEqual) {
                if (ret != 0) {
                    return -ret;
                }
                if (c1.getInequality() == c2.getInequality()) {
                    return 0;
                }
                if (c1.getInequality() == Inequality.SmallerThan) {
                    return 1;
                }
                return -1;
            }
            else {
                return 1;
            }
        }
    }

    private class Face {
        private int[] verticeIndexes;
    }
}
