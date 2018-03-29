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

        Vector3 center = new Vector3();
        center.x = vertices.stream().collect(Collectors.averagingDouble(v -> v.x));
        center.y = vertices.stream().collect(Collectors.averagingDouble(v -> v.y));
        center.z = vertices.stream().collect(Collectors.averagingDouble(v -> v.z));
        transform.setPosition(center);

        vertices.sort(new PointSortByAngleIn3D(center, normal, vertices.get(0)));

        float[] localSpaceVertices = new float[vertices.size() * 3];
        for (int i = 0; i < vertices.size(); i++) {
            localSpaceVertices[i * 3] = (float)(vertices.get(i).x - center.x);
            localSpaceVertices[i * 3 + 1] = (float)(vertices.get(i).y - center.y);
            localSpaceVertices[i * 3 + 2] = (float)(vertices.get(i).z - center.z);
        }

        int[] faceArray = new int[(vertices.size() - 2) * 6];
        for (int i = 0; i < vertices.size() - 2; i++) {
            faceArray[i * 6] = i + 2;
            faceArray[i * 6 + 2] = i + 1;
            faceArray[i * 6 + 4] = 0;
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
