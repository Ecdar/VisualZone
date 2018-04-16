package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;

import java.util.Collection;

public class PivotResult {

    private Vertex vertex;
    private Collection<Clock> missingDimensions;

    public PivotResult(Vertex vertex, Collection<Clock> missingDimensions) {
        this.vertex = vertex;
        this.missingDimensions = missingDimensions;
    }

    public void addMissingConstraint(Clock dimension, Constraint constraint) {
        vertex.addConstraint(dimension, constraint);
    }

    public void addMissingConstraints(Clock dimension, Collection<Constraint> constraints) {
        vertex.addConstraints(dimension, constraints);
    }

    public Vertex getVertex() {
        return vertex;
    }

    public Collection<Clock> getMissingDimensions() {
        return missingDimensions;
    }
}
