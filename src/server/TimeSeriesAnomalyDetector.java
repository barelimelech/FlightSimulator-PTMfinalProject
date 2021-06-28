package server;

import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;

public interface TimeSeriesAnomalyDetector {
    void learnNormal(TimeSeries ts);
    List<AnomalyReport> detect(TimeSeries ts);
    public Object paint(String featureName);
}
