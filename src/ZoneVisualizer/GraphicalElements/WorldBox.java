package ZoneVisualizer.GraphicalElements;

import javafx.scene.shape.Box;

public class WorldBox extends Box implements Object3D {

    private final WorldTransform transform = new WorldTransform();
    private final TransformUpdater transformUpdater;

    public WorldBox() {
        super();

        transformUpdater = new TransformUpdater(this, transform);
    }

    public WorldBox(double width, double height, double depth) {
        super(width, height, depth);

        transformUpdater = new TransformUpdater(this, transform);
    }

    @Override
    public WorldTransform getTransform() {
        return transform;
    }
}
