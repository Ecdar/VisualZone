package sample.GraphicalElements;

import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;

public class WorldPolygon extends MeshView implements Object3D {

    private Rotate xRotation = new Rotate(0, Rotate.X_AXIS);
    private Rotate yRotation = new Rotate(0, Rotate.Y_AXIS);
    private Rotate zRotation = new Rotate(0, Rotate.Z_AXIS);
    private final WorldTransform transform = new WorldTransform();

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
        getTransforms().addAll(xRotation, yRotation, zRotation);

        TriangleMesh triangleMesh = new TriangleMesh();
        triangleMesh.getPoints().addAll(vertices);
        triangleMesh.getTexCoords().addAll(texCoords);
        triangleMesh.getFaces().addAll(faces);
        setMesh(triangleMesh);
        setDrawMode(DrawMode.FILL);
        setCullFace(CullFace.NONE);

        transform.addOnPositionChange(this::updatePosition);
        transform.addOnRotationChange(this::updateRotation);
        transform.addOnScaleChange(this::updateScale);
    }

    private void updatePosition() {
        setTranslateX(transform.getPositionReadonly().x);
        setTranslateY(transform.getPositionReadonly().y);
        setTranslateZ(transform.getPositionReadonly().z);
    }

    private void updateRotation() {
        xRotation.setAngle(transform.getRotationReadonly().x);
        yRotation.setAngle(transform.getRotationReadonly().y);
        zRotation.setAngle(transform.getRotationReadonly().z);
    }

    private void updateScale() {
        setScaleX(transform.getScaleReadonly().x);
        setScaleY(transform.getScaleReadonly().y);
        setScaleZ(transform.getScaleReadonly().z);
    }

    @Override
    public WorldTransform getTransform() {
        return transform;
    }
}
