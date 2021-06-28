package server;

import javafx.application.Platform;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegressionAlg implements TimeSeriesAnomalyDetector {

    //DM
    private final AuxiliaryFunctions auxiliaryFunctions;
    private ArrayList<CorrelatedFeatures> cf;
    private TimeSeries learnTS, testTS;
    private List<AnomalyReport> reports;

    //Constructor
    public RegressionAlg() {
        cf = new ArrayList<>();
        auxiliaryFunctions = new AuxiliaryFunctions();
    }

    public RegressionAlg(List<CorrelatedFeatures> newCf) {
        cf = new ArrayList<>();
        cf.addAll(newCf);
        auxiliaryFunctions = new AuxiliaryFunctions();
    }

    public ArrayList<CorrelatedFeatures> getCorrelatedList() {
        return cf;
    }

    /*----------------------------------------------------------------------------*/

    @Override
    public void learnNormal(TimeSeries ts) {
        learnTS = ts;
        ArrayList<String> feat = ts.getFeatures();//רשימת 42 המאפיינים
        int len = ts.getNumOfRow();//מספר השורות = 2175
        HashMap<Integer, TimeSeries.myEntry> map = ts.getMap();


        //אחסון כל קובץ הCSV במטריצה
        String vals[][] = new String[feat.size()][len];//42 עמודות ו2175 שורות
        for (int i = 0; i < feat.size(); ++i) {//עבור כל עמודה
            for (int j = 0; j < len; ++j) {//עבור כל שורה - יכול להיות שצריך להתחיל מ1 ולא מ0
                vals[i][j] = map.get(i).getArray().get(j);//במפה שלנו - במפתח מספר i הולכים למערך המייצג עמודה לתא הj
            }
        }

        for (int i = 0; i < feat.size(); ++i) {
            for (int j = i + 1; j < feat.size(); ++j) {
                float p = StatLib.pearson(auxiliaryFunctions.stringToFloat(vals[i]), auxiliaryFunctions.stringToFloat(vals[j]));

                if (Math.abs(p) > 0.95) {
                    Point ps[] = auxiliaryFunctions.toPoints(auxiliaryFunctions.stringListToFloatList(map.get(i).getArray()),
                            auxiliaryFunctions.stringListToFloatList((map.get(j).getArray())));
                    Line lin_reg = StatLib.linear_reg(ps);
                    float threshold = auxiliaryFunctions.findThreshold(ps, lin_reg) * 1.1f; // 10% increase

                    CorrelatedFeatures c = new CorrelatedFeatures(feat.get(i), feat.get(j), p, lin_reg, threshold);
                    cf.add(c);
                }
            }
        }
    }


    @Override
    public List<AnomalyReport> detect(TimeSeries ts) {
        testTS = ts;
        ArrayList<AnomalyReport> v = new ArrayList<>();
        int f1Index, f2Index;

        for (CorrelatedFeatures c : cf) {
            f1Index = ts.firstIndexOfFeature(c.feature1);
            f2Index = ts.firstIndexOfFeature(c.feature2);

            ArrayList<Float> x = auxiliaryFunctions.stringListToFloatList(ts.getMap().get(f1Index).getArray());
            ArrayList<Float> y = auxiliaryFunctions.stringListToFloatList(ts.getMap().get(f2Index).getArray());
            for (int i = 0; i < x.size(); i++) {
                if (Math.abs(y.get(i) - c.lin_reg.f(x.get(i))) > c.threshold) {
                    String d = c.feature1 + "," + c.feature2;
                    v.add(new AnomalyReport(d, (i + 1)));
                }
            }
        }
        reports = v;
        return v;
    }


    private float maxValue(ArrayList<Float> arr) {
        float max = 0;

        for (float f : arr) {
            if (f > max) max = f;
        }

        return max;
    }

    private float minValue(ArrayList<Float> arr) {
        float min = 10000;

        for (float f : arr) {
            if (f < min) min = f;
        }

        return min;
    }

    @Override
    public ArrayList<XYChart.Series> paint(String featureName) {
        ArrayList<XYChart.Series> total = new ArrayList<>();
        //Platform.runLater(() -> {
        TimeSeries ts = new TimeSeries("reg_flight.csv");
        learnTS = ts;
        testTS = ts;

        System.out.println(" hello   world  : feature  : " + featureName);
        HashMap<Integer, TimeSeries.myEntry> learnMap = learnTS.getMap();
        // HashMap<Integer, TimeSeries.myEntry> testMap = testTS.getMap();

        XYChart.Series lineSeries = new XYChart.Series<>();
        XYChart.Series dotsSeries = new XYChart.Series();
        XYChart.Series learnSeries = new XYChart.Series();
        XYChart.Series detectSeries = new XYChart.Series();

        String feat2 = "";
        Line lin_reg = null;

        System.out.println(" cf         :" + cf);

        //מציאת העמודה הקורלטיבית לעמודה שהתקבלה + שמירת קו הרגרסיה של שתי העמודות
        for (CorrelatedFeatures corr : cf) {
            if (corr.feature1.equals(featureName)) {
                feat2 = corr.feature2;
                lin_reg = corr.lin_reg;
                break;
            } else if (corr.feature2.equals(featureName)) {
                feat2 = corr.feature1;
                lin_reg = corr.lin_reg;
                break;
            }
        }

        System.out.println(" feature name   : " + featureName);
        // שמירת עמודות הפיצרים הקורלטיבים כערכי float במקום string
        ArrayList<Float> f1 = auxiliaryFunctions.stringListToFloatList(
                learnMap.get(learnTS.firstIndexOfFeature(featureName)).getArray());

        ArrayList<Float> f2 = auxiliaryFunctions.stringListToFloatList(
                learnMap.get(learnTS.firstIndexOfFeature(feat2)).getArray());

        System.out.println(f1);
        System.out.println(f2);

        //יצירת שתי נקודות (עם ערכי מינימום ומקסימום של שתי העמודות) על מנת לצייר את קו הרגרסיה
        Point minPoint = new Point(minValue(f1), lin_reg.f(minValue(f1)));
        Point maxPoint = new Point(maxValue(f2), lin_reg.f(maxValue(f2)));

        System.out.println(minPoint.x + " " + minPoint.y + "       " + maxPoint.x + " " + maxPoint.y);

        lineSeries.getData().add(new XYChart.Data<>(minPoint.x, minPoint.y));
        lineSeries.getData().add(new XYChart.Data<>(maxPoint.x, maxPoint.y));


        //מערך הנקודות של טיסה תקינה (אפורות)
        Point[] points = auxiliaryFunctions.toPoints(f1, f2);
        for (Point p : points) {
            dotsSeries.getData().add(new XYChart.Data<>(p.x, p.y));
        }


        total.add(lineSeries);
        total.add(dotsSeries);
        System.out.println("line series : " + lineSeries.getData() + " dots series : " + dotsSeries.getData());

        System.out.println("total : " + total);
        return total;
    }


}