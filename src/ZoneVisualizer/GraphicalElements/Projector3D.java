package ZoneVisualizer.GraphicalElements;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.Constraints.TwoClockConstraint;
import ZoneVisualizer.Zones.Face;
import ZoneVisualizer.Zones.Projector;
import ZoneVisualizer.Zones.Vertex;
import ZoneVisualizer.Zones.Zone;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.stream.Collectors;

public class Projector3D extends Projector {
    private final Clock dimension1;
    private final Clock dimension2;
    private final Clock dimension3;

    public Projector3D(Clock dimension1, Clock dimension2, Clock dimension3, double maxValue, double infinityExtrusion) {
        super(maxValue, infinityExtrusion);
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
        this.dimension3 = dimension3;
    }

    @Override
    public Collection<WorldPolygon> project(Zone zone, Color zoneColor) {
        List<WorldPolygon> projectedPolygons = new ArrayList<>();
        if (zone.getVertices().isEmpty()) {
            return projectedPolygons;
        }

        List<Face> faces = zone.getFaces().values().stream()
                .filter(f -> projectNormal(f).getMagnitude() > 0)
                .filter(f -> Double.isFinite(f.getConstraint().getnValue()))
                .collect(Collectors.toList());

        for (Face face : faces) {
            WorldPolygon polygon = face.project(dimension1, dimension2, dimension3,
                    (v, c) -> v.getVisualCoordinate(c, maxValue));
            polygon.setColor(zoneColor);
            projectedPolygons.add(polygon);
            Collection<WorldPolygon> extrusionPolygons = face.extrudeInfinity(dimension1, dimension2, dimension3,
                    (v, c) -> v.getVisualCoordinate(c, maxValue),
                    (v, c) -> v.getVisualCoordinate(c, maxValue * (1d + infinityExtrusion)));
            extrusionPolygons.forEach(p -> p.setColor(Color.color(0, 0, 1, 0.6)));
            projectedPolygons.addAll(extrusionPolygons);
        }

        return projectedPolygons;
    }

    private Vector3 projectNormal(Face f) {
        return f.getConstraint().getProjectedNormal(dimension1, dimension2, dimension3);
    }
}
