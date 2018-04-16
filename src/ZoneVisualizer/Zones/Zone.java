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
        vertices.add(origin);

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
                twoClockConstraints.removeIf(tcc -> tcc.getnValue() <= oldTcc.getnValue());
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
            pivotResult.addMissingConstraints(missingDimension, minMaxConstraints);
        }

        if (!vertices.contains(pivotResult.getVertex())) {
            vertices.add(pivotResult.getVertex());
            int index = vertices.size() - 1;
            for (Constraint constraint : pivotResult.getVertex().getAllConstraints()) {
                addVertexToFace(index, constraint);
            }
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

    public WorldPolygon projectTo2DMesh(Clock dimension1, Clock dimension2) {
        List<Vector3> projectedVertices = new ArrayList<>();

        for (Vertex vertex : vertices) {
            Vector3 projectedVertex = new Vector3(vertex.getCoordinate(dimension1), vertex.getCoordinate(dimension2), 0);
            if (!projectedVertices.contains(projectedVertex)) {
                projectedVertices.add(projectedVertex);
            }
        }
        List<Vector3> hullVertices = getHullVerticesOfZPlane(projectedVertices);

        return new WorldPolygon(hullVertices, Vector3.back());
    }

    public List<WorldPolygon> projectTo3DMesh(Clock dimension1, Clock dimension2, Clock dimension3) {
        List<WorldPolygon> projectedPolygons = new ArrayList<>();
        List<Vector3> projectedVertices = new ArrayList<>();

        for (Vertex vertex : vertices) {
            Vector3 projectedVertex = new Vector3(vertex.getCoordinate(dimension1),
                    vertex.getCoordinate(dimension2), vertex.getCoordinate(dimension3));
            if (!projectedVertices.contains(projectedVertex)) {
                projectedVertices.add(projectedVertex);
            }
        }
        //Find the single clock constraint faces (non-tilted faces)
        //(Inefficient? Traverses vertices a ton of times. Could use faces instead)
        Double minX = getMinFromMappedValues(projectedVertices, vertex -> vertex.x);
        Double maxX = getMaxFromMappedValues(projectedVertices, vertex -> vertex.x);
        Double minY = getMinFromMappedValues(projectedVertices, vertex -> vertex.y);
        Double maxY = getMaxFromMappedValues(projectedVertices, vertex -> vertex.y);
        Double minZ = getMinFromMappedValues(projectedVertices, vertex -> vertex.z);
        Double maxZ = getMaxFromMappedValues(projectedVertices, vertex -> vertex.z);

        List<Vector3> planeVertices = projectedVertices.stream()
                .filter(v -> v.x == minX)
                .collect(Collectors.toList());
        List<Vector3> hullVertices = getHullVerticesOfXPlane(planeVertices);
        WorldPolygon polygon = new WorldPolygon(hullVertices, Vector3.left());
        projectedPolygons.add(polygon);

        planeVertices = projectedVertices.stream()
                .filter(v -> v.x == maxX)
                .collect(Collectors.toList());
        hullVertices = getHullVerticesOfXPlane(planeVertices);
        projectedPolygons.add(new WorldPolygon(hullVertices, Vector3.right()));

        planeVertices = projectedVertices.stream()
                .filter(v -> v.y == minY)
                .collect(Collectors.toList());
        List<Vector3> yMinHullVertices = getHullVerticesOfYPlane(planeVertices);
        projectedPolygons.add(new WorldPolygon(yMinHullVertices, Vector3.down()));

        planeVertices = projectedVertices.stream()
                .filter(v -> v.y == maxY)
                .collect(Collectors.toList());
        hullVertices = getHullVerticesOfYPlane(planeVertices);
        projectedPolygons.add(new WorldPolygon(hullVertices, Vector3.up()));

        planeVertices = projectedVertices.stream()
                .filter(v -> v.z == minZ)
                .collect(Collectors.toList());
        hullVertices = getHullVerticesOfZPlane(planeVertices);
        projectedPolygons.add(new WorldPolygon(hullVertices, Vector3.back()));

        planeVertices = projectedVertices.stream()
                .filter(v -> v.z == maxZ)
                .collect(Collectors.toList());
        hullVertices = getHullVerticesOfZPlane(planeVertices);
        projectedPolygons.add(new WorldPolygon(hullVertices, Vector3.forward()));

        //Finds the two clock constraint faces (tilted faces)
        //(Doesn't reduce to hull vertices, so there will be more triangles than necessary)
        for (Map.Entry<Constraint, Face> face : faces.entrySet()) {
            if (face.getKey() instanceof TwoClockConstraint) {
                TwoClockConstraint tcConstraint = (TwoClockConstraint)face.getKey();
                if (isTCConstraintOfClocks(tcConstraint, dimension1, dimension2, dimension3)) {
                    projectedPolygons.add(face.getValue().project(dimension1, dimension2, dimension3));
                }
            }
        }

        return projectedPolygons;
    }

    private List<Vector3> getHullVerticesOfXPlane(List<Vector3> projectedVertices) {
        Double minY = getMinFromMappedValues(projectedVertices, vertex -> vertex.y);
        Double maxY = getMaxFromMappedValues(projectedVertices, vertex -> vertex.y);
        Double minZ = getMinFromMappedValues(projectedVertices, vertex -> vertex.z);
        Double maxZ = getMaxFromMappedValues(projectedVertices, vertex -> vertex.z);

        return projectedVertices.stream()
                .filter(v -> v.y == minY || v.y == maxY || v.z == minZ || v.z == maxZ)
                .collect(Collectors.toList());
    }

    private List<Vector3> getHullVerticesOfYPlane(List<Vector3> projectedVertices) {
        Double minX = getMinFromMappedValues(projectedVertices, vertex -> vertex.x);
        Double maxX = getMaxFromMappedValues(projectedVertices, vertex -> vertex.x);
        Double minZ = getMinFromMappedValues(projectedVertices, vertex -> vertex.z);
        Double maxZ = getMaxFromMappedValues(projectedVertices, vertex -> vertex.z);

        return projectedVertices.stream()
                .filter(v -> v.x == minX || v.x == maxX || v.z == minZ || v.z == maxZ)
                .collect(Collectors.toList());
    }

    private List<Vector3> getHullVerticesOfZPlane(List<Vector3> projectedVertices) {
        Double minX = getMinFromMappedValues(projectedVertices, vertex -> vertex.x);
        Double maxX = getMaxFromMappedValues(projectedVertices, vertex -> vertex.x);
        Double minY = getMinFromMappedValues(projectedVertices, vertex -> vertex.y);
        Double maxY = getMaxFromMappedValues(projectedVertices, vertex -> vertex.y);

        return projectedVertices.stream()
                .filter(v -> v.x == minX || v.x == maxX || v.y == minY || v.y == maxY)
                .collect(Collectors.toList());
    }

    private Double getMinFromMappedValues(List<Vector3> projectedVertices, Function<? super Vector3, ? extends Double> mapper) {
        return projectedVertices.stream().map(mapper).min(Double::compare).get();
    }

    private Double getMaxFromMappedValues(List<Vector3> projectedVertices, Function<? super Vector3, ? extends Double> mapper) {
        return projectedVertices.stream().map(mapper).max(Double::compare).get();
    }

    private boolean isTCConstraintOfClocks(TwoClockConstraint tcConstraint, Clock c1, Clock c2, Clock c3) {
        Clock clock1 = tcConstraint.getClock1(), clock2 = tcConstraint.getClock2();
        return  (clock1 == c1 || clock1 == c2 || clock1 == c3) &&
                (clock2 == c1 || clock2 == c2 || clock2 == c3);
    }

    protected class Face {
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
