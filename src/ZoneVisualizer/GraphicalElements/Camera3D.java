package ZoneVisualizer.GraphicalElements;

import javafx.scene.PerspectiveCamera;

public class Camera3D extends PerspectiveCamera implements Object3D {

    private final WorldTransform transform = new WorldTransform();
    private final TransformUpdater transformUpdater;

    public Camera3D() {
        super(true);

        transformUpdater = new TransformUpdater(this, transform);

        double camZoomDistance = CameraContext.getMaxZoom() - CameraContext.getMinZoom();
        setNearClip(0.5);
        setFarClip(2 * camZoomDistance);
    }

    @Override
    public WorldTransform getTransform() {
        return transform;
    }
}
