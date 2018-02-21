package visualization.of.zones.in.tlts;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;

/**
 * FXML Controller class
 *
 * @author Brobak
 */
public class ZoneVisualizationController implements Initializable {

    @FXML
    protected GridPane rootGrid;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    @FXML
    protected void testModelInit() {
        initializeModel();
    }
    
    public void initializeModel() {
        rootGrid.getChildren().clear();
        for (int i = 0; i < 5; i++) {
            CheckBox checkBox = new CheckBox("Dimension: " + i);
            GridPane.setColumnIndex(checkBox, 0);
            GridPane.setRowIndex(checkBox, i);
            
            rootGrid.getChildren().add(checkBox);
        }
    }
}
