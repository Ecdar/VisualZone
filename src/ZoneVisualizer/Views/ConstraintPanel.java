package ZoneVisualizer.Views;

import ZoneVisualizer.*;
import ZoneVisualizer.Constraints.*;
import ZoneVisualizer.GraphicalElements.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ConstraintPanel extends ScrollPane {

    protected Button testButton;
    protected List<CheckBox> constraintCheckBoxes;
    protected List<Constraint> constraints;
    protected List<Clock> clockDimensions;
    private final ObservableList<Node> constraintUI;

    public ConstraintPanel(double prefWidth) {
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setPrefHeight(250);
        this.setPrefWidth(prefWidth);
        VBox constraintRoot = new VBox(5);
        constraintRoot.setPadding(new Insets(15, 0, 0, 10));
        setContent(constraintRoot);
        constraintUI = constraintRoot.getChildren();
        testButton = new Button("Test Add");
        testButton.setOnAction(this::testAddButtonPress);
        constraintUI.add(testButton);
        clockDimensions = getClocks();
        constraints = getConstraints();
        constraintCheckBoxes = constraints.stream().map(c -> new CheckBox(c.toString())).collect(Collectors.toList());
        constraintCheckBoxes.forEach(cb -> cb.setSelected(true));
        constraintUI.addAll(constraintCheckBoxes);
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
