package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.*;
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
        Map<Clock, BackedUpValue<Constraint, Double>> chosenConstraints = new HashMap<>();
        findVerticesForClocks(chosenConstraints, tempClocks, constraintZone);
    }

    private void findVerticesForClocks(Map<Clock, BackedUpValue<Constraint, Double>> chosenConstraints,
                                       List<Clock> remainingClocks, ConstraintZone constraintZone) {
        if (remainingClocks.isEmpty()) {
            addVertex(chosenConstraints);
            return;
        }
        Clock clock = remainingClocks.get(0);
        remainingClocks.remove(clock);
        chosenConstraints.put(clock,
                new BackedUpValue<>(constraintZone.getMinConstraint(clock), Double.valueOf(0)));
        findVerticesForClocks(chosenConstraints, remainingClocks, constraintZone);

        chosenConstraints.put(clock,
                new BackedUpValue<>(constraintZone.getMaxConstraint(clock), Double.POSITIVE_INFINITY));
        findVerticesForClocks(chosenConstraints, remainingClocks, constraintZone);
        remainingClocks.add(clock);
    }

    private void addVertex(Map<Clock, BackedUpValue<Constraint, Double>> constraintMap) {
        Map<Clock, Double> vertex = new HashMap<>();
        for (Map.Entry<Clock, BackedUpValue<Constraint, Double>> constraintEntry : constraintMap.entrySet()){
            if (constraintEntry.getValue().isNull()) {
                vertex.put(constraintEntry.getKey(), constraintEntry.getValue().getBackupValue());
            }
            else {
                vertex.put(constraintEntry.getKey(), constraintEntry.getValue().getValue().getnValue());
            }
        }
        vertices.add(vertex);
        int index = vertices.size() - 1;
        for (BackedUpValue<Constraint, Double> constraint : constraintMap.values()) {
            if (!constraint.isNull()) {
                addVertexToFace(index, constraint.getValue());
            }
        }
    }

    private void addVertexToFace(int vertexIndex, Constraint constraint) {
        if (!faces.containsKey(constraint)) {
            faces.put(constraint, new Face());
        }
        faces.get(constraint).addVertexIndex(vertexIndex);
    }

    public WorldPolygon projectTo2DMesh(Clock dimension1, Clock dimension2) {
        double[][] projectedVertices = new double[vertices.size()][3];

        for (int i = 0; i < vertices.size(); i++) {
            Map<Clock, Double> vertex = vertices.get(i);
            projectedVertices[i][0] = vertex.get(dimension1);
            projectedVertices[i][1] = vertex.get(dimension2);
        }
        Double minX = Arrays.asList(projectedVertices).stream()
                .map(doubles -> doubles[0]).min(Double::compare).get();
        Double maxX = Arrays.asList(projectedVertices).stream()
                .map(doubles -> doubles[0]).max(Double::compare).get();
        Double minY = Arrays.asList(projectedVertices).stream()
                .map(doubles -> doubles[1]).min(Double::compare).get();
        Double maxY = Arrays.asList(projectedVertices).stream()
                .map(doubles -> doubles[1]).max(Double::compare).get();

        List<Double> vertList = Arrays.asList(projectedVertices).stream()
                .filter(v -> v[0] == minX || v[0] == maxX || v[1] == minY || v[1] == maxY)
                .flatMap(v -> Arrays.stream(v).boxed())
                .collect(Collectors.toList());

        float[] polygonVertices = new float[vertList.size()];
        for (int i = 0; i < vertList.size(); i++) {
            polygonVertices[i] = (float)vertList.get(i).doubleValue();
        }

        return new WorldPolygon(polygonVertices);
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
