package ZoneVisualizer.Constraints;

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
}
