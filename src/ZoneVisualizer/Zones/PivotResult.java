package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PivotResult {

    private Vertex vertex;
    private List<Clock> missingDimensions;
    private Collection<VertexPotential> vertexPotentials;

    public PivotResult(Vertex vertex, List<Clock> missingDimensions, Collection<VertexPotential> vertexPotentials) {
        this.vertex = vertex;
        this.missingDimensions = missingDimensions;
        this.vertexPotentials = vertexPotentials;
    }

    public void addMissingConstraint(Clock dimension, Constraint constraint) {
        missingDimensions.remove(dimension);

        Collection<VertexPotential> resolveCandidates = vertexPotentials.stream()
                .filter(p -> p.getOldDimension() == dimension)
                .collect(Collectors.toList());
        Collection<VertexPotential> secondaryResolveCandidates = vertexPotentials.stream()
                .filter(p -> p.getNewDimension() == dimension)
                .collect(Collectors.toList());
        if (resolveCandidates.isEmpty()) {
            resolveMissingConstraint(dimension, constraint, resolveCandidates);
            return;
        }
        if (!Double.isFinite(constraint.getnValue())) {
            //Old ones should bound again

            return;
        }
        //Maybe? Could be degenerate
        resolveMissingConstraint(dimension, constraint, resolveCandidates);
        //Resolve candidates
    }

    private void resolveMissingConstraint(Clock dimension, Constraint constraint, Collection<VertexPotential> resolveCandidates) {
        vertex.addConstraint(dimension, constraint);
        for (VertexPotential candidate : resolveCandidates) {
            vertex.addConstraint(candidate.getNewDimension(), candidate.getConstraint());
        }
        Collection<VertexPotential> deniedCandidates = vertexPotentials.stream()
                .filter(p -> p.getNewDimension() == dimension)
                .collect(Collectors.toList());
        for (VertexPotential candidate : deniedCandidates) {
            vertex.addConstraint(candidate.getOldDimension(), candidate.getConstraint());
        }
    }

    public void addMissingConstraints(Clock dimension, Collection<Constraint> constraints) {
        missingDimensions.remove(dimension);
        vertex.addConstraints(dimension, constraints);
    }


    public Vertex getVertex() {
        return vertex;
    }

    public List<Clock> getMissingDimensions() {
        return missingDimensions;
    }

    public Collection<VertexPotential> getVertexPotentials() {
        return vertexPotentials;
    }
}
