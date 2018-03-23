package ZoneVisualizer.GraphicalElements;

import ZoneVisualizer.Utility.LINQ;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WorldPolygon extends MeshView implements Object3D {

    private final WorldTransform transform = new WorldTransform();
    private final TransformUpdater transformUpdater;

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

        TriangleMesh triangleMesh = new TriangleMesh();
        triangleMesh.getPoints().addAll(vertices);
        triangleMesh.getTexCoords().addAll(texCoords);
        triangleMesh.getFaces().addAll(faces);
        setMesh(triangleMesh);
        setDrawMode(DrawMode.FILL);
        setCullFace(CullFace.NONE);

        transformUpdater = new TransformUpdater(this, transform);
    }

    public WorldPolygon(float[] vertices) {
        float texCoords[] = new float[1];
        List<Integer> faces = new ArrayList<>();

        //Todo implement ear clipping algorithm to find faces

        TriangleMesh triangleMesh = new TriangleMesh();
        triangleMesh.getPoints().addAll(vertices);
        triangleMesh.getTexCoords().addAll(texCoords);
        int[] facesArray = new int[faces.size() * 2];
        for (int i = 0; i < faces.size(); i++) {
            facesArray[i * 2] = faces.get(i);
        }
        triangleMesh.getFaces().addAll(facesArray);
        setMesh(triangleMesh);
        setDrawMode(DrawMode.FILL);
        setCullFace(CullFace.NONE);

        transformUpdater = new TransformUpdater(this, transform);
    }

    @Override
    public WorldTransform getTransform() {
        return transform;
    }
}
