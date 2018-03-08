package sample.Transforms;

import com.sun.javafx.geom.PickRay;
import com.sun.javafx.scene.input.PickResultChooser;
import javafx.scene.Node;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Tetragon extends MeshView implements Object3D {

    private Rotate xRotation = new Rotate(0, Rotate.X_AXIS);
    private Rotate yRotation = new Rotate(0, Rotate.Y_AXIS);
    private Rotate zRotation = new Rotate(0, Rotate.Z_AXIS);
    private final WorldTransform transform = new WorldTransform();

    public Tetragon(Vector3 topLeft, Vector3 topRight, Vector3 bottomRight, Vector3 bottomLeft)  {
        this((float)topLeft.x, (float)topLeft.y, (float)topLeft.z,
             (float)topRight.x, (float)topRight.y, (float)topRight.z,
             (float)bottomRight.x, (float)bottomRight.y, (float)bottomRight.z,
             (float)bottomLeft.x, (float)bottomLeft.y, (float)bottomLeft.z);
    }

    public Tetragon(float tlX, float tlY, float tlZ, float trX, float trY, float trZ,
                    float brX, float brY, float brZ, float blX, float blY, float blZ) {
        super();
        TriangleMesh triangleMesh = new TriangleMesh();
        triangleMesh.getPoints().addAll(tlX, tlY, tlZ,
                                        trX, trY, trZ,
                                        brX, brY, brZ,
                                        blX, blY, blZ);
        triangleMesh.getTexCoords().addAll(0, 0, 1, 0, 1, 1, 0, 1);
        triangleMesh.getFaces().addAll( 0, 0, 1, 1, 2, 2,
                                        2, 2, 3, 3, 0, 0);

        setMesh(triangleMesh);
        setDrawMode(DrawMode.FILL);
        setCullFace(CullFace.NONE);
        updatePosition();
        transform.addOnPositionChange(this::updatePosition);
        updateRotation();
        transform.addOnRotationChange(this::updateRotation);
        updateScale();
        transform.addOnScaleChange(this::updateScale);

        getTransforms().addAll(xRotation, yRotation, zRotation);
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
