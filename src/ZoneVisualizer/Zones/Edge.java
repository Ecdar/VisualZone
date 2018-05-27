package ZoneVisualizer.Zones;

public class Edge {

    private final Vertex lowerVertex;
    private final Vertex upperVertex;

    public Edge(Vertex lowerVertex, Vertex upperVertex) {
        this.lowerVertex = lowerVertex;
        this.upperVertex = upperVertex;
    }

    public Vertex getLowerVertex() {
        return lowerVertex;
    }

    public Vertex getUpperVertex() {
        return upperVertex;
    }
}
