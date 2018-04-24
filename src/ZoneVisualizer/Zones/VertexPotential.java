package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.Constraints.TwoClockConstraint;

public class VertexPotential {

    private final TwoClockConstraint constraint;
    private final Clock oldDimension;
    private final Clock newDimension;

    public VertexPotential(TwoClockConstraint constraint, Clock oldDimension, Clock newDimension) {
        this.constraint = constraint;
        this.oldDimension = oldDimension;
        this.newDimension = newDimension;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public Clock getOldDimension() {
        return oldDimension;
    }

    public Clock getNewDimension() {
        return newDimension;
    }
}
