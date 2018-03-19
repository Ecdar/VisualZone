package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.*;
import ZoneVisualizer.Utility.LINQ;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Zone {

    private double[][] vertices;
    private Face[] faces;

    public Zone(Collection<Constraint> constraints, Collection<Clock> clocks) {
        ArrayList<double[]> verts = new ArrayList<>();
        Map<Clock, Double> minBounds = clocks.stream().collect(Collectors.toMap(c -> c, c -> 0d));
        Map<Clock, Boolean> minBoundsInclusive = clocks.stream().collect(Collectors.toMap(c -> c, c -> true));
        Map<Clock, Double> maxBounds = clocks.stream().collect(Collectors.toMap(c -> c, c -> Double.POSITIVE_INFINITY));
        Map<Clock, Boolean> maxBoundsInclusive = clocks.stream().collect(Collectors.toMap(c -> c, c -> true));

        calculateBoxZone(LINQ.ofType(constraints), minBounds, minBoundsInclusive, maxBounds, maxBoundsInclusive);

        checkForEmptyZone(clocks, minBounds, minBoundsInclusive, maxBounds, maxBoundsInclusive);

        cutZoneCorners(LINQ.ofType(constraints), minBounds, minBoundsInclusive, maxBounds, maxBoundsInclusive);

        vertices = verts.toArray(vertices);
    }

    //Calculates the zone using only the single dimensional constraints.
    //In other words the zone without taking "tilted" planes into account
    private void calculateBoxZone(Collection<SingleClockConstraint> constraints,
                                  Map<Clock, Double> minBounds, Map<Clock, Boolean> minBoundsInclusive,
                                  Map<Clock, Double> maxBounds, Map<Clock, Boolean> maxBoundsInclusive) {
        for (SingleClockConstraint constraint : constraints) {
            Inequality inequality = constraint.getInequality();
            double nValue = constraint.getnValue();
            Clock clock = constraint.getClock();
            if (inequality == Inequality.SmallerThan
                    || inequality == Inequality.SmallerThanEqual) {
                Double clockMax = maxBounds.get(clock);
                if (nValue < clockMax) {
                    maxBounds.put(clock, nValue);

                    maxBoundsInclusive.put(clock, inequality == Inequality.SmallerThanEqual);
                }
                else if (nValue == clockMax
                        && inequality == Inequality.SmallerThanEqual) {
                    maxBoundsInclusive.put(clock, true);
                }
            }
            else if (inequality == Inequality.GreaterThan
                    || inequality == Inequality.GreaterThanEqual) {
                Double clockMin = minBounds.get(clock);
                if (nValue > clockMin) {
                    minBounds.put(clock, nValue);

                    minBoundsInclusive.put(clock, inequality == Inequality.GreaterThanEqual);
                }
                else if (nValue == clockMin
                        && inequality == Inequality.GreaterThanEqual) {
                    minBoundsInclusive.put(clock, true);
                }
            }
        }
    }

    //Checks if the zone is empty (could again some performance by doing this check earlier)
    private void checkForEmptyZone(Collection<Clock> clocks,
                                   Map<Clock, Double> minBounds, Map<Clock, Boolean> minBoundsInclusive,
                                   Map<Clock, Double> maxBounds, Map<Clock, Boolean> maxBoundsInclusive) {
        for (Clock clock : clocks) {
            if (minBounds.get(clock) > maxBounds.get(clock)
                    || (minBounds.get(clock) == maxBounds.get(clock)
                    && (!minBoundsInclusive.get(clock) || !maxBoundsInclusive.get(clock)))) {
                //Empty zone
                minBounds.clear();
                maxBounds.clear();
            }
        }
    }

    private void cutZoneCorners(Collection<TwoClockConstraint> constraints,
                                Map<Clock, Double> minBounds, Map<Clock, Boolean> minBoundsInclusive,
                                Map<Clock, Double> maxBounds, Map<Clock, Boolean> maxBoundsInclusive) {

    }

    private class Face {
        private int[] verticeIndexes;
    }
}
