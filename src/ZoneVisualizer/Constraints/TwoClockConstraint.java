package ZoneVisualizer.Constraints;

public class TwoClockConstraint extends Constraint {

    protected Clock clock1;
    protected Clock clock2;

    public TwoClockConstraint(Inequality inequality, double nValue, Clock clock1, Clock clock2) {
        super(inequality, nValue);
        this.clock1 = clock1;
        this.clock2 = clock2;
    }

    @Override
    public boolean holds() {
        switch (inequality) {
            case GreaterThan:
                return clock1.value - clock2.value > getnValue();
            case SmallerThan:
                return clock1.value - clock2.value < getnValue();
            case GreaterThanEqual:
                return clock1.value - clock2.value >= getnValue();
            case SmallerThanEqual:
                return clock1.value - clock2.value <= getnValue();
            default:
                return false;
        }
    }

    public Clock getClock1() {
        return clock1;
    }

    public Clock getClock2() {
        return clock2;
    }
}
