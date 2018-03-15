package ZoneVisualizer.Constraints;

public class SingleClockConstraint extends Constraint {

    protected Clock clock;

    public SingleClockConstraint(Inequality inequality, double nValue, Clock clock) {
        super(inequality, nValue);
        this.clock = clock;
    }

    public Clock getClock() {
        return clock;
    }
}
