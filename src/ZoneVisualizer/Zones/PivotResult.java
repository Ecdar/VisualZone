package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.Utility.LINQ;

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
        missingDimensions.remove(dimension);
        Collection<VertexPotential> resolveCandidates = vertexPotentials.stream()
                .filter(p -> p.getOldDimension() == dimension)
                .collect(Collectors.toList());
        if (Double.isFinite(constraint.getnValue())) {
            resolveConstraint(constraint, resolveCandidates);
            return;
        }

        //Todo this is as far as I got (everything below in this method is probably shit)
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

    private void resolveConstraint(Constraint constraint, Collection<VertexPotential> resolveCandidates) {
        for (VertexPotential candidate : new ArrayList<>(resolveCandidates)) {
            candidate.setResolution(constraint);
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
        Collection<VertexPotential> minimumCandidates = new ArrayList<>();
        Iterator<VertexPotential> iterator = resolvedPotentials.iterator();
        VertexPotential first = iterator.next();
        minimumCandidates.add(first);
        double minimum = resolveValue(first);
        while (iterator.hasNext()) {
            VertexPotential candidate = iterator.next();
            double dimensionValue = resolveValue(candidate);
            if (dimensionValue < minimum) {
                minimumCandidates.clear();
                minimumCandidates.add(candidate);
                minimum = dimensionValue;
            }
            else if (dimensionValue == minimum) {
                minimumCandidates.add(candidate);
            }
        }
        resolvedPotentials.removeAll(minimumCandidates);
        vertex.addConstraints(newDimension, minimumCandidates.stream().map(VertexPotential::getConstraint).collect(Collectors.toList()));
        for (VertexPotential potential : minimumCandidates) {
            vertex.addConstraint(potential.getOldDimension(), potential.getResolution());
        }
        for (VertexPotential potential : resolvedPotentials) {
            vertex.addConstraint(potential.getOldDimension(), potential.getConstraint());
        }
    }

    private double resolveValue(VertexPotential first) {
        double knownValue = vertex.calculateCoordinate(first.getOldDimension(), first.getResolution());
        return first.getConstraint()
                .getOtherValue(first.getOldDimension(), knownValue);
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
