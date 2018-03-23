package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.*;
import ZoneVisualizer.GraphicalElements.WorldPolygon;

import java.util.*;

public class Zone {

    protected double[][] vertices;
    protected Map<Constraint, Face> faces;

    public Zone(Collection<Constraint> constraints, Collection<Clock> clocks) {
        ConstraintZone constraintZone = new ConstraintZone(constraints);
        if (constraintZone.isRestrictedToEmptiness()) {
            vertices = new double[0][];
            faces = new HashMap<>();
            return;
        }
        //Todo find points where constraints cross each other
        faces = new HashMap<>();
        List<SingleClockConstraint> tempMinBounds = new ArrayList<>(constraintZone.getMinConstraints());
        List<SingleClockConstraint> tempMaxBounds = new ArrayList<>(constraintZone.getMaxConstraints());
        List<TwoClockConstraint> tempTCConstraints = new ArrayList<>(constraintZone.getTCConstraints());

        for (SingleClockConstraint scC : tempMinBounds) {
            for (SingleClockConstraint scC2 : tempMaxBounds) {

            }
        }

        List<Clock> tempClocks = new ArrayList<>(clocks);
        findVerticesForClocks(tempClocks, constraintZone);
    }

    private void findVerticesForClocks(List<Clock> remainingClocks, ConstraintZone constraintZone) {
        if (remainingClocks.size() > 0) {

        }
    }

    public WorldPolygon projectTo2DMesh() {
        double[][] projectedVertices = new double[vertices.length][3];
        //Todo project vertices to 2D (and find order? for polygon line)
        for (Map.Entry<Constraint, Face> face : faces.entrySet()) {

        }
        float[] polygonVertices = new float[6];

        return new WorldPolygon(polygonVertices);
    }

    public List<WorldPolygon> projectTo3DMesh() {
        List<WorldPolygon> projectedPolygons = new ArrayList<>();
        //Todo project to 3D space

        return projectedPolygons;
    }

    protected class Face {
        private int[] verticeIndexes;

        public int[] getVerticeIndexes() {
            return verticeIndexes;
        }
    }
}
