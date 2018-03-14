package sample;

import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.GraphicalElements.*;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private static ObservableList<Node> dimensionUI;
    private static ObservableList<Node> zone3DUI;
    private static WorldTransform cameraTransform = new WorldTransform();
    private static Gizmo3D gizmo;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Zone Visualization");
        primaryStage.setScene(new Scene(setupScene()));
        primaryStage.show();

        Button testButton = new Button("Test Add");
        testButton.setOnAction(Main::testAddButtonPress);
        dimensionUI.add(testButton);
    }

    private static void testAddButtonPress(ActionEvent event) {
        ArrayList<String> dimensionNames = new ArrayList<>();
        dimensionNames.add("cooldown");
        dimensionNames.add("work");
        dimensionNames.add("patience");
        setDimensions(dimensionNames);

        ArrayList<Shape3D> shapes = new ArrayList<>();
        Tetragon rect = new Tetragon(-1, -1, 0, -1, 1, 0, 1, 1, 0, 1, -1, 0);
        rect.setMaterial(new PhongMaterial(Color.RED));
        rect.getTransform().setPosition(5, 10, 0);
        rect.getTransform().setRotation(0, 0, 45);
        rect.getTransform().setScale(1, 1, 1);
        shapes.add(rect);
        set3DContent(shapes);
    }

    public static Parent setupScene() throws Exception {
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

        HBox parent = new HBox(10);
        parent.getChildren().addAll(dimensionScrollPane, sub3DScene);

        return new Group(parent);
    }

    public static SubScene create3DScene() {
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
        cameraTransform.setPosition(10, 5, 0);
        cameraTransform.setPivot(5, 10, 0);
        cameraTransform.setRotation(0, -90, 0);

//        subScene.addEventHandler(ScrollEvent.ANY, camera.getScrollEventHandler());
//        subScene.addEventHandler(ZoomEvent.ANY, camera.getZoomEventHandler());

        return subScene;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void setDimensions(List<String> dimensions) {
        dimensionUI.clear();
        for (String dimension : dimensions) {
            CheckBox checkBox = new CheckBox(dimension);
            dimensionUI.add(checkBox);
        }
    }

    public static void set3DContent(List<Shape3D> content) {
        zone3DUI.clear();
        for (Shape3D shape : content) {
            zone3DUI.add(shape);
        }
    }
}
