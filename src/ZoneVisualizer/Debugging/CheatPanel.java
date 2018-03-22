package ZoneVisualizer.Debugging;

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
                new SingleClockConstraint(Inequality.LessThan, true, 10, clockDimensions.get(0)),
                new SingleClockConstraint(Inequality.GreaterThan, false, 2, clockDimensions.get(1)),
                new SingleClockConstraint(Inequality.LessThan, false, 10, clockDimensions.get(1)),
                new SingleClockConstraint(Inequality.GreaterThan, true, 2, clockDimensions.get(3)),
                new SingleClockConstraint(Inequality.LessThan, false, 10, clockDimensions.get(3)),
                new TwoClockConstraint(Inequality.GreaterThan, false, -6, clockDimensions.get(1), clockDimensions.get(0)),
                new TwoClockConstraint(Inequality.LessThan, true, 6, clockDimensions.get(1), clockDimensions.get(0)),
                new TwoClockConstraint(Inequality.LessThan, true, 4, clockDimensions.get(1), clockDimensions.get(3)),
                new TwoClockConstraint(Inequality.GreaterThan, false, -7, clockDimensions.get(3), clockDimensions.get(0))
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

    private  void testAddButtonPress(ActionEvent event) {
        List<Constraint> chosenConstraints = new ArrayList<>();
        for (int i = 0; i < constraints.size(); i++) {
            if (constraintCheckBoxes.get(i).isSelected()) {
                chosenConstraints.add(constraints.get(i));
            }
        }

        ZoneVisualization.initialize(clockDimensions, chosenConstraints);

        ArrayList<Shape3D> shapes = new ArrayList<>();
        Tetragon rect = new Tetragon(-1, -1, 0, -1, 1, 0, 1, 1, 0, 1, -1, 0);
        rect.setMaterial(new PhongMaterial(Color.RED));
        rect.getTransform().setPosition(5, 5, 5);
        rect.getTransform().setRotation(0, 0, 45);
        rect.getTransform().setScale(1, 1, 1);
        shapes.add(rect);
        ZoneVisualizationApp.set3DContent(shapes);
    }
}
