package sample.GraphicalElements;

import javafx.event.EventHandler;
import javafx.scene.PerspectiveCamera;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Camera3D extends PerspectiveCamera implements Object3D {

    private Rotate xRotation = new Rotate(0, Rotate.X_AXIS);
    private Rotate yRotation = new Rotate(0, Rotate.Y_AXIS);
    private Rotate zRotation = new Rotate(0, Rotate.Z_AXIS);
    private final WorldTransform transform = new WorldTransform();

    public Camera3D() {
        super(true);

        getTransforms().addAll(xRotation, yRotation, zRotation);
        transform.addOnPositionChange(this::updatePosition);
        transform.addOnPivotChange(this::updatePivot);
        transform.addOnRotationChange(this::updateRotation);
        transform.addOnScaleChange(this::updateScale);

        setNearClip(0.5);
        setFarClip(CameraContext.getMaxZoom() - CameraContext.getMinZoom());
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
