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
    private final TransformUpdater transformUpdater;

    public TransformGroup() {
        super();

        getTransforms().addAll(xRotation, yRotation, zRotation);
        transformUpdater = new TransformUpdater(this, transform, xRotation, yRotation, zRotation);
    }

    public TransformGroup(Node... children) {
        super(children);

        getTransforms().addAll(xRotation, yRotation, zRotation);
        transformUpdater = new TransformUpdater(this, transform, xRotation, yRotation, zRotation);
    }

    public TransformGroup(Collection<Node> children) {
        super(children);

        getTransforms().addAll(xRotation, yRotation, zRotation);
        transformUpdater = new TransformUpdater(this, transform, xRotation, yRotation, zRotation);
    }

    @Override
    public WorldTransform getTransform() {
        return transform;
    }
}
