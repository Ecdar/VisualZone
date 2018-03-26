package ZoneVisualizer.GraphicalElements;

import ZoneVisualizer.Utility.LINQ;
import ZoneVisualizer.Utility.PointSortByAngleIn3D;
import ZoneVisualizer.Utility.Utility;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.Arrays;
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
        else if (faces.length % 3 != 0) {
            throw new RuntimeException("Received " + faces.length + " face indices. Must be divisible by 3");
        }
        transformUpdater = new TransformUpdater(this, transform);
        if (faces.length > 0) {
            Vector3 p1 = new Vector3(vertices[faces[0]], vertices[faces[2]], vertices[faces[4]]),
                    p2 = new Vector3(vertices[faces[6]], vertices[faces[8]], vertices[faces[10]]),
                    p3 = new Vector3(vertices[faces[12]], vertices[faces[14]], vertices[faces[16]]);
            this.normal = p1.minus(p2).crossProduct(p3.minus(p2));
        }
        else {
            this.normal = Vector3.zero();
        }

        createTriangleMesh(vertices, texCoords, faces);
    }

    public WorldPolygon(float[] vertices, Vector3 normal) {
        transformUpdater = new TransformUpdater(this, transform);
        this.normal = normal;
        if (vertices.length == 0) {
            return;
        }

        Vector3 center = new Vector3();
        center.x = IntStream.range(0, vertices.length / 3).boxed()
                .collect(Collectors.averagingDouble((Integer i) -> vertices[i * 3]));
        center.y = IntStream.range(0, vertices.length / 3).boxed()
                .collect(Collectors.averagingDouble((Integer i) -> vertices[i * 3 + 1]));
        center.z = IntStream.range(0, vertices.length / 3).boxed()
                .collect(Collectors.averagingDouble((Integer i) -> vertices[i * 3 + 2]));
        transform.setPosition(center);

        List<Vector3> vectorVertices = new ArrayList<>();
        for (int i = 0; i < vertices.length; i += 3) {
            vectorVertices.add(new Vector3(vertices[i], vertices[i + 1], vertices[i + 2]));
        }
        vectorVertices.sort(new PointSortByAngleIn3D(center, normal, vectorVertices.get(0)));

        float[] localSpaceVertices = new float[vertices.length];
        for (int i = 0; i < vectorVertices.size(); i++) {
            localSpaceVertices[i * 3] = (float)(vectorVertices.get(i).x - center.x);
            localSpaceVertices[i * 3 + 1] = (float)(vectorVertices.get(i).y - center.y);
            localSpaceVertices[i * 3 + 2] = (float)(vectorVertices.get(i).z - center.z);
        }

        int[] faceArray = new int[localSpaceVertices.length - 2];

        createTriangleMesh(localSpaceVertices, new float[2], faceArray);
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
