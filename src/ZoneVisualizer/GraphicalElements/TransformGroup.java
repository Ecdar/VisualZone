package ZoneVisualizer.GraphicalElements;

import javafx.scene.Group;
import javafx.scene.Node;
import java.util.Collection;

public class TransformGroup extends Group implements Object3D {

    private final WorldTransform transform = new WorldTransform();
    private final TransformUpdater transformUpdater;

    public TransformGroup() {
        super();

        transformUpdater = new TransformUpdater(this, transform);
    }

    public TransformGroup(Node... children) {
        super(children);

        transformUpdater = new TransformUpdater(this, transform);
    }

    public TransformGroup(Collection<Node> children) {
        super(children);

        transformUpdater = new TransformUpdater(this, transform);
    }

    @Override
    public WorldTransform getTransform() {
        return transform;
    }
}
