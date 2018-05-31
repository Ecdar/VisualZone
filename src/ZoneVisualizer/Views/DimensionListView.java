package ZoneVisualizer.Views;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.Utility.LINQ;
import ZoneVisualizer.ZoneVisualization;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class DimensionListView extends ScrollPane {

    private final ObservableList<Node> dimensionUI;

    public DimensionListView(double width) {
        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setPrefWidth(width);
        setFitToHeight(true);

        VBox dimensionRoot = new VBox(5);
        dimensionRoot.setPadding(new Insets(15, 0, 0, 10));
        setContent(dimensionRoot);
        dimensionUI = dimensionRoot.getChildren();
        dimensionRoot.setPrefHeight(250);
    }

    public void setClockDimensions(List<Clock> clocks) {
        dimensionUI.clear();
        int i = 0;
        for (Clock clock : clocks) {
            CheckBox checkBox = new CheckBox(clock.getName());
            dimensionUI.add(checkBox);

            if (i < 3) {
                i++;
                checkBox.setSelected(true);
            }
            else {
                checkBox.setDisable(true);
            }

            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    ZoneVisualization.chooseClockDimension(clock);
                }
                else {
                    ZoneVisualization.removeClockDimension(clock);
                }
            });
        }
    }

    public void setDisableRemainingClockDimensions(boolean disable) {
        for (CheckBox cb : LINQ.<Node, CheckBox>cast(dimensionUI)) {
            if (!cb.isSelected()) {
                cb.setDisable(disable);
            }
        }
    }
}
