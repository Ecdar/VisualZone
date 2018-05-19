package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.Constraints.SingleClockConstraint;
import ZoneVisualizer.Constraints.TwoClockConstraint;
import ZoneVisualizer.Utility.IAction2;
import ZoneVisualizer.Utility.LINQ;

import java.util.*;
import java.util.stream.Collectors;

public class VertexPotential {

    private final Vertex baseVertex;
    private final Map<Clock, Collection<Constraint>> potentialConstraints = new HashMap<>();
    private final Map<Clock, Collection<Constraint>> potentialAdditions = new HashMap<>();
    private final Collection<TwoClockConstraint> remainingUnknowns = new ArrayList<>();
    private final Clock keyDimension;

    private boolean valuated = false;
    private double potentialKeyValue;

    public static void mergeIntoVertex(Vertex resultVertex, Clock missingDimension,
                                       Collection<VertexPotential> potentials, Collection<TwoClockConstraint> freeTCCs) {
        Collection<Constraint> newBoundsOnMissing = potentials.stream()
                .flatMap(p -> p.potentialConstraints.get(missingDimension).stream())
                .collect(Collectors.toList());
        resultVertex.addConstraints(missingDimension, newBoundsOnMissing);

        List<Clock> foundDimensions = new ArrayList<>();
        foundDimensions.add(missingDimension);
        for (VertexPotential potential : potentials) {
            for (Map.Entry<Clock, Collection<Constraint>> bounds : potential.potentialAdditions.entrySet()) {
                if (bounds.getKey() == missingDimension) {
                    continue;
                }
                Collection<Constraint> newConstraints = new ArrayList<>(bounds.getValue());
                newConstraints.removeAll(newBoundsOnMissing);
                if (!newConstraints.isEmpty()) {
                    foundDimensions.add(bounds.getKey());
                    resultVertex.addConstraints(bounds.getKey(), newConstraints);
                }
            }
        }

        freeTCCs.removeAll(newBoundsOnMissing);
        resolveUnknowns(foundDimensions, freeTCCs, (clock, constraint) ->
        {
            resultVertex.addConstraint(clock, constraint);
            freeTCCs.remove(constraint);
        });
    }

    private static void resolveUnknowns(Collection<Clock> foundDimensions, Collection<TwoClockConstraint> unknowns,
                                        IAction2<Clock, Constraint> binderMethod) {
        if (unknowns.isEmpty()) {
            return;
        }
        List<Clock> knownDimensions = new ArrayList<>(foundDimensions);
        for (int i = 0; i < knownDimensions.size(); i++) {
            Clock knownDimension = knownDimensions.get(i);
            List<TwoClockConstraint> knowables = unknowns.stream()
                    .filter(tcc -> tcc.hasClock(knownDimension))
                    .collect(Collectors.toList());
            for (TwoClockConstraint knowable : knowables) {
                Clock unknownClock = knowable.getOtherClock(knownDimension);
                binderMethod.invoke(unknownClock, knowable);
                knownDimensions.add(unknownClock);
            }
        }
    }

    public VertexPotential(Vertex baseVertex, Clock keyDimension, Collection<TwoClockConstraint> remainingUnknowns) {
        this.baseVertex = baseVertex;
        this.remainingUnknowns.addAll(remainingUnknowns);
        this.keyDimension = keyDimension;
    }

    private VertexPotential(Vertex baseVertex, Clock keyDimension,
                            Map<Clock, Collection<Constraint>> potentialConstraints,
                            Collection<TwoClockConstraint> remainingUnknowns,
                            boolean valuated, double potentialKeyValue) {
        this.baseVertex = baseVertex;
        this.potentialConstraints.putAll(potentialConstraints);
        this.remainingUnknowns.addAll(remainingUnknowns);
        this.keyDimension = keyDimension;
        this.valuated = valuated;
        this.potentialKeyValue = potentialKeyValue;
    }

    public void addPotentialConstraint(Clock dimension, Constraint potentialConstraint) {
        LINQ.addToDeepMap(potentialConstraints, dimension, potentialConstraint);
        if (!remainingUnknowns.remove(potentialConstraint)) {
            LINQ.addToDeepMap(potentialAdditions, dimension, potentialConstraint);
        }
    }

    public void bindRemainingUnknowns() {
        bindRemainingUnknowns(potentialConstraints.keySet());
    }

    public void bindRemainingUnknowns(Collection<Clock> foundDimensions) {
        resolveUnknowns(foundDimensions, remainingUnknowns, this::addPotentialConstraint);
    }

    public Collection<TwoClockConstraint> getRemainingUnknowns() {
        return remainingUnknowns;
    }

    public double getPotentialKeyValue() {
        if (!valuated) {
            potentialKeyValue = calculateValue(keyDimension);
            valuated = true;
        }
        return potentialKeyValue;
    }

    private double calculateValue(Clock dimension) {
        Constraint c;
        if (potentialConstraints.containsKey(dimension)) {
            c = LINQ.first(potentialConstraints.get(dimension));
        }
        else {
            c = LINQ.first(baseVertex.getConstraints(dimension));
        }
        if (c instanceof SingleClockConstraint) {
            return c.getnValue();
        }
        TwoClockConstraint tcc = (TwoClockConstraint)c;
        Clock otherClock = tcc.getOtherClock(dimension);
        //Recursion beware
        double otherValue = calculateValue(otherClock);
        return tcc.getOtherValue(otherClock, otherValue);
    }

    public VertexPotential clone() {
        return new VertexPotential(baseVertex, keyDimension, potentialConstraints, remainingUnknowns, valuated, potentialKeyValue);
    }
}
