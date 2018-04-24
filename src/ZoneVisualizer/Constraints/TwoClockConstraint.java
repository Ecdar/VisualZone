package ZoneVisualizer.Constraints;

import ZoneVisualizer.GraphicalElements.Vector3;
import com.sun.deploy.util.ArrayUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TwoClockConstraint extends Constraint {

    protected final Clock clock1;
    protected final Clock clock2;
    protected TwoClockRestrictionType restrictionType;

    public TwoClockConstraint(Inequality inequality, boolean inclusive, double nValue, Clock clock1, Clock clock2) {
        super(inequality, inclusive, nValue);
        this.clock1 = clock1;
        this.clock2 = clock2;
    }

    public Clock getClock1() {
        return clock1;
    }

    public Clock getClock2() {
        return clock2;
    }

    public Clock getOtherClock(Clock clock) {
        if (clock == clock1) {
            return clock2;
        }
        if (clock == clock2) {
            return clock1;
        }
        return null;
    }

    public double getOtherValue(Clock knownDimension, double knownValue) {
        if (knownDimension == getClock1()) {
            return getnValue() + knownValue;
        }
        return knownValue - getnValue();
    }

    public TwoClockConstraint getInvertedConstraint() {
        Inequality invertedInequality = inequality;
        switch (inequality) {
            case LessThan:
                invertedInequality = Inequality.GreaterThan;
                break;
            case GreaterThan:
                invertedInequality = Inequality.LessThan;
                break;
        }
        return new TwoClockConstraint(invertedInequality, inclusive, -nValue, clock2, clock1);
    }

    @Override
    public String toString() {
        return clock1.getName() + " - " + clock2.getName() + " " + super.toString();
    }

    public TwoClockRestrictionType getRestrictionType() {
        return restrictionType;
    }

    @Override
    public Vector3 getProjectedNormal(Clock dimension1, Clock dimension2, Clock dimension3) {
        Vector3 result = new Vector3();
        if (clock1 == dimension1) {
            result.x -= 1;
        }
        if (clock1 == dimension2) {
            result.y -= 1;
        }
        if (clock1 == dimension3) {
            result.z -= 1;
        }
        if (clock2 == dimension1) {
            result.x += 1;
        }
        if (clock2 == dimension2) {
            result.y += 1;
        }
        if (clock2 == dimension3) {
            result.z += 1;
        }
        return result;
    }

    @Override
    public double getNormalComponent(Clock dimension) {
        if (clock1 == dimension) {
            return -1;
        }
        if (clock2 == dimension) {
            return 1;
        }
        return 0;
    }

    @Override
    public Collection<Clock> clocksAsCollection() {
        return Arrays.asList(clock1, clock2);
    }

    public void setRestrictionType(TwoClockRestrictionType restrictionType) {
        if (restrictionType != TwoClockRestrictionType.NotFound) {
            this.restrictionType = restrictionType;
        }
    }
}
