package view.joystickPack;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;


import java.util.ArrayList;

public class JoystickController {

    @FXML
    Canvas joystick;
    @FXML
    public Slider rudder, throttle;
    public DoubleProperty jx, jy ,aileron, elevator;
    ArrayList<String> feathers;

    public JoystickController() {
        aileron= new SimpleDoubleProperty();
        elevator= new SimpleDoubleProperty();
        rudder= new Slider();
        throttle= new Slider();

        feathers=new ArrayList<>();

        jx=aileron;
        jy=elevator;
        jx.addListener((o,ov,nv)->this.paint());
        jy.addListener((o,ov,nv)->this.paint());


    }

    public void paint() {
        GraphicsContext gc = joystick.getGraphicsContext2D();
        gc.clearRect(0, 0, joystick.getWidth(), joystick.getHeight());
        gc.strokeOval((jx.doubleValue()*50)  +45, (jy.doubleValue()*50)+45, 50, 50);
        gc.fillOval((jx.doubleValue()*50)  +45, (jy.doubleValue()*50)+45, 50, 50);
        gc.setStroke(Color.BLACK);
        gc.fill();

    }

}
