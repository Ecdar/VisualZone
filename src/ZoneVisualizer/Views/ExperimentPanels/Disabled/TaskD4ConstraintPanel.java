package ZoneVisualizer.Views.ExperimentPanels.Disabled;

import ZoneVisualizer.Constraints.*;
import ZoneVisualizer.Views.ConstraintPanel;

import java.util.Arrays;
import java.util.List;

public class TaskD4ConstraintPanel extends ConstraintPanel {

    public TaskD4ConstraintPanel(double prefWidth) {
        super(prefWidth);
    }

    @Override
    protected List<Clock> getClocks() {
        return Arrays.asList(
                new Clock("x"),
                new Clock("y"),
                new Clock("z"),
                new Clock("w")
        );
    }

    @Override
    protected List<Constraint> getConstraints() {
        return Arrays.asList(
//                new SingleClockConstraint(Inequality.GreaterThan, true, 9, clockDimensions.get(0)),
                new SingleClockConstraint(Inequality.GreaterThan, false, 2, clockDimensions.get(1)),
                new SingleClockConstraint(Inequality.GreaterThan, true, 9, clockDimensions.get(2)),
                new SingleClockConstraint(Inequality.GreaterThan, true, 0, clockDimensions.get(3)),
//                new SingleClockConstraint(Inequality.LessThan, true, 19, clockDimensions.get(0)),
//                new SingleClockConstraint(Inequality.LessThan, true, 10, clockDimensions.get(1)),
                new SingleClockConstraint(Inequality.LessThan, true, 14, clockDimensions.get(2)),
                new SingleClockConstraint(Inequality.LessThan, true, 5, clockDimensions.get(3)),

                new TwoClockConstraint(Inequality.GreaterThan, true, 9, clockDimensions.get(0), clockDimensions.get(3)),
                new TwoClockConstraint(Inequality.LessThan, true, 14,   clockDimensions.get(0), clockDimensions.get(3)),

                new TwoClockConstraint(Inequality.GreaterThan, false, 2, clockDimensions.get(1), clockDimensions.get(3)),
                new TwoClockConstraint(Inequality.LessThan, true, 5,     clockDimensions.get(1), clockDimensions.get(3)),

                new TwoClockConstraint(Inequality.GreaterThan, true, -5, clockDimensions.get(2), clockDimensions.get(0)),
                new TwoClockConstraint(Inequality.LessThan, true, 0,     clockDimensions.get(2), clockDimensions.get(0)),

                new TwoClockConstraint(Inequality.GreaterThan, true, 4, clockDimensions.get(2), clockDimensions.get(1)),
                new TwoClockConstraint(Inequality.LessThan, true, 10,   clockDimensions.get(2), clockDimensions.get(1)),

                new TwoClockConstraint(Inequality.GreaterThan, true, 9, clockDimensions.get(2), clockDimensions.get(3)),
                new TwoClockConstraint(Inequality.LessThan, false, 13,  clockDimensions.get(2), clockDimensions.get(3))
        );
    }
}
