/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entregable2;

import electionresults.model.PartyResults;
import electionresults.model.ProvinceInfo;
import electionresults.persistence.io.DataAccessLayer;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author cristianzazo
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private PieChart seatsDisChart;
    @FXML
    private BarChart<String, Number> partyVotesChart;
    @FXML
    private BarChart<?, ?> participationChart;
    @FXML
    private StackedBarChart<?, ?> seatsChart;
    @FXML
    private LineChart<?, ?> votesChart;
    @FXML
    private ChoiceBox<String> comarcaSelector;
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
    String provinceToShow;
    String comarcaToShow;
    
    @FXML
    private VBox anyoSelector;
    @FXML
    private Label communityLabel;
    @FXML
    private Label yearLabel;
    @FXML
    private Label comarcaLabel;
    
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
                yearLabel.setText(yearToShow + "");
                updateCharts(yearToShow,provinceToShow,comarcaToShow);
            });
            yearContainer.getChildren().add(b);
        }
        
    }

    private void updateCharts(int y, String p, String c){
        if(yearToShow != 0 && provinceToShow != null) {
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            //seatsDisChart.setData(null);
            ObservableList<XYChart.Data<String,Number>> barData = FXCollections.observableArrayList();
            partyVotesChart.setData(null);
            // Actualiza gráficas
            System.out.println("Año: " + yearToShow + " y Provincia: " + provinceToShow);
            updateSeatsDisChart(yearToShow, provinceToShow);
        } else {
            System.out.println("Faltan cosas");
        }
    }
    
    private void updateSeatsDisChart(int y, String p){
        int talla = DataAccessLayer.getElectionResults(y).getProvinceResults(p).getPartyResultsSorted().size();
        ObservableList<PieChart.Data> obs = FXCollections.observableArrayList();
        for (int i = 0; i < talla; i++) {
           PartyResults aux = DataAccessLayer.getElectionResults(y).getProvinceResults(p).getPartyResultsSorted().get(i);
           PieChart.Data d = new PieChart.Data(aux.getParty() + " (" + aux.getSeats() + ")", aux.getSeats());
           obs.add(d);
        }
        seatsDisChart.setData(obs);
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
        provinceToShow = "Castellón";
        communityLabel.setText(provinceToShow);
        seatsDisChart.setTitle("Seats distribution for " + provinceToShow);
        partyVotesChart.setTitle("Party votes in " + provinceToShow);
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
        communityLabel.setText(provinceToShow);
        seatsDisChart.setTitle("Seats distribution for " + provinceToShow);
        partyVotesChart.setTitle("Party votes in " + provinceToShow);
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
        communityLabel.setText(provinceToShow);
        seatsDisChart.setTitle("Seats distribution for " + provinceToShow);
        partyVotesChart.setTitle("Party votes in " + provinceToShow);
        updateCharts(yearToShow, provinceToShow, comarcaToShow);
    }
    
}
