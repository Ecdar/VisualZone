package ZoneVisualizer.Constraints;

public abstract class Constraint {

    protected Inequality inequality;
    protected double nValue;

    public Constraint(Inequality inequality, double nValue) {
        this.inequality = inequality;
        this.nValue = nValue;
    }

    public Inequality getInequality() {
        return inequality;
    }

    public int getInequalityAsInt() {
        switch (inequality) {
            case GreaterThan:
                return 0;
            case GreaterThanEqual:
                return 1;
            case SmallerThanEqual:
                return 2;
            case SmallerThan:
                return 3;
                default:
                    return -1;
        }
    }

    public double getnValue() {
        return nValue;
    }
}
