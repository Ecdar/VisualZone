package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.*;
import ZoneVisualizer.GraphicalElements.Vector3;
import ZoneVisualizer.GraphicalElements.WorldPolygon;
import ZoneVisualizer.Utility.LINQ;
import com.sun.deploy.util.ArrayUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Zone {

    protected List<Map<Clock, Double>> vertices;
    protected Map<Constraint, Face> faces;

    public Zone(Collection<Constraint> constraints, Collection<Clock> clocks) {
        ConstraintZone constraintZone = new ConstraintZone(constraints);
        vertices = new ArrayList<>();
        faces = new HashMap<>();
        if (constraintZone.isRestrictedToEmptiness()) {
            return;
        }

        Set<Constraint> origin = new HashSet<>();
        for (Clock clock : clocks) {
            Constraint c = constraintZone.getMinConstraint(clock);
            if (c != null) {
                origin.add(c);
            }
            else {
                origin.add(constraintZone.getTCConstraintBySecondary(clock));
            }
        }

        List<Set<Constraint>> foundVertices = new ArrayList<>();
        TreeMap<Set<Constraint>, Constraint> verticesWithNewestConstraint = new TreeMap<>();
        verticesWithNewestConstraint.put(origin, null);
        foundVertices.add(origin);

        while (!verticesWithNewestConstraint.isEmpty()) {
            Map.Entry<Set<Constraint>, Constraint> vertexToExpandFrom = verticesWithNewestConstraint.firstEntry();
            for (Constraint c : new ArrayList<>(vertexToExpandFrom.getKey())) {
                if (c == vertexToExpandFrom.getValue()) {
                    continue;
                }
                if (clocks.stream().noneMatch(clock -> c.getNormalComponent(clock) > 0)) {
                    //Todo Should catch all constraints that won't make a value grater by being removed
                    //(or ignore? only helps performance really)
                    continue;
                }
                Set<Constraint> nextVertex = new HashSet<>(vertexToExpandFrom.getKey());
                nextVertex.remove(c);

                Constraint replacingConstraint = null;
                //Todo find constraint to replace c with

                if (!foundVertices.contains(nextVertex)) {
                    foundVertices.add(nextVertex);
                    verticesWithNewestConstraint.put(nextVertex, replacingConstraint);
                }
            }


            verticesWithNewestConstraint.remove(vertexToExpandFrom.getKey());
        }




        //Old method (doesn't handle two clock constraints)
        List<Clock> tempClocks = new ArrayList<>(clocks);
        Map<Clock, Constraint> chosenConstraints = new HashMap<>();
        findVerticesForClocks(chosenConstraints, tempClocks, constraintZone);
    }

    private Constraint findNextConstraintToExpandAlong(Collection<Clock> dimensionsToExpand, Iterator<Constraint> constraintIterator) {
        Constraint c;
        while (constraintIterator.hasNext()) {
            c = constraintIterator.next();
            if (c.getNormalComponent()) {
                return c;
            }
        }
        return null;
    }

    private void findVerticesForClocks(Map<Clock, Constraint> chosenConstraints,
                                       List<Clock> remainingClocks, ConstraintZone constraintZone) {
        if (remainingClocks.isEmpty()) {
            addVertex(chosenConstraints);
            return;
        }
        Clock clock = remainingClocks.get(0);
        remainingClocks.remove(clock);

        Constraint chosenConstraint;
        TwoClockConstraint tcConstraint;

        //Handle right and bottom sides of rectangle
        tcConstraint = constraintZone.getTCConstraint(clock);
        if (tcConstraint == null ||
                (tcConstraint.getRestrictionType() != TwoClockRestrictionType.CutOfBottomAndRightSide
                && tcConstraint.getRestrictionType() != TwoClockRestrictionType.CutOfRightSide)) {
            //Right flat side
            chosenConstraint = constraintZone.getMaxConstraint(clock);
            if (chosenConstraint == null) {
                chosenConstraint = new SingleClockConstraint
                        (Inequality.LessThan, false, Double.POSITIVE_INFINITY, clock);
            }
            chosenConstraints.put(clock, chosenConstraint);
            findVerticesForClocks(chosenConstraints, remainingClocks, constraintZone);
        }
        if (tcConstraint != null &&
                (tcConstraint.getRestrictionType() == TwoClockRestrictionType.CutOfBottomAndRightSide
                || tcConstraint.getRestrictionType() == TwoClockRestrictionType.CutOfRightSide
                || tcConstraint.getRestrictionType() == TwoClockRestrictionType.CutOfNothing)) {
            //Right/bottom slanted line crosses either top or bottom side (or both)
            chosenConstraints.put(clock, tcConstraint);
            findVerticesForClocks(chosenConstraints, remainingClocks, constraintZone);
        }

        //Handle left and top of rectangle
        tcConstraint = constraintZone.getTCConstraintBySecondary(clock);
        if (tcConstraint == null ||
                (tcConstraint.getRestrictionType() != TwoClockRestrictionType.CutOfBottomAndRightSide
                && tcConstraint.getRestrictionType() != TwoClockRestrictionType.CutOfBottom)) {
            //Left flat side
            chosenConstraint = constraintZone.getMinConstraint(clock);
            if (chosenConstraint == null) {
                chosenConstraint = new SingleClockConstraint
                        (Inequality.GreaterThan, false, 0, clock);
            }
            chosenConstraints.put(clock, chosenConstraint);
            findVerticesForClocks(chosenConstraints, remainingClocks, constraintZone);
        }
        //todo handle left/top slanted line across top or bottom (or both)

        remainingClocks.add(clock);
    }

    private void addVertex(Map<Clock, Constraint> constraintMap) {
        Map<Clock, Double> vertex = new HashMap<>();
        for (Map.Entry<Clock, Constraint> constraintEntry : constraintMap.entrySet()){
            vertex.put(constraintEntry.getKey(), constraintEntry.getValue().getnValue());
        }
        vertices.add(vertex);
        int index = vertices.size() - 1;
        for (Constraint constraint : constraintMap.values()) {
            addVertexToFace(index, constraint);
        }
    }

    private void addVertexToFace(int vertexIndex, Constraint constraint) {
        if (!faces.containsKey(constraint)) {
            faces.put(constraint, new Face(constraint));
        }
        faces.get(constraint).addVertexIndex(vertexIndex);
    }

    public WorldPolygon projectTo2DMesh(Clock dimension1, Clock dimension2) {
        List<Vector3> projectedVertices = new ArrayList<>();

        for (Map<Clock, Double> vertex : vertices) {
            Vector3 projectedVertex = new Vector3(vertex.get(dimension1), vertex.get(dimension2), 0);
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

        for (Map<Clock, Double> vertex : vertices) {
            Vector3 projectedVertex = new Vector3(vertex.get(dimension1), vertex.get(dimension2), vertex.get(dimension3));
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
                    .map(v -> new Vector3(v.get(dimension1), v.get(dimension2), v.get(dimension3)))
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
