package view.clocksPack;

import eu.hansolo.medusa.Gauge;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;

public class ClocksController {

    @FXML
    public Gauge altitude;
    @FXML
    public Gauge heading;
    @FXML
    public Gauge airspeed;
    @FXML
    public Gauge pitch;
    @FXML
    public Gauge roll;
    @FXML
    public Gauge yaw;


    public ClocksController() {
        altitude = new Gauge();
        heading = new Gauge();
        airspeed = new Gauge();
        pitch = new Gauge();
        roll = new Gauge();
        yaw = new Gauge();


        altitude.setMinValue(0);
        heading.setMinValue(0);
        airspeed.setMinValue(0);
        pitch.setMinValue(0);
        roll.setMinValue(0);
        yaw.setMinValue(0);

        altitude.setMaxValue(1);
        heading.setMaxValue(1);
        airspeed.setMaxValue(1);
        pitch.setMaxValue(1);
        roll.setMaxValue(1);
        yaw.setMaxValue(1);

    }
}
