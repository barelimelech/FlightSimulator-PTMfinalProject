package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TimeSeries {

    //new class with feature and ArrayList<String>
    public class myEntry {
        private String feature;
        private ArrayList<String> array;

        public String getFeature() {
            return feature;
        }

        public void setFeature(String feature) {
            this.feature = feature;
        }

        public ArrayList<String> getArray() {
            return array;
        }

        public void setArray(ArrayList<String> array) {
            this.array = array;
        }
    }


    //private Map<String, ArrayList<String>> csv;
    private HashMap<Integer, myEntry> myMap;
    private ArrayList<String> features;
    private int numOfRow;

    //CTOR
    public TimeSeries(String csvFileName) {
        /*this.csv = new HashMap<>();*/

        myMap = new HashMap<>();
        this.features = new ArrayList<>();

        try {
            loadFromCSV(csvFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadFromCSV(String fileName) throws Exception {
        BufferedReader reader = null;
        String line = null;
        String str[];
        try {
            reader = new BufferedReader(new FileReader(fileName));

            if ((line = reader.readLine()) != null) {
                str = line.split(",");
                this.features.addAll(Arrays.asList(str));

                for (int i = 0; i < str.length; ++i) {
                    myEntry e = new myEntry();
                    e.setFeature(str[i]);
                    e.array = new ArrayList<>();
                    myMap.put(i, e);
                }
            }

            while ((line = reader.readLine()) != null) {
                str = line.split(","); // -8, -5, 10
                for (int k = 0; k < features.size(); ++k) {
                    myMap.get(k).array.add(str[k]);
                    //this.csv.get(features.get(i) + i).add(str[i]);
                }
            }

            numOfRow = myMap.get(0).array.size();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }
    }

    /*public Map<String, ArrayList<String>> getCsv() {return csv; }

    public void setCsv(Map<String, ArrayList<String>> csv) {
        this.csv = csv;
    }*/

    public HashMap<Integer, myEntry> getMap() {
        return myMap;
    }

    public void setMap(HashMap<Integer, myEntry> map) {
        myMap = map;
    }

    public ArrayList<String> getFeatures() {
        return features;
    }

    public void setFeatures(ArrayList<String> titles) {
        this.features = titles;
    }

    public int getNumOfRow() {
        return numOfRow;
    }

    public int firstIndexOfFeature(String featureName) {
        for (int index : myMap.keySet()) {
            if (myMap.get(index).feature.equals(featureName))
                return index;
        }
        return 0;
    }

    public String readLine(int index) {

        StringBuilder line = new StringBuilder();
        line.append(myMap.get(0).getArray().get(index));
        for (int i : myMap.keySet()) {
            if(i == 0)
                continue;
            line.append(",");
            line.append(myMap.get(i).getArray().get(index));
        }
        return line.toString();
    }


}
