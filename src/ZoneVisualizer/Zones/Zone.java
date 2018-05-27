package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.*;

import java.util.*;

public class Zone {

    protected final List<Vertex> vertices;
    protected final List<Edge> edges;
    protected Map<Constraint, Face> faces;
    protected final double maxValue;
    protected final double infinityValue;

    public Zone(Collection<Constraint> constraints, Collection<Clock> clocks, double maxValue) {
        this.maxValue = maxValue;
        infinityValue = 2 * maxValue;
        ConstraintZone constraintZone = new ConstraintZone(constraints);
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        faces = new HashMap<>();
        if (constraintZone.isRestrictedToEmptiness()) {
            return;
        }

        Vertex origin = OriginCreator.findOrigin(constraintZone, clocks);
        tryAddVertex(origin);

        for (int i = 0; i < vertices.size(); i++) {
            Vertex pivot = vertices.get(i);

            for (PivotResult pivotResult : pivot.useAsPivot()) {
                pivotResult.findMissingConstraints(constraintZone, infinityValue);
                edges.add(new Edge(pivot, pivotResult.getVertex()));
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

    private void addVertexToFace(int vertexIndex, Constraint constraint) {
        if (!faces.containsKey(constraint)) {
            faces.put(constraint, new Face(this, constraint));
        }
        faces.get(constraint).addVertexIndex(vertexIndex);
    }

    public List<Vertex> getVertices() {
        return  new ArrayList<>(vertices);
    }

    public Map<Constraint, Face> getFaces() {
        return new HashMap<>(faces);
    }

}
