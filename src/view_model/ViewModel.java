package view_model;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import model.Model;
import server.*;
import view.readXML.myProperty;
import view.readXML.xmlMain;
//import server.HybridAlg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class ViewModel implements Observer {
//צריך להיות פה timeSeries

    public Timer c;
    public Timer timer = null;
    Socket fg;
    TimeSeriesAnomalyDetector timeSeriesAnomalyDetector;
    public ListProperty<AnomalyReport> anomalyReports;
    public final Runnable play, pause, stop, left, right, leftX2, rightX2;
    public IntegerProperty timeStep, timeStep1;
    public DoubleProperty open, aileron, elevator, rudder, throttle, pitch, roll, heading, airspeed, altitude, yaw;
    private HashMap<String, DoubleProperty> displayVariables;
    public TimeSeries ts;
    public TimeSeries testTS;
    public StringProperty fileName;
    public ObservableList<String> observableList;
    public List<AnomalyReport> tmpAnomalyReports = new ArrayList<>();
    HashMap<String, String> properties;
    public Map<Integer, TimeSeries.myEntry> myCsv;
    Collection<TimeSeries.myEntry> mapEntry;
    public DoubleProperty vmSpeed = new SimpleDoubleProperty();
    public DoubleProperty minProp, maxProp;
    public static ArrayList<CorrelatedFeatures> correlatedF;
    public static RegressionAlg reg;
    public static zScoreAlg zScoreAlg;
    public static StringProperty featureName;
    public StringProperty tmpFeatureName;
    ArrayList<XYChart.Series> arrSeries;
    ArrayList<view.readXML.myProperty> propList = view.readXML.xmlMain.getPropertyList();

    public ViewModel() {
        featureName = new SimpleStringProperty();
        tmpFeatureName = new SimpleStringProperty();

        minProp = new SimpleDoubleProperty();
        maxProp = new SimpleDoubleProperty();

        properties = new HashMap<>();
        c = new Timer();
        loadFileProperties("properties.csv");

        timer = new Timer();
        myCsv = new HashMap<>();

        observableList = new SimpleListProperty<>();
        fileName = new SimpleStringProperty();
        rudder = new SimpleDoubleProperty();
        throttle = new SimpleDoubleProperty();
        aileron = new SimpleDoubleProperty();
        elevator = new SimpleDoubleProperty();
        pitch = new SimpleDoubleProperty();
        roll = new SimpleDoubleProperty();
        heading = new SimpleDoubleProperty();
        airspeed = new SimpleDoubleProperty();
        altitude = new SimpleDoubleProperty();
        yaw = new SimpleDoubleProperty();

        timeStep = new SimpleIntegerProperty(0);
        Model m = new Model(timeStep);
        m.addObserver(this);

        arrSeries = new ArrayList<>();
        timeStep1 = timeStep;
        this.vmSpeed.bind(m.speed);
        anomalyReports = new SimpleListProperty<>();

        displayVariables = new HashMap<>();
        displayVariables.put("rudder", rudder);
        displayVariables.put("throttle", throttle);
        displayVariables.put("aileron", aileron);
        displayVariables.put("elevator", elevator);
        displayVariables.put("airspeed", airspeed);
        displayVariables.put("altitude", altitude);
        displayVariables.put("heading", heading);
        displayVariables.put("roll", roll);
        displayVariables.put("yaw", yaw);
        displayVariables.put("pitch", pitch);


        timeStep.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                int line = newValue.intValue();
                c.updateTimer(newValue.intValue() - oldValue.intValue());
                if (ts != null && ts.getMap() != null) {
                    for (String s : displayVariables.keySet()) {
                        String index = properties.get(s);
                        ArrayList<String> arr = ts.getMap().get(Integer.valueOf(index)).getArray();
                        double min = findMin(s);
                        double max = findMax(s);

                        String t = arr.get(line);
                        double normalisedValue;
                        if (s.equals("throttle") || s.equals("rudder") || s.equals("aileron") || s.equals("elevator"))
                            Platform.runLater(() -> displayVariables.get(s).set(Double.parseDouble(t)));
                        else {
                            normalisedValue = 100*normalise1(Double.parseDouble(t), min, max);
                            Platform.runLater(() -> displayVariables.get(s).set(normalisedValue));
                        }
                    }

                }
            }
        });


        play = () -> {
           // System.out.println("play 2");
            m.setTimeSeries(ts);
            m.play(1);
        };
        pause = () -> {
            m.pause();
        };
        stop = () -> {
            m.stop();
        };
        left = () -> m.left();
        leftX2 = () -> m.leftX2();
        right = () -> m.right();
        rightX2 = () -> m.rightX2();


    }

    public double findMax(String feature) {
        maxProp = new SimpleDoubleProperty(0);
        String index = properties.get(feature);
        ArrayList<String> arr = ts.getMap().get(Integer.valueOf(index)).getArray();

        for (String s : arr) {
            if (Double.parseDouble(s) > maxProp.get())
                maxProp.setValue(Double.parseDouble(s));
        }
        return maxProp.get();
    }

    public double findMin(String feature) {
        minProp = new SimpleDoubleProperty(0);
        String index = properties.get(feature);
        ArrayList<String> arr = ts.getMap().get(Integer.valueOf(index)).getArray();

        for (String s : arr) {
            if (Double.parseDouble(s) < minProp.get())
                minProp.setValue(Double.parseDouble(s));
        }
        return minProp.get();
    }

    public double normalise1(double value, double min, double max) {
        return ((value - min) / (max - min));
    }

    public double normalise2(double value, double min, double max) {
        return (2 * ((value - min) / (max - min)) - 1);
    }

    public DoubleProperty getProperty(String name) {
        return displayVariables.get(name);
    }


    public ArrayList<String> getSomeFeature(String featureName) {
        if (properties.containsKey(featureName)) {
            int index = Integer.parseInt(properties.get(featureName));
            ArrayList<String> value = ts.getMap().get(index).getArray();
            return value;
        }

        myCsv = ts.getMap();
        mapEntry = ts.getMap().values();

        int index = ts.firstIndexOfFeature(featureName);
        ArrayList<String> value = ts.getMap().get(index).getArray();
        return value;
    }


    public void loadFileProperties(String propertiesFileName) {
        properties = new HashMap<>();
        BufferedReader in;
        fg = new Socket();
        try {
            in = new BufferedReader(new FileReader(propertiesFileName));
            String line;
            while ((line = in.readLine()) != null) {
                String[] sp = line.split(",");
                properties.put(sp[0], sp[1]);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void initTimeSeriesTest(String fileName) {
        testTS = new TimeSeries(fileName);

        Thread th1 = new Thread(() -> {
            try {
                Thread.sleep(20 * 1000);
                System.out.println(" sleep is over ");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tmpAnomalyReports = reg.detect(testTS);
            ObservableList<AnomalyReport> observableList = FXCollections.observableArrayList(tmpAnomalyReports);
            anomalyReports = new SimpleListProperty<>(observableList);
          /*  System.out.println("anomaly reports thread  " + anomalyReports);
            for (AnomalyReport a : anomalyReports) {
                System.out.println("timeStep " + a.timeStep);
                System.out.println("description " + a.description);
            }*/

        });
        th1.start();

    }

    public void initTimeSeries(String fileName) {
        ts = new TimeSeries(fileName);
        Model m = new Model(timeStep);
        m.setTimeSeries(ts);
        Thread th = new Thread(() -> {
            TimeSeries tmpTs = new TimeSeries("reg_flight.csv");
            reg = new RegressionAlg();
            reg.learnNormal(tmpTs);
            correlatedF = reg.getCorrelatedList();
           // System.out.println(" cor features thread  " + correlatedF);

        });
        th.start();

        Thread th2 = new Thread(() -> {
            TimeSeries tmpTs = new TimeSeries("reg_flight.csv");
            zScoreAlg = new zScoreAlg();
            zScoreAlg.learnNormal(tmpTs);
           // System.out.println(" zScore algo is done  ");

        });
        th2.start();

   /*     for (myProperty mp : propList) {
            mp.setValMin(String.valueOf(findMin(mp.getName())));
            mp.setValMax(String.valueOf(findMax(mp.getName())));
        }
        xmlMain.WriteToXML(propList);*/
    }

    public void setFeatureName(String featureName1) {
        tmpFeatureName.setValue(featureName1);
        System.out.println(featureName.get());
    }


    public ArrayList<XYChart.Series> setClass() {
        System.out.println("view model feature : " + featureName.get());

        TimeSeries tmpTs = new TimeSeries("reg_flight.csv");
        Class<?> c1 = timeSeriesAnomalyDetector.getClass();

        if (c1 == RegressionAlg.class) {

            arrSeries = reg.paint(featureName.get());
            return arrSeries;

        } else if (c1 == zScoreAlg.class) {
            arrSeries = zScoreAlg.paint(featureName.get());
            return arrSeries;
        }
        return null;

    }

    public void SetAnomalyDetector(TimeSeriesAnomalyDetector sc) {
        this.timeSeriesAnomalyDetector = sc;
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
