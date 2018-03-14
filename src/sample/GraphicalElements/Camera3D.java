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
    private final TransformUpdater transformUpdater;

    public Camera3D() {
        super(true);

        getTransforms().addAll(xRotation, yRotation, zRotation);
        transformUpdater = new TransformUpdater(this, transform, xRotation, yRotation, zRotation);

        setNearClip(0.5);
        setFarClip(CameraContext.getMaxZoom() - CameraContext.getMinZoom());
    }

    @Override
    public WorldTransform getTransform() {
        return transform;
    }
}
