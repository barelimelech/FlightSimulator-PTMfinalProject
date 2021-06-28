package view.buttonsPack;


import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.util.StringConverter;


public class ButtonsController {


    public Runnable onPlay, onPause, onStop,onLeft,onLeftX2,onRight,onRightX2;
    @FXML
    public Slider bottomSlider;
    @FXML
    public TextField speedText;
    @FXML
    public TextField seconds;
    @FXML
    public TextField minutes;
    @FXML
    public TextField milliSec;
    @FXML
    public Text time;


    public IntegerProperty timeStepSlider;
    public StringConverter<Double> stringConverter;

    public ButtonsController() {
        speedText=new TextField();
        seconds=new TextField();
        minutes=new TextField();
        milliSec=new TextField();
        timeStepSlider=new SimpleIntegerProperty();

        /*stringConverter = new StringConverter<Double>() {

            @Override
            public String toString(Double object) {
                long seconds = object.longValue();
                long minutes = TimeUnit.SECONDS.toMinutes(seconds);
                long remainingSeconds = seconds - TimeUnit.MINUTES.toSeconds(minutes);
                return String.format("%02d", minutes) + ":" + String.format("%02d", remainingSeconds);
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        };*/

    }



    public void setBottomSlider(){
        System.out.println(bottomSlider.getValue());
        System.out.println("auto slider");
    }

  /*  public void setTimer(){
        //sl.setMajorTickUnit(450);
        //sl.setShowTickLabels(true);
        stringConverter = new StringConverter<Double>() {

            @Override
            public String toString(Double object) {
                long seconds = object.longValue();
                long minutes = TimeUnit.SECONDS.toMinutes(seconds);
                long remainingSeconds = seconds - TimeUnit.MINUTES.toSeconds(minutes);
                return String.format("%02d", minutes) + ":" + String.format("%02d", remainingSeconds);
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        };

    }*/


    public void play() {
        if (onPlay != null) {
            onPlay.run();
        }
    }

    public void pause() {
        if (onPause != null) {
            onPause.run();
        }
    }

    public void right() {
        if (onRight != null) {
            onRight.run();
        }
    }

    public void rightX2() {
        if (onRightX2 != null) {
            onRightX2.run();
        }
    }
    public void stop() {
        if (onStop != null) {
            onStop.run();
        }
    }
    public void left() {
        if (onLeft != null) {
            onLeft.run();
        }
    }
    public void leftX2() {
        if (onLeftX2 != null) {
            onLeftX2.run();
        }
    }



}