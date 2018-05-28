package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.Constraints.SingleClockConstraint;
import ZoneVisualizer.GraphicalElements.Vector3;
import ZoneVisualizer.GraphicalElements.WorldPolygon;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Face {

    private Zone zone;
    private final List<Integer> vertexIndices = new ArrayList<>();
    private final Constraint constraint;

    public Face(Zone zone, Constraint constraint) {
        this.zone = zone;
        this.constraint = constraint;
    }

    public WorldPolygon project(Clock dimension1, Clock dimension2, Clock dimension3) {
        List<Vector3> projectedVertices = vertexIndices.stream()
                .map(i -> zone.vertices.get(i))
                .map(v -> new Vector3(v.getCoordinate(dimension1), v.getCoordinate(dimension2), v.getCoordinate(dimension3)))
                .collect(Collectors.toList());
        Vector3 vNormal = constraint.getProjectedNormal(dimension1, dimension2, dimension3);

        return new WorldPolygon(projectedVertices, vNormal.multiply(-1));
    }

    public WorldPolygon project(Clock dimension1, Clock dimension2, Clock dimension3, BiFunction<Vertex, Clock, Double> mapper) {
        List<Vector3> projectedVertices = vertexIndices.stream()
                .map(i -> zone.vertices.get(i))
                .map(v -> new Vector3(mapper.apply(v, dimension1), mapper.apply(v, dimension2), mapper.apply(v, dimension3)))
                .collect(Collectors.toList());
        Vector3 vNormal = constraint.getProjectedNormal(dimension1, dimension2, dimension3);

        return new WorldPolygon(projectedVertices, vNormal.multiply(-1));
    }

    public Collection<WorldPolygon> extrudeInfinity(Clock dimension1, Clock dimension2, Clock dimension3,
                                                    BiFunction<Vertex, Clock, Double> mapper,
                                                    BiFunction<Vertex, Clock, Double> infinityMapper) {
        Set<Set<Vertex>> infinityVertices = new HashSet<>();

        List<Vertex> vertices = vertexIndices.stream()
                .map(i -> zone.vertices.get(i))
                .collect(Collectors.toList());
        infinityVertices.add(vertices.stream().filter(v -> isVertexInfinite(v, dimension1)).collect(Collectors.toSet()));
        infinityVertices.add(vertices.stream().filter(v -> isVertexInfinite(v, dimension2)).collect(Collectors.toSet()));
        infinityVertices.add(vertices.stream().filter(v -> isVertexInfinite(v, dimension3)).collect(Collectors.toSet()));

        Collection<WorldPolygon> result = new ArrayList<>();
        Vector3 vNormal = constraint.getProjectedNormal(dimension1, dimension2, dimension3);

        for (Collection<Vertex> infinityVerticesOfDimension : infinityVertices) {
            List<Vector3> projectedVertices = new ArrayList<>();
            projectedVertices.addAll(infinityVerticesOfDimension.stream()
                    .map(v ->
                    new Vector3(mapper.apply(v, dimension1),
                                mapper.apply(v, dimension2),
                                mapper.apply(v, dimension3)))
                    .distinct()
                    .collect(Collectors.toList()));
            if (projectedVertices.size() != 2) {
                continue;
            }
            projectedVertices.addAll(infinityVerticesOfDimension.stream()
                    .map(v ->
                            new Vector3(infinityMapper.apply(v, dimension1),
                                        infinityMapper.apply(v, dimension2),
                                        infinityMapper.apply(v, dimension3)))
                    .distinct()
                    .collect(Collectors.toList()));

            result.add(new WorldPolygon(projectedVertices, vNormal.multiply(-1)));
        }

        return result;
    }

    private boolean isVertexInfinite(Vertex v, Clock dimension) {
        Optional<Constraint> scc = v.getConstraints(dimension).stream().filter(c -> c instanceof SingleClockConstraint).findFirst();
        if (!scc.isPresent()) {
            return false;
        }
        return !Double.isFinite(scc.get().getnValue());
    }

    public List<Vertex> getVertices() {
        return vertexIndices.stream()
                .map(i -> zone.vertices.get(i))
                .collect(Collectors.toList());
    }

    public List<Integer> getVertexIndices() {
        return vertexIndices;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public void addVertexIndex(Integer index) {
        vertexIndices.add(index);
    }
}
