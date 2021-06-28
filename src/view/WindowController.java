package view;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import server.AnomalyReport;
import server.CorrelatedFeatures;
import server.RegressionAlg;
import server.TimeSeriesAnomalyDetector;
import view.buttonsPack.Buttons;
import view.clocksPack.Clocks;
import view.featuresListPack.FeaturesList;
import view.featuresListPack.FeaturesListController;
import view.joystickPack.Joystick;
import view.popup.PopUp;
import view.popup.PopUpController;
import view_model.Timer;
import view_model.ViewModel;

public class WindowController extends Pane implements Observer {

    @FXML
    Pane board;
    @FXML
    FeaturesList listView;
    @FXML
    Joystick joystick;
    @FXML
    Buttons buttons;
    @FXML
    Clocks clocks;
    @FXML
    PopUp popup;


    IntegerProperty milliSec, seconds, minutes;
    public static ArrayList<XYChart.Series> seriesArrayList;
    public static ArrayList<Float> floatSeries;

    public ArrayList<CorrelatedFeatures> corrFeat;


    public StringProperty feature;
    StringProperty algo_name;
    String cur_algo;
    public static StringProperty featurename;
    public BooleanProperty bool = new SimpleBooleanProperty();

    view_model.Timer c;
    public ViewModel vm;
    public Thread tr1;
    Thread tmpThread;
    boolean tmpBool = false;
    boolean tmpAnomalyBool = false;

    ListProperty<AnomalyReport> anomalyReports;


    HashMap<String, ArrayList<Integer>> mapAnomaly;
    ArrayList<String> arrDescriptionAnomaly;
    ArrayList<Integer> arrTimeStepAnomaly;
    boolean listAnomalyBool = false;
    int index = 0;


    public WindowController() {
        vm = new ViewModel();
    }

    public void init() {

        anomalyReports = new SimpleListProperty<>();
        mapAnomaly = new HashMap<>();
        arrDescriptionAnomaly = new ArrayList<>();
        arrTimeStepAnomaly = new ArrayList<>();
        vm = new ViewModel();
        c = new Timer();
        bool.set(false);
        minutes = new SimpleIntegerProperty();
        seconds = new SimpleIntegerProperty();
        milliSec = new SimpleIntegerProperty();
        this.feature = new SimpleStringProperty();
        this.feature.bind(listView.featuresListController.feature);
        seriesArrayList = new ArrayList<>();

        algo_name = new SimpleStringProperty();
        cur_algo = algo_name.getValue();

        listView.featuresListController.maxVal.bind(vm.maxProp);
        listView.featuresListController.minVal.bind(vm.minProp);

        buttons.buttonsController.onPlay = vm.play;
        buttons.buttonsController.onPause = vm.pause;
        buttons.buttonsController.onStop = vm.stop;
        buttons.buttonsController.onLeft = vm.left;
        buttons.buttonsController.onLeftX2 = vm.leftX2;
        buttons.buttonsController.onRight = vm.right;
        buttons.buttonsController.onRightX2 = vm.rightX2;

        corrFeat = listView.featuresListController.corrFeat;

        listView.featuresListController.bool.bind(this.bool);
        vm.timeStep.bindBidirectional(buttons.buttonsController.bottomSlider.valueProperty());

        buttons.buttonsController.speedText.textProperty().bind(vm.vmSpeed.asString());
        popup.popUpController.algoName.addListener((o, ov, nv) -> {
            this.algo_name.setValue(nv);
            this.seriesArrayList = null;
        });

        buttons.buttonsController.bottomSlider.valueProperty().addListener((o, ov, nv) -> {
            c.updateTimer(nv.intValue() - ov.intValue());
            milliSec.setValue(c.miliSec.get());
            seconds.setValue(c.seconds.get());
            minutes.setValue(c.minutes.get());
        });
/*
        buttons.buttonsController.time.textProperty().bind(buttons.buttonsController.bottomSlider.valueProperty().asString());
*/
        buttons.buttonsController.milliSec.textProperty().bind(this.milliSec.asString());
        buttons.buttonsController.minutes.textProperty().bind(this.minutes.asString());
        buttons.buttonsController.seconds.textProperty().bind(this.seconds.asString());

        listView.featuresListController.onOpen = vm.open;
        listView.featuresListController.testFileName.addListener((o, ov, nv) -> {
            vm.initTimeSeriesTest(nv);
        });

        listView.featuresListController.fileName.addListener((o, ov, nv) -> {
            vm.initTimeSeries(nv);
            tmpBool = true;
            tr1 = new Thread(() -> {
                corrFeat = vm.correlatedF;
                tmpBool = false;
                listView.featuresListController.corrFeat = vm.correlatedF;
            });
        });


        featurename = new SimpleStringProperty();
        this.feature.addListener((observableValue, oldValue, newValue) -> {
            this.anomalyReports = vm.anomalyReports;
            if (!listAnomalyBool) {
                this.readAnomalyReport();
                listAnomalyBool = true;
            }
            vm.timeStep.addListener((o, ov, nv) -> {
                if (algo_name.getValue() != null) {
                    if (algo_name.getValue().equals("RegressionAlg")) {
                        String f2 = getCorFeature(feature.get());
                        String tmp = feature.get() + "," + f2;
                        if (mapAnomaly.containsKey(tmp)) {
                            ArrayList<Integer> tmpArr = mapAnomaly.get(tmp);
                            if (index < tmpArr.size()) {
                                if (tmpArr.get(index) == vm.timeStep.get()) {
                                    listView.featuresListController.changeGraphColor1();
                                    index++;
                                }
                            } else {
                                listView.featuresListController.changeGraphColor2();
                            }
                        }
                    }
                }

            });
        });


        this.feature.addListener((observableValue, oldValue, newValue) -> {
            if (tmpBool == true) {
                tr1.start();
            }
            vm.featureName.setValue(newValue);
            vm.timeStep.addListener((o, ov, nv) -> {
                Platform.runLater(() -> {
                    ArrayList<String> list = vm.getSomeFeature(observableValue.getValue());
                    String cor = getCorFeature(observableValue.getValue());
                    ArrayList<String> listOfCorrelated = vm.getSomeFeature(cor);
                    if (!bool.getValue()) {
                        listView.featuresListController.setGraph();
                        bool.set(true);
                    }
                    listView.featuresListController.drawLeftGraph(nv.toString(), Double.parseDouble(list.get(nv.intValue())));
                    if (listOfCorrelated != null) {
                        listView.featuresListController.drawRightGraph(nv.toString(), Double.parseDouble(listOfCorrelated.get(nv.intValue())));
                    }
                    if (algo_name.getValue() != null && (this.seriesArrayList != null) && algo_name.getValue().equals("zScoreAlg")) {
                        listView.featuresListController.paintZSAlgo(this.seriesArrayList, nv.intValue(), observableValue.toString());
                        cur_algo = this.algo_name.getValue();
                    }
                    if (this.algo_name.getValue() != cur_algo) {
                        if (this.seriesArrayList != null) {
                            cur_algo = this.algo_name.getValue();
                            if (algo_name.get().equals("RegressionAlg")) {
                                listView.featuresListController.paintRegAlgo(this.seriesArrayList);
                            }
                        }
                    }
                });
            });

            if ((newValue != null) && (!newValue.equals(oldValue))) {
                listView.featuresListController.clearGraph();
                listView.featuresListController.clearRegAlgo();
                cur_algo = "";
                this.seriesArrayList = null;
            }
        });

        if (bool.get()) {
            tmpThread.start();
        }


        joystick.joystickController.rudder.valueProperty().bind(vm.getProperty("rudder"));
        joystick.joystickController.throttle.valueProperty().bind(vm.getProperty("throttle"));
        joystick.joystickController.aileron.bind(vm.getProperty("aileron"));
        joystick.joystickController.elevator.bind(vm.getProperty("elevator"));
        joystick.joystickController.paint();

        clocks.clocksController.altitude.valueProperty().bind(vm.getProperty("altitude"));
        clocks.clocksController.heading.valueProperty().bind(vm.getProperty("heading"));
        clocks.clocksController.airspeed.valueProperty().bind(vm.getProperty("airspeed"));
        clocks.clocksController.pitch.valueProperty().bind(vm.getProperty("pitch"));
        clocks.clocksController.roll.valueProperty().bind(vm.getProperty("roll"));
        clocks.clocksController.yaw.valueProperty().bind(vm.getProperty("yaw"));

        buttons.setLayoutY(450);
        buttons.setLayoutX(20);
        joystick.setLayoutX(720);
        joystick.setLayoutY(-10);
        listView.setLayoutX(28);
        clocks.setLayoutX(660);
        clocks.setLayoutY(230);
        popup.setLayoutX(70);
        popup.setLayoutY(450);


    }


    public void readAnomalyReport() {
        String temp = anomalyReports.get(0).description;
        System.out.println(anomalyReports);
        for (AnomalyReport a : anomalyReports) {
            if (!a.description.equals(temp)) {
                if (!tmpAnomalyBool) {
                    arrTimeStepAnomaly = new ArrayList<>();
                    tmpAnomalyBool = true;
                }
                arrTimeStepAnomaly.add((int) a.timeStep);
                mapAnomaly.put(a.description, arrTimeStepAnomaly);
                continue;
            }
            arrTimeStepAnomaly.add((int) a.timeStep);
            mapAnomaly.put(a.description, arrTimeStepAnomaly);
        }
    }

    public String getCorFeature(String name) {
        for (CorrelatedFeatures cf : corrFeat) {
            if (cf.feature1.equals(name)) {
                return cf.feature2;
            }
        }
        return null;
    }

    public void ClassLoadPop(String path) {
        if (path.equals("HybridAlg") || path.equals("zScoreAlg") || path.equals("RegressionAlg")) {
            loadClass(path);
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("the class is not correct or not been upload");
            a.show();
        }
    }

    public void loadClass(String directory) {

        URLClassLoader urlClassLoader = null;
        try {
            urlClassLoader = URLClassLoader.newInstance(new URL[]{
                    new URL("file://C:\\Users\\baros\\IdeaProjects\\patam2-jar\\out\\artifacts\\patam2_jar_jar\\patam2-jar.jar")
            });
            Class<?> c = urlClassLoader.loadClass("server." + directory);

            TimeSeriesAnomalyDetector sc = (TimeSeriesAnomalyDetector) c.newInstance();
            vm.SetAnomalyDetector(sc);
            this.seriesArrayList = vm.setClass();

        } catch (MalformedURLException e) {

        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void update(Observable o, Object arg) {

    }
}
