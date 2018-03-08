package sample;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.Transforms.Tetragon;
import sample.Transforms.WorldTransform;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private static ObservableList<Node> dimensionUI;
    private static ObservableList<Node> zone3DUI;
    private static WorldTransform cameraTransform = new WorldTransform();

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Zone Visualization");
        primaryStage.setScene(new Scene(setupScene()));
        primaryStage.show();

        Button testButton = new Button("Test Add");
        testButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ArrayList<String> dimensionNames = new ArrayList<>();
                dimensionNames.add("cooldown");
                dimensionNames.add("work");
                dimensionNames.add("patience");
                setDimensions(dimensionNames);

                ArrayList<Shape3D> shapes = new ArrayList<>();
                Tetragon rect = new Tetragon(-1, -1, 0, -1, 1, 0, 1, 1, 0, 1, -1, 0);
                rect.setMaterial(new PhongMaterial(Color.RED));
                rect.getTransform().setPosition(250, 100, 400);
                rect.getTransform().setScale(100, 100, 100);
                shapes.add(rect);
                set3DContent(shapes);
            }
        });
        dimensionUI.add(testButton);
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
        PerspectiveCamera camera = new PerspectiveCamera(false);
        Rotate cameraRotationX = new Rotate(0, Rotate.X_AXIS);
        Rotate cameraRotationY = new Rotate(0, Rotate.Y_AXIS);
        Rotate cameraRotationZ = new Rotate(0, Rotate.Z_AXIS);
        camera.getTransforms().addAll(cameraRotationX, cameraRotationY, cameraRotationZ);
        cameraTransform.addOnRotationChange(() -> {
            cameraRotationX.setAngle(cameraTransform.getRotationReadonly().x);
            cameraRotationY.setAngle(cameraTransform.getRotationReadonly().y);
            cameraRotationZ.setAngle(cameraTransform.getRotationReadonly().z);
        });
        cameraTransform.addOnPositionChange(() -> {
            camera.setTranslateX(cameraTransform.getPositionReadonly().x);
            camera.setTranslateY(cameraTransform.getPositionReadonly().y);
            camera.setTranslateZ(cameraTransform.getPositionReadonly().z);
        });
        cameraTransform.setPosition(0, 0, 0);

        Group subRoot = new Group();
        zone3DUI = subRoot.getChildren();

        SubScene subScene = new SubScene(subRoot, 640, 480,
                true, SceneAntialiasing.BALANCED);
        subScene.setCamera(camera);

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
