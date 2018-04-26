package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.Constraints.TwoClockConstraint;

import java.util.Collection;

public class VertexPotential {

    private final TwoClockConstraint constraint;
    private Collection<? extends Constraint> resolution;
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

    public Collection<? extends Constraint> getResolution() {
        return resolution;
    }

    public void setResolution(Collection<? extends Constraint> resolution) {
        this.resolution = resolution;
    }

    public Clock getOtherDimension(Clock clock) {
        if (clock == newDimension) {
            return oldDimension;
        }
        if (clock == oldDimension) {
            return newDimension;
        }
        return null;
    }

    @Override
    public String toString() {
        return constraint.toString() + ", " + (resolution == null ? "unresolved" : "(" + resolution + ")");
    }
}
