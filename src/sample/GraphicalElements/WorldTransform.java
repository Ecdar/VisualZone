package sample.GraphicalElements;

import sample.Utility.Action;
import sample.Utility.IAction;

public class WorldTransform {
    private Vector3 position;
    private Vector3 rotation;
    private Vector3 scale;

    private Action onPositionChange = new Action();
    private Action onRotationChange = new Action();
    private Action onScaleChange = new Action();

    public WorldTransform() {
        position = Vector3.zero;
        rotation = Vector3.zero;
        scale = Vector3.zero;
    }

    public WorldTransform(Vector3 position, Vector3 rotation, Vector3 scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public WorldTransform(double positionX, double positionY, double positionZ,
                          double rotationX, double rotationY, double rotationZ,
                          double scaleX, double scaleY, double scaleZ) {
        this.position = new Vector3(positionX, positionY, positionZ);
        this.rotation = new Vector3(rotationX, rotationY, rotationZ);
        this.scale = new Vector3(scaleX, scaleY, scaleZ);
    }

    public Vector3 getPositionReadonly() {
        return (Vector3) position.clone();
    }

    public Vector3 getRotationReadonly() {
        return (Vector3) rotation.clone();
    }

    public Vector3 getScaleReadonly() {
        return (Vector3) scale.clone();
    }

    public void setPosition(Vector3 position) {
        this.position = position;
        onPositionChange.invoke();
    }

    public void changePosition(Vector3 deltaPosition) {
        this.position.minus(deltaPosition);
        onPositionChange.invoke();
    }

    public void setPosition(double x, double y, double z) {
        position.set(x, y, z);
        onPositionChange.invoke();
    }

    public void changePosition(double x, double y, double z) {
        changePosition(new Vector3(x, y, z));
    }

    public void setPositionX(double x) {
        position.x = x;
        onPositionChange.invoke();
    }

    public void changePositionX(double deltaX) {
        position.x -= deltaX;
        onPositionChange.invoke();
    }

    public void setPositionY(double y) {
        position.y = y;
        onPositionChange.invoke();
    }

    public void changePositionY(double deltaY) {
        position.y -= deltaY;
        onPositionChange.invoke();
    }

    public void setPositionZ(double z) {
        position.z = z;
        onPositionChange.invoke();
    }

    public void changePositionZ(double deltaZ) {
        position.z -= deltaZ;
        onPositionChange.invoke();
    }

    public void setRotation(Vector3 rotation) {
        this.rotation = rotation;
        onRotationChange.invoke();
    }

    public void setRotation(double x, double y, double z) {
        rotation.set(x, y, z);
        onRotationChange.invoke();
    }

    public void setRotationX(double x) {
        rotation.x = x;
        onRotationChange.invoke();
    }

    public void setRotationY(double y) {
        rotation.y = y;
        onRotationChange.invoke();
    }

    public void setRotationZ(double z) {
        rotation.z = z;
        onRotationChange.invoke();
    }

    public void setScale(Vector3 scale) {
        this.scale = scale;
        onScaleChange.invoke();
    }

    public void setScale(double x, double y, double z) {
        scale.set(x, y, z);
        onScaleChange.invoke();
    }

    public void setScaleX(double x) {
        scale.x = x;
        onScaleChange.invoke();
    }

    public void setScaleY(double y) {
        scale.y = y;
        onScaleChange.invoke();
    }

    public void setScaleZ(double z) {
        scale.z = z;
        onScaleChange.invoke();
    }

    public void addOnPositionChange(IAction action) {
        onPositionChange.add(action);
    }

    public void addOnRotationChange(IAction action) {
        onRotationChange.add(action);
    }

    public void addOnScaleChange(IAction action) {
        onScaleChange.add(action);
    }
}
