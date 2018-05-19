package ZoneVisualizer.GraphicalElements;

import ZoneVisualizer.Utility.LINQ;
import ZoneVisualizer.Utility.PointSortByAngleIn3D;
import ZoneVisualizer.Utility.Utility;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;

import java.util.*;
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
        if (vertices.isEmpty()) {
            return;
        }

        Vector3 center = new Vector3();
        center.x = (vertices.stream().map(v -> v.x).min(Double::compareTo).get() +
                    vertices.stream().map(v -> v.x).max(Double::compareTo).get()) / 2;
        center.y = (vertices.stream().map(v -> v.y).min(Double::compareTo).get() +
                    vertices.stream().map(v -> v.y).max(Double::compareTo).get()) / 2;
        center.z = (vertices.stream().map(v -> v.z).min(Double::compareTo).get() +
                    vertices.stream().map(v -> v.z).max(Double::compareTo).get()) / 2;
        transform.setPosition(center);

        for (int i = 0; i < vertices.size(); i++) {
            Vector3 vertex = vertices.get(i);
            Vector3 replace = new Vector3();
            replace.x = vertex.x - center.x;
            replace.y = vertex.y - center.y;
            replace.z = vertex.z - center.z;
            vertices.remove(i);
            vertices.add(i, replace);
        }

        if (normal.y != 0 && normal.x == 0 && normal.z == 0) {
            //Up or down facing faces must sort reversed cause y is flipped
            vertices.sort(new PointSortByAngleIn3D(normal, vertices.get(0)));
        }
        else {
            vertices.sort(new PointSortByAngleIn3D(normal, vertices.get(0)).reversed());
        }

        float[] localSpaceVertices = new float[vertices.size() * 3];
        for (int i = 0; i < vertices.size(); i++) {
            Vector3 vertex = vertices.get(i);
            localSpaceVertices[i * 3] = (float)vertex.x;
            //Flip y axis because JavaFX
            localSpaceVertices[i * 3 + 1] = (float)(-vertex.y);
            localSpaceVertices[i * 3 + 2] = (float)vertex.z;
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
