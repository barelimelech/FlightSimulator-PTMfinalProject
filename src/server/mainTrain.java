package server;

import java.util.List;

public class mainTrain {
    public static void main(String[] args) {
        TimeSeries train = new TimeSeries("reg_flight.csv");
        TimeSeries test = new TimeSeries("anomaly_flight.csv");

        zScoreAlg z=new zScoreAlg();
        //HybridAlg h = new HybridAlg();
        //h.learnNormal(train);
        //List<AnomalyReport> l = h.detect(test);

        //System.out.println("l size: "+l.size());

     /*   for (AnomalyReport ap : l) {
            System.out.println(ap.description+" --> time step: "+ap.timeStep);
        }*/


    }
}
