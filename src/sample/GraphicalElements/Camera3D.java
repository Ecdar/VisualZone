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
    private Translate translate = new Translate(0, 0, 0);
    private final WorldTransform transform = new WorldTransform();

    private Vector3 focusPoint = new Vector3();
    private double minZoom = 100, maxZoom = 0;
    private double zoomSpeed = 0.2;

    private final EventHandler<ScrollEvent> scrollEventHandler = event -> {
        double z = transform.getPositionReadonly().z + (event.getDeltaY() * zoomSpeed);
        z = Math.max(z,-minZoom);
        z = Math.min(z,maxZoom);
        transform.setPositionZ(z);
    };
    private final EventHandler<ZoomEvent> zoomEventHandler = event -> {
        System.out.print("Zooming\n");
        if (!Double.isNaN(event.getZoomFactor()) && event.getZoomFactor() > 0.8 && event.getZoomFactor() < 1.2) {
            double z = getTransform().getPositionReadonly().z / event.getZoomFactor();
            z = Math.max(z,-minZoom);
            z = Math.min(z,maxZoom);
            getTransform().setPositionZ(z);
        }
    };

    public Camera3D(double minZoom, double maxZoom) {
        this();
        this.minZoom = minZoom;
        this.maxZoom = maxZoom;
    }

    public Camera3D() {
        super(true);

        getTransforms().addAll(translate, xRotation, yRotation, zRotation);
        transform.addOnPositionChange(this::updatePosition);
        transform.addOnRotationChange(this::updateRotation);
        transform.addOnScaleChange(this::updateScale);
    }

    private void updatePosition() {
        translate.setX(transform.getPositionReadonly().x);
        translate.setY(-transform.getPositionReadonly().y);
        translate.setZ(transform.getPositionReadonly().z);
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

    public EventHandler<ScrollEvent> getScrollEventHandler() {
        return scrollEventHandler;
    }

    public EventHandler<ZoomEvent> getZoomEventHandler() {
        return zoomEventHandler;
    }

    public Vector3 getFocusPointReadonly() {
        return (Vector3) focusPoint.clone();
    }

    public void setFocusPoint(Vector3 focusPoint) {
        this.focusPoint = focusPoint;


    }

    public double getZoomSpeed() {
        return zoomSpeed;
    }

    public void setZoomSpeed(double zoomSpeed) {
        this.zoomSpeed = zoomSpeed;
    }
}
