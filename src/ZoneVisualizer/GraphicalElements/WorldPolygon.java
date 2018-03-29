package ZoneVisualizer.GraphicalElements;

import ZoneVisualizer.Utility.LINQ;
import ZoneVisualizer.Utility.PointSortByAngleIn3D;
import ZoneVisualizer.Utility.Utility;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WorldPolygon extends MeshView implements Object3D {

    private final WorldTransform transform = new WorldTransform();
    private final TransformUpdater transformUpdater;
    private final Vector3 normal;

    public WorldPolygon(float[] vertices, float texCoords[], int[] faces) {
        super();
        if (vertices.length % 3 != 0) {
            throw new RuntimeException("Received " + vertices.length + " vertices. Must be divisible by 3");
        }
        else if (texCoords.length % 2 != 0) {
            throw new RuntimeException("Received " + texCoords.length + " tex coordinates. Must be divisible by 2");
        }
        else if (faces.length % 6 != 0) {
            throw new RuntimeException("Received " + faces.length + " face indices. Must be divisible by 3");
        }
        transformUpdater = new TransformUpdater(this, transform);
        if (faces.length > 0) {
            Vector3 p1 = new Vector3(vertices[faces[0]], vertices[faces[0] + 1], vertices[faces[0] + 2]),
                    p2 = new Vector3(vertices[faces[2]], vertices[faces[2] + 1], vertices[faces[2] + 2]),
                    p3 = new Vector3(vertices[faces[4]], vertices[faces[4] + 1], vertices[faces[4] + 2]);
            this.normal = p1.minus(p2).crossProduct(p3.minus(p2));
        }
        else {
            this.normal = Vector3.zero();
        }

        createTriangleMesh(vertices, texCoords, faces);

        setMaterial(new PhongMaterial(Color.RED));
    }

    public WorldPolygon(List<Vector3> vertices, Vector3 normal) {
        transformUpdater = new TransformUpdater(this, transform);
        this.normal = normal;
        if (vertices.size() == 0) {
            return;
        }

        double maxValue = vertices.stream().map(v -> v.max()).filter(Double::isFinite).max(Double::compareTo).get();
        Vector3 center = new Vector3();
        center.x = vertices.stream().collect(Collectors.averagingDouble(v -> Double.isFinite(v.x) ? v.x : maxValue * 2 + 50));
        center.y = vertices.stream().collect(Collectors.averagingDouble(v -> Double.isFinite(v.y) ? v.y : maxValue * 2 + 50));
        center.z = vertices.stream().collect(Collectors.averagingDouble(v -> Double.isFinite(v.z) ? v.z : maxValue * 2 + 50));
        transform.setPosition(center);

        vertices.sort(new PointSortByAngleIn3D(center, normal, vertices.get(0)));

        float[] localSpaceVertices = new float[vertices.size() * 3];
        for (int i = 0; i < vertices.size(); i++) {
            Vector3 vertex = vertices.get(i);
            localSpaceVertices[i * 3] = Double.isFinite(vertex.x) ?
                    (float)(vertex.x - center.x) : (float)maxValue * 10 + 100;
            localSpaceVertices[i * 3 + 1] = Double.isFinite(vertex.y) ?
                    (float)(vertex.y - center.y) : (float)maxValue * 10 + 100;
            localSpaceVertices[i * 3 + 2] = Double.isFinite(vertex.z) ?
                    (float)(vertex.z - center.z) : (float)maxValue * 10 + 100;
        }

        int[] faceArray = new int[(vertices.size() - 2) * 6];
        for (int i = 0; i < vertices.size() - 2; i++) {
            faceArray[i * 6] = 0;
            faceArray[i * 6 + 2] = i + 1;
            faceArray[i * 6 + 4] = i + 2;
        }

        createTriangleMesh(localSpaceVertices, new float[2], faceArray);

        setMaterial(new PhongMaterial(Color.RED));
    }

    private void createTriangleMesh(float[] vertices, float[] texCoords, int[] faces) {
        TriangleMesh triangleMesh = new TriangleMesh();
        triangleMesh.setVertexFormat(VertexFormat.POINT_TEXCOORD);
        triangleMesh.getPoints().addAll(vertices);
        triangleMesh.getTexCoords().addAll(texCoords);
        triangleMesh.getFaces().addAll(faces);
        setMesh(triangleMesh);
        setDrawMode(DrawMode.FILL);
        setCullFace(CullFace.NONE);
    }

    @Override
    public WorldTransform getTransform() {
        return transform;
    }
}
