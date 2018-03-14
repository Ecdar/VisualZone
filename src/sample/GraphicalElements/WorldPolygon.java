package sample.GraphicalElements;

import javafx.scene.shape.*;

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

    @Override
    public WorldTransform getTransform() {
        return transform;
    }
}
