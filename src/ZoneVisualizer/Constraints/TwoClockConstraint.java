package ZoneVisualizer.Constraints;

public class TwoClockConstraint extends Constraint {

    protected Clock clock1;
    protected Clock clock2;

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
}
