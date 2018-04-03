package ZoneVisualizer.Constraints;

import ZoneVisualizer.GraphicalElements.Vector3;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class Constraint {

    protected Inequality inequality;
    protected boolean inclusive;
    protected double nValue;

    public Constraint(Inequality inequality, boolean inclusive, double nValue) {
        this.inequality = inequality;
        this.inclusive = inclusive;
        this.nValue = nValue;
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
