package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.*;

import java.util.*;
import java.util.stream.Collectors;

public class ConstraintZone {

    private final Map<Clock, SingleClockConstraint> minBoundConstraints = new HashMap<>();
    private final Map<Clock, SingleClockConstraint> maxBoundConstraints = new HashMap<>();
    private final Map<Clock, Map<Clock, TwoClockConstraint>> twoClockConstraints = new HashMap<>();
    private final Map<Clock, Map<Clock, TwoClockConstraint>> twoClockConstraintsBySecondary = new HashMap<>();
    private boolean restrictedToEmptiness = false;

    public ConstraintZone(Collection<Constraint> constraints) {
        for (Constraint constraint : constraints) {
            if (constraint instanceof SingleClockConstraint) {
                if (addSingleClockConstraint((SingleClockConstraint)constraint)) return;
            }
            else if (constraint instanceof TwoClockConstraint) {
                if (addTwoClockConstraint((TwoClockConstraint)constraint)) return;
            }
        }

        checkTwoClockAgainstOneClock();
        twoClockConstraintsBySecondary.putAll(twoClockConstraints.values().stream()
                .flatMap(map -> map.values().stream())
                .collect(Collectors.groupingBy((TwoClockConstraint tcc) -> tcc.getClock2()))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        kvp -> kvp.getKey(),
                        kvp -> kvp.getValue().stream()
                                .collect(Collectors.toMap(tcc -> tcc.getClock1(), tcc -> tcc)))));
    }

    private boolean addSingleClockConstraint(SingleClockConstraint constraint) {
        if (constraint.getInequality() == Inequality.GreaterThan) {
            if (checkEmptiness(constraint, maxBoundConstraints.get(constraint.getClock()))) {
                restrictedToEmptiness = true;
                return true;
            }
            tryAddMinBound(constraint);
        }
        else {
            if (checkEmptiness(minBoundConstraints.get(constraint.getClock()), constraint)) {
                restrictedToEmptiness = true;
                return true;
            }
            tryAddMaxBound(constraint);
        }
        return false;
    }

    private boolean addTwoClockConstraint(TwoClockConstraint tcConstraint) {
        //Invert greater than constraints
        if (tcConstraint.getInequality() == Inequality.GreaterThan) {
            tcConstraint = tcConstraint.getInvertedConstraint();
        }
        if (checkEmptiness(tcConstraint)) {
            restrictedToEmptiness = true;
            return true;
        }
        tryAddTwoClockBound(tcConstraint);
        return false;
    }

    private void checkTwoClockAgainstOneClock() {
        Collection<TwoClockConstraint> tempTCConstraints = getTCConstraints();
        for (TwoClockConstraint tcConstraint : tempTCConstraints) {
            //Remember all of these can be null!
            SingleClockConstraint
                    xMin = minBoundConstraints.get(tcConstraint.getClock1()),
                    xMax = maxBoundConstraints.get(tcConstraint.getClock1()),
                    yMin = minBoundConstraints.get(tcConstraint.getClock2()),
                    yMax = maxBoundConstraints.get(tcConstraint.getClock2());
            if (xMin != null && yMax != null && checkConstraintLessThan(tcConstraint, xMin, yMax)) {
                //Empty case
                restrictedToEmptiness = true;
                return;
            }
            if (xMax != null && yMin != null && checkConstraintGreaterThan(tcConstraint, xMax, yMin)) {
                //Redundant case
                twoClockConstraints.remove(tcConstraint.getClock1());
                continue;
            }
            cutZoneCorner(tcConstraint, xMin, xMax, yMin, yMax);
        }
    }

    private void cutZoneCorner(TwoClockConstraint tcConstraint, SingleClockConstraint xMin, SingleClockConstraint xMax, SingleClockConstraint yMin, SingleClockConstraint yMax) {
        boolean nGreaterThanMaxMax = xMax != null &&
                    (yMax == null || checkConstraintGreaterThan(tcConstraint, xMax, yMax)),
                nGreaterThanMinMin = xMin == null ||
                    (yMin != null && checkConstraintGreaterThan(tcConstraint, xMin, yMin)),
                nLessThanMaxMax = xMax == null ||
                    (yMax != null && checkConstraintLessThan(tcConstraint, xMax, yMax)),
                nLessThanMinMin = xMin != null &&
                    (yMin == null || checkConstraintLessThan(tcConstraint, xMin, yMin));

        if (nLessThanMinMin && nLessThanMaxMax) {
            //Cuts of bottom and right side
            if (xMax != null) {
                maxBoundConstraints.remove(xMax.getClock());
            }
            if (yMin != null) {
                minBoundConstraints.remove(yMin.getClock());
            }
            tcConstraint.setRestrictionType(TwoClockRestrictionType.CutOfBottomAndRightSide);
        }
        else if (nGreaterThanMinMin && nLessThanMaxMax) {
            //Cuts of right side
            if (xMax != null) {
                maxBoundConstraints.remove(xMax.getClock());
            }
            tcConstraint.setRestrictionType(TwoClockRestrictionType.CutOfRightSide);
        }
        else if (nLessThanMinMin && nGreaterThanMaxMax) {
            //Cuts of bottom
            if (yMin != null) {
                minBoundConstraints.remove(yMin.getClock());
            }
            tcConstraint.setRestrictionType(TwoClockRestrictionType.CutOfBottom);
        }
        else {
            tcConstraint.setRestrictionType(TwoClockRestrictionType.CutOfNothing);
        }
    }

    //Adds the given constraint to list of minimum bounds if there isn't one or if it is higher than the old bound
    private void tryAddMinBound(SingleClockConstraint constraint) {
        SingleClockConstraint old = minBoundConstraints.get(constraint.getClock());
        if (old == null || constraint.getnValue() > old.getnValue() ||
                (constraint.getnValue() == old.getnValue() && !constraint.isInclusive())) {
            minBoundConstraints.put(constraint.getClock(), constraint);
        }
    }

    //Adds the given constraint to list of maximum bounds if there isn't one or if it is lower than the old bound
    private void tryAddMaxBound(SingleClockConstraint constraint) {
        SingleClockConstraint old = maxBoundConstraints.get(constraint.getClock());
        if (old == null || constraint.getnValue() < old.getnValue() ||
                (constraint.getnValue() == old.getnValue() && !constraint.isInclusive())) {
            maxBoundConstraints.put(constraint.getClock(), constraint);
        }
    }

    //Adds the given constraint to list of two clock bounds if there isn't one or
    //if it is more restrictive than the old bound
    private void tryAddTwoClockBound(TwoClockConstraint constraint) {
        Map<Clock, TwoClockConstraint> mapByFirstClock = twoClockConstraints.get(constraint.getClock1());
        if (mapByFirstClock == null) {
            mapByFirstClock = new HashMap<>();
            twoClockConstraints.put(constraint.getClock1(), mapByFirstClock);
        }
        TwoClockConstraint old = mapByFirstClock.get(constraint.getClock2());
        if (old == null || constraint.getnValue() < old.getnValue() ||
                (constraint.getnValue() == old.getnValue() && !constraint.isInclusive())) {
            mapByFirstClock.put(constraint.getClock2(), constraint);
        }
    }

    //Returns true if the given constraints (on the same clock) restrict us to the empty space
    private boolean checkEmptiness(SingleClockConstraint min, SingleClockConstraint max) {
        if (min == null || max == null) {
            return false;
        }
        if (min.getnValue() < max.getnValue()) {
            return false;
        }
        if (min.getnValue() > max.getnValue()) {
            return true;
        }
        if (!min.isInclusive() || !max.isInclusive()) {
            return true;
        }
        return false;
    }

    //Returns true if the given constraints with the same 2 clocksAsCollection restricts us to the empty space
    private boolean checkEmptiness(TwoClockConstraint c1) {
        Map<Clock, TwoClockConstraint> opposites = twoClockConstraints.get(c1.getClock2());
        if (opposites == null) {
            return false;
        }
        TwoClockConstraint c2 = opposites.get(c1.getClock1());
        if (c1 == null || c2 == null) {
            return false;
        }
        if (c1.getnValue() > -c2.getnValue()) {
            return false;
        }
        if (c1.getnValue() < -c2.getnValue()) {
            return true;
        }
        if (!c1.isInclusive() || !c2.isInclusive()) {
            return true;
        }
        return false;
    }

    //Returns true if two clock constraint is more restrictive than the point where x and y cross
    private boolean checkConstraintLessThan(TwoClockConstraint tcConstraint, SingleClockConstraint x, SingleClockConstraint y) {
        boolean inclusive = x.isInclusive() && y.isInclusive();
        double boundingValue = x.getnValue() - y.getnValue();
        if (tcConstraint.getnValue() < boundingValue) {
            return true;
        }
        if (tcConstraint.getnValue() == boundingValue && (!inclusive || !tcConstraint.isInclusive())) {
            return true;
        }
        return false;
    }

    //Returns true if two clock constraint is less restrictive than the point where x and y cross
    private boolean checkConstraintGreaterThan(TwoClockConstraint tcConstraint, SingleClockConstraint x, SingleClockConstraint y) {
        boolean inclusive = x.isInclusive() && y.isInclusive();
        double boundingValue = x.getnValue() - y.getnValue();
        if (tcConstraint.getnValue() > boundingValue) {
            return true;
        }
        if (tcConstraint.getnValue() == boundingValue && (!inclusive || tcConstraint.isInclusive())) {
            return true;
        }
        return false;
    }

    public boolean isRestrictedToEmptiness() {
        return restrictedToEmptiness;
    }

    public SingleClockConstraint getMinConstraint(Clock key) {
        return minBoundConstraints.get(key);
    }

    public SingleClockConstraint getMaxConstraint(Clock key) {
        if (!maxBoundConstraints.containsKey(key)) {
            maxBoundConstraints.put(key, new SingleClockConstraint(Inequality.LessThan, true, Double.POSITIVE_INFINITY, key));
        }
        return maxBoundConstraints.get(key);
    }

    public TwoClockConstraint getTCConstraint(Clock key1, Clock key2) {
        Map<Clock, TwoClockConstraint> mapByFirstClock = twoClockConstraints.get(key1);
        if (mapByFirstClock == null) {
            return null;
        }
        return mapByFirstClock.get(key2);
    }

    public Collection<TwoClockConstraint> getTCConstraintByPrimary(Clock key) {
        Map<Clock, TwoClockConstraint> mapBySecondClock = twoClockConstraints.get(key);
        if (mapBySecondClock == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(mapBySecondClock.values());
    }

    public Collection<TwoClockConstraint> getTCConstraintBySecondary(Clock key) {
        Map<Clock, TwoClockConstraint> mapBySecondClock = twoClockConstraintsBySecondary.get(key);
        if (mapBySecondClock == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(mapBySecondClock.values());
    }

    public Collection<SingleClockConstraint> getMinConstraints() {
        return new ArrayList<>(minBoundConstraints.values());
    }

    public Collection<SingleClockConstraint> getMaxConstraints() {
        return new ArrayList<>(maxBoundConstraints.values());
    }

    public Collection<TwoClockConstraint> getTCConstraints() {
        return twoClockConstraints.values().stream()
                .flatMap(map -> map.values().stream())
                .collect(Collectors.toList());
    }
}
