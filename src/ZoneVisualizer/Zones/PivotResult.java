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
            Collection<TwoClockConstraint> remainingFreeConstraints = new ArrayList<>(freeTwoClockConstraints);
            remainingFreeConstraints.remove(potential);
            addMaxOfDimension(constraintZone, otherClock, potentialAdditions, remainingFreeConstraints);
            if (this.freeTwoClockConstraints.size() > 1) {
                ArrayList<Clock> knownDimensions = new ArrayList<>(potentialAdditions.keySet());
                ArrayList<TwoClockConstraint> remainingUnknowns = new ArrayList<>(this.freeTwoClockConstraints);
                remainingUnknowns.remove(potential);
                potentialAdditions.forEach((key, value) -> remainingUnknowns.removeAll(value));
                bindRemainingTCC(potentialAdditions, knownDimensions, remainingUnknowns);
            }
            double valuation = getValuation(potentialAdditions, missingDimension);
            if (valuation > oldValue) {
                potentials.put(potentialAdditions, valuation);
            }
        }
        Map<Clock, Collection<Constraint>> potentialAdditions = new HashMap<>();
        ArrayList<Clock> knownDimensions = new ArrayList<>();
        knownDimensions.add(missingDimension);
        ArrayList<TwoClockConstraint> remainingUnknowns = new ArrayList<>(freeTwoClockConstraints);
        bindRemainingTCC(potentialAdditions, knownDimensions, remainingUnknowns);

        //Find potential vertex for max constraint
        Map<Clock, Collection<Constraint>> potentialMaxAdditions = new HashMap<>(potentialAdditions);
        LINQ.addToDeepMap(potentialMaxAdditions, missingDimension, constraintZone.getMaxConstraint(missingDimension));
        double valuation = getValuation(potentialMaxAdditions, missingDimension);
        potentials.put(potentialMaxAdditions, valuation);
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

    private void bindRemainingTCC(Map<Clock, Collection<Constraint>> potentialAdditions,
                                  List<Clock> knownDimensions, Collection<TwoClockConstraint> remainingUnknowns) {
        for (int i = 0; i < knownDimensions.size(); i++) {
            Clock knownDimension = knownDimensions.get(i);
            List<TwoClockConstraint> knowables = remainingUnknowns.stream()
                    .filter(tcc -> tcc.hasClock(knownDimension))
                    .collect(Collectors.toList());
            remainingUnknowns.removeAll(knowables);
            for (TwoClockConstraint knowable : knowables) {
                Clock unknownClock = knowable.getOtherClock(knownDimension);
                LINQ.addToDeepMap(potentialAdditions, unknownClock, knowable);
                knownDimensions.add(unknownClock);
            }
        }
    }

    private void addMaxOfDimension(ConstraintZone constraintZone, Clock dimension,
                                   Map<Clock, Collection<Constraint>> potentialAdditions,
                                   Collection<TwoClockConstraint> remainingFreeConstraints) {
        Collection<TwoClockConstraint> twoClockConstraints = getEligibleTCCs(constraintZone, dimension);
        Map<Constraint, Double> maximizingPair = new HashMap<>();
        Collection<SingleClockConstraint> vertexSCC = LINQ.ofType(vertex.getAllConstraints());
        for (TwoClockConstraint tcc : twoClockConstraints) {
            Clock otherDimension = tcc.getOtherClock(dimension);
            Optional<SingleClockConstraint> first = vertexSCC.stream()
                    .filter(c -> c.getClock().equals(otherDimension))
                    .findFirst();
            if (first.isPresent()) {
                SingleClockConstraint scc = first.get();
                maximizingPair.put(tcc, tcc.getOtherValue(otherDimension, scc.getnValue()));
            }
        }
        for (TwoClockConstraint tcc : remainingFreeConstraints) {
            Clock otherDimension = tcc.getOtherClock(dimension);
            //Todo might need recursion here
            SingleClockConstraint scc = constraintZone.getMaxConstraint(otherDimension);
            double valuation = tcc.getOtherValue(otherDimension, scc.getnValue());
            maximizingPair.put(scc, valuation);
            maximizingPair.put(tcc, valuation);
        }
        Collection<Map.Entry<Constraint, Double>> minMaxConstraints =
                LINQ.getMinimums(maximizingPair.entrySet(), Map.Entry::getValue);
        SingleClockConstraint max = constraintZone.getMaxConstraint(dimension);
        if (minMaxConstraints.isEmpty() || max.getnValue() < LINQ.first(minMaxConstraints).getValue()) {
            LINQ.addToDeepMap(potentialAdditions, dimension, max);
        }
        else {
            List<Constraint> constraints = minMaxConstraints.stream()
                    .map(entry -> entry.getKey())
                    .collect(Collectors.toList());
            for (Constraint c : constraints) {
                if (c instanceof SingleClockConstraint) {
                    SingleClockConstraint scc = (SingleClockConstraint)c;
                    LINQ.addToDeepMap(potentialAdditions, scc.getClock(), scc);
                }
                else {
                    LINQ.addToDeepMap(potentialAdditions, dimension, c);
                }
            }
        }
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
