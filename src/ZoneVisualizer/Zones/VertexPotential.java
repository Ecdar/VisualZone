package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.Constraints.SingleClockConstraint;
import ZoneVisualizer.Constraints.TwoClockConstraint;

public class VertexPotential {

    private final TwoClockConstraint constraint;
    private Constraint resolution;
    private final Clock oldDimension;
    private final Clock newDimension;

    public VertexPotential(TwoClockConstraint constraint, Clock oldDimension, Clock newDimension) {
        this.constraint = constraint;
        this.oldDimension = oldDimension;
        this.newDimension = newDimension;
    }

    public TwoClockConstraint getConstraint() {
        return constraint;
    }

    public Clock getOldDimension() {
        return oldDimension;
    }

    public Clock getNewDimension() {
        return newDimension;
    }

    public Constraint getResolution() {
        return resolution;
    }

    public void setResolution(Constraint resolution) {
        this.resolution = resolution;
    }
}
