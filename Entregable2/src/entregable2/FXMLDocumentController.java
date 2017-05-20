/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entregable2;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author cristianzazo
 */
public class FXMLDocumentController implements Initializable {
    
    private Label label;
    @FXML
    private PieChart seatsDisChart;
    @FXML
    private BarChart<?, ?> partyVotesChart;
    @FXML
    private BarChart<?, ?> participationChart;
    @FXML
    private StackedBarChart<?, ?> seatsChart;
    @FXML
    private LineChart<?, ?> votesChart;
    @FXML
    private ChoiceBox<?> comarcaSelector;
    @FXML
    private ImageView castellonImage;
    @FXML
    private ImageView valenciaImage;
    @FXML
    private ImageView alicanteImage;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    @FXML
    private void unfocusCst(MouseEvent event) {
        castellonImage.setOpacity(0.75);
    }

    @FXML
    private void focusCst(MouseEvent event) {
        castellonImage.setOpacity(1);
    }

    @FXML
    private void unfocusVlc(MouseEvent event) {
        valenciaImage.setOpacity(0.75);
    }

    @FXML
    private void focusVlc(MouseEvent event) {
        valenciaImage.setOpacity(1);
    }

    @FXML
    private void unfocusAlc(MouseEvent event) {
        alicanteImage.setOpacity(0.75);
    }

    @FXML
    private void focusAlc(MouseEvent event) {
        alicanteImage.setOpacity(1);
    }
    
}
