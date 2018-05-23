package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.GraphicalElements.Vector3;
import ZoneVisualizer.GraphicalElements.WorldPolygon;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Projector2D extends Projector {
    private final Clock dimension1;
    private final Clock dimension2;

    public Projector2D(Clock dimension1, Clock dimension2, double maxValue) {
        super(maxValue);
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
    }

    @Override
    public Collection<WorldPolygon> project(Zone zone) {
        if (zone.getVertices().isEmpty()) {
            return new ArrayList<>();
        }
        List<Vector3> projectedVertices = new ArrayList<>();
        Collection<Vertex> xInfiniteVertices = new ArrayList<>();
        Collection<Vertex> yInfiniteVertices = new ArrayList<>();

        for (Vertex vertex : zone.getVertices()) {
            Vector3 projectedVertex = new Vector3(vertex.getVisualCoordinate(dimension1, maxValue),
                    vertex.getVisualCoordinate(dimension2, maxValue), 0);
            if (!projectedVertices.contains(projectedVertex)) {
                projectedVertices.add(projectedVertex);
            }
        }
        List<Vector3> hullVertices = getHullVerticesOfZPlane(projectedVertices);
        //Todo show infinite zones with more than double size face

        return Arrays.asList(new WorldPolygon(hullVertices, Vector3.back(), Color.RED));
    }
}
