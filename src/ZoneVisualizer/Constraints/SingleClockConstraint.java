package ZoneVisualizer.Constraints;

public class SingleClockConstraint extends Constraint {

    protected Clock clock;

    public SingleClockConstraint(Inequality inequality, double nValue, Clock clock) {
        super(inequality, nValue);
        this.clock = clock;
    }

    @Override
    public boolean holds() {
        switch (inequality) {
            case GreaterThan:
                return clock.value > getnValue();
            case SmallerThan:
                return clock.value < getnValue();
            case GreaterThanEqual:
                return clock.value >= getnValue();
            case SmallerThanEqual:
                return clock.value <= getnValue();
            default:
                return false;
        }
    }

    public Clock getClock() {
        return clock;
    }
}
