package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.Constraints.TwoClockConstraint;
import ZoneVisualizer.Utility.LINQ;

import java.util.*;
import java.util.stream.Collectors;

public class OriginCreator {

    private final ConstraintZone constraintZone;
    private final Vertex origin;
    private final List<Clock> unknownDimensions = new ArrayList<>();
    private final List<Clock> knownDimensions = new ArrayList<>();
    private final Map<Clock, Collection<TwoClockConstraint>> unresolvedConstraintsOnDimensions = new HashMap<>();
    private final Map<Clock, Set<TwoClockConstraint>> candidatesOnDimensions = new HashMap<>();
    private final Map<Clock, Double> maxValues = new HashMap<>();
    private final Map<Clock, Set<TwoClockConstraint>> unresolvedCandidatesOnDimensions = new HashMap<>();
    private Set<Clock> optimizableDimensions;

    public static Vertex findOrigin(ConstraintZone constraintZone, Collection<Clock> dimensions) {
        OriginCreator creator = new OriginCreator(constraintZone, dimensions);

        return creator.origin;
    }

    private OriginCreator(ConstraintZone constraintZone, Collection<Clock> dimensions) {
        this.constraintZone = constraintZone;
        origin = new Vertex(dimensions);
        decideTrivialDimensions(dimensions);

        setupUnresolvedConstraints();

        for (Clock dimension : knownDimensions) {
            resolveConstraintsWithKnownDimension(dimension, getCoordinate(dimension));
        }

        resolveRemainingDimensions();

        while (!unknownDimensions.isEmpty()) {
            resolveDimension(unknownDimensions.get(0));
        }
    }

    private void decideTrivialDimensions(Collection<Clock> dimensions) {
        for (Clock clock : dimensions) {
            Constraint c = constraintZone.getMinConstraint(clock);
            if (c != null) {
                //Simple case; a greater than constraint exists for this dimension
                addConstraint(clock, c);
                knownDimensions.add(clock);
            }
            else if (constraintZone.getTCConstraintBySecondary(clock).isEmpty()) {
                //No bounds on this dimension. Add an implicit greater than 0 bound
                addConstraint(clock, Constraint.zeroBound(clock));
                knownDimensions.add(clock);
            }
            else {
                unknownDimensions.add(clock);
            }
        }
    }

    private void setupUnresolvedConstraints() {
        for (Clock unknownDimension : unknownDimensions) {
            unresolvedConstraintsOnDimensions.put(unknownDimension, constraintZone.getTCConstraintBySecondary(unknownDimension));
            candidatesOnDimensions.put(unknownDimension, new HashSet<>());
            maxValues.put(unknownDimension, (double)0);
            unresolvedCandidatesOnDimensions.put(unknownDimension, new HashSet<>());
        }
    }

    private void resolveConstraintsWithKnownDimension(Clock knownDimension, double knownValue) {
        Collection<TwoClockConstraint> potentialLowerBounds = constraintZone.getTCConstraintByPrimary(knownDimension);
        for (TwoClockConstraint potentialLowerBound : potentialLowerBounds) {
            Clock otherClock = potentialLowerBound.getClock2();
            if (!unresolvedConstraintsOnDimensions.containsKey(otherClock)) {
                //Dimension is already solved as a trivial case
                continue;
            }
            double valuation = potentialLowerBound.getOtherValue(knownDimension, knownValue);
            Collection<TwoClockConstraint> constraintsOnThis = unresolvedConstraintsOnDimensions.get(otherClock);
            Collection<TwoClockConstraint> candidatesOnThis = candidatesOnDimensions.get(otherClock);
            Set<TwoClockConstraint> unresolvedCandidatesOnThis = unresolvedCandidatesOnDimensions.get(otherClock);
            double maxOnThis = maxValues.get(otherClock);
            if (valuation == maxOnThis) {
                LINQ.move(potentialLowerBound, constraintsOnThis, candidatesOnThis);
            }
            else if (valuation > maxOnThis) {
                candidatesOnThis.clear();
                LINQ.moveAll(unresolvedCandidatesOnThis, unresolvedCandidatesOnThis, constraintsOnThis);
                LINQ.move(potentialLowerBound, constraintsOnThis, candidatesOnThis);
                maxValues.put(otherClock, valuation);
            }
            else {
                //Not feasible
                constraintsOnThis.remove(potentialLowerBound);
            }
            if (constraintsOnThis.isEmpty()) {
                //Found a pairing for all tcc's of dimension. Resolve
                resolveDimension(otherClock);
            }
        }
    }

    private void resolveRemainingDimensions() {
        optimizableDimensions = new HashSet<>(unknownDimensions);
        while (!optimizableDimensions.isEmpty()) {
            Clock optimizableDimension = LINQ.first(optimizableDimensions);
            optimizableDimensions.remove(optimizableDimension);
            double minMaxValue = maxValues.get(optimizableDimension);
            for (Clock unresolvedDimension : new ArrayList<>(unknownDimensions)) {
                if (unresolvedDimension == optimizableDimension || !unknownDimensions.contains(unresolvedDimension)) {
                    continue;
                }
                double minMaxOnThis = maxValues.get(unresolvedDimension);
                Collection<TwoClockConstraint> unresolvedOnThis = unresolvedConstraintsOnDimensions.get(unresolvedDimension);
                Set<TwoClockConstraint> candidatesOnThis = candidatesOnDimensions.get(unresolvedDimension);
                Set<TwoClockConstraint> unresolvedCandidatesOnThis = unresolvedCandidatesOnDimensions.get(unresolvedDimension);
                for (TwoClockConstraint optimization : unresolvedOnThis.stream()
                        .filter(c -> c.getClock1() == optimizableDimension)
                        .collect(Collectors.toList())) {
                    double optimizationValuation = optimization.getOtherValue(optimizableDimension, minMaxValue);
                    if (optimizationValuation == minMaxOnThis) {
                        LINQ.move(optimization, unresolvedOnThis, unresolvedCandidatesOnThis);
                    }
                    else if (optimizationValuation > minMaxOnThis) {
                        candidatesOnThis.clear();
                        LINQ.moveAll(unresolvedCandidatesOnThis, unresolvedCandidatesOnThis, unresolvedOnThis);
                        LINQ.move(optimization, unresolvedOnThis, unresolvedCandidatesOnThis);
                        minMaxOnThis = optimizationValuation;
                        maxValues.put(unresolvedDimension, minMaxOnThis);
                        optimizableDimensions.add(unresolvedDimension);
                    }
                }
            }
        }
    }

    private void markClockKnown(Clock clock) {
        unknownDimensions.remove(clock);
        unresolvedConstraintsOnDimensions.remove(clock);
        unresolvedCandidatesOnDimensions.remove(clock);
        candidatesOnDimensions.remove(clock);
        maxValues.remove(clock);
        optimizableDimensions.remove(clock);
    }

    private void resolveDimension(Clock dimension) {
        Collection<TwoClockConstraint> candidatesOnThis = candidatesOnDimensions.get(dimension);
        Set<TwoClockConstraint> unresolvedCandidatesOnThis = unresolvedCandidatesOnDimensions.get(dimension);
        LINQ.moveAll(unresolvedCandidatesOnThis, unresolvedCandidatesOnThis, candidatesOnThis);
        if (candidatesOnThis.isEmpty()) {
            addConstraint(dimension, Constraint.zeroBound(dimension));
        }
        else {
            addConstraints(dimension, candidatesOnThis);
        }
        double maxOfDimension = maxValues.get(dimension);
        markClockKnown(dimension);
        resolveConstraintsWithKnownDimension(dimension, maxOfDimension);
    }

    private Double getCoordinate(Clock key) {
        return origin.getCoordinate(key);
    }

    private void addConstraint(Clock key, Constraint value) {
        origin.addConstraint(key, value);
    }

    private void addConstraints(Clock key, Collection<? extends Constraint> values) {
        origin.addConstraints(key, values);
    }
}
