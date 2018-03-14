package sample.GraphicalElements;


import javafx.scene.Node;
import javafx.scene.transform.Rotate;

public class TransformUpdater {

    protected final Node node;
    protected final WorldTransform transform;
    protected final Rotate xRotation;
    protected final Rotate yRotation;
    protected final Rotate zRotation;

    public TransformUpdater(Node node, WorldTransform transform, Rotate xRotation, Rotate yRotation, Rotate zRotation) {
        this.transform = transform;
        this.node = node;
        this.xRotation = xRotation;
        this.yRotation = yRotation;
        this.zRotation = zRotation;

        transform.addOnPositionChange(this::updatePosition);
        transform.addOnPivotChange(this::updatePivot);
        transform.addOnRotationChange(this::updateRotation);
        transform.addOnScaleChange(this::updateScale);
    }

    protected void updatePosition() {
        Vector3 position = transform.getPositionReadonly();
        node.setTranslateX(position.x);
        node.setTranslateY(-position.y);
        node.setTranslateZ(position.z);
    }

    protected void updatePivot() {
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

    protected void updateRotation() {
        Vector3 rotation = transform.getRotationReadonly();
        xRotation.setAngle(rotation.x);
        yRotation.setAngle(rotation.y);
        zRotation.setAngle(rotation.z);
    }

    protected void updateScale() {
        Vector3 scale = transform.getScaleReadonly();
        node.setScaleX(scale.x);
        node.setScaleY(scale.y);
        node.setScaleZ(scale.z);
    }
}
