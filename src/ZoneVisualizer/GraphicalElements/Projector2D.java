package ZoneVisualizer.GraphicalElements;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.GraphicalElements.Vector3;
import ZoneVisualizer.GraphicalElements.WorldPolygon;
import ZoneVisualizer.Zones.Face;
import ZoneVisualizer.Zones.Projector;
import ZoneVisualizer.Zones.Vertex;
import ZoneVisualizer.Zones.Zone;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.stream.Collectors;

public class Projector2D extends Projector {
    private final Clock dimension1;
    private final Clock dimension2;

    public Projector2D(Clock dimension1, Clock dimension2, double maxValue, double infinityExtrusion) {
        super(maxValue, infinityExtrusion);
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
    }

    @Override
    public Collection<WorldPolygon> project(Zone zone, Color zoneColor) {
        if (zone.getVertices().isEmpty()) {
            return new ArrayList<>();
        }
        List<Face> faces = zone.getFaces().entrySet().stream()
                .filter(entry -> entry.getKey().getNormalComponent(dimension1) != 0 ||
                        entry.getKey().getNormalComponent(dimension2) != 0)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        List<Vector3> projectedVertices = new ArrayList<>();

        List<Vertex> faceVertices = faces.stream().flatMap(f -> f.getVertices().stream()).distinct().collect(Collectors.toList());
        for (Vertex vertex : faceVertices) {
            Vector3 projectedVertex = new Vector3(vertex.getVisualCoordinate(dimension1, maxValue),
                    vertex.getVisualCoordinate(dimension2, maxValue), 0);
            if (!projectedVertices.contains(projectedVertex)) {
                projectedVertices.add(projectedVertex);
            }
        }
        List<Vector3> hullVertices = getHullVerticesOfZPlane(projectedVertices);
        //Todo show infinite zones with more than double size face

        WorldPolygon worldPolygon = new WorldPolygon(hullVertices, Vector3.back());
        worldPolygon.setColor(zoneColor);
        return Arrays.asList(worldPolygon);
    }
}
