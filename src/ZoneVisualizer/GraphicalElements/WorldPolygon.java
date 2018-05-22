package ZoneVisualizer.GraphicalElements;

import ZoneVisualizer.Utility.PointSortByAngleIn3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;

import java.util.List;
import java.util.stream.Collectors;

public class WorldPolygon extends MeshView implements Object3D {

    private final WorldTransform transform = new WorldTransform();
    private final TransformUpdater transformUpdater;
    private final Vector3 normal;
    private final List<Vector3> vertices;
    private WorldPolygon backFace;

    private WorldPolygon(List<Vector3> vertices, Vector3 normal, WorldPolygon backFace) {
        this(vertices, normal);

        this.backFace = backFace;
    }

    public WorldPolygon(List<Vector3> vertices, Vector3 normal) {
        transformUpdater = new TransformUpdater(this, transform);
        this.normal = normal;
        this.vertices = vertices;
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
    }

    private void createTriangleMesh(float[] vertices, float[] texCoords, int[] faces) {
        TriangleMesh triangleMesh = new TriangleMesh();
        triangleMesh.setVertexFormat(VertexFormat.POINT_TEXCOORD);
        triangleMesh.getPoints().addAll(vertices);
        triangleMesh.getTexCoords().addAll(texCoords);
        triangleMesh.getFaces().addAll(faces);
        setMesh(triangleMesh);
        setDrawMode(DrawMode.FILL);
        setCullFace(CullFace.BACK);
        PhongMaterial material = new PhongMaterial(Color.color(1, 0, 0, 0.75));
        setMaterial(material);
    }

    public WorldPolygon getBackFace()
    {
        if (backFace == null) {
            List<Vector3> worldspaceVertices = vertices.stream().map(v -> v.plus(transform.getPositionReadonly())).collect(Collectors.toList());
            backFace = new WorldPolygon(worldspaceVertices, normal.multiply(-1), this);
        }
        return backFace;
    }

    @Override
    public WorldTransform getTransform() {
        return transform;
    }
}
