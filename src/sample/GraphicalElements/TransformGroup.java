package sample.GraphicalElements;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;

import java.util.Collection;

public class TransformGroup extends Group implements Object3D {

    private Rotate xRotation = new Rotate(0, Rotate.X_AXIS);
    private Rotate yRotation = new Rotate(0, Rotate.Y_AXIS);
    private Rotate zRotation = new Rotate(0, Rotate.Z_AXIS);
    private final WorldTransform transform = new WorldTransform();

    public TransformGroup() {
        super();

        initialize();
    }

    public TransformGroup(Node... children) {
        super(children);

        initialize();
    }

    public TransformGroup(Collection<Node> children) {
        super(children);

        initialize();
    }

    private void initialize() {
        getTransforms().addAll(xRotation, yRotation, zRotation);

        transform.addOnPositionChange(this::updatePosition);
        transform.addOnPivotChange(this::updatePivot);
        transform.addOnRotationChange(this::updateRotation);
        transform.addOnScaleChange(this::updateScale);
    }

    private void updatePosition() {
        Vector3 position = transform.getPositionReadonly();
        double x = position.x, y = -position.y, z = position.z;
        setTranslateX(x);
        setTranslateY(y);
        setTranslateZ(z);
    }

    private void updatePivot() {
        Vector3 pivot = transform.getPivotReadonly();
        xRotation.setPivotX(pivot.x);
        xRotation.setPivotY(pivot.y);
        xRotation.setPivotZ(pivot.z);
        yRotation.setPivotX(pivot.x);
        yRotation.setPivotY(pivot.y);
        yRotation.setPivotZ(pivot.z);
        zRotation.setPivotX(pivot.x);
        zRotation.setPivotY(pivot.y);
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
