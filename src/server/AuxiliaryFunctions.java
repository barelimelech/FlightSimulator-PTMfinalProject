package server;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuxiliaryFunctions {

    public Point[] toPoints(ArrayList<Float> x, ArrayList<Float> y) {
        Point[] ps = new Point[x.size()];
        for (int i = 0; i < ps.length; i++)
            ps[i] = new Point(x.get(i), y.get(i));
        return ps;
    }

    public float findThreshold(Point[] ps, Line rl) {
        float max = 0;
        for (int i = 0; i < ps.length; i++) {
            float d = Math.abs(ps[i].y - rl.f(ps[i].x));
            if (d > max)
                max = d;
        }
        return max;
    }

    public ArrayList<Point> floatToPoint(float[] X, float[] Y) { //creat list of points
        ArrayList<Point> points = new ArrayList<Point>();

        for (int i = 0; i < X.length; i++) {
            Point p = new Point(X[i], Y[i]);
            points.add(p);
        }
        return points;
    }

    //Create a new csv file for zScore algorithm
    public String ListToCSV(List<CorrelatedFeatures> list, HashMap<Integer, TimeSeries.myEntry> table) {

        String FileName = "zScoreCsv.csv";
        try {
            FileWriter outputFile = new FileWriter(FileName);

            ArrayList<String> featuresList = new ArrayList<>();
            for (CorrelatedFeatures c : list) {
                featuresList.add(c.feature1);
            }

            // 2174
            int numOfRows = table.get(0).getArray().size();
            ArrayList<String> newLine;


            String temp;           // 2174
            for (int row = -1; row < numOfRows; ++row) {
                newLine = new ArrayList<>();

                if (row == -1)
                    newLine = featuresList;//כדי לכתוב את שמות העמודות הרלוונטיות

                else {
                    for (int i : table.keySet()) {
                        if (featuresList.contains(table.get(i).getFeature()))
                            newLine.add(table.get(i).getArray().get(row));
                    }
                }

                // לולאה שכותבת לתוך הקובץ
                for (int k = 0; k < newLine.size(); k++) {
                    if (k == newLine.size() - 1) {
                        outputFile.write(newLine.get(k) + "\n");
                        outputFile.flush();
                    } else {
                        outputFile.write(newLine.get(k) + ",");
                        outputFile.flush();
                    }
                }

            }

            outputFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return FileName;
    }


    public float[] stringListToFloatArray(ArrayList<String> array) {
        int length = array.size();
        float[] floatValues = new float[length];

        for (int i = 0; i < length; i++) {
            floatValues[i] = Float.parseFloat(array.get(i));
        }
        return floatValues;
    }

    public float[] stringToFloat(String[] arr) {
        int length = arr.length;
        float[] floatValues = new float[length];

        for (int i = 0; i < length; i++) {
            floatValues[i] = Float.parseFloat(arr[i]);
        }
        return floatValues;
    }

    public ArrayList<Float> stringListToFloatList(ArrayList<String> strings) {
        ArrayList<Float> floatList = new ArrayList<>();
        for (String s : strings) {
            floatList.add(Float.parseFloat(s));
        }
        return floatList;
    }
}
