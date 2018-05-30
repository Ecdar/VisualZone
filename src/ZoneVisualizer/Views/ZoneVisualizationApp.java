package ZoneVisualizer.Views;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.GraphicalElements.Vector3;
import ZoneVisualizer.Views.ExperimentPanels.Task1ConstraintPanel;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Shape3D;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ZoneVisualizationApp extends Application {

    private static Scene3D sub3DScene;
    private static DimensionListView dimensionListView;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Zone Visualization");
        primaryStage.setScene(new Scene(setupScene()));
        primaryStage.show();
    }

    private static Parent setupScene() throws Exception {
        sub3DScene = new Scene3D(640, 480);
        dimensionListView = new DimensionListView(240);

        VBox leftBarParent = new VBox(5);
        leftBarParent.getChildren().addAll(dimensionListView, new PointCreationView(240), new Task1ConstraintPanel());

        HBox parent = new HBox(10);
        parent.getChildren().addAll(leftBarParent, sub3DScene);

        return new Group(parent);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void setClockDimensions(List<Clock> clocks) {
        dimensionListView.setClockDimensions(clocks);
    }

    public static void setDisableRemainingClockDimensions(boolean disable) {
        dimensionListView.setDisableRemainingClockDimensions(disable);
    }

    public static void set3DZone(Collection<? extends Shape3D> content) {
        sub3DScene.setZone(content);
    }

    public static void set3DContent(Shape3D... content) {
        set3DContent(Arrays.asList(content));
    }

    public static void set3DContent(Collection<? extends Shape3D> content) {
        sub3DScene.set3DContent(content);
    }

    public static void add3DContent(Shape3D... content) {
        add3DContent(Arrays.asList(content));
    }

    public static void add3DContent(Collection<? extends Shape3D> content) {
        sub3DScene.add3DContent(content);
    }

    public static void remove3DContent(Shape3D... content) {
        remove3DContent(Arrays.asList(content));
    }

    public static void remove3DContent(Collection<? extends Shape3D> content) {
        sub3DScene.remove3DContent(content);
    }

    public static void setCamera2D(Vector3 focusPoint, Clock clock1, Clock clock2) {
        sub3DScene.setCamera2D(focusPoint, clock1, clock2);
    }

    public static void setCamera3D(Vector3 focusPoint, Clock clock1, Clock clock2, Clock clock3) {
        sub3DScene.setCamera3D(focusPoint, clock1, clock2, clock3);
    }
}
