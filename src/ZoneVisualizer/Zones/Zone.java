package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.*;
import ZoneVisualizer.GraphicalElements.WorldPolygon;

import java.util.*;

public class Zone {

    protected double[][] vertices;
    protected Face[] faces;

    public Zone(Collection<Constraint> constraints, Collection<Clock> clocks) {
        ConstraintZone constraintZone = new ConstraintZone(constraints);
        if (constraintZone.isRestrictedToEmptiness()) {
            vertices = new double[0][];
            faces = new Face[0];
            return;
        }
        //Todo find points where constraints cross each other
    }

    public WorldPolygon projectTo2DMesh() {
        double[][] projectedVertices = new double[vertices.length][2];
        //Todo project vertices to 2D (and find order? for polygon line)
        for (Face face : faces) {

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
        private Constraint constraint;
        private int[] verticeIndexes;

        public int[] getVerticeIndexes() {
            return verticeIndexes;
        }
    }
}
