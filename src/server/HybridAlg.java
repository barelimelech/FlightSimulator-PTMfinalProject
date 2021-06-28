package server;

import javafx.scene.chart.XYChart;

import static server.SmallestEnclosingCircle.makeCircle;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HybridAlg implements TimeSeriesAnomalyDetector {

    /*------------------------------------Data Member------------------------------------*/
    private final AuxiliaryFunctions auxiliaryFunctions = new AuxiliaryFunctions();
    private List<CorrelatedFeatures> cf;
    private Map<String, ArrayList<String>> txValueLearnNormal;
    private zScoreAlg zScoreDM;
    private RegressionAlg regressionDM;
    private TimeSeries train;
    private List<CorrelatedFeatures> cf1; // list of correlated features to the second algorithm.
    private List<CorrelatedFeatures> cf2; // list of correlated features to the second algorithm.
    private List<CorrelatedFeatures> cf3; // list of correlated features to the third algorithm.
    private TimeSeriesAnomalyDetector obj;
    /*----------------------------------------CTOR----------------------------------------*/

    public HybridAlg() {
        this.cf = new ArrayList<CorrelatedFeatures>();
        this.cf1 = new ArrayList<CorrelatedFeatures>();
        this.cf2 = new ArrayList<CorrelatedFeatures>();
        this.cf3 = new ArrayList<CorrelatedFeatures>();
    }

    private List<AnomalyReport> hybridDetect(TimeSeries ts) {
        ArrayList<Point> points = new ArrayList<>();
        HashMap<Integer,TimeSeries.myEntry> m = ts.getMap();
        List<AnomalyReport> list = new ArrayList<>();

        for (int i = 0; i < cf3.size(); i++) {

            int in1 = ts.firstIndexOfFeature(cf3.get(i).feature1);
            int in2 = ts.firstIndexOfFeature(cf3.get(i).feature2);

            ArrayList<String> ar1 = m.get(in1).getArray();
            ArrayList<String> ar2 = m.get(in2).getArray();

            points.addAll(auxiliaryFunctions.floatToPoint(auxiliaryFunctions.stringListToFloatArray(ar1),
                    auxiliaryFunctions.stringListToFloatArray(ar2)));
            Circle c = makeCircle(points);

            for (int j = 0; j < points.size(); j++) {
                // checking if the point is in the circle.
                if (!c.contains(points.get(j))) {
                    list.add(new AnomalyReport(cf3.get(i).feature1 + "-" + cf3.get(i).feature2, j + 1));
                }
            }
            points.clear();
        }
        return list;
    }

    /****************************************************************************************/


    @Override
    public void learnNormal(TimeSeries ts) {
        train = ts;
        ArrayList<String> feat = train.getFeatures();
        int len = train.getNumOfRow();
        HashMap<Integer, TimeSeries.myEntry> map = train.getMap();

        //אחסון כל קובץ הCSV במטריצה
        String[][] vals = new String[feat.size()][len];
        for (int i = 0; i < feat.size(); ++i) {
            for (int j = 0; j < len; ++j) {
                vals[i][j] = map.get(i).getArray().get(j);
            }
        }

        /*----------------------------------------------------*/
        //למידה של רגרסיה והכנסה למערך cf2
        regressionDM = new RegressionAlg();
        regressionDM.learnNormal(ts);
        cf2 = regressionDM.getCorrelatedList();
        /*----------------------------------------------------*/

        for (int i = 0; i < feat.size(); ++i) {
            for (int j = i + 1; j < feat.size(); ++j) {
                float p = StatLib.pearson(auxiliaryFunctions.stringToFloat(vals[i]), auxiliaryFunctions.stringToFloat(vals[j]));

                if (Math.abs(p) >= 0.5 && Math.abs(p)<0.95) {
                    Point[] ps = auxiliaryFunctions.toPoints(auxiliaryFunctions.stringListToFloatList(map.get(i).getArray()),
                            auxiliaryFunctions.stringListToFloatList((map.get(j).getArray())));
                    Line lin_reg = StatLib.linear_reg(ps);
                    float threshold = auxiliaryFunctions.findThreshold(ps, lin_reg) * 1.1f; // 10% increase

                    CorrelatedFeatures c = new CorrelatedFeatures(feat.get(i), feat.get(j), p, lin_reg, threshold);
                    cf3.add(c);
                }

                else if(Math.abs(p) < 0.5){

                    ArrayList<Float> al = auxiliaryFunctions.stringListToFloatList(map.get(i).getArray());
                    Point[] ps = auxiliaryFunctions.toPoints(al,al);
                    Line lin_reg = StatLib.linear_reg(ps);
                    //  float threshold = auxiliaryFunctions.findThreshold(ps, lin_reg) * 1.1f; // 10% increase

                    CorrelatedFeatures c = new CorrelatedFeatures(feat.get(i), feat.get(i), p, lin_reg, 0);
                    cf1.add(c);
                }
            }
        }
        System.out.println("learnNormal hybrid");
    }

    @Override
    public List<AnomalyReport> detect(TimeSeries ts) {

        train = ts;
        HashMap<Integer, TimeSeries.myEntry> map = train.getMap();

        TimeSeries newTS;
        List<AnomalyReport> temp;
        List<AnomalyReport> Report = new ArrayList<>();


        /*-- -- -- -- -- -- --- -- -- --- ZSCORE -- -- -- -- -- -- --- -- -- ---*/
        /*zScoreDM = new zScoreAlg();

        newTS = new TimeSeries(auxiliaryFunctions.ListToCSV(cf1, map));
        zScoreDM.learnNormal(newTS);

        newTS = new TimeSeries(auxiliaryFunctions.ListToCSV(cf1, map));
        temp = zScoreDM.detect(newTS);
        if (temp != null)
            Report.addAll(temp);*/


        /*-- -- -- -- -- -- --- -- -- --- REGRESSION -- -- -- -- -- -- --- -- -- ---*/
        obj = new RegressionAlg(cf2);
        temp = obj.detect(ts);

        if (temp != null)
            Report.addAll(temp);

        /*-- -- -- -- -- -- --- -- -- --- HYBRID -- -- -- -- -- -- --- -- -- ---*/

        Report.addAll(hybridDetect(ts));

        System.out.println("detect hybrid");
        return Report;
    }

    @Override
    public ArrayList<XYChart.Series> paint(String featureName) {
        return null;
    }


}
