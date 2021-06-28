package server;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.*;
import java.lang.Math;


public class zScoreAlg implements TimeSeriesAnomalyDetector {
    /*----------------------------------------Data Members----------------------------------------*/
    Map<Integer, TimeSeries.myEntry> csv;
    Collection<TimeSeries.myEntry> mapEntry;
    Map<String, ArrayList<String>> txValueLearnNormal;
    ArrayList<AnomalyReport> reports;
    TimeSeries timeSeries;
    Thread zThread;

    /*----------------------------------------CTOR----------------------------------------*/
    public zScoreAlg() {
        csv = new HashMap<>();
        txValueLearnNormal = new HashMap<>();
        reports = new ArrayList<>();
        mapEntry = csv.values();
    }

    /*------------------------------------Auxiliary functions------------------------------------*/

    public Map<String, ArrayList<String>> getTxValueLearnNormal() {
        return txValueLearnNormal;
    }

    // zScore's calculate
    public float zScore(float[] points, float p) {
        if (Math.sqrt(StatLib.var(points)) == 0)
            return 0;
        float z = (float) (Math.abs(p - StatLib.avg(points)) / Math.sqrt(StatLib.var(points)));
        return z;

    }

    // Calculate the tx value of each column
    public Map<String, ArrayList<String>> tx(int size) {
        Map<String, ArrayList<String>> map = new HashMap<>();
        float[] tmp = null;
        int i = 0, j = 0;
        ArrayList<String> index;
        ArrayList<String> tmpValues = new ArrayList<>();

        for (Integer integer : csv.keySet()) {
            tmpValues.add(csv.get(integer).getFeature());
        }
        for (int c = 0; c < csv.size(); c++) {

            ArrayList<String> arr = csv.get(i).getArray();
            //System.out.println(arr);
            i++;
            int time = 0;
            float maxZ = -1, z = 0;
            index = new ArrayList<>();

            for (String str : arr) {
                j++;
                time += 1;
                tmp = new float[time - 1];
                if (time > 1) {
                    for (int k = 0; k < time - 1; k++) {
                        tmp[k] = Float.parseFloat(arr.get(k));
                    }
                    z = zScore(tmp, Float.parseFloat(str));

                }
                if (z > maxZ) {
                    maxZ = z;
                    index = new ArrayList<>();
                    index.add(String.valueOf(j));
                    index.add(String.valueOf(maxZ));
                }

            }
            j = 0;
            //System.out.println(tmpValues);
            map.put(tmpValues.get(i - 1), index);
        }
        return map;
    }


    /********************************************************************************************/

    @Override
    public void learnNormal(TimeSeries ts) {
        this.timeSeries = ts;
        csv = ts.getMap();
        txValueLearnNormal = tx(csv.values().size());
        System.out.println("learnNormal zScore");
    }

    @Override
    public List<AnomalyReport> detect(TimeSeries ts) {
        this.timeSeries = ts;

        csv = ts.getMap();

        reports = new ArrayList<>();
        zThread = new Thread(() -> {
            int i = 0;
            ArrayList<String> tmpValues = new ArrayList<>();
            float[] tmp = null;
            for (Integer integer : csv.keySet()) {
                tmpValues.add(csv.get(integer).getFeature());
            }
            for (int c = 0; c < csv.size(); c++) {
                ArrayList<String> arr = csv.get(i).getArray();
                i++;
                int time = 0;
                float z = 0;
                String key = tmpValues.get(i - 1);
                ArrayList<String> value = txValueLearnNormal.get(key);

                String threshold = value.get(1);//the highest tx of the column

                for (String str : arr) {
                    time += 1;
                    tmp = new float[time - 1];

                    if (time > 1) {
                        for (int k = 0; k < time - 1; k++) {
                            tmp[k] = Float.parseFloat(arr.get(k));
                        }
                        z = zScore(tmp, Float.parseFloat(str));

                    }
                    if (z > Float.parseFloat(threshold)) {
                        reports.add(new AnomalyReport(key, Long.parseLong(String.valueOf(z))));

                    }
                }
            }
            System.out.println("detect zScore");
        });
        zThread.start();
        return reports;
    }

    public ArrayList<Float> findAllZScore(String featureName) {
        ArrayList<String> arr = null;
        float z = 0;
        int time = 0;
        float[] tmp = null;
        ArrayList<Float> zScoreArr = new ArrayList<>();

        for (Integer i : csv.keySet()) {
            time += 1;
            tmp = new float[time - 1];
            if (csv.get(i).getFeature().equals(featureName)) {
                arr = csv.get(i).getArray();
                for (String str : arr) {
                    for (int k = 0; k < time - 1; k++) {
                        tmp[k] = Float.parseFloat(arr.get(k));
                    }
                    z = zScore(tmp, Float.parseFloat(str));
                    zScoreArr.add(z);
                }
            }
            time++;
        }


        return zScoreArr;
    }

    @Override
    public ArrayList<XYChart.Series> paint(String featureName) {
        ArrayList<Float> zScoreArr = findAllZScore(featureName);
        System.out.println("zScoreArr  ALGZSCORE" + zScoreArr);

        ArrayList<XYChart.Series> total = new ArrayList<>();

        XYChart.Series series1 =new XYChart.Series();
        int i=0;
        IntegerProperty index=new SimpleIntegerProperty();
        for(Float f : zScoreArr) {
            index.setValue(i);
            series1.getData().add(new XYChart.Data<>(index.toString(),f));
            //System.out.println("series1 zscore  : "+ series1);
            i++;
        }

        total.add(series1);
       // System.out.println("total zScore Algo : "+total);
        //System.out.println("y :   "+series1.getChart().getYAxis() + "x     : "+series1.getChart().getXAxis());

        return total;
    }


}