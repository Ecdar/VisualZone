package sample.GraphicalElements;

public class CameraContext {

    private Camera3D camera;
    private WorldTransform sceneTransform;
    private WorldTransform fakeCameraTransform;

    public CameraContext(Camera3D camera, WorldTransform sceneTransform) {
        this.camera = camera;
        this.sceneTransform = sceneTransform;

        fakeCameraTransform = new WorldTransform(
                sceneTransform.getPositionReadonly().multiply(-1),
                sceneTransform.getPivotReadonly(),
                sceneTransform.getRotationReadonly().multiply(-1),
                sceneTransform.getScaleReadonly().multiply(-1));

        fakeCameraTransform.addOnPositionChange(this::updateScenePosition);
        fakeCameraTransform.addOnPivotChange(this::updateScenePivot);
        fakeCameraTransform.addOnRotationChange(this::updateSceneRotation);
        fakeCameraTransform.addOnScaleChange(this::updateSceneScale);
    }

    private void updateScenePosition() {
        Vector3 camPosition = fakeCameraTransform.getPositionReadonly();
        sceneTransform.setPosition(camPosition.multiply(-1));
    }

    private void updateScenePivot() {
        Vector3 camPivot = fakeCameraTransform.getPivotReadonly();
        sceneTransform.setPivot(camPivot);
    }

    private void updateSceneRotation() {
        Vector3 camRotation = fakeCameraTransform.getRotationReadonly();
        sceneTransform.setRotation(camRotation.multiply(-1));
    }

    private void updateSceneScale() {
        Vector3 camScale = fakeCameraTransform.getScaleReadonly();
        sceneTransform.setScale(camScale.multiply(-1));
    }

    public WorldTransform getFakeCameraTransform() {
        return fakeCameraTransform;
    }
}
