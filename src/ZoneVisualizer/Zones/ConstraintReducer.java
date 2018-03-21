package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.*;

import java.util.*;

public class ConstraintReducer {

    private Map<Clock, SingleClockConstraint> minBoundConstraints = new HashMap<>();
    private Map<Clock, SingleClockConstraint> maxBoundConstraints = new HashMap<>();
    private Map<Clock, TwoClockConstraint> twoClockConstraints = new HashMap<>();
    private boolean restrictedToEmptiness = false;

    public ConstraintReducer(Collection<Constraint> constraints) {
        for (Constraint constraint : constraints) {
            if (constraint instanceof SingleClockConstraint) {
                if (addSingleClockConstraint((SingleClockConstraint)constraint)) return;
            }
            else if (constraint instanceof TwoClockConstraint) {
                if (addTwoClockConstraint((TwoClockConstraint)constraint)) return;
            }
        }

        Collection<TwoClockConstraint> tempTCConstraints = new ArrayList<>(twoClockConstraints.values());
        for (TwoClockConstraint tcConstraint : tempTCConstraints) {
            //Remember all of these can be null!
            SingleClockConstraint
                    xMin = minBoundConstraints.get(tcConstraint.getClock1()),
                    xMax = maxBoundConstraints.get(tcConstraint.getClock1()),
                    yMin = minBoundConstraints.get(tcConstraint.getClock2()),
                    yMax = maxBoundConstraints.get(tcConstraint.getClock2());
            boolean inclusive = xMin.isInclusive() && yMax.isInclusive() && tcConstraint.isInclusive();
            double boundingValue = xMin.getnValue() - yMax.getnValue();
            if (boundingValue > tcConstraint.getnValue() ||
                    (boundingValue == tcConstraint.getnValue() && !inclusive)) {
                //Empty case
                restrictedToEmptiness = true;
                return;
            }
            inclusive = xMax.isInclusive() && yMin.isInclusive();
            boundingValue = xMax.getnValue() - yMin.getnValue();
            if (boundingValue < tcConstraint.getnValue() ||
                    (boundingValue == tcConstraint.getnValue() && (!inclusive || tcConstraint.isInclusive()))) {
                //Redundant case
                twoClockConstraints.remove(tcConstraint.getClock1());
                continue;
            }
            boolean nGreaterThanMaxMax, nLessThanMinMin;
            //Todo check for case 2, 3, 4 and 6
        }
        //Todo check for emptiness between two clock constraints and single clock constraints
        //Todo remove single clock constraints that are redundant because of two clock constraints and vice versa
    }

    private boolean addTwoClockConstraint(TwoClockConstraint tcConstraint) {
        //Invert greater than constraints
        if (tcConstraint.getInequality() == Inequality.GreaterThan) {
            tcConstraint = tcConstraint.getInvertedConstraint();
        }
        if (checkEmptiness(tcConstraint, twoClockConstraints.get(tcConstraint.getClock2()))) {
            restrictedToEmptiness = true;
            return true;
        }
        tryAddTwoClockBound(tcConstraint);
        return false;
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
        TwoClockConstraint old = twoClockConstraints.get(constraint.getClock1());
        if (old == null || constraint.getnValue() < old.getnValue() ||
                (constraint.getnValue() == old.getnValue() && !constraint.isInclusive())) {
            twoClockConstraints.put(constraint.getClock1(), constraint);
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

    //Returns true if the given constraints with the same 2 clocks restricts us to the empty space
    private boolean checkEmptiness(TwoClockConstraint c1, TwoClockConstraint c2) {
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

    public boolean isRestrictedToEmptiness() {
        return restrictedToEmptiness;
    }
}
