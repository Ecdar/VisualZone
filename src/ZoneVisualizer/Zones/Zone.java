package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.*;
import ZoneVisualizer.GraphicalElements.Vector3;
import ZoneVisualizer.GraphicalElements.WorldPolygon;
import ZoneVisualizer.Utility.BackedUpValue;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

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

        List<Clock> tempClocks = new ArrayList<>(clocks);
        Map<Clock, Constraint> chosenConstraints = new HashMap<>();
        findVerticesForClocks(chosenConstraints, tempClocks, constraintZone);
    }

    private void findVerticesForClocks(Map<Clock, Constraint> chosenConstraints,
                                       List<Clock> remainingClocks, ConstraintZone constraintZone) {
        if (remainingClocks.isEmpty()) {
            addVertex(chosenConstraints);
            return;
        }
        Clock clock = remainingClocks.get(0);
        remainingClocks.remove(clock);
        Constraint chosenConstraint = constraintZone.getMinConstraint(clock);
        if (chosenConstraint == null) {
            chosenConstraint = new SingleClockConstraint
                    (Inequality.GreaterThan, false, 0, clock);
        }
        chosenConstraints.put(clock, chosenConstraint);
        findVerticesForClocks(chosenConstraints, remainingClocks, constraintZone);

        chosenConstraint = constraintZone.getMaxConstraint(clock);
        if (chosenConstraint == null) {
            chosenConstraint = new SingleClockConstraint
                    (Inequality.LessThan, false, Double.POSITIVE_INFINITY, clock);
        }
        chosenConstraints.put(clock, chosenConstraint);
        findVerticesForClocks(chosenConstraints, remainingClocks, constraintZone);
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
            faces.put(constraint, new Face());
        }
        faces.get(constraint).addVertexIndex(vertexIndex);
    }

    public WorldPolygon projectTo2DMesh(Clock dimension1, Clock dimension2) {
        List<Vector3> projectedVertices = new ArrayList<>();

        for (int i = 0; i < vertices.size(); i++) {
            Map<Clock, Double> vertex = vertices.get(i);
            Vector3 vectorVertice = new Vector3(vertex.get(dimension1), vertex.get(dimension2), 0);
            if (!projectedVertices.contains(vectorVertice)) {
                projectedVertices.add(vectorVertice);
            }
        }
        Double minX = projectedVertices.stream()
                .map(vertice -> vertice.x).min(Double::compare).get();
        Double maxX = projectedVertices.stream()
                .map(vertice -> vertice.x).max(Double::compare).get();
        Double minY = projectedVertices.stream()
                .map(vertice -> vertice.y).min(Double::compare).get();
        Double maxY = projectedVertices.stream()
                .map(vertice -> vertice.y).max(Double::compare).get();

        List<Vector3> hullVertices = projectedVertices.stream()
                .filter(v -> v.x == minX || v.x == maxX || v.y == minY || v.y == maxY)
                .collect(Collectors.toList());

        return new WorldPolygon(hullVertices, Vector3.back());
    }

    public List<WorldPolygon> projectTo3DMesh(Clock dimension1, Clock dimension2, Clock dimension3) {
        List<WorldPolygon> projectedPolygons = new ArrayList<>();
        //Todo project to 3D space

        return projectedPolygons;
    }

    protected class Face {
        private List<Integer> verticeIndices = new ArrayList<>();

        public List<Integer> getVerticeIndices() {
            return verticeIndices;
        }

        public void addVertexIndex(Integer index) {
            verticeIndices.add(index);
        }
    }
}
