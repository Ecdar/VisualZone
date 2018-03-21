package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.*;
import ZoneVisualizer.Utility.LINQ;

import java.util.*;
import java.util.stream.Collectors;

public class Zone {

    private double[][] vertices;
    private Face[] faces;

    public Zone(Collection<Constraint> constraints, Collection<Clock> clocks) {
        ArrayList<double[]> verts = new ArrayList<>();

        ConstraintReducer constraintReducer = new ConstraintReducer(constraints);

        vertices = verts.toArray(vertices);
    }


    private class Face {
        private int[] verticeIndexes;
    }
}
