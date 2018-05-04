package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.Constraints.SingleClockConstraint;
import ZoneVisualizer.Constraints.TwoClockConstraint;
import ZoneVisualizer.Utility.LINQ;

import java.util.*;
import java.util.stream.Collectors;

public class PivotResult {

    private Vertex fromPivot;
    private Vertex vertex;
    private Clock missingDimension;
    private Collection<TwoClockConstraint> freeTwoClockConstraints;

    public PivotResult(Vertex fromPivot, Vertex vertex, Clock missingDimension, Collection<TwoClockConstraint> twoClockConstraints) {
        this.fromPivot = fromPivot;
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

    public void findMissingConstraints(ConstraintZone constraintZone) {
        Map<Map<Clock, Constraint>, Double> potentials = new HashMap<>();
        //Find potential vertices for existing tcc's
        for (TwoClockConstraint potential : freeTwoClockConstraints.stream()
                .filter(c -> c.hasClock(missingDimension))
                .collect(Collectors.toList())) {
            Clock otherClock = potential.getOtherClock(missingDimension);
            Map<Clock, Constraint> potentialAdditions = new HashMap<>();
            potentialAdditions.put(missingDimension, potential);
            potentialAdditions.put(otherClock, constraintZone.getMaxConstraint(otherClock));
            for (TwoClockConstraint tcc : freeTwoClockConstraints) {
                if (tcc.equals(potential)) {
                    continue;
                }
                Clock key = tcc.getOtherClock(missingDimension);
                potentialAdditions.put(key, tcc);
            }
            potentials.put(potentialAdditions, getValuation(potentialAdditions, missingDimension));
        }
        Map<Clock, Constraint> potentialAdditions = new HashMap<>();
        for (TwoClockConstraint tcc : freeTwoClockConstraints) {
            Clock key = tcc.getOtherClock(missingDimension);
            potentialAdditions.put(key, tcc);
        }
        //Find potential vertex for max constraint
        Map<Clock, Constraint> potentialMaxAdditions = new HashMap<>(potentialAdditions);
        potentialMaxAdditions.put(missingDimension, constraintZone.getMaxConstraint(missingDimension));
        potentials.put(potentialMaxAdditions, getValuation(potentialMaxAdditions, missingDimension));
        //Find potential vertices for Two Clock max bounds
        Collection<TwoClockConstraint> twoClockConstraints = constraintZone.getTCConstraintByPrimary(missingDimension);
        twoClockConstraints.removeAll(freeTwoClockConstraints);
        twoClockConstraints.removeAll(vertex.getAllConstraints());
        for (TwoClockConstraint tcc : twoClockConstraints) {
            Map<Clock, Constraint> tccPotential = new HashMap<>(potentialAdditions);
            tccPotential.put(missingDimension, tcc);
            potentials.put(tccPotential, getValuation(tccPotential, missingDimension));
        }
        Collection<Map<Clock, Constraint>> minMaxBounds = LINQ.getMinimums(potentials.keySet(), p -> potentials.get(p));



        //Old implementation
        /*while (!pivotResult.getMissingDimensions().isEmpty()) {
            Clock missingDimension = pivotResult.getMissingDimensions().get(0);
            Collection<TwoClockConstraint> twoClockConstraints =
                    constraintZone.getTCConstraintByPrimary(missingDimension);
            SingleClockConstraint dimensionMax = constraintZone.getMaxConstraint(missingDimension);
            Double oldValue = pivot.getCoordinate(missingDimension);
            //A TCC is eligible for addition if it was not used earlier and it will make dimension greater,
            //but not greater than the max of that dimension
            //Todo this can gives the wrong result when following 2 tcc's
            twoClockConstraints.removeIf(tcc -> {
                        if(pivot.getAllConstraints().contains(tcc)) {
                            return true;
                        }
                        Double tccValue = getTCCValue(pivot, tcc);
                        return tccValue <= oldValue || tccValue >= dimensionMax.getnValue();
                    });

            if (twoClockConstraints.isEmpty()) {
                pivotResult.addMissingConstraint(missingDimension, dimensionMax);
                continue;
            }

            //Calculating the TCC values again unnecessarily, probably better to save them
            Collection<TwoClockConstraint> minMaxConstraints =
                    LINQ.getMinimums(twoClockConstraints, tcc -> getTCCValue(pivot, tcc));

            pivotResult.addMissingConstraints(missingDimension, minMaxConstraints);
        }

        if (!vertices.contains(pivotResult.getVertex())) {
            addVertex(pivotResult.getVertex());
        }*/
    }

    private double getValuation(Map<Clock, Constraint> valueMap, Clock dimension) {
        Constraint c;
        if (valueMap.containsKey(dimension)) {
            c = valueMap.get(dimension);
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
