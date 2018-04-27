package ZoneVisualizer.Zones;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Constraints.Constraint;
import ZoneVisualizer.Constraints.TwoClockConstraint;

import java.util.Collection;

public class VertexPotential {

    private final TwoClockConstraint constraint;
    private Collection<? extends Constraint> resolution;

    public VertexPotential(TwoClockConstraint constraint) {
        this.constraint = constraint;
    }

    public TwoClockConstraint getConstraint() {
        return constraint;
    }

    public Clock getOldDimension() {
        return constraint.getClock1();
    }

    public Clock getNewDimension() {
        return constraint.getClock2();
    }

    public Collection<? extends Constraint> getResolution() {
        return resolution;
    }

    public void setResolution(Collection<? extends Constraint> resolution) {
        this.resolution = resolution;
    }

    public Clock getOtherDimension(Clock clock) {
        return constraint.getOtherClock(clock);
    }

    @Override
    public String toString() {
        return constraint.toString() + ", " + (resolution == null ? "unresolved" : "(" + resolution + ")");
    }
}
