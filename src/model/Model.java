package model;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.stage.FileChooser;
import server.CorrelatedFeatures;
import server.RegressionAlg;
import server.TimeSeries;
import server.TimeSeriesAnomalyDetector;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.*;
import java.util.Timer;

public class Model extends Observable {

    private TimeSeries ts = null;
    public DoubleProperty speed = new SimpleDoubleProperty(0);
    Timer t = null;
    public IntegerProperty timeStep;


    public Model(IntegerProperty timeStep) {
        this.timeStep = timeStep;
    }//CTOR


    public void play(int rate) {
        System.out.println("play function in MODEL start");

        if (t == null) {
            t = new Timer();

            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (speed.getValue() == 0) {
                        speed.setValue(rate);
                    }

                    Socket fg = null;
                    PrintWriter out = null;
                    String line = null;
                    int i = 0;

                    try {
                        fg = new Socket("localhost", 5400);
                        out = new PrintWriter(fg.getOutputStream());

                        if(i < timeStep.get()) {
                            i = timeStep.get();
                        }

                        while (((line = ts.readLine(timeStep.get())) != null) && (t != null) /*&& bool*/) {

                            out.println(line);
                            out.flush();
                            Thread.sleep(mySpeed(speed.doubleValue()));
                            i++;
                            timeStep.set(timeStep.get() + 1);
                            notifyObservers();
                            setChanged();
                        }
                        out.close();
                        fg.close();

                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }, 0, 1000);
        }

    }



    public void setTime(double d) {
        d=timeStep.doubleValue();
        timeStep.setValue(d);
    }

    public int getTimeStep() {
        return timeStep.get();
    }


    public void pause() {
        System.out.println("pause function in MODEL start");

        t.cancel();
        t.purge();

        t = null;
    }

    public void stop() {
        System.out.println("stop function in MODEL start");
        t.cancel();
        t = null;
        timeStep.set(1);

    }

    public void left() {
        if (this.speed.getValue() > 0.0)
            this.speed.setValue(speed.getValue() - 0.25);

        System.out.println("speed: " + this.speed);

    }

    public void right() {

        if (this.speed.getValue() < 2.0)
            this.speed.setValue(speed.getValue() + 0.25);
        System.out.println("speed: " + this.speed);

    }

    public void leftX2() {
        if (this.speed.getValue() == 0.25) {
            this.speed.setValue(0.0);
        }
        if (this.speed.getValue() > 0.0)
            this.speed.setValue(this.speed.getValue() - 0.5);
        System.out.println("speed: " + this.speed);

    }


    public void rightX2() {
        if (this.speed.getValue() == 1.75) {
            this.speed.setValue(2.0);
        }
        if (this.speed.getValue() < 2.0)
            this.speed.setValue(this.speed.getValue() + 0.5);

        System.out.println("speed: " + this.speed);

    }

    public boolean is_timer_exists(){
        return t != null;
    }
    public void setTimeSeries(TimeSeries ts) {
        this.ts = ts;
    }


    private int mySpeed(double speed) {
        switch (speed + "") {
            case "0.0":
                pause();
                return 0;

            case "0.25":
                return 400;
            case "0.5":
                return 200;
            case "0.75":
                return 134;
            case "1.25":
                return 80;
            case "1.50":
                return 67;
            case "1.75":
                return 57;
            case "2.0":
                return 50;

            default:
                return 100;
        }
    }



}




