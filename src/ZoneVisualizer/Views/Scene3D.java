package ZoneVisualizer.Views;

import ZoneVisualizer.Constraints.Clock;
import ZoneVisualizer.GraphicalElements.*;
import javafx.collections.ObservableList;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape3D;

import java.util.ArrayList;
import java.util.Collection;

public class Scene3D extends SubScene {

    private final Camera3D camera;
    private final ObservableList<Node> scene3DContent;
    private final WorldTransform cameraTransform;
    private final Gizmo3D gizmo;

    private final Collection<Shape3D> zone3DContent = new ArrayList<>();
    private final Collection<Shape3D> other3DContent = new ArrayList<>();

    public Scene3D(double width, double height) {
        this(width, height, true, SceneAntialiasing.BALANCED);
    }

    public Scene3D(double width, double height, boolean depthBuffer, SceneAntialiasing antiAliasing) {
        super(new TransformGroup(), width, height, depthBuffer, antiAliasing);
        //Create 3D scene and content
        camera = new Camera3D();
        camera.getTransform().setPosition(0, 0, -25);

        Group subRoot = new Group();
        subRoot.setAutoSizeChildren(false);
        scene3DContent = subRoot.getChildren();
        TransformGroup subParent = new TransformGroup();
        subParent.setAutoSizeChildren(false);
        gizmo = new Gizmo3D(0.1, 25);
        subParent.getChildren().addAll(gizmo, subRoot);

        CameraContext cameraContext = new CameraContext(camera, subParent.getTransform());
        cameraTransform = cameraContext.getFakeCameraTransform();

        setRoot(subParent);

        setFill(Color.WHITE);
        setCamera(camera);
        cameraTransform.setPositionZ(0);
        cameraTransform.setRotation(0, 0, 0);

        addEventHandler(ScrollEvent.ANY, cameraContext::handleScrolling);
        addEventHandler(MouseEvent.ANY, cameraContext::handleMouseDrag);
    }

    public void setZone(Collection<? extends Shape3D> content) {
        zone3DContent.clear();
        zone3DContent.addAll(content);
        updateSceneView();
    }

    public void set3DContent(Collection<? extends Shape3D> content) {
        other3DContent.clear();
        add3DContent(content);
    }

    public void add3DContent(Collection<? extends Shape3D> content) {
        other3DContent.addAll(content);
        updateSceneView();
    }

    public void remove3DContent(Collection<? extends Shape3D> content) {
        other3DContent.removeAll(content);
        scene3DContent.removeAll(content);
    }

    private void updateSceneView() {
        scene3DContent.clear();
        scene3DContent.addAll(other3DContent);
        scene3DContent.addAll(zone3DContent);
    }

    public void setCamera2D(Vector3 focusPoint, Clock clock1, Clock clock2) {
        gizmo.showThirdDimension(false);
        cameraTransform.setRotation(Vector3.zero());
        cameraTransform.setPivot(focusPoint);
        cameraTransform.setPositionX(focusPoint.x);
        cameraTransform.setPositionY(focusPoint.y);
        CameraContext.setIs2D(true);

        gizmo.setXaxisName(clock1.getName());
        gizmo.setYaxisName(clock2.getName());
    }

    public void setCamera3D(Vector3 focusPoint, Clock clock1, Clock clock2, Clock clock3) {
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
