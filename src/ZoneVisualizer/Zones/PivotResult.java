package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.Constraints.SingleClockConstraint;
import ZoneVisualizer.Constraints.TwoClockConstraint;
import ZoneVisualizer.Utility.LINQ;

import java.util.*;
import java.util.stream.Collectors;

public class PivotResult {

    private Vertex vertex;
    private List<Clock> missingDimensions;
    private Collection<VertexPotential> vertexPotentials;

    private final Map<Clock, Collection<VertexPotential>> resolutionCandidates = new HashMap<>();
    private final Map<Clock, Collection<VertexPotential>> reversionCandidates = new HashMap<>();
    private final Map<Clock, Collection<VertexPotential>> additionCandidates = new HashMap<>();

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

    public void addMissingConstraint(Clock dimension, Constraint... constraints) {
        addMissingConstraints(dimension, Arrays.asList(constraints));
    }

    public void addMissingConstraints(Clock dimension, Collection<? extends Constraint> constraints) {
        missingDimensions.remove(dimension);
        Collection<VertexPotential> resolveCandidates = vertexPotentials.stream()
                .filter(p -> p.getOldDimension() == dimension)
                .collect(Collectors.toList());
        if (resolveCandidates.isEmpty()) {
            Collection<VertexPotential> resolveReversionCandidates = getAllReversionCandidates().stream()
                    .filter(c -> c.getNewDimension() == dimension)
                    .collect(Collectors.toList());
            if (resolveReversionCandidates.isEmpty()) {
                vertex.addConstraints(dimension, constraints);
            }
            else {
                resolveReversionsWithConstraints(constraints, resolveReversionCandidates);
            }
            return;
        }
        Constraint first = LINQ.first(constraints);
        if (Double.isFinite(first.getnValue())) {
            if (first instanceof TwoClockConstraint) {
                TwoClockConstraint tcc = (TwoClockConstraint)first;
                Clock otherClock = tcc.getOtherClock(dimension);
                Collection<Constraint> edgeConstraints = vertex.getConstraints(otherClock);
                if (!LINQ.first(edgeConstraints).isLowerBoundOnDimension(otherClock)) {
                    Collection<VertexPotential> potentials = new ArrayList<>();
                    potentials.addAll(constraints.stream()
                            .map(c -> (TwoClockConstraint)c)
                            .map(c -> new VertexPotential(c, dimension, c.getOtherClock(dimension)))
                            .collect(Collectors.toList()));
                    potentials.forEach(p -> p.setResolution(edgeConstraints));
                    potentials.addAll(resolveCandidates);
                    vertexPotentials.removeAll(resolveCandidates);
                    return;
                }
            }
            resolveWithConstraints(constraints, resolveCandidates);
            resolveReversionsWithConstraints(dimension, constraints);
            return;
        }

        reversionCandidates.put(dimension, resolveCandidates);
        vertexPotentials.removeAll(resolveCandidates);
        for (VertexPotential candidate : resolveCandidates) {
            Clock missingDimension = candidate.getNewDimension();
            Collection<Constraint> foundConstraints = vertex.getConstraints(missingDimension);
            if (!foundConstraints.isEmpty()) {
                //Dimension has already been found
                candidate.setResolution(foundConstraints);
                continue;
            }
            if (!missingDimensions.contains(missingDimension)) {
                missingDimensions.add(missingDimension);
            }
        }
        if (reversionResolutionReady(dimension)) {
            resolveDimension(dimension, reversionCandidates);
        }
    }

    private void resolveWithConstraints(Collection<? extends Constraint> constraints, Collection<VertexPotential> resolveCandidates) {
        for (VertexPotential candidate : new ArrayList<>(resolveCandidates)) {
            candidate.setResolution(constraints);
            vertexPotentials.remove(candidate);
            Clock newDimension = candidate.getNewDimension();
            LINQ.addToDeepMap(resolutionCandidates, newDimension, candidate);
            if (candidatesResolutionReady(newDimension)) {
                resolveDimension(newDimension, resolutionCandidates);
            }
        }
    }

    private void resolveReversionsWithConstraints(Clock dimension, Collection<? extends Constraint> constraints) {
        Collection<VertexPotential> resolveDeniedCandidates = getAllReversionCandidates().stream()
                .filter(c -> c.getNewDimension() == dimension)
                .collect(Collectors.toList());
        resolveReversionsWithConstraints(constraints, resolveDeniedCandidates);
    }

    private void resolveReversionsWithConstraints(Collection<? extends Constraint> constraints,
                                                  Collection<VertexPotential> resolveDeniedPotentials) {
        for (VertexPotential deniedCandidate : resolveDeniedPotentials) {
            deniedCandidate.setResolution(constraints);
            Clock oldDimension = deniedCandidate.getOldDimension();
            if (reversionResolutionReady(oldDimension)) {
                resolveDimension(oldDimension, reversionCandidates);
            }
        }
    }

    private Collection<VertexPotential> getAllReversionCandidates() {
        return reversionCandidates.values().stream().flatMap(c -> c.stream()).collect(Collectors.toList());
    }

    private void resolveDimension(Clock dimension, Map<Clock, Collection<VertexPotential>> candidatesToFinish) {
        Collection<VertexPotential> resolvedPotentials = candidatesToFinish.remove(dimension);
        //Might be a problem here? If there is a lower bound parallel to the potential
        //Todo there is a problem here when there are multiple potentials and a resolution is also a TCC
        Collection<VertexPotential> minimumCandidates = LINQ.getMinimums(resolvedPotentials, p -> resolveValue(p, dimension));
        resolvedPotentials.removeAll(minimumCandidates);
        vertex.addConstraints(dimension, minimumCandidates.stream().map(VertexPotential::getConstraint).collect(Collectors.toList()));
        for (VertexPotential potential : minimumCandidates) {
            vertex.addConstraints(potential.getOtherDimension(dimension), potential.getResolution());
        }
        for (VertexPotential potential : resolvedPotentials) {
            //Todo unless dimension is handled
            vertex.addConstraint(potential.getOtherDimension(dimension), potential.getConstraint());
        }
    }

    private double resolveValue(VertexPotential potential, Clock dimension) {
        Clock knownDimension = potential.getOtherDimension(dimension);
        double knownValue = vertex.calculateCoordinate(knownDimension, LINQ.first(potential.getResolution()));
        return potential.getConstraint()
                .getOtherValue(knownDimension, knownValue);
    }

    private boolean candidatesResolutionReady(Clock newDimension) {
        return vertexPotentials.stream().noneMatch(p -> p.getNewDimension() == newDimension);
    }

    private boolean reversionResolutionReady(Clock dimension) {
        if (!reversionCandidates.containsKey(dimension)) {
            return false;
        }
        return reversionCandidates.get(dimension).stream().allMatch(c -> c.getResolution() != null);
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
