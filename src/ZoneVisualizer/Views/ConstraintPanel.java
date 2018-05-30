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

public abstract class ConstraintPanel extends GridPane {

    protected Button testButton;
    protected List<CheckBox> constraintCheckBoxes;
    protected List<Constraint> constraints;
    protected List<Clock> clockDimensions;

    public ConstraintPanel() {
        testButton = new Button("Test Add");
        testButton.setOnAction(this::testAddButtonPress);
        clockDimensions = getClocks();
        constraints = getConstraints();
        constraintCheckBoxes = constraints.stream().map(c -> new CheckBox(c.toString())).collect(Collectors.toList());
        constraintCheckBoxes.forEach(cb -> cb.setSelected(true));

        this.getChildren().addAll(constraintCheckBoxes);
        this.getChildren().addAll(testButton);
        for (int i = 0; i < constraintCheckBoxes.size(); i++) {
            setRowIndex(constraintCheckBoxes.get(i), i);
        }
        setRowIndex(testButton, constraintCheckBoxes.size());
    }

    protected abstract List<Clock> getClocks();

    protected abstract List<Constraint> getConstraints();

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
