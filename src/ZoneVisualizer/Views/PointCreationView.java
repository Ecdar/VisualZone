package ZoneVisualizer.Views;

import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.Arrays;
import java.util.List;

public class PointCreationView extends GridPane {

    private final NumericField xField = new NumericField("0");
    private final NumericField yField = new NumericField("0");
    private final NumericField zField = new NumericField("0");

    public PointCreationView(double width) {
        setPrefWidth(width);
        setHgap(5);
        setVgap(5);
        setPadding(new Insets(5, 5, 10, 5));

        setupInputFields(width);

        setupButtons(width);
    }

    private void setupButtons(double width) {
        Button addPointButton = new Button("Add Point");
        getChildren().add(addPointButton);
        setRowIndex(addPointButton, 2);
        setColumnSpan(addPointButton, 3);
        addPointButton.setPrefWidth((width - 10) / 2);
        setHalignment(addPointButton, HPos.CENTER);
        addPointButton.setOnAction(this::addPoint);

        Button clearPointsButton = new Button("Clear Points");
        getChildren().add(clearPointsButton);
        setRowIndex(clearPointsButton, 3);
        setColumnSpan(clearPointsButton, 3);
        clearPointsButton.setPrefWidth((width - 10) / 2);
        setHalignment(clearPointsButton, HPos.CENTER);
        clearPointsButton.setOnAction(this::clearPoints);
    }

    private void setupInputFields(double width) {
        Label xLabel = new Label("X"), yLabel = new Label("Y"), zLabel = new Label("Z");
        getChildren().addAll(xLabel, yLabel, zLabel, xField, yField, zField);
        setRowIndex(xLabel, 0);
        setColumnIndex(xLabel, 0);
        setRowIndex(yLabel, 0);
        setColumnIndex(yLabel, 1);
        setRowIndex(zLabel, 0);
        setColumnIndex(zLabel, 2);

        List<NumericField> fields = Arrays.asList(xField, yField, zField);
        for (int i = 0; i < fields.size(); i++) {
            NumericField field = fields.get(i);
            setRowIndex(field, 1);
            setColumnIndex(field, i);
            field.setPrefWidth((width - 20) / 3);
            field.setPrefHeight(20);
        }
    }

    private void addPoint(ActionEvent event) {
    }

    private void clearPoints(ActionEvent event) {
    }
}
