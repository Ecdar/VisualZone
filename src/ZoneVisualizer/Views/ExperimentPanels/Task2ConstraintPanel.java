package ZoneVisualizer.Views.ExperimentPanels;

import ZoneVisualizer.Constraints.*;
import ZoneVisualizer.Views.ConstraintPanel;

import java.util.Arrays;
import java.util.List;

public class Task2ConstraintPanel extends ConstraintPanel {

    public Task2ConstraintPanel(double prefWidth) {
        super(prefWidth);
    }

    @Override
    protected List<Clock> getClocks() {
        return Arrays.asList(
                new Clock("x"),
                new Clock("y"),
                new Clock("z")
        );
    }

    @Override
    protected List<Constraint> getConstraints() {
        return Arrays.asList(
                new SingleClockConstraint(Inequality.GreaterThan, true, 1, clockDimensions.get(0)),
                new SingleClockConstraint(Inequality.GreaterThan, false, 10, clockDimensions.get(1)),
                new SingleClockConstraint(Inequality.GreaterThan, false, 5, clockDimensions.get(2)),
                new SingleClockConstraint(Inequality.LessThan, false, 14, clockDimensions.get(0)),
                new SingleClockConstraint(Inequality.LessThan, false, 22, clockDimensions.get(1)),
                new SingleClockConstraint(Inequality.LessThan, false, 19, clockDimensions.get(2)),

                new TwoClockConstraint(Inequality.GreaterThan, false, 8, clockDimensions.get(1), clockDimensions.get(0)),
                new TwoClockConstraint(Inequality.LessThan, true, 14,    clockDimensions.get(1), clockDimensions.get(0)),

                new TwoClockConstraint(Inequality.GreaterThan, false, 3, clockDimensions.get(2), clockDimensions.get(0)),
                new TwoClockConstraint(Inequality.LessThan, false, 6,    clockDimensions.get(2), clockDimensions.get(0)),

                new TwoClockConstraint(Inequality.GreaterThan, false, 3, clockDimensions.get(1), clockDimensions.get(2)),
                new TwoClockConstraint(Inequality.LessThan, false, 10,   clockDimensions.get(1), clockDimensions.get(2))
        );
    }
}
