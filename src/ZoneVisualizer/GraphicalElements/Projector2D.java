package ZoneVisualizer.GraphicalElements;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.GraphicalElements.Vector3;
import ZoneVisualizer.GraphicalElements.WorldPolygon;
import ZoneVisualizer.Utility.Utility;
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
    private Color zoneColor;
    private Color infinityColor;

    public Projector2D(Clock dimension1, Clock dimension2, double maxValue, double infinityExtrusion,
                       Color zoneColor, Color infinityColor) {
        super(maxValue, infinityExtrusion);
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
        this.zoneColor = zoneColor;
        this.infinityColor = infinityColor;
    }

    @Override
    public Collection<WorldPolygon> project(Zone zone) {
        if (zone.getVertices().isEmpty()) {
            return new ArrayList<>();
        }
        List<Face> faces = zone.getFaces().entrySet().stream()
                .filter(entry -> entry.getKey().getNormalComponent(dimension1) != 0 ||
                        entry.getKey().getNormalComponent(dimension2) != 0)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        List<Vector3> projectedVertices = new ArrayList<>();
        Map<Clock, Set<Vertex>> infinityVertices = new HashMap<>();
        infinityVertices.put(dimension1, new HashSet<>());
        infinityVertices.put(dimension2, new HashSet<>());

        List<Vertex> faceVertices = faces.stream().flatMap(f -> f.getVertices().stream()).distinct().collect(Collectors.toList());
        for (Vertex vertex : faceVertices) {
            Vector3 projectedVertex = new Vector3(vertex.getVisualCoordinate(dimension1, maxValue),
                    vertex.getVisualCoordinate(dimension2, maxValue), 0);
            if (!projectedVertices.contains(projectedVertex)) {
                projectedVertices.add(projectedVertex);
                if (Utility.isVertexInfinite(vertex, dimension1)){
                    infinityVertices.get(dimension1).add(vertex);
                }
                if (Utility.isVertexInfinite(vertex, dimension2)){
                    infinityVertices.get(dimension2).add(vertex);
                }
            }
        }
        List<Vector3> hullVertices = getHullVerticesOfZPlane(projectedVertices);
        Collection<WorldPolygon> result = new ArrayList<>();
        boolean extruded = false;
        for (Collection<Vertex> infinityVerticesOfDimension : infinityVertices.values()) {
            List<Vector3> projectedInfinity = new ArrayList<>();
            projectedInfinity.addAll(infinityVerticesOfDimension.stream()
                    .map(v ->
                            new Vector3(v.getVisualCoordinate(dimension1, maxValue),
                                        v.getVisualCoordinate(dimension2, maxValue),
                                        0))
                    .distinct()
                    .collect(Collectors.toList()));
            if (projectedInfinity.size() < 2) {
                continue;
            }
            extruded = true;
            projectedInfinity.addAll(infinityVerticesOfDimension.stream()
                    .map(v ->
                            new Vector3(v.getVisualCoordinate(dimension1, maxValue * (1 + infinityExtrusion)),
                                        v.getVisualCoordinate(dimension2, maxValue * (1 + infinityExtrusion)),
                                        0))
                    .distinct()
                    .collect(Collectors.toList()));

            result.add(new WorldPolygon(projectedInfinity, Vector3.back()));
        }
        if (!extruded && infinityVertices.size() > 1) {
            Set<Vertex> allInfinityVertices = infinityVertices.values().stream()
                    .flatMap(set -> set.stream())
                    .collect(Collectors.toSet());
            List<Vector3> projectedInfinity = new ArrayList<>();
            projectedInfinity.addAll(allInfinityVertices.stream()
                    .map(v ->
                            new Vector3(v.getVisualCoordinate(dimension1, maxValue),
                                        v.getVisualCoordinate(dimension2, maxValue),
                                        0))
                    .distinct()
                    .collect(Collectors.toList()));
            if (projectedInfinity.size() >= 2) {
                projectedInfinity.addAll(allInfinityVertices.stream()
                        .map(v ->
                                new Vector3(v.getVisualCoordinate(dimension1, maxValue * (1 + infinityExtrusion)),
                                            v.getVisualCoordinate(dimension2, maxValue * (1 + infinityExtrusion)),
                                            0))
                        .distinct()
                        .collect(Collectors.toList()));

                result.add(new WorldPolygon(projectedInfinity, Vector3.back()));
            }
        }
        result.forEach(p -> p.setColor(infinityColor));

        WorldPolygon worldPolygon = new WorldPolygon(hullVertices, Vector3.back());
        worldPolygon.setColor(zoneColor);
        result.add(worldPolygon);
        return result;
    }
}
