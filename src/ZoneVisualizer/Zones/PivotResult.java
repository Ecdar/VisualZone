package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.Utility.LINQ;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class PivotResult {

    private Vertex vertex;
    private List<Clock> missingDimensions;
    private Collection<VertexPotential> vertexPotentials;

    private Map<Clock, Collection<VertexPotential>> resolutionCandidates = new HashMap<>();

    public PivotResult(Vertex vertex, List<Clock> missingDimensions, Collection<VertexPotential> vertexPotentials) {
        this.vertex = vertex;
        this.missingDimensions = missingDimensions;
        this.vertexPotentials = new ArrayList<>();
        fillVertexPotentials(vertex, missingDimensions, vertexPotentials);
    }

    private void fillVertexPotentials(Vertex vertex, List<Clock> missingDimensions, Collection<VertexPotential> vertexPotentials) {
        Map<Clock, VertexPotential> trivialPotentials = new HashMap<>();
        for (VertexPotential potential : vertexPotentials) {
            Clock oldDimension = potential.getOldDimension();
            if (missingDimensions.contains(oldDimension)) {
                this.vertexPotentials.add(potential);
                continue;
            }
            if (!trivialPotentials.containsKey(oldDimension)) {
                trivialPotentials.put(oldDimension, potential);
            }
            else {
                VertexPotential previous = trivialPotentials.get(oldDimension);
                if  (previous != null) {
                    this.vertexPotentials.add(previous);
                    trivialPotentials.put(oldDimension, null);
                }
                this.vertexPotentials.add(potential);
            }
        }
        for (VertexPotential trivialPotential : trivialPotentials.values()) {
            if (trivialPotential == null) {
                continue;
            }
            vertex.addConstraint(trivialPotential.getNewDimension(), trivialPotential.getConstraint());
        }
    }

    public void addMissingConstraint(Clock dimension, Constraint constraint) {
        addMissingConstraints(dimension, Arrays.asList(constraint));
    }

    public void addMissingConstraints(Clock dimension, Collection<? extends Constraint> constraints) {
        missingDimensions.remove(dimension);
        Collection<VertexPotential> resolveCandidates = vertexPotentials.stream()
                .filter(p -> p.getOldDimension() == dimension)
                .collect(Collectors.toList());
        if (Double.isFinite(LINQ.first(constraints).getnValue())) {
            if (resolveCandidates.isEmpty()) {
                vertex.addConstraints(dimension, constraints);
            }
            else {
                resolveConstraint(constraints, resolveCandidates);
            }
            return;
        }

        //Todo handle none finite cases
    }

    private void resolveConstraint(Collection<? extends Constraint> constraints, Collection<VertexPotential> resolveCandidates) {
        for (VertexPotential candidate : new ArrayList<>(resolveCandidates)) {
            candidate.setResolution(constraints);
            vertexPotentials.remove(candidate);
            Clock newDimension = candidate.getNewDimension();
            LINQ.addToDeepMap(resolutionCandidates, newDimension, candidate);
            if (vertexPotentials.stream().noneMatch(p -> p.getNewDimension() == newDimension)) {
                resolveDimension(newDimension);
            }
        }
    }

    private void resolveDimension(Clock newDimension) {
        Collection<VertexPotential> resolvedPotentials = resolutionCandidates.remove(newDimension);
        Pair<Collection<VertexPotential>, Double> minimizingResult = LINQ.getMinimums(resolvedPotentials, this::resolveValue);
        Collection<VertexPotential> minimumCandidates = minimizingResult.getKey();
        resolvedPotentials.removeAll(minimumCandidates);
        vertex.addConstraints(newDimension, minimumCandidates.stream().map(VertexPotential::getConstraint).collect(Collectors.toList()));
        for (VertexPotential potential : minimumCandidates) {
            vertex.addConstraints(potential.getOldDimension(), potential.getResolution());
        }
        for (VertexPotential potential : resolvedPotentials) {
            vertex.addConstraint(potential.getOldDimension(), potential.getConstraint());
        }
    }

    private double resolveValue(VertexPotential potential) {
        double knownValue = vertex.calculateCoordinate(potential.getOldDimension(), LINQ.first(potential.getResolution()));
        return potential.getConstraint()
                .getOtherValue(potential.getOldDimension(), knownValue);
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
