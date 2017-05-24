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
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
    private Label communityLabel;
    @FXML
    private Label yearLabel;
    @FXML
    private Label comarcaLabel;
    @FXML
    private Slider sliderFilter;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initImages();
        initYears();
        initSlider();
        initComarcas();

        updateComarcas();
        updateCharts();
    }

    // Lanzadera para actualizar las gráficas
    private void updateCharts() {
        updateSeatsDisChart();
    }

    // Actualiza el ChoiceBox de comarcas
    private void updateComarcas() {
        if (provinceToShow != null) {
            comarcaSelector.setDisable(false);
            Map<String, String> m = DataAccessLayer.getElectionResults(yearToShow).getRegionProvinces();
            ObservableList<String> clist = FXCollections.observableArrayList();
            comarcaSelector.setItems(clist);
            for (Map.Entry<String, String> entry : m.entrySet()) {
                if (entry.getValue().equals(provinceToShow)) {
                    clist.add(entry.getKey());
                }
            }
            comarcaSelector.setItems(clist);
        } else {
            comarcaSelector.setDisable(true);
            comarcaToShow = null;
            comarcaLabel.setText("");
            ObservableList<String> clist = FXCollections.observableArrayList();
            comarcaSelector.setItems(clist);
        }
    }

    // Actualiza el PieChart (Seats Distribution)
    private void updateSeatsDisChart() {
        if (provinceToShow != null) {
            int talla = DataAccessLayer.getElectionResults(yearToShow).getProvinceResults(provinceToShow).getPartyResultsSorted().size();
            ObservableList<PieChart.Data> obs = FXCollections.observableArrayList();
            for (int i = 0; i < talla; i++) {
                PartyResults aux = DataAccessLayer.getElectionResults(yearToShow).getProvinceResults(provinceToShow).getPartyResultsSorted().get(i);
                if (aux.getPercentage() > filterToShow) {
                    PieChart.Data d = new PieChart.Data(aux.getParty() + " (" + aux.getSeats() + ")", aux.getSeats());
                    obs.add(d);
                }
            }
            seatsDisChart.setData(obs);
        } else {
            ObservableList<PieChart.Data> cobs = FXCollections.observableArrayList();
            for (int i = 0; i < DataAccessLayer.getElectionResults(yearToShow).getGlobalResults().getPartyResultsSorted().size(); i++) {
                PartyResults aux = DataAccessLayer.getElectionResults(yearToShow).getGlobalResults().getPartyResultsSorted().get(i);
                if (aux.getPercentage() * 100 > filterToShow) {
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

    // Listener del ChoiceBox de comarcas
    private void initComarcas() {
        comarcaSelector.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observableValue, Number number, Number number2) -> {
            try {
                String cChanged = comarcaSelector.getItems().get((Integer) number2);
                System.out.println(comarcaToShow);
                comarcaLabel.setText(cChanged);
                comarcaToShow = cChanged;
                System.out.println(comarcaToShow);
                updateCharts();
            } catch (Exception e) {
            }
        });
    }

    // Iniciar filtro (slider)
    private void initSlider() {
        sliderFilter.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                double aux = sliderFilter.valueProperty().getValue();
                if (aux % 0.5 == 0) {
                    filterToShow = (Double) new_val;
                    updateSeatsDisChart();
                }
            }
        });
    }

    // Crear botones de años
    private void initYears() {
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
        yearToShow = 2015;
    }

    // Iniciar comportamiento del mapa
    private void initImages() {
        // Default opacity 80%
        cstDefOp = 0.8;
        vlcDefOp = 0.8;
        alcDefOp = 0.8;
        // Listeners for Castellon
        castellonImage.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            if (provinceToShow == "Castellón") {
                cstDefOp = 0.8;
                castellonImage.setOpacity(cstDefOp);
                vlcDefOp = 0.8;
                valenciaImage.setOpacity(vlcDefOp);
                alcDefOp = 0.8;
                alicanteImage.setOpacity(alcDefOp);
                comarcaToShow = null;
                provinceToShow = null;
                communityLabel.setText("Comunidad Valenciana");

                seatsDisChart.setTitle("Seats distribution for " + "Comunidad Valenciana");
                partyVotesChart.setTitle("Party votes in " + "Comunidad Valenciana");
                updateComarcas();
                updateCharts();
            } else {
                cstDefOp = 0.9;
                castellonImage.setOpacity(cstDefOp);
                vlcDefOp = 0.5;
                valenciaImage.setOpacity(vlcDefOp);
                alcDefOp = 0.5;
                alicanteImage.setOpacity(alcDefOp);
                comarcaToShow = null;
                provinceToShow = "Castellón";
                communityLabel.setText(provinceToShow);

                seatsDisChart.setTitle("Seats distribution for " + provinceToShow);
                partyVotesChart.setTitle("Party votes in " + provinceToShow);
                updateComarcas();
                updateCharts();
            }
            castellonImage.setOpacity(0.9);
            event.consume();
        });
        castellonImage.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event) -> {
            castellonImage.setOpacity(1);
            event.consume();
        });
        castellonImage.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent event) -> {
            castellonImage.setOpacity(cstDefOp);
            event.consume();
        });
        // Listeners for Valencia
        valenciaImage.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            if (provinceToShow == "Valencia") {
                cstDefOp = 0.8;
                castellonImage.setOpacity(cstDefOp);
                vlcDefOp = 0.8;
                valenciaImage.setOpacity(vlcDefOp);
                alcDefOp = 0.8;
                alicanteImage.setOpacity(alcDefOp);
                comarcaToShow = null;
                provinceToShow = null;
                communityLabel.setText("Comunidad Valenciana");

                seatsDisChart.setTitle("Seats distribution for " + "Comunidad Valenciana");
                partyVotesChart.setTitle("Party votes in " + "Comunidad Valenciana");
                updateComarcas();
                updateCharts();
            } else {
                cstDefOp = 0.5;
                castellonImage.setOpacity(cstDefOp);
                vlcDefOp = 0.9;
                valenciaImage.setOpacity(vlcDefOp);
                alcDefOp = 0.5;
                alicanteImage.setOpacity(alcDefOp);
                comarcaToShow = null;
                provinceToShow = "Valencia";
                communityLabel.setText(provinceToShow);

                seatsDisChart.setTitle("Seats distribution for " + provinceToShow);
                partyVotesChart.setTitle("Party votes in " + provinceToShow);
                updateComarcas();
                updateCharts();
            }
            valenciaImage.setOpacity(0.9);
            event.consume();
        });
        valenciaImage.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event) -> {
            valenciaImage.setOpacity(1);
            event.consume();
        });
        valenciaImage.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent event) -> {
            valenciaImage.setOpacity(vlcDefOp);
            event.consume();
        });
        // Listeners for Alicante
        alicanteImage.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            if (provinceToShow == "Alicante") {
                cstDefOp = 0.8;
                castellonImage.setOpacity(cstDefOp);
                vlcDefOp = 0.8;
                valenciaImage.setOpacity(vlcDefOp);
                alcDefOp = 0.8;
                alicanteImage.setOpacity(alcDefOp);
                comarcaToShow = null;
                provinceToShow = null;
                communityLabel.setText("Comunidad Valenciana");

                seatsDisChart.setTitle("Seats distribution for " + "Comunidad Valenciana");
                partyVotesChart.setTitle("Party votes in " + "Comunidad Valenciana");
                updateComarcas();
                updateCharts();
            } else {
                cstDefOp = 0.5;
                castellonImage.setOpacity(cstDefOp);
                vlcDefOp = 0.5;
                valenciaImage.setOpacity(vlcDefOp);
                alcDefOp = 0.9;
                alicanteImage.setOpacity(alcDefOp);
                comarcaToShow = null;
                provinceToShow = "Alicante";
                communityLabel.setText(provinceToShow);

                seatsDisChart.setTitle("Seats distribution for " + provinceToShow);
                partyVotesChart.setTitle("Party votes in " + provinceToShow);
                updateComarcas();
                updateCharts();
            }
            alicanteImage.setOpacity(0.9);
            event.consume();
        });
        alicanteImage.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event) -> {
            alicanteImage.setOpacity(1);
            event.consume();
        });
        alicanteImage.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent event) -> {
            alicanteImage.setOpacity(alcDefOp);
            event.consume();
        });
    }
}