package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.Constraints.TwoClockConstraint;
import ZoneVisualizer.GraphicalElements.Vector3;
import ZoneVisualizer.GraphicalElements.WorldPolygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Projector3D extends Projector {
    private final Clock dimension1;
    private final Clock dimension2;
    private final Clock dimension3;

    public Projector3D(Clock dimension1, Clock dimension2, Clock dimension3) {
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
        this.dimension3 = dimension3;
    }

    @Override
    public Collection<WorldPolygon> project(Zone zone) {
        List<WorldPolygon> projectedPolygons = new ArrayList<>();
        if (zone.getVertices().isEmpty()) {
            return projectedPolygons;
        }
        List<Vector3> projectedVertices = new ArrayList<>();

        for (Vertex vertex : zone.getVertices()) {
            Vector3 projectedVertex = new Vector3(vertex.getCoordinate(dimension1),
                    vertex.getCoordinate(dimension2), vertex.getCoordinate(dimension3));
            if (!projectedVertices.contains(projectedVertex)) {
                projectedVertices.add(projectedVertex);
            }
        }
        //Find the single clock constraint faces (non-tilted faces)
        //(Inefficient? Traverses vertices a ton of times. Could use faces instead)
        Double minX = getMinFromMappedValues(projectedVertices, vertex -> vertex.x);
        Double maxX = getMaxFromMappedValues(projectedVertices, vertex -> vertex.x);
        Double minY = getMinFromMappedValues(projectedVertices, vertex -> vertex.y);
        Double maxY = getMaxFromMappedValues(projectedVertices, vertex -> vertex.y);
        Double minZ = getMinFromMappedValues(projectedVertices, vertex -> vertex.z);
        Double maxZ = getMaxFromMappedValues(projectedVertices, vertex -> vertex.z);

        List<Vector3> hullVertices = null;
        List<Vector3> planeVertices = projectedVertices.stream()
                .filter(v -> v.x == minX)
                .collect(Collectors.toList());
        if (planeVertices.size() >= 3) {
            hullVertices = getHullVerticesOfXPlane(planeVertices);
            projectedPolygons.add(new WorldPolygon(hullVertices, Vector3.left()));
        }

        planeVertices = projectedVertices.stream()
                .filter(v -> v.x == maxX)
                .collect(Collectors.toList());
        if (planeVertices.size() >= 3) {
            hullVertices = getHullVerticesOfXPlane(planeVertices);
            projectedPolygons.add(new WorldPolygon(hullVertices, Vector3.right()));
        }

        planeVertices = projectedVertices.stream()
                .filter(v -> v.y == minY)
                .collect(Collectors.toList());
        if (planeVertices.size() >= 3) {
            hullVertices = getHullVerticesOfYPlane(planeVertices);
            projectedPolygons.add(new WorldPolygon(hullVertices, Vector3.down()));
        }

        planeVertices = projectedVertices.stream()
                .filter(v -> v.y == maxY)
                .collect(Collectors.toList());
        if (planeVertices.size() >= 3) {
            hullVertices = getHullVerticesOfYPlane(planeVertices);
            projectedPolygons.add(new WorldPolygon(hullVertices, Vector3.up()));
        }

        planeVertices = projectedVertices.stream()
                .filter(v -> v.z == minZ)
                .collect(Collectors.toList());
        if (planeVertices.size() >= 3) {
            hullVertices = getHullVerticesOfZPlane(planeVertices);
            projectedPolygons.add(new WorldPolygon(hullVertices, Vector3.back()));
        }

        planeVertices = projectedVertices.stream()
                .filter(v -> v.z == maxZ)
                .collect(Collectors.toList());
        if (planeVertices.size() >= 3) {
            hullVertices = getHullVerticesOfZPlane(planeVertices);
            projectedPolygons.add(new WorldPolygon(hullVertices, Vector3.forward()));
        }

        //Finds the two clock constraint faces (tilted faces)
        //(Doesn't reduce to hull vertices, so there will be more triangles than necessary)
        for (Map.Entry<Constraint, Zone.Face> face : zone.getFaces().entrySet()) {
            if (face.getKey() instanceof TwoClockConstraint) {
                TwoClockConstraint tcConstraint = (TwoClockConstraint)face.getKey();
                if (isTCConstraintOfClocks(tcConstraint, dimension1, dimension2, dimension3)) {
                    projectedPolygons.add(face.getValue().project(dimension1, dimension2, dimension3));
                }
            }
        }

        return projectedPolygons;
    }
}
