package ZoneVisualizer.Views;

import ZoneVisualizer.*;
import ZoneVisualizer.Constraints.*;
import ZoneVisualizer.GraphicalElements.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CheatPanel extends GridPane {

    private Button testButton;
    private List<CheckBox> constraintCheckBoxes;
    private List<Constraint> constraints;
    private List<Clock> clockDimensions;

    public CheatPanel() {
        testButton = new Button("Test Add");
        testButton.setOnAction(this::testAddButtonPress);
        clockDimensions = Arrays.asList(
            new Clock("cooldown"),
            new Clock("work"),
            new Clock("patience"),
            new Clock("tired"),
            new Clock("angry")
        );
        constraints = Arrays.asList(
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
        constraintCheckBoxes = constraints.stream().map(c -> new CheckBox(c.toString())).collect(Collectors.toList());
        constraintCheckBoxes.forEach(cb -> cb.setSelected(true));

        this.getChildren().addAll(constraintCheckBoxes);
        this.getChildren().addAll(testButton);
        for (int i = 0; i < constraintCheckBoxes.size(); i++) {
            setRowIndex(constraintCheckBoxes.get(i), i);
        }
        setRowIndex(testButton, constraintCheckBoxes.size());
    }

    private void testAddButtonPress(ActionEvent event) {
        List<Constraint> chosenConstraints = new ArrayList<>();
        for (int i = 0; i < constraints.size(); i++) {
            if (constraintCheckBoxes.get(i).isSelected()) {
                chosenConstraints.add(constraints.get(i));
            }
        }

        ZoneVisualization.initialize(clockDimensions, chosenConstraints, 50);
    }
}
