package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.GraphicalElements.Vector3;
import ZoneVisualizer.GraphicalElements.WorldPolygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Projector2D extends Projector {
    private final Clock dimension1;
    private final Clock dimension2;

    public Projector2D(Clock dimension1, Clock dimension2) {
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
    }

    @Override
    public Collection<WorldPolygon> project(Zone zone) {
        if (zone.getVertices().isEmpty()) {
            return new ArrayList<>();
        }
        List<Vector3> projectedVertices = new ArrayList<>();

        for (Vertex vertex : zone.getVertices()) {
            Vector3 projectedVertex = new Vector3(vertex.getCoordinate(dimension1), vertex.getCoordinate(dimension2), 0);
            if (!projectedVertices.contains(projectedVertex)) {
                projectedVertices.add(projectedVertex);
            }
        }
        List<Vector3> hullVertices = getHullVerticesOfZPlane(projectedVertices);

        return Arrays.asList(new WorldPolygon(hullVertices, Vector3.back()));
    }
}
