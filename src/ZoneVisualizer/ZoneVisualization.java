package ZoneVisualizer;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.GraphicalElements.Vector3;
import ZoneVisualizer.GraphicalElements.WorldPolygon;
import ZoneVisualizer.Zones.ConstraintZone;
import ZoneVisualizer.Zones.Zone;
import javafx.scene.shape.Shape3D;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        WorldPolygon projectedZone = zone.projectTo2DMesh(currentClockDimensions.get(0), currentClockDimensions.get(1));
        ZoneVisualizationApp.set3DContent(projectedZone);
        ZoneVisualizationApp.setCamera2D(projectedZone.getTransform().getPositionReadonly(),
                currentClockDimensions.get(0), currentClockDimensions.get(1));
    }

    private static void threeClocksSetup() {
        ZoneVisualizationApp.disableRemainingClockDimensions(true);
        List<WorldPolygon> projectedZoneFaces = zone.projectTo3DMesh(
                currentClockDimensions.get(0), currentClockDimensions.get(1), currentClockDimensions.get(2));
        ZoneVisualizationApp.set3DContent(projectedZoneFaces);
        Vector3 center = new Vector3();
        List<Vector3> facePositions = projectedZoneFaces.stream()
                .map(f -> f.getTransform().getPositionReadonly()).collect(Collectors.toList());
        center.x = facePositions.stream().collect(Collectors.averagingDouble((Vector3 p) -> p.x));
        center.y = facePositions.stream().collect(Collectors.averagingDouble((Vector3 p) -> p.y));
        center.z = facePositions.stream().collect(Collectors.averagingDouble((Vector3 p) -> p.z));
        ZoneVisualizationApp.setCamera3D(center,
                currentClockDimensions.get(0), currentClockDimensions.get(1), currentClockDimensions.get(2));
    }
}
