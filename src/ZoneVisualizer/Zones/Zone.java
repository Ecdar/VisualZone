package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.*;

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

    }

    protected class Face {
        private int[] verticeIndexes;
    }
}
