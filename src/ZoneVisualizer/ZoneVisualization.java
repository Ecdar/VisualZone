package ZoneVisualizer;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.GraphicalElements.Vector3;

import java.util.ArrayList;
import java.util.List;

public class ZoneVisualization {

    private static ArrayList<Clock> clocks = new ArrayList<>();
    private static ArrayList<Constraint> constraints = new ArrayList<>();

    private static ArrayList<Clock> currentClockDimensions = new ArrayList<>();

    public static void initialize(List<Clock> clockList, List<Constraint> constraintList) {
        clocks.clear();
        constraints.clear();
        clocks.addAll(clockList);
        constraints.addAll(constraintList);

        ZoneVisualizationApp.setClockDimensions(clocks);
    }

    public static void chooseClockDimension(Clock clock) {
        currentClockDimensions.add(clock);
        if (currentClockDimensions.size() >= 3) {
            ZoneVisualizationApp.disableRemainingClockDimensions(true);
            ZoneVisualizationApp.setCamera3D(new Vector3(5, 5, 5));
        }
        else if (currentClockDimensions.size() == 2) {
            twoClocksSetup();
        }
    }

    public static void removeClockDimension(Clock clock) {
        int prevSize = currentClockDimensions.size();
        currentClockDimensions.remove(clock);
        if (prevSize >= 3 && currentClockDimensions.size() < 3) {
            ZoneVisualizationApp.disableRemainingClockDimensions(false);
        }
        if (currentClockDimensions.size() == 2) {
            twoClocksSetup();
        }
    }

    private static void twoClocksSetup() {
        ZoneVisualizationApp.setCamera2D(new Vector3(5, 5, 0));
    }
}
