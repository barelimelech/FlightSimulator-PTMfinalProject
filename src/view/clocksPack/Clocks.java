package view.clocksPack;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import view.joystickPack.JoystickController;

import java.io.IOException;

public class Clocks extends Pane {

    public ClocksController clocksController;


    public Clocks(){
        super();

        FXMLLoader fxl = new FXMLLoader();

        Pane hb = null;
        try {

            hb = fxl.load(getClass().getResource("Clocks.fxml").openStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (hb != null) {
            clocksController = fxl.getController();
            this.getChildren().add(hb);

        } else {
            clocksController = null;
        }



    }
}
