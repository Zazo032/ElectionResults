/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entregable2;

import electionresults.persistence.io.DataAccessLayer;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

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
    @FXML
    private HBox yearContainer;
    
    double cstDefOp, vlcDefOp, alcDefOp;
    int yearToShow = 0;
    String comarcaToShow, provinceToShow;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cstDefOp = 0.75;
        vlcDefOp = 0.75;
        alcDefOp = 0.75;
        List<Integer> years = DataAccessLayer.getElectionYears();
        ObservableList<Integer> yearsList = FXCollections.observableList(years);
        for (Integer year : yearsList) {
            Button b = new Button("" + year);
            b.setStyle("-fx-background-color:#ffffff;-fx-border-color:#3f51b5;-fx-border-width:2px;-fx-border-radius:2px;");
            b.setOnAction((ActionEvent event) -> {
                yearToShow = Integer.parseInt(b.getText());
                updateCharts(yearToShow,provinceToShow,comarcaToShow);
            });
            yearContainer.getChildren().add(b);
        }
    }

    private void updateCharts(int y, String p, String c){
        if(yearToShow != 0 && provinceToShow != null) {
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            seatsDisChart.setData(null);
            ObservableList<XYChart.Data<String,Number>> barData = FXCollections.observableArrayList();
            partyVotesChart.setData(null);
            // Actualiza gráficas
            System.out.println("Año: " + yearToShow + "\nProvincia: " + provinceToShow);
        } else {
            System.out.println("Faltan cosas");
        }
    }
    
    @FXML
    private void unfocusCst(MouseEvent event) {
        castellonImage.setOpacity(cstDefOp);
    }

    @FXML
    private void focusCst(MouseEvent event) {
        castellonImage.setOpacity(1);
    }

    @FXML
    private void unfocusVlc(MouseEvent event) {
        valenciaImage.setOpacity(vlcDefOp);
    }

    @FXML
    private void focusVlc(MouseEvent event) {
        valenciaImage.setOpacity(1);
    }

    @FXML
    private void unfocusAlc(MouseEvent event) {
        alicanteImage.setOpacity(alcDefOp);
    }

    @FXML
    private void focusAlc(MouseEvent event) {
        alicanteImage.setOpacity(1);
    }

    @FXML
    private void selectCst(MouseEvent event) {
        cstDefOp = 1;
        castellonImage.setOpacity(cstDefOp);
        vlcDefOp = 0.75;
        valenciaImage.setOpacity(vlcDefOp);
        alcDefOp = 0.75;
        alicanteImage.setOpacity(alcDefOp);
        provinceToShow = "Castellon";
        seatsDisChart.setTitle("Seats distribution for Castellon");
        partyVotesChart.setTitle("Party votes in Castellon");
        updateCharts(yearToShow, provinceToShow, comarcaToShow);
    }

    @FXML
    private void selectVlc(MouseEvent event) {
        cstDefOp = 0.75;
        castellonImage.setOpacity(cstDefOp);
        vlcDefOp = 1;
        valenciaImage.setOpacity(vlcDefOp);
        alcDefOp = 0.75;
        alicanteImage.setOpacity(alcDefOp);
        provinceToShow = "Valencia";
        seatsDisChart.setTitle("Seats distribution for Valencia");
        partyVotesChart.setTitle("Party votes in Valencia");
        updateCharts(yearToShow, provinceToShow, comarcaToShow);
    }

    @FXML
    private void selectAlc(MouseEvent event) {
        cstDefOp = 0.75;
        castellonImage.setOpacity(cstDefOp);
        vlcDefOp = 0.75;
        valenciaImage.setOpacity(vlcDefOp);
        alcDefOp = 1;
        alicanteImage.setOpacity(alcDefOp);
        provinceToShow = "Alicante";
        seatsDisChart.setTitle("Seats distribution for Alicante");
        partyVotesChart.setTitle("Party votes in Alicante");
        updateCharts(yearToShow, provinceToShow, comarcaToShow);
    }
    
}
