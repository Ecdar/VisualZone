package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.TwoClockConstraint;
import ZoneVisualizer.GraphicalElements.Vector3;
import ZoneVisualizer.GraphicalElements.WorldPolygon;
import javafx.scene.paint.Color;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Projector {

    protected final double maxValue;
    protected final double infinityExtrusion;

    public Projector(double maxValue, double infinityExtrusion) {
        this.maxValue = maxValue;
        this.infinityExtrusion = infinityExtrusion;
    }

    public abstract Collection<WorldPolygon> project(Zone zone);

    protected List<Vector3> getHullVerticesOfXPlane(List<Vector3> projectedVertices) {
        Double minY = getMinFromMappedValues(projectedVertices, vertex -> vertex.y);
        Double maxY = getMaxFromMappedValues(projectedVertices, vertex -> vertex.y);
        Double minZ = getMinFromMappedValues(projectedVertices, vertex -> vertex.z);
        Double maxZ = getMaxFromMappedValues(projectedVertices, vertex -> vertex.z);

        return projectedVertices.stream()
                .filter(v -> v.y == minY || v.y == maxY || v.z == minZ || v.z == maxZ)
                .collect(Collectors.toList());
    }

    protected List<Vector3> getHullVerticesOfYPlane(List<Vector3> projectedVertices) {
        Double minX = getMinFromMappedValues(projectedVertices, vertex -> vertex.x);
        Double maxX = getMaxFromMappedValues(projectedVertices, vertex -> vertex.x);
        Double minZ = getMinFromMappedValues(projectedVertices, vertex -> vertex.z);
        Double maxZ = getMaxFromMappedValues(projectedVertices, vertex -> vertex.z);

        return projectedVertices.stream()
                .filter(v -> v.x == minX || v.x == maxX || v.z == minZ || v.z == maxZ)
                .collect(Collectors.toList());
    }

    protected List<Vector3> getHullVerticesOfZPlane(List<Vector3> projectedVertices) {
        Double minX = getMinFromMappedValues(projectedVertices, vertex -> vertex.x);
        Double maxX = getMaxFromMappedValues(projectedVertices, vertex -> vertex.x);
        Double minY = getMinFromMappedValues(projectedVertices, vertex -> vertex.y);
        Double maxY = getMaxFromMappedValues(projectedVertices, vertex -> vertex.y);

        return projectedVertices.stream()
                .filter(v -> v.x == minX || v.x == maxX || v.y == minY || v.y == maxY)
                .collect(Collectors.toList());
    }

    protected Double getMinFromMappedValues(List<Vector3> projectedVertices, Function<? super Vector3, ? extends Double> mapper) {
        return projectedVertices.stream().map(mapper).min(Double::compare).get();
    }

    protected Double getMaxFromMappedValues(List<Vector3> projectedVertices, Function<? super Vector3, ? extends Double> mapper) {
        return projectedVertices.stream().map(mapper).max(Double::compare).get();
    }

    protected boolean isTCConstraintOfClocks(TwoClockConstraint tcConstraint, Clock c1, Clock c2, Clock c3) {
        Clock clock1 = tcConstraint.getClock1(), clock2 = tcConstraint.getClock2();
        return  (clock1 == c1 || clock1 == c2 || clock1 == c3) &&
                (clock2 == c1 || clock2 == c2 || clock2 == c3);
    }
}
