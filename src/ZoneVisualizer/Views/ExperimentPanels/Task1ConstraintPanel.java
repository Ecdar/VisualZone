package ZoneVisualizer.Views.ExperimentPanels;

import ZoneVisualizer.Constraints.*;
import ZoneVisualizer.Views.ConstraintPanel;

import java.util.Arrays;
import java.util.List;

public class Task1ConstraintPanel extends ConstraintPanel {

    public Task1ConstraintPanel() {
        super();
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
                new SingleClockConstraint(Inequality.LessThan, true, 25, clockDimensions.get(0)),
                new SingleClockConstraint(Inequality.LessThan, false, 15, clockDimensions.get(1)),
                new SingleClockConstraint(Inequality.LessThan, false, 13, clockDimensions.get(2)),
                new SingleClockConstraint(Inequality.GreaterThan, true, 5, clockDimensions.get(0)),
                new SingleClockConstraint(Inequality.GreaterThan, false, 2, clockDimensions.get(1)),
                new TwoClockConstraint(Inequality.LessThan, false, 8, clockDimensions.get(0), clockDimensions.get(1)),
                new TwoClockConstraint(Inequality.LessThan, false, 10, clockDimensions.get(0), clockDimensions.get(2)),
                new TwoClockConstraint(Inequality.LessThan, true, 0, clockDimensions.get(1), clockDimensions.get(0)),
                new TwoClockConstraint(Inequality.LessThan, true, 7, clockDimensions.get(1), clockDimensions.get(2)),
                new TwoClockConstraint(Inequality.LessThan, false, 0, clockDimensions.get(2), clockDimensions.get(0)),
                new TwoClockConstraint(Inequality.LessThan, false, 6, clockDimensions.get(2), clockDimensions.get(1))
        );
    }
}
