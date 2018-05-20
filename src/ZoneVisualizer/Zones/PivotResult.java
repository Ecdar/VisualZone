package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.Constraints.SingleClockConstraint;
import ZoneVisualizer.Constraints.TwoClockConstraint;
import ZoneVisualizer.Utility.IAction2;
import ZoneVisualizer.Utility.LINQ;

import java.util.*;
import java.util.stream.Collectors;

public class PivotResult {

    private final Vertex fromPivot;
    private Vertex vertex;
    private Clock missingDimension;
    private final Collection<TwoClockConstraint> freeTwoClockConstraints;
    private final Collection<Constraint> fromPivotConstraints;

    public PivotResult(Vertex fromPivot, Vertex vertex, Clock missingDimension, Collection<TwoClockConstraint> twoClockConstraints) {
        this.fromPivot = fromPivot;
        this.fromPivotConstraints = fromPivot.getAllConstraints();
        this.vertex = vertex;
        this.missingDimension = missingDimension;
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

    public void findMissingConstraints(ConstraintZone constraintZone, double infinityValue) {
        Collection<VertexPotential> potentials = new ArrayList<>();
        double oldValue = fromPivot.getCoordinate(missingDimension);
        //Find potential vertices for existing tcc's
        for (TwoClockConstraint potentialAddition : freeTwoClockConstraints.stream()
                .filter(c -> c.hasClock(missingDimension))
                .collect(Collectors.toList())) {
            Clock otherClock = potentialAddition.getOtherClock(missingDimension);
            VertexPotential potential = new VertexPotential(fromPivot, missingDimension, infinityValue, freeTwoClockConstraints);
            potential.addPotentialConstraint(missingDimension, potentialAddition);
            addMaxOfDimension(constraintZone, otherClock, potential, infinityValue);
            potential.bindRemainingUnknowns();
            if (potential.getPotentialKeyValue() > oldValue) {
                potentials.add(potential);
            }
        }
        VertexPotential basePotential = new VertexPotential(fromPivot, missingDimension, infinityValue, freeTwoClockConstraints);
        basePotential.bindRemainingUnknowns(Collections.singletonList(missingDimension));

        //Find potential vertex for max constraint
        VertexPotential maxPotential = basePotential.clone();
        maxPotential.addPotentialConstraint(missingDimension, constraintZone.getMaxConstraint(missingDimension));
        potentials.add(maxPotential);

        //Find potential vertices for Two Clock max bounds
        Collection<TwoClockConstraint> twoClockConstraints = getEligibleTCCs(constraintZone, missingDimension);
        for (TwoClockConstraint tcc : twoClockConstraints) {
            VertexPotential tccPotential = basePotential.clone();
            tccPotential.addPotentialConstraint(missingDimension, tcc);
            if (tccPotential.getPotentialKeyValue() > oldValue) {
                potentials.add(tccPotential);
            }
        }

        //Find the smallest maximizing bounds. Add them to the vertex
        Collection<VertexPotential> minMaxBounds = LINQ.getMinimums(potentials, VertexPotential::getPotentialKeyValue);
        VertexPotential.mergeIntoVertex(vertex, missingDimension, minMaxBounds, freeTwoClockConstraints);
    }

    private void addMaxOfDimension(ConstraintZone constraintZone, Clock dimension, VertexPotential potential, double infinityValue) {
        Collection<TwoClockConstraint> twoClockConstraints = getEligibleTCCs(constraintZone, dimension);
        Map<Constraint, Double> maximizingPair = new HashMap<>();
        Collection<SingleClockConstraint> vertexSCC = LINQ.ofTypeSCC(vertex.getAllConstraints());
        for (TwoClockConstraint tcc : twoClockConstraints) {
            Clock otherDimension = tcc.getOtherClock(dimension);
            Optional<SingleClockConstraint> first = vertexSCC.stream()
                    .filter(c -> c.getClock().equals(otherDimension))
                    .findFirst();
            if (first.isPresent()) {
                double sccValue = first.get().getnValue();
                if (!Double.isFinite(sccValue)) {
                    sccValue = infinityValue;
                }
                maximizingPair.put(tcc, tcc.getOtherValue(otherDimension, sccValue));
            }
        }
        for (TwoClockConstraint tcc : potential.getRemainingUnknowns().stream()
                .filter(c -> c.hasClock(dimension))
                .collect(Collectors.toList())) {
            Clock otherDimension = tcc.getOtherClock(dimension);
            //Todo might need recursion here
            SingleClockConstraint scc = constraintZone.getMaxConstraint(otherDimension);
            double sccValue = scc.getnValue();
            if (!Double.isFinite(sccValue)) {
                sccValue = infinityValue;
            }
            double valuation = tcc.getOtherValue(otherDimension, sccValue);
            maximizingPair.put(scc, valuation);
            maximizingPair.put(tcc, valuation);
        }
        Collection<Map.Entry<Constraint, Double>> minMaxConstraints =
                LINQ.getMinimums(maximizingPair.entrySet(), Map.Entry::getValue);
        SingleClockConstraint max = constraintZone.getMaxConstraint(dimension);
        if (minMaxConstraints.isEmpty() || max.getnValue() < LINQ.first(minMaxConstraints).getValue()) {
            potential.addPotentialConstraint(dimension, max);
        }
        else {
            List<Constraint> constraints = minMaxConstraints.stream()
                    .map(entry -> entry.getKey())
                    .collect(Collectors.toList());
            for (Constraint c : constraints) {
                if (c instanceof SingleClockConstraint) {
                    SingleClockConstraint scc = (SingleClockConstraint)c;
                    potential.addPotentialConstraint(scc.getClock(), scc);
                }
                else {
                    potential.addPotentialConstraint(dimension, c);
                }
            }
        }
    }

    private Collection<TwoClockConstraint> getEligibleTCCs(ConstraintZone constraintZone, Clock dimension) {
        Collection<TwoClockConstraint> twoClockConstraints = constraintZone.getTCConstraintByPrimary(dimension);
        twoClockConstraints.removeAll(fromPivotConstraints);
        twoClockConstraints.removeIf(tcc ->
                freeTwoClockConstraints.stream().anyMatch(free -> free.hasClock(tcc.getClock1())) &&
                freeTwoClockConstraints.stream().anyMatch(free -> free.hasClock(tcc.getClock2())));
        return twoClockConstraints;
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
