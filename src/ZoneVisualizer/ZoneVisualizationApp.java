package ZoneVisualizer;

import ZoneVisualizer.Debugging.CheatPanel;
import ZoneVisualizer.Utility.LINQ;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape3D;
import javafx.stage.Stage;
import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.GraphicalElements.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ZoneVisualizationApp extends Application {

    private static ObservableList<Node> dimensionUI;
    private static ObservableList<Node> zone3DUI;
    private static WorldTransform cameraTransform = new WorldTransform();
    private static Gizmo3D gizmo;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Zone Visualization");
        primaryStage.setScene(new Scene(setupScene()));
        primaryStage.show();
    }

    private static Parent setupScene() throws Exception {
        SubScene sub3DScene = create3DScene();

        //Create dimension list
        ScrollPane dimensionScrollPane = new ScrollPane();
        dimensionScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        dimensionScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        dimensionScrollPane.setPrefWidth(240);
        dimensionScrollPane.setFitToHeight(true);

        VBox dimensionRoot = new VBox(5);
        dimensionRoot.setPadding(new Insets(15, 0, 0, 10));
        dimensionScrollPane.setContent(dimensionRoot);
        dimensionUI = dimensionRoot.getChildren();

        //Debugging
        dimensionRoot.setPrefHeight(300);
        VBox leftBarParent = new VBox(5);
        leftBarParent.getChildren().addAll(dimensionScrollPane, new CheatPanel());
        //Debugging end

        HBox parent = new HBox(10);
        parent.getChildren().addAll(leftBarParent, sub3DScene);

        return new Group(parent);
    }

    private static SubScene create3DScene() {
        //Create 3D scene and content
        Camera3D camera = new Camera3D();
        camera.getTransform().setPosition(0, 0, -25);

        Group subRoot = new Group();
        subRoot.setAutoSizeChildren(false);
        zone3DUI = subRoot.getChildren();
        TransformGroup subParent = new TransformGroup();
        subParent.setAutoSizeChildren(false);
        gizmo = new Gizmo3D(0.1, 25);
        subParent.getChildren().addAll(subRoot, gizmo);

        CameraContext cameraContext = new CameraContext(camera, subParent.getTransform());
        cameraTransform = cameraContext.getFakeCameraTransform();

        SubScene subScene = new SubScene(subParent, 640, 480,
                true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.WHITE);
        subScene.setCamera(camera);
        cameraTransform.setPositionZ(0);
        cameraTransform.setRotation(0, 0, 0);

        subScene.addEventHandler(ScrollEvent.ANY, cameraContext::handleScrolling);
        subScene.addEventHandler(MouseEvent.ANY, cameraContext::handleMouseDrag);

        return subScene;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void setClockDimensions(List<Clock> clocks) {
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

    public static void disableRemainingClockDimensions(boolean disable) {
        for (CheckBox cb : LINQ.<Node, CheckBox>ofType(dimensionUI)) {
            if (!cb.isSelected()) {
                cb.setDisable(disable);
            }
        }
    }

    public static void set3DContent(Shape3D... content) {
        set3DContent(Arrays.asList(content));
    }

    public static void set3DContent(Collection<? extends Shape3D> content) {
        zone3DUI.clear();
        zone3DUI.addAll(content);
    }

    public static void setCamera2D(Vector3 focusPoint, Clock clock1, Clock clock2) {
        gizmo.showThirdDimension(false);
        cameraTransform.setRotation(Vector3.zero());
        cameraTransform.setPivot(focusPoint);
        cameraTransform.setPositionX(focusPoint.x);
        cameraTransform.setPositionY(focusPoint.y);
        CameraContext.setIs2D(true);

        gizmo.setXaxisName(clock1.getName());
        gizmo.setYaxisName(clock2.getName());
    }

    public static void setCamera3D(Vector3 focusPoint, Clock clock1, Clock clock2, Clock clock3) {
        gizmo.showThirdDimension(true);
        cameraTransform.setPivot(focusPoint);
        cameraTransform.setPositionX(focusPoint.x);
        cameraTransform.setPositionY(focusPoint.y);
        CameraContext.setIs2D(false);

        gizmo.setXaxisName(clock1.getName());
        gizmo.setYaxisName(clock2.getName());
        gizmo.setZaxisName(clock3.getName());
    }
}
