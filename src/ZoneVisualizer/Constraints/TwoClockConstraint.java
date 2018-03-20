package ZoneVisualizer.Constraints;

public class TwoClockConstraint extends Constraint {

    protected Clock clock1;
    protected Clock clock2;

    public TwoClockConstraint(Inequality inequality, double nValue, Clock clock1, Clock clock2) {
        super(inequality, nValue);
        this.clock1 = clock1;
        this.clock2 = clock2;
    }

    public Clock getClock1() {
        return clock1;
    }

    public Clock getClock2() {
        return clock2;
    }

    public TwoClockConstraint getInvertedConstraint() {
        Inequality invertedInequality = inequality;
        switch (inequality) {
            case SmallerThan:
                invertedInequality = Inequality.GreaterThan;
                break;
            case SmallerThanEqual:
                invertedInequality = Inequality.GreaterThanEqual;
                break;
            case GreaterThan:
                invertedInequality = Inequality.SmallerThan;
                break;
            case GreaterThanEqual:
                invertedInequality = Inequality.SmallerThanEqual;
                break;
        }
        return new TwoClockConstraint(invertedInequality, -nValue, clock2, clock1);
    }
}
