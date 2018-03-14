package sample.GraphicalElements;

import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

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
        transform.addOnPivotChange(this::updatePivot);
        transform.addOnRotationChange(this::updateRotation);
        transform.addOnScaleChange(this::updateScale);
    }

    private void updatePosition() {
        Vector3 position = transform.getPositionReadonly();
        setTranslateX(position.x);
        setTranslateY(-position.y);
        setTranslateZ(position.z);
    }

    private void updatePivot() {
        Vector3 pivot = transform.getPivotReadonly();
        xRotation.setPivotX(pivot.x);
        xRotation.setPivotY(-pivot.y);
        xRotation.setPivotZ(pivot.z);
        yRotation.setPivotX(pivot.x);
        yRotation.setPivotY(-pivot.y);
        yRotation.setPivotZ(pivot.z);
        zRotation.setPivotX(pivot.x);
        zRotation.setPivotY(-pivot.y);
        zRotation.setPivotZ(pivot.z);
    }

    private void updateRotation() {
        Vector3 rotation = transform.getRotationReadonly();
        xRotation.setAngle(rotation.x);
        yRotation.setAngle(rotation.y);
        zRotation.setAngle(rotation.z);
    }

    private void updateScale() {
        Vector3 scale = transform.getScaleReadonly();
        setScaleX(scale.x);
        setScaleY(scale.y);
        setScaleZ(scale.z);
    }

    @Override
    public WorldTransform getTransform() {
        return transform;
    }
}
