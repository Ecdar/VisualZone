package ZoneVisualizer.Constraints;

import ZoneVisualizer.GraphicalElements.Vector3;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SingleClockConstraint extends Constraint {

    protected final Clock clock;

    public SingleClockConstraint(Inequality inequality, boolean inclusive, double nValue, Clock clock) {
        super(inequality, inclusive, nValue);
        this.clock = clock;
    }

    @Override
    public Vector3 getProjectedNormal(Clock dimension1, Clock dimension2, Clock dimension3) {
        Vector3 result = new Vector3();
        if (clock == dimension1) {
            result.x = inequality == Inequality.GreaterThan ? 1 : -1;
        }
        if (clock == dimension2) {
            result.y = inequality == Inequality.GreaterThan ? 1 : -1;
        }
        if (clock == dimension3) {
            result.z = inequality == Inequality.GreaterThan ? 1 : -1;
        }
        return result;
    }

    @Override
    public double getNormalComponent(Clock dimension) {
        if (clock == dimension) {
            return inequality == Inequality.GreaterThan ? 1 : -1;
        }
        return 0;
    }

    public Clock getClock() {
        return clock;
    }

    @Override
    public Collection<Clock> clocksAsCollection() {
        return Arrays.asList(clock);
    }

    @Override
    public String toString() {
        return clock.getName() + " " + super.toString();
    }
}
