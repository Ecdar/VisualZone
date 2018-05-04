package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
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
