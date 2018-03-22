package ZoneVisualizer.Constraints;

public class SingleClockConstraint extends Constraint {

    protected Clock clock;

    public SingleClockConstraint(Inequality inequality, boolean inclusive, double nValue, Clock clock) {
        super(inequality, inclusive, nValue);
        this.clock = clock;
    }

    public Clock getClock() {
        return clock;
    }

    @Override
    public String toString() {
        return clock.getName() + " " + super.toString();
    }
}
