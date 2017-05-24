/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entregable2;

import electionresults.model.PartyResults;
import electionresults.persistence.io.DataAccessLayer;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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
    double filterToShow;
    
    @FXML
    private VBox anyoSelector;
    @FXML
    private Label communityLabel;
    @FXML
    private Label yearLabel;
    @FXML
    private Label comarcaLabel;
    @FXML
    private Slider sliderFilter;
    
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
                updateCharts();
            });
            yearContainer.getChildren().add(b);
        }
        sliderFilter.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
                    double aux = sliderFilter.valueProperty().getValue();
                    if(aux % 0.5 == 0){
                        filterToShow = (Double) new_val;
                        System.out.println(filterToShow);
                        updateSeatsDisChart();
                    }
            }
        });
        yearToShow = 2015;
        updateCharts();
    }

    private void updateCharts(){
        updateSeatsDisChart();
    }
    
    private void updateSeatsDisChart(){
        if(provinceToShow != null){
            int talla = DataAccessLayer.getElectionResults(yearToShow).getProvinceResults(provinceToShow).getPartyResultsSorted().size();
            ObservableList<PieChart.Data> obs = FXCollections.observableArrayList();
            for (int i = 0; i < talla; i++) {
               PartyResults aux = DataAccessLayer.getElectionResults(yearToShow).getProvinceResults(provinceToShow).getPartyResultsSorted().get(i);
               if(aux.getPercentage() > filterToShow ){
                    PieChart.Data d = new PieChart.Data(aux.getParty() + " (" + aux.getSeats() + ")", aux.getSeats());
                    System.out.println(aux.getParty() + " " + aux.getPercentage());
                    obs.add(d);
               }
            }
            seatsDisChart.setData(obs);
        } else {
            ObservableList<PieChart.Data> cobs = FXCollections.observableArrayList();
        for (int i = 0; i < DataAccessLayer.getElectionResults(yearToShow).getGlobalResults().getPartyResultsSorted().size(); i++) {
            PartyResults aux = DataAccessLayer.getElectionResults(yearToShow).getGlobalResults().getPartyResultsSorted().get(i);
            if(aux.getPercentage()*100 > filterToShow){
                PieChart.Data d = new PieChart.Data(
                    aux.getParty() + " (" + aux.getSeats() + ")",
                    aux.getSeats()
                );
                cobs.add(d);
            }
        }
        seatsDisChart.setData(cobs);
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
        provinceToShow = "Castellón";
        communityLabel.setText(provinceToShow);
        seatsDisChart.setTitle("Seats distribution for " + provinceToShow);
        partyVotesChart.setTitle("Party votes in " + provinceToShow);
        updateCharts();
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
        updateCharts();
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
        updateCharts();
    }
    
}
