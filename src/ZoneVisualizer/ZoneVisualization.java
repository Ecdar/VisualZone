package ZoneVisualizer;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;

import java.util.ArrayList;
import java.util.List;

public class ZoneVisualization {

    private static ArrayList<Clock> clocks = new ArrayList<>();
    private static ArrayList<Constraint> constraints = new ArrayList<>();

    private static ArrayList<Clock> currentClockDimensions = new ArrayList<>();

    public static void initialize(List<Clock> clocklist, List<Constraint> constraintList) {
        clocks.clear();
        constraints.clear();
        clocks.addAll(clocklist);
        constraints.addAll(constraintList);

        Main.setClockDimensions(clocks);
    }

    public static void chooseClockDimension(Clock clock) {
        currentClockDimensions.add(clock);
        if (currentClockDimensions.size() >= 3) {
            Main.disableRemainingClockDimensions(true);
        }
    }

    public static void removeClockDimension(Clock clock) {
        int prevSize = currentClockDimensions.size();
        currentClockDimensions.remove(clock);
        if (prevSize >= 3 && currentClockDimensions.size() < 3) {
            Main.disableRemainingClockDimensions(false);
        }
    }
}
