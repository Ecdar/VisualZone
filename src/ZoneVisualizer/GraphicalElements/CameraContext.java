package ZoneVisualizer.GraphicalElements;

import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class CameraContext {

    private static double zoomSpeed = 0.2;
    private static double maxZoom = 25;
    private static double minZoom = -100;
    private static double pivotSpeed = 0.5;

    private Camera3D camera;
    private WorldTransform sceneTransform;
    private WorldTransform fakeCameraTransform;

    private double dragStartX, dragStartY;
    private double startRotationX, startRotationY;

    public CameraContext(Camera3D camera, WorldTransform sceneTransform) {
        this.camera = camera;
        this.sceneTransform = sceneTransform;

        fakeCameraTransform = new WorldTransform(
                sceneTransform.getPositionReadonly().multiply(-1),
                sceneTransform.getPivotReadonly(),
                sceneTransform.getRotationReadonly().multiply(-1),
                sceneTransform.getScaleReadonly().multiply(-1));

        fakeCameraTransform.addOnPositionChange(this::updateScenePosition);
        fakeCameraTransform.addOnPivotChange(this::updateScenePivot);
        fakeCameraTransform.addOnRotationChange(this::updateSceneRotation);
        fakeCameraTransform.addOnScaleChange(this::updateSceneScale);
    }

    private void updateScenePosition() {
        Vector3 camPosition = fakeCameraTransform.getPositionReadonly();
        sceneTransform.setPosition(camPosition.multiply(-1));
    }

    private void updateScenePivot() {
        Vector3 camPivot = fakeCameraTransform.getPivotReadonly();
        sceneTransform.setPivot(camPivot);
    }

    private void updateSceneRotation() {
        Vector3 camRotation = fakeCameraTransform.getRotationReadonly();
        sceneTransform.setRotation(camRotation.multiply(-1));
    }

    private void updateSceneScale() {
        Vector3 camScale = fakeCameraTransform.getScaleReadonly();
        sceneTransform.setScale(camScale.multiply(-1));
    }

    public void handleScrolling(ScrollEvent event) {
        double z = fakeCameraTransform.getPositionReadonly().z;
        z += event.getDeltaY() * zoomSpeed;
        z = Math.max(z, minZoom);
        z = Math.min(z, maxZoom);
        fakeCameraTransform.setPositionZ(z);
    }

    public void handleMouseDrag(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            dragStartX = event.getSceneX();
            dragStartY = event.getSceneY();
            startRotationX = fakeCameraTransform.getRotationReadonly().x;
            startRotationY = fakeCameraTransform.getRotationReadonly().y;
        }
        else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            double deltaX = event.getSceneX() - dragStartX;
            double deltaY = event.getSceneY() - dragStartY;

            fakeCameraTransform.setRotationX(startRotationX - deltaY * pivotSpeed);
            fakeCameraTransform.setRotationY(startRotationY - deltaX * pivotSpeed);
        }
    }

    public WorldTransform getFakeCameraTransform() {
        return fakeCameraTransform;
    }

    public static double getZoomSpeed() {
        return zoomSpeed;
    }

    public static void setZoomSpeed(double zoomSpeed) {
        CameraContext.zoomSpeed = zoomSpeed;
    }

    public static double getMaxZoom() {
        return maxZoom;
    }

    public static void setMaxZoom(double maxZoom) {
        CameraContext.maxZoom = maxZoom;
    }

    public static double getMinZoom() {
        return minZoom;
    }

    public static void setMinZoom(double minZoom) {
        CameraContext.minZoom = minZoom;
    }

    public static double getPivotSpeed() {
        return pivotSpeed;
    }

    public static void setPivotSpeed(double pivotSpeed) {
        CameraContext.pivotSpeed = pivotSpeed;
    }
}
