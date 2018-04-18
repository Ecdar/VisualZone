package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.*;
import ZoneVisualizer.GraphicalElements.Vector3;
import ZoneVisualizer.GraphicalElements.WorldPolygon;
import ZoneVisualizer.Utility.LINQ;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Zone {

    protected final List<Vertex> vertices;
    protected Map<Constraint, Face> faces;

    public Zone(Collection<Constraint> constraints, Collection<Clock> clocks) {
        ConstraintZone constraintZone = new ConstraintZone(constraints);
        vertices = new ArrayList<>();
        faces = new HashMap<>();
        if (constraintZone.isRestrictedToEmptiness()) {
            return;
        }

        Vertex origin = findOrigin(constraintZone, clocks);
        addVertex(origin);

        for (int i = 0; i < vertices.size(); i++) {
            Vertex pivot = vertices.get(i);

            for (Clock clock : clocks) {
                PivotResult pivotResult = pivot.pivot(clock);
                findMissingConstraintsAfterPivot(constraintZone, pivot, pivotResult);
            }
            if (pivot.isDegenerate()) {
                //Todo pivot along extra edges in degenerate case
            }
        }
    }

    private void findMissingConstraintsAfterPivot(ConstraintZone constraintZone, Vertex pivot, PivotResult pivotResult) {
        if (pivotResult == null) {
            return;
        }
        for (Clock missingDimension : pivotResult.getMissingDimensions()) {
            Collection<TwoClockConstraint> twoClockConstraints =
                    constraintZone.getTCConstraintByPrimary(missingDimension);
            Constraint oldConstraint = LINQ.first(pivot.getConstraints(missingDimension));
            if (oldConstraint instanceof TwoClockConstraint) {
                //Remove constraints that won't maximize dimension
                TwoClockConstraint oldTcc = (TwoClockConstraint)oldConstraint;
                if (oldTcc.getClock1() == missingDimension) {
                    twoClockConstraints.removeIf(tcc -> tcc.getnValue() <= oldTcc.getnValue());
                }
                //Might need an else here for multi TCC vertices
            }
            if (twoClockConstraints.isEmpty()) {
                pivotResult.addMissingConstraint(missingDimension, constraintZone.getMaxConstraint(missingDimension));
                continue;
            }
            Double minN = twoClockConstraints.stream()
                    .map(TwoClockConstraint::getnValue)
                    .min(Double::compareTo)
                    .get();
            Collection<Constraint> minMaxConstraints = twoClockConstraints.stream()
                    .filter(c -> c.getnValue() == minN).collect(Collectors.toList());
            TwoClockConstraint tcc = (TwoClockConstraint)LINQ.first(minMaxConstraints);
            double tccValue = tcc.getnValue() + pivotResult.getVertex().getCoordinate(tcc.getClock2());
            SingleClockConstraint scc = constraintZone.getMaxConstraint(missingDimension);
            if (tccValue < scc.getnValue() || (tccValue == scc.getnValue() && !tcc.isInclusive() && scc.isInclusive())) {
                pivotResult.addMissingConstraints(missingDimension, minMaxConstraints);
            }
            else {
                pivotResult.addMissingConstraint(missingDimension, scc);
            }
        }

        if (!vertices.contains(pivotResult.getVertex())) {
            addVertex(pivotResult.getVertex());
        }
    }

    private void addVertex(Vertex vertex) {
        vertices.add(vertex);
        int index = vertices.size() - 1;
        for (Constraint constraint : vertex.getAllConstraints()) {
            addVertexToFace(index, constraint);
        }
    }

    private Vertex findOrigin(ConstraintZone constraintZone, Collection<Clock> clocks) {
        Vertex origin = new Vertex(clocks);
        Collection<Clock> delayedDimensions = new ArrayList<>();
        for (Clock clock : clocks) {
            Constraint c = constraintZone.getMinConstraint(clock);
            if (c != null) {
                //Simple case; a greater than constraint exists for this dimension
                origin.addConstraint(clock, c);
            }
            else {
                delayedDimensions.add(clock);
            }
        }
        for (Clock clock : delayedDimensions) {
            Collection<TwoClockConstraint> twoClockMaxConstraints = constraintZone.getTCConstraintBySecondary(clock);
            if (!twoClockMaxConstraints.isEmpty()) {
                //There exists one or more two clock constraints bounding the minimum value of this dimension
                //Find the highest minimum bound. Can be several constraints if origin is degenerate
                double originNValue = twoClockMaxConstraints.stream()
                        .map(Constraint::getnValue)
                        .min(Double::compareTo).get();
                Collection<Constraint> maximizedMinBounds = twoClockMaxConstraints.stream()
                        .filter(tcc -> tcc.getnValue() == originNValue)
                        .collect(Collectors.toList());
                TwoClockConstraint tcc = (TwoClockConstraint)LINQ.first(maximizedMinBounds);
                double otherDimValue = 0;
                if (!delayedDimensions.contains(tcc.getClock1())) {
                    otherDimValue = origin.getCoordinate(tcc.getClock1());
                }
                if (tcc.getnValue() <= otherDimValue) {
                    origin.addConstraints(clock, maximizedMinBounds);
                    continue;
                }
            }
            //No bounds on this dimension. Add an implicit greater than 0 bound
            origin.addConstraint(clock, new SingleClockConstraint(Inequality.GreaterThan, true, 0, clock));
        }
        return origin;
    }

    private void addVertexToFace(int vertexIndex, Constraint constraint) {
        if (!faces.containsKey(constraint)) {
            faces.put(constraint, new Face(constraint));
        }
        faces.get(constraint).addVertexIndex(vertexIndex);
    }

    public List<Vertex> getVertices() {
        return  new ArrayList<>(vertices);
    }

    public Map<Constraint, Face> getFaces() {
        return new HashMap<>(faces);
    }

    public class Face {
        private final List<Integer> verticeIndices = new ArrayList<>();
        private final Constraint constraint;

        public Face(Constraint constraint) {
            this.constraint = constraint;
        }

        public WorldPolygon project(Clock dimension1, Clock dimension2, Clock dimension3) {
            List<Vector3> projectedVertices = verticeIndices.stream()
                    .map(i -> vertices.get(i))
                    .map(v -> new Vector3(v.getCoordinate(dimension1), v.getCoordinate(dimension2), v.getCoordinate(dimension3)))
                    .collect(Collectors.toList());
            Vector3 vNormal = constraint.getProjectedNormal(dimension1, dimension2, dimension3);

            return new WorldPolygon(projectedVertices, vNormal.multiply(-1));
        }

        public List<Integer> getVerticeIndices() {
            return verticeIndices;
        }

        public Constraint getConstraint() {
            return constraint;
        }

        public void addVertexIndex(Integer index) {
            verticeIndices.add(index);
        }
    }
}
