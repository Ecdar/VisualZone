package ZoneVisualizer;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.GraphicalElements.Vector3;
import ZoneVisualizer.Zones.ConstraintZone;
import ZoneVisualizer.Zones.Zone;
import javafx.scene.shape.Shape3D;

import java.util.ArrayList;
import java.util.List;

public class ZoneVisualization {

    private static ArrayList<Clock> clocks = new ArrayList<>();
    private static ArrayList<Constraint> constraints = new ArrayList<>();

    private static ArrayList<Clock> currentClockDimensions = new ArrayList<>();
    private static Zone zone;

    public static void initialize(List<Clock> clockList, List<Constraint> constraintList) {
        clocks.clear();
        constraints.clear();
        currentClockDimensions.clear();
        clocks.addAll(clockList);
        constraints.addAll(constraintList);

        zone = new Zone(constraintList, clockList);

        ZoneVisualizationApp.setClockDimensions(clocks);
        if (clocks.size() > 2) {
            currentClockDimensions.addAll(clocks.subList(0, 3));
            threeClocksSetup();
        }
        else {
            currentClockDimensions.addAll(clocks);
            if (currentClockDimensions.size() == 2) {
                twoClocksSetup();
            }
        }
    }

    public static void chooseClockDimension(Clock clock) {
        currentClockDimensions.add(clock);
        if (currentClockDimensions.size() == 3) {
            threeClocksSetup();
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
        ZoneVisualizationApp.set3DContent(zone.projectTo2DMesh());
    }

    private static void threeClocksSetup() {
        ZoneVisualizationApp.disableRemainingClockDimensions(true);
        ZoneVisualizationApp.setCamera3D(new Vector3(5, 5, 5));
        ZoneVisualizationApp.set3DContent(zone.projectTo3DMesh());
    }
}
