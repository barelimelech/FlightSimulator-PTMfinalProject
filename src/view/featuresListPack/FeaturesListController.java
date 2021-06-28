package view.featuresListPack;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import server.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FeaturesListController {

    @FXML
    public ListView<String> listViewProp;
    @FXML
    Button open;;
    @FXML
    LineChart<String, Number> leftLineChart;
    @FXML
    LineChart<String, Number> rightLineChart;
    @FXML
    LineChart bottomLineChart;


    Alert alert;
    File chosen, chosenTest, chosenXML;
    String oldValue="";
    public DoubleProperty onOpen;
    public StringProperty fileName, testFileName, xmlFileName;
    public BooleanProperty bool = new SimpleBooleanProperty();

    NumberAxis y;
    CategoryAxis x;
    NumberAxis xAxis;
    NumberAxis yAxis;

    public List<String> tmp;
    public SimpleStringProperty feature;
    public ArrayList<CorrelatedFeatures> corrFeat;

    XYChart.Series leftSeries;
    XYChart.Series rightSeries;

    public DoubleProperty minVal, maxVal;
    ObservableList<XYChart.Data<String, Float>> data;
     XYChart.Series bottomSeries;

    boolean tmpBool=false;



    public FeaturesListController() {
        fileName = new SimpleStringProperty();
        testFileName = new SimpleStringProperty();
        xmlFileName = new SimpleStringProperty();
        bool.set(false);

        tmp = new ArrayList<>();
        feature = new SimpleStringProperty();
        x = new CategoryAxis();
        y = new NumberAxis();
        minVal = new SimpleDoubleProperty();
        maxVal = new SimpleDoubleProperty();
        leftLineChart = new LineChart<>(x, y);
        rightLineChart = new LineChart<>(x, y);


        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        bottomLineChart = new LineChart<>(xAxis, yAxis);

    }


    public void openLearnFile() {
        System.out.println("openFile function in MODEL start");
        FileChooser fc = new FileChooser();
        fc.setTitle("open csv file");
        FileChooser.ExtensionFilter ef = new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv");
        fc.getExtensionFilters().add(ef);
        fc.setInitialDirectory(new File("./Learn CSV"));
        chosen = fc.showOpenDialog(null);
        if (chosen != null) {
            fileName.setValue(chosen.getName());

            if (!chosen.getName().equals("reg_flight.csv")) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("      Error        ");
                alert.setContentText("The selected file doesn't match the normal flight file.\n Please select again");
                alert.showAndWait();
            } else {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("      Excellent        ");
                alert.setContentText("File '" + chosen.getName() + "' successfully selected");
                alert.showAndWait();
            }
            BufferedReader reader = null;
            String line = null;
            String str[];
            try {
                reader = new BufferedReader(new FileReader(chosen.getName()));
                if ((line = reader.readLine()) != null) {
                    str = line.split(",");
                    tmp.addAll(Arrays.asList(str));
                    listViewProp.getItems().addAll(tmp);
                }
            } catch (IOException e) {
                e.printStackTrace();
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("      Error        ");
                alert.setContentText("Error in read from learning file");
                alert.showAndWait();
            }

        }
    }

    public void openTestFile() {
        System.out.println("openFile function in MODEL start");
        FileChooser fc = new FileChooser();
        fc.setTitle("open csv file");
        FileChooser.ExtensionFilter ef = new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv");
        fc.getExtensionFilters().add(ef);
        fc.setInitialDirectory(new File("./Test CSV"));
        chosenTest = fc.showOpenDialog(null);
        if (chosenTest != null) {
            testFileName.setValue(chosenTest.getName());

            if (chosenTest == null) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("      Error        ");
                alert.setContentText("You need to select a learning file first");
                alert.showAndWait();
            } else {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("      Excellent        ");
                alert.setContentText("File '" + chosenTest.getName() + "' successfully selected");
                alert.showAndWait();
            }
            BufferedReader reader = null;
            String line = null;
            String str[];
            try {
                reader = new BufferedReader(new FileReader(chosenTest.getName()));
                if ((line = reader.readLine()) != null) {
                    str = line.split(",");
                    tmp.addAll(Arrays.asList(str));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void openXmlFile() {
        FileChooser fc = new FileChooser();
        fc.setTitle("open csv file");
        FileChooser.ExtensionFilter ef = new FileChooser.ExtensionFilter("XML Files (*.xml)", "*.xml");
        fc.getExtensionFilters().add(ef);
        fc.setInitialDirectory(new File("./XML Files"));
        chosenXML = fc.showOpenDialog(null);
        if (chosenXML != null) {
            xmlFileName.setValue(chosenXML.getName());
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("      Excellent        ");
            alert.setContentText("File '" + chosenXML.getName() + "' successfully selected");
            alert.showAndWait();
        }
    }

    @FXML
    public void handleMouseClick(MouseEvent arg0) {
        if (chosenXML == null) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("      Error        ");
            alert.setContentText("You need to select a properties file before you select feature from the list");
            alert.showAndWait();
        } else
            feature.set(listViewProp.getSelectionModel().getSelectedItem());
    }

    public void drawLeftGraph(String time, double value) {
        Platform.runLater(() -> {
            leftSeries.getData().add(new XYChart.Data<>(time, value));
            leftLineChart.setCreateSymbols(false);
            leftSeries.getNode().setStyle("-fx-stroke: black;");
        });
    }

    public void setGraph() {
        if (leftLineChart != null) {
            leftSeries = new XYChart.Series();
            leftLineChart.getData().add(leftSeries);
        }
        if (rightLineChart != null) {
            rightSeries = new XYChart.Series();
            rightLineChart.getData().add(rightSeries);
        }
    }


    public void setZScoreGraph(){
        if(bottomLineChart!=null){
            bottomSeries=new XYChart.Series();
            bottomLineChart.getData().add(bottomSeries);
        }
    }

    public void clearGraph() {
        if (leftSeries != null)
            leftSeries.getData().clear();
        if (rightSeries != null)
            rightSeries.getData().clear();

    }

    public void drawRightGraph(String time, double value) {
        Platform.runLater(() -> {
            rightSeries.getData().add(new XYChart.Data<>(time, value));
            rightLineChart.setCreateSymbols(false);
            rightSeries.getNode().setStyle("-fx-stroke: black;");
        });

    }


    public void clearRegAlgo() {
        bottomLineChart.getData().clear();
    }

    public void paintZSAlgo(ArrayList<XYChart.Series> series, int timeStep,String name) {
        System.out.println("-----------------------------------------------paint ZSCORE-----------------------------------------------------");
        Platform.runLater(() -> {
            if(!name.equals(oldValue)){
                data = FXCollections.observableArrayList();
                data.addAll(series.get(0).getData());
                tmpBool=false;
            }

            oldValue=name;
            if(!tmpBool) {
                bottomSeries=new XYChart.Series();
                bottomLineChart.getData().add(bottomSeries);
                bottomLineChart.setLegendVisible(false);
                tmpBool=true;
            }
            Float y;
            String x;
            y = data.get(timeStep).getYValue();
            Platform.runLater(()-> {
                bottomSeries.getData().add(new XYChart.Data<>(timeStep, y));
            });
            bottomLineChart.setCreateSymbols(false);
            bottomLineChart.setLegendVisible(false);
            bottomSeries.getNode().setStyle("-fx-stroke: black;");
        });
    }

    public void changeGraphColor1() {
        bottomLineChart.setStyle("-fx-background-color: red");
    }

    public void changeGraphColor2() {
        bottomLineChart.setStyle("-fx-background-color: white");
    }

    public void paintRegAlgo(ArrayList<XYChart.Series> series) {
        Platform.runLater(() -> {
            System.out.println("-----------------------------------------------paint REGRESSION-----------------------------------------------------");
            XYChart.Series series1 = series.get(0);
            bottomLineChart.getData().add(series1);

            bottomLineChart.setAnimated(false);
            bottomLineChart.setCreateSymbols(true);
            series1.getNode().setStyle("-fx-stroke: black;");
            bottomLineChart.setLegendVisible(false);
        });
    }

}


