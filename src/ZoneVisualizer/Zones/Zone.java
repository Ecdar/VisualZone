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
        Collection<Constraint> tempConstraints = new ArrayList<>(constraints);

        reduceConstraints(tempConstraints);

        vertices = verts.toArray(vertices);
    }

    //Removes any (trivial) redundant constraints and also checks for (trivial) emptiness
    //Returns true if this zone is empty
    private boolean reduceConstraints(Collection<Constraint> constraints) {
        if (reduceSingleClockConstraints(constraints)) return true;

        List<TwoClockConstraint> twoClockConstraints =
                new ArrayList<>(LINQ.<Constraint, TwoClockConstraint>ofType(constraints));
        //Invert greater than constraints
        for (TwoClockConstraint c : twoClockConstraints) {
            if (c.getInequalityAsInt() > 1) {
                constraints.remove(c);
                twoClockConstraints.remove(c);
                TwoClockConstraint inverted = c.getInvertedConstraint();
                constraints.add(inverted);
                twoClockConstraints.add(inverted);
            }
        }
        Map<Clock, List<TwoClockConstraint>> clockToTwoClockConstraintsMap =
                twoClockConstraints.stream().collect(Collectors.groupingBy(TwoClockConstraint::getClock1));

        for (Map.Entry<Clock, List<TwoClockConstraint>> constraintsOfClock : clockToTwoClockConstraintsMap.entrySet()) {
            List<TwoClockConstraint> constraintsToRemove = new ArrayList<>(constraintsOfClock.getValue());
            TwoClockConstraint max = constraintsToRemove.stream().min((c1, c2) -> {
                int ret = Double.compare(c1.getnValue(), c2.getnValue());
                if (ret != 0) {
                    return ret;
                }
                if (c1.getInequality() == c2.getInequality()) {
                    return 0;
                }
                if (c1.getInequality() == Inequality.SmallerThan) {
                    return -1;
                }
                return 1;
            }).get();
            constraintsToRemove.remove(max);
            for (TwoClockConstraint c : constraintsToRemove) {
                constraintsOfClock.getValue().remove(c);
                constraints.remove(c);
            }
        }
        //todo check for emptiness

        //todo compare two clock constraints to single clock constraints

        return false;
    }

    private boolean reduceSingleClockConstraints(Collection<Constraint> constraints) {
        List<SingleClockConstraint> singleClockConstraints =
                new ArrayList<>(LINQ.<Constraint, SingleClockConstraint>ofType(constraints));
        Map<Clock, List<SingleClockConstraint>> clockToConstraintsMap =
                singleClockConstraints.stream()
                        .collect(Collectors.groupingBy(SingleClockConstraint::getClock));

        for (Map.Entry<Clock, List<SingleClockConstraint>> constraintsOfClock : clockToConstraintsMap.entrySet()) {
            List<SingleClockConstraint> sortedConstraints = constraintsOfClock.getValue().stream()
                    .sorted(this::singleClockConstraintComparator)
                    .collect(Collectors.toList());
            SingleClockConstraint max = sortedConstraints.get(0);
            if (max.getInequalityAsInt() < 2) {
                max = null;
            }
            SingleClockConstraint min = sortedConstraints.get(sortedConstraints.size() - 1);
            if (min.getInequalityAsInt() > 1) {
                min = null;
            }

            if (min != null && max != null
                    && (min.getnValue() > max.getnValue()
                        || (min.getnValue() == max.getnValue()
                            && (!(min.getInequality() == Inequality.GreaterThanEqual)
                                || !(max.getInequality() == Inequality.SmallerThanEqual))))) {
                return true;
            }

            if (max != null) {
                sortedConstraints.remove(max);
            }
            if (min != null) {
                sortedConstraints.remove(min);
            }
            constraints.removeAll(sortedConstraints);
        }
        return false;
    }

    //Sorts constraints so max bounds are first, and min bounds last
    //Some trickery as max bounds are smallest values first and min bounds are largest values first
    private int singleClockConstraintComparator(SingleClockConstraint c1, SingleClockConstraint c2) {
        int ret = Double.compare(c1.getnValue(), c2.getnValue());
        if (c1.getInequalityAsInt() < 2) {
            if (c2.getInequalityAsInt() < 2) {
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
            if (c2.getInequalityAsInt() > 1) {
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
