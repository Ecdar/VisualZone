package ZoneVisualizer.Constraints;

import ZoneVisualizer.GraphicalElements.Vector3;

import java.util.Collection;

public abstract class Constraint {

    protected final Inequality inequality;
    protected final boolean inclusive;
    protected final double nValue;

    public Constraint(Inequality inequality, boolean inclusive, double nValue) {
        this.inequality = inequality;
        this.inclusive = inclusive;
        this.nValue = nValue;
    }

    public static SingleClockConstraint zeroBound(Clock dimension) {
        return new SingleClockConstraint(Inequality.GreaterThan, true, 0, dimension);
    }

    public static SingleClockConstraint infinityBound(Clock dimension) {
        return new SingleClockConstraint(Inequality.LessThan, true, Double.POSITIVE_INFINITY, dimension);
    }

    public Inequality getInequality() {
        return inequality;
    }

    public double getnValue() {
        return nValue;
    }

    public boolean isInclusive() {
        return inclusive;
    }

    public abstract Vector3 getProjectedNormal(Clock dimension1, Clock dimension2, Clock dimension3);

    public abstract double getNormalComponent(Clock dimension);

    public abstract Collection<Clock> clocksAsCollection();

    public abstract boolean isLowerBoundOnDimension(Clock dimension);

    @Override
    public String toString() {
        String result = "";
        if (inequality == Inequality.GreaterThan) {
            result += ">";
        }
        else if (inequality == Inequality.LessThan) {
            result += "<";
        }
        if (inclusive) {
            result += "=";
        }
        result += " " + nValue;

        return result;
    }
}
