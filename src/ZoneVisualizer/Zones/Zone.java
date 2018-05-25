package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.*;
import ZoneVisualizer.GraphicalElements.Vector3;
import ZoneVisualizer.GraphicalElements.WorldPolygon;
import ZoneVisualizer.Utility.LINQ;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Zone {

    protected final List<Vertex> vertices;
    protected Map<Constraint, Face> faces;
    protected final double maxValue;
    protected final double infinityValue;

    public Zone(Collection<Constraint> constraints, Collection<Clock> clocks, double maxValue) {
        this.maxValue = maxValue;
        infinityValue = 2 * maxValue;
        ConstraintZone constraintZone = new ConstraintZone(constraints, clocks);
        vertices = new ArrayList<>();
        faces = new HashMap<>();
        if (constraintZone.isRestrictedToEmptiness()) {
            return;
        }

        Vertex origin = findOrigin(constraintZone, clocks);
        tryAddVertex(origin);

        for (int i = 0; i < vertices.size(); i++) {
            Vertex pivot = vertices.get(i);

            for (PivotResult pivotResult : pivot.useAsPivot()) {
                pivotResult.findMissingConstraints(constraintZone, infinityValue);
                tryAddVertex(pivotResult.getVertex());
            }
        }
    }

    private void tryAddVertex(Vertex vertex) {
        if (vertices.contains(vertex)) {
            return;
        }
        vertices.add(vertex);
        int index = vertices.size() - 1;
        for (Constraint constraint : vertex.getAllConstraints()) {
            addVertexToFace(index, constraint);
        }
    }

    private Vertex findOrigin(ConstraintZone constraintZone, Collection<Clock> clocks) {
        Vertex origin = new Vertex(clocks);
        List<Clock> unknownDimensions = new ArrayList<>();
        List<Clock> knownDimensions = new ArrayList<>();
        //Handle trivial case
        for (Clock clock : clocks) {
            Constraint c = constraintZone.getMinConstraint(clock);
            if (c != null) {
                //Simple case; a greater than constraint exists for this dimension
                origin.addConstraint(clock, c);
                knownDimensions.add(clock);
            }
            else {
                unknownDimensions.add(clock);
            }
        }
        //Find the TCC's that can bound the unknown dimensions
        Map<Clock, Collection<TwoClockConstraint>> unresolvedConstraintsOnDimensions = new HashMap<>();
        Map<Clock, Set<Constraint>> candidatesOnDimension = new HashMap<>();
        Map<Clock, Double> maxValues = new HashMap<>();
        for (Clock unknownDimension : unknownDimensions) {
            unresolvedConstraintsOnDimensions.put(unknownDimension, constraintZone.getTCConstraintBySecondary(unknownDimension));
            candidatesOnDimension.put(unknownDimension, new HashSet<>());
            maxValues.put(unknownDimension, (double)0);
        }
        //Resolve the remaining TCC's
        for (int i = 0; i < knownDimensions.size(); i++) {
            Clock knownDimension = knownDimensions.get(i);
            Collection<TwoClockConstraint> pairings = constraintZone.getTCConstraintByPrimary(knownDimension);
            if (pairings.isEmpty()) {
                //Doesn't solve anything
                continue;
            }
            for (TwoClockConstraint pairing : pairings) {
                Clock otherClock = pairing.getClock2();
                if (!unresolvedConstraintsOnDimensions.containsKey(otherClock)) {
                    //Dimension is already solved as a trivial case
                    continue;
                }
                double valuation = pairing.getOtherValue(knownDimension, origin.getCoordinate(knownDimension));
                Collection<TwoClockConstraint> constraintsOnThis = unresolvedConstraintsOnDimensions.get(otherClock);
                Collection<Constraint> candidatesOnThis = candidatesOnDimension.get(otherClock);
                Double maxOnThis = maxValues.get(otherClock);
                if (valuation == maxOnThis) {
                    constraintsOnThis.remove(pairing);
                    candidatesOnThis.add(pairing);
                }
                else if (valuation > maxOnThis) {
                    constraintsOnThis.remove(pairing);
                    candidatesOnThis.clear();
                    candidatesOnThis.add(pairing);
                    maxValues.put(otherClock, valuation);
                }
                else {
                    //Not feasible
                    constraintsOnThis.remove(pairing);
                }
                if (!constraintsOnThis.isEmpty()) {
                    continue;
                }
                //Found a pairing for all tcc's of dimension. Resolve
                origin.addConstraints(otherClock, candidatesOnThis);
                knownDimensions.add(otherClock);
                unknownDimensions.remove(otherClock);
                unresolvedConstraintsOnDimensions.remove(otherClock);
            }
        }

        return origin;
    }

    private void addVertexToFace(int vertexIndex, Constraint constraint) {
        if (!faces.containsKey(constraint)) {
            faces.put(constraint, new Face(constraint));
        }
        faces.get(constraint).addVertexIndex(vertexIndex);
    }

    public List<Vertex> getVertices() {
        return  new ArrayList<>(vertices);
    }

    public Map<Constraint, Face> getFaces() {
        return new HashMap<>(faces);
    }

    public class Face {
        private final List<Integer> verticeIndices = new ArrayList<>();
        private final Constraint constraint;

        public Face(Constraint constraint) {
            this.constraint = constraint;
        }

        public WorldPolygon project(Clock dimension1, Clock dimension2, Clock dimension3) {
            List<Vector3> projectedVertices = verticeIndices.stream()
                    .map(i -> vertices.get(i))
                    .map(v -> new Vector3(v.getCoordinate(dimension1), v.getCoordinate(dimension2), v.getCoordinate(dimension3)))
                    .collect(Collectors.toList());
            Vector3 vNormal = constraint.getProjectedNormal(dimension1, dimension2, dimension3);

            return new WorldPolygon(projectedVertices, vNormal.multiply(-1), Color.color(1, 0, 0, 0.75));
        }

        public WorldPolygon project(Clock dimension1, Clock dimension2, Clock dimension3, BiFunction<Vertex, Clock, Double> mapper) {
            List<Vector3> projectedVertices = verticeIndices.stream()
                    .map(i -> vertices.get(i))
                    .map(v -> new Vector3(mapper.apply(v, dimension1), mapper.apply(v, dimension2), mapper.apply(v, dimension3)))
                    .collect(Collectors.toList());
            Vector3 vNormal = constraint.getProjectedNormal(dimension1, dimension2, dimension3);

            return new WorldPolygon(projectedVertices, vNormal.multiply(-1), Color.color(1, 0, 0, 0.75));
        }

        public List<Integer> getVerticeIndices() {
            return verticeIndices;
        }

        public Constraint getConstraint() {
            return constraint;
        }

        public void addVertexIndex(Integer index) {
            verticeIndices.add(index);
        }
    }
}
