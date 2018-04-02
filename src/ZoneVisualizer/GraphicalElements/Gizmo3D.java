package ZoneVisualizer.GraphicalElements;

import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.text.*;

public class Gizmo3D extends Group {

    private Box xAxis;
    private Box yAxis;
    private Box zAxis;

    private Tooltip xTooltip = new Tooltip("X-axis");
    private Tooltip yTooltip = new Tooltip("Y-axis");
    private Tooltip zTooltip = new Tooltip("Z-axis");

    public Gizmo3D(double thickness, double scale) {
        super();
        xAxis = new Box(thickness,thickness,thickness);
        yAxis = new Box(thickness,thickness,thickness);
        zAxis = new Box(thickness,thickness,thickness);

        this.getChildren().addAll(xAxis, yAxis, zAxis );
        xAxis.setMaterial(new PhongMaterial(Color.RED));
        yAxis.setMaterial(new PhongMaterial(Color.GREEN));
        zAxis.setMaterial(new PhongMaterial(Color.BLUE));

        scaleTo(scale);

        Tooltip.install(xAxis, xTooltip);
        Tooltip.install(yAxis, yTooltip);
        Tooltip.install(zAxis, zTooltip);
    }

    public void scaleTo(double scale) {
        xAxis.setWidth(scale);
        yAxis.setHeight(scale);
        zAxis.setDepth(scale);

        xAxis.setTranslateX(scale / 2);
        yAxis.setTranslateY(-scale / 2);
        zAxis.setTranslateZ(scale / 2);
    }

    public void showThirdDimension(boolean show) {
        zAxis.setVisible(show);
    }

    public void setXaxisName(String value) {
        xTooltip.setText(value);
    }

    public void setYaxisName(String value) {
        yTooltip.setText(value);
    }

    public void setZaxisName(String value) {
        zTooltip.setText(value);
    }
}
