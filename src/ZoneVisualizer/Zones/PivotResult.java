package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;

import java.util.Collection;

public class PivotResult {

    private Vertex vertex;
    private Clock missingDimension;

    public PivotResult(Vertex vertex, Clock missingDimension) {
        this.vertex = vertex;
        this.missingDimension = missingDimension;
    }

    public void addMissingConstraints(Collection<Constraint> constraints) {
        vertex.addConstraints(missingDimension, constraints);
    }

    public Vertex getVertex() {
        return vertex;
    }

    public Clock getMissingDimension() {
        return missingDimension;
    }
}
