package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.Constraints.SingleClockConstraint;
import ZoneVisualizer.Constraints.TwoClockConstraint;
import ZoneVisualizer.Utility.LINQ;

import java.util.*;
import java.util.stream.Collectors;

public class PivotResult {

    private final Vertex fromPivot;
    private Vertex vertex;
    private Clock missingDimension;
    private final Collection<TwoClockConstraint> freeTwoClockConstraints;
    private final Collection<Constraint> fromPivotConstraints;
    private final Collection<TwoClockConstraint> initialTwoClockConstraints;

    public PivotResult(Vertex fromPivot, Vertex vertex, Clock missingDimension, Collection<TwoClockConstraint> twoClockConstraints) {
        this.fromPivot = fromPivot;
        this.fromPivotConstraints = fromPivot.getAllConstraints();
        this.vertex = vertex;
        this.missingDimension = missingDimension;
        this.initialTwoClockConstraints = new ArrayList<>(twoClockConstraints);
        //Find TwoClockConstraints that can only bound one dimension
        List<Clock> knownDimensions = new ArrayList<>(vertex.getKnownDimensions());
        for (int i = 0; i < knownDimensions.size(); i++) {
            Clock knownDimension = knownDimensions.get(i);
            List<TwoClockConstraint> knowables = twoClockConstraints.stream()
                    .filter(tcc -> tcc.hasClock(knownDimension))
                    .collect(Collectors.toList());
            twoClockConstraints.removeAll(knowables);
            for (TwoClockConstraint knowable : knowables) {
                Clock otherClock = knowable.getOtherClock(knownDimension);
                vertex.addConstraint(otherClock, knowable);
                knownDimensions.add(otherClock);
            }
        }
        this.freeTwoClockConstraints = twoClockConstraints;
    }

    public void findMissingConstraints(ConstraintZone constraintZone) {
        Map<Map<Clock, Collection<Constraint>>, Double> potentials = new HashMap<>();
        double oldValue = fromPivot.getCoordinate(missingDimension);
        //Find potential vertices for existing tcc's
        for (TwoClockConstraint potential : freeTwoClockConstraints.stream()
                .filter(c -> c.hasClock(missingDimension))
                .collect(Collectors.toList())) {
            Clock otherClock = potential.getOtherClock(missingDimension);
            Map<Clock, Collection<Constraint>> potentialAdditions = new HashMap<>();
            LINQ.addToDeepMap(potentialAdditions, missingDimension, potential);
            Collection<Constraint> opposites = getMaxOfDimension(constraintZone, otherClock);
            LINQ.addAllToDeepMap(potentialAdditions, otherClock, opposites);

            for (TwoClockConstraint tcc : freeTwoClockConstraints) {
                if (tcc.equals(potential)) {
                    continue;
                }
                Clock key = tcc.getOtherClock(missingDimension);
                LINQ.addToDeepMap(potentialAdditions, key, tcc);
            }
            double valuation = getValuation(potentialAdditions, missingDimension);
            if (valuation > oldValue) {
                potentials.put(potentialAdditions, valuation);
            }
        }
        Map<Clock, Collection<Constraint>> potentialAdditions = new HashMap<>();
        for (TwoClockConstraint tcc : freeTwoClockConstraints) {
            Clock key = tcc.getOtherClock(missingDimension);
            LINQ.addToDeepMap(potentialAdditions, key, tcc);
        }
        //Find potential vertex for max constraint
        Map<Clock, Collection<Constraint>> potentialMaxAdditions = new HashMap<>(potentialAdditions);
        LINQ.addToDeepMap(potentialMaxAdditions, missingDimension, constraintZone.getMaxConstraint(missingDimension));
        double valuation = getValuation(potentialMaxAdditions, missingDimension);
        if (valuation > oldValue) {
            potentials.put(potentialMaxAdditions, valuation);
        }
        //Find potential vertices for Two Clock max bounds
        Collection<TwoClockConstraint> twoClockConstraints = getEligibleTCCs(constraintZone, missingDimension);
        for (TwoClockConstraint tcc : twoClockConstraints) {
            Map<Clock, Collection<Constraint>> tccPotential = new HashMap<>(potentialAdditions);
            LINQ.addToDeepMap(tccPotential, missingDimension, tcc);
            valuation = getValuation(tccPotential, missingDimension);
            if (valuation > oldValue) {
                potentials.put(tccPotential, valuation);
            }
        }

        //Find the smallest maximizing bound. It is the new constraint to add
        Collection<Map<Clock, Collection<Constraint>>> minMaxBounds = LINQ.getMinimums(potentials.keySet(), potentials::get);
        Collection<Constraint> newBoundsOnMissing = minMaxBounds.stream()
                .flatMap(map -> map.get(missingDimension).stream())
                .collect(Collectors.toList());
        vertex.addConstraints(missingDimension, newBoundsOnMissing);
        for (Map<Clock, Collection<Constraint>> minMaxBound : minMaxBounds) {
            for (Map.Entry<Clock, Collection<Constraint>> bounds : minMaxBound.entrySet()) {
                if (newBoundsOnMissing.containsAll(bounds.getValue())) {
                    continue;
                }
                if (!vertex.knowsDimension(bounds.getKey())) {
                    vertex.addConstraints(bounds.getKey(), bounds.getValue());
                }
            }
        }
    }

    private Collection<Constraint> getMaxOfDimension(ConstraintZone constraintZone, Clock dimension) {
        SingleClockConstraint max = constraintZone.getMaxConstraint(dimension);
        if (Double.isFinite(max.getnValue())) {
            return Arrays.asList(max);
        }
        Collection<TwoClockConstraint> twoClockConstraints = getEligibleTCCs(constraintZone, dimension);
        Map<TwoClockConstraint, SingleClockConstraint> maximizingPair = new HashMap<>();
        Collection<SingleClockConstraint> vertexSCC = LINQ.ofType(vertex.getAllConstraints());
        for (TwoClockConstraint tcc : twoClockConstraints) {
            Clock otherDimension = tcc.getOtherClock(dimension);
            Optional<SingleClockConstraint> first = vertexSCC.stream().filter(c -> c.getClock().equals(otherDimension)).findFirst();
            if (first.isPresent()) {
                maximizingPair.put(tcc, first.get());
            }
        }
        Collection<Map.Entry<TwoClockConstraint, SingleClockConstraint>> minMaxConstraints =
                LINQ.getMinimums(maximizingPair.entrySet(), entry ->
                        entry.getKey().getOtherValue(entry.getValue().getClock(), entry.getValue().getnValue()));
        return minMaxConstraints.stream().map(entry -> entry.getKey()).collect(Collectors.toList());
    }

    private Collection<TwoClockConstraint> getEligibleTCCs(ConstraintZone constraintZone, Clock dimension) {
        Collection<TwoClockConstraint> twoClockConstraints = constraintZone.getTCConstraintByPrimary(dimension);
        twoClockConstraints.removeIf(tcc -> initialTwoClockConstraints.stream()
                .anyMatch(init -> init.hasClock(tcc.getClock1()) && init.hasClock(tcc.getClock2())));
        twoClockConstraints.removeAll(fromPivotConstraints);
        return twoClockConstraints;
    }

    private double getValuation(Map<Clock, Collection<Constraint>> valueMap, Clock dimension) {
        Constraint c;
        if (valueMap.containsKey(dimension)) {
            c = LINQ.first(valueMap.get(dimension));
        }
        else {
            c = LINQ.first(vertex.getConstraints(dimension));
        }
        if (c instanceof SingleClockConstraint) {
            return c.getnValue();
        }
        TwoClockConstraint tcc = (TwoClockConstraint)c;
        //Recursion beware
        Clock otherClock = tcc.getOtherClock(dimension);
        double otherValue = getValuation(valueMap, otherClock);
        return tcc.getOtherValue(otherClock, otherValue);
    }

    public Vertex getVertex() {
        return vertex;
    }

    public Clock getMissingDimension() {
        return missingDimension;
    }

    public Collection<TwoClockConstraint> getFreeTwoClockConstraints() {
        return freeTwoClockConstraints;
    }
}
