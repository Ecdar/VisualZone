package sample.GraphicalElements;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class Gizmo3D extends Group {

    Box xAxis;
    Box yAxis;
    Box zAxis;

    public Gizmo3D(double thickness, double scale) {
        super();
        xAxis = new Box(thickness,thickness,thickness);
        yAxis = new Box(thickness,thickness,thickness);
        zAxis = new Box(thickness,thickness,thickness);

        this.getChildren().addAll(xAxis, yAxis, zAxis);
        xAxis.setMaterial(new PhongMaterial(Color.RED));
        yAxis.setMaterial(new PhongMaterial(Color.GREEN));
        zAxis.setMaterial(new PhongMaterial(Color.BLUE));

        scaleTo(scale);
    }

    public void scaleTo(double scale) {
        xAxis.setWidth(scale);
        yAxis.setHeight(scale);
        zAxis.setDepth(scale);

        xAxis.setTranslateX(scale / 2);
        yAxis.setTranslateY(-scale / 2);
        zAxis.setTranslateZ(scale / 2);
    }
}
