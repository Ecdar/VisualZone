package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.*;

import java.util.*;

public class Zone {

    protected double[][] vertices;
    protected Face[] faces;

    protected class Face {
        private int[] verticeIndexes;
    }
}
