package ZoneVisualizer;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.GraphicalElements.Vector3;
import ZoneVisualizer.GraphicalElements.WorldPolygon;
import ZoneVisualizer.Views.ZoneVisualizationApp;
import ZoneVisualizer.Zones.Projector;
import ZoneVisualizer.Zones.Projector2D;
import ZoneVisualizer.Zones.Projector3D;
import ZoneVisualizer.Zones.Zone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ZoneVisualization {

    private static ArrayList<Clock> clocks = new ArrayList<>();
    private static ArrayList<Constraint> constraints = new ArrayList<>();
    private static double maximumValue;

    private static ArrayList<Clock> currentClockDimensions = new ArrayList<>();
    private static Zone zone;
    private static Collection<WorldPolygon> visualizedZone;

    public static void initialize(List<Clock> clockList, List<Constraint> constraintList, double maxValue) {
        clocks.clear();
        constraints.clear();
        currentClockDimensions.clear();
        clocks.addAll(clockList);
        constraints.addAll(constraintList);
        maximumValue = maxValue;

        zone = new Zone(constraintList, clockList, maxValue);

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
            ZoneVisualizationApp.setDisableRemainingClockDimensions(false);
        }
        if (currentClockDimensions.size() == 2) {
            twoClocksSetup();
        }
    }

    private static void twoClocksSetup() {
        Projector projector = new Projector2D(currentClockDimensions.get(0), currentClockDimensions.get(1), maximumValue);
        Vector3 center = find3DContentAndCenter(projector);
        ZoneVisualizationApp.setCamera2D(center,
                currentClockDimensions.get(0), currentClockDimensions.get(1));
    }

    private static void threeClocksSetup() {
        ZoneVisualizationApp.setDisableRemainingClockDimensions(true);
        Projector projector = new Projector3D(currentClockDimensions.get(0), currentClockDimensions.get(1), currentClockDimensions.get(2), maximumValue);
        Vector3 center = find3DContentAndCenter(projector);
        ZoneVisualizationApp.setCamera3D(center,
                currentClockDimensions.get(0), currentClockDimensions.get(1), currentClockDimensions.get(2));
    }

    private static Vector3 find3DContentAndCenter(Projector projector) {
        //Remove old visualization
        if (visualizedZone != null) {
            ZoneVisualizationApp.remove3DContent(visualizedZone);
        }
        //Show new visualization
        Collection<WorldPolygon> projectedZoneFaces = projector.project(zone);
        Collection<WorldPolygon> sceneContent = new ArrayList<>();
        projectedZoneFaces.forEach(face -> sceneContent.add(face.getBackFace()));
        sceneContent.addAll(projectedZoneFaces);
        ZoneVisualizationApp.add3DContent(sceneContent);
        visualizedZone = sceneContent;

        //Find center of projected content
        Vector3 center = new Vector3();
        List<Vector3> facePositions = projectedZoneFaces.stream()
                .map(f -> f.getTransform().getPositionReadonly()).collect(Collectors.toList());
        center.x = facePositions.stream().collect(Collectors.averagingDouble((Vector3 p) -> p.x));
        center.y = facePositions.stream().collect(Collectors.averagingDouble((Vector3 p) -> p.y));
        center.z = facePositions.stream().collect(Collectors.averagingDouble((Vector3 p) -> p.z));
        return center;
    }
}
