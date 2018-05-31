package ZoneVisualizer.Views;

import ZoneVisualizer.Constraints.*;

import java.util.Arrays;
import java.util.List;

public class CheatPanel extends ConstraintPanel {

    public CheatPanel(double prefWidth) {
        super(prefWidth);
    }

    @Override
    protected List<Clock> getClocks() {
        return Arrays.asList(
                new Clock("cooldown"),
                new Clock("work"),
                new Clock("patience"),
                new Clock("tired"),
                new Clock("angry")
        );
    }

    @Override
    protected List<Constraint> getConstraints() {
        return Arrays.asList(
                new SingleClockConstraint(Inequality.GreaterThan, false, 2, clockDimensions.get(0)),
                new SingleClockConstraint(Inequality.LessThan, true, 12, clockDimensions.get(0)),
                new SingleClockConstraint(Inequality.GreaterThan, false, 1, clockDimensions.get(1)),
                new SingleClockConstraint(Inequality.LessThan, false, 10, clockDimensions.get(1)),
                new SingleClockConstraint(Inequality.GreaterThan, true, 5, clockDimensions.get(2)),
                new SingleClockConstraint(Inequality.LessThan, false, 15, clockDimensions.get(2)),
                new SingleClockConstraint(Inequality.GreaterThan, false, 3, clockDimensions.get(3)),
                new SingleClockConstraint(Inequality.GreaterThan, false, 4, clockDimensions.get(4)),
                new TwoClockConstraint(Inequality.GreaterThan, false, -6, clockDimensions.get(1), clockDimensions.get(0)),
                new TwoClockConstraint(Inequality.LessThan, true, 6, clockDimensions.get(1), clockDimensions.get(0)),
                new TwoClockConstraint(Inequality.LessThan, true, 4, clockDimensions.get(1), clockDimensions.get(2)),
                new TwoClockConstraint(Inequality.GreaterThan, false, -4, clockDimensions.get(2), clockDimensions.get(0)),
//                new TwoClockConstraint(Inequality.LessThan, true, 6, clockDimensions.get(2), clockDimensions.get(4)),
//                new TwoClockConstraint(Inequality.LessThan, true, 4, clockDimensions.get(0), clockDimensions.get(3)),
//                new TwoClockConstraint(Inequality.LessThan, true, 6, clockDimensions.get(1), clockDimensions.get(3)),
//                new TwoClockConstraint(Inequality.LessThan, true, 4, clockDimensions.get(1), clockDimensions.get(4))

                new SingleClockConstraint(Inequality.LessThan, false, 8, clockDimensions.get(0)),
                new TwoClockConstraint(Inequality.LessThan, false, -2, clockDimensions.get(2), clockDimensions.get(0)),
                new TwoClockConstraint(Inequality.LessThan, true, -2, clockDimensions.get(1), clockDimensions.get(2)),
                new TwoClockConstraint(Inequality.LessThan, true, 5, clockDimensions.get(2), clockDimensions.get(1)),
                new TwoClockConstraint(Inequality.LessThan, true, 4, clockDimensions.get(0), clockDimensions.get(2))
        );
    }
}
