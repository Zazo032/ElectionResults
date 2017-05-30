/*
 * Entregable 2 - IPC 2017
 */
package entregable2;

import electionresults.model.ElectionResults;
import electionresults.model.PartyResults;
import electionresults.persistence.io.DataAccessLayer;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 * 
 * @author Cristian Zazo
 * @author Jorge Asensi
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private PieChart seatsDisChart;
    @FXML
    private BarChart<String, Number> partyVotesChart;
    @FXML
    private BarChart<String, Number> participationChart;
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
    @FXML
    private Label communityLabel;
    @FXML
    private Label yearLabel;
    @FXML
    private Label comarcaLabel;
    @FXML
    private Slider sliderFilter;

    double cstDefOp, vlcDefOp, alcDefOp;
    int yearToShow;
    String provinceToShow;
    String comarcaToShow;
    double filterToShow;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar interfaz
        initImages();
        initYears();
        initSlider();
        initComarcas();
        
        updateComarcas();
        updateCharts();
        updateParticipationChart();
    }

    // Lanzadera para actualizar las gráficas
    private void updateCharts() {
        updateSeatsDisChart();
        updatePartyVotesChart();
    }

    // Actualiza el ChoiceBox de comarcas
    private void updateComarcas() {
        // IGUAL HAY QUE PONER TASK (puede ser)
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
        Task<List<PartyResults>> task = new Task<List<PartyResults>>() {
            
            @Override
            protected List<PartyResults> call() throws Exception {
                return DataAccessLayer.getElectionResults(yearToShow).getProvinceResults(provinceToShow).getPartyResultsSorted();
            }
            
            @Override
            protected void succeeded(){
                List<PartyResults> lpr = getValue();
                ObservableList<PieChart.Data> obs = FXCollections.observableArrayList();
                for (int i = 0; i < lpr.size(); i++) {
                    PartyResults aux = lpr.get(i);
                    if (aux.getSeats() > 0) {
                        PieChart.Data d = new PieChart.Data(aux.getParty() + " (" + aux.getSeats() + ")", aux.getSeats());
                        obs.add(d);
                    }
                }
                seatsDisChart.setData(obs);
            }
        };
        
        Task<List<PartyResults>> task2 = new Task<List<PartyResults>>() {
            
            @Override
            protected List<PartyResults> call() throws Exception {
                System.out.println("Chart Comunidad");
                System.out.println(yearToShow + provinceToShow);
                return DataAccessLayer.getElectionResults(yearToShow).getGlobalResults().getPartyResultsSorted();
            }
            
            @Override
            protected void succeeded(){
                System.out.println("Succeed Comunidad");
                ObservableList<PieChart.Data> cobs = FXCollections.observableArrayList();
                List<PartyResults> lpr = getValue();
                for (int i = 0; i < lpr.size(); i++) {
                    PartyResults aux = lpr.get(i);
                    if (aux.getSeats() > 0) {
                        PieChart.Data d = new PieChart.Data(aux.getParty() + " (" + aux.getSeats() + ")", aux.getSeats());
                        cobs.add(d);
                    }
                }
                seatsDisChart.setData(cobs);
            }
            
            @Override
            protected void failed(){
                System.out.println("Fail Comunidad");
            }
        };
        
        
        if (provinceToShow != null) {
            seatsDisChart.setTitle("Seats distribution for " + provinceToShow);
            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        } else {
            seatsDisChart.setTitle("Seats distribution for Comunidad Valenciana");
            Thread t2 = new Thread(task2);
            t2.setDaemon(true);
            t2.start();
        }
    }

    // Actualiza el BarChart (Party Votes)
    private void updatePartyVotesChart(){
        Task<List<PartyResults>> task1 = new Task<List<PartyResults>>(){
            @Override
            protected List<PartyResults> call() throws Exception {
                return DataAccessLayer.getElectionResults(yearToShow).getGlobalResults().getPartyResultsSorted();
            }
        
            @Override
            protected void succeeded(){
                System.out.println("Comunidad votes succeed");
                List<PartyResults> lrr = getValue();
                partyVotesChart.setData(FXCollections.observableArrayList());
                for (PartyResults r : lrr) {
                    XYChart.Series s = new XYChart.Series();
                    s.setName(r.getParty());
                    if(r.getPercentage() > filterToShow){
                        XYChart.Data d = new XYChart.Data("" + yearToShow,r.getVotes());
                        s.getData().add(d);
                        partyVotesChart.getData().add(s);
                    }
                }
            }
            
            @Override
            protected void failed(){
                System.out.println("Comunidad votes fail");
            }
        };
        if(provinceToShow == null) { // Comunidad Valenciana
            partyVotesChart.setTitle("Party Votes for Comunidad Valenciana");
            Thread t1 = new Thread(task1);
            //t1.setDaemon(true);
            t1.start();
        } else {
            if(comarcaToShow == null) { // Provincia
                partyVotesChart.setTitle("Party Votes for " + provinceToShow);
                partyVotesChart.setData(FXCollections.observableArrayList());
                List<PartyResults> lrr = DataAccessLayer.getElectionResults(yearToShow).getProvinceResults(provinceToShow).getPartyResultsSorted();
                for (PartyResults r : lrr) {
                    XYChart.Series s = new XYChart.Series();
                    s.setName(r.getParty());
                    if(r.getPercentage() > filterToShow){
                        XYChart.Data d = new XYChart.Data("" + yearToShow,r.getVotes());
                        s.getData().add(d);
                        partyVotesChart.getData().add(s);
                    }
                }
            } else { // Comarca
                partyVotesChart.setTitle("Party Votes for " + comarcaToShow);
                partyVotesChart.setData(FXCollections.observableArrayList());
                List<PartyResults> lrr = DataAccessLayer.getElectionResults(yearToShow).getRegionResults(comarcaToShow).getPartyResultsSorted();
                for (PartyResults r : lrr) {
                    XYChart.Series s = new XYChart.Series();
                    s.setName(r.getParty());
                    if(r.getPercentage() > filterToShow){
                        XYChart.Data d = new XYChart.Data("" + yearToShow,r.getVotes());
                        s.getData().add(d);
                        partyVotesChart.getData().add(s);
                    }
                }
            }
        }
    }
    
    // Actualiza el BarChart (Participation %)
    private void updateParticipationChart(){
        /*
        Task<List<ElectionResults>> task3 = new Task<List<ElectionResults>>(){
            
            @Override
            protected List<ElectionResults> call() throws Exception {
                System.out.println("updateParticipationChart()");
                return DataAccessLayer.getAllElectionResults();
            }
            
            @Override
            protected void succeeded(){
                Collection<XYChart.Series<String,Number>> c = FXCollections.observableArrayList();
                XYChart.Series s1 = new XYChart.Series();
                s1.setName("Comunidad Valenciana");
                c.add(s1);
                XYChart.Series s2 = new XYChart.Series();
                s2.setName("Alicante");
                c.add(s2);
                XYChart.Series s3 = new XYChart.Series();
                s3.setName("Castellón");
                c.add(s3);
                XYChart.Series s4 = new XYChart.Series();
                s4.setName("Valencia");
                c.add(s4);
        
                List<ElectionResults> ler = getValue();
                for (ElectionResults y : ler) {
                    for (XYChart.Series<String, Number> s : c) {
                        if(s.getName().equals("Alicante") || s.getName().equals("Valencia") || s.getName().equals("Castellón")){
                            double v = ((double) y.getProvinceResults(s.getName()).getPollData().getVotes() / y.getProvinceResults(s.getName()).getPollData().getCensus()) * 100;
                            XYChart.Data d = new XYChart.Data("" + y.getYear(),v);
                            s.getData().add(d);
                        } else {
                            double v = ((double) y.getGlobalResults().getPollData().getVotes() / y.getGlobalResults().getPollData().getCensus()) * 100;
                            XYChart.Data d = new XYChart.Data("" + y.getYear(),v);
                            s1.getData().add(d);
                        }
                    }
                }
                participationChart.setData(FXCollections.observableArrayList(c));
            }
            
            @Override
            protected void failed(){
            System.out.println("updateParticipationChart() ERROR");
            }
        };
        
        Thread t3 = new Thread(task3);
        t3.setDaemon(true);
        t3.start();
        */
    }
    
    // Listener del ChoiceBox de comarcas
    private void initComarcas() {
        comarcaSelector.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observableValue, Number number, Number number2) -> {
            try {
                String cChanged = comarcaSelector.getItems().get((Integer) number2);
                comarcaLabel.setText(cChanged);
                comarcaToShow = cChanged;
                updatePartyVotesChart();
            } catch (Exception e) {
                comarcaToShow = null;
                comarcaLabel.setText("");
                updatePartyVotesChart();
            }
        });
    }

    // Iniciar filtro (slider)
    private void initSlider() {
        filterToShow = 2.5;
        sliderFilter.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                double aux = sliderFilter.valueProperty().getValue();
                if (aux % 0.5 == 0) {
                    filterToShow = (Double) new_val;
                    updatePartyVotesChart();
                }
                System.out.println(filterToShow);
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
        yearLabel.setText("" + yearToShow);
        communityLabel.setText("Comunidad Valenciana");
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