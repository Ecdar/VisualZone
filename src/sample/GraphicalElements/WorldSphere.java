package sample.GraphicalElements;

import javafx.scene.shape.Sphere;

public class WorldSphere extends Sphere implements Object3D {

    private final WorldTransform transform = new WorldTransform();
    private final TransformUpdater transformUpdater;

    public WorldSphere() {
        super();

        transformUpdater = new TransformUpdater(this, transform);
    }

    public WorldSphere(double radius) {
        super(radius);

        transformUpdater = new TransformUpdater(this, transform);
    }

    public WorldSphere(double radius, int divisions) {
        super(radius, divisions);

        transformUpdater = new TransformUpdater(this, transform);
    }

    @Override
    public WorldTransform getTransform() {
        return transform;
    }
}
