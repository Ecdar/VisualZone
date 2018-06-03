package ZoneVisualizer.Views.ExperimentPanels;

import ZoneVisualizer.Constraints.*;
import ZoneVisualizer.Views.ConstraintPanel;

import java.util.Arrays;
import java.util.List;

public class Task11ConstraintPanel extends ConstraintPanel {

    public Task11ConstraintPanel(double prefWidth) {
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
//                new SingleClockConstraint(Inequality.GreaterThan, false, 3, clockDimensions.get(0)),
                new SingleClockConstraint(Inequality.GreaterThan, false, 5, clockDimensions.get(1)),
                new SingleClockConstraint(Inequality.GreaterThan,false, 3, clockDimensions.get(2)),
//                new SingleClockConstraint(Inequality.LessThan, false, 25, clockDimensions.get(0)),
                new SingleClockConstraint(Inequality.LessThan, true, 25, clockDimensions.get(1)),
                new SingleClockConstraint(Inequality.LessThan, true, 23, clockDimensions.get(2)),

//                new TwoClockConstraint(Inequality.GreaterThan, false, -6, clockDimensions.get(0), clockDimensions.get(1)),
                new TwoClockConstraint(Inequality.LessThan, true, 0,    clockDimensions.get(0), clockDimensions.get(1)),

                new TwoClockConstraint(Inequality.GreaterThan, true, 0,  clockDimensions.get(0), clockDimensions.get(2)),
                new TwoClockConstraint(Inequality.LessThan, false, 5,     clockDimensions.get(0), clockDimensions.get(2)),

//                new TwoClockConstraint(Inequality.GreaterThan, true, 0, clockDimensions.get(1), clockDimensions.get(2)),
                new TwoClockConstraint(Inequality.LessThan, true, 6,   clockDimensions.get(1), clockDimensions.get(2))
        );
    }
}
